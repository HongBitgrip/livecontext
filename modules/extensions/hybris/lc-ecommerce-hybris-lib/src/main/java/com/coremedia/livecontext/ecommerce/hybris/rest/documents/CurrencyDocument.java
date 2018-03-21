package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyDocument extends AbstractHybrisDocument {

  @JsonProperty("@isocode")
  private String isoCode;

  public String getIsoCode() {
    return isoCode;
  }

}
