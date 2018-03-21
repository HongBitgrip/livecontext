package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.support.URIBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

import static com.coremedia.blueprint.elastic.social.cae.springsocial.Requests.getServletRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
@RequestMapping("/signin")
public class SignInFailedController {

  private static final Logger LOG = LoggerFactory.getLogger(SignInFailedController.class);

  @RequestMapping(method = RequestMethod.GET)
  public RedirectView signIn(NativeWebRequest request) {
    HttpSession session = getServletRequest(request).getSession();

    LOG.warn("Sign in with provider {} failed. Fix configuration.", session.getAttribute("providerId"));

    String loginUrl = (String) session.getAttribute("loginUrl");
    String registerUrl = (String) session.getAttribute("registerUrl");

    String nextUrl = request.getContextPath();
    if (!isBlank(loginUrl)) {
      nextUrl = URIBuilder.fromUri(loginUrl).queryParam("error", "provider").build().toString();
    } else if (!isBlank(registerUrl)) {
      nextUrl = URIBuilder.fromUri(registerUrl).queryParam("error", "provider").build().toString();
    }

    return new RedirectView(nextUrl);
  }
}
