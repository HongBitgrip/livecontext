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
public class CategoryDocumentIT extends HybrisITBase {

  @Betamax(tape = "hy_testCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCategory() {
    CategoryDocument categoryDocument = performGetWithStoreContext(
            "/catalogs/apparelProductCatalog/catalogversions/Staged/categories/350000", CategoryDocument.class);

    assertThat(categoryDocument.getCode()).isEqualTo("350000");
  }
}
