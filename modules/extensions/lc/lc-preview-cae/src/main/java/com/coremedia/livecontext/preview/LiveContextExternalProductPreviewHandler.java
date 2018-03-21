package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;

@Link
@RequestMapping
public class LiveContextExternalProductPreviewHandler extends LiveContextPageHandlerBase {

  @SuppressWarnings("unused")
  @Link(type = LiveContextExternalProduct.class)
  public Object buildLinkForExternalProduct(LiveContextExternalProduct externalProduct, String viewName,
                                            Map<String, Object> linkParameters, HttpServletRequest request) {
    Optional<StoreContext> storeContext = CurrentCommerceConnection.find().map(CommerceConnection::getStoreContext);
    if (!storeContext.isPresent()) {
      // not responsible
      return null;
    }

    Product product;
    try {
      product = externalProduct.getProduct();
    } catch (NotFoundException | InvalidIdException e) {
      LOG.info("could not find product in catalog for id {}", externalProduct.getExternalId(), e);
      return null;
    }

    if (product == null || !useCommerceProductLinks(externalProduct.getSite())) {
      // not responsible
      return null;
    }

    return buildCommerceLinkFor(product, linkParameters, request);
  }

  private boolean useCommerceProductLinks(Site site) {
    return getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS, Boolean.class, true, site);
  }
}
