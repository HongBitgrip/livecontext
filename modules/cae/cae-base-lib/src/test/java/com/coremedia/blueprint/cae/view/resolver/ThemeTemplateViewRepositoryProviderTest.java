package com.coremedia.blueprint.cae.view.resolver;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ThemeTemplateViewRepositoryProviderTest.LocalConfig.class)
@ActiveProfiles(ThemeTemplateViewRepositoryProviderTest.LocalConfig.PROFILE)
public class ThemeTemplateViewRepositoryProviderTest {
  @Configuration
  @ImportResource(
          value = {
                  "classpath:/framework/spring/blueprint-views.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import({XmlRepoConfiguration.class})
  @Profile(LocalConfig.PROFILE)
  public static class LocalConfig {
    static final String PROFILE = "ThemeTemplateViewRepositoryProviderTest";
    private static final String CONTENT = "classpath:com/coremedia/blueprint/cae/view/resolver/content.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return XmlUapiConfig.builder()
              .withContent(CONTENT)
              .withContentTypes("classpath:framework/doctypes/blueprint/blueprint-doctypes.xml")
              .build();
    }
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ContentRepository contentRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Resource(name="themeTemplateViewRepositoryProvider")
  private ThemeTemplateViewRepositoryProvider testling;

  @Before
  public void checkWiring() {
    assumeTrue("themeService not found in ThemeServiceConfiguration, these tests will fail.", testling!=null);
  }

  @Test
  public void testTemplatesLocations() {
    List<String> locations = testling.templateLocations("theme::2/corporate");
    assertTrue(locations.size()==2);
    // The order matters.
    assertEquals("jar:id:contentproperty:/Themes/corporate/corporate-templates/archive!/META-INF/resources/WEB-INF/templates/corporate", locations.get(0));
    assertEquals("jar:id:contentproperty:/Themes/corporate/bricks-templates/archive!/META-INF/resources/WEB-INF/templates/bricks", locations.get(1));
  }

  @Test
  public void testViewRepositoryNames() {
    Content theme = content(2);
    List<String> locations = testling.viewRepositoryNames(theme, null);
    assertTrue(locations.size()==2);
    // The order matters.
    assertEquals("theme::2/corporate", locations.get(0));
    assertEquals("theme::2/bricks", locations.get(1));
  }


  // --- internal ---------------------------------------------------

  private Content content(int id) {
    return contentRepository.getContent(IdHelper.formatContentId(id));
  }
}
