package com.coremedia.blueprint.elastic.social.cae.flows;

import com.coremedia.blueprint.elastic.social.cae.springsocial.SpringSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper.addErrorMessage;
import static com.coremedia.common.logging.BaseMarker.AUTHENTICATION;

@Named
public class LoginHelper {
  private static final Logger LOG = LoggerFactory.getLogger(LoginHelper.class);

  @Inject
  private AuthenticationManager authenticationManager;

  @Inject
  private ElasticSocialUserHelper elasticSocialUserHelper;

  @Inject
  private SessionAuthenticationStrategy sessionAuthenticationStrategy;

  @Inject
  private SecurityContextRepository securityContextRepository;

  @Inject
  private SpringSocialConfiguration springSocialConfiguration;

  private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

  public void preProcess(RequestContext context) {
    ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils(springSocialConfiguration.connectionFactoryLocator(), springSocialConfiguration.usersConnectionRepository());

    // If a user does not finish a provider registration, the provider connection must be removed
    // from the session. Otherwise the registration form is always initialized with the provider data
    // and the user cannot register without provider or with another provider.
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
    if (providerSignInUtils.getConnectionFromSession(servletRequestAttributes) != null) {
      servletRequestAttributes.removeAttribute(ProviderSignInAttempt.class.getName(), RequestAttributes.SCOPE_SESSION);
    }
  }

  public boolean login(LoginForm form, RequestContext context) {
    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(form.getName(), form.getPassword());
    return authenticate(authenticationToken, context);
  }

  public boolean authenticate(Authentication authenticationToken, RequestContext context) {
    try {
      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      if (authentication == null) {
        LOG.error("Could not get user, authentication is null");
        addErrorMessage(context, WebflowMessageKeys.LOGIN_FORM_ERROR);
        return false;
      }

      CommunityUser user = elasticSocialUserHelper.getUser(authentication.getPrincipal());
      if (user == null) {
        LOG.error(AUTHENTICATION, "Could not get user for principal {}", authentication.getPrincipal());
        addErrorMessage(context, WebflowMessageKeys.LOGIN_FORM_ERROR);
        return false;
      }
      SecurityContextHolder.getContext().setAuthentication(authentication);

      if (!authenticationTrustResolver.isAnonymous(authentication)) {
        // The user has been authenticated during the current request, so call the session strategy
        HttpServletRequest nativeRequest = (HttpServletRequest) context.getExternalContext().getNativeRequest();
        HttpServletResponse nativeResponse = (HttpServletResponse) context.getExternalContext().getNativeResponse();
        try {
          sessionAuthenticationStrategy.onAuthentication(authentication, nativeRequest, nativeResponse);
        } catch (SessionAuthenticationException e) {
          // The session strategy can reject the authentication
          LOG.info(AUTHENTICATION, "SessionAuthenticationStrategy rejected the authentication object", e);
          SecurityContextHolder.clearContext();

          //... add error message
          addErrorMessage(context, WebflowMessageKeys.LOGIN_FORM_ERROR);
          return false;
        }
        // Eagerly save the security context to make it available for any possible re-entrant
        // requests which may occur before the current request completes. SEC-1396.
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), nativeRequest, nativeResponse);
      }

      UserContext.setUser(user);
      return true;
    } catch (AuthenticationException e) {
      LOG.debug(AUTHENTICATION, "Login failed", e);
      addErrorMessage(context, WebflowMessageKeys.LOGIN_GENERAL_ERROR);
      return false;
    }
  }

  public boolean isLoggedIn() {
    return elasticSocialUserHelper.getLoggedInUser() != null;
  }

  public void postProcessProviderLogin(RequestContext context) {
    if (context.getRequestParameters().contains("error")) {
      addErrorMessage(context, WebflowMessageKeys.LOGIN_PROVIDER_ERROR);
    }
    SharedAttributeMap sessionMap = context.getExternalContext().getSessionMap();
    String messageKey = (String) sessionMap.remove("providerLogin.messageKey");
    if (messageKey != null) {
      addErrorMessage(context, WebflowMessageKeys.LOGIN_NAME_DEACTIVATED_ERROR);
    }
  }
}
