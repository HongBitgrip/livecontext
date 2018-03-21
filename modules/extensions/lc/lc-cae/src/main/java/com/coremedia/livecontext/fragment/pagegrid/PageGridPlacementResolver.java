package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.common.layout.HasPageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PageGridPlacementResolver {
  @Nullable
  PageGridPlacement resolvePageGridPlacement(@Nonnull HasPageGrid bean, @Nonnull String placementName);
}
