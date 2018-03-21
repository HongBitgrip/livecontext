package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.blueprint.lc.test.BetamaxTestHelper;
import com.coremedia.livecontext.ecommerce.hybris.HybrisITBase;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class HybrisRestConnectorIT extends HybrisITBase {

  @Test
  public void testListCatalogs() {
    if (BetamaxTestHelper.useBetamaxTapes()) {
      return;
    }
    String responseValue = performGetWithStoreContext("/catalogs", String.class);

    assertThat(responseValue).isNotNull();
  }

  @Test
  public void testMainCatalog() {
    if (BetamaxTestHelper.useBetamaxTapes()) {
      return;
    }
    String responseValue = performGetWithStoreContext("/catalogs/apparelProductCatalog", String.class);

    assertThat(responseValue).isNotNull();
  }

  @Test
  public void testMainCatalogStagedVersion() {
    if (BetamaxTestHelper.useBetamaxTapes()) {
      return;
    }
    String responseValue = performGetWithStoreContext("/catalogs/apparelProductCatalog/catalogversions/Staged",
            String.class);

    assertThat(responseValue).isNotNull();
  }

  @Test
  public void testRootCategory() {
    if (BetamaxTestHelper.useBetamaxTapes()) {
      return;
    }
    String responseValue = performGetWithStoreContext(
            "/catalogs/apparelProductCatalog/catalogversions/Staged/categories/brands", String.class);

    assertThat(responseValue).isNotNull();
  }

  @Test
  public void testSubCategory() {
    if (BetamaxTestHelper.useBetamaxTapes()) {
      return;
    }
    CategoryDocument responseValue = performGetWithStoreContext(
            "/catalogs/apparelProductCatalog/catalogversions/Staged/categories/400000", CategoryDocument.class);

    assertThat(responseValue).isNotNull();
  }

  @Test
  public void testProduct() {
    if (BetamaxTestHelper.useBetamaxTapes()) {
      return;
    }
    String responseValue = performGetWithStoreContext(
            "/catalogs/apparelProductCatalog/catalogversions/Staged/products/300044600", String.class);

    assertThat(responseValue).isNotNull();
  }
}
