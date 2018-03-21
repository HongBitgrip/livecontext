package com.coremedia.livecontext.ecommerce.hybris.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.AbstractHybrisDocument;
import com.google.common.base.Joiner;

import javax.annotation.Nonnull;


public abstract class AbstractHybrisDocumentCacheKey<T extends AbstractHybrisDocument> extends AbstractCommerceCacheKey<T> {
  private final static Joiner JOINER = Joiner.on(":").useForNull("undefined");

  private final CommerceId commerceId;

  AbstractHybrisDocumentCacheKey(@Nonnull CommerceId id,
                                 StoreContext storeContext,
                                 String configKey, CommerceCache commerceCache) {
    super(CommerceIdFormatterHelper.format(id), storeContext, configKey, commerceCache);
    if (!HybrisCommerceIdProvider.isHybrisId(id)) {
      throw new InvalidIdException(id + " is not a hybris id.");
    }
    this.commerceId = id;
  }

  @Override
  public void addExplicitDependency(T document) {
    if (document != null) {
      String dependencyId = document.getCode();
      Cache.dependencyOn(dependencyId);
    }
  }

  @Nonnull
  protected String getExternalIdOrTechId() {
    return commerceId.getExternalId()
            .orElseGet(() -> commerceId.getTechId()
                    .orElseThrow(() -> new InvalidIdException("Neither external id nor external tech id is set: " + id + '.')));
  }

  @Override
  protected String getCacheIdentifier() {
    return JOINER.join(getCacheIdArgs());
  }

  protected Object[] getCacheIdArgs() {
    return new Object[]{
            id,
            configKey,
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getLocale(),
            storeContext.getCurrency()
    };
  }
}
