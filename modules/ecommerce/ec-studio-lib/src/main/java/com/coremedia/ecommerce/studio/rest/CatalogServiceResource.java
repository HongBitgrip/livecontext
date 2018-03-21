package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.rest.cap.common.represent.SuggestionRepresentation;
import com.coremedia.rest.cap.common.represent.SuggestionResultRepresentation;
import com.coremedia.rest.cap.content.SearchParameterNames;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static java.util.Collections.emptyList;

/**
 * Catalog configuration helpter as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext")
public class CatalogServiceResource {

  private static final String DEFAULT_SUGGESTIONS_LIMIT = "10";
  private static final String DEFAULT_SEARCH_LIMIT = "-1";

  private static final String SEARCH_PARAM_CATEGORY = "category";
  private static final String SEARCH_PARAM_CATALOG_ALIAS = "catalogAlias";
  private static final String SEARCH_PARAM_SITE_ID = "siteId";
  private static final String SEARCH_PARAM_QUERY = "query";
  private static final String SEARCH_PARAM_LIMIT = "limit";
  private static final String SEARCH_PARAM_ORDER_BY = "orderBy";
  private static final String SEARCH_PARAM_WORKSPACE_ID = "workspaceId";

  private static final String SEARCH_PARAM_SEARCH_TYPE = "searchType";
  public static final String SEARCH_TYPE_PRODUCT_VARIANT = "ProductVariant";
  private static final String SEARCH_TYPE_MARKETING_SPOTS = "MarketingSpot";

  @Nullable
  public CatalogService getCatalogService() {
    return CurrentCommerceConnection.get().getCatalogService();
  }

  @Nullable
  public MarketingSpotService getMarketingSpotService() {
    return CurrentCommerceConnection.get().getMarketingSpotService();
  }

  @GET
  @Path("search/{siteId:[^/]+}")
  @Nullable
  public CatalogSearchResultRepresentation search(@PathParam(SEARCH_PARAM_SITE_ID) String siteId,
                                                  @QueryParam(SEARCH_PARAM_QUERY) String query,
                                                  @QueryParam(SEARCH_PARAM_LIMIT) @DefaultValue(DEFAULT_SEARCH_LIMIT) int limit,
                                                  @QueryParam(SEARCH_PARAM_ORDER_BY) String orderBy,
                                                  @QueryParam(SEARCH_PARAM_SEARCH_TYPE) String searchType,
                                                  @QueryParam(SEARCH_PARAM_CATEGORY) String category,
                                                  @QueryParam(SEARCH_PARAM_CATALOG_ALIAS) String catalogAlias,
                                                  @QueryParam(SEARCH_PARAM_WORKSPACE_ID) String workspaceId) {
    // The site ID in the URL is ignored here, but the `SiteFilter`
    // should have picked it up so the `CommerceConnectionFilter`
    // provides a commerce connection based on the site ID.

    StoreContext newStoreContextForSite = getStoreContext(workspaceId);
    if (newStoreContextForSite == null) {
      return null;
    }

    newStoreContextForSite.setWorkspaceId(workspaceId);

    Map<String, String> params = getParams(category, catalogAlias, newStoreContextForSite, limit);
    SearchResult<? extends CommerceBean> searchResult = search(query, searchType, newStoreContextForSite, params);

    return new CatalogSearchResultRepresentation(searchResult.getSearchResult(), searchResult.getTotalCount());
  }

  @Nonnull
  private Map<String, String> getParams(String category, String catalogAlias, StoreContext storeContext, int limit) {
    Map<String, String> params = new HashMap<>();

    if (!StringUtils.isEmpty(category) && !isRootCategory(category, storeContext)) {
      params.put(CatalogService.SEARCH_PARAM_CATEGORYID, category);
    }

    if (!StringUtils.isEmpty(catalogAlias)) {
      params.put(CatalogService.SEARCH_PARAM_CATALOG_ALIAS, catalogAlias);
    }

    if (limit > 0) {
      params.put(CatalogService.SEARCH_PARAM_PAGESIZE, String.valueOf(limit));
    }

    return params;
  }

  private boolean isRootCategory(@Nonnull String categoryParam, StoreContext storeContext) {
    // check if it is our symbolic URL segment for the root category
    // (independent of the particular commerce system)
    if (CategoryResource.ROOT_CATEGORY_ROLE_ID.equals(categoryParam)) {
      return true;
    }

    // check if it is the actual id of the root category
    // (depends on the commerce implementation)
    String rootCategoryId = CommerceIdFormatterHelper.format(getCatalogService().findRootCategory(DEFAULT_CATALOG_ALIAS, storeContext).getId());
    return categoryParam.equals(rootCategoryId);
  }

  private SearchResult<? extends CommerceBean> search(String query, String searchType,
                                                      @Nonnull StoreContext newStoreContextForSite,
                                                      @Nonnull Map<String, String> params) {
    if (searchType != null && searchType.equals(SEARCH_TYPE_PRODUCT_VARIANT)) {
      return getCatalogService().searchProductVariants(query, params, newStoreContextForSite);
    } else if (searchType != null && searchType.equals(SEARCH_TYPE_MARKETING_SPOTS)) {
      if (getMarketingSpotService() == null) {
        SearchResult<? extends CommerceBean> searchResult = new SearchResult<>();
        searchResult.setSearchResult(emptyList());
        searchResult.setTotalCount(0);
        return searchResult;
      } else {
        return getMarketingSpotService().searchMarketingSpots(query, params, newStoreContextForSite);
      }
    } else {// default: Product
      return getCatalogService().withStoreContext(newStoreContextForSite).searchProducts(query, params, newStoreContextForSite);
    }
  }

  @GET
  @Path("suggestions")
  @Nonnull
  public SuggestionResultRepresentation searchSuggestions(@QueryParam(SearchParameterNames.QUERY) String query,
                                                          @QueryParam(SearchParameterNames.LIMIT) @DefaultValue(DEFAULT_SUGGESTIONS_LIMIT) int limit,
                                                          @QueryParam(SEARCH_PARAM_SEARCH_TYPE) String searchType,
                                                          @QueryParam(SEARCH_PARAM_SITE_ID) String siteId,
                                                          @QueryParam(SEARCH_PARAM_CATEGORY) String category,
                                                          @QueryParam(SEARCH_PARAM_WORKSPACE_ID) String workspaceId) {
    //TODO not supported yet
    return new SuggestionResultRepresentation(new ArrayList<SuggestionRepresentation>());
  }

  @Nullable
  protected StoreContext getStoreContext(String workspaceId) {
    StoreContext storeContext = CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContext)
            .orElse(null);

    if (storeContext == null) {
      return null;
    }

    storeContext.setWorkspaceId(workspaceId);
    return storeContext;
  }
}
