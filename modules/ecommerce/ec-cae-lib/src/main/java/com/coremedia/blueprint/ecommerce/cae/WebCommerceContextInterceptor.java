package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.cap.multisite.Site;

import javax.servlet.http.HttpServletRequest;

/**
 * Suitable for URLs whose second segment denotes the site, e.g. /helios/...
 */
public class WebCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  @Override
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    return getSiteResolver().findSiteByPath(normalizedPath);
  }
}
