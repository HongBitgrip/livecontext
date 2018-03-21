package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.blueprint.lc.test.AbstractServiceTest;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class InvalidationServiceImplIT extends AbstractServiceTest {

  @Inject
  private InvalidationServiceImpl testling;

  @Test
  public void testInvalidationsBootstrap() {
    if (useBetamaxTapes()) {
      return;
    }

    List<InvalidationEvent> invalidations = testling.getInvalidations(-1, getStoreContext());
    assertThat(invalidations).isNotNull();

    if (!invalidations.isEmpty()) {
      InvalidationEvent invalidationEvent = invalidations.get(0);
      assertThat(invalidationEvent.getTimestamp()).isGreaterThan(0);
    }
  }

  @Test
  public void testInvalidationsWithBigTimeStamp() {
    if (useBetamaxTapes()) {
      return;
    }

    List<InvalidationEvent> invalidations = testling.getInvalidations(10000, getStoreContext());
    assertThat(invalidations).isNotEmpty();
    assertThat(invalidations.size()).isEqualTo(1);
    assertThat(invalidations.get(0).getContentType()).isEqualTo(InvalidationEvent.CLEAR_ALL_EVENT);
  }
}
