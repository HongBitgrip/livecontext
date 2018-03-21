package com.coremedia.livecontext.fragment.links.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * Helper for accessing a {@link Context livecontext context}.
 */
public final class LiveContextContextHelper {

  private static final String CONTEXT_ATTRIBUTE = "com.coremedia.livecontext.CONTEXT";

  /**
   * Utility class should not have a public default constructor.
   */
  private LiveContextContextHelper() {
  }

  /**
   * Stores a context in the request and makes it available.
   */
  public static void setContext(@Nonnull HttpServletRequest request, Context context) {
    request.setAttribute(CONTEXT_ATTRIBUTE, context);
  }

  /**
   * Retrieve the context. Will NOT create a context if it does not exist
   */
  @Nullable
  public static Context fetchContext(@Nonnull HttpServletRequest request) {
    return (Context) request.getAttribute(CONTEXT_ATTRIBUTE);
  }
}
