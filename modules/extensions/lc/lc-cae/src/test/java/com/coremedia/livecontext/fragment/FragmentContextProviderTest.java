package com.coremedia.livecontext.fragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FragmentContextProviderTest {
  @Test
  public void test() throws ServletException, IOException {
    FragmentContextProvider testling = new FragmentContextProvider();
    testling.init(filterConfig);

    testling.doFilter(servletRequest, servletResponse, new TestFilterChain());
    assertNotNull(FragmentContextProvider.getFragmentContext(servletRequest));

    testling.destroy();
  }

  @Mock
  private FilterConfig filterConfig;

  @Mock
  private ServletRequest servletRequest;

  @Mock
  private ServletResponse servletResponse;

  private class TestFilterChain implements FilterChain {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
      FragmentContext context = new FragmentContext();
      String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;";
      FragmentParameters params = FragmentParametersFactory.create(url);
      context.setParameters(params);

      when(servletRequest.getAttribute(FragmentContextProvider.FRAGMENT_CONTEXT_ATTRIBUTE)).thenReturn(context);
      FragmentContextProvider.getFragmentContext(servletRequest).setFragmentRequest(true);
      assertTrue(FragmentContextProvider.getFragmentContext(servletRequest).isFragmentRequest());
    }
  }
}
