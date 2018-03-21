package com.coremedia.blueprint.feeder.populate;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.ContentFeedableAspect;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.TextParameters;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class SiteFeedablePopulator implements FeedablePopulator<Content> {

  private static final Logger LOG = LoggerFactory.getLogger(SiteFeedablePopulator.class);

  private static final String SITE_SOLR_INDEX_FIELD = "site";

  private SitesService sitesService;

  //--- Spring configuration --
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Override
  public void populate(MutableFeedable feedable, Content content) {
    if (feedable == null || content == null) {
      throw new IllegalArgumentException("feedable and content must not be null");
    }

    // set the Content's site if this is a full index document update (add/replace) where all fields need to be set
    // or if it's a partial update and the content's path has changed to a possibly different site
    if (!feedable.isUpdating(ContentFeedableAspect.PATH)) {
      return;
    }

    String siteName = getSiteName(content);
    Map<String, ?> parameters = TextParameters.NONE.asMap();

    feedable.setStringElement(SITE_SOLR_INDEX_FIELD, siteName, parameters);
  }

  @Nullable
  private String getSiteName(@Nonnull Content content) {
    Optional<Site> site = sitesService.getContentSiteAspect(content).findSite();
    String siteName = site.map(Site::getName).orElse(null);
    LOG.debug("Site for {}: {}", content, siteName);
    return siteName;
  }
}
