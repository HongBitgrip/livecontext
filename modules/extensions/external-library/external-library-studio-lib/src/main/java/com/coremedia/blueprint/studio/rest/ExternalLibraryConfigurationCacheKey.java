package com.coremedia.blueprint.studio.rest;

import com.coremedia.blueprint.base.rest.config.ConfigurationService;
import com.coremedia.blueprint.base.rest.config.StructConfiguration;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The cache key that contains the ExternalLibrarySourceListRepresentation, which contains all
 * entries available for a site in the external library.
 * The configuration is read from a struct property of a site depending content object.
 * The path of the configuration content is build via spring settings and the constant for the document name,
 * see documentation below.
 */
public class ExternalLibraryConfigurationCacheKey extends CacheKey<ExternalLibrarySourceListRepresentation> {

  //The document name the settings struct is read from, check the spring configuration for path details
  private static final String EXTERNAL_LIBRARIES = "ExternalLibraries";
  private static final String STRUCT_NAME = "externalLibraries";
  private String preferredSiteId;
  private ConfigurationService configurationService;

  /**
   * Creates a new site depending configuration cache key.
   *
   * @param preferredSiteId The if of the site the configuration will be cached for.
   */
  public ExternalLibraryConfigurationCacheKey(ConfigurationService configurationService, String preferredSiteId) {
    this.preferredSiteId = preferredSiteId;
    this.configurationService = configurationService;
  }

  /**
   * Creates the source list representations that is cached.
   *
   * @param cache the cache managing asked to return a value for this cache key
   * @return the source list representations that is cached
   * @throws Exception if the value cannot be computed. This will prevent the value from being cached.
   */
  @Override
  public ExternalLibrarySourceListRepresentation evaluate(Cache cache) throws Exception {
    ExternalLibrarySourceListRepresentation result = new ExternalLibrarySourceListRepresentation();

    StructConfiguration config = configurationService.getStructMaps(preferredSiteId, EXTERNAL_LIBRARIES, "settings");
    addConfiguration(result, config.getGlobalStructs());
    addConfiguration(result, config.getLocalStructs());
    return result;
  }

  private void addConfiguration(ExternalLibrarySourceListRepresentation result, Map<String, Object> structMap) {
    if (structMap != null && structMap.containsKey(STRUCT_NAME)) {
      List<Map<String, Object>> items = (List<Map<String, Object>>) structMap.get(STRUCT_NAME);
      /**
       * Iteration over each item provider.
       */
      for (Map<String, Object> entry : items) {
        ExternalLibrarySourceItemRepresentation thirdPartySourceRepresentation = new ExternalLibrarySourceItemRepresentation();
        thirdPartySourceRepresentation.setIndex((Integer) entry.get("index"));
        thirdPartySourceRepresentation.setName((String) entry.get("name"));
        thirdPartySourceRepresentation.setProviderClass((String) entry.get("providerId"));
        thirdPartySourceRepresentation.setDataUrl((String) entry.get("dataUrl"));
        thirdPartySourceRepresentation.setPreviewType((String) entry.get("previewType"));
        thirdPartySourceRepresentation.setContentType((String) entry.get("contentType"));
        thirdPartySourceRepresentation.setMarkAsRead((Boolean) entry.get("markAsRead"));
        //additional config parameters like credentials are added this way
        thirdPartySourceRepresentation.setParameters(entry);
        result.addItem(thirdPartySourceRepresentation);
      }

      Collections.sort(result.getItems(), new ExternalLibrarySourceItemRepresentationComparator());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExternalLibraryConfigurationCacheKey)) {
      return false;
    }

    ExternalLibraryConfigurationCacheKey that = (ExternalLibraryConfigurationCacheKey) o;

    return !(preferredSiteId != null ? !preferredSiteId.equals(that.preferredSiteId) : that.preferredSiteId != null);

  }

  @Override
  public int hashCode() {
    return preferredSiteId != null ? preferredSiteId.hashCode() : 0;
  }
}
