package com.coremedia.livecontext.ecommerce.hybris.asset;

import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetUrlProviderImpl implements AssetUrlProvider {

  private UrlPrefixResolver urlPrefixResolver;
  private SitesService sitesService;
  private String commercePreviewUrl;

  @Nullable
  @Override
  public String getImageUrl(@Nonnull String imageSegment) {
    if (imageSegment.startsWith("http") || imageSegment.startsWith("//")) {
      return imageSegment;
    }
    if (imageSegment.contains("catalogimage")) {
      return getCaeUrlPrefix() + imageSegment;
    }
    return commercePreviewUrl + imageSegment;
  }

  @Nullable
  @Override
  public String getImageUrl(@Nonnull String imageSegment, boolean prependCatalogPath) {
    return null;
  }

  private String getCaeUrlPrefix() {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    if (storeContext != null) {
      String siteId = storeContext.getSiteId();
      if (siteId != null) {
        Site site = sitesService.getSite(siteId);
        if (site != null) {
          return urlPrefixResolver.getUrlPrefix(siteId, site.getSiteRootDocument(), null);
        }
      }
    }
    return "";
  }


  @Override
  @Required
  public void setCommercePreviewUrl(@Nonnull String commercePreviewUrl) {
    if (commercePreviewUrl.endsWith("/")){
      this.commercePreviewUrl = commercePreviewUrl.substring(0, commercePreviewUrl.length()-1);
    }
    else {
      this.commercePreviewUrl = commercePreviewUrl;
    }
  }

  @Override
  @Required
  public void setCommerceProductionUrl(@Nonnull String commerceProductionHost) {

  }

  @Override
  @Required
  public void setCatalogPathPrefix(String catalogPathPrefix) {

  }

  @Required
  public void setUrlPrefixResolver(UrlPrefixResolver urlPrefixResolver) {
    this.urlPrefixResolver = urlPrefixResolver;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }
}
