package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATALOG;

public class CatalogCacheKey extends AbstractHybrisDocumentCacheKey<CatalogDocument> {

  private CatalogResource resource;

  public CatalogCacheKey(CommerceId id,
                         StoreContext storeContext,
                         CatalogResource resource,
                         CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_CATEGORY, commerceCache);
    this.resource = resource;
    if (!CATALOG.equals(id.getCommerceBeanType())) {
      throw new InvalidIdException(id + " is not a catalog id.");
    }

  }

  @Override
  public CatalogDocument computeValue(Cache cache) {
    return resource.getCatalog(storeContext);
  }
}
