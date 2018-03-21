package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import com.coremedia.springframework.context.support.RequiredPropertySourcesPlaceholderConfigurerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@PropertySource("classpath:/com/coremedia/livecontext/ecommerce/hybris/test-hybris-services.properties")
@Import(RequiredPropertySourcesPlaceholderConfigurerConfiguration.class)
public class TestConfiguration {

  @Bean
  public HybrisRestConnector hybrisRestConnector() {
    return new HybrisRestConnector();
  }

  /**
   * Spring test's don't convert ENUMS - production application contexts do
   */
  @Bean
  public static ConversionService conversionService() {
    return new DefaultConversionService();
  }

  @Bean
  @Profile("oauthconnector")
  public OAuthConnector oAuthConnector() {
    OAuthConnector connector = new OAuthConnector();
    connector.setHttpClient(HttpClientFactory.createHttpClient(true, true, 10, -1, -1, -1));
    return connector;
  }
}
