package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cap.themeimporter.ThemeImporterResult;
import com.coremedia.mimetype.TikaMimeTypeService;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ThemeImporterImplTest.LocalConfig.class)
public class ThemeImporterImplTest {
  private static final String THEMES = "/Themes";
  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private CapConnection capConnection;

  @Configuration
  @Import(XmlRepoConfiguration.class)
  public static class LocalConfig {
    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return XmlUapiConfig.builder().withContentTypes("classpath:framework/doctypes/blueprint/blueprint-doctypes.xml").build();
    }
  }

  @Mock
  private LocalizationService localizationService;

  private InputStream corporateTheme;
  private ThemeImporterImpl themeImporter;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    corporateTheme = getClass().getResource("./corporate-theme.zip").openStream();

    TikaMimeTypeService tikaMimeTypeService = new TikaMimeTypeService();
    tikaMimeTypeService.init();
    themeImporter = new ThemeImporterImpl(capConnection, tikaMimeTypeService, localizationService);
  }

  @After
  public void tearDown() {
    IOUtils.closeQuietly(corporateTheme);
  }

  @Test
  public void extractFromZip() throws Exception {
    String corporateThemePath = THEMES + "/corporate/Corporate Theme";

    ThemeImporterResult themeImporterResult = themeImporter.importThemes(THEMES, Collections.singletonList(corporateTheme), true, true);
    assertTrue(themeImporterResult.isSuccessful());
    assertTrue(themeImporterResult.getFailedPaths().isEmpty());
    assertThat(themeImporterResult.getUpdatedContents().keySet(),
            CoreMatchers.hasItems(CoreMatchers.startsWith(corporateThemePath)));

    Content corporateTheme = capConnection.getContentRepository().getChild(corporateThemePath);
    assertNotNull(corporateTheme);
    checkLinkList(corporateTheme, "css", 1);
    checkLinkList(corporateTheme, "javaScripts", 16);
    checkLinkList(corporateTheme, "javaScriptLibs", 6);
    checkLinkList(corporateTheme, "templateSets", 2);
    checkLinkList(corporateTheme, "resourceBundles", 4);

    checkOneCheckedInVersion(capConnection.getContentRepository().getChild(THEMES));
  }

  @Test
  public void cleanAndExtractFromZip() throws Exception {
    ContentRepository contentRepository = capConnection.getContentRepository();

    String testPathCleaned = THEMES + "/corporate/x";
    Content testDocumentCleaned = contentRepository.getContentType("CMCSS").create(contentRepository.getRoot(), testPathCleaned);

    String testPathNotCleaned = THEMES + "/cawporate/x";
    Content testDocumentNotCleaned = contentRepository.getContentType("CMCSS").create(contentRepository.getRoot(), testPathNotCleaned);

    ThemeImporterResult themeImporterResult = themeImporter.importThemes(THEMES, Collections.singletonList(corporateTheme), true, true);
    assertTrue(testDocumentCleaned.isDeleted());
    assertFalse(testDocumentNotCleaned.isDeleted());
    assertTrue(themeImporterResult.isSuccessful());
  }

  @Test
  public void writeOnlyChanges() throws IOException {
    assertTrue(themeImporter.importThemes(THEMES, Collections.singletonList(corporateTheme), true, true).isSuccessful());
    IOUtils.closeQuietly(corporateTheme);
    checkOneCheckedInVersion(capConnection.getContentRepository().getChild(THEMES).getChild("corporate"));
    // same theme again:
    try (InputStream theme = getClass().getResource("./corporate-theme.zip").openStream()) {
      assumeTrue(themeImporter.importThemes(THEMES, Collections.singletonList(theme), true, false).isSuccessful());
    }
    checkOneCheckedInVersion(capConnection.getContentRepository().getChild(THEMES).getChild("corporate"));
  }


  // --- internal ---------------------------------------------------

  private void checkLinkList(Content corporateTheme, String propertyName, int expected) {
    assertNotNull(corporateTheme.getProperties().get(propertyName));
    assertEquals(expected, ((List) corporateTheme.getProperties().get(propertyName)).size());
  }

  private void checkOneCheckedInVersion(Content folder) {
    for (Content child : folder.getChildren()) {
      if (child.isFolder()) {
        checkOneCheckedInVersion(child);
      } else {
        assertTrue(child.isCheckedIn());
        assertEquals(child.getId() + ", " + child.getPath(), 1, IdHelper.parseVersionId(child.getCheckedInVersion().getId()));
      }
    }
  }
}