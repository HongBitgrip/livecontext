package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommerceSegmentSourceTest {
  private static final String USER1_NAME = "testUser";
  private static final String USER1_ID = "4711";

  private CommerceSegmentSource testling;

  private CommerceConnection commerceConnection;
  private MockCommerceEnvBuilder envBuilder;

  @Before
  public void setup() {
    initMocks(this);
    testling = new CommerceSegmentSource();
    testling.setContextName("commerce");

    envBuilder = MockCommerceEnvBuilder.create();
    commerceConnection = envBuilder.setupEnv();
    UserContext userContext = UserContext.buildCopyOf(commerceConnection.getUserContext())
                                             .withUserId(USER1_ID)
                                             .withUserName(USER1_NAME)
                                             .build();
    commerceConnection.setUserContext(userContext);
    CurrentCommerceConnection.set(commerceConnection);
  }

  @After
  public void teardown() {
    envBuilder.tearDownEnv();
  }

  @Test
  public void testPreHandle() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ContextCollection contextCollection = new ContextCollectionImpl();

    Segment seg1 = mock(Segment.class);
    when(seg1.getName()).thenReturn("name1");
    when(seg1.getExternalId()).thenReturn("extId1");
    when(seg1.getExternalTechId()).thenReturn("techId1");
    when(seg1.getId()).thenReturn(CommerceIdParserHelper.parseCommerceIdOrThrow("vendor:///catalog/segment/id1"));

    Segment seg2 = mock(Segment.class);
    when(seg2.getName()).thenReturn("name2");
    when(seg2.getExternalId()).thenReturn("extId2");
    when(seg2.getExternalTechId()).thenReturn("techId2");
    when(seg2.getId()).thenReturn(CommerceIdParserHelper.parseCommerceIdOrThrow("vendor:///catalog/segment/id2"));

    List<Segment> segmentList = new ArrayList<>();
    segmentList.add(seg1);
    segmentList.add(seg2);
    when(commerceConnection.getSegmentService().findSegmentsForCurrentUser(any(StoreContext.class))).thenReturn(segmentList);

    testling.preHandle(request, response, contextCollection);
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertNotNull(profile);
    assertEquals("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,", profile.getProperty("usersegments"));
  }

  @Test
  public void testPreHandleFromUserContext() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ContextCollection contextCollection = new ContextCollectionImpl();

    StoreContext storeContext = commerceConnection.getStoreContext();
    storeContext.setUserSegments("id1,id2");
    testling.preHandle(request, response, contextCollection);
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertNotNull(profile);
    assertEquals("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,", profile.getProperty("usersegments"));
    verify(commerceConnection.getSegmentService(), times(0)).findSegmentsForCurrentUser(storeContext);
  }
}
