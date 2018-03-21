package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Segments;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The resource handles the top level store node "Segments".
 * It is not showed in the catalog tree but used to invalidate the list of available commerce segments
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/segments/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class SegmentsResource extends AbstractCatalogResource<Segments> {

  @Override
  public SegmentsRepresentation getRepresentation() {
    SegmentsRepresentation representation = new SegmentsRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(SegmentsRepresentation representation) {
    Segments segments = getEntity();

    if (segments == null) {
      throw new CatalogBeanNotFoundRestException("Could not load segments bean.");
    }

    representation.setId(segments.getId());
    SegmentService segmentService = getConnection().getSegmentService();
    if (segmentService != null) {
      representation.setSegments(segmentService.findAllSegments(segments.getContext()));
    }
  }

  @Override
  protected Segments doGetEntity() {
    return new Segments(getStoreContext());
  }

  @Override
  public void setEntity(Segments segments) {
    setSiteId(segments.getContext().getSiteId());
    setWorkspaceId(segments.getContext().getWorkspaceId());
  }
}
