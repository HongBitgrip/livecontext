package com.coremedia.livecontext.marketingspot;


import com.coremedia.livecontext.contentbeans.CMMarketingSpot;
import com.coremedia.objectserver.view.RenderNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CMMarketingSpotDynamicIncludePredicateTest {

  CMMarketingSpotDynamicIncludePredicate testling;

  @Before
  public void setUp() throws Exception {
    testling = new CMMarketingSpotDynamicIncludePredicate();
  }

  @Test
  public void testInputNull() throws Exception {
    assertFalse(testling.apply(null));
  }

  @Test
  public void testInputNotInstanceOfCMMarketingSpot() throws Exception {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(new Object());
    assertFalse(testling.apply(input));
  }

  @Test
  public void testInputCMMarketingSpotNoView() throws Exception {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(CMMarketingSpot.class));
    when(input.getView()).thenReturn(null);
    assertTrue(testling.apply(input));
  }


  @Test
  public void testInputCMMarketingSpotFragmentPreviewSet() throws Exception {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(CMMarketingSpot.class));
    when(input.getView()).thenReturn("fragmentPreview");
    assertFalse(testling.apply(input));
  }

  @Test
  public void testInputCMMarketingSpotOtherViewSet() throws Exception {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(CMMarketingSpot.class));
    when(input.getView()).thenReturn("any_view_except_fragmentPreview");
    assertTrue(testling.apply(input));
  }
}