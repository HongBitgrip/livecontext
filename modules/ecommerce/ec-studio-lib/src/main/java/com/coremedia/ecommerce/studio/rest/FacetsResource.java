package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Facets;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * A facets object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/facets/{siteId:[^/]+}/{catalogAlias:[^/]+}/{workspaceId:[^/]+}/{id:.+}")
public class FacetsResource extends AbstractCatalogResource<Facets> {

  @Override
  public FacetsRepresentation getRepresentation() {
    FacetsRepresentation facetsRepresentation = new FacetsRepresentation();
    fillRepresentation(facetsRepresentation);
    return facetsRepresentation;
  }

  protected void fillRepresentation(FacetsRepresentation representation) {
    Facets facets = getEntity();

    if (facets == null) {
      throw new CatalogBeanNotFoundRestException("Could not load facets bean.");
    }

    representation.setId(facets.getId());

    CommerceConnection commerceConnection = getConnection();
    StoreContext storeContext = requireNonNull(getStoreContext(), "store context not available");
    CatalogService catalogService = requireNonNull(commerceConnection.getCatalogService(), "no catalog service available");
    CommerceIdProvider commerceIdProvider = requireNonNull(commerceConnection.getIdProvider(), "no commerce id provider available");

    CatalogAlias catalogAlias = storeContext.getCatalogAlias();
    CommerceId commerceId = commerceIdProvider.formatCategoryId(catalogAlias, getId());
    Category category = catalogService.findCategoryById(commerceId, storeContext);

    if (category != null) {
      Map<String, List<SearchFacet>> searchFacetMap = catalogService.getFacetsForProductSearch(category, storeContext);
      Map<String, List<Facet>> serializeFacets = simplifySearchFacets(searchFacetMap);
      representation.setFacets(serializeFacets);
    }
  }

  @Override
  protected Facets doGetEntity() {
    return new Facets(getStoreContext());
  }

  @Override
  public void setEntity(Facets facets) {
    StoreContext context = facets.getContext();

    setId(facets.getId());
    setSiteId(context.getSiteId());
    setCatalogAlias(context.getCatalogAlias().value());
    setWorkspaceId(context.getWorkspaceId());
  }

  private Map<String, List<Facet>> simplifySearchFacets(Map<String, List<SearchFacet>> searchFacetMap) {
    return searchFacetMap.entrySet().stream().collect(
            toMap(Map.Entry::getKey,
                    entry -> entry.getValue().stream().map(
                            searchFacet -> new Facet(searchFacet.getQuery(), searchFacet.getLabel())
                    ).collect(toList())
            )
    );
  }

}
