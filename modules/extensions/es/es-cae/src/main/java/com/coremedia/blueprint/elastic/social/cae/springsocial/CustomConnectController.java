package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

import static com.coremedia.blueprint.elastic.social.cae.springsocial.Requests.getServletRequest;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * Patched version of ConnectController for spring 5 compatibility and redirecting to the
 * Elastic Social user profile
 */
public class CustomConnectController extends ConnectController {

  private final UrlPathHelper urlPathHelper = new UrlPathHelper();

  @Inject
  private LinkFormatter linkFormatter;

  @Inject
  private CommunityUserService communityUserService;

  public CustomConnectController(ConnectionFactoryLocator connectionFactoryLocator,
                                 ConnectionRepository connectionRepository) {
    super(connectionFactoryLocator, connectionRepository);
  }

  @Override
  public RedirectView oauth1Callback(@PathVariable String providerId, NativeWebRequest request) {
    super.oauth1Callback(providerId, request);

    return createRedirectView(request);
  }

  @Override
  public RedirectView oauth2Callback(@PathVariable String providerId, NativeWebRequest request) {
    super.oauth2Callback(providerId, request);

    return createRedirectView(request);
  }

  @Override
  protected RedirectView connectionStatusRedirect(String providerId, NativeWebRequest request) {
    HttpServletRequest servletRequest = getServletRequest(request);

    String path = "/connect/" + providerId + getPathExtension(servletRequest);
    if (prependServletPath(servletRequest)) {
      path = servletRequest.getServletPath() + path;
    }

    return new RedirectView(path, true);
  }

  private boolean prependServletPath(@Nonnull HttpServletRequest request) {
    return !urlPathHelper.getPathWithinServletMapping(request).equals("");
  }

  /*
   * Determines the path extension, if any.
   * Returns the extension, including the period at the beginning, or an empty string if there is no extension.
   * This makes it possible to append the returned value to a path even if there is no extension.
   */
  @Nonnull
  private static String getPathExtension(@Nonnull HttpServletRequest request) {
    return nullToEmpty(UriUtils.extractFileExtension(request.getRequestURI()));
  }

  @Nonnull
  private RedirectView createRedirectView(@Nonnull NativeWebRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Principal principal = (Principal) authentication.getPrincipal();
    CommunityUser user = communityUserService.getUserById(principal.getName());

    HttpServletRequest httpServletRequest = (HttpServletRequest) request.getNativeRequest();
    HttpServletResponse httpServletResponse = (HttpServletResponse) request.getNativeResponse();

    String url = linkFormatter.formatLink(user, null, httpServletRequest, httpServletResponse, false);

    return new RedirectView(url);
  }
}
