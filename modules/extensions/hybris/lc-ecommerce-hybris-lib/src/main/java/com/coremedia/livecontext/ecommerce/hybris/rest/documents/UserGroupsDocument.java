package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserGroupsDocument  extends AbstractHybrisDocument{
  @JsonProperty("usergroup")
  private List<UserGroupRefDocument> userGroups;

  public List<UserGroupRefDocument> getUserGroups() {
    return userGroups;
  }
}
