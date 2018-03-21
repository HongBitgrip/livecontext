package com.coremedia.blueprint.localization;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BundleResolver {
  @Nullable Struct resolveBundle(@Nonnull Content bundle);
}
