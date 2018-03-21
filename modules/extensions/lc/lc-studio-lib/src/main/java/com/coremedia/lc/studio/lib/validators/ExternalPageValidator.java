package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.rest.validation.Issues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.coremedia.rest.validation.Severity.ERROR;

public class ExternalPageValidator extends CatalogLinkValidator {

  private static final String CODE_ISSUE_EXTERNAL_PAGE_ID_EMPTY = "EmptyExternalPageId";

  @Override
  protected void emptyPropertyValue(@Nonnull Content content, @Nonnull Issues issues) {
    Content siteRootDocument = getSiteRootDocument(content);

    // Only add issue if content is not the site root document.
    // Site root document can have an empty external id.
    if (siteRootDocument == null || siteRootDocument.equals(content)) {
      return;
    }

    addIssue(issues, ERROR, CODE_ISSUE_EXTERNAL_PAGE_ID_EMPTY);
  }

  @Nullable
  private Content getSiteRootDocument(@Nonnull Content content) {
    Site site = getSite(content);
    if (site == null) {
      return null;
    }

    return site.getSiteRootDocument();
  }

  @Override
  protected void invalidExternalId(Issues issues, Object... arguments) {
    // validation for external id not applicable for external pages
  }
}
