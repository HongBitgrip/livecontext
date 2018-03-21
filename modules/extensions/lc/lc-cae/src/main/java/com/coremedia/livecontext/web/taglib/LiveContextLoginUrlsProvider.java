package com.coremedia.livecontext.web.taglib;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides URLs to Commerce Login Form and logout handlers.
 */
public interface LiveContextLoginUrlsProvider {

  /**
   * Returns the URL for the login form of the Commerce system.
   *
   * @return login form URL
   * @param request the current request
   */
  @Nonnull
  String buildLoginFormUrl(@Nonnull HttpServletRequest request);

  /**
   * Returns the URL to log out of the Commerce system.
   *
   * @return logout URL
   * @param request the current request
   */
  @Nonnull
  String buildLogoutUrl(@Nonnull HttpServletRequest request);

  /**
   *  Transforms the given URL of the {@link com.coremedia.livecontext.handler.LoginStatusHandler}
   *  for the use with the Commerce system.
   *
   *  <p>For example, the implementation could add required query parameters.
   *
   * @param url login status handler URL
   * @param request the current request
   * @return transformed URL
   */
  @Nonnull
  String transformLoginStatusUrl(@Nonnull String url, @Nonnull HttpServletRequest request);

}
