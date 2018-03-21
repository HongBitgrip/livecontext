package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static com.coremedia.elastic.core.test.Injection.inject;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomConnectControllerTest {
  @InjectMocks
  private CustomConnectController connectController;

  @Mock
  private NativeWebRequest webRequest;

  @Mock
  private ConnectionFactoryLocator connectionFactoryLocator;

  @Mock
  private OAuth1ConnectionFactory<?> oAuth1ConnectionFactory;

  @Mock
  private OAuth2ConnectionFactory<?> oAuth2ConnectionFactory;

  @Mock
  private Authentication authentication;

  @Mock
  private Principal principal;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private LinkFormatter linkFormatter;

  @Mock
  private HttpServletRequest httpServletRequest;

  @SuppressWarnings({"unchecked"})
  @Test
  public void testOauth1Callback() {
    inject(connectController, communityUserService);
    inject(connectController, linkFormatter);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    when(authentication.getPrincipal()).thenReturn(principal);
    when(connectionFactoryLocator.getConnectionFactory("twitter")).thenReturn((ConnectionFactory) oAuth1ConnectionFactory);
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getRequestURI()).thenReturn("hello");
    when(httpServletRequest.getContextPath()).thenReturn("/");
    when(httpServletRequest.getServletPath()).thenReturn("");

    RedirectView redirectView = connectController.oauth1Callback("twitter", webRequest);

    assertNotNull(redirectView);
  }

  @SuppressWarnings({"unchecked"})
  @Test
  public void testOauth2Callback() {
    inject(connectController, communityUserService);
    inject(connectController, linkFormatter);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    when(authentication.getPrincipal()).thenReturn(principal);
    when(connectionFactoryLocator.getConnectionFactory("twitter")).thenReturn((ConnectionFactory) oAuth2ConnectionFactory);
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getRequestURI()).thenReturn("hello");
    when(httpServletRequest.getContextPath()).thenReturn("/");
    when(httpServletRequest.getServletPath()).thenReturn("");

    RedirectView redirectView = connectController.oauth2Callback("twitter", webRequest);

    assertNotNull(redirectView);
  }
}
