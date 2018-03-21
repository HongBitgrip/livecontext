package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.test.TestVendors;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.beans.ContentBeanFactory;
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
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceContractIdTestContextExtractorTest {

  private CommerceContractIdTestContextExtractor testling;

  @Mock
  private Content content;

  @Mock
  private CMUserProfile cmUserProfile;

  @Mock
  private Map<String, Object> profileExtensions, properties, commerce;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  private StoreContext storeContext;

  @Mock
  CommerceConnection commerceConnection;

  @Before
  public void setUp() throws Exception {
    testling = new CommerceContractIdTestContextExtractor();
    testling.setContentBeanFactory(contentBeanFactory);
    storeContext = newStoreContext();

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
    String userContractsStr = "vendor:///catalog/contract/contract1,vendor:///catalog/contract/contract2";
    String[] userContractIds = new String[]{"contract1", "contract2"};
    List<String> contracts = StringUtil.tokenizeToList(userContractsStr, ",");
    when(contentBeanFactory.createBeanFor(content)).thenReturn(cmUserProfile);
    when(cmUserProfile.getProfileExtensions()).thenReturn(profileExtensions);
    when(profileExtensions.get(CommerceContractIdTestContextExtractor.PROPERTIES_PREFIX)).thenReturn(properties);
    when(properties.get(CommerceContractIdTestContextExtractor.COMMERCE_CONTEXT)).thenReturn(commerce);
    when(commerce.get(CommerceContractIdTestContextExtractor.USER_CONTRACT_PROPERTY)).thenReturn(contracts);

    testling.extractTestContextsFromContent(content, null);

    //assert the user segments in the store context
    assertArrayEquals(userContractIds, storeContext.getContractIdsForPreview());
  }
}
