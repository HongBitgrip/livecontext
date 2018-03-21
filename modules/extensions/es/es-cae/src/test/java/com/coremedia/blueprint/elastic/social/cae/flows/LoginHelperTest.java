package com.coremedia.blueprint.elastic.social.cae.flows;

import com.coremedia.blueprint.elastic.social.cae.springsocial.SpringSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginHelperTest {
  private String name = "test";
  private String password = "secret";

  @InjectMocks
  private LoginHelper loginHelper;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private LoginForm loginForm;

  @Mock
  private RequestContext requestContext;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserPrincipal principal;

  @Mock
  private Authentication authentication;

  @Mock
  private ElasticSocialUserHelper userFilter;

  @Mock
  private DefaultMessageContext messageContext;

  @Mock
  private SharedAttributeMap sessionMap;

  @Mock
  private ExternalContext externalContext;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private SessionAuthenticationStrategy sessionAuthenticationStrategy;

  @Mock
  private SecurityContextRepository securityContextRepository;

  @Mock
  private ParameterMap parameterMap;

  @Mock
  private HttpSession session;

  @Mock
  private ProviderSignInAttempt providerSignInAttempt;

  @Mock
  private Connection connection;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private SpringSocialConfiguration springSocialConfiguration;

  @Before
  public void setup() {
    when(loginForm.getName()).thenReturn(name);
    when(loginForm.getPassword()).thenReturn(password);
    when(requestContext.getMessageContext()).thenReturn(messageContext);
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getNativeRequest()).thenReturn(request);
    when(externalContext.getNativeResponse()).thenReturn(response);
  }

  @Test
  public void testLoginSuccess() {
    when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(principal);
    when(userFilter.getUser(principal)).thenReturn(communityUser);

    boolean result = loginHelper.login(loginForm, requestContext);

    assertTrue(result);
  }

  @Test
  public void testLoginTwiceNotAllowed() {
    when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(principal);
    when(userFilter.getUser(principal)).thenReturn(communityUser);
    doThrow(new SessionAuthenticationException("intended")).when(sessionAuthenticationStrategy).onAuthentication(authentication, request, response);

    boolean result = loginHelper.login(loginForm, requestContext);

    assertFalse(result);
    verify(messageContext).addMessage(any(MessageResolver.class));
    verify(sessionAuthenticationStrategy).onAuthentication(authentication, request, response);
  }

  @Test
  public void testLoginAuthenticationNull() {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(name, password);
    when(authenticationManager.authenticate(authenticationToken)).thenReturn(null);

    boolean result = loginHelper.login(loginForm, requestContext);

    assertFalse(result);
    verify(authenticationManager).authenticate(authenticationToken);
    verify(messageContext).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testLoginWithException() {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(name, password);
    when(authenticationManager.authenticate(authenticationToken)).thenThrow(new BadCredentialsException("fail"));

    boolean result = loginHelper.login(loginForm, requestContext);

    assertFalse(result);
    verify(authenticationManager).authenticate(authenticationToken);
    verify(messageContext).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testLoginInvalidPrincipal() {
    when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password))).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn("");

    boolean result = loginHelper.login(loginForm, requestContext);

    assertFalse(result);
  }

  @Test
  public void userNotLoggedIn() {
    SecurityContextHolder.clearContext();

    boolean result = loginHelper.isLoggedIn();

    assertFalse(result);
  }

  @Test
  public void userLoggedIn() {
    when(userFilter.getLoggedInUser()).thenReturn(communityUser);

    boolean result = loginHelper.isLoggedIn();

    assertTrue(result);
  }

  @Test
  public void preProcessWithProviderConnection() {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(ProviderSignInAttempt.class.getName())).thenReturn(providerSignInAttempt);
    when(providerSignInAttempt.getConnection(any(ConnectionFactoryLocator.class))).thenReturn(connection);
    loginHelper.preProcess(requestContext);

    verify(session).getAttribute(ProviderSignInAttempt.class.getName());
    verify(session).removeAttribute(ProviderSignInAttempt.class.getName());
  }

  @Test
  public void preProcessWithoutProviderConnection() {
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(ProviderSignInAttempt.class.getName())).thenReturn(null);
    loginHelper.preProcess(requestContext);

    verify(session).getAttribute(ProviderSignInAttempt.class.getName());
    verify(session, never()).removeAttribute(ProviderSignInAttempt.class.getName());
  }

  @Test
  public void testPostProcessProviderConnection() {
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getSessionMap()).thenReturn(sessionMap);
    when(requestContext.getRequestParameters()).thenReturn(parameterMap);

    loginHelper.postProcessProviderLogin(requestContext);

    verify(messageContext, never()).addMessage(any());
    verify(parameterMap).contains("error");
  }

  @Test
  public void testPostProcessProviderConnectionDuplicateConnection() {
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getSessionMap()).thenReturn(sessionMap);
    when(sessionMap.remove("providerLogin.messageKey")).thenReturn("some.key");
    when(requestContext.getRequestParameters()).thenReturn(parameterMap);

    loginHelper.postProcessProviderLogin(requestContext);

    verify(messageContext).addMessage(any());
    verify(parameterMap).contains("error");
  }


  @Test
  public void testPostProcessProviderConnectionError() {
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getSessionMap()).thenReturn(sessionMap);
    when(requestContext.getRequestParameters()).thenReturn(parameterMap);
    when(parameterMap.contains("error")).thenReturn(true);

    loginHelper.postProcessProviderLogin(requestContext);

    verify(messageContext).addMessage(any());
    verify(parameterMap).contains("error");
  }
}
