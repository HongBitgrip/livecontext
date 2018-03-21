package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.util.List;

/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the review widget to rendered views.
 */
@Named
public class ReviewsViewHookEventListener extends AbstractESViewHookEventListener {

  @Override
  protected boolean isEnabled(@Nonnull ElasticSocialConfiguration elasticSocialConfiguration) {
    return elasticSocialConfiguration.isReviewingEnabled();
  }

  @Nonnull
  @Override
  protected List<String> getWhitelistTypes(@Nonnull ElasticSocialConfiguration elasticSocialConfiguration) {
    return elasticSocialConfiguration.getReviewDocumentTypes();
  }

  @Nullable
  @Override
  protected ReviewsResult getContribution(@Nonnull Object target) {
    return new ReviewsResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
