package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.common.layout.HasPageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PdpPageGridPlacementResolver implements PageGridPlacementResolver {
  @Nullable
  @Override
  public PageGridPlacement resolvePageGridPlacement(@Nonnull HasPageGrid bean, @Nonnull String placementName) {
    if (bean instanceof CMExternalChannel) {
      return ((CMExternalChannel) bean).getPdpPagegrid().getPlacementForName(placementName);
    }
    return bean.getPageGrid().getPlacementForName(placementName);
  }
}
