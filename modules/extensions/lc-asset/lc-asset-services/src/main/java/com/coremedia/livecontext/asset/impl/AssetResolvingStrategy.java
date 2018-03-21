package com.coremedia.livecontext.asset.impl;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The AssetResolvingStrategy is used to define an algorithm to find
 * assets in the content management system linked to a thirdparty system.
 */
public interface AssetResolvingStrategy {
  /**
   * Find asset contents of the given type linked to the given id of the external content below the given site.
   *
   * @param contentType The content type of the asset.
   * @param id         The full qualified id to an external content (e.g. product).
   * @param site       The site to search in
   * @return a list of contents of the given content type linking to the external content (e.g. product)
   */
  @Nonnull
  List<Content> findAssets(@Nonnull String contentType,
                           @Nonnull CommerceId id,
                           @Nonnull Site site);
}
