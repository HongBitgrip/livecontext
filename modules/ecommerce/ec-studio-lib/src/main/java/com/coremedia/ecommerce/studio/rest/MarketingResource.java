package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Marketing;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The resource is just used for handling the top level store node "Marketing Spots".
 * It is not necessary for the commerce API. This ensures a unified handling
 * of tree nodes in the Studio library window.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/marketing/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class MarketingResource extends AbstractCatalogResource<Marketing> {

  @Override
  public MarketingRepresentation getRepresentation() {
    MarketingRepresentation representation = new MarketingRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(MarketingRepresentation representation) {
    Marketing entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load marketing bean.");
    }

    representation.setId(entity.getId());
    MarketingSpotService marketingSpotService = getConnection().getMarketingSpotService();
    if (marketingSpotService != null) {
      representation.setMarketingSpots(marketingSpotService.findMarketingSpots(entity.getContext()));
    }
  }

  @Override
  protected Marketing doGetEntity() {
    return new Marketing(getStoreContext());
  }

  @Override
  public void setEntity(Marketing marketing) {
    setSiteId(marketing.getContext().getSiteId());
    setWorkspaceId(marketing.getContext().getWorkspaceId());
  }
}
