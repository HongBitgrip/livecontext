package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.common.Blob;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Serves arbitrary download data of mime type *.*.
 * </p>
 * <p>
 * Represents document type {@link #NAME CMDownload}.
 * </p>
 *
 * @cm.template.api
 */
public interface CMDownload extends CMTeasable {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMDownload'.
   */
  String NAME = "CMDownload";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMDownload} object
   */
  @Override
  CMDownload getMaster();

  @Override
  Map<Locale, ? extends CMDownload> getVariantsByLocale();

  @Override
  Collection<? extends CMDownload> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMDownload>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMDownload>> getAspects();

  /**
   * Name of the document property 'data'.
   */
  String DATA = "data";

  /**
   * Returns the value of the document property {@link #DATA}.
   *
   * @return the value of the document property {@link #DATA}
   * @cm.template.api
   */
  Blob getData();

  /**
   * Name of the document property 'filename'.
   */
  String FILENAME = "filename";

  /**
   * Returns the value of the document property {@link #FILENAME}.
   *
   * @return the value of the document property {@link #FILENAME}
   * @cm.template.api
   */
  String getFilename();

}
