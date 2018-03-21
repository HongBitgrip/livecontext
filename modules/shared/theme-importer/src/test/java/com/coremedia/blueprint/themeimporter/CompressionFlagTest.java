package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.mimetype.TikaMimeTypeService;
import org.apache.commons.io.IOUtils;
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
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CompressionFlagTest.LocalConfig.class)
public class CompressionFlagTest {
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

  private InputStream compressionTheme;
  private ThemeImporterImpl themeImporter;

  @Mock
  private LocalizationService localizationService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    compressionTheme = getClass().getResource("./compression-theme.zip").openStream();

    TikaMimeTypeService tikaMimeTypeService = new TikaMimeTypeService();
    tikaMimeTypeService.init();
    themeImporter = new ThemeImporterImpl(capConnection, tikaMimeTypeService, localizationService);
  }

  @After
  public void tearDown() {
    IOUtils.closeQuietly(compressionTheme);
  }

  @Test
  public void testCompressionFlag() {
    assertTrue(themeImporter.importThemes(THEMES, Collections.singletonList(compressionTheme), true, true).isSuccessful());

    // Manual check: The logging contains warnings about
    // unknownuncompressible.js,
    // misconfigureduncompressible.min.js,
    // misconfigureduncompressible.min.css
    // because they are considered as not compressible, but not
    // flagged accordingly in the theme descriptor.

    Content folder = capConnection.getContentRepository().getChild(THEMES).getChild("compression").getChild("js");
    assertTrue(folder.getChild("bynameuncompressible.min.js").getBoolean("disableCompress"));
    assertFalse(folder.getChild("compressible.js").getBoolean("disableCompress"));
    assertTrue(folder.getChild("knownuncompressible.js").getBoolean("disableCompress"));
    assertTrue(folder.getChild("misconfigureduncompressible.min.js").getBoolean("disableCompress"));
    assertTrue(folder.getChild("unknownuncompressible.js").getBoolean("disableCompress"));
    assertTrue(folder.getChild("unwantedcompressible.js").getBoolean("disableCompress"));

    folder = capConnection.getContentRepository().getChild(THEMES).getChild("compression").getChild("css");
    assertTrue(folder.getChild("bynameuncompressible.min.css").getBoolean("disableCompress"));
    assertFalse(folder.getChild("compressible.css").getBoolean("disableCompress"));
    assertTrue(folder.getChild("misconfigureduncompressible.min.css").getBoolean("disableCompress"));
    assertTrue(folder.getChild("unwantedcompressible.css").getBoolean("disableCompress"));
  }
}