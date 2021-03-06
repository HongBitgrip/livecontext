package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class Store implements CommerceObject {

  private StoreContext context;

  public Store(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "store-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

  /**
   * @return Returns the web URL of the commerce system's management tool
   */
  @Nonnull
  public Optional<String> getVendorUrl() {
    return getConnectionAttribute(CommerceConnection::getVendorUrl);
  }

  @Nonnull
  public Optional<String> getVendorVersion() {
    return getConnectionAttribute(CommerceConnection::getVendorVersion);
  }

  @Nonnull
  public Optional<String> getVendorName() {
    return getConnectionAttribute(CommerceConnection::getVendorName);
  }

  @Nonnull
  private static Optional<String> getConnectionAttribute(Function<CommerceConnection, String> getter) {
    return CurrentCommerceConnection.find().map(getter);
  }

  @Nonnull
  public Optional<Catalog> getDefaultCatalog() {
    CatalogService catalogService = getCatalogService();

    if (catalogService == null || !hasStoreName()) {
      return Optional.empty();
    }

    return catalogService.getDefaultCatalog(context);
  }

  @Nonnull
  public List<Catalog> getCatalogs() {
    CatalogService catalogService = getCatalogService();

    if (catalogService == null || !hasStoreName()) {
      return emptyList();
    }

    return catalogService.getCatalogs(context);
  }

  @Nonnull
  public List<Category> getRootCategories() {
    CatalogService catalogService = getCatalogService();

    if (catalogService == null || !hasStoreName()) {
      return emptyList();
    }

    return catalogService.getCatalogs(context).stream()
            .map(Catalog::getRootCategory)
            .filter(Objects::nonNull)
            .collect(toList());
  }

  @Nullable
  private static CatalogService getCatalogService() {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getCatalogService)
            .orElse(null);
  }

  private boolean hasStoreName() {
    return context.getStoreName() != null;
  }
}
