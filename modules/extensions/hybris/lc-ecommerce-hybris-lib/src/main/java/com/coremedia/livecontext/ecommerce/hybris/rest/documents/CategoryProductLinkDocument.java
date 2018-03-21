package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryProductLinkDocument extends AbstractHybrisDocument {

  @JsonProperty("sku")
  private String sku;

  @JsonProperty("position")
  private int position;

  @JsonProperty("category_id")
  private String categoryId;

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }
}
