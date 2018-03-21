package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;

import javax.annotation.Nonnull;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;

public class CategoryCacheKey extends AbstractHybrisDocumentCacheKey<CategoryDocument> {

  private CatalogResource resource;

  public CategoryCacheKey(@Nonnull CommerceId id,
                          StoreContext storeContext,
                          CatalogResource resource,
                          CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_CATEGORY, commerceCache);
    this.resource = resource;
    if (!CATEGORY.equals(id.getCommerceBeanType())) {
      throw new InvalidIdException(id + " is not a category id.");
    }
  }

  @Override
  public CategoryDocument computeValue(Cache cache) {
    String externalId = getExternalIdOrTechId();
    return resource.getCategoryById(externalId, storeContext);
  }

}
