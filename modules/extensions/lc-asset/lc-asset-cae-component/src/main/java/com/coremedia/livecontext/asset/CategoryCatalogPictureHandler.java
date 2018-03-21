package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static java.util.Objects.requireNonNull;

@RequestMapping
public class CategoryCatalogPictureHandler extends CatalogPictureHandlerBase {

  /**
   * URI Pattern for transformed blobs for categories
   * e.g. /catalogimage/category/10202/en_US/full/PC_Deli.jpg
   */
  public static final String IMAGE_URI_PATTERN =
          "/" + AssetService.CATEGORY_URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  /**
   * e.g. /catalogimage/category/[storeId]/en_US/[catalogId]/full/PC_Deli.jpg
   */
  public static final String IMAGE_URI_PATTERN_FOR_CATALOG =
          "/" + AssetService.CATEGORY_URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + CATALOG_ID + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";

  @RequestMapping(value = IMAGE_URI_PATTERN_FOR_CATALOG)
  public ModelAndView handleRequestWidthHeightForCategoryWithCatalog(@PathVariable(STORE_ID) String storeId,
                                                                     @PathVariable(LOCALE) String locale,
                                                                     @PathVariable(FORMAT_NAME) String formatName,
                                                                     @PathVariable(PART_NUMBER) String partNumber,
                                                                     @PathVariable(SEGMENT_EXTENSION) String extension,
                                                                     @PathVariable(CATALOG_ID) String catalogId,
                                                                     WebRequest request) throws IOException {

    CommerceConnection connection = CurrentCommerceConnection.get();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");

    CatalogAlias catalogAlias = resolveCatalogAliasFromId(catalogId, storeContext);
    CommerceId id = idProvider.formatCategoryId(catalogAlias, partNumber);

    return handleRequestWidthHeight(storeId, locale, formatName, id, extension, request);

  }

  @RequestMapping(value = IMAGE_URI_PATTERN)
  public ModelAndView handleRequestWidthHeightForCategory(@PathVariable(STORE_ID) String storeId,
                                                          @PathVariable(LOCALE) String locale,
                                                          @PathVariable(FORMAT_NAME) String formatName,
                                                          @PathVariable(PART_NUMBER) String partNumber,
                                                          @PathVariable(SEGMENT_EXTENSION) String extension,
                                                          WebRequest request) throws IOException {
    CommerceConnection connection = CurrentCommerceConnection.get();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");

    CommerceId id = idProvider.formatCategoryId(DEFAULT_CATALOG_ALIAS, partNumber);

    return handleRequestWidthHeight(storeId, locale, formatName, id, extension, request);
  }
}
