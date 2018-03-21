package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * Request utilities
 */
class Requests {

  private Requests() {
  }

  /**
   * Return the underlying servlet request for this {@link NativeWebRequest}.
   *
   * @return the underlying servlet request, never null
   * @throws IllegalStateException if there is no underlying servlet request
   */
  @Nonnull
  static HttpServletRequest getServletRequest(@Nonnull NativeWebRequest nativeWebRequest) {
    HttpServletRequest servletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

    if (servletRequest == null) {
      throw new IllegalStateException("Underlying native servlet request is not available.");
    }

    return servletRequest;
  }
}
