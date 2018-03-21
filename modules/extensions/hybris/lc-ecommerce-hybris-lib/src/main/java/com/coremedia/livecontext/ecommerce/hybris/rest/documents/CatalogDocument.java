package com.coremedia.livecontext.ecommerce.hybris.rest.documents;


import com.coremedia.livecontext.ecommerce.hybris.rest.CategoryDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public class CatalogDocument extends AbstractHybrisDocument {

  @JsonProperty("rootCategories")
  @JsonDeserialize(using = CategoryDeserializer.class)
  private List<CategoryRefDocument> rootCategories;

  public List<CategoryRefDocument> getRootCategories() {
    return rootCategories;
  }

  @Override
  public String getCode() {
    String code = super.getCode();
    return code != null ? code : getKey();
  }
}
