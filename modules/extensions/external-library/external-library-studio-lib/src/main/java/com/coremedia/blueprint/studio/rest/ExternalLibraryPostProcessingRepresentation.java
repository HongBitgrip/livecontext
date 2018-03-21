package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.content.Content;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation to encapsulates the results that have been produces during the post processing.
 *
 * The container can be used deliver additional data to the UI by adding
 * the information that have been created during the post processing.
 */
public class ExternalLibraryPostProcessingRepresentation {
  private Content createdContent;
  private List<Content> additionalContent = new ArrayList<>();

  public ExternalLibraryPostProcessingRepresentation(Content content) {
    this.createdContent = content;
  }

  public Content getCreatedContent() {
    return createdContent;
  }

  public void addCreatedContent(Content content) {
    additionalContent.add(content);
  }

  public List<Content> getAdditionalContent() {
    return additionalContent;
  }
}
