package com.coremedia.livecontext.ecommerce.hybris.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InvalidationsDocument {

  @JsonProperty("lastInvalidation")
  private long lastInvalidation;

  private List<JsonInvalidationEvent> invalidations;

  public long getLastInvalidation() {
    return lastInvalidation;
  }

  public List<JsonInvalidationEvent> getInvalidations() {
    return invalidations;
  }
}
