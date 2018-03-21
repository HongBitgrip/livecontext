package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Initializes the StoreContextProvider according to the current request.
 */
public abstract class AbstractCommerceContextInterceptor extends HandlerInterceptorAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractCommerceContextInterceptor.class);

  public static final String QUERY_PARAMETER_WORKSPACE_ID = "workspaceId";

  private static final String DYNAMIC_FRAGMENT = "/" + UriConstants.Segments.PREFIX_DYNAMIC + "/";

  private static final String STORE_CONTEXT_INITIALIZED = AbstractCommerceContextInterceptor.class.getName()
          + "#storeContext.initialized";

  private SiteResolver siteResolver;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  private boolean preview;
  private boolean initUserContext = false;

  // --- configure --------------------------------------------------

  /**
   * Default: false
   */
  public void setInitUserContext(boolean initUserContext) {
    this.initUserContext = initUserContext;
  }

  @Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  @Required
  public void setSiteResolver(SiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

  // --- HandlerInterceptor -----------------------------------------

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String normalizedPath = normalizePath(request.getPathInfo());
    Site site = getSite(request, normalizedPath);

    // If site is null, we cannot help it here.  Silently do nothing.
    // It is up to the request handler to return 404.
    if (site == null) {
      return true;
    }

    // Initialize just once.
    // We're not testing against `Commerce.get()` in
    // case we're running behind `CommerceConnectionFilter`.
    if (request.getAttribute(STORE_CONTEXT_INITIALIZED) != null) {
      return true;
    }

    SiteHelper.setSiteToRequest(site, request);
    prepareCommerceConnection(site, request);

    return true;
  }

  private void prepareCommerceConnection(@Nonnull Site site, @Nonnull HttpServletRequest request) {
    try {
      Optional<CommerceConnection> commerceConnection = getCommerceConnectionWithConfiguredStoreContext(site, request);

      if (!commerceConnection.isPresent()) {
        return;
      }

      CurrentCommerceConnection.set(commerceConnection.get());

      request.setAttribute(STORE_CONTEXT_INITIALIZED, true);

      if (initUserContext) {
        initUserContext(commerceConnection.get(), request);
      }
    } catch (CommerceException e) {
      LOG.debug("No commerce connection found for site '{}'.", site.getName(), e);
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
          throws Exception {
    super.afterCompletion(request, response, handler, ex);

    CurrentCommerceConnection.remove();
  }

// --- abstract ---------------------------------------------------

  /**
   * Calculate a site from the request.
   * <p/>
   *
   * @param request        the request
   * @param normalizedPath is the URL path w/o a dynamic fragment prefix
   * @return a Site or null
   */
  @Nullable
  protected abstract Site getSite(HttpServletRequest request, String normalizedPath);

  // --- hook points and utils for extending classes ----------------

  public SiteResolver getSiteResolver() {
    return siteResolver;
  }

  protected boolean isPreview() {
    return preview;
  }

  // --- basics, suitable for most extending classes ----------------

  @Nonnull
  protected Optional<CommerceConnection> getCommerceConnectionWithConfiguredStoreContext(
          @Nonnull Site site, @Nonnull HttpServletRequest request) {
    Optional<CommerceConnection> connection = commerceConnectionInitializer.findConnectionForSite(site);

    // The commerce connection is supposed to be prototype-scoped (i.e.
    // a new instance is created every time the bean is requested).
    // Thus, fiddling with it here should be fine (although it would be
    // better to avoid that).

    if (!connection.isPresent()) {
      LOG.debug("Site '{}' has no commerce connection.", site.getName());
      return Optional.empty();
    }

    if (preview) {
      StoreContext originalStoreContext = connection.get().getStoreContext();
      StoreContextProvider storeContextProvider = connection.get().getStoreContextProvider();

      StoreContextBuilder storeContextBuilder = storeContextProvider.buildContext(originalStoreContext);

      StoreContext clonedStoreContext = prepareStoreContextForPreview(request, storeContextBuilder)
              .build();

      connection.get().setStoreContext(clonedStoreContext);
    }

    return connection;
  }

  @Nonnull
  @SuppressWarnings("AssignmentToMethodParameter")
  private static StoreContextBuilder prepareStoreContextForPreview(@Nonnull HttpServletRequest request,
                                                                   @Nonnull StoreContextBuilder storeContextBuilder) {
    // search for an existing workspace param and put it in the store context
    String workspaceId = request.getParameter(QUERY_PARAMETER_WORKSPACE_ID);
    storeContextBuilder = storeContextBuilder.withWorkspaceId(workspaceId);

    String previewDate = request.getParameter(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE);
    if (previewDate != null) {
      storeContextBuilder = storeContextBuilder.withPreviewDate(previewDate);
    }

    return storeContextBuilder;
  }

  /**
   * Sets the user context to the user context provider.
   * You will need this if you want to do a call for a user.
   */
  protected void initUserContext(@Nonnull CommerceConnection commerceConnection, @Nonnull HttpServletRequest request) {
    try {
      UserContext userContext = commerceConnection.getUserContextProvider().createContext(request);
      commerceConnection.setUserContext(userContext);
    } catch (CommerceException e) {
      LOG.warn("Error creating commerce user context: {}", e.getMessage(), e);
    }
  }

  /**
   * Cut off a possible dynamic prefix
   */
  @Nullable
  @VisibleForTesting
  static String normalizePath(@Nullable String urlPath) {
    return urlPath != null
            && urlPath.startsWith(DYNAMIC_FRAGMENT) ? urlPath.substring(DYNAMIC_FRAGMENT.length() - 1) : urlPath;
  }
}
