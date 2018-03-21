package com.coremedia.blueprint.elastic.social.cae.guid;

import com.coremedia.elastic.core.api.settings.Settings;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import static com.coremedia.elastic.core.test.Injection.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuidFilterTest {
  private String GUID;
  private GuidFilter filter;

  @Before
  public void initializeGuid() throws NoSuchAlgorithmException {
    Settings settings = mock(Settings.class);
    RSAKeyPair rsaKeyPair = RSAKeyPair.createFrom(settings);

    RSAPrivateKey privateKey = (RSAPrivateKey) rsaKeyPair.getPrivateKey();

    when(settings.getString("signCookie.publicKey")).thenReturn(
            Base64.encodeBase64String(rsaKeyPair.getPublicKey().getEncoded()));
    when(settings.getString("signCookie.privateKey")).thenReturn(
            Base64.encodeBase64String(privateKey.getEncoded()) + "#" + privateKey.getPrivateExponent().toString() + "#" + privateKey.getModulus().toString());

    filter = new GuidFilter(settings);
    GUID = filter.createGuid();
  }

  @Test
  public void extractGuid() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("guid", GUID)});
    ServletResponse response = mock(ServletResponse.class);
    FilterConfig filterConfig = mock(FilterConfig.class);
    FilterChain filterChain = mock(FilterChain.class);

    filter.init(filterConfig);
    filter.doFilter(request, response, filterChain);
    filter.destroy();

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void extractGuidCookie() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("elastic", GUID)});
    ServletResponse response = mock(ServletResponse.class);
    FilterConfig filterConfig = mock(FilterConfig.class);
    when(filterConfig.getInitParameter("cookieName")).thenReturn("elastic");
    FilterChain filterChain = mock(FilterChain.class);

    filter.init(filterConfig);
    assertEquals("elastic", filter.getCookieName());

    filter.doFilter(request, response, filterChain);
    filter.destroy();

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void cannotExtractGuid() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(new Cookie[0]);
    ServletResponse response = mock(ServletResponse.class);
    FilterConfig filterConfig = mock(FilterConfig.class);
    FilterChain filterChain = mock(FilterChain.class);

    filter.init(filterConfig);
    filter.doFilter(request, response, filterChain);
    filter.destroy();

    verify(filterChain).doFilter(request, response);
  }

  @Test
  public void filterSetsCookie() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getCookies()).thenReturn(new Cookie[0]);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterConfig filterConfig = mock(FilterConfig.class);
    FilterChain filterChain = mock(FilterChain.class);

    filter.init(filterConfig);
    filter.doFilter(request, response, filterChain);
    filter.destroy();

    verify(filterChain).doFilter(request, response);
    verify(response).addCookie(argThat(cookie -> "guid".equals(cookie.getName())));
  }

  @Test
  public void setGuid() throws ServletException, IOException {
    GuidFilter.setCurrentGuid("4711");
    assertEquals("4711", GuidFilter.getCurrentGuid());
  }

  @Test
  public void testValidateGuid() {
    String guid1 = filter.createGuid();
    String guid2 = filter.createGuid();
    assertNotSame(guid1, guid2);

    assertTrue(filter.validateGuid(guid1));
    assertTrue(filter.validateGuid(guid2));
  }

  @Test(expected = RuntimeException.class)
  public void testGetCurrentGuidInvalidKeys() throws NoSuchAlgorithmException {
    Settings settings = mock(Settings.class);
    when(settings.getString("signCookie.privateKey")).thenReturn("error");

    GuidFilter filter = new GuidFilter(settings);
    inject(filter, settings);

    filter.createGuid();
  }

  @Test
  public void testValidateGuidNoContent() {
    boolean isValid = filter.validateGuid("a");
    assertFalse(isValid);
  }

  @Test(expected = RuntimeException.class)
  public void testValidateGuidInvalidKeys() throws NoSuchAlgorithmException {
    Settings settings = mock(Settings.class);
    when(settings.getString("signCookie.publicKey")).thenReturn("error");

    GuidFilter filter = new GuidFilter(settings);
    inject(filter, settings);
    filter.createGuid();

    filter.validateGuid("1:2");
  }
}
