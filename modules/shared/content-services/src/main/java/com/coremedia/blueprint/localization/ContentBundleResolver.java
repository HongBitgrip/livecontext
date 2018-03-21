package com.coremedia.blueprint.localization;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentBundleResolver implements BundleResolver {
  private static final String LOCALIZATIONS = "localizations";

  /**
   * Returns the "localizations" struct of the given content.
   * <p>
   * The content is supposed to be a CMResourceBundle.
   */
  @Override
  @Nullable
  public Struct resolveBundle(@Nonnull Content bundle) {
    return bundle.getStruct(LOCALIZATIONS);
  }
}
