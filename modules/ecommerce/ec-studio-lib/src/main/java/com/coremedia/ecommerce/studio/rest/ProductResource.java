package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Currency;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * A catalog {@link Product} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path(ProductResource.URI_PATH)
public class ProductResource extends CommerceBeanResource<Product> {

  public static final String URI_PATH
          = "livecontext/product/{siteId:[^/]+}/{catalogAlias:[^/]+}/{workspaceId:[^/]+}/{id:.+}";

  @Override
  protected ProductRepresentation getRepresentation() {
    ProductRepresentation representation = new ProductRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  protected void fillRepresentation(ProductRepresentation representation) {
    super.fillRepresentation(representation);

    Product entity = getEntity();

    representation.setName(entity.getName());
    representation.setShortDescription(entity.getShortDescription().asXml());
    representation.setLongDescription(entity.getLongDescription().asXml());
    String thumbnailUrl = entity.getThumbnailUrl();
    representation.setThumbnailUrl(
            RepresentationHelper.modifyAssetImageUrl(thumbnailUrl, getContentRepositoryResource().getEntity()));
    representation.setCategory(entity.getCategory());
    representation.setStore(new Store(entity.getContext()));
    representation.setCatalog(entity.getCatalog().orElse(null));
    representation.setOfferPrice(entity.getOfferPrice());
    representation.setListPrice(entity.getListPrice());

    Currency currency = entity.getCurrency();
    Locale locale = entity.getLocale();
    if (currency != null && locale != null) {
      representation.setCurrency(currency.getSymbol(locale));
    }

    representation.setVariants(entity.getVariants());
    representation.setPictures(entity.getPictures());
    representation.setDownloads(entity.getDownloads());
    representation.setDescribingAttributes(entity.getDescribingAttributes());
    representation.setContent(getContent());
  }

  @Override
  protected Product doGetEntity() {
    CommerceConnection connection = getConnection();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "catalog service not available");

    StoreContext storeContext = getStoreContext();
    CommerceId commerceId = idProvider.formatProductId(storeContext.getCatalogAlias(), getId());

    return catalogService.findProductById(commerceId, storeContext);
  }

  @Autowired(required = false)
  @Qualifier("productAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    super.setAugmentationService(augmentationService);
  }
}
