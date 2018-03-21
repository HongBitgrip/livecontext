package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuggestionDocument extends AbstractHybrisDocument {

  @JsonProperty("@query")
  private String query;

  @JsonProperty("@suggestion")
  private String suggestion;

  public String getQuery() {
    return query;
  }

  public String getSuggestion() {
    return suggestion;
  }
}
