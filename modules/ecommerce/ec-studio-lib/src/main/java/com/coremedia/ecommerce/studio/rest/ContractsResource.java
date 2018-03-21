package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Contracts;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The resource handles the top level store node "Contracts".
 * It is not showed in the catalog tree but used to invalidate the list of available commerce contracts
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/contracts/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class ContractsResource extends AbstractCatalogResource<Contracts> {

  @Override
  public ContractsRepresentation getRepresentation() {
    ContractsRepresentation representation = new ContractsRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(ContractsRepresentation representation) {
    Contracts contracts = getEntity();

    if (contracts == null) {
      throw new CatalogBeanNotFoundRestException("Could not load contracts bean.");
    }

    representation.setId(contracts.getId());
    ContractService contractService = getConnection().getContractService();
    if (contractService != null) {
      Collection<Contract> contractIdsForServiceUser = contractService.findContractIdsForServiceUser(getStoreContext());
      //filter default contract from contract list
      Collection<Contract> filteredContracts = new ArrayList<>();
      for (Contract contract : contractIdsForServiceUser) {
        if (!contract.isDefaultContract()){
          filteredContracts.add(contract);
        }
      }
      representation.setContracts(filteredContracts);
    }
  }

  @Override
  protected Contracts doGetEntity() {
    return new Contracts(getStoreContext());
  }

  @Override
  public void setEntity(Contracts contracts) {
    setSiteId(contracts.getContext().getSiteId());
    setWorkspaceId(contracts.getContext().getWorkspaceId());
  }
}
