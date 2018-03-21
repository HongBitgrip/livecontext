package com.coremedia.catalog.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.List;

public class CatalogProductValidator extends ContentTypeValidatorBase{
  private static final String CODE_ISSUE_NOT_IN_CATALOG = "productIsNotLinkedInCatalog";
  private static final String CONTEXTS_PROPERTY_NAME = "contexts";

  @Override
  public void validate(Content content, Issues issues) {
    List<Content> parentCategories = content.getLinks(CONTEXTS_PROPERTY_NAME);
    if(parentCategories.isEmpty()){
      issues.addIssue(Severity.ERROR, CONTEXTS_PROPERTY_NAME, CODE_ISSUE_NOT_IN_CATALOG);
    }
  }
}