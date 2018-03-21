package com.coremedia.blueprint.themeimporter.configuration;

import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.blueprint.localization.configuration.LocalizationServiceConfiguration;
import com.coremedia.blueprint.themeimporter.ThemeImporterImpl;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.themeimporter.ThemeImporter;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.inject.Inject;

@Configuration
@Import(LocalizationServiceConfiguration.class)
@ImportResource(
        value = {
                "classpath:/com/coremedia/mimetype/mimetype-service.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class)
public class ThemeImporterConfiguration {
  private MimeTypeService mimeTypeService;
  private CapConnection capConnection;
  private LocalizationService localizationService;

  @Resource(name="mimeTypeService")
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @Resource(name="localizationService")
  public void setLocalizationService(LocalizationService localizationService) {
    this.localizationService = localizationService;
  }

  @Inject
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Bean(name="themeImporter")
  public ThemeImporter themeImporter() {
    return new ThemeImporterImpl(capConnection, mimeTypeService, localizationService);
  }
}
