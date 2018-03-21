package com.coremedia.ecommerce.studio.rest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Strategy interface to resolve content for a given commerce shop url
 */
public interface PbeShopUrlTargetResolver {

  @Nullable
  Object resolveUrl(@Nonnull String urlStr, @Nullable String siteId);

}
