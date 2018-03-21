package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.StoreConfigResource;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class HybrisStoreContextProvider extends AbstractStoreContextProvider {

  private StoreConfigResource storeConfigResource;

  private String defaultStoreId;
  private String defaultStoreLocale;

  private String previewDefaultCatalogVersion;
  private String liveDefaultCatalogVersion;

  @Nullable
  @Override
  protected StoreContext internalCreateContext(@Nonnull Site site) {
    StoreContext result = null;

    // only create catalog context if settings were found for current site
    Struct repositoryStoreConfig = getSettingsService().setting(CONFIG_KEY_STORE_CONFIG, Struct.class,
            site.getSiteRootDocument());
    if (repositoryStoreConfig != null) {
      try {
        Map<String, Object> targetConfig = new HashMap<>();

        String configId = StructUtil.getString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID);

        readStoreConfigFromSpring(configId, targetConfig);
        updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig, site);

        String catalogVersion = isPreviewContentRepository(site) ? previewDefaultCatalogVersion : liveDefaultCatalogVersion;

        result = StoreContextHelper.createContext(
                (String) targetConfig.get(CONFIG_KEY_CONFIG_ID),
                (String) targetConfig.get(CONFIG_KEY_STORE_ID),
                (String) targetConfig.get(CONFIG_KEY_STORE_NAME),
                (String) targetConfig.get(CONFIG_KEY_CATALOG_ID),
                site.getLocale(),
                (String) targetConfig.get(CONFIG_KEY_CURRENCY),
                catalogVersion
        );

        StoreContextHelper.setSiteId(result, site.getId());
      } catch (NoSuchPropertyDescriptorException e) {
        throw new InvalidContextException("Missing properties in store configuration. ", e);
      }

    }
    return result;
  }

  private static boolean isPreviewContentRepository(@Nonnull Site site) {
    return site.getSiteRootDocument().getRepository().isContentManagementServer();
  }

  public StoreConfigResource getStoreConfigResource() {
    return storeConfigResource;
  }

  @Required
  public void setStoreConfigResource(StoreConfigResource storeConfigResource) {
    this.storeConfigResource = storeConfigResource;
  }

  public String getDefaultStoreId() {
    return defaultStoreId;
  }

  @Value("${livecontext.hybris.default.storeId:default}")
  public void setDefaultStoreId(String defaultStoreId) {
    this.defaultStoreId = defaultStoreId;
  }

  public String getDefaultStoreLocale() {
    return defaultStoreLocale;
  }

  @Value("${livecontext.hybris.default.locale:en_US}")
  public void setDefaultStoreLocale(String defaultStoreLocale) {
    this.defaultStoreLocale = defaultStoreLocale;
  }

  @Value("${livecontext.hybris.rest.defaultCatalagVersion.preview:Staged}")
  public void setPreviewDefaultCatalogVersion(String previewDefaultCatalogVersion) {
    this.previewDefaultCatalogVersion = previewDefaultCatalogVersion;
  }

  @Value("${livecontext.hybris.rest.defaultCatalagVersion.live:Online}")
  public void setLiveDefaultCatalogVersion(String liveDefaultCatalogVersion) {
    this.liveDefaultCatalogVersion = liveDefaultCatalogVersion;
  }
}

