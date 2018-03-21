package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.resolver.SearchTermExternalReferenceResolver;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;

/**
 * Extension that create custom links on CMChannel documents.
 */
class SearchLandingPagesLinkBuilderHelper {
  private String keywordsProperty;
  private String segmentPath;
  private TreeRelation<Content> navigationTreeRelation;

  boolean isSearchLandingPage(@Nonnull CMChannel channel, @Nonnull Site site) {
    Content content = channel.getContent();

    Content context = getNavigationContext(site);

    return context != null && context.getLinks(CMChannel.CHILDREN).contains(content);
  }

  /**
   * Creates a search URL that points to the commerce system, including the search
   * parameters that a read from a content property of the channel.
   */
  @Nullable
  Object createSearchLandingPageURLFor(@Nonnull CMChannel channel, CommerceConnection commerceConnection,
                                       @Nonnull HttpServletRequest request, @Nonnull StoreContext storeContext) {
    String term = channel.getContent().getString(keywordsProperty);

    return commerceConnection.getServiceForVendor(CommerceSearchRedirectUrlProvider.class)
            .map(provider -> provider.provideRedirectUrl(term, request, storeContext))
            .orElse(null);
  }

  // ----------------- Helper -------------------------------

  /**
   * Returns the navigation context configured with {@link #setSegmentPath(String)} relative to the
   * {@link com.coremedia.cap.multisite.Site#getSiteRootDocument() root document} of the given site.
   *
   * @param site site
   * @return navigation, null if not found
   */
  @Nullable
  private Content getNavigationContext(@Nonnull Site site) {
    Preconditions.checkArgument(!segmentPath.startsWith("/"),
            "Segment path must be relative and not start with a slash: " + segmentPath);
    Iterable<String> segments = Splitter.on('/').omitEmptyStrings().split(segmentPath);

    Content context = site.getSiteRootDocument();
    if (context == null) {
      return null;
    }

    QueryService queryService = site.getSiteRootDocument().getRepository().getQueryService();
    Iterator<String> it = segments.iterator();
    while (it.hasNext() && context != null) {
      String segment = it.next();
      Collection<Content> children = navigationTreeRelation.getChildrenOf(context);
      context = queryService.getContentFulfilling(children, SearchTermExternalReferenceResolver.QUERY_NAVIGATION_WITH_SEGMENT, segment);
    }
    return context;
  }

  // ---------------- Config --------------------------------

  @Required
  public void setKeywordsProperty(String keywordsProperty) {
    this.keywordsProperty = keywordsProperty;
  }

  @Required
  public void setSegmentPath(String segmentPath) {
    this.segmentPath = segmentPath;
  }

  @Required
  public void setNavigationTreeRelation(TreeRelation<Content> navigationTreeRelation) {
    this.navigationTreeRelation = navigationTreeRelation;
  }
}
