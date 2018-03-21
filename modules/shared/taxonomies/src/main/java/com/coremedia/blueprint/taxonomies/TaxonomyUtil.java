package com.coremedia.blueprint.taxonomies;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.rest.cap.content.search.SearchServiceResult;
import com.coremedia.rest.cap.content.search.solr.SolrSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Common utility methods for taxonomies.
 */
public final class TaxonomyUtil {

  private static final String CHILDREN_ATTRIBUTE_IDENTIFIER = "children";
  private static final String CM_TAXONOMY_TYPE_IDENTIFIER = "CMTaxonomy";

  /**
   * Hide Utility Class Constructor
   */
  private TaxonomyUtil() {
  }

  private static final String CONTENT_ID_PREFIX = "coremedia:///cap/";

  /**
   * Escapes special characters in the document name and replaces them with wildcards.
   *
   * @param name The document name that should be searched/filtered for.
   * @return The formatted search pattern.
   */
  public static String formatSolrSearch(String name) {
    String result = '*' + name.replaceAll("-", " ");
    return result.endsWith(" ") ? result : result + '*';
  }

  /**
   * Checks if the given taxonomy has a cyclic dependency.
   *
   * @param tax The taxonomy to check a cycle for.
   * @param contentType type of the taxonomy
   * @return True, if the taxonomy contains a cyclic child relation,
   *        false if no cycle is detected or given content is not a taxonomy
   */
  public static boolean isCyclic(Content tax, ContentType contentType) {
    return checkCycle(tax, new ArrayList<String>(), contentType);
  }

  private static boolean checkCycle(Content tax, List<String> path, ContentType contentType) {
    if (path.contains(tax.getId())) {
      return true;
    }

    if (!isTaxonomy(tax, contentType)) {
      return false;
    }

    path.add(tax.getId());
    List<Content> children = tax.getLinks(CHILDREN_ATTRIBUTE_IDENTIFIER);

    if (!tax.isDestroyed() && tax.isInProduction() && !children.isEmpty()) {
      for (Content child : children) {
        if (checkCycle(child, path, contentType)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Determines if the given content is of type CMTaxonomy.
   *
   * @param tax content which might be a CMTaxonomy
   * @param contentType content type of the taxonomy
   * @return true if the content is a taxonomy, otherwise false
   */
  public static boolean isTaxonomy(Content tax, ContentType contentType) {
    return tax.getType().isSubtypeOf(contentType);
  }

  /**
   * Determines if the given content is of type CMTaxonomy.
   *
   * @param tax content which might be a CMTaxonomy
   * @param contentType content type of the taxonomy
   * @return true if the content is a taxonomy, otherwise false
   */
  public static boolean isTaxonomy(Content tax, String contentType) {
    return tax.getType().isSubtypeOf(contentType);
  }

  public static List<Content> solrSearch(SolrSearchService solrSearchService, Content folder, ContentType type, String query, int limit) {
    List<ContentType> types = new ArrayList<>();
    types.add(type);
    return solrSearch(solrSearchService, folder, types, query, limit);
  }

  /**
   * Recursive call to collect all taxonomies for a folder.
   */
  public static List<Content> solrSearch(SolrSearchService solrSearchService, Content folder, Collection<ContentType> types, String query, int limit) {
    SearchServiceResult result = solrSearchService.search(query, limit,
            new ArrayList<String>(),
            folder,
            true,
            types,
            true,
            Collections.singletonList("isdeleted:false"),
            new ArrayList<String>(),
            new ArrayList<String>());
    return result.getHits();
  }

  /**
   * Recursive call to collect find the first taxonomy for a folder.
   * Ignores non taxonomies in given folder.
   *
   * @param folder The active folder.
   * @param contentType content type of the taxonomy
   * @return first taxonomy found in hierarchy, null if non can be found.
   */
  public static Content findFirstTaxonomy(Content folder, String contentType) {
    for (Content c : folder.getChildren()) {
      if (c.isDocument()) {
        if (isTaxonomy(c, contentType)) {
          return c;
        }
      } else {
        return findFirstTaxonomy(c, contentType);
      }
    }
    return null;
  }

  public static String getRestIdFromCapId(String ref) {
    return "content/" + ref.substring(ref.lastIndexOf('/') + 1, ref.length());
  }

  public static String asContentId(String nodeRef) {
    return CONTENT_ID_PREFIX + nodeRef;
  }

  /**
   * Formats the content id to a taxonomy node id
   *
   * @param contentId The cap id to format the node id for.
   * @return The formatted node id.
   */
  public static String asNodeRef(String contentId) {
    return contentId.substring(CONTENT_ID_PREFIX.length());
  }
}
