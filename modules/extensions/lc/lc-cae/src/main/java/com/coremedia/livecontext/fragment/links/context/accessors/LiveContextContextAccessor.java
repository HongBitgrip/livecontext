package com.coremedia.livecontext.fragment.links.context.accessors;

import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Provide access to LiveContext
 * Call openAccessToContext to make context available via @link{LiveContextContextHelper}
 * <p>
 * Extracted to make available to Servlet Filters (required for Elastic Social)
 */
public class LiveContextContextAccessor {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextContextAccessor.class);

  private StandardContextResolver contextResolver = new StandardContextResolver();

  /**
   * Extract the context and make it available via LC1ContextHelper
   * Operation is idemPotent, i.e. can be called several times
   *
   * @return the context
   */
  public Context openAccessToContext(HttpServletRequest request) {
    if (request == null) {
      return null;
    }

    try {
      Context context = LiveContextContextHelper.fetchContext(request);
      if (context == null) {
        context = contextResolver.resolveContext(request);

        // store the context and make it available to LiveContextContextHelper
        if (context != null) {
          LiveContextContextHelper.setContext(request, context);
        }
      }
      return context;
    } catch (Exception e) {
      LOG.error("Error retrieving LiveContext context", e);
      return null;
    }
  }
}
