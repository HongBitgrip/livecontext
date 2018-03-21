package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public interface LiveContextSiteResolver extends SiteResolver {

  @Nullable
  Site findSiteFor(@Nonnull FragmentParameters fragmentParameters);

  @Nullable
  Site findSiteFor(@Nonnull String storeId, @Nonnull Locale locale);
}
