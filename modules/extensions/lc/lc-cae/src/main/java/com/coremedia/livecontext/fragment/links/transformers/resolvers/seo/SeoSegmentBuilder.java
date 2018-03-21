package com.coremedia.livecontext.fragment.links.transformers.resolvers.seo;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;

import javax.annotation.Nonnull;

public interface SeoSegmentBuilder {
  @Nonnull
  String asSeoSegment(CMNavigation navigation, CMObject target);
}
