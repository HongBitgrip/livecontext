package com.coremedia.blueprint.feeder.populate;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SiteFeedablePopulatorTest {

  private FeedablePopulator<Content> populator;

  @Mock
  private MutableFeedable feedable;

  @Mock
  private Content content;

  @Before
  public void beforeEachTest() {
    MockitoAnnotations.initMocks(this);
    this.populator = new SiteFeedablePopulator();
  }


  @Test (expected = IllegalArgumentException.class)
  public void testContentIsNull() {
    populator.populate(feedable, null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testFeedableIsNull() {
    populator.populate(null, content);
  }
}
