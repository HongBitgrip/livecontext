package com.coremedia.livecontext.ecommerce.hybris.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.CategoryImpl;
import com.coremedia.livecontext.ecommerce.hybris.cache.CatalogCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductSearchDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATALOG;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;
import static com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceIdProvider.commerceId;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

public class CatalogServiceImpl extends AbstractHybrisService implements CatalogService {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceImpl.class);

  public static final String SEARCH_PARAM_PAGENUMBER = "currentPage";
  public static final String SEARCH_PARAM_CATEGORYID = "category";

  private CatalogResource catalogResource;

  private int maxProductSearchResults = 50;

  @Nullable
  @Override
  public Product findProductById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    CommerceCache cache = getCommerceCache();

    ProductCacheKey cacheKey = new ProductCacheKey(id, storeContext, catalogResource, cache);

    return cache.find(cacheKey)
            .map(p -> createProductBean(p, storeContext))
            .orElse(null);
  }

  @Nullable
  private Product createProductBean(@Nonnull ProductDocument productDocument, @Nonnull StoreContext storeContext) {
    if (productDocument.getBaseProduct() == null) {
      return createBeanFor(productDocument, storeContext, PRODUCT, Product.class);
    } else {
      return createBeanFor(productDocument, storeContext, SKU, ProductVariant.class);
    }
  }

  @Nullable
  @Override
  public Product findProductBySeoSegment(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    throw new UnsupportedOperationException("Hybris webservice does not support to find products by seo segments.");
  }

  @Nullable
  @Override
  public ProductVariant findProductVariantById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    CommerceCache cache = getCommerceCache();

    ProductCacheKey cacheKey = new ProductCacheKey(id, storeContext, catalogResource, cache);

    return cache.find(cacheKey)
            .map(productDocument -> createBeanFor(productDocument, storeContext, SKU, ProductVariant.class))
            .orElse(null);
  }

  @Nonnull
  @Override
  public List<Product> findProductsByCategory(@Nonnull Category category) {
    CategoryImpl categoryImpl = (CategoryImpl) category;
    CategoryDocument categoryDocument = categoryImpl.getDelegate();
    List<ProductRefDocument> productRefs = categoryDocument.getProducts();

    List<ProductRefDocument> productRefDocuments = productRefs != null ? filterProductRefs(productRefs) : emptyList();

    return resolveProductRefs(productRefDocuments, category.getContext());
  }

  @Nonnull
  @Override
  public List<Category> findTopCategories(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    String catalogId = StoreContextHelper.getCatalogId(storeContext);
    CommerceCache cache = getCommerceCache();

    CommerceId catalogCommerceId = commerceId(CATALOG).withExternalId(catalogId).build();
    CatalogCacheKey cacheKey = new CatalogCacheKey(catalogCommerceId, storeContext, catalogResource, cache);
    Optional<CatalogDocument> catalogDocument = cache.find(cacheKey);

    List<CategoryRefDocument> rootCategories = catalogDocument
            .map(CatalogDocument::getRootCategories)
            .orElse(emptyList());

    List<Category> topCategories = new ArrayList<>();
    for (CategoryRefDocument categoryRefDocument : rootCategories) {
      String externalId = categoryRefDocument.getCode();
      if (externalId != null) {
        CommerceId commerceId = commerceId(CATEGORY).withExternalId(externalId).build();
        Category category = findCategoryById(commerceId, storeContext);
        if (category == null) {
          throw new NotFoundException("Top level category not found with id: " + externalId);
        }

        topCategories.add(category);
      }
    }

    return topCategories;
  }

  @Nonnull
  @Override
  public List<Category> findSubCategories(@Nonnull Category parentCategory) {
    if (parentCategory.isRoot()) {
      CatalogAlias catalogAlias = parentCategory.getId().getCatalogAlias();
      return findTopCategories(catalogAlias, parentCategory.getContext());
    }

    CategoryImpl parentCategoryImpl = (CategoryImpl) parentCategory;
    CategoryDocument categoryDocument = parentCategoryImpl.getDelegate();
    List<Category> subCategories = new ArrayList<>();
    List<CategoryRefDocument> subCategoryRefDocuments = categoryDocument.getSubCategories();
    if (subCategoryRefDocuments != null) {
      for (CategoryRefDocument subCategoryRefDeocument : subCategoryRefDocuments) {
        String code = subCategoryRefDeocument.getCode();
        CommerceId commerceId = commerceId(CATEGORY).withExternalId(code).build();
        Category subCategory = findCategoryById(commerceId, parentCategory.getContext());
        if (subCategory == null) {
          LOG.warn("Cannot find subcategory '{}'.", code);
        } else {
          subCategories.add(subCategory);
        }
      }
    }
    return subCategories;
  }

  @Nullable
  @Override
  public Category findCategoryById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    if (CategoryImpl.isRootCategoryId(id)) {
      return findRootCategory(id.getCatalogAlias(), storeContext);
    }

    CommerceCache cache = getCommerceCache();

    CategoryCacheKey cacheKey = new CategoryCacheKey(id, storeContext, catalogResource, cache);

    return cache.find(cacheKey)
            .map(categoryDocument -> createBeanFor(categoryDocument, storeContext, CATEGORY, Category.class))
            .orElse(null);
  }

  @Nullable
  @Override
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    return null;
  }

  @Nonnull
  @Override
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm,
                                              @Nonnull Map<String, String> searchParams,
                                              @Nonnull StoreContext storeContext) {
    SearchResult<Product> result = new SearchResult<>();
    // catalogAlias processing to be implemented with CMS-9516 (multi catalog support for hybris)

    // in some cases of an id as search term we do not find a product, therefore we try a direct find call first...
    CommerceId commerceId = commerceId(PRODUCT).withExternalId(searchTerm).build();
    Product product = findProductById(commerceId, storeContext);
    if (product != null) {
      List<Product> products = convertToProducts(Collections.singletonList(product));
      if (!products.isEmpty()) {
        result.setSearchResult(products);
        result.setTotalCount(products.size());
        result.setPageSize(products.size());
        result.setPageNumber(1);
        return result;
      }
    }
    result = searchProductsPaginated(searchTerm, searchParams, storeContext, Product.class);
    return result;
  }

  @Nonnull
  @Override
  public Map<String, List<SearchFacet>> getFacetsForProductSearch(@Nonnull Category category,
                                                                  @Nonnull StoreContext storeContext) {
    String categoryId = category.getExternalTechId();
    Map<String, String> searchParams = ImmutableMap.of(
            CatalogService.SEARCH_PARAM_CATEGORYID, categoryId,
            "fields", "DEFAULT,facets",
            CatalogService.SEARCH_PARAM_PAGESIZE, "1",
            CatalogService.SEARCH_PARAM_PAGENUMBER, "1",
            CatalogService.SEARCH_PARAM_TOTAL, "1");

    SearchResult<Product> searchResult = searchProducts("*", searchParams, storeContext);

    return searchResult.getFacets().stream()
            .collect(toMap(SearchFacet::getLabel, SearchFacet::getChildFacets));
  }

  @Nonnull
  @Override
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm,
                                                            @Nonnull Map<String, String> searchParams,
                                                            @Nonnull StoreContext storeContext) {
    SearchResult<ProductVariant> result = new SearchResult<>();
    // catalogAlias processing to be implemented with CMS-9516 (multi catalog support for hybris)

    CommerceId commerceId = commerceId(SKU).withExternalId(searchTerm).build();
    Product product = findProductById(commerceId, storeContext);
    if (product != null) {
      if (product.isVariant()) {
        result.setSearchResult(Collections.singletonList((ProductVariant) product));
        result.setTotalCount(1);
        result.setPageSize(1);
        result.setPageNumber(1);
      } else {
        List<ProductVariant> skus = product.getVariants();
        result.setSearchResult(skus);
        result.setTotalCount(skus.size());
        result.setPageSize(skus.size());
        result.setPageNumber(1);
      }
    } else {
      result = searchProductsPaginated(searchTerm, searchParams, storeContext, ProductVariant.class);
    }
    return result;
  }

  @Nonnull
  @Override
  public List<Catalog> getCatalogs(@Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getCatalog(@Nonnull CatalogId catalogId, @Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getCatalog(@Nonnull CatalogAlias alias, @Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getDefaultCatalog(@Nonnull StoreContext storeContext) {
    // to be implemented with CMS-9516 (multi catalog support for hybris)
    return Optional.empty();
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  private <T> SearchResult<T> searchProductsPaginated(@Nonnull String searchTerm,
                                                      @Nonnull Map<String, String> searchParams,
                                                      @Nonnull StoreContext storeContext,
                                                      @Nonnull Class<T> returnType) {
    Set<T> resultSet = new LinkedHashSet<>();
    Map<String, String> params = new HashMap<>(searchParams);

    int pageCount = params.get(CatalogService.SEARCH_PARAM_PAGENUMBER) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_PAGENUMBER)) : 0;
    int pageSize = params.get(CatalogService.SEARCH_PARAM_PAGESIZE) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_PAGESIZE)) : maxProductSearchResults;
    int pages = Integer.MAX_VALUE;

    int maxItems = params.get(CatalogService.SEARCH_PARAM_TOTAL) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_TOTAL)) : maxProductSearchResults;

    // the given offset in parameters is actually the startPosition (startPostion is one more than offset)
    int startPosition = params.get(CatalogService.SEARCH_PARAM_OFFSET) != null ?
            Integer.parseInt(params.get(CatalogService.SEARCH_PARAM_OFFSET)) : 1;
    int offset = startPosition > 1 ? startPosition - 1 : 0;

    ProductSearchDocument productSearchDocument = null;
    while (pageCount < pages && resultSet.size() < maxItems) {
      params.put(CatalogService.SEARCH_PARAM_PAGENUMBER, Integer.toString(pageCount));
      params.put(CatalogService.SEARCH_PARAM_PAGESIZE, Integer.toString(pageSize));

      productSearchDocument = catalogResource.searchProducts(searchTerm, params, storeContext);
      if (productSearchDocument == null) {
        break;
      }

      List<ProductRefDocument> productRefDocuments = productSearchDocument.getProducts();
      if (productRefDocuments == null) {
        break;
      }

      for (ProductRefDocument productRefDocument : productRefDocuments) {
        if (productRefDocument == null) {
          continue;
        }

        if (resultSet.size() >= maxItems + offset) {
          break;
        }

        Product product = resolveProductRef(productRefDocument, storeContext).orElse(null);
        if (product != null) {
          if (returnType == ProductVariant.class) {
            if (product.isVariant()) {
              resultSet.add((T) product);
            }
          } else {
            convertToProduct(product)
                    .ifPresent(productConverted -> resultSet.add((T) productConverted));
          }
        }
      }

      pageCount = productSearchDocument.getPagination().getCurrentPage() + 1;
      pages = productSearchDocument.getPagination().getTotalPages();
    }

    List<T> totalList = new ArrayList<>(resultSet);
    List<T> subList = totalList.subList(offset, totalList.size());

    SearchResult<T> result = new SearchResult<>();
    result.setSearchResult(subList);
    if (productSearchDocument != null) {
      // we cannot take the values in the productSearchDocument.pagination structure
      // because it may take multiple calls to get the desired amount of products
      result.setTotalCount(subList.size());
      result.setPageSize(Math.max(productSearchDocument.getPagination().getPageSize(), subList.size()));
      result.setPageNumber(1);
      result.setFacets(productSearchDocument.getFacets());
    }
    return result;
  }

  /**
   * Resolve a list of products and variant refs and turn it into a list of products and variants.
   */
  @Nonnull
  private List<Product> resolveProductRefs(@Nonnull List<ProductRefDocument> productRefDocuments,
                                           @Nonnull StoreContext context) {
    return productRefDocuments.stream()
            .filter(Objects::nonNull)
            .map(productRefDocument -> resolveProductRef(productRefDocument, context))
            .flatMap(Streams::stream)
            .collect(Collectors.toList());
  }

  /**
   * Resolve a product or variant ref and turn it into a product or variant bean.
   */
  @Nonnull
  private Optional<Product> resolveProductRef(@Nonnull ProductRefDocument productRefDocument,
                                              @Nonnull StoreContext context) {
    String externalId = productRefDocument.getCode();
    CommerceId commerceId = commerceId(PRODUCT).withExternalId(externalId).build();

    Product product = findProductById(commerceId, context); // Expensive but necessary to filter out product variants.

    if (product == null) {
      LOG.warn("Cannot find product '{}'.", externalId);
      return Optional.empty();
    }

    return Optional.of(product);
  }

  /**
   * Filters product refs in a list of product and variant refs.
   */
  @Nonnull
  private static List<ProductRefDocument> filterProductRefs(@Nonnull List<ProductRefDocument> productRefs) {
    Set<ProductRefDocument> uniqueProductRefsWithoutType = productRefs.stream()
            .filter(productRef -> productRef.getType() == null)
            // Use `LinkedHashSet` to keep order of products. Order might be irrelevant, though.
            .collect(toCollection(LinkedHashSet::new));

    return new ArrayList<>(uniqueProductRefsWithoutType);
  }

  /**
   * Filters products from a list with variants and products.
   * Variants will be additionally converted to products.
   */
  @Nonnull
  private static List<Product> convertToProducts(@Nonnull List<Product> products) {
    Set<Product> result = products.stream()
            .map(CatalogServiceImpl::convertToProduct)
            .flatMap(Streams::stream)
            // Use `LinkedHashSet` to keep order of products.
            .collect(toCollection(LinkedHashSet::new));

    return ImmutableList.copyOf(result);
  }

  @Nonnull
  private static Optional<Product> convertToProduct(@Nonnull Product product) {
    if (!product.isVariant()) {
      return Optional.of(product);
    }

    if (product instanceof ProductVariant) {
      Product parent = ((ProductVariant) product).getParent();
      while (parent instanceof ProductVariant && ((ProductVariant) parent).getParent() != null) {
        parent = ((ProductVariant) parent).getParent();
      }
      if (parent != null) {
        return Optional.of(parent);
      }
    }

    return Optional.empty();
  }

  @Nonnull
  @Override
  public Category findRootCategory(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    CommerceId commerceId = commerceId(CATEGORY)
            .withExternalId(CategoryImpl.ROOT_CATEGORY_ROLE_ID)
            .withCatalogAlias(catalogAlias)
            .build();

    Category rootCategoryBean = (Category) getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
    if (rootCategoryBean == null) {
      throw new NotFoundException("Cannot create root category for id " + commerceId);
    }

    return rootCategoryBean;
  }

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CatalogService.class);
  }

  @Required
  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }

  public void setMaxProductSearchResults(int maxProductSearchResults) {
    this.maxProductSearchResults = maxProductSearchResults;
  }
}
