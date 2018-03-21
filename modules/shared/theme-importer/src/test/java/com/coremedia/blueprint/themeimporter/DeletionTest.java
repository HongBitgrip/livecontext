package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.cap.themeimporter.ThemeImporterResult;
import com.coremedia.mimetype.TikaMimeTypeService;
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
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DeletionTest.LocalConfig.class)
public class DeletionTest {
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

  private ThemeImporterImpl themeImporter;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    TikaMimeTypeService tikaMimeTypeService = new TikaMimeTypeService();
    tikaMimeTypeService.init();
    themeImporter = new ThemeImporterImpl(capConnection, tikaMimeTypeService, localizationService);
  }

  @Test
  public void testDeleteCheckedOut() throws Exception {
    String themePath = "/any/where";
    String originalPath = "/dir/file";

    ContentRepository contentRepository = capConnection.getContentRepository();
    Content document = contentRepository.getContentType("CMCSS").create(contentRepository.getRoot(), themePath + originalPath);
    assertFalse(document.isDeleted());
    assertTrue(document.isCheckedOut());

    themeImporter.deleteCodeResource(themePath, originalPath);
    assertTrue(document.isDeleted());
    assertFalse(document.isCheckedOut());
  }

  @Test
  public void testDeleteCheckedIn() throws Exception {
    String themePath = "/any/where";
    String originalPath = "/dir/file";

    ContentRepository contentRepository = capConnection.getContentRepository();
    Content document = contentRepository.getContentType("CMCSS").create(contentRepository.getRoot(), themePath + originalPath);
    document.checkIn();
    assertFalse(document.isDeleted());
    assertFalse(document.isCheckedOut());

    ThemeImporterResult themeImporterResult = themeImporter.deleteCodeResource(themePath, originalPath);

    assertTrue(themeImporterResult.getFailedPaths().isEmpty());
    assertEquals(Collections.singletonMap(themePath + originalPath, document),
            themeImporterResult.getUpdatedContents());

    assertTrue(document.isDeleted());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteBadPath() throws Exception {
    String themePath = "/any/where";
    String originalPath = "../file";

    themeImporter.deleteCodeResource(themePath, originalPath);
  }
}