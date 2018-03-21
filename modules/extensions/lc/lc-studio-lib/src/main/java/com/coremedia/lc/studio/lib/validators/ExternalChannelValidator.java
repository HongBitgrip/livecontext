package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import javax.annotation.Nonnull;

public class ExternalChannelValidator extends CatalogLinkValidator {

  private static final String CODE_ISSUE_CATEGORY_EMPTY = "EmptyCategory";

  @Override
  protected void emptyPropertyValue(@Nonnull Content content, @Nonnull Issues issues) {
    addIssue(issues, Severity.ERROR, CODE_ISSUE_CATEGORY_EMPTY);
  }
}
