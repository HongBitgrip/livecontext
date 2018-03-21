package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ProductSearchDocument extends AbstractHybrisDocument {

  @JsonProperty("@freeTextSearch")
  private String query;

  @JsonProperty("products")
  private List<ProductRefDocument> products;

  @JsonProperty("pagination")
  private PaginationDocument pagination;

  @JsonProperty("spellingSuggestion")
  private SuggestionDocument spellingSuggestion;

  @JsonProperty("facets")
  private List<SearchFacetDocument> facets;

  public List<ProductRefDocument> getProducts() {
    return products;
  }

  @Nonnull
  public List<SearchFacet> getFacets() {
    return Collections.unmodifiableList(facets == null ? Collections.emptyList() : facets);
  }

  public String getQuery() {
    return query;
  }

  public PaginationDocument getPagination() {
    return pagination;
  }

  public SuggestionDocument getSpellingSuggestion() {
    return spellingSuggestion;
  }
}
