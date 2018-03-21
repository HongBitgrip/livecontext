package com.coremedia.livecontext.handler;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * Provider for search URLs pointing to the commerce system.
 */
public interface CommerceSearchRedirectUrlProvider {
  /**
   * Provide the commerce search URL for redirect.
   * @param term the optional search term
   * @param request the current request
   * @param storeContext the current store context
   * @return a commerce search URL
   */
  Object provideRedirectUrl(@Nullable String term, @Nonnull HttpServletRequest request, @Nonnull StoreContext storeContext);
}
