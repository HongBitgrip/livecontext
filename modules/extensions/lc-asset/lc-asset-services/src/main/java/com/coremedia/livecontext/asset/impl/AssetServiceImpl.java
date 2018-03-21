package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class AssetServiceImpl implements AssetService {

  private static final String CONFIG_KEY_DEFAULT_PICTURE = "livecontext.assets.default.picture";

  private SitesService sitesService;
  private SettingsService settingsService;
  private AssetResolvingStrategy assetResolvingStrategy;

  @Nonnull
  public CatalogPicture getCatalogPicture(String url) {
    return computeReferenceIdFromUrl(url)
            .map(referenceIdFromUrl -> getCatalogPicture(url, referenceIdFromUrl))
            .orElseGet(() -> new CatalogPicture(url, null));
  }

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture(@Nonnull String url, @Nonnull CommerceId commerceId) {

    CommerceConnection connection = getCommerceConnection();
    AssetUrlProvider assetUrlProvider = connection.getAssetUrlProvider();
    if (assetUrlProvider == null) {
      return new CatalogPicture(url, null);
    }

    // Rewrite a given commerce url (make absolute and replace `{cmsHost}`)
    String imageUrl = assetUrlProvider.getImageUrl(url);

    // try to find assets related to the given commerce id
    List<Content> pictures = findPictures(commerceId, false);
    Content picture = pictures.stream().findFirst().orElse(null);
    return new CatalogPicture(imageUrl, picture);
  }

  @Nonnull
  @Override
  public List<Content> findPictures(@Nonnull CommerceId commerceId) {
    return findPictures(commerceId, true);
  }

  @Nonnull
  @Override
  public List<Content> findPictures(@Nonnull CommerceId commerceId, boolean withDefault) {
    Site site = findSite().orElse(null);
    if (site == null) {
      return emptyList();
    }

    List<Content> references = assetResolvingStrategy.findAssets("CMPicture", commerceId, site);
    if (!references.isEmpty()) {
      return references;
    }

    if (withDefault) {
      Content defaultPicture = getDefaultPicture(site);
      if (defaultPicture != null) {
        return singletonList(defaultPicture);
      }
    }

    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> findVisuals(@Nonnull CommerceId id) {
    return findVisuals(id, true);
  }

  @Nonnull
  @Override
  public List<Content> findVisuals(@Nonnull CommerceId commerceId, boolean withDefault) {
    Site site = findSite().orElse(null);
    if (site == null) {
      return emptyList();
    }

    List<Content> visuals = assetResolvingStrategy.findAssets("CMVisual", commerceId, site);

    if (withDefault && visuals.isEmpty()) {
      Content defaultPicture = getDefaultPicture(site);
      if (defaultPicture != null) {
        return singletonList(defaultPicture);
      }
    }

    return filterSpinners(visuals);
  }

  @Nonnull
  private static List<Content> filterSpinners(@Nonnull List<Content> allVisuals) {
    Set<Content> picturesInSpinners = extractPicturesInSpinners(allVisuals);
    return removePicturesInSpinners(allVisuals, picturesInSpinners);
  }

  @Nonnull
  private static List<Content> removePicturesInSpinners(@Nonnull List<Content> allVisuals,
                                                        @Nonnull Set<Content> picturesInSpinners) {
    return allVisuals.stream()
            .filter(visual -> !picturesInSpinners.contains(visual))
            .collect(toList());
  }

  @Nonnull
  private static Set<Content> extractPicturesInSpinners(@Nonnull List<Content> allVisuals) {
    return allVisuals.stream()
            .filter(visual -> visual.getType().isSubtypeOf("CMSpinner"))
            .flatMap(spinner -> ((List<Content>) spinner.getList("sequence")).stream())
            .collect(toSet());
  }

  @Nonnull
  @Override
  public List<Content> findDownloads(@Nonnull CommerceId commerceId) {
    return findSite()
            .map(site -> assetResolvingStrategy.findAssets("CMDownload", commerceId, site))
            .orElseGet(Collections::emptyList);
  }

  @Nonnull
  private Optional<Site> findSite() {
    return findCommerceConnection()
            .map(CommerceConnection::getStoreContext)
            .map(StoreContext::getSiteId)
            .flatMap(sitesService::findSite);
  }

  @Nullable
  @Override
  public Content getDefaultPicture(@Nonnull Site site) {
    return settingsService.setting(CONFIG_KEY_DEFAULT_PICTURE, Content.class, site.getSiteRootDocument());
  }

  @Nonnull
  private static Optional<CommerceId> computeReferenceIdFromUrl(@Nullable String url) {
    if (StringUtils.isBlank(url)) {
      return Optional.empty();
    }

    CommerceConnection connection = getCommerceConnection();

    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    StoreContext storeContext = requireNonNull(connection.getStoreContext(), "store context not available");

    String partNumber = parsePartNumberFromUrl(url).orElse(null);
    if (partNumber == null) {
      return Optional.empty();
    }

    CatalogAlias catalogAlias = storeContext.getCatalogAlias();

    if (url.contains(CATEGORY_URI_PREFIX)) {
      CommerceId categoryId = idProvider.formatCategoryId(catalogAlias, partNumber);
      return Optional.of(categoryId);
    }

    if (url.contains(PRODUCT_URI_PREFIX)) {
      CommerceId productId = idProvider.formatProductId(catalogAlias, partNumber);
      return Optional.of(productId);
    }

    return Optional.empty();
  }

  @Nonnull
  private static Optional<String> parsePartNumberFromUrl(@Nonnull String urlStr) {
    int index = urlStr.lastIndexOf('.');
    if (index < 0) {
      return Optional.empty();
    }

    String fileName = urlStr.substring(0, index);
    index = fileName.lastIndexOf('/');
    if (index < 0) {
      return Optional.empty();
    }

    String partNumber = fileName.substring(index + 1);
    return Optional.of(partNumber);
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Autowired
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Autowired
  public void setAssetResolvingStrategy(AssetResolvingStrategy assetResolvingStrategy) {
    this.assetResolvingStrategy = assetResolvingStrategy;
  }

  @Nonnull
  @Override
  public AssetService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, AssetService.class);
  }

  @Nullable
  public static AssetUrlProvider getAssetUrlProvider() {
    return getCommerceConnection().getAssetUrlProvider();
  }

  @Nonnull
  private static Optional<CommerceConnection> findCommerceConnection() {
    return CurrentCommerceConnection.find();
  }

  @Nonnull
  private static CommerceConnection getCommerceConnection() {
    return CurrentCommerceConnection.get();
  }
}
