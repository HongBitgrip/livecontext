package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.multisite.impl.SitesServiceImpl;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import java.util.ResourceBundle;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CACHE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.ID_PROVIDER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.LINK_FORMATTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LinklistPageResourceBundleFactoryTest.LocalConfig.class)
@ActiveProfiles(LinklistPageResourceBundleFactoryTest.LocalConfig.PROFILE)
public class LinklistPageResourceBundleFactoryTest {
  private static final String TESTLING_RESOURCE = "classpath:/framework/spring/blueprint-i18n.xml";

  @Configuration
  @ImportResource(
          value = {
                  CONTENT_BEAN_FACTORY,
                  ID_PROVIDER,
                  LINK_FORMATTER,
                  CACHE,
                  TESTLING_RESOURCE
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import({XmlRepoConfiguration.class, ContentTestConfiguration.class})
  @Profile(LocalConfig.PROFILE)
  public static class LocalConfig {
    static final String PROFILE = "LinklistPageResourceBundleFactoryTest";
    private static final String CONTENT = "classpath:com/coremedia/blueprint/cae/web/i18n/resourcebundles/content.xml";

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
  private ContentTestHelper contentTestHelper;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Resource(name="linklistPageResourceBundleFactory")
  private LinklistPageResourceBundleFactory testling;

  @Before
  public void checkWiring() {
    assumeTrue("linklistPageResourceBundleFactory not found in " + TESTLING_RESOURCE + ", these tests will fail.", testling!=null);
  }

  @Test
  public void testBundleForPage() {
    ContentBean article = contentTestHelper.getContentBean(30);
    Navigation navigation = contentTestHelper.getContentBean(20);
    Page page = new PageImpl(navigation, article, false, new SitesServiceImpl(), null, null, null, null);
    ResourceBundle resourceBundle = testling.resourceBundle(page, null);
    assertEquals("str2fromBundle10", resourceBundle.getString("str2"));
    assertEquals("str4fromBundle20a", resourceBundle.getString("str4"));
    assertEquals("str1fromBundle20", resourceBundle.getString("str1"));
    assertEquals("str3fromBundle20", resourceBundle.getString("str3"));
    assertEquals("str5fromBundle10", resourceBundle.getString("str5"));
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testBundleForPageByTheme() {
    Navigation navigation = contentTestHelper.getContentBean(220);
    Page page = new PageImpl(navigation, navigation, false, new SitesServiceImpl(), null, null, null, null);
    ResourceBundle resourceBundle = testling.resourceBundle(page, null);
    assertEquals("localeFromBundle_en_gb_europe", resourceBundle.getString("locale"));
    assertEquals("str1", resourceBundle.getString("str1"));
    assertEquals("str2", resourceBundle.getString("str2"));
    assertEquals("str3", resourceBundle.getString("str3"));

    navigation = contentTestHelper.getContentBean(216);
    page = new PageImpl(navigation, navigation, false, new SitesServiceImpl(), null, null, null, null);

    resourceBundle = testling.resourceBundle(page, null);
    assertEquals("localeFromBundle_en_gb", resourceBundle.getString("locale"));
    assertEquals("str1", resourceBundle.getString("str1"));
    assertEquals("str2", resourceBundle.getString("str2"));

    navigation = contentTestHelper.getContentBean(210);
    page = new PageImpl(navigation, navigation, false, new SitesServiceImpl(), null, null, null, null);

    resourceBundle = testling.resourceBundle(page, null);
    assertEquals("localeFromBundle_en", resourceBundle.getString("locale"));
    assertEquals("str1", resourceBundle.getString("str1"));
  }

  @Test
  public void testOverrideInheritedTheme() {
    Navigation navigation = contentTestHelper.getContentBean(226);
    Page page = new PageImpl(navigation, navigation, false, new SitesServiceImpl(), null, null, null, null);
    ResourceBundle resourceBundle = testling.resourceBundle(page, null);
    assertEquals("locale_overridden", resourceBundle.getString("locale"));
  }
}
