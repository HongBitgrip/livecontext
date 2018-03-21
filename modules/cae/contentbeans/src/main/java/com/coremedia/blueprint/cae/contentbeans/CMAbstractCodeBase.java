package com.coremedia.blueprint.cae.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;
import com.coremedia.xml.Markup;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Generated base class for immutable beans of document type CMAbstractCode.
 * Should not be changed.
 */
public abstract class CMAbstractCodeBase extends CMLocalizedImpl implements CMAbstractCode {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMAbstractCode} object
   */
  @Override
  public CMAbstractCode getMaster() {
    return (CMAbstractCode) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMAbstractCode> getVariantsByLocale() {
    return getVariantsByLocale(CMAbstractCode.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMAbstractCode> getLocalizations() {
    return (Collection<? extends CMAbstractCode>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMAbstractCode>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMAbstractCode>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMAbstractCode>> getAspects() {
    return (List<? extends Aspect<? extends CMAbstractCode>>) super.getAspects();
  }

  /**
   * Returns the value of the document property {@link #DESCRIPTION}.
   *
   * @return the value of the document property {@link #DESCRIPTION}
   */
  @Override
  public String getDescription() {
    return getContent().getString(DESCRIPTION);
  }

  /**
   * Returns the value of the document property {@link #CODE}.
   *
   * @return the value of the document property {@link #CODE}
   */
  @Override
  public Markup getCode() {
    return getMarkup(CODE);
  }

  /**
   * Returns the value of the document property {@link #INCLUDE}.
   *
   * @return a list of {@link CMAbstractCode} objects
   */
  @Override
  @Nonnull
  public List<? extends CMAbstractCode> getInclude() {
    List<Content> contents = getContent().getLinks(INCLUDE);
    return createBeansFor(contents, CMAbstractCode.class);
  }

  /**
   * Returns the value of the document property {@link #IE_EXPRESSION}.
   *
   * @return the value of the document property {@link #IE_EXPRESSION}
   */
  @Override
  public String getIeExpression() {
    return getContent().getString(IE_EXPRESSION);
  }

  /**
   * Returns the value of the document property {@link #IE_REVEALED}.
   *
   * @return the value of the document property {@link #IE_REVEALED}
   */
  @Override
  public boolean isIeRevealed() {
    return getContent().getBoolean(IE_REVEALED);
  }

  @Override
  public String getDataUrl() {
    return getContent().getString(DATA_URL);
  }

  @Override
  @Deprecated
  public boolean isCompressionDisabled() {
    return getContent().getBoolean(DISABLE_COMPRESSION);
  }
}
  
