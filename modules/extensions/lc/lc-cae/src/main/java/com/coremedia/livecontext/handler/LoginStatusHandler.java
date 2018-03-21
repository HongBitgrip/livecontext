package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.web.links.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static java.util.Objects.requireNonNull;

@Link
@Controller
public class LoginStatusHandler {

  private static final String STATUS = '/' + PREFIX_DYNAMIC + "/loginstatus";
  private static final Logger LOG = LoggerFactory.getLogger(LoginStatusHandler.class);

  private final UserSessionService userSessionService;
  private final LiveContextSiteResolver liveContextSiteResolver;
  private final CommerceConnectionInitializer commerceConnectionInitializer;

  @SuppressWarnings("WeakerAccess") // used in Spring XML
  public LoginStatusHandler(@Nonnull UserSessionService userSessionService,
                            @Nonnull LiveContextSiteResolver liveContextSiteResolver,
                            @Nonnull CommerceConnectionInitializer commerceConnectionInitializer) {
    this.userSessionService = requireNonNull(userSessionService);
    this.liveContextSiteResolver = requireNonNull(liveContextSiteResolver);
    this.commerceConnectionInitializer = requireNonNull(commerceConnectionInitializer);
  }

  @GetMapping(value = STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> handleStatus(@RequestParam String storeId,
                                                          @RequestParam Locale locale,
                                                          @Nonnull HttpServletRequest request) {
    return withConnection(storeId, locale, request, () -> new ResponseEntity<>(status(), HttpStatus.OK));
  }

  private Map<String, Object> status() {
    return Collections.singletonMap("loggedIn", isLoggedIn());
  }

  private boolean isLoggedIn() {
    try {
      return userSessionService.isLoggedIn();
    } catch (CredentialExpiredException e) {
      return false;
    }
  }

  /**
   * Calls the given handler with {@link CurrentCommerceConnection} set for given store and locale.
   *
   * <p>This method ensures that the current commerce connection is restored to its original value when it returns.
   *
   * @param storeId store ID
   * @param locale locale
   * @param handler handler that is called with commerce connection, if a connection was found for store ID and locale
   * @param <T> ResponseEntity body type
   * @return result from given handler, or entity with {@link HttpStatus#NOT_FOUND} if no connection was found.
   */
  private <T> ResponseEntity<T> withConnection(String storeId,
                                               Locale locale,
                                               @Nonnull HttpServletRequest request,
                                               Supplier<ResponseEntity<T>> handler) {
    Optional<CommerceConnection> connection = findConnection(storeId, locale);
    if (!connection.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Optional<CommerceConnection> oldConnection = CurrentCommerceConnection.find();
    try {
      CurrentCommerceConnection.set(connection.get());
      initUserContext(connection.get(), request);
      return handler.get();
    } finally {
      oldConnection.ifPresent(CurrentCommerceConnection::set);
      if (!oldConnection.isPresent()) {
        CurrentCommerceConnection.remove();
      }
    }
  }

  private void initUserContext(@Nonnull CommerceConnection commerceConnection, @Nonnull HttpServletRequest request) {
    try {
      UserContext userContext = commerceConnection.getUserContextProvider().createContext(request);
      commerceConnection.setUserContext(userContext);
    } catch (CommerceException e) {
      LOG.warn("Error creating commerce user context: {}", e.getMessage(), e);
    }
  }

  private Optional<CommerceConnection> findConnection(String storeId, Locale locale) {
    Site site = liveContextSiteResolver.findSiteFor(storeId, locale);
    return site == null
            ? Optional.empty()
            : commerceConnectionInitializer.findConnectionForSite(site);
  }

  // --- Link building ----------------------------------------------------------------------

  @Link(type = LinkType.class, uri = STATUS)
  public UriComponents buildLink(LinkType linkType, UriComponentsBuilder uriTemplate) {
    StoreContext storeContext = CurrentCommerceConnection.get().getStoreContext();
    String storeId = storeContext.getStoreId();
    Locale locale = storeContext.getLocale();
    uriTemplate.queryParam("storeId", storeId);
    uriTemplate.queryParam("locale", locale.toLanguageTag());
    return uriTemplate.build();
  }

  public enum LinkType {
    STATUS
  }
}
