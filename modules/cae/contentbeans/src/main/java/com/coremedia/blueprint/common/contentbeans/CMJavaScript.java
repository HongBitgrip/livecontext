package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Content beans for static JavaScript code.
 * </p>
 * <p>
 * Represents the document type {@link #NAME CMJavaScript}.
 * </p>
 *
 * @cm.template.api
 */
public interface CMJavaScript extends CMAbstractCode {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMJavaScript'.
   */
  String NAME = "CMJavaScript";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMJavaScript} objects
   */
  @Override
  CMJavaScript getMaster();

  @Override
  Map<Locale, ? extends CMJavaScript> getVariantsByLocale();

  @Override
  Collection<? extends CMJavaScript> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMJavaScript>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMJavaScript>> getAspects();

  /**
   * Returns the value of the document property {@link #INCLUDE}.
   *
   * @return a list of {@link CMJavaScript} objects
   */
  @Override
  @Nonnull
  List<? extends CMJavaScript> getInclude();
}
