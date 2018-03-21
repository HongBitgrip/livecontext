package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.transform.TransformedBlob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CatalogPictureHandlerBase extends HandlerBase {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogPictureHandlerBase.class);

  public static final String FORMAT_KEY_THUMBNAIL = "thumbnail";
  public static final String FORMAT_KEY_FULL = "full";

  protected AssetService assetService;
  private LiveContextSiteResolver siteResolver;
  private ContentBeanFactory contentBeanFactory;
  private Map<String, String> pictureFormats;
  private TransformImageService transformImageService;
  protected CatalogAliasTranslationService catalogAliasTranslationService;

  protected static final String STORE_ID = "storeId";
  protected static final String CATALOG_ID = "catalogId";
  protected static final String LOCALE = "locale";
  protected static final String PART_NUMBER = "partNumber";
  protected static final String FORMAT_NAME = "formatName";

  /**
   * handle picture request with the given store id, locale, picture format name, reference id and request
   * @param storeId the store id
   * @param locale the locale
   * @param formatName the picture format name
   * @param commerceId the reference id
   * @param extension the mime type extension
   * @param request the request
   */
  @Nullable
  protected ModelAndView handleRequestWidthHeight(String storeId,
                                                  String locale,
                                                  String formatName,
                                                  CommerceId commerceId,
                                                  String extension,
                                                  WebRequest request) throws IOException {
    Locale localeObj = LocaleHelper.parseLocaleFromString(locale).orElse(null);
    Site site = siteResolver.findSiteFor(storeId, localeObj);
    if (site == null) {
      //Site not found
      return HandlerHelper.notFound();
    }

    Content catalogPictureObject = findCatalogPictureFor(commerceId, site);

    if (catalogPictureObject == null) {
      //Picture not found
      return HandlerHelper.notFound();
    }

    String pictureFormat = pictureFormats.get(formatName);
    if (pictureFormat == null) {
      //format not found
      return HandlerHelper.notFound();
    }

    //picture format value consists of <transformation segment>/<width>/<height>
    String[] split = pictureFormat.split("/");
    String transformationName = split[0];
    String width = split[1];
    String height = split[2];

    CMPicture catalogPicture = contentBeanFactory.createBeanFor(catalogPictureObject, CMPicture.class);

    Blob transformedBlob = transformImageService.transformWithDimensions(catalogPictureObject, catalogPicture.getData(),
        (TransformedBlob) catalogPicture.getTransformedData(transformationName), transformationName,
        extension, Integer.parseInt(width), Integer.parseInt(height));

    if (transformedBlob == null) {
      return HandlerHelper.notFound();
    }

    if (request.checkNotModified(transformedBlob.getETag())) {
      // shortcut exit - no further processing necessary
      return null;
    }

    return HandlerHelper.createModel(transformedBlob);
  }

  protected CatalogAlias resolveCatalogAliasFromId(@Nullable String catalogId, @Nonnull StoreContext storeContext) {
    if (catalogId == null) {
      return storeContext.getCatalogAlias();
    }
    Optional<CatalogAlias> catalogAliasForId = catalogAliasTranslationService.getCatalogAliasForId(CatalogId.of(catalogId), storeContext.getSiteId());
    return catalogAliasForId.orElse(storeContext.getCatalogAlias());
  }

  /**
   * find the catalog picture of the given reference id and site
   * @param id the reference id
   * @param site the given site
   * @return the found catalog picture document
   */
  private Content findCatalogPictureFor(@Nonnull CommerceId id, @Nonnull Site site) {
    if(null != assetService) {
      List<Content> pictureList = assetService.findPictures(id, true);
      if (pictureList.size() > 1) {
        LOG.debug("More than one CMPicture found for the catalog object with the id " + id + " in the site " + site.getName());
      }
      if(!pictureList.isEmpty()) {
        return pictureList.get(0);
      }
    }
    return null;
  }

  @Required
  public void setSiteResolver(LiveContextSiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setPictureFormats(Map<String, String> pictureFormats) {
    this.pictureFormats = pictureFormats;
  }

  @Autowired(required = false)
  public void setAssetService(AssetService assetService) {
    this.assetService = assetService;
  }

  @Autowired
  public void setCatalogAliasTranslationService(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  @Required
  public void setTransformImageService(TransformImageService transformImageService) {
    this.transformImageService = transformImageService;
  }
}
