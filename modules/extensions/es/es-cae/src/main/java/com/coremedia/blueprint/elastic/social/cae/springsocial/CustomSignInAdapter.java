package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.coremedia.blueprint.elastic.social.cae.springsocial.Requests.getServletRequest;
import static org.slf4j.LoggerFactory.getLogger;

public class CustomSignInAdapter implements SignInAdapter {

  private static final Logger LOG = getLogger(CustomSignInAdapter.class);

  private SignInAdapter delegate;

  public void setDelegate(SignInAdapter delegate) {
    this.delegate = delegate;
  }

  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    delegate.signIn(userId, connection, request);

    HttpServletRequest httpServletRequest = getServletRequest(request);
    HttpSession session = httpServletRequest.getSession();

    String nextUrl = getAndRemoveAttribute(session, "nextUrl");

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      return removeContextPath(nextUrl, httpServletRequest);
    } else {
      session.setAttribute("providerLogin.messageKey", "error");

      String loginUrl = getAndRemoveAttribute(session, "loginUrl");
      if (StringUtils.isBlank(loginUrl)) {
        String registerUrl = getAndRemoveAttribute(session, "registerUrl");
        return appendParameter(removeContextPath(registerUrl, httpServletRequest), "next=" + nextUrl);
      }

      return appendParameter(removeContextPath(loginUrl, httpServletRequest), "next=" + nextUrl);
    }
  }

  private static String getAndRemoveAttribute(HttpSession session, String name) {
    String nextUrl = (String) session.getAttribute(name);
    session.removeAttribute(name);
    return nextUrl;
  }

  private static String removeContextPath(String url, HttpServletRequest servletRequest) {
    String contextPath = servletRequest.getContextPath();
    LOG.debug("Remove context path from nextUrl: {}, context path: {}", url, contextPath);
    return url.startsWith(contextPath) ? url.substring(contextPath.length()) : url;
  }

  private static String appendParameter(String url, String parameter) {
    return url + (url.indexOf('?') > -1 ? "&" : "?") + parameter;
  }
}
