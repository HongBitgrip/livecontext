package com.coremedia.livecontext.fragment;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

/**
 * <p>
 * A <code>Filter</code> that creates a fresh {@link FragmentContext} per request.
 * </p>
 */
public class FragmentContextProvider implements Filter {

  protected static final String FRAGMENT_CONTEXT_ATTRIBUTE = "CM_FRAGMENT_CONTEXT";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    //interface method not needed for this implementation.
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    FragmentContext fragmentContext = new FragmentContext();

    if (servletRequest instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      String url = request.getRequestURL().toString();
      fragmentContext.setFragmentRequest(url.contains("/params;"));

      //apply the fragment parameters to the request since they are using other interceptors too.
      if(fragmentContext.isFragmentRequest()) {
        FragmentParameters params = FragmentParametersFactory.create(url);
        fragmentContext.setParameters(params);
      }
    }
    servletRequest.setAttribute(FRAGMENT_CONTEXT_ATTRIBUTE, fragmentContext);
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {
    //interface method not needed for this implementation.
  }

  /**
   * Returns the {@link FragmentContext} for the given request.
   *
   * @param request request
   * @return FragmentContext
   * @throws IllegalStateException if {@link #doFilter} was not called for the given request
   */
  @Nonnull
  public static FragmentContext getFragmentContext(@Nonnull ServletRequest request) {
    return findFragmentContext(request).orElseThrow(
            () -> new IllegalStateException("FragmentContext not set. FragmentContextProvider#doFilter not invoked?"));
  }

  /**
   * Returns the {@link FragmentContext} for the given request, if available
   *
   * @param request request
   * @return Optional with FragmentContext, or empty Optional if no FragmentContext is available.
   * @see #getFragmentContext(ServletRequest)
   */
  @Nonnull
  public static Optional<FragmentContext> findFragmentContext(@Nonnull ServletRequest request) {
    Object attribute = request.getAttribute(FRAGMENT_CONTEXT_ATTRIBUTE);
    return attribute instanceof FragmentContext
            ? Optional.of((FragmentContext) attribute)
            : Optional.empty();
  }
}
