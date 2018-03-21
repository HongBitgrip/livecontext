package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.test.TestVendors;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.PropertyProfile;
import com.coremedia.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceSegmentTestContextExtractorTest {

  private CommerceSegmentTestContextExtractor testling;

  @Mock
  private Content content;

  @Mock
  private CMUserProfile cmUserProfile;

  @Mock
  private Map<String, Object> profileExtensions, properties, commerce;

  private ContextCollection contextCollection;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  private StoreContext storeContext;

  @Mock
  CommerceConnection commerceConnection;

  @Before
  public void setUp() throws Exception {
    testling = new CommerceSegmentTestContextExtractor();
    testling.setContentBeanFactory(contentBeanFactory);
    storeContext = newStoreContext();
    contextCollection = new ContextCollectionImpl();

    CurrentCommerceConnection.set(commerceConnection);

    when(commerceConnection.getStoreContext()).thenReturn(storeContext);
    BaseCommerceIdProvider idProvider = TestVendors.getIdProvider("vendor");
    when(commerceConnection.getIdProvider()).thenReturn(idProvider);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testExtractTestContextsFromContent() {
    String userSegmentsStr = "vendor:///catalog/segment/segment1,vendor:///catalog/segment/segment2";
    String userSegmentIds = "segment1,segment2";
    List<String> userSegments = StringUtil.tokenizeToList(userSegmentsStr, ",");
    when(contentBeanFactory.createBeanFor(content)).thenReturn(cmUserProfile);
    when(cmUserProfile.getProfileExtensions()).thenReturn(profileExtensions);
    when(profileExtensions.get(CommerceSegmentTestContextExtractor.PROPERTIES_PREFIX)).thenReturn(properties);
    when(properties.get(CommerceSegmentTestContextExtractor.COMMERCE_CONTEXT)).thenReturn(commerce);
    when(commerce.get(CommerceSegmentTestContextExtractor.USER_SEGMENTS_PROPERTY)).thenReturn(userSegments);

    testling.extractTestContextsFromContent(content, contextCollection);

    //assert the user segments in the store context
    assertEquals(userSegmentIds, storeContext.getUserSegments());

    //assert the user segments in the context collection
    PropertyProfile commerceProperty = (PropertyProfile) contextCollection.getContext(
            CommerceSegmentTestContextExtractor.COMMERCE_CONTEXT);
    assertEquals(userSegmentsStr, commerceProperty.getProperty(CommerceSegmentTestContextExtractor.USER_SEGMENTS_PROPERTY));
  }
}
