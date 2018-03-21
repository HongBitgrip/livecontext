package com.coremedia.blueprint.studio.rest;

import java.util.Map;

/**
 * Common interface for external library platform provider's API.
 * The bean must have the prototype scope.
 */
public interface ExternalLibraryProvider {

  /**
   * Establishes the initial connection to the external library provider platform.
   */
  void init(String preferredSite, Map<String, Object> configuration);

  /**
   * Returns a list of all external library items that are available
   * for the configured external library provider account.
   *
   * @return A list of external library item representations.
   */
  ExternalLibraryItemListRepresentation getItems(String filter);

  /**
   * Returns the external library representation for the given id.
   *
   * @param id The providers ID of the video.
   * @return The video representation instance.
   */
  ExternalLibraryItemRepresentation getItem(String id);

  /**
   * Allows the provider implementations to set specific fields from
   * the third party item for the content's properties.
   *
   * @param item    The third party item the content has been created from.
   * @param representation The created content represention container used to add additional information.
   */
  void postProcessNewContent(ExternalLibraryItemRepresentation item, ExternalLibraryPostProcessingRepresentation representation);
}
