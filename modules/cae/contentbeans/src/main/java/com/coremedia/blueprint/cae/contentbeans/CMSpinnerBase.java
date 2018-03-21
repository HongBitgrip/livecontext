package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMSpinner;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMSpinner.
 * Should not be changed.
 */
public abstract class CMSpinnerBase extends CMVisualImpl implements CMSpinner {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMSpinner} objects
   */
  @Override
  public CMSpinner getMaster() {
    return (CMSpinner) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMSpinner> getVariantsByLocale() {
    return getVariantsByLocale(CMSpinner.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMSpinner> getLocalizations() {
    return (Collection<? extends CMSpinner>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMSpinner>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMSpinner>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMSpinner>> getAspects() {
    return (List<? extends Aspect<? extends CMSpinner>>) super.getAspects();
  }

  @Override
  public List<CMPicture> getSequence() {
    List<Content> images = getContent().getLinks(SEQUENCE);
    return createBeansFor(images, CMPicture.class);
  }
}
