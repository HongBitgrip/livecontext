package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.blueprint.common.util.Flatless;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * A collection of media contents.
 * </p>
 * <p>
 * Represents the document type {@link #NAME CMGallery}.
 * </p>
 *
 * @cm.template.api
 */
public interface CMGallery<T extends CMMedia> extends CMCollection<T>, Flatless {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMCollection'.
   */
  String NAME = "CMGallery";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMCollection} object
   */
  @Override
  CMGallery<T> getMaster();

  @Override
  Map<Locale, ? extends CMGallery<T>> getVariantsByLocale();

  @Override
  Collection<? extends CMGallery<T>> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMGallery<T>>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMGallery<T>>> getAspects();

  /**
   * Returns the value of the document property {@link #ITEMS} filtered by all validators.
   *
   * @return a list of {@link CMTeasable} objects
   */
  @Override
  List<T> getItems();
}
