package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.controller.CommentsResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.util.List;

/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the comments widget to rendered views.
 */
@Named
public class CommentsViewHookEventListener extends AbstractESViewHookEventListener {

  @Override
  protected boolean isEnabled(@Nonnull ElasticSocialConfiguration elasticSocialConfiguration) {
    return elasticSocialConfiguration.isCommentingEnabled();
  }

  @Nullable
  @Override
  protected List<String> getWhitelistTypes(@Nonnull ElasticSocialConfiguration elasticSocialConfiguration) {
    return elasticSocialConfiguration.getCommentDocumentTypes();
  }

  @Override
  protected CommentsResult getContribution(@Nonnull Object target) {
    return new CommentsResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
