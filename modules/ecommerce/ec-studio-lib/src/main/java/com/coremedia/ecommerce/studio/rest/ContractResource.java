package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * A catalog {@link Contract} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/contract/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class ContractResource extends AbstractCatalogResource<Contract> {

  @Override
  public ContractRepresentation getRepresentation() {
    ContractRepresentation representation = new ContractRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(ContractRepresentation representation) {
    Contract entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load contract bean.");
    }

    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
  }

  @Override
  protected Contract doGetEntity() {
    ContractService contractService = getContractService();
    if (contractService == null) {
      return null;
    }

    String id = getId();

    // Iterating all eligible contracts is a workaround since the
    // `findContractById` call does not consider the store ID.
    // Therefor we make use of the eligible call since it does consider
    // the store ID.
    StoreContext storeContext = getStoreContext();
    if (storeContext != null) {
      Collection<Contract> contracts = contractService.findContractIdsForServiceUser(storeContext);
      for (Contract contract : contracts) {
        String externalId = contract.getExternalId();
        if (externalId != null && externalId.equals(id)) {
          return contractService.findContractById(contract.getId(), storeContext);
        }
      }
    }

    return null;
  }

  @Override
  public void setEntity(Contract contract) {
    CommerceId contractId = contract.getId();
    String externalId = CommerceIdHelper.getExternalIdOrThrow(contractId);
    setId(externalId);

    StoreContext context = contract.getContext();
    setSiteId(context.getSiteId());
    setWorkspaceId(context.getWorkspaceId());
  }

  public ContractService getContractService() {
    return CurrentCommerceConnection.get().getContractService();
  }
}
