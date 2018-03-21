package com.coremedia.livecontext.hybris.links;

import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.navigation.LiveContextCategoryNavigation;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;

import javax.servlet.http.HttpServletRequest;

import static com.coremedia.livecontext.hybris.links.HybrisPreviewLinkScheme.isHybris;

/**
 * Links in Fragments are always build on the hybris system. The LinkData is prepared by com.coremedia.livecontext.fragment.links.transformers.LiveContextLinkTransformer.
 * But it seems that there always has to be a matching linkscheme before LiveContextLinkTransformer
 * is called.
 */
@Link
public class HybrisLiveLinkSchemes {
  @Link(type = {LiveContextExternalChannel.class, LiveContextCategoryNavigation.class, LiveContextExternalProduct.class, CommerceBean.class, Category.class, CategoryInSite.class, Product.class, ProductInSite.class, CMProductTeaser.class, CMExternalPage.class}, order = 3)
  public Object dummyLiveLinkForHybris(
          final Object object,
          final HttpServletRequest request) {

    if (!isApplicable(request)) {
      return null;
    }

    return "hybris";
  }

  @Link(type = {LiveContextExternalChannel.class, LiveContextCategoryNavigation.class, LiveContextExternalProduct.class, CommerceBean.class, Category.class, CategoryInSite.class, Product.class, ProductInSite.class, CMProductTeaser.class, CMExternalPage.class}, view = HandlerHelper.VIEWNAME_DEFAULT, order = 3)
  public Object dummyLiveLinkForHybrisWithDefaultView(
          final Object object,
          final HttpServletRequest request) {

    if (!isApplicable(request)) {
      return null;
    }

    return "hybris";
  }

  static boolean isFragmentRequest(HttpServletRequest request) {
    return FragmentContextProvider.getFragmentContext(request).isFragmentRequest();
  }

  static boolean isApplicable(HttpServletRequest request) {
    return isHybris() && isFragmentRequest(request);
  }

}
