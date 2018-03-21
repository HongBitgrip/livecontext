package com.coremedia.blueprint.cae.feeds.impl;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.contentbeans.CMVideo;

import java.util.ArrayList;
import java.util.List;

public class VideoFeedItemDataProvider extends TeasableFeedItemDataProvider {
  @Override
  public boolean isSupported(Object item) {
    return (super.isSupported(item) && CMVideo.class.isAssignableFrom(item.getClass()));
  }

  @Override
  protected List<CMTeasable> getRelatedMediaContents(CMTeasable teasable) {
    List<CMTeasable> related = new ArrayList<>();
    CMVideo video = (CMVideo)teasable;
    related.addAll(video.getPictures());
    if (video.getData() != null) {
      related.add(video);
    }
    return related;
  }
}