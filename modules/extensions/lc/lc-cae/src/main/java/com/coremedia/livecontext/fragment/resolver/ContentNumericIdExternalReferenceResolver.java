package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * External Content resolver for 'externalRef' values that contain a numeric content id.
 */
public class ContentNumericIdExternalReferenceResolver extends ExternalReferenceResolverBase {

  public ContentNumericIdExternalReferenceResolver() {
    super(CONTENT_ID_FRAGMENT_PREFIX);
  }

  // --- interface --------------------------------------------------

  @Override
  protected boolean include(@Nonnull FragmentParameters fragmentParameters, @Nonnull String referenceInfo) {
    try {
      //noinspection ResultOfMethodCallIgnored
      Integer.parseInt(referenceInfo); // NOSONAR squid:S2201 "Return values should not be ignored"
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Nullable
  @Override
  protected LinkableAndNavigation resolveExternalRef(@Nonnull FragmentParameters fragmentParameters,
                                                     @Nonnull String referenceInfo,
                                                     @Nonnull Site site) {
    Content linkable = contentRepository.getContent(IdHelper.formatContentId(referenceInfo));
    Content navigation = null;
    if (linkable != null) {
      // Determine context of linkable and set it as navigation
      navigation = getNavigationForLinkable(linkable);
    }
    if (navigation == null) {
      navigation = site.getSiteRootDocument();
    }

    return new LinkableAndNavigation(linkable, navigation);
  }

}
