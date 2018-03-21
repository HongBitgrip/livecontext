package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserGroupRefDocument extends UserGroupDocument{

  @JsonProperty("@uid")
  private String uid;

  public String getUid() {
    return uid;
  }

}
