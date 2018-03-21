package com.coremedia.blueprint.livecontext.ecommerce.filter;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Initializes the commerce connection for the requests site (if any).
 *
 * @see SiteHelper
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class CommerceConnectionFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceConnectionFilter.class);

  @Autowired
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // Do nothing.
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    SiteHelper.findSite(request).ifPresent(this::setCommerceConnection);

    try {
      chain.doFilter(request, response);
    } finally {
      CurrentCommerceConnection.remove();
    }
  }

  private void setCommerceConnection(@Nonnull Site site) {
    try {
      Optional<CommerceConnection> connection = commerceConnectionInitializer.findConnectionForSite(site);

      if (!connection.isPresent()) {
        LOG.debug("Site '{}' has no commerce connection.", site.getName());
        return;
      }

      CurrentCommerceConnection.set(connection.get());
    } catch (Exception e) {
      LOG.debug("Unable to set commerce connection for site '{}' (locale: '{}').", site.getName(), site.getLocale(),
              e);
      CurrentCommerceConnection.remove();
    }
  }

  @Override
  public void destroy() {
    // Do nothing.
  }
}
