package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.user.User;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.contentbeans.ProductDetailPage;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.UserVariantHelper;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

/**
 * A fragment handler that handles all fragment requests that within the context of a given product. It will fall back
 * to the sites root channel if no context could be found for the given category.
 * The parameter productId can be
 * {@link com.coremedia.livecontext.ecommerce.catalog.Category#getExternalTechId() external technical id} or
 * {@link com.coremedia.livecontext.ecommerce.catalog.Category#getExternalId()} () external id}.
 * Also see {@link ProductFragmentHandler#setUseStableIds}.
 */
public class ProductFragmentHandler extends FragmentHandler {

  private static final String AS_ASSETS_VIEW = "asAssets";
  private static final String ORIENTATION_PARAM_NAME = "orientation";
  private static final String TYPES_PARAM_NAME = "types";
  private static final String PDP_PAGE_ID = "pdpPage";

  private ResolveContextStrategy contextStrategy;
  private boolean useStableIds = false;
  private boolean useContentPagegrid = false;
  private AugmentationService productAugmentationService;

  /**
   * Renders the complete context (which is a CMChannel) of the given <code>product</code> using the given <code>view</code>.
   * If no context can be found for the product, the <code>view</code> of the root channel will be rendered. The site
   * is determined by the tuple <code>(storeId, locale)</code>, which must be unique across all sites.
   *
   * @return the {@link ModelAndView model and view} containing the {@link com.coremedia.blueprint.common.contentbeans.Page page}
   * as <code>self</code> object, that contains the context (CMChannel) that shall be rendered.
   */
  @Nullable
  @Override
  public ModelAndView createModelAndView(@Nonnull FragmentParameters params, @Nonnull HttpServletRequest request) {
    Site site = SiteHelper.getSiteFromRequest(request);

    if (site == null) {
      throw buildExceptionForMissingNavigation(params);
    }

    String externalTechId = params.getProductId();
    String view = params.getView();
    String placement = params.getPlacement();
    String categoryTechId = params.getCategoryId();

    LiveContextNavigation navigation = getLiveContextNavigation(params, site, externalTechId, categoryTechId);

    Content rootChannelContent = site.getSiteRootDocument();
    CMChannel rootChannel = getContentBeanFactory().createBeanFor(rootChannelContent, CMChannel.class);
    User developer = UserVariantHelper.getUser(request);

    if (!isNullOrEmpty(placement)) {
      return createFragmentModelAndViewForPlacementAndView(navigation, externalTechId, placement, view, rootChannel,
              developer);
    }

    if (view != null && view.equals(AS_ASSETS_VIEW)) {
      String parameter = params.getParameter();

      String orientation = extractParameterValue(parameter, ORIENTATION_PARAM_NAME);
      String types = extractParameterValue(parameter, TYPES_PARAM_NAME);

      return createModelAndViewForProductPage(navigation, externalTechId, view, orientation, types, developer);
    }

    return createModelAndViewForProductPage(navigation, externalTechId, view, null, null, developer);
  }

  private LiveContextNavigation getLiveContextNavigation(@Nonnull FragmentParameters params, Site site, String externalTechId, String categoryTechId) {
    CommerceConnection currentConnection = CurrentCommerceConnection.get();
    StoreContext storeContext = currentConnection.getStoreContext();

    CatalogService catalogService = requireNonNull(currentConnection.getCatalogService(),
            "No catalog service configured for commerce connection '" + currentConnection + "'.");
    CommerceIdProvider idProvider = requireNonNull(currentConnection.getIdProvider(),
            "No commerce id provider configured for commerce connection '" + currentConnection + "'.");

    CommerceBean commerceBean;
    CatalogAlias catalogAlias = storeContext.getCatalogAlias();
    if (StringUtils.isNotEmpty(categoryTechId)) {
      commerceBean = catalogService.findCategoryById(idProvider.formatCategoryTechId(catalogAlias, categoryTechId), storeContext);
    } else {
      commerceBean = catalogService.findProductById(idProvider.formatProductTechId(catalogAlias, externalTechId), storeContext);
    }

    if (commerceBean == null) {
      throw buildExceptionForMissingNavigation(params);
    }

    //noinspection ConstantConditions
    LiveContextNavigation navigation = contextStrategy.resolveContext(site, commerceBean);

    if (navigation == null) {
      throw buildExceptionForMissingNavigation(params);
    }
    return navigation;
  }

  private static IllegalStateException buildExceptionForMissingNavigation(@Nonnull FragmentParameters params) {
    String storeId = params.getStoreId();
    Locale locale = params.getLocale();
    String categoryId = params.getCategoryId();

    return new IllegalStateException(String.format(
            "ProductFragmentHandler did not find a navigation for store ID \"%s\", locale \"%s\", category ID \"%s\".",
            storeId, locale, categoryId));
  }

