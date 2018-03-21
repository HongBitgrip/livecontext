package com.coremedia.ecommerce.studio.rest;

import javax.ws.rs.core.Response;

/**
 * Exception to indicate to the REST client that a catalog bean could not be found.
 */
public class CatalogBeanNotFoundRestException extends CatalogRestException {

  public CatalogBeanNotFoundRestException(String message) {
    super(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, message);
  }

  public CatalogBeanNotFoundRestException(Response.Status status, String message) {
    super(status, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, message);
  }
}
