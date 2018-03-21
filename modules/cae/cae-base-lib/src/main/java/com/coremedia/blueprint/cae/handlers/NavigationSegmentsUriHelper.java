package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.base.navigation.context.ContentRootNavigationsBySegmentCacheKey;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class NavigationSegmentsUriHelper {

  public static final String SEGMENT_DELIM = "/";

  private ContentBeanFactory contentBeanFactory;
  private SitesService sitesService;
  private Cache cache;
  private UrlPathFormattingHelper urlPathFormattingHelper;

  // --- features ---------------------------------------------------

  public List<String> getPathList(Navigation bean) {
    List<String> pathList = new ArrayList<>();

    for (Linkable navigation : bean.getNavigationPathList()) {
      pathList.add(navigation.getSegment());
    }

    return pathList;
  }

  /**
   * Looks up a {@link CMNavigation} in the navigation hierarchy, with the first path segment denoting a root segment.
   *
   * @param segments navigation path {@link com.coremedia.blueprint.common.contentbeans.CMLinkable#getSegment() segments}
   * @return the navigation object specified by the given navigation path, or {@code null}, if none is found
   */
  public Navigation parsePath(List<String> segments) {
    if (isEmpty(segments)) {
      return null;
    }
    // resolve first URL segment to site root channel
    Iterator<String> it = segments.iterator();
    Navigation navigation = lookupRootSegment(it.next());

    // iterate over next segments and find the respective navigation child
    while (navigation!=null && it.hasNext()) {
      navigation = childBySegment(navigation, it.next());
    }
    return navigation;
  }

  private static Navigation childBySegment(Navigation navigation, String segment) {
    List<? extends Linkable> children = navigation.getChildren();
    for (Linkable child : children) {
      if (child instanceof Navigation && segment.equals(child.getSegment())) {
        return (Navigation)child;
      }
    }
    return null;
  }

  public Navigation parsePath(String navigationPath) {
    List<String> segments = Arrays.asList(navigationPath.split(SEGMENT_DELIM));
    return parsePath(segments);
  }

  /**
   * Looks up a {@link CMNavigation} for a root channel by evaluating its URL segment
   * @param segment the URL segment to look for
   * @return the root channel as CMNavigation object that this segment belongs to
   */
  public CMNavigation lookupRootSegment(String segment) {
    ContentRootNavigationsBySegmentCacheKey cacheKey = new ContentRootNavigationsBySegmentCacheKey(sitesService, urlPathFormattingHelper);
    final Map<String, Content> contentRootNavigations = cache.get(cacheKey);
    final Content content = contentRootNavigations.get(segment);
    return contentBeanFactory.createBeanFor(content, CMNavigation.class);
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }
}
