package com.coremedia.blueprint.studio.rest;

import com.coremedia.blueprint.base.rest.config.ConfigurationService;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.linking.AbstractLinkingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Resource class for retrieving external library information.
 * The type of external library provider is created by a separate factory implementation
 * that implement the interface IThirdPartyProvider. The representation objects for
 * the external library list and single external library items are provider independent, therefor the
 * every provider created the video representations by converting their own client entry items.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("externallibrary")
public class ExternalLibraryResource extends AbstractLinkingResource implements ApplicationContextAware {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalLibraryResource.class);
  private static final String PREFERRED_SITE = "preferredSite";
  private static final String INDEX = "index";
  private CapConnection capConnection;
  private ApplicationContext applicationContext;
  private Cache cache;
  private ConfigurationService configurationService;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Required
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  /**
   * Returns a list of external library sources for a specific site
   *
   * @param site the site to load the sources for
   * @return a list of external library sources for a specific site
   */
  @GET
  @Path("sources")
  public ExternalLibrarySourceListRepresentation getSources(@QueryParam(PREFERRED_SITE) String site) {
    return loadConfiguration(site);
  }

  /**
   * Returns a list of external library items depending on the configured provider.
   * The provider is injected and configured in the spring configuration of this module.
   *
   * @param filter The filter string to filter the external library list for.
   * @return A representation that contains the details of all hits of.
   */
  @GET
  @Path("items")
  public ExternalLibraryItemListRepresentation getItems(@QueryParam(INDEX) int index,
                                                   @QueryParam(PREFERRED_SITE) String siteId,
                                                   @QueryParam("filter") String filter) {
    ExternalLibraryProvider providerImpl = getProvider(applicationContext, index, siteId);
    return providerImpl.getItems(filter);
  }

  /**
   * Returns a single video representation. The type of id is the one provided
   * by the external library provider. The provider is injected and configured in the spring configuration of this module.
   *
   * @param id The id of the external library to return.
   * @return The common external library representation, provider independent.
   */
  @GET
  @Path("item")
  public ExternalLibraryItemRepresentation getItem(@QueryParam(INDEX) int index,
                                              @QueryParam(PREFERRED_SITE) String siteId,
                                              @QueryParam("id") String id) {
    ExternalLibraryProvider providerImpl = getProvider(applicationContext, index, siteId);
    return providerImpl.getItem(id);
  }


  /**
   * Fills the new content object with data from the third party item. The provider implementation
   * decides which fields to map from the third party item to the newly created content properties.
   *
   * @param provider The provider implementation, executing the post processing.
   * @param dataUrl  The data url of the third party.
   * @param itemId   The id of the item to create the content from.
   * @param capId    The cap id of the created content.
   * @return The content id of the third party content.
   */
  @POST
  @Path("postProcess")
  @Consumes("application/x-www-form-urlencoded")
  public ExternalLibraryPostProcessingRepresentation postProcess(@FormParam("provider") String provider,
                            @FormParam(PREFERRED_SITE) String siteId,
                            @FormParam("dataUrl") String dataUrl,
                            @FormParam("id") String itemId,
                            @FormParam("providerId") Integer providerId,
                            @FormParam("capId") String capId) {
    //first read the external library item
    ExternalLibraryProvider providerImpl = getProvider(applicationContext, providerId, siteId);
    ExternalLibraryItemRepresentation item = providerImpl.getItem(itemId);
    Content createdContent = capConnection.getContentRepository().getContent(capId);

    //post process content via provider
    ExternalLibraryPostProcessingRepresentation rep = new ExternalLibraryPostProcessingRepresentation(createdContent);
    providerImpl.postProcessNewContent(item, rep);
    capConnection.flush();
    return rep;
  }

  /**
   * Retrieves the provider from the application context.
   *
   * @param index The index of the provider implementation.
   * @return the provider loaded for index or null
   */
  public ExternalLibraryProvider getProvider(ApplicationContext applicationContext, int index, String preferredSiteId) {
    try {
      ExternalLibrarySourceListRepresentation thirdPartySourceListRepresentation = loadConfiguration(preferredSiteId);
      ExternalLibrarySourceItemRepresentation itemById = thirdPartySourceListRepresentation.getItemById(index);


      ExternalLibraryProvider provider = (ExternalLibraryProvider) applicationContext.getBean(itemById.getProviderClass());
      provider.init(preferredSiteId, itemById.getParameters());
      return provider;
    } catch (Exception e) {
      LOG.error("Failed to get third party provider '" + index + "': " + e.getMessage(), e);
    }
    return null;
  }

  private ExternalLibrarySourceListRepresentation loadConfiguration(String preferredSiteId) {
    ExternalLibraryConfigurationCacheKey cacheKey = new ExternalLibraryConfigurationCacheKey(configurationService, preferredSiteId);
    return cache.get(cacheKey);
  }
}
