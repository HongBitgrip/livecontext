package com.coremedia.livecontext.navigation;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;

import javax.annotation.Nonnull;

/**
 * Immutable instances of CategoryInSite.
 */
public class CategoryInSiteImpl implements CategoryInSite {
  private final Category category;
  private final Site site;

  public CategoryInSiteImpl(@Nonnull Category category, @Nonnull Site site) {
    this.category = category;
    this.site = site;
  }

  @Nonnull
  @Override
  public Category getCategory() {
    return category;
  }

  @Nonnull
  @Override
  public Site getSite() {
    return site;
  }
}
