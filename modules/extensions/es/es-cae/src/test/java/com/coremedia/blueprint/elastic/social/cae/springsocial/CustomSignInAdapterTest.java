package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomSignInAdapterTest {
  @InjectMocks
  private CustomSignInAdapter signInAdapter;

  @Mock
  private Connection<?> connection;

  @Mock
  private NativeWebRequest webRequest;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpSession httpSession;

  @Mock
  private AuthenticationEventPublisher authenticationEventPublisher;

  @Mock
  private SignInAdapter delegate;

  @Mock
  private Authentication authentication;

  @Test
  public void testSignIn() {
    SecurityContextHolder.getContext().setAuthentication(authentication);
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(httpServletRequest.getContextPath()).thenReturn("/a");
    when(httpSession.getAttribute("nextUrl")).thenReturn("/a/b");

    String result = signInAdapter.signIn("4711", connection, webRequest);

    assertEquals("/b", result);
    verify(delegate).signIn("4711", connection, webRequest);
  }

  @Test
  public void testSignInNextUrlWithoutContextPath() {
    SecurityContextHolder.getContext().setAuthentication(authentication);
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(httpServletRequest.getContextPath()).thenReturn("/a");
    when(httpSession.getAttribute("nextUrl")).thenReturn("/b");

    String result = signInAdapter.signIn("4711", connection, webRequest);

    assertEquals("/b", result);
    verify(delegate).signIn("4711", connection, webRequest);
  }

  @Test
  public void testSignInDeniedLoginUrl() {
    SecurityContextHolder.clearContext();
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(httpServletRequest.getContextPath()).thenReturn("/c");
    when(httpSession.getAttribute("nextUrl")).thenReturn("/a/b");
    when(httpSession.getAttribute("loginUrl")).thenReturn("/c/d");

    String result = signInAdapter.signIn("4711", connection, webRequest);

    assertEquals("/d?next=/a/b", result);
    verify(delegate).signIn("4711", connection, webRequest);
  }

  @Test
  public void testSignInDeniedRegisterUrl() {
    SecurityContextHolder.clearContext();
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(httpServletRequest.getContextPath()).thenReturn("/e");
    when(httpSession.getAttribute("nextUrl")).thenReturn("/a/b");
    when(httpSession.getAttribute("loginUrl")).thenReturn("");
    when(httpSession.getAttribute("registerUrl")).thenReturn("/e/f");

    String result = signInAdapter.signIn("4711", connection, webRequest);

    assertEquals("/f?next=/a/b", result);
    verify(delegate).signIn("4711", connection, webRequest);
  }
}
