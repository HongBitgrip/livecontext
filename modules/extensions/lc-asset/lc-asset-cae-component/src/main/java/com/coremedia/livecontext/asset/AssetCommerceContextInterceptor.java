package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Initialize Context for asset urls (e.g. {@link ProductCatalogPictureHandler#IMAGE_URI_PATTERN}
 * and {@link ProductCatalogPictureHandler#IMAGE_URI_PATTERN_FOR_CATALOG}
 */
public class AssetCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  LiveContextSiteResolver liveContextSiteResolver;

  @Nullable
  @Override
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    String storeId = extractStoreId(normalizedPath);
    Locale locale = extractLocale(normalizedPath);

    return liveContextSiteResolver.findSiteFor(storeId, locale);
  }

  private static String extractStoreId(String path) {
    return extractToken(path, 3);
  }

  @Nullable
  private static Locale extractLocale(String path) {
    String localeToken = extractToken(path, 4);
    return LocaleHelper.parseLocaleFromString(localeToken).orElse(null);
  }

  private static String extractToken(String path, int index) {
    String[] split = path.split("/");
    //length == 7 no catalogId
    //length == 8 with catalogId
    if (split.length == 7 || split.length == 8) {
      return split[index];
    }
    throw new IllegalArgumentException("Cannot handle path " + path);
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }
}
