package com.coremedia.livecontext.hybris.links;

import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * expose the library's default configuration
 */
@Configuration
@PropertySource("classpath:/META-INF/coremedia/hybris-cae-defaults.properties")
class LcEcommerceHybrisCaeConfiguration {

  @Bean
  FilterRegistrationBean hybrisPreviewTokenMarkerFilter() {
    return RegistrationBeanBuilder.forFilter(new HybrisPreviewTokenMarkerFilter())
            .order(20_100)
            .build();
  }
}


