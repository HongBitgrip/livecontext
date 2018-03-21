package com.coremedia.ecommerce.studio.rest;

import javax.ws.rs.core.Response;

import static com.coremedia.ecommerce.studio.rest.CatalogRestErrorCodes.COULD_NOT_FIND_STORE_BEAN;

public class StoreBeanNotFoundException extends CatalogRestException {
  public StoreBeanNotFoundException(Response.Status status, String message) {
    super(status, COULD_NOT_FIND_STORE_BEAN, message);
  }
}
