package com.coremedia.blueprint.elastic.social.demodata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class DemoDataGeneratorConfiguration {
  private static final String TENANT_SCOPE_NAME = "tenant";

  @Bean
  @Scope(value = TENANT_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public UserGenerator userGenerator() {
    return new UserGenerator();
  }

  @Bean
  @Scope(value = TENANT_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public CommentGenerator commentGenerator() {
    return new CommentGenerator();
  }

  @Bean
  @Scope(value = TENANT_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public LikeGenerator likeGenerator() {
    return new LikeGenerator();
  }

  @Bean
  @Scope(value = TENANT_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public RatingGenerator ratingGenerator() {
    return new RatingGenerator();
  }

  @Bean
  @Scope(value = TENANT_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public DemoDataGenerator demoDataGenerator() {
    return new DemoDataGenerator();
  }

}
