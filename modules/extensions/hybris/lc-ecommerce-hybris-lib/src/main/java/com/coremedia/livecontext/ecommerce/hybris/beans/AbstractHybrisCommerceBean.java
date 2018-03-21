package com.coremedia.livecontext.ecommerce.hybris.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.hybris.cache.AbstractHybrisDocumentCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.AbstractHybrisDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.xml.Markup;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

public abstract class AbstractHybrisCommerceBean extends AbstractCommerceBean {

  private AbstractHybrisDocument delegate;

  private CatalogResource catalogResource;

  private CommerceCache commerceCache;

  CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  public AbstractHybrisDocument getDelegate() {
    if (delegate == null) {
      load();
    }

    return delegate;
  }

  public AssetUrlProvider getAssetUrlProvider() {
    return CurrentCommerceConnection.get().getAssetUrlProvider();
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   *
   * @param delegate the arbitrarily backing object
   */
  public void setDelegate(AbstractHybrisDocument delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getExternalId() {
    // do not call the getDelegate() method because it has consequences regarding a untimely loading of beans
    CommerceId commerceId = getId();
    return commerceId.getExternalId().orElseGet(() -> commerceId.getTechId().orElse(null));
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  public CatalogResource getCatalogResource() {
    return catalogResource;
  }

  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }

  protected <T extends AbstractHybrisDocument> void loadCached(AbstractHybrisDocumentCacheKey<T> cacheKey) {
    AbstractHybrisDocument document = getCommerceCache().find(cacheKey)
            .orElseThrow(() -> new NotFoundException("Commerce object not found with id '" + getId() + "'."));

    setDelegate(document);
  }

  protected static Markup buildRichtextMarkup(String str) {
    return toRichtext(str, false);
  }

  @Override
  public Locale getLocale() {
    return getContext().getLocale();
  }
}
