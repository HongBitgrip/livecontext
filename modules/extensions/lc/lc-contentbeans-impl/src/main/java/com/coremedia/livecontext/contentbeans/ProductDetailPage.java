package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.google.common.annotations.VisibleForTesting;

/**
 * Custom page that renders the PDP page grid instead of the regular page grid.
 * <p>
 * Does not work with arbitrary Navigations, but only with LiveContextExternalChannels.
 */
public class ProductDetailPage extends PageImpl {
  @VisibleForTesting  // use the "pdpPage" factory bean
  public ProductDetailPage(boolean developerMode, SitesService sitesService, Cache cache,
                           TreeRelation<Content> contentTreeRelation,
                           ContentBeanFactory contentBeanFactory,
                           DataViewFactory dataViewFactory) {
    super(developerMode, sitesService, cache, contentTreeRelation, contentBeanFactory, dataViewFactory);
  }

  /**
   * Better use setLiveContextNavigation for type safety.
   *
   * @param navigation must be a LiveContextNavigation
   */
  @Override
  public final void setNavigation(Navigation navigation) {
    if (!(navigation instanceof LiveContextNavigation)) {
      throw new IllegalArgumentException("Navigation " + navigation + " is no LiveContextNavigation.  Use setLiveContextNavigation in order to avoid this kind of mismatch.");
    }
    setLiveContextNavigation((LiveContextNavigation)navigation);
  }

  public void setLiveContextNavigation(LiveContextNavigation navigation) {
    super.setNavigation(navigation);
  }

  @Override
  public PageGrid getPageGrid() {
    LiveContextExternalChannelImpl externalChannel = getExternalChannel();
    if (externalChannel==null) {
      throw new IllegalStateException("Must set an external channel before accessing the pagegrid.");
    }
    return externalChannel.getPdpPagegrid();
  }

  protected LiveContextExternalChannelImpl getExternalChannel() {
    // We ensured the type in the setter.  No need to check here.
    return (LiveContextExternalChannelImpl)getNavigation();
  }
}
