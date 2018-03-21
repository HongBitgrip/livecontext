package com.coremedia.blueprint.analytics.settings;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Named
@Produces("application/json")
@Path("alxservice")
public class AnalyticsSettingsResource {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyticsSettingsResource.class);

  @Inject
  private Collection<AnalyticsSettingsProvider> analyticsSettingsProviders;
  @Inject
  private ContentRepository contentRepository;

  @GET
  @Path("/{id}")
  public Map<String, String> getAlxUrl(@PathParam("id") String id) {

    final Content content = contentRepository.getContent(id);

    final Map<String, String> alxUrlMap = new HashMap<>(analyticsSettingsProviders.size());

    if (content != null && !content.isDeleted()) {
      for(AnalyticsSettingsProvider analyticsSettingsProvider : analyticsSettingsProviders) {
        String reportURL = null;
        try {
          reportURL = analyticsSettingsProvider.getReportUrlFor(content);
        } catch (RuntimeException e) {
          LOG.info("ignoring exception while creating report url for service {} and content {}: {}",
                  analyticsSettingsProvider.getServiceKey(),
                  content,
                  e.getMessage());
        }
        alxUrlMap.put(analyticsSettingsProvider.getServiceKey(), reportURL);
      }
    }

    return alxUrlMap;
  }

  @PostConstruct void initialize() {
    LOG.info("Found analytics providers {}", analyticsSettingsProviders);
  }

}
