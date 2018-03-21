package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignUpControllerTest {
  @InjectMocks
  private SignUpController signUpController;

  @Mock
  private NativeWebRequest webRequest;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpSession httpSession;

  @Test
  public void testSignUp() {
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(httpSession.getAttribute("registerUrl")).thenReturn("/a/b");

    RedirectView result = signUpController.signUp(webRequest);

    assertNotNull(result);
    assertEquals("/a/b", result.getUrl());
  }

  @Test
  public void testSignUpNoUrl() {
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(webRequest.getContextPath()).thenReturn("/a");
    when(httpServletRequest.getSession()).thenReturn(httpSession);

    RedirectView result = signUpController.signUp(webRequest);

    assertNotNull(result);
    assertEquals("/a", result.getUrl());
  }
}
