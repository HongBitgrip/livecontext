package com.coremedia.ecommerce.studio.rest;

import com.coremedia.rest.cap.exception.ParameterizedException;
import com.coremedia.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception to transport a given error code and message to the REST client.
 */
public class CatalogRestException extends ParameterizedException {

  private static Map<String, String> errorNames = new HashMap<>();

  public CatalogRestException(Response.Status status, String errorCode, String message) {
    super(status, errorCode, getErrorName(errorCode), message);
  }

  /**
   * Returns a human-readable error name of this exception.
   *
   * @param errorCode code to translate
   * @return a human-readable error name of this exception
   */
  @Nullable
  public static synchronized String getErrorName(String errorCode) {
    return errorNames.computeIfAbsent(errorCode, k -> fetchErrorName(errorCode));
  }

  @Nullable
  private static String fetchErrorName(@Nonnull String errorCode) {
    return Util.getConstantName(CatalogRestErrorCodes.class, "LIVECONTEXT_ERROR_", errorCode);
  }
}
