package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A catalog {@link com.coremedia.livecontext.ecommerce.p13n.MarketingSpot} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/marketingspot/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class MarketingSpotResource extends AbstractCatalogResource<MarketingSpot> {

  @Override
  public MarketingSpotRepresentation getRepresentation() {
    MarketingSpotRepresentation representation = new MarketingSpotRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(MarketingSpotRepresentation representation) {
    MarketingSpot entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load spot bean");
    }

    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName());
    representation.setShortDescription(entity.getDescription());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
    representation.setStore(new Store(entity.getContext()));
  }

  @Override
  protected MarketingSpot doGetEntity() {
    StoreContext storeContext = getStoreContext();
    if (storeContext == null) {
      return null;
    }

    CommerceConnection connection = getConnection();
    MarketingSpotService marketingSpotService = connection.getMarketingSpotService();
    if (marketingSpotService == null) {
      return null;
    }
    CommerceIdProvider idProvider = connection.getIdProvider();
    if (!(idProvider instanceof BaseCommerceIdProvider)) {
      return null;
    }
    CommerceId commerceId = ((BaseCommerceIdProvider) idProvider).builder(BaseCommerceBeanType.MARTETING_SPOT).withExternalId(getId()).build();
    return marketingSpotService.findMarketingSpotById(commerceId, storeContext);
  }

  @Override
  public void setEntity(MarketingSpot spot) {
    CommerceId commerceId = spot.getId();
    String extId = commerceId.getExternalId()
            .orElse(spot.getExternalId());
    setId(extId);
    setSiteId(spot.getContext().getSiteId());
    setWorkspaceId(spot.getContext().getWorkspaceId());
  }
}
