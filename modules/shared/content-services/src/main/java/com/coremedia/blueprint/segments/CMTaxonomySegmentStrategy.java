package com.coremedia.blueprint.segments;

import com.coremedia.blueprint.base.links.ContentSegmentStrategy;
import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;

/**
 * ContentSegmentStrategy for CMTaxonomy
 */
public class CMTaxonomySegmentStrategy implements ContentSegmentStrategy {
  private static final String VALUE = "value";

  /**
   * Returns the taxonomy's value.
   */
  @Override
  @Nonnull
  public String segment(@Nonnull Content content) {
    return content.getString(VALUE);
  }
}
