package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A catalog {@link com.coremedia.livecontext.ecommerce.p13n.Segment} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/workspace/{siteId:[^/]+}/{id:[^/]+}")
public class WorkspaceResource extends AbstractCatalogResource<Workspace> {

  @Override
  public WorkspaceRepresentation getRepresentation() {
    WorkspaceRepresentation representation = new WorkspaceRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(WorkspaceRepresentation representation) {
    Workspace entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load workspace bean.");
    }

    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
  }

  @Override
  protected Workspace doGetEntity() {
    StoreContext storeContext = getStoreContext();
    if (storeContext == null) {
      return null;
    }

    return getWorkspaceService().findAllWorkspaces(storeContext).stream()
            .filter(workspace -> getId().equals(workspace.getExternalTechId()))
            .findFirst()
            .orElse(null);
  }

  @Override
  public void setEntity(Workspace workspace) {
    CommerceId workspaceId = workspace.getId();
    String externalId = workspaceId.getExternalId().orElseGet(workspace::getExternalId);
    setId(externalId);

    setSiteId(workspace.getContext().getSiteId());
  }

  private WorkspaceService getWorkspaceService() {
    return CurrentCommerceConnection.get().getWorkspaceService();
  }
}
