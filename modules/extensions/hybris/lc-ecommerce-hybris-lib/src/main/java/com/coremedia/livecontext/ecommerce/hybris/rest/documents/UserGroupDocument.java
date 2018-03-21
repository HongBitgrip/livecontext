package com.coremedia.livecontext.ecommerce.hybris.rest.documents;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public class UserGroupDocument extends AbstractHybrisDocument {
  @JsonProperty("@uid")
  private String uid;

  @JsonProperty("@description")
  private String description;

  public String getUid() {
    return uid;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String getCode() {
    return getUid();
  }
}
