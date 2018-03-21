package com.coremedia.blueprint.component.cae;

import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.cae.filter.SiteFilter;
import com.coremedia.blueprint.cae.filter.UnknownMimetypeCharacterEncodingFilter;
import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;

@Configuration
public class CaeBaseComponentConfiguration {

  public static final int ORDER_SITE_FILTER = 100;

  @Bean
  public Filter characterEncodingFilter() {
    CharacterEncodingFilter filter = new UnknownMimetypeCharacterEncodingFilter();
    filter.setEncoding(StandardCharsets.UTF_8.toString());
    filter.setForceEncoding(true);
    return filter;
  }

  @Bean
  public FilterRegistrationBean characterEncodingFilterRegistration(Filter characterEncodingFilter) {
    return RegistrationBeanBuilder.forFilter(characterEncodingFilter)
            .order(Ordered.HIGHEST_PRECEDENCE)
            .build();
  }

  @Bean
  public Filter deviceResolverRequestFilter() {
    return new DeviceResolverRequestFilter();
  }

  @Bean
  public FilterRegistrationBean deviceResolverRequestFilterRegistration(Filter deviceResolverRequestFilter) {
    return RegistrationBeanBuilder.forFilter(deviceResolverRequestFilter)
            .order(Ordered.HIGHEST_PRECEDENCE + 10)
            .build();
  }

  @Bean
  public Filter siteFilter(SiteResolver siteResolver) {
    SiteFilter siteFilter = new SiteFilter();
    siteFilter.setSiteResolver(siteResolver);
    return siteFilter;
  }

  @Bean
  public FilterRegistrationBean siteFilterRegistration(Filter siteFilter) {
    return RegistrationBeanBuilder.forFilter(siteFilter)
            .urlPatterns("/servlet/*")
            .order(ORDER_SITE_FILTER)
            .build();
  }

}
