package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

import static com.coremedia.blueprint.elastic.social.cae.springsocial.Requests.getServletRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
@RequestMapping("/signup")
public class SignUpController {

  @RequestMapping(method = RequestMethod.GET)
  public RedirectView signUp(NativeWebRequest request) {
    HttpSession session = getServletRequest(request).getSession();

    String registerUrl = (String) session.getAttribute("registerUrl");

    return new RedirectView(isBlank(registerUrl) ? request.getContextPath() : registerUrl);
  }
}
