package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

import static com.coremedia.blueprint.elastic.social.cae.springsocial.Requests.getServletRequest;

public class CustomProviderSignInController extends ProviderSignInController {

  /**
   * Creates a new provider sign-in controller.
   *
   * @param connectionFactoryLocator  the locator of
   *                                  {@link org.springframework.social.connect.ConnectionFactory connection factories}
   *                                  used to support provider sign-in.
   *                                  Note: this reference should be a serializable proxy to a singleton-scoped target
   *                                  instance. This is because
   *                                  {@link org.springframework.social.connect.web.ProviderSignInAttempt} are
   *                                  session-scoped objects that hold ConnectionFactoryLocator references. If these
   *                                  references cannot be serialized, NotSerializableExceptions can occur at runtime.
   * @param usersConnectionRepository the global store for service provider connections across all users.
   *                                  Note: this reference should be a serializable proxy to a singleton-scoped target
   *                                  instance.
   * @param signInAdapter             handles user sign-in
   */
  public CustomProviderSignInController(ConnectionFactoryLocator connectionFactoryLocator,
                                        UsersConnectionRepository usersConnectionRepository,
                                        SignInAdapter signInAdapter) {
    super(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
  }

  @Override
  public RedirectView signIn(@PathVariable String providerId, NativeWebRequest request) {
    HttpSession session = getServletRequest(request).getSession();

    session.setAttribute("nextUrl", request.getParameter("nextUrl"));
    session.setAttribute("registerUrl", request.getParameter("registerUrl"));
    session.setAttribute("loginUrl", request.getParameter("loginUrl"));
    session.setAttribute("providerId", providerId);

    return super.signIn(providerId, request);
  }
}
