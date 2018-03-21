package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class WebCommerceContextInterceptorTest {

  @Mock
  private SiteResolver siteLinkHelper;

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private CommerceConnection connection;

  private WebCommerceContextInterceptor testling = new WebCommerceContextInterceptor();
  private MockCommerceEnvBuilder envBuilder;

  @Before
  public void setup() {
    envBuilder = MockCommerceEnvBuilder.create();
    connection = envBuilder.setupEnv();
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(connection));

    testling.setSiteResolver(siteLinkHelper);
    testling.setInitUserContext(false);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setPreview(false);

    when(siteLinkHelper.findSiteBySegment("helios")).thenReturn(site);
  }

  @After
  public void tearDown() throws Exception {
    envBuilder.tearDownEnv();
  }

  // --- test base class features -----------------------------------

  @Test
  public void testPreHandle() {
    String path = "/helios";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo(path);
    when(testling.getSite(request, path)).thenReturn(site);

    testling.preHandle(request, null, null);

    verify(commerceConnectionInitializer).findConnectionForSite(any(Site.class));
    assertThat(SiteHelper.findSite(request)).isPresent();
  }

  @Test
  public void testNoopPreHandle() {
    String path = "/nosite";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo(path);
    when(testling.getSite(request, path)).thenReturn(null);
    StoreContext storeContextBefore = connection.getStoreContext();

    testling.preHandle(request, null, null);

    assertThat(connection.getStoreContext()).isSameAs(storeContextBefore);
    assertThat(SiteHelper.findSite(request)).isNotPresent();
  }
}
