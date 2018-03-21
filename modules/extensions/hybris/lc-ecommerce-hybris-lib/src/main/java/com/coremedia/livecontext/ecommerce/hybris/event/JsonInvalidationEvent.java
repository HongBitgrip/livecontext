package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Extension of @link {@link InvalidationEvent} for use with Jackson.
 */
public class JsonInvalidationEvent extends InvalidationEvent {

  @JsonCreator
  public JsonInvalidationEvent(@JsonProperty("techId") String techId,
                               @JsonProperty("contentType") String contentType,
                               @JsonProperty("timestamp") long timestamp) {
    super(techId, contentType, timestamp);
  }
}
