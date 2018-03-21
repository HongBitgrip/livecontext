package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.common.layout.HasPageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultPageGridPlacementResolver implements PageGridPlacementResolver {
  @Nullable
  @Override
  public PageGridPlacement resolvePageGridPlacement(@Nonnull HasPageGrid bean, @Nonnull String placementName) {
    return bean.getPageGrid().getPlacementForName(placementName);
  }
}
