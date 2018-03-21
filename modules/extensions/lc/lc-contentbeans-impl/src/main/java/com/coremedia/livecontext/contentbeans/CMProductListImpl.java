package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.cae.contentbeans.CMQueryListImpl;
import com.coremedia.blueprint.common.contentbeans.CMCollection;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.cae.contentbeans.CMQueryListImpl;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.navigation.ProductInSiteImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class CMProductListImpl extends CMQueryListImpl implements CMProductList {

  private static final Logger LOG = LoggerFactory.getLogger(CMProductListImpl.class);

  public static final String EXTERNAL_ID = "externalId";

  public static final int MAX_LENGTH_DEFAULT = 10;
  public static final int OFFSET_DEFAULT = 0;
  public static final String DIGIT_PATTERN = "[0-9]*";
  public static final String EMPTY_STRING = "";
  public static final String ALL_QUERY = "*";
  public static final String CATALOG_SERVICE_NOT_AVAILABLE = "catalog service not available";

  private String overrideCategoryId;

  @Inject
  private CommerceConnectionSupplier commerceConnectionSupplier;

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMProductList} objects
   */
  @Override
  public CMProductList getMaster() {
    return (CMProductList) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMProductList> getVariantsByLocale() {
    return getVariantsByLocale(CMProductList.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMProductList> getLocalizations() {
    return (Collection<? extends CMProductList>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMProductList>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMProductList>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMProductList>> getAspects() {
    return (List<? extends Aspect<? extends CMProductList>>) super.getAspects();
  }

  @Override
  public String getExternalId() {
    return getContent().getString(EXTERNAL_ID);
  }

  public Category getCategory() {
    Optional<CommerceId> categoryIdOptional = parseCommerceId(getExternalId());

    if (!categoryIdOptional.isPresent()) {
      return null;
    }

    Optional<CommerceConnection> commerceConnection = commerceConnectionSupplier.findConnectionForContent(getContent());

    if (!commerceConnection.isPresent()) {
      return null;
    }

    CommerceConnection connection = commerceConnection.get();

    StoreContext storeContext = connection.getStoreContext();
    CommerceId commerceId = categoryIdOptional.get();

    try {
      CatalogService catalogService = requireNonNull(connection.getCatalogService(), "catalog service not available");
      return catalogService.findCategoryById(commerceId, storeContext);
    } catch (CommerceException e) {
      LOG.warn("Could not retrieve category for Product List {}.", this, e);
      return null;
    }
  }

  @Override
  public List<Linkable> getItems() {
    List<Map<String, Object>> fixedItemsStructList = getFixedItemsStructList();
    List products = getProducts(); // Products should be Linkables
    return mergeFixedItems(fixedItemsStructList, products, getMaxLength());
  }


  public String getOrderBy() {
    Object value = getProductListSettings().get("orderBy");
    return value instanceof String ? value.toString() : null;
  }

  public int getOffset() {
    Object value = getProductListSettings().get("offset");
    return value instanceof String ? Integer.parseInt((String) value) : OFFSET_DEFAULT;
  }

  @Override
  public int getMaxLength() {
    Object value = getProductListSettings().get("maxLength");
    return value instanceof String ? Integer.parseInt((String)value) : MAX_LENGTH_DEFAULT;
  }

  public List<ProductInSite> getProducts() {
    Site site = getSitesService().getSiteAspect(getContent()).getSite();
    if (site == null) {
      LOG.debug("Site not found for content: " + getContent());
      return Collections.emptyList();
    }

    String facet = getFacet();
    Category category = getCategory();
    CatalogAlias catalogAlias = category != null ? category.getReference().getCatalogAlias() : null;

    Optional<CommerceConnection> commerceConnection = commerceConnectionSupplier.findConnectionForContent(getContent());

    if (!commerceConnection.isPresent()) {
      return Collections.emptyList();
    }

    CommerceConnection connection = commerceConnection.get();

    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "catalog service not available");
    SearchResult<Product> searchResult = catalogService.searchProducts(getQuery(),
            getSearchParams(category, catalogAlias, getOrderBy(), getMaxLength(), getOffset(), facet), connection.getStoreContext());
    return searchResult.getSearchResult().stream()
            .map(product -> new ProductInSiteImpl(product, site))
            .collect(toList());
  }

  @Override
  public String getFacet() {
    Object value = getProductListSettings().get("selectedFacetValue");
    String strValue = EMPTY_STRING;
    if (value != null) {
      strValue = (String) value;
      if (strValue.matches(DIGIT_PATTERN)) {
        overrideCategoryId = strValue;
        return "";
      }
    }
    return strValue;
  }

  @Override
  public String getQuery() {
    return ALL_QUERY;
  }

  @Override
  public Map<String, Object> getProductListSettings() {
    Map<String, Object> result = new HashMap<>();
    try {
      if (getLocalSettings() != null) {
        Map<String, Object> structMap = getLocalSettings().getStruct("productList").getProperties();
        //copy struct because it may be cached and the cache MUST NEVER be modified.
        for (Map.Entry<String, Object> entry : structMap.entrySet()) {
          if (entry.getValue() != null) {
            result.put(entry.getKey(), entry.getValue().toString());
          }
        }
      }
    } catch (NoSuchPropertyDescriptorException e) {
      //no struct configured for current content, empty map will be returned.
    }
    return result;
  }

  @Nonnull
  private Map<String, String> getSearchParams(@Nullable Category category, @Nullable CatalogAlias catalogAlias,
                                              String orderBy, int limit, int offset, String facet) {
    Map<String, String> params = new HashMap<>();

    if (!StringUtils.isEmpty(overrideCategoryId)) {
      params.put(CatalogService.SEARCH_PARAM_CATEGORYID, overrideCategoryId);
    } else if (category != null && !category.isRoot()) {
      params.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    }

    if (catalogAlias != null && !StringUtils.isEmpty(catalogAlias.value())) {
      params.put(CatalogService.SEARCH_PARAM_CATALOG_ALIAS, catalogAlias.value());
    }

    if (!StringUtils.isEmpty(orderBy)) {
      params.put(CatalogService.SEARCH_PARAM_ORDERBY, orderBy);
    }

    if (limit >= 0) {
      params.put(CatalogService.SEARCH_PARAM_TOTAL, String.valueOf(limit));
    }

    if (offset > 0) {
      params.put(CatalogService.SEARCH_PARAM_OFFSET, String.valueOf(offset));
    }

    if (StringUtils.isNotEmpty(facet)) {
      params.put(CatalogService.SEARCH_PARAM_FACET, facet);
    }

    return params;
  }
}
