package com.coremedia.livecontext.p13n.studio {
import com.coremedia.cap.undoc.content.Content;
import com.coremedia.ui.data.dependencies.DependencyTracker;

import ext.container.Viewport;

public class CommerceCatalogObjectsSelectFormTestViewBase extends Viewport{

  public function CommerceCatalogObjectsSelectFormTestViewBase(config:CommerceCatalogObjectsSelectFormTestView = null) {
    super(config);
  }

  [ProvideToExtChildren]
  public function getContent():Content {
    DependencyTracker.dependOnObservable(this, "content");
    return this['content'];
  }
}
}