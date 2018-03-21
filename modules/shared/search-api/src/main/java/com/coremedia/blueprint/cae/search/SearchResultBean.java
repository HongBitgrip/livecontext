package com.coremedia.blueprint.cae.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The SearchResultBean is a search engine independent representation of a search result.
 *
 * @cm.template.api
 */
public class SearchResultBean {

  private SearchQueryBean searchQuery = null;
  private List<?> hits = Collections.emptyList();
  private long numHits = 0;
  private Map<String, List<ValueAndCount>> facets = Collections.emptyMap();
  private String spellSuggestion = "";
  private Map<Object, Map<String, List<String>>> highlightingResults = Collections.emptyMap();
  private List<ValueAndCount> autocompleteSuggestions;

  /**
   * Returns the original {@link SearchQueryBean query} for this result.
   *
   * @return the original {@link SearchQueryBean query} for this result.
   */
  public SearchQueryBean getSearchQuery() {
    return searchQuery;
  }

  /**
   * Sets the original {@link SearchQueryBean query} for this result.
   *
   * @param searchQuery the original {@link SearchQueryBean query} for this result.
   */
  public void setSearchQuery(SearchQueryBean searchQuery) {
    this.searchQuery = searchQuery;
  }

  /**
   * Returns the hits returned by the search engine
   *
   * @return the hits returned by the search engine
   * @cm.template.api
   */
  public List<?> getHits() {
    return hits;
  }

  /**
   * Sets the hits returned by the search engine
   *
   * @param hits the hits returned by the search engine
   */
  public void setHits(List<?> hits) {
    this.hits = hits;
  }

  /**
   * Returns the total number of hits
   *
   * @return the total number of hits
   * @cm.template.api
   */
  public long getNumHits() {
    return numHits;
  }

  /**
   * Sets the total number of hits
   *
   * @param numHits the total number of hits
   */
  public void setNumHits(long numHits) {
    this.numHits = numHits;
  }

  /**
   * Returns the facets for this search
   *
   * @return the facets for this search
   */
  public Map<String, List<ValueAndCount>> getFacets() {
    return facets;
  }

  /**
   * Sets the facets for this search
   *
   * @param facets the facets for this search
   */
  public void setFacets(Map<String, List<ValueAndCount>> facets) {
    this.facets = facets;
  }

  /**
   * Returns the spell suggestion for this query
   *
   * @return the spell suggestion for this query
   */
  public String getSpellSuggestion() {
    return spellSuggestion;
  }

  /**
   * Sets the spell suggestion for this query
   *
   * @param spellSuggestion the spell suggestion for this query
   */
  public void setSpellSuggestion(String spellSuggestion) {
    this.spellSuggestion = spellSuggestion;
  }

  /**
   * Returns the search engine supplied highlighted search results (if any). Follows the Solr convention and
   * returns a map where the key is the id of the result item and the value is a map where the key is a field
   * in the result item and the value is a list of Strings with highlighting embedded.
   *
   * @return a map where the key is the id of the result item and the value is a map where the key is a field
   * in the result item and the value is a list of Strings with highlighting embedded.
   * @cm.template.api
   */
  public Map<Object, Map<String, List<String>>> getHighlightingResults() {
    return highlightingResults;
  }

  /**
   * Sets the search engine supplied highlighted search results.
   *
   * @param highlightingResults the search engine supplied highlighted search results.
   */
  public void setHighlightingResults(Map<Object, Map<String, List<String>>> highlightingResults) {
    this.highlightingResults = highlightingResults;
  }

  /**
   * Returns the auto-complete suggestions for this query.
   *
   * @return the auto-complete suggestions for this query
   */
  public List<ValueAndCount> getAutocompleteSuggestions() {
    return autocompleteSuggestions;
  }

  /**
   * Sets the auto-complete suggestions for this query.
   */
  public void setAutocompleteSuggestions(List<ValueAndCount> autocompleteSuggestions) {
    this.autocompleteSuggestions = autocompleteSuggestions;
  }
}
