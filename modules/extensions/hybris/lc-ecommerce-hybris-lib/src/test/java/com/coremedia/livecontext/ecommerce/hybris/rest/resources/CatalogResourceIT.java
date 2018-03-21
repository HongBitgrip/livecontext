package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.lc.test.AbstractServiceTest;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupRefDocument;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, CatalogResourceIT.LocalConfig.class})
public class CatalogResourceIT extends AbstractServiceTest {

  @Inject
  private CatalogResource catalogResource;

  @Betamax(tape = "hy_testGetCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetCategory() {
    CategoryDocument cat = catalogResource.getCategoryById("40500", StoreContextHelper.getCurrentContext());

    assertThat(cat).isNotNull();
    assertThat(cat.getCode()).isEqualTo("40500");
  }

  @Betamax(tape = "hy_testGetCategoryProducts", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetCategoryProducts() {
    CategoryDocument cat = catalogResource.getCategoryById("Vans", StoreContextHelper.getCurrentContext());

    assertThat(cat).isNotNull();
    assertThat(cat.getProducts()).isNotEmpty();
  }

  @Betamax(tape = "hy_testListUserGroups", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testListUserGroups() {
    List<UserGroupRefDocument> userGroups = catalogResource.getAllUserGroups(StoreContextHelper.getCurrentContext());

    assertThat(userGroups).isNotEmpty();
  }

  @Betamax(tape = "hy_testGetUserGroup", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetUserGroup() {
    UserGroupDocument userGroup = catalogResource.getUserGroup("customergroup", StoreContextHelper.getCurrentContext());

    assertThat(userGroup).isNotNull();
    assertThat(userGroup.getUid()).isEqualTo("customergroup");
  }

  @Test
  @Ignore
  public void testFetchAuthToken() {
    String token = catalogResource.getConnector().fetchAuthToken();

    assertThat(token).isEqualTo("foo");
  }

  @Configuration
  @PropertySource(
          value = {
                  "classpath:/com/coremedia/livecontext/ecommerce/hybris/test-hybris-services.properties",
                  "classpath:/com/coremedia/livecontext/ecommerce/hybris/hybris-example-catalog.properties"
          }
  )
  @ImportResource(
          value = {
                  "classpath:/com/coremedia/livecontext/ecommerce/hybris/test-hybris-services.xml"
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  public static class LocalConfig {
  }
}
