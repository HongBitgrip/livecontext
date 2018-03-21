package com.coremedia.livecontext.hybris.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProductImpl;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.HybrisCommerceConnection;
import com.coremedia.livecontext.ecommerce.hybris.preview.PreviewTokenService;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper.getCurrentContext;
import static com.coremedia.livecontext.handler.LiveContextPageHandlerBase.P13N_URI_PARAMETER;

/**
 * This class is an example to show how to write a annotation based linkscheme that is in front of
 * all existing linkschemes. It has the advantage that we can define the target bean type via annotations.
 * <p>
 * The alternative way is a classic link scheme that implements #formatLink(). But there we would have
 * a longer path of if-then-else statements. But maybe its not a bad idea...
 * <p>
 * Because here is a proposal to solve the "URLProvider per site" problem:
 * <p>
 * - make a classic ExternalLinkScheme (in a module like lc-ecommerce-ext that gets a bean as object from the
 * LinkFormatter and it passes the call to a URLProvider that can obtained from the connection
 * <p>
 * - for this the BaseCommerceConnection class must be extended to provide a URLProvider (with a new Interface
 * CommerceConnectionExt
 * <p>
 * - in the linkscheme we can test and cast against CommerceConnectionExt, if not it is obviously an IBM connection
 * <p>
 * - the linkscheme can then call urlProvider.provideUrl(bean)
 * <p>
 * - the code that dispatches between the different target types is then within the URLProvider (and he returns
 * null if he is not responsible so all other linkschemes can run too)
 */
@Link
public class HybrisPreviewLinkScheme {

  private String previewStoreFrontUrl;
  private String hybrisPreviewServiceUrl;
  private CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper;

  private PreviewTokenService previewTokenService;

  @Link(type = {LiveContextExternalChannel.class, LiveContextExternalChannelImpl.class}, order = 2)
  public Object buildPreviewLinkForAugmentedCategory(LiveContextExternalChannel navigation, String viewName,
                                                     Map<String, Object> linkParameters, HttpServletRequest request,
                                                     HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    Category category = navigation.getCategory();
    if (category == null) {
      return null;
    }

    return buildPreviewLinkForCategory(category, viewName, linkParameters, request, response);
  }

  @Link(type = Category.class, order = 2)
  public Object buildPreviewLinkForCategory(Category category, String viewName, Map<String, Object> linkParameters,
                                            HttpServletRequest request, HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    if (category == null) {
      return null;
    }

    String previewTicketId = previewTokenService.getPreviewTicketId();
    if (previewTicketId == null) {
      return null;
    }

    return buildLinkInternal(category.getExternalId(), BaseCommerceBeanType.CATEGORY.type(), previewTicketId);
  }

  @Link(type = CategoryInSite.class, order = 2)
  public Object buildPreviewLinkForCategoryInSite(CategoryInSite categoryInSite, String viewName,
                                                  Map<String, Object> linkParameters, HttpServletRequest request,
                                                  HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    Category category = categoryInSite.getCategory();
    if (category == null) {
      return null;
    }

    return buildPreviewLinkForCategory(category, viewName, linkParameters, request, response);
  }

  @Link(type = CMExternalPage.class, order = 2)
  public Object buildPreviewLinkForExternalPage(CMExternalPage externalPage, String viewName,
                                                Map<String, Object> linkParameters, HttpServletRequest request,
                                                HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    String previewTicketId = previewTokenService.getPreviewTicketId();
    if (externalPage == null || previewTicketId == null) {
      return null;
    }

    return buildLinkInternal(externalPage.getExternalId(), "externalpage", previewTicketId);
  }

  @Link(type = Product.class, order = 2)
  public Object buildPreviewLinkForProduct(Product product, String viewName, Map<String, Object> linkParameters,
                                           HttpServletRequest request, HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    String previewTicketId = previewTokenService.getPreviewTicketId();
    if (product == null || previewTicketId == null) {
      return null;
    }

    return buildLinkInternal(product.getExternalId(), BaseCommerceBeanType.PRODUCT.type(), previewTicketId);
  }

  @Link(type = {LiveContextExternalProduct.class, LiveContextExternalProductImpl.class}, order = 2)
  public Object buildPreviewLinkForAugmentedProduct(LiveContextExternalProduct augmentedProduct, String viewName,
                                                    Map<String, Object> linkParameters, HttpServletRequest request,
                                                    HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    Product product = augmentedProduct.getProduct();
    if (product == null) {
      return null;
    }

    return buildPreviewLinkForProduct(product, viewName, linkParameters, request, response);
  }

