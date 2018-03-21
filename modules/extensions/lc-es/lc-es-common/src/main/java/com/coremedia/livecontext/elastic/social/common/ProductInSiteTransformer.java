package com.coremedia.livecontext.elastic.social.common;

import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetTransformer;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.ProductInSite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;

@Named
public class ProductInSiteTransformer implements ContributionTargetTransformer<ProductInSite,ProductInSite> {

  @Nonnull
  @Override
  public ProductInSite transform(@Nonnull ProductInSite target) {
    return target;
  }

  @Nullable
  @Override
  public Site getSite(@Nonnull ProductInSite target) {
    return target.getSite();
  }

  @Nonnull
  @Override
  public Class<ProductInSite> getType() {
    return ProductInSite.class;
  }
}
