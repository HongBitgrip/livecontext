package com.coremedia.livecontext.hybris.links;

import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Add request attribute {@link LiveContextPageHandlerBase#HAS_PREVIEW_TOKEN} for the request if
 * {@link HybrisPreviewTokenMarkerFilter#CMS_TICKET_ID} was added by hybris commerce preview.
 */
public class HybrisPreviewTokenMarkerFilter implements Filter {

  private static final String CMS_TICKET_ID = "cmsTicketId";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request.getParameterMap().containsKey(CMS_TICKET_ID)) {
      request.setAttribute(LiveContextPageHandlerBase.HAS_PREVIEW_TOKEN, true);
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {

  }
}
