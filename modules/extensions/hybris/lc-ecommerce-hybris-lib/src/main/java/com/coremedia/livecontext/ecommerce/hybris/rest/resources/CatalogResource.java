package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductSearchDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupsDocument;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CatalogResource extends AbstractHybrisResource {

  private static final String CATALOG_PATH = "/catalogs/{catalogId}/catalogversions/{catalogVersion}/";

  private static final String CATEGORIES_PATH = "/catalogs/{catalogId}/catalogversions/{catalogVersion}/categories/{categoryId}";

//  private static final String PRODUCTS_FOR_CATEGORY_PATH = "/{storeId}/cmocc/{catalogId}/{catalogVersion}/category/{categoryId}/products";

  private static final String PRICES_PATH = "/pricerows/{priceId}";

  private static final String PRODUCTS_PATH = "/catalogs/{catalogId}/catalogversions/{catalogVersion}/products/{productId}";
  //private static final String PRODUCTS_SKU_PATH = PRODUCTS_PATH + "/{sku}";

  private static final String PRODUCT_SEARCH_PATH = "/{storeId}/products/search";

  private static final String USER_GROUPS_PATH = "/usergroups/";
  private static final String USER_GROUP_BY_ID_PATH = "/usergroups/{groupId}";
  private static final String FIELDS_PARAM = "fields";

  @Nullable
  public CatalogDocument getCatalog(@Nonnull StoreContext storeContext) {
    String catalogId = StoreContextHelper.getCatalogId();
    String catalogVersion = StoreContextHelper.getCatalogVersion();
    List<String> uriTemplateParameters = newUriTemplateParameters(storeContext, catalogId, catalogVersion);
    return getConnector().performGet(CATALOG_PATH, storeContext, CatalogDocument.class, uriTemplateParameters);
  }

  public List<UserGroupRefDocument> getAllUserGroups(@Nonnull StoreContext storeContext) {
    UserGroupsDocument userGroupDocuments = getConnector().performGet(USER_GROUPS_PATH, storeContext,
            UserGroupsDocument.class);
    return userGroupDocuments.getUserGroups();
  }

  @Nullable
  public UserGroupDocument getUserGroup(String userGroupId, @Nonnull StoreContext storeContext) {
    List<String> uriTemplateParameters = newUriTemplateParameters("userGroupId", userGroupId);
    return getConnector().performGet(USER_GROUP_BY_ID_PATH, storeContext, UserGroupDocument.class, uriTemplateParameters);
  }

  @Nullable
  public CategoryDocument getCategoryById(@Nonnull String categoryId, @Nonnull StoreContext storeContext) {
    String catalogId = storeContext.getCatalogId();
    String catalogVersion = storeContext.getCatalogVersion();
    List<String> uriTemplateParameters = newUriTemplateParameters(storeContext, catalogId, catalogVersion, categoryId);
    return getConnector().performGet(CATEGORIES_PATH, storeContext, CategoryDocument.class, uriTemplateParameters);
  }

  @Nullable
  public ProductDocument getProductById(String productId, @Nonnull StoreContext storeContext) {
    String catalogId = StoreContextHelper.getCatalogId();
    String catalogVersion = StoreContextHelper.getCatalogVersion();
    List<String> uriTemplateParameters  = newUriTemplateParameters(storeContext, catalogId, catalogVersion, productId);
    return getConnector().performGet(PRODUCTS_PATH, storeContext, ProductDocument.class, uriTemplateParameters);
  }

  @Nullable
  public PriceDocument getPriceDocumentById(String priceId, @Nonnull StoreContext storeContext) {
    List<String> uriTemplateParameters = newUriTemplateParameters("priceId", priceId);
    return getConnector().performGet(PRICES_PATH, storeContext, PriceDocument.class, uriTemplateParameters);
  }

  @Nullable
  public ProductSearchDocument searchProducts(@Nonnull String searchTerm, @Nonnull Map<String, String> searchParams,
                                              @Nonnull StoreContext storeContext) {
    List<String> pathParams = newUriTemplateParameters(storeContext, StoreContextHelper.getStoreId());

    MultiValueMap<String, String> queryParams = prepareQueryParams(searchTerm, searchParams);

    return getOccConnector().performGet(PRODUCT_SEARCH_PATH, storeContext, ProductSearchDocument.class, pathParams,
            queryParams, true);
  }

  @Nonnull
  private static MultiValueMap<String, String> prepareQueryParams(@Nonnull String searchTerm, @Nonnull Map<String, String> searchParams) {

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    String sortParam = "sort";
    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_ORDERBY)) {
      queryParams.add(sortParam, mapOrderByType(searchParams.get(CatalogService.SEARCH_PARAM_ORDERBY)));
    } else {
      queryParams.add(sortParam, OrderByType.ORDER_BY_DEFAULT.getValue());
    }
    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_PAGENUMBER)) {
      queryParams.add(CatalogServiceImpl.SEARCH_PARAM_PAGENUMBER, searchParams.get(CatalogService.SEARCH_PARAM_PAGENUMBER));
    }
    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_PAGESIZE)) {
      queryParams.add(CatalogServiceImpl.SEARCH_PARAM_PAGESIZE, searchParams.get(CatalogService.SEARCH_PARAM_PAGESIZE));
    }
    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_CATEGORYID)) {
      queryParams.add(CatalogServiceImpl.SEARCH_PARAM_CATEGORYID, searchParams.get(CatalogService.SEARCH_PARAM_CATEGORYID));
    }
    if (searchParams.containsKey(FIELDS_PARAM)) {
      queryParams.add(FIELDS_PARAM, searchParams.get(FIELDS_PARAM));
    }

    StringBuilder queryTerm = new StringBuilder("*".equals(searchTerm) ? "" : searchTerm);
    queryTerm.append(":");
    queryTerm.append("");
    for(String key : queryParams.keySet()) {
      String value = queryParams.getFirst(key);
      if (!sortParam.equals(key)) {
        queryTerm.append(":");
        queryTerm.append(key);
        queryTerm.append(":");
        queryTerm.append(value);
      }
    }
    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_FACET)) {
      String facet = searchParams.get(CatalogService.SEARCH_PARAM_FACET);
      if (!StringUtils.isEmpty(facet)) {
        if (facet.contains(OrderByType.ORDER_BY_DEFAULT.getValue())) {
          facet = facet.replace(":" + OrderByType.ORDER_BY_DEFAULT.getValue(), "");
        }
        queryTerm.append(facet);
      }
    }

    queryParams.add("query", queryTerm.toString());

    return queryParams;
  }

  private static String mapOrderByType(String orderByType) {
    try {
      return OrderByType.valueOf(orderByType).getValue();
    } catch (IllegalArgumentException e) {
      return OrderByType.ORDER_BY_DEFAULT.getValue();
    }
  }


}
