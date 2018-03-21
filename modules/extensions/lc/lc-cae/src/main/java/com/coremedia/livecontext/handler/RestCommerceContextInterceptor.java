package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;

import javax.servlet.http.HttpServletRequest;

/**
 * Suitable for URLs whose second segment is a content id, e.g. /rest/1234/...
 */
public class RestCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  @Override
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    return getSiteResolver().findSiteForPathWithContentId(normalizedPath);
  }
}
