package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

enum OrderByType {

  ORDER_BY_DEFAULT("relevance"),
  ORDER_BY_RELEVANCE("relevance"),
  ORDER_BY_TOP_RATED("topRated"),
  ORDER_BY_TYPE_NAME_ASC("name-asc"),
  ORDER_BY_TYPE_NAME_DSC("name-desc"),
  ORDER_BY_TYPE_PRICE_ASC("price-asc"),
  ORDER_BY_TYPE_PRICE_DSC("price-desc");

  private final String value;

  OrderByType(final String orderByType) {
    this.value = orderByType;
  }

  public String getValue() {
    return value;
  }
}
