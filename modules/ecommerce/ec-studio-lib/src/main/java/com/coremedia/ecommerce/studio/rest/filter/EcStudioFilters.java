package com.coremedia.ecommerce.studio.rest.filter;

import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class EcStudioFilters {

  private static final String SITE_FILTER = "siteFilter";

  @Bean
  public FilterRegistrationBean siteFilterRegistration() {
    return RegistrationBeanBuilder
            .forFilter(siteFilter())
            .name(SITE_FILTER)
            .urlPatterns("/api/livecontext/*")
            .order(900)
            .build();
  }

  @Bean
  public Filter siteFilter() {
    return new SiteFilter();
  }
}
