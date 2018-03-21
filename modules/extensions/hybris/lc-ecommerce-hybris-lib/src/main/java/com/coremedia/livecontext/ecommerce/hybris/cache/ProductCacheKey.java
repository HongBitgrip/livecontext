package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;

import javax.annotation.Nonnull;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;

public class ProductCacheKey extends AbstractHybrisDocumentCacheKey<ProductDocument> {

  private CatalogResource resource;

  public ProductCacheKey(@Nonnull CommerceId id,
                         StoreContext storeContext,
                         CatalogResource resource,
                         CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_PRODUCT, commerceCache);
    this.resource = resource;
    CommerceBeanType commerceBeanType = id.getCommerceBeanType();
    if (!PRODUCT.equals(commerceBeanType) && !SKU.equals(commerceBeanType)) {
      throw new InvalidIdException(id + " is neither a product nor sku id.");
    }
  }

  @Override
  public ProductDocument computeValue(Cache cache) {
    String externalId = getExternalIdOrTechId();
    return resource.getProductById(externalId, storeContext);
  }

}
