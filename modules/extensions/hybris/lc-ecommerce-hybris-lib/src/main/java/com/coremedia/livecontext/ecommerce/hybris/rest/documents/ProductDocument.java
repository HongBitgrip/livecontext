package com.coremedia.livecontext.ecommerce.hybris.rest.documents;


import com.coremedia.livecontext.ecommerce.hybris.rest.CategoryIdDeserializer;
import com.coremedia.livecontext.ecommerce.hybris.rest.PriceDeserializer;
import com.coremedia.livecontext.ecommerce.hybris.rest.ProductVariantDeserializer;
import com.coremedia.livecontext.ecommerce.hybris.rest.SwatchColorDeserializer;
import com.coremedia.livecontext.ecommerce.hybris.rest.VariantAttributesDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductDocument extends AbstractHybrisDocument {

  @JsonProperty("creationtime")
  private Date creationTime;

  @JsonProperty("modifiedtime")
  private Date modificationTime;


  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("summary")
  private String summary;

  @JsonProperty("baseProduct")
  private ProductRefDocument baseProduct;

  @JsonProperty("supercategories")
  @JsonDeserialize(using = CategoryIdDeserializer.class)
  private String categoryId;

  @JsonProperty("thumbnail")
  private MediaDocument thumbnailMediaDocument;

  @JsonProperty("picture")
  private MediaDocument pictureMediaDocument;

  @JsonProperty("europe1Prices")
  @JsonDeserialize(using = PriceDeserializer.class)
  private List<PriceRefDocument> priceRefDocuments;

  @JsonProperty("variants")
  @JsonDeserialize(using = ProductVariantDeserializer .class)
  private List<ProductVariantRefDocument> variants;

  @JsonProperty("variantAttributes")
  @JsonDeserialize(using = VariantAttributesDeserializer.class)
  private List<VariantAttributeDocument> variantAttributes;

  @JsonProperty("swatchColors")
  @JsonDeserialize(using = SwatchColorDeserializer.class)
  private List<String> swatchColors;

  public String getDescription() {
    return description;
  }

  public String getSummary() {
    return summary;
  }

  public ProductRefDocument getBaseProduct() {
      return baseProduct;
  }

  public String getCategoryId() {
    return categoryId;
  }


 public Date getCreationTime() {
    return creationTime;
  }

  public Date getModificationTime() {
    return modificationTime;
  }

  public String getName() {
    return name;
  }

  public List<ProductVariantRefDocument> getVariantRefDocuments(){
    return variants;
  }

  public String getThumbnailDownloadUrl() {
    if (thumbnailMediaDocument == null) {
      return StringUtils.EMPTY;
    }
    String downloadUrl = thumbnailMediaDocument.getDownloadUrl();
    return downloadUrl;
  }

  public String getPictureDownloadUrl() {
    if (pictureMediaDocument == null) {
      return StringUtils.EMPTY;
    }
    return pictureMediaDocument.getDownloadUrl();
  }

  public List<PriceRefDocument> getPriceRefDocuments() {
    return priceRefDocuments;
  }

  public List<VariantAttributeDocument> getVariantAttributes() {
    return variantAttributes;
  }

  public List<String> getSwatchColors() {
    return swatchColors;
  }
}