  @Link(type = ProductInSite.class, order = 2)
  public Object buildPreviewLinkForProductInSite(ProductInSite productInSite, String viewName,
                                                 Map<String, Object> linkParameters, HttpServletRequest request,
                                                 HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    return buildPreviewLinkForProduct(productInSite.getProduct(), viewName, linkParameters, request, response);
  }

  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT, order = 2)
  public Object buildLinkForProductTeaser(CMProductTeaser productTeaser, String viewName,
                                          Map<String, Object> linkParameters, HttpServletRequest request,
                                          HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    ProductInSite productInSite = productTeaser.getProductInSite();
    if (productInSite == null) {
      return null;
    }

    return buildPreviewLinkForProductInSite(productInSite, viewName, linkParameters, request, response);
  }

  @Link(type = CMChannel.class, order = 2)
  @Nullable
  public Object buildPreviewLinkForMicrosite(@Nonnull CMChannel channel, @Nullable String viewName,
                                             @Nonnull Map<String, Object> linkParameters, HttpServletRequest request,
                                             HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    if (!commerceLedLinkBuilderHelper.isCommerceLedChannel(channel)) {
      return null;
    }

    // Display all channels in the shop preview
    String previewTicketId = previewTokenService.getPreviewTicketId();
    String seoSegmentForChannel = commerceLedLinkBuilderHelper.getSeoSegmentForChannel(channel);

    return buildLinkInternal(seoSegmentForChannel, "content", previewTicketId);
  }

  static boolean isHybris() {
    return CurrentCommerceConnection.find()
            .filter(HybrisCommerceConnection.class::isInstance)
            .isPresent();
  }

  private static boolean isStudioPreviewRequest(@Nonnull HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request) || "true".equals(request.getParameter(P13N_URI_PARAMETER));
  }

  private static boolean isApplicable(@Nonnull HttpServletRequest request) {
    return isHybris() && isStudioPreviewRequest(request);
  }

  private String buildLinkInternal(String id, String type, String previewTicketId) {
    String adjustedHybrisPreviewServiceUrl = previewStoreFrontUrl.endsWith("/") && hybrisPreviewServiceUrl.startsWith("/")
            ? hybrisPreviewServiceUrl.substring(1)
            : hybrisPreviewServiceUrl;

    return previewStoreFrontUrl
            + replaceTokens(adjustedHybrisPreviewServiceUrl, id, type)
            + "&ticketId=" + previewTicketId
            + addUserSegmentParamIfAvailable();
  }

  private String replaceTokens(String pattern, String id, String type) {
    String result = pattern.replace("{storeId}", getCurrentStoreContext().getStoreId() + "");
    result = result.replace("{siteId}", getCurrentStoreContext().getSiteId() + "");
    //TODO fetch catalogId from id parameter for multi catalog support
    result = result.replace("{catalogId}", getCurrentStoreContext().getCatalogId() + "");
    result = result.replace("{language}", getCurrentStoreContext().getLocale().getLanguage() + "");
    result = result.replace("{id}", id + "");
    result = result.replace("{type}", type + "");
    return result;
  }

  private String addUserSegmentParamIfAvailable() {
    String userSegments = getCurrentContext().getUserSegments();

    if (StringUtils.isEmpty(userSegments)) {
      return "";
    }

    return "&userGroup=" + userSegments;
  }

  private StoreContext getCurrentStoreContext() {
    return CurrentCommerceConnection.get().getStoreContext();
  }

  @Required
  public void setPreviewStoreFrontUrl(String previewStoreFrontUrl) {
    this.previewStoreFrontUrl = previewStoreFrontUrl;
  }

  @Required
  public void setHybrisPreviewServiceUrl(String hybrisPreviewServiceUrl) {
    this.hybrisPreviewServiceUrl = hybrisPreviewServiceUrl;
  }

  @Required
  public void setPreviewTokenService(PreviewTokenService previewTokenService) {
    this.previewTokenService = previewTokenService;
  }

  @Required
  public void setCommerceLedLinkBuilderHelper(CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper) {
    this.commerceLedLinkBuilderHelper = commerceLedLinkBuilderHelper;
  }
}
