package com.coremedia.livecontext.handler;

import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * LC Url Provider to help with formatted not encoded commerce URLs.
 */
public interface LiveContextUrlProvider {

  /**
   * Build a link for the given category in the commerce system.
   * @param category the category
   * @param queryParams additional link parameters
   * @param request the current request
   * @return a URI components builder for the link to the commerce system
   */
  @Nullable
  UriComponentsBuilder buildCategoryLink(@Nonnull Category category,
                                         @Nonnull Map<String, Object> queryParams,
                                         @Nonnull HttpServletRequest request);

  /**
   * Build a link for the given product in the commerce system.
   * @param product the product
   * @param queryParams additional link parameters
   * @param request the current request
   * @return a URI components builder for the link to the commerce system
   */
  @Nullable
  UriComponentsBuilder buildProductLink(@Nonnull Product product,
                                        @Nonnull Map<String, Object> queryParams,
                                        @Nonnull HttpServletRequest request);

  /**
   * Build a link for the given external navigation page in the commerce system.
   * @param navigation the exernal page document
   * @param queryParams additional link parameters
   * @param request the current request
   * @return a URI components builder for the link to the commerce system
   */
  @Nullable
  UriComponentsBuilder buildPageLink(@Nonnull CMExternalPage navigation,
                                     @Nonnull Map<String, Object> queryParams,
                                     @Nonnull HttpServletRequest request,
                                     @Nonnull StoreContext storeContext);

  /**
   * Build a SEO link for the commerce system.
   * @param seoSegments the SEO segments String
   * @param queryParams additional link parameters
   * @param request the current request
   * @return a URI components builder for the link to the commerce system
   */
  @Nullable
  UriComponentsBuilder buildShopLink(@Nonnull String seoSegments,
                                     @Nonnull Map<String, Object> queryParams,
                                     @Nonnull HttpServletRequest request,
                                     @Nonnull StoreContext storeContext);
}
