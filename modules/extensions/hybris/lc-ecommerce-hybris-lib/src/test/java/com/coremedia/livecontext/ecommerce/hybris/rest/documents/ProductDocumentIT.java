package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.hybris.HybrisITBase;
import com.coremedia.livecontext.ecommerce.hybris.rest.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class ProductDocumentIT extends HybrisITBase {

  @Betamax(tape = "hy_testProduct2", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProduct() {
    ProductDocument productDocument = performGetWithStoreContext(
            "/catalogs/apparelProductCatalog/catalogversions/Staged/products/111160", ProductDocument.class);

    assertThat(productDocument.getCode()).isEqualTo("111160");
  }
}
