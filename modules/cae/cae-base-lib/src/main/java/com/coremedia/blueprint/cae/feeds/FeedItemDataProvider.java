
package com.coremedia.blueprint.cae.feeds;

import com.sun.syndication.feed.synd.SyndEntry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
  Base interface for FeedItemDataProvider
 */
public interface FeedItemDataProvider {

  /**
   * determines, if the provider is suitable for the given content bean
   * @param item the content bean to check for
   * @return true, if the provider is suitable, false if not
   */
  boolean isSupported(Object item);

  /**
   * creates a ROME SyndEntry for the given linkable Object
   * @param request the http-request of the user
   * @param response the http-response of the user
   * @param bean the linkable to generate the SyndEntry for
   * @return the generated SyndEntry itselfs
   */
  SyndEntry getSyndEntry(HttpServletRequest request, HttpServletResponse response, Object bean);
  
}
