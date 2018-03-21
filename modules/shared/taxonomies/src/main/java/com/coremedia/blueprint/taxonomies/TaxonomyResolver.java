package com.coremedia.blueprint.taxonomies;

import java.util.Collection;

/**
 * Resolve the ITaxonomy that represent a taxonomy tree.
 */
public interface TaxonomyResolver {

  /**
   * Returns the taxonomy that matches the given taxonomy id.
   *
   * @return the taxonomy that matches the given taxonomy id.
   */
  Taxonomy getTaxonomy(String siteId, String taxonomyId);

  /**
   * Returns the collection of detected taxonomies.
   *
   * @return the collection of detected taxonomies.
   */
  Collection<Taxonomy> getTaxonomies();


  /**
   * Method for manual reload of taxonomies, e.g. after server imports.
   */
  boolean reload();
}
