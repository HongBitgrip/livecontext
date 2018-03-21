package com.coremedia.blueprint.studio.blueprint.component.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnProperty(name = "elastic.core.persistence", havingValue = "memory")
@ImportResource({
        "classpath:/META-INF/coremedia/studio-in-memory-cap-list.xml",
        "classpath:/META-INF/coremedia/user-changes.xml",
        "classpath:/META-INF/coremedia/workflow-notifications.xml"
})
public class StudioInMemoryConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(StudioInMemoryConfiguration.class);

  @PostConstruct
  void initialize() {
    LOG.info("Initializing in-memory configuration for studio-webapp.");
  }
}
