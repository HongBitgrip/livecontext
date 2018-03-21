package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.api.settings.Settings;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class SpringSocialConfigurationTest {
  @Test
  public void test() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            SpringSocialConfiguration.class,
            SpringSocialConfigurationTestConfiguration.class
    );

    assertNotNull(applicationContext.getBean(SignUpController.class));
  }

  @Configuration
  public static class SpringSocialConfigurationTestConfiguration {
    @Bean
    public LinkFormatter linkFormatter() {
      return mock(LinkFormatter.class);
    }

    @Bean
    public CommunityUserService communityUserService() {
      return mock(CommunityUserService.class);
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher() {
      return mock(AuthenticationEventPublisher.class);
    }

    @Bean
    public Settings settings() {
      return mock(Settings.class);
    }

    @Bean
    public TenantService tenantService() {
      return mock(TenantService.class);
    }

    @Bean
    public ProviderConfiguration providerConfiguration() {
      return mock(ProviderConfiguration.class);
    }

    @Bean
    public SettingsService settingsService() {
      return mock(SettingsService.class);
    }

    @Bean
    public SitesService sitesService(){
      return mock(SitesService.class);
    }
  }
}
