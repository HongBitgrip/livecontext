package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.elastic.social.springsocial.CommunityUserSignInAdapter;
import com.coremedia.elastic.social.springsocial.CommunityUsersConnectionRepository;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
public class SpringSocialConfiguration {

  @Value("${application.url:}")
  private String applicationUrl;

  @Inject
  private ProviderConfiguration providerConfiguration;

  @Bean
  @Scope(value = "tenant", proxyMode = ScopedProxyMode.INTERFACES)
  public ConnectionFactoryLocator connectionFactoryLocator() {
    return new CustomConnectionFactoryRegistry(providerConfiguration);
  }

  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  public ConnectionRepository connectionRepository() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
    }

    return usersConnectionRepository().createConnectionRepository(authentication.getName());
  }

  @Bean
  public UsersConnectionRepository usersConnectionRepository() {
    return new CommunityUsersConnectionRepository();
  }

  @Bean
  public SignInAdapter communityUserSignInAdapter() {
    return new CommunityUserSignInAdapter();
  }

  @Bean
  public SignInAdapter signInAdapter() {
    CustomSignInAdapter signInAdapter = new CustomSignInAdapter();
    signInAdapter.setDelegate(communityUserSignInAdapter());
    return signInAdapter;
  }

  @Bean
  public ProviderSignInController providerSignInController() {
    CustomProviderSignInController controller = new CustomProviderSignInController(connectionFactoryLocator(),
            usersConnectionRepository(), signInAdapter());

    controller.setSignUpUrl("/servlet/signup");
    controller.setSignInUrl("/servlet/signin");
    if (isNotBlank(applicationUrl)) {
      controller.setApplicationUrl(applicationUrl);
    }

    return controller;
  }

  @Bean
  public SignUpController signUpController() {
    return new SignUpController();
  }

  @Bean
  public SignInFailedController signInFailedController() {
    return new SignInFailedController();
  }

  @Bean
  public ConnectController connectController() {
    CustomConnectController controller = new CustomConnectController(connectionFactoryLocator(), connectionRepository());

    if (isNotBlank(applicationUrl)) {
      controller.setApplicationUrl(applicationUrl);
    }

    return controller;
  }

  @Bean
  public HttpClient httpClientAutoRedirect() {
    return new DefaultHttpClient();
  }
}
