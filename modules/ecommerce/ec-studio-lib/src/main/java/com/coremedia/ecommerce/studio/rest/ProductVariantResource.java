package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.content.ContentRepositoryResource;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static java.util.Objects.requireNonNull;

/**
 * A catalog {@link ProductVariant} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path(ProductVariantResource.URI_PATH)
public class ProductVariantResource extends CommerceBeanResource<ProductVariant> {

  static final String URI_PATH = "livecontext/sku/{siteId:[^/]+}/{catalogAlias:[^/]+}/{workspaceId:[^/]+}/{id:.+}";

  @Inject
  private ContentRepositoryResource contentRepositoryResource;

  @Override
  public ProductVariantRepresentation getRepresentation() {
    ProductVariantRepresentation productVariantRepresentation = new ProductVariantRepresentation();
    fillRepresentation(productVariantRepresentation);
    return productVariantRepresentation;
  }

  private void fillRepresentation(ProductVariantRepresentation representation) {
    super.fillRepresentation(representation);
    ProductVariant entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load sku bean");
    }

    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
    String shortDescription = entity.getShortDescription().asXml();
    representation.setShortDescription(shortDescription);
    String longDescription = entity.getLongDescription().asXml();
    representation.setLongDescription(longDescription);
    String thumbnailUrl = entity.getThumbnailUrl();
    representation.setThumbnailUrl(RepresentationHelper.modifyAssetImageUrl(thumbnailUrl, contentRepositoryResource.getEntity()));
    representation.setParent(entity.getParent());
    representation.setCategory(entity.getCategory());
    representation.setStore(new Store(entity.getContext()));
    representation.setCatalog(entity.getCatalog().orElse(null));
    representation.setOfferPrice(entity.getOfferPrice());
    representation.setListPrice(entity.getListPrice());
    representation.setCurrency(entity.getCurrency().getSymbol(entity.getLocale()));
    representation.setPictures(entity.getPictures());
    representation.setDownloads(entity.getDownloads());
    representation.setDefiningAttributes(entity.getDefiningAttributes());
    representation.setDescribingAttributes(entity.getDescribingAttributes());
  }

  @Override
  protected ProductVariant doGetEntity() {
    CommerceConnection connection = getConnection();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "catalog service not available");
    StoreContext storeContext = getStoreContext();
    CommerceId commerceId = idProvider.formatProductVariantId(storeContext.getCatalogAlias(), getId());
    return catalogService.findProductVariantById(commerceId, storeContext);
  }
}
