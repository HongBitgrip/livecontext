package com.coremedia.blueprint.themeimporter.client;

import com.coremedia.blueprint.coderesources.configuration.ThemeServiceConfiguration;
import com.coremedia.blueprint.themeimporter.configuration.ThemeImporterConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * The ThemeImportRunner is being used by the workspace to create serverimportable content from theme resources.
 * <p>
 * This simplifies automated initial deployments.  They can simply serverimport
 * the themes along with any other initial content and do not need to run the
 * theme importer explicitly as an additional step.
 * <p>
 * How to use: Add the following plugin do your workspace and configure the two system properties themes_folder and export_path
 * <plugin>
 * <groupId>org.codehaus.mojo</groupId>
 * <artifactId>exec-maven-plugin</artifactId>
 * <version>1.5.0</version>
 * <configuration>
 * <executable>java</executable>
 * <arguments>
 * <argument>-Dthemes_folder=${project.build.directory}/themes</argument>
 * <argument>-Dexport_path=${project.build.directory}/content</argument>
 * <argument>-classpath</argument>
 * <classpath/>
 * <argument>com.coremedia.blueprint.themeimporter.ThemeImporterRunner</argument>
 * </arguments>
 * <includePluginDependencies>true</includePluginDependencies>
 * </configuration>
 * </plugin>
 */
@Configuration
@Import({XmlRepoConfiguration.class, ThemeImporterConfiguration.class, ThemeServiceConfiguration.class})
public class ThemeImporterRunner extends AbstractThemeImporterClient {
  private static final Logger LOG = LoggerFactory.getLogger(ThemeImporterRunner.class);


  // --- Spring -----------------------------------------------------

  @Bean
  @Scope(SCOPE_SINGLETON)
  public static XmlUapiConfig xmlUapiConfig() {
    return XmlUapiConfig.builder().withContentTypes("classpath:framework/doctypes/blueprint/blueprint-doctypes.xml").build();
  }


  // --- main -------------------------------------------------------

  public static void main(String[] args) {
    AtomicInteger exitCodeCallback = new AtomicInteger(0);
    SpringApplication springApplication = createSpringApplication(exitCodeCallback);
    if (springApplication != null) {
      springApplication.setBannerMode(Banner.Mode.OFF);
      runSpringApplication(springApplication, exitCodeCallback);
    }
    System.exit(exitCodeCallback.get());
  }

  private static SpringApplication createSpringApplication(AtomicInteger exitCodeCallback) {
    ThemeImporterRunnerParameters params = parseParameters();
    if (params == null) {
      exitCodeCallback.set(10);
      return null;
    }
    ThemeImporterInitializer themeImporterInitializer =
            new ThemeImporterInitializer(params.repositoryFolder, themeFiles(params.themeFolder), params.exportPath, false, false, exitCodeCallback);
    SpringApplication springApplication = new SpringApplication(ThemeImporterRunner.class);
    springApplication.setBannerMode(Banner.Mode.OFF);
    springApplication.addInitializers(themeImporterInitializer);
    return springApplication;
  }


  // --- internal ---------------------------------------------------

  private static ThemeImporterRunnerParameters parseParameters() {
    ThemeImporterRunnerParameters parameters = new ThemeImporterRunnerParameters();

    if (StringUtils.hasText(System.getProperties().getProperty("import_folder"))) {
      parameters.repositoryFolder = System.getProperties().getProperty("import_folder").trim();
    }

    parameters.themeFolder = System.getProperties().getProperty("themes_folder").trim();
    if (StringUtils.isEmpty(parameters.themeFolder)) {
      LOG.warn("Wrong argument for parameter themes_folder.");
      return null;
    }

    parameters.exportPath = System.getProperties().getProperty("export_path").trim();
    if (StringUtils.isEmpty(parameters.exportPath)) {
      LOG.warn("Wrong argument for parameter export_path.");
      return null;
    }

    return parameters;
  }

  private static List<String> themeFiles(String themeFolder) {
    File themeFolderAsFile = new File(themeFolder);  // NOSONAR Yes, as a matter of fact, we do read files, like it or not.
    File[] zipFiles = themeFolderAsFile.listFiles(new ZipFilenameFilter());
    if (zipFiles == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(zipFiles).map(File::getAbsolutePath).collect(toList());
  }


  // --- inner classes ----------------------------------------------

  private static class ThemeImporterRunnerParameters {
    String repositoryFolder = ThemeImporterInitializer.REPOSITORY_FOLDER;
    String themeFolder;
    String exportPath;
  }

  private static class ZipFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      return name.toLowerCase(Locale.ROOT).endsWith(".zip");
    }
  }
}
