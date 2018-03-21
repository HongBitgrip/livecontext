package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nullable;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * A catalog {@link Catalog} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/catalog/{siteId:[^/]+}/{id:[^/]+}")
public class CatalogResource extends AbstractCatalogResource<Catalog> {

  @Override
  public CatalogRepresentation getRepresentation() {
    CatalogRepresentation representation = new CatalogRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(CatalogRepresentation representation) {
    Catalog entity = getEntity();

    if (entity == null) {
      LOG.warn("Error loading catalog bean");
      throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load workspace bean");
    }

    representation.setDefault(entity.isDefaultCatalog());
    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName().value());

    Category rootCategory = entity.getRootCategory();
    representation.setTopCategories(rootCategory.getChildren());
    representation.setRootCategory(rootCategory);
  }

  @Override
  protected Catalog doGetEntity() {
    CatalogId catalogId = getCatalogId();
    StoreContext storeContext = getStoreContext();
    if (storeContext == null) {
      return null;
    }
    return getCatalogService().getCatalog(catalogId, storeContext).orElse(null);
  }

  @Nullable
  @Override
  protected StoreContext getStoreContext() {
    StoreContext storeContext = super.getStoreContext();
    if (storeContext == null) {
      return null;
    }

    CatalogAliasTranslationService catalogAliasTranslationService = getCatalogAliasTranslationService();
    if (catalogAlias != null) {
      storeContext.setCatalog(catalogAlias, getCatalogId());
    } else {
      CatalogId catalogId = getCatalogId();
      Optional<CatalogAlias> catalogAliasForId = catalogAliasTranslationService.getCatalogAliasForId(catalogId, getSiteId());
      storeContext.setCatalog(catalogAliasForId.orElse(null), catalogId);
    }

    return storeContext;
  }

  @Override
  public void setEntity(Catalog catalog) {
    CommerceId commerceId = catalog.getId();
    String extId = commerceId.getExternalId().orElseGet(catalog::getExternalId);
    setId(extId);
    setSiteId(catalog.getContext().getSiteId());
  }

  public CatalogService getCatalogService() {
    return CurrentCommerceConnection.get().getCatalogService();
  }

  private CatalogId getCatalogId() {
    return CatalogId.of(getId());
  }
}
