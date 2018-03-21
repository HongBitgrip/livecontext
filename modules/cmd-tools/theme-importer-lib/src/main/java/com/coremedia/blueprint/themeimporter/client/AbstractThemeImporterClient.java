package com.coremedia.blueprint.themeimporter.client;

import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.server.importexport.base.exporter.ServerExporter;
import com.coremedia.cap.themeimporter.ThemeImporter;
import com.coremedia.cap.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.themeimporter.client.ThemeImporterInitializer.THEMEIMPORTER_CLEAN;
import static com.coremedia.blueprint.themeimporter.client.ThemeImporterInitializer.THEMEIMPORTER_DEVELOPMENT_MODE;
import static com.coremedia.blueprint.themeimporter.client.ThemeImporterInitializer.THEMEIMPORTER_EXITCODE;
import static com.coremedia.blueprint.themeimporter.client.ThemeImporterInitializer.THEMEIMPORTER_FOLDER;
import static com.coremedia.blueprint.themeimporter.client.ThemeImporterInitializer.THEMEIMPORTER_SERVEREXPORTPATH;
import static com.coremedia.blueprint.themeimporter.client.ThemeImporterInitializer.THEMEIMPORTER_THEMES;

public abstract class AbstractThemeImporterClient implements CommandLineRunner, ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractThemeImporterClient.class);

  private CapConnection capConnection;
  private ThemeImporter themeImporter;
  private ThemeService themeService;
  private Environment env;


  // --- Spring -----------------------------------------------------

  @Inject
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Resource(name="themeImporter")
  public void setThemeImporter(ThemeImporter themeImporter) {
    this.themeImporter = themeImporter;
  }

  @Resource(name="themeService")
  public void setThemeService(ThemeService themeService) {
    this.themeService = themeService;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    env = applicationContext.getEnvironment();
  }

  @Override
  public void run(String... strings) {
    try {
      if (isDevelopmentMode()) {
        LOG.info("Theme importer runs in development mode.");
      }

      work();

      if (getExitCode().get() != 0) {
        LOG.warn("Done, with errors.");
      } else {
        LOG.info("Done.");
      }
    } catch (Exception e) {
      LOG.error("Failed!", e);
      getExitCode().set(20);
    }
  }

  protected static void runSpringApplication(SpringApplication springApplication, AtomicInteger exitCodeCallback) {
    try (ConfigurableApplicationContext applicationContext = springApplication.run()) {
      LOG.debug("Theme importer success");
    } catch (Exception e) {
      LOG.error("Theme importer failed", e);
      exitCodeCallback.set(20);
    }
  }


  // --- properties -------------------------------------------------

  private boolean isDevelopmentMode() {
    return getProperty(THEMEIMPORTER_DEVELOPMENT_MODE, Boolean.class).orElse(false);
  }

  private String getFolder() {
    String originalFolder = env.getProperty(THEMEIMPORTER_FOLDER);

    if (!isDevelopmentMode()) {
      return originalFolder;
    }

    User developer = capConnection.getSession().getUser();
    return themeService.developerPath(originalFolder, developer);
  }

  private boolean cleanBeforeImport() {
    return getProperty(THEMEIMPORTER_CLEAN, Boolean.class).orElse(false);
  }

  private List<String> getThemes() {
    return env.getProperty(THEMEIMPORTER_THEMES, List.class);
  }

  private String getServerExportPath() {
    return env.getProperty(THEMEIMPORTER_SERVEREXPORTPATH);
  }

  private AtomicInteger getExitCode() {
    return env.getProperty(THEMEIMPORTER_EXITCODE, AtomicInteger.class);
  }


  // --- internal ---------------------------------------------------

  private void work() {
    String targetFolder = getFolder();
    if (targetFolder == null) {
      LOG.error("No target folder, or no corresponding development folder");
      getExitCode().set(5);
      return;
    }
    LOG.info("Import themes to {}", targetFolder);
    Collection<File> files = collectThemeFiles(getThemes(), getExitCode());
    List<InputStream> streams = files.stream().
            map(AbstractThemeImporterClient::openStream).
            filter(Objects::nonNull).
            collect(Collectors.toList());
    themeImporter.importThemes(targetFolder, streams, true, cleanBeforeImport());
    String serverExportPath = getServerExportPath();
    if (serverExportPath!=null) {
      Content themeRoot = capConnection.getContentRepository().getChild(targetFolder);
      if (themeRoot == null) {
        LOG.warn("Nothing to export at {}, maybe the theme import did not work as expected.", targetFolder);
      } else {
        LOG.info("serverexport themes to {}", serverExportPath);
        export(capConnection, themeRoot, serverExportPath);
      }
    }
  }

  private static InputStream openStream(File file) {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      LOG.error("could not open file {}, skipping", file, e);
      return null;
    }
  }

  private static Collection<File> collectThemeFiles(List<String> themes, AtomicInteger exitCode) {
    Collection<File> files = new ArrayList<>();
    for (String theme : themes) {
      File file = new File(theme);  // NOSONAR Yes, as a matter of fact, we do read files, like it or not.
      if (file.exists() && !file.isDirectory() && file.canRead()) {
        files.add(file);
      } else {
        LOG.warn("Cannot read theme {}, skipped.", theme);
        exitCode.set(100);
      }
    }
    return files;
  }

  private static void export(CapConnection capConnection, Content themeRoot, String exportPath) {
    ServerExporter export = new ServerExporter(capConnection, null);
    export.setContentIds(themeRoot.getId());
    export.setBaseDir(new File(exportPath));  // NOSONAR Yes, as a matter of fact, we do write files, like it or not.
    export.setPrettyPrint(true);
    export.setRecursive(true);
    export.doExport();
  }

  @Nonnull
  private <T> Optional<T> getProperty(@Nonnull String key, @Nonnull Class<T> targetType) {
    T property = env.getProperty(key, targetType);
    return Optional.ofNullable(property);
  }
}
