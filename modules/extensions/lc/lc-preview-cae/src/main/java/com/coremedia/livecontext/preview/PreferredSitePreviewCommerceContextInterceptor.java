package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Creates the store context for preview urls containing studioPreferredSite parameter.
 */
public class PreferredSitePreviewCommerceContextInterceptor extends AbstractCommerceContextInterceptor {
  private static final String STUDIO_PREFERRED_SITE_PARAM = "studioPreferredSite";
  private SitesService sitesService;
  private String queryParam = STUDIO_PREFERRED_SITE_PARAM;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String queryString = request.getQueryString();
    if (StringUtils.isEmpty(queryString) || !request.getParameterMap().containsKey(queryParam)) {
      return true;
    }
    return super.preHandle(request, response, handler);
  }

  @Override
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    Site site = null;
    String[] siteIds = request.getParameterMap().get(queryParam);
    if (siteIds != null && siteIds.length == 1) {
      site = sitesService.getSite(siteIds[0]);
    }
    if (site == null) {
      String[] ids = request.getParameterMap().get(queryParam);
      if (ids != null && ids.length > 0) {
        String id = ids[0];
        if (IdHelper.isContentId(id) || StringUtils.isNumeric(id)) {
          int contentId = IdHelper.parseContentId(id);
          site = getSiteResolver().findSiteForContentId(contentId);
        }
      }
    }
    return site;
  }

  @Required
  public void setSitesService(SitesService siteService) {
    this.sitesService = siteService;
  }

  public void setQueryParam(String queryParam) {
    this.queryParam = queryParam;
  }
}
