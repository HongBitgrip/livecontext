package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import com.coremedia.objectserver.web.taglib.MetadataTagSupport;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * A Facade for LiveContext utility functions used by FreeMarker templates.
 */
public class LiveContextFreemarkerFacade extends MetadataTagSupport {
  private static final long serialVersionUID = 577878275971542409L;

  private static final String CATALOG_ID = "catalogId";
  private static final String LANG_ID = "langId";
  private static final String SITE_ID = "siteId";
  private static final String PAGE_ID = "pageId";
  private static final String STORE_ID = "storeId";
  private static final String STORE_REF = "storeRef";

  private transient LiveContextNavigationFactory liveContextNavigationFactory;
  private String secureScheme;

  private AugmentationService categoryAugmentationService;
  private AugmentationService productAugmentationService;

  private SitesService sitesService;

  public AugmentationService getCategoryAugmentationService() {
    return categoryAugmentationService;
  }

  public void setCategoryAugmentationService(AugmentationService augmentationService) {
    this.categoryAugmentationService = augmentationService;
  }

  public AugmentationService getProductAugmentationService() {
    return productAugmentationService;
  }

  public void setProductAugmentationService(AugmentationService productAugmentationService) {
    this.productAugmentationService = productAugmentationService;
  }

  public SitesService getSitesService() {
    return sitesService;
  }

  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public String formatPrice(Object amount, Currency currency, Locale locale) {
    return FormatFunctions.formatPrice(amount, currency, locale);
  }

  public ProductInSite createProductInSite(Product product) {
    CommerceConnection connection = getCommerceConnection();
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");
    return liveContextNavigationFactory.createProductInSite(product, storeContext.getSiteId());
  }

  public String getSecureScheme() {
    return secureScheme;
  }

  /**
   * This method returns a {@link Map} which contains information for the preview.<br>
   * The map contains the following keys: {@link #CATALOG_ID}, {@link #LANG_ID}, {@link #SITE_ID} and {@link #STORE_ID}.<br>
   *
   * @return a map containing informations for preview of fragments
   * @throws IOException
   */
  @Nonnull
  public Map<String, Object> getPreviewMetadata() throws IOException {
    if (!isMetadataEnabled()) {
      return Collections.emptyMap();
    }

    if (fragmentContext() == null || !fragmentContext().isFragmentRequest()) {
      return Collections.emptyMap();
    }

    FragmentParameters parameters = fragmentContext().getParameters();
    CommerceConnection connection = getCommerceConnection();
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");

    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    builder.put(CATALOG_ID, parameters.getCatalogId().orElse(storeContext.getCatalogId()))
            .put(LANG_ID, "" + storeContext.getLocale())
            .put(SITE_ID, storeContext.getSiteId())
            .put(STORE_ID, parameters.getStoreId());

    boolean isAugmentedPage = isAugmentedPage(parameters);

    if (isAugmentedPage) {
      builder.put(PAGE_ID, parameters.getPageId())
              .put(STORE_REF, storeContext);
    }

    return builder.build();
  }

  /**
   * Checks if the current fragment request targets an Augmented Page (NO Category Page, NO Product Page)
   * @param parameters fragment parameters
   * @return true if request targets an Augmented Page
   */
  private boolean isAugmentedPage(FragmentParameters parameters) {
    return isEmpty(parameters.getCategoryId()) && isEmpty(parameters.getProductId());
  }

  public boolean isAugmentedContent() {
    CommerceConnection connection = getCommerceConnection();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");
    FragmentParameters parameters = fragmentContext().getParameters();
    boolean isAugmentedPage = isAugmentedPage(parameters);

    if (isAugmentedPage) {
      return true;
    }

    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "catalog service not available");

    String categoryId = parameters.getCategoryId();
    String productId = parameters.getProductId();
    Content content = null;
    CommerceBean commerceBean = null;
    CatalogAlias catalogAlias = storeContext.getCatalogAlias();
    if (!isEmpty(productId)) {
      CommerceId productTechId = idProvider.formatProductTechId(catalogAlias, productId);
      commerceBean = catalogService.findProductById(productTechId, storeContext);
      if (commerceBean instanceof ProductVariant) {
        // variants are not augmented, we need to check its parent
        Product parent = ((ProductVariant) commerceBean).getParent();
        commerceBean = parent != null ? parent : commerceBean;
      }
      content = productAugmentationService.getContent(commerceBean);
    } else if (!isEmpty(categoryId)) {
      CommerceId categoryTechId = idProvider.formatCategoryTechId(catalogAlias, categoryId);
      commerceBean = catalogService.findCategoryById(categoryTechId, storeContext);
      content = categoryAugmentationService.getContent(commerceBean);
    }
    return content != null;
  }

  public FragmentContext fragmentContext() {
    return FragmentContextProvider.getFragmentContext(FreemarkerUtils.getCurrentRequest());
  }

  @Override //Overriden for mocking in test.
  protected boolean isMetadataEnabled() {
    return super.isMetadataEnabled();
  }

  @Required
  public void setLiveContextNavigationFactory(@Nonnull LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public void setSecureScheme(String secureScheme) {
    this.secureScheme = secureScheme;
  }

  public String getVendorName() {
    return CurrentCommerceConnection.find().map(CommerceConnection::getVendorName).orElse(null);
  }

  @Nonnull
  private static CommerceConnection getCommerceConnection() {
    return CurrentCommerceConnection.get();
  }
}
