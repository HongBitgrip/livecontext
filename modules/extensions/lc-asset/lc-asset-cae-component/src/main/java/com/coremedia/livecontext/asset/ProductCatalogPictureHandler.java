package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static java.util.Objects.requireNonNull;

@RequestMapping
public class ProductCatalogPictureHandler extends CatalogPictureHandlerBase {

  //TODO: mbi siehe unten
  private static final String SAP_HYBRIS_VENDOR_ID = "SAP Hybris";

  /**
   * URI Pattern for transformed blobs for products
   * e.g. /catalogimage/product/10202/en_US/full/PC_SHIRT.jpg
   */
  public static final String IMAGE_URI_PATTERN =
          "/" + AssetService.PRODUCT_URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  /**
   * e.g. /catalogimage/product/[storeId]/en_US/[catalogId]/full/PC_SHIRT.jpg
   */
  public static final String IMAGE_URI_PATTERN_FOR_CATALOG =
          "/" + AssetService.PRODUCT_URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + CATALOG_ID + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  @RequestMapping(value = IMAGE_URI_PATTERN_FOR_CATALOG)
  public ModelAndView handleRequestWidthHeightForProductWithCatalog(@PathVariable(STORE_ID) String storeId,
                                                                    @PathVariable(LOCALE) String locale,
                                                                    @PathVariable(FORMAT_NAME) String formatName,
                                                                    @PathVariable(PART_NUMBER) String partNumber,
                                                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                                                    @PathVariable(CATALOG_ID) String catalogId,
                                                                    WebRequest request,
                                                                    HttpServletResponse response) throws IOException {
    //the given partnumber can be of a product or of a sku but we need the correct reference id
    //so ask catalog service we will give us a sku instance if the partnumber belongs to a sku
    CommerceConnection connection = CurrentCommerceConnection.get();
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");
    CommerceIdProvider idProvider = connection.getIdProvider();

    CatalogAlias catalogAlias = resolveCatalogAliasFromId(catalogId, storeContext);
    CommerceId productId = idProvider.formatProductId(catalogAlias, partNumber);

    //Try to load product or product variant
    Optional<Product> productOrSkuOpt = loadProductOrVariant(catalogAlias, partNumber, storeContext);
    CommerceId lookupId = productOrSkuOpt
            .map(CommerceBean::getReference)
            .orElse(productId);

    ModelAndView modelAndView = handleRequestWidthHeight(storeId, locale, formatName, lookupId, extension, request);
    if (modelAndView == null) {
      // not modified
      return null;
    }

    if (HandlerHelper.isNotFound(modelAndView)
            && SAP_HYBRIS_VENDOR_ID.equals(connection.getVendorName())
            && productOrSkuOpt.isPresent()) {
      response.sendRedirect(productOrSkuOpt.get().getCatalogPicture().getUrl());
    }

    return modelAndView;
  }


  @RequestMapping(value = IMAGE_URI_PATTERN)
  public ModelAndView handleRequestWidthHeightForProduct(@PathVariable(STORE_ID) String storeId,
                                                         @PathVariable(LOCALE) String locale,
                                                         @PathVariable(FORMAT_NAME) String formatName,
                                                         @PathVariable(PART_NUMBER) String partNumber,
                                                         @PathVariable(SEGMENT_EXTENSION) String extension,
                                                         WebRequest request,
                                                         HttpServletResponse response) throws IOException {

    CommerceConnection connection = CurrentCommerceConnection.get();
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");

    return handleRequestWidthHeightForProductWithCatalog(storeId, locale, formatName, partNumber, extension,
            storeContext.getCatalogId(), request, response);
  }

  private Optional<Product> loadProductOrVariant(CatalogAlias catalogAlias, String partNumber, StoreContext storeContext){
    CommerceConnection connection = CurrentCommerceConnection.get();
    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "no catalog service available");
    CommerceIdProvider idProvider = connection.getIdProvider();

    Product result = catalogService.findProductById(idProvider.formatProductId(catalogAlias, partNumber), storeContext);
    if (result == null){
      return Optional.empty();
    }
    // if we only use a product id (instead of a sku id) the fallback does not work in the asset service
    if (result.isVariant()) {
      //load ProductVariant bean
      result = catalogService.findProductVariantById(idProvider.formatProductVariantId(catalogAlias, partNumber), storeContext);
    }
    return Optional.of(result);
  }

}
