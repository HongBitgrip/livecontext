package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A catalog {@link com.coremedia.livecontext.ecommerce.p13n.Segment} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/segment/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class SegmentResource extends AbstractCatalogResource<Segment> {

  @Override
  public SegmentRepresentation getRepresentation() {
    SegmentRepresentation representation = new SegmentRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(SegmentRepresentation representation) {
    Segment entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load segment bean.");
    }

    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
  }

  @Override
  protected Segment doGetEntity() {
    StoreContext storeContext = getStoreContext();
    if (storeContext == null) {
      return null;
    }

    SegmentService segmentService = getSegmentService();
    CommerceId commerceId = getConnection().getIdProvider().formatSegmentId(getId());
    return segmentService != null ? segmentService.findSegmentById(commerceId, storeContext) : null;
  }

  @Override
  public void setEntity(Segment segment) {
    CommerceId segmentId = segment.getId();
    String externalId = segmentId.getExternalId().orElseGet(segment::getExternalId);
    setId(externalId);

    StoreContext context = segment.getContext();
    setSiteId(context.getSiteId());
    setWorkspaceId(context.getWorkspaceId());
  }

  public SegmentService getSegmentService() {
    return CurrentCommerceConnection.get().getSegmentService();
  }
}
