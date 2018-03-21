package com.coremedia.blueprint.analytics.elastic.tasks;

import com.coremedia.blueprint.analytics.elastic.retrieval.AnalyticsServiceProvider;
import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * A task to retrieve the number of page views from the configured analytics service providers.
 */
@Named
public class FetchPageViewHistoryTask extends AbstractRootContentProcessingTask {

  private final FetchPageViewHistoryRootContentProcessor fetchPageViewHistoryRootContentProcessor;

  @Inject
  public FetchPageViewHistoryTask(FetchPageViewHistoryRootContentProcessor fetchPageViewHistoryRootContentProcessor, RootContentProcessingTaskHelper rootContentProcessingTaskHelper) {
    super(rootContentProcessingTaskHelper);
    this.fetchPageViewHistoryRootContentProcessor = fetchPageViewHistoryRootContentProcessor;
  }

  @Override
  void processRootNavigation(@Nonnull Content rootNavigation, @Nonnull Map<String, Object> serviceSettings, @Nonnull AnalyticsServiceProvider analyticsServiceProvider) {
    fetchPageViewHistoryRootContentProcessor.processRootContent(rootNavigation, serviceSettings, analyticsServiceProvider);
  }
}
