package com.coremedia.blueprint.livecontext.ecommerce.filter;

import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Set up filter proxy for commerce connection filter
 */
@Configuration
public class LcCommonWebApplicationFilters {

  private static final String COMMERCE_CONNECTION_FILTER = "commerceConnectionFilter";

  @Bean
  public FilterRegistrationBean createRegistrationBeans() {
    return RegistrationBeanBuilder
            .forFilter(commerceConnectionFilter())
            .name(COMMERCE_CONNECTION_FILTER)
            .order(2000)
            .build();
  }

  @Bean
  public Filter commerceConnectionFilter() {
    return new CommerceConnectionFilter();
  }
}
