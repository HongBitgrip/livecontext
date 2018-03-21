package com.coremedia.blueprint.cae.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMCSS;
import com.coremedia.blueprint.common.contentbeans.CMJavaScript;
import com.coremedia.blueprint.common.contentbeans.CMResourceBundle;
import com.coremedia.blueprint.common.contentbeans.CMSymbol;
import com.coremedia.blueprint.common.contentbeans.CMTheme;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.common.Blob;
import com.coremedia.xml.Markup;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;


/**
 * Base class for immutable beans of document type CMTheme.
 * Should not be changed.
 */
public abstract class CMThemeBase extends CMLocalizedImpl implements CMTheme {
  private static final String UNCHECKED = "unchecked";

  @SuppressWarnings({UNCHECKED})
  @Override
  public Map<String, ? extends Aspect<? extends CMTheme>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMTheme>>) super.getAspectByName();
  }

  @SuppressWarnings({UNCHECKED})
  @Override
  public List<? extends Aspect<? extends CMTheme>> getAspects() {
    return (List<? extends Aspect<? extends CMTheme>>) super.getAspects();
  }

  @Override
  public String getDescription() {
    return getContent().getString(DESCRIPTION);
  }

  @Override
  public Blob getIcon() {
    return getContent().getBlobRef(CMSymbol.ICON);
  }

  @Override
  @Nonnull
  public List<CMJavaScript> getJavaScriptLibraries() {
    return createBeansFor(getContent().getLinks(JAVA_SCRIPT_LIBS), CMJavaScript.class);
  }

  @Override
  @Nonnull
  public List<CMJavaScript> getJavaScripts() {
    return createBeansFor(getContent().getLinks(JAVA_SCRIPTS), CMJavaScript.class);
  }

  @Override
  @Nonnull
  public List<CMCSS> getCss() {
    return createBeansFor(getContent().getLinks(CSS), CMCSS.class);
  }

  @Override
  @Nonnull
  public List<CMResourceBundle> getResourceBundles() {
    return createBeansFor(getContent().getLinks(RESOURCE_BUNDLES), CMResourceBundle.class);
  }

  @Override
  public String getViewRepositoryName() {
    return getContent().getString(VIEW_REPOSITORY_NAME);
  }

  @Override
  public Markup getDetailText() {
    return getMarkup(DETAIL_TEXT);
  }
}
  