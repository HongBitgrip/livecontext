package com.coremedia.blueprint.elastic.social.cae.util;


import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetTransformer;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.objectserver.beans.ContentBean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

@Named
class ContentBeanTransformer implements ContributionTargetTransformer<ContentBean, ContentWithSite> {
  @Inject
  private SitesService sitesService;

  @Nonnull
  private Content getContent(@Nonnull ContentBean target) {
    return target.getContent();
  }

  @Nullable
  private Site getSiteForContent(@Nonnull Content content) {
    return sitesService.getContentSiteAspect(content).getSite();
  }

  @Nonnull
  @Override
  public ContentWithSite transform(@Nonnull ContentBean target) {
    final Content content = getContent(target);
    return new ContentWithSite(content, getSiteForContent(content));
  }

  @Nullable
  @Override
  public Site getSite(@Nonnull ContentBean target) {
    return getSiteForContent(target.getContent());
  }

  @Override
  @Nonnull
  public Class<ContentBean> getType() {
    return ContentBean.class;
  }
}