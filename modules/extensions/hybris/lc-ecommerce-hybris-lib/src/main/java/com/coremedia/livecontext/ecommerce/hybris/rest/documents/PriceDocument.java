package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PriceDocument extends AbstractHybrisDocument {

  @JsonProperty("currency")
  private CurrencyDocument currency;

  /** Net (= offer)  or gross (=list) price. */
  @JsonProperty("net")
  private Boolean isNetPrice;

  @JsonProperty("giveAwayPrice")
  private Boolean isGiveAwayPrice;

  @JsonProperty("price")
  private String price;


  public String getCurrencyISOCode() {
    return currency.getIsoCode();
  }

  public Boolean isNetPrice() {
    return isNetPrice;
  }

  public Boolean isGiveAwayPrice() { return isGiveAwayPrice; }

  public String getPrice() {
    return price;
  }
}
