package com.coremedia.blueprint.elastic.tenant;

import com.coremedia.elastic.core.api.tenant.TenantService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TenantFilterTest {

  private TenantFilter tenantFilter;

  @Before
  public void setUp() throws Exception {
    tenantFilter = new TenantFilter();
    TenantService tenantService = mock(TenantService.class);
    tenantFilter.setTenantService(tenantService);
  }

  @Test
  public void lookup() throws ServletException, IOException {
    ServletRequest request = mock(ServletRequest.class);
    when(request.getServerName()).thenReturn("localhost");
    ServletResponse response = mock(ServletResponse.class);
    MyFilterChain filterChain = new MyFilterChain();

    tenantFilter.doFilter(request, response, filterChain);
    tenantFilter.destroy();

    assertTrue(filterChain.isCalled());
  }

  private class MyFilterChain implements FilterChain {
    private boolean called = false;

    public boolean isCalled() {
      return called;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
      called = true;
    }
  }
}