  @Nonnull
  protected ModelAndView createFragmentModelAndViewForPlacementAndView(@Nonnull Navigation navigation,
                                                                       @Nonnull String productId,
                                                                       @Nonnull String placement,
                                                                       @Nullable String view,
                                                                       @Nonnull CMChannel rootChannel,
                                                                       @Nullable User developer) {
    Product product = getProductFromId(productId);
    Content externalProductContent = getAugmentedProductContent(product);
    if (externalProductContent == null) {
      return createFragmentModelAndViewForPlacementAndView(navigation, placement, view, rootChannel, developer);
    }

    LiveContextExternalProduct externalProduct = getContentBeanFactory().createBeanFor(externalProductContent,
            LiveContextExternalProduct.class);

    PageGridPlacement pageGridPlacement = pageGridPlacementResolver.resolvePageGridPlacement(externalProduct, placement);
    if (pageGridPlacement == null) {
      return createPlacementUnresolvableError(externalProduct, placement);
    }

    Page page = asPage(navigation, externalProduct, developer);
    ModelAndView resultMV = HandlerHelper.createModelWithView(pageGridPlacement, view);
    RequestAttributeConstants.setPage(resultMV, page);
    NavigationLinkSupport.setNavigation(resultMV, navigation);

    return resultMV;
  }

  @Nullable
  private Content getAugmentedProductContent(@Nullable Product product) {
    if (product == null) {
      return null;
    }

    Content externalProductContent = productAugmentationService.getContent(product);
    if (externalProductContent != null) {
      return externalProductContent;
    }

    if (!product.isVariant()) {
      return null;
    }

    Product parentProduct = ((ProductVariant) product).getParent();

    while (parentProduct != null && parentProduct.isVariant()) {
      parentProduct = ((ProductVariant) parentProduct).getParent();
    }

    if (parentProduct == null) {
      return null;
    }

    return productAugmentationService.getContent(parentProduct);
  }

  @Nonnull
  protected ModelAndView createModelAndViewForProductPage(Navigation navigation, String productId, String view,
                                                          String orientation, String types, @Nullable User developer) {
    Product product = getProductFromId(productId);
    Content augmentedProductContent = getAugmentedProductContent(product);

    ModelAndView modelAndView = HandlerHelper.createModelWithView(product, view);
    if (!isNullOrEmpty(orientation)) {
      modelAndView.addObject("orientation", orientation);
    }
    if (!isNullOrEmpty(types)) {
      modelAndView.addObject("types", types);
    }

    Linkable linkable = augmentedProductContent != null ? (Linkable) getContentBeanFactory().createBeanFor(augmentedProductContent) : navigation;
    Page page = createProductDetailPage(linkable, navigation, developer);
    addPageModel(modelAndView, page);

    return modelAndView;
  }

  @Nullable
  private Product getProductFromId(@Nullable String productId) {
    if (isNullOrEmpty(productId)) {
      return null;
    }

    CommerceConnection connection = CurrentCommerceConnection.get();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");

    CatalogAlias catalogAlias = storeContext.getCatalogAlias();

    CommerceId commerceId = useStableIds
            ? idProvider.formatProductId(catalogAlias, productId)
            : idProvider.formatProductTechId(catalogAlias, productId);

    CommerceBeanFactory commerceBeanFactory = connection.getCommerceBeanFactory();
    Product product = (Product) commerceBeanFactory.createBeanFor(commerceId, storeContext);

    if (product.isVariant()) {
      commerceId = useStableIds
              ? idProvider.formatProductVariantId(catalogAlias, productId)
              : idProvider.formatProductVariantTechId(catalogAlias, productId);

      return (ProductVariant) commerceBeanFactory.createBeanFor(commerceId, storeContext);
    }

    return product;
  }

  @Override
  protected PageImpl createPageImpl(Object content, Navigation context, @Nullable User developer) {
    return useContentPagegrid
            ? super.createPageImpl(content, context, developer)
            : createProductDetailPage(content, context, developer);
  }

  private ProductDetailPage createProductDetailPage(Object content, Navigation context, User developer) {
    ProductDetailPage page = getBeanFactory().getBean(PDP_PAGE_ID, ProductDetailPage.class);
    page.setContent(content);
    page.setNavigation(context);
    page.setDeveloper(developer);
    return page;
  }

  @Nullable
  private static String extractParameterValue(@Nullable String parameters, @Nonnull String parameterName) {
    if (isNullOrEmpty(parameters)) {
      return parameters;
    }

    Iterable<String> keyValueStrs = Splitter.on(',').split(parameters);
    for (String keyValueStr : keyValueStrs) {
      List<String> keyValue = Splitter.on('=').splitToList(keyValueStr);

      if (keyValue.size() == 2 && parameterName.equals(keyValue.get(0))) {
        return keyValue.get(1);
      }
    }

    return null;
  }

  @Override
  public boolean include(@Nonnull FragmentParameters params) {
    return !isNullOrEmpty(params.getProductId())
            && (isNullOrEmpty(params.getExternalRef()) || !params.getExternalRef().startsWith("cm-"));
  }

  // ------------ Config --------------------------------------------

  @Required
  public void setContextStrategy(ResolveContextStrategy contextStrategy) {
    this.contextStrategy = contextStrategy;
  }

  public void setUseStableIds(boolean useStableIds) {
    this.useStableIds = useStableIds;
  }

  /**
   * Determines whether to use the page grid for content pages or for
   * product detail pages.
   * <p>
   * Default is false, which means the pdp pagegrid is used.
   */
  public void setUseContentPagegrid(boolean useContentPagegrid) {
    this.useContentPagegrid = useContentPagegrid;
  }

  @Autowired
  @Qualifier("productAugmentationService")
  public void setProductAugmentationService(AugmentationService augmentationService) {
    this.productAugmentationService = augmentationService;
  }
}
