package com.coremedia.livecontext.ecommerce.hybris.rest.documents;


import com.coremedia.livecontext.ecommerce.hybris.rest.CategoryDeserializer;
import com.coremedia.livecontext.ecommerce.hybris.rest.CategoryIdDeserializer;
import com.coremedia.livecontext.ecommerce.hybris.rest.ProductDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;
import java.util.List;

public class CategoryDocument extends AbstractHybrisDocument {

  @JsonProperty("creationtime")
  private Date creationTime;

  @JsonProperty("modifiedtime")
  private Date modificationTime;

  @JsonProperty("name")
  private String name;

  @JsonProperty("supercategories")
  @JsonDeserialize(using = CategoryIdDeserializer.class)
  private String parentId;

  @JsonProperty("categories")
  @JsonDeserialize(using = CategoryDeserializer.class)
  private List<CategoryRefDocument> subCategories;

  @JsonProperty("products")
  @JsonDeserialize(using = ProductDeserializer.class)
  private List<ProductRefDocument> products;

  @JsonProperty("thumbnail")
  private MediaDocument thumbnail;

  @JsonProperty("picture")
  private MediaDocument picture;

  @JsonProperty("description")
  private String description;

  public List<CategoryRefDocument> getSubCategories() {
    return subCategories;
  }

  public String getName() {
    return name;
  }


  public List<ProductRefDocument> getProducts() {
    return products;
  }

  public String getParentId() {
    return parentId;
  }

  public MediaDocument getThumbnail() {
    return thumbnail;
  }

  public MediaDocument getPicture() {
    return picture;
  }

  public String getDescription() {
    return description;
  }
}
