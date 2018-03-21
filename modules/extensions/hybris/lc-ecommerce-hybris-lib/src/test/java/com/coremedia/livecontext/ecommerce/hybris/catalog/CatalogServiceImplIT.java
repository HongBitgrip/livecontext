package com.coremedia.livecontext.ecommerce.hybris.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.lc.test.CatalogServiceBaseTest;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class CatalogServiceImplIT extends CatalogServiceBaseTest {

  @Inject
  private CatalogServiceImpl testling;

  @Betamax(tape = "hy_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductById() throws Exception {
    super.testFindProductById();
  }

  @Betamax(tape = "hy_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryById() throws Exception {
    super.testFindCategoryById();
  }

  @Betamax(tape = "hy_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByIdNotFound() throws Exception {
    super.testFindProductByIdNotFound();
  }

  @Betamax(tape = "hy_testFindProductByIdWithSlash", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore
  public void testFindProductByIdWithSlash() {
    super.testFindProductByIdWithSlash();
  }

  @Betamax(tape = "hy_testFindProductBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("seo segments not supported")
  public void testFindProductBySeoSegment() throws Exception {
    super.testFindProductBySeoSegment();
  }

  @Betamax(tape = "hy_testFindProductBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("seo segments not supported")
  public void testFindProductBySeoSegmentIsNull() throws Exception {
    super.testFindProductBySeoSegmentIsNull();
  }

  @Betamax(tape = "hy_testFindProductVariantById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantById() throws Exception {
    super.testFindProductVariantById();
  }

  @Betamax(tape = "hy_testFindProductVariantsByProductId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindProductVariantsByProductId() {
    CommerceId productId = HybrisCommerceIdProvider.commerceId(PRODUCT).withExternalId(PRODUCT_CODE).build();
    Product product = testling.findProductById(productId, getStoreContext());
    assertThat(product).isNotNull();

    List<ProductVariant> variants = product.getVariants();
    assertThat(variants).isNotEmpty();
  }

  @Betamax(tape = "hy_testProductAxisFilters", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductAxisFilters() {
    CommerceId productId = HybrisCommerceIdProvider.commerceId(PRODUCT).withExternalId(PRODUCT_CODE).build();
    Product product = testling.findProductById(productId, getStoreContext());
    assertThat(product).isNotNull();

    List<String> variantAxisNames = product.getVariantAxisNames();
    assertThat(variantAxisNames).isNotEmpty();
  }

  @Betamax(tape = "hy_testFindProductVariantByIdWithSlash", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore
  public void testFindProductVariantByIdWithSlash() throws Exception {
    super.testFindProductVariantByIdWithSlash();
  }

  @Betamax(tape = "hy_testFindProductsByCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategory() throws Exception {
    super.testFindProductsByCategory();
  }

  @Betamax(tape = "hy_testFindProductsByCategoryCheckProductsOnly", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindProductsByCategoryCheckProductsOnly() throws Exception {
    CommerceId productId = getIdProvider().formatProductId(null, PRODUCT_CODE);
    Product product = testling.findProductById(productId, getStoreContext());
    assertThat(product).isNotNull();

    Category category = product.getCategory();
    List<Product> productsByCategory = testling.findProductsByCategory(category);
    for (Product productInList : productsByCategory) {
      assertThat(productInList.isVariant()).isFalse();
    }
  }

  @Betamax(tape = "hy_testFindProductsByCategoryIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    super.testFindProductsByCategoryIsEmpty();
  }

  @Betamax(tape = "hy_testFindProductsByCategoryIsRoot", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsRoot() throws Exception {
    super.testFindProductsByCategoryIsRoot();
  }

  @Betamax(tape = "hy_testFindTopCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Betamax(tape = "hy_testFindRootCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindRootCategory() throws Exception {
    super.testFindRootCategory();
  }

  @Betamax(tape = "hy_testFindSubCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "hy_testFindSubCategoriesIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategoriesIsEmpty() throws Exception {
    super.testFindSubCategoriesIsEmpty();
  }

  @Betamax(tape = "hy_testFindCategoryByIdWithSlash", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore
  public void testFindCategoryByIdWithSlash() {
    super.testFindCategoryByIdWithSlash();
  }

  @Betamax(tape = "hy_testFindCategoryByIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByIdIsNull() {
    super.testFindCategoryByIdIsNull();
  }

  @Betamax(tape = "hy_testFindCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("seo not supported")
  public void testFindCategoryBySeoSegment() throws Exception {
    super.testFindCategoryBySeoSegment();
  }

  @Betamax(tape = "hy_testFindGermanCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("no seo support")
  public void testFindGermanCategoryBySeoSegment() throws Exception {
    super.testFindGermanCategoryBySeoSegment();
  }

  @Betamax(tape = "hy_testFindCategoryBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Test
  @Override
  public void testSearchProducts() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    //TODO: call super.testSearchProducts(), when hybris rest services are customized
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "3");
    SearchResult<Product> searchResult = testling.searchProducts(SEARCH_TERM_1, searchParams, getStoreContext());
    assertThat(searchResult).isNotNull();
    assertThat(searchResult.getSearchResult()).isNotEmpty();

    //search result paging
    //since hybris searches always for Products and ProductVariants
    //search result paging
    //TODO: fix in hybris rest implementation

    //search product below category
    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "3");
    SearchResult<Product> searchResultByCategory = testling.searchProducts(SEARCH_TERM_1, searchParams, getStoreContext());
    assertThat(searchResultByCategory).isNotNull();
    assertThat(searchResultByCategory.getSearchResult().size()).isGreaterThanOrEqualTo(3);
    assertThat(searchResultByCategory.getTotalCount()).isLessThanOrEqualTo(searchResult.getTotalCount());

    //search product with invalid param
    searchParams = new HashMap<>();
    searchParams.put("blub", "10");
    searchParams.put(CatalogService.SEARCH_PARAM_PAGESIZE, "10");
    searchParams.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "1");
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "3");
    SearchResult<Product> searchResultIgnoredParam = testling.searchProducts(SEARCH_TERM_1, searchParams, getStoreContext());
    assertThat(searchResultIgnoredParam).isNotNull();
    assertEquals("given page size has changed", searchResultIgnoredParam.getPageSize(), 10);
    assertThat(searchResultIgnoredParam.getTotalCount()).isEqualTo(searchResult.getTotalCount());

    //search product no hits
    SearchResult<Product> searchResultEmpty = testling.searchProducts("schnasndasn", emptyMap(), getStoreContext());
    assertThat(searchResultEmpty).isNotNull();
    assertThat(searchResultEmpty.getSearchResult()).isEmpty();

    //search product multiple words no hit
    //since hybris uses "OR" as default search operation for multiple terms
    //search result reduction is not working.
    //TODO: fix in hybris rest implementation
  }

  @Test
  public void testSortedSearchProductsByNameAsc() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_NAME_ASC");

    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "7");

    SearchResult<Product> searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    int size = searchProducts.getSearchResult().size();
    assertThat(size).isGreaterThan(0);
    List<Product> products = searchProducts.getSearchResult();
    int counter = 1;
    while (counter < size) {
      Product previousProduct = products.get(counter - 1);
      Product currentProduct = products.get(counter);
      String previousProductName = previousProduct.getName();
      String currentProductName = currentProduct.getName();
      assertThat(previousProductName.compareTo(currentProductName) < 0).isTrue();
      counter++;
    }
  }

  @Test
  public void testSortedSearchProductsByNameDesc() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_NAME_DSC");
    SearchResult<Product> searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    int size = searchProducts.getSearchResult().size();
    assertThat(size).isGreaterThan(0);
    List<Product> products = searchProducts.getSearchResult();
    int counter = 1;
    while (counter < size) {
      Product previousProduct = products.get(counter - 1);
      Product currentProduct = products.get(counter);
      String previousProductName = previousProduct.getName();
      String currentProductName = currentProduct.getName();
      assertThat(previousProductName.compareTo(currentProductName) > 0).isTrue();
      counter++;
    }
  }

  @Test
  public void testSortedSearchProductsByPriceAsc() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_PRICE_ASC");
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "7");
    SearchResult<Product> searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    int size = searchProducts.getSearchResult().size();
    assertThat(size).isGreaterThan(0);
    List<Product> products = searchProducts.getSearchResult();
    int counter = 1;
    while (counter < size) {
      Product previousProduct = products.get(counter - 1);
      Product currentProduct = products.get(counter);
      BigDecimal previousProductListPrice = previousProduct.getListPrice();
      BigDecimal currentProductListPrice = currentProduct.getListPrice();
      assertThat(previousProductListPrice).isLessThanOrEqualTo(currentProductListPrice);
      counter++;
    }
  }

  @Test
  public void testSortedSearchProductsByPriceDesc() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_PRICE_DSC");
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "7");
    SearchResult<Product> searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    int size = searchProducts.getSearchResult().size();
    assertThat(size).isGreaterThan(0);
    List<Product> products = searchProducts.getSearchResult();
    int counter = 1;
    while (counter < size) {
      Product previousProduct = products.get(counter - 1);
      Product currentProduct = products.get(counter);
      BigDecimal previousProductListPrice = previousProduct.getListPrice();
      BigDecimal currentProductListPrice = currentProduct.getListPrice();
      assertThat(previousProductListPrice).isGreaterThanOrEqualTo(currentProductListPrice);
      counter++;
    }
  }

  @Test
  public void testSearchProductsWithOffset() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    int start = 3;
    int total = 5;
    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    SearchResult<Product> searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    List<Product> originalProducts = searchProducts.getSearchResult();
    assertThat(originalProducts).isNotNull();
    Product firstProduct = originalProducts.get(start - 1);
    Product lastProduct = originalProducts.get(start + total - 2);

    searchParams.put(CatalogService.SEARCH_PARAM_OFFSET, Integer.toString(start));
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, Integer.toString(total));
    SearchResult<Product> searchProductsWithOffset = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProductsWithOffset).isNotNull();
    assertThat(searchProductsWithOffset.getTotalCount()).isEqualTo(total);
    List<Product> limitedProducts = searchProductsWithOffset.getSearchResult();
    assertThat(limitedProducts).isNotNull();
    assertThat(limitedProducts.size()).isEqualTo(total);
    assertThat(firstProduct.getName()).isEqualTo(limitedProducts.get(0).getName());
    assertThat(lastProduct.getName()).isEqualTo(limitedProducts.get(total - 1).getName());

    start = 5;
    total = 2;
    firstProduct = originalProducts.get(start - 1);
    lastProduct = originalProducts.get(start + total - 2);
    searchParams.put(CatalogService.SEARCH_PARAM_OFFSET, Integer.toString(start));
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, Integer.toString(total));
    searchProductsWithOffset = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProductsWithOffset).isNotNull();
    assertThat(searchProductsWithOffset.getTotalCount()).isEqualTo(total);
    limitedProducts = searchProductsWithOffset.getSearchResult();
    assertThat(limitedProducts).isNotNull();
    assertThat(limitedProducts.size()).isEqualTo(total);
    assertThat(firstProduct.getName()).isEqualTo(limitedProducts.get(0).getName());
    assertThat(lastProduct.getName()).isEqualTo(limitedProducts.get(total - 1).getName());
  }

  @Test
  public void testSearchProductsWithFacet() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId categoryId = getIdProvider().formatCategoryId(null, TOP_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, getStoreContext());
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_FACET, ":relevance:category:220000:price:£100-£199.99");
    SearchResult<Product> searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    assertThat(searchProducts.getTotalCount()).isEqualTo(4);
    List<Product> searchResult = searchProducts.getSearchResult();
    assertThat(searchResult).isNotNull();
    for (Product product : searchResult) {
      BigDecimal listPrice = product.getListPrice();
      assertThat(listPrice.floatValue()).isGreaterThanOrEqualTo(100);
      assertThat(listPrice.floatValue()).isLessThan(200);
    }

    searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_FACET, ":relevance:category:220000:brand:Burton");
    searchProducts = testling.searchProducts("*", searchParams, getStoreContext());
    assertThat(searchProducts).isNotNull();
    assertThat(searchProducts.getTotalCount()).isEqualTo(7);
    searchResult = searchProducts.getSearchResult();
    assertThat(searchResult).isNotNull();
    assertThat(searchResult.size()).isEqualTo(7);
  }

  @Test
  public void testSearchFacetsProducts() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    HashMap<String,String> searchParams = new HashMap<>();
    searchParams.put("fields", "DEFAULT,facets");

    super.testSearchFacetsProducts(SEARCH_TERM_1, searchParams);
  }

  @Test
  public void testGetFacetSearchProducts() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testGetFacetSearchProducts();
  }

  @Test
  @Override
  public void testSearchProductVariants() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testSearchProductVariants();
  }

  @Betamax(tape = "hy_testWithStoreContext", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testWithStoreContext() {
    super.testWithStoreContext();
  }

  @Betamax(tape = "hy_testWithStoreContextRethrowException", match = {MatchRule.path, MatchRule.query})
  @Test(expected = CommerceException.class)
  @Override
  public void testWithStoreContextRethrowException() {
    super.testWithStoreContextRethrowException();
  }
}
