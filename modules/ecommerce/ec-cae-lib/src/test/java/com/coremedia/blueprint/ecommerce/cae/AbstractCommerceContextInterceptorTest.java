package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
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

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractCommerceContextInterceptorTest {

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private HttpServletRequest request;

  @Mock
  private SiteResolver siteResolver;

  private AbstractCommerceContextInterceptor testling;
  private MockCommerceEnvBuilder envBuilder;

  // --- setup ------------------------------------------------------

  @Before
  public void setup() {
    envBuilder = MockCommerceEnvBuilder.create();
    BaseCommerceConnection connection = envBuilder.setupEnv();
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(connection));

    testling = new NonAbstractTestling();

    // Set all @Required properties to make it afterPropertiesSet safe.
    // Tests may override, so do not call afterPropertiesSet yet.
    testling.setSiteResolver(siteResolver);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
  }

  @After
  public void tearDown() throws Exception {
    envBuilder.tearDownEnv();
  }

  // --- tests ------------------------------------------------------

  @Test
  public void testNormalizePath() {
    String path = "/helios";

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(path);

    assertThat(normalizedPath).as("changed path").isEqualTo(path);
  }

  @Test
  public void testNormalizeDynamicFragmentPath() {
    String path = "/cart/helios/action/cart";
    String dynamicPath = "/" + UriConstants.Segments.PREFIX_DYNAMIC + path;

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(dynamicPath);

    assertThat(normalizedPath).as("path not normalized").isEqualTo(path);
  }

  @Test
  public void testNormalizePathWithNull() {
    String path = null;

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(path);

    assertThat(normalizedPath).isNull();
  }

  @Test
  public void testInitStoreContextProvider() {
    // This does not work with the @Mock request.
    MockHttpServletRequest request = new MockHttpServletRequest();

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).hasValueSatisfying(c -> assertThat(c.getStoreContext()).isNotNull());
  }

  @Test
  public void testInitStoreContextProviderWithPreviewParameters() {
    when(request.getParameter(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE)).thenReturn("12-06-2014 13:00 Europe/Berlin");
    when(request.getParameter(AbstractCommerceContextInterceptor.QUERY_PARAMETER_WORKSPACE_ID)).thenReturn("aWorkspaceId");
    testling.setPreview(true);

    testling.getCommerceConnectionWithConfiguredStoreContext(site, request);

    StoreContext context = CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContext)
            .orElse(null);

    assertThat(context).isNotNull();
    assertThat(context.getPreviewDate()).isNotNull();
    assertThat(context.getWorkspaceId()).isNotNull();
  }

  // --- internal ---------------------------------------------------

  private class NonAbstractTestling extends AbstractCommerceContextInterceptor {

    @Nullable
    @Override
    protected Site getSite(HttpServletRequest request, String normalizedPath) {
      throw new UnsupportedOperationException("This dummy impl is not sufficient for your test.");
    }
  }
}
