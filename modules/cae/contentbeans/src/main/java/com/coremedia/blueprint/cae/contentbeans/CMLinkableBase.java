package com.coremedia.blueprint.cae.contentbeans;


import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMLocTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMLocalized;
import com.coremedia.blueprint.common.contentbeans.CMResourceBundle;
import com.coremedia.blueprint.common.contentbeans.CMSettings;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMViewtype;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMLinkable.
 * Should not be changed.
 */
public abstract class CMLinkableBase extends CMLocalizedImpl implements CMLinkable {
  static final String IS_IN_PRODUCTION = "isInProduction";

  public static final String ANNOTATED_LINKS_STRUCT_ROOT_PROPERTY_NAME = "links";
  public static final String ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME = "target";

  private ContextStrategy<CMLinkable, CMContext> contextStrategy;

  private ValidationService<Linkable> validationService;

  private SettingsService settingsService;
  private UrlPathFormattingHelper urlPathFormattingHelper;


  // This should be protected, since it is not meant to be a feature of
  // a contentbean, but only for internal usage in subclasses.
  // public only for compatibility reasons.
  public ContextStrategy<CMLinkable, CMContext> getContextStrategy() {
    return contextStrategy;
  }

  @Required
  public void setContextStrategy(ContextStrategy<CMLinkable, CMContext> contextStrategy) {
    if(contextStrategy == null) {
      throw new IllegalArgumentException("supplied 'contextStrategy' must not be null");
    }
    this.contextStrategy = contextStrategy;
  }

  @SuppressWarnings("unchecked")
  public ValidationService<Linkable> getValidationService() {
    return validationService;
  }

  @Required
  public void setValidationService(ValidationService<Linkable> validationService) {
    if(validationService == null) {
      throw new IllegalArgumentException("supplied 'validationService' must not be null");
    }
    this.validationService = validationService;
  }

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMLinkable} objects
   */
  @Override
  public CMLinkable getMaster() {
    return (CMLinkable) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMLinkable> getVariantsByLocale() {
    return getVariantsByLocale(CMLinkable.class);
  }

  @Override
  protected <T extends CMLocalized> Map<Locale, T> getVariantsByLocale(Class<T> type) {
    Map<Locale, T> variantsByLocale = super.getVariantsByLocale(type);
    return ImmutableMap.copyOf(Maps.filterValues(variantsByLocale, new Predicate<T>() {
      @Override
      public boolean apply(T variant) {
        return variant instanceof Linkable && validationService.validate((Linkable) variant);
      }
    }));
  }

  public Map<Locale, ? extends CMLinkable> getVariantsByLocaleUnfiltered() {
    return super.getVariantsByLocale(CMLinkable.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMLinkable> getLocalizations() {
    return (Collection<? extends CMLinkable>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMLinkable>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMLinkable>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMLinkable>> getAspects() {
    return (List<? extends Aspect<? extends CMLinkable>>) super.getAspects();
  }

  /**
   * Returns the first value of the document property {@link #VIEWTYPE}.
   *
   * @return a {@link CMViewtype}
   */
  @Override
  public CMViewtype getViewtype() {
    Content viewtype = getContent().getLink(VIEWTYPE);
    return createBeanFor(viewtype, CMViewtype.class);
  }

  /**
   * Returns the value of the document property {@link #KEYWORDS}.
   *
   * @return the value of the document property {@link #KEYWORDS}
   */
  @Override
  public String getKeywords() {
    return getContent().getString(KEYWORDS);
  }

  /**
   * Returns the value of the document property {@link #SEGMENT}.
   *
   * @return the value of the document property {@link #SEGMENT}
   */
  @Override
  public String getSegment() {
    return getContent().getString(SEGMENT);
  }

  /**
   * Returns the value of the document property  {@link #TITLE}
   *
   * @return the value of the document property  {@link #TITLE}
   */
  @Override
  public String getTitle() {
    return getContent().getString(CMLinkable.TITLE);
  }

  /**
   * Returns the value of the document property {@link #HTML_TITLE}
   *
   * @return the value of the document property {@link #HTML_TITLE}
   */
  @Override
  public String getHtmlTitle() {
    String title = getContent().getString(CMLinkable.HTML_TITLE);
    if(StringUtils.isEmpty(title)) {
      title = getContent().getString(CMLinkable.TITLE);
    }
    return title;
  }

  /**
   * Returns the value of the document property {@link #HTML_DESCRIPTION}
   *
   * @return the value of the document property {@link #HTML_DESCRIPTION}
   */
  @Override
  public String getHtmlDescription() {
    return getContent().getString(CMLinkable.HTML_DESCRIPTION);
  }

  /**
   * Returns the value of the document property {@link #LOCAL_SETTINGS}.
   *
   * @return the value of the document property {@link #LOCAL_SETTINGS}
   */
  @Override
  public Struct getLocalSettings() {
    Struct struct = getContent().getStruct(LOCAL_SETTINGS);
    return struct != null ? struct : getContent().getRepository().getConnection().getStructService().emptyStruct();
  }

  /**
   * Returns the value of the document property {@link #LINKED_SETTINGS}.
   *
   * @return a list of {@link CMSettings} objects
   */
  @Override
  public List<CMSettings> getLinkedSettings() {
    List<Content> contents = getContent().getLinks(LINKED_SETTINGS);
    return createBeansFor(contents, CMSettings.class);
  }

  @Override
  public Calendar getValidFrom() {
    return getContent().getDate(CMLinkable.VALID_FROM);
  }

  @Override
  public Calendar getExternallyDisplayedDate() {
    Calendar displayedDate = getContent().getDate(CMLinkable.EXTERNALLY_DISPLAYED_DATE);
    Calendar modificationDate = getContent().getModificationDate();

    if (displayedDate == null) {
      displayedDate = modificationDate;
    }
    return displayedDate;
  }

  @Override
  public Calendar getValidTo() {
    return getContent().getDate(CMLinkable.VALID_TO);
  }

  @Override
  public List<CMTaxonomy> getSubjectTaxonomy() {
    List<Content> contents = getContent().getLinksFulfilling(SUBJECT_TAXONOMY, IS_IN_PRODUCTION);
    return createBeansFor(contents, CMTaxonomy.class);
  }

  @Override
  public List<CMLocTaxonomy> getLocationTaxonomy() {
    List<Content> contents = getContent().getLinksFulfilling(LOCATION_TAXONOMY, IS_IN_PRODUCTION);
    return createBeansFor(contents, CMLocTaxonomy.class);
  }

  @Override
  public List<CMResourceBundle> getResourceBundles2() {
    return createBeansFor(getContent().getLinks(RESOURCE_BUNDLES2), CMResourceBundle.class);
  }

  protected SettingsService getSettingsService() {
    return settingsService;
  }

  protected UrlPathFormattingHelper getUrlPathFormattingHelper() {
    return urlPathFormattingHelper;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }

  /**
   * Provide a value for a legacy link property which has been replaced by an annotated link list property.
   * If the legacy property still holds a value, a corresponding CMLinkable content bean is returned, or null if this
   * bean is invalid according to the configured validation service.
   *
   * <p>Otherwise, the first valid target of the annotated link list property is returned, or null if no such target
   * exists.
   *
   * @param annotatedLinkListPropertyName the name of the new annotated link list property (e.g. "targets")
   * @param legacyLinkPropertyName the name of the legacy property name (e.g. "target")
   * @return a valid CMLinkable bean, or null
   */
  @Nullable
  protected CMLinkable getLegacyAnnotatedLink(String annotatedLinkListPropertyName, String legacyLinkPropertyName) {
    Content targetValue = getContent().getLink(legacyLinkPropertyName);
    if (targetValue == null) {
      return getFirstValidTarget(annotatedLinkListPropertyName);
    }
    CMLinkable bean = createBeanFor(targetValue, CMLinkable.class);
    return bean != null && getValidationService().validate(bean) ? bean : null;
  }

  /**
   * Provide values for a legacy link list property which has been replaced by an annotated link list property.
   * If the legacy property still holds a value, corresponding CMLinkable content beans are returned. The returned list
   * is filtered by the configured {@link #setValidationService(ValidationService) validation service}.
   *
   * <p>Otherwise, a list of valid targets of the new annotated link list property is returned, or the empty list if
   * no such targets exist.
   *
   * @param annotatedLinkListPropertyName the name of the new annotated link list property
   * @param legacyLinkPropertyName the name of the legacy property name
   * @return a list of valid CMLinkable beans, or the empty list if there are none
   */
  @Nullable
  protected List<CMLinkable> getLegacyAnnotatedLinks(String annotatedLinkListPropertyName, String legacyLinkPropertyName) {
    if (getContent().get(legacyLinkPropertyName) == null) {
      return getValidTargets(annotatedLinkListPropertyName);
    }
    //noinspection unchecked
    return getValidationService().filterList(createBeansFor(getContent().getLinks(legacyLinkPropertyName)));
  }

  private CMLinkable getFirstValidTarget(String annotatedLinkListPropertyName) {
    Struct targetsValue = getContent().getStruct(annotatedLinkListPropertyName);
    if (targetsValue != null) {
      List<Struct> structs = targetsValue.getStructs(ANNOTATED_LINKS_STRUCT_ROOT_PROPERTY_NAME);
      for (Struct targetStruct :structs) {
        Content content = targetStruct.getLink(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME);
        CMLinkable bean = createBeanFor(content, CMLinkable.class);
        if (bean != null && getValidationService().validate(bean)) {
          return bean;
        }
      }
    }
    return null;
  }

  private List<CMLinkable> getValidTargets(String annotatedLinkListPropertyName) {
    Struct targetsValue = getContent().getStruct(annotatedLinkListPropertyName);
    if (targetsValue == null) {
      return Collections.emptyList();
    }
    List<Struct> structs = targetsValue.getStructs(ANNOTATED_LINKS_STRUCT_ROOT_PROPERTY_NAME);
    List<CMLinkable> result = new ArrayList<>(structs.size());
    for (Struct targetStruct : structs) {
      Content content = targetStruct.getLink(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME);
      CMLinkable bean = createBeanFor(content, CMLinkable.class);
      if (bean != null && getValidationService().validate(bean)) {
        result.add(bean);
      }
    }
    return result;
  }

  /**
   * Return the value of an annotated link list property.
   *
   * <p>Annotated link lists are {@link Struct} properties with the following structure:
   * <pre>
   *   {
   *     "links": [
   *       {
   *         "target": target1,
   *         "property1": value1_1,
   *         "property2": "value1_2"
   *       },
   *       {
   *         "target": target2,
   *         "property1": value2_1,
   *         "property2": "value2_2"
   *       },
   *       ...
   *     ]
   *   }
   * </pre>
   *
   * <p>Content references are converted to content beans. Each target is validated against the configured validation service and filtered out if invalid.
   *
   * <p>To help migrating from a plain old link list to a new structured annotated link list, this method accepts a parameter <code>legacyLinkListPropertyName</code>.
   * If the new annotated link list property does not contains a value (yet), the value of this old link list property is taken and converted into the structure above.
   * This makes the new new annotated link list property (which must be a {@link Struct} property) to virtually contain content
   * if only the old link list contains a value.
   *
   * <p>Subclasses may override the {@link #convertLinkListToAnnotatedLinkList} method to populate the converted
   * structure with additional properties beside the target property. Otherwise, each target structure will
   * contain just the target property. See the implementation of
   * {@link CMTeaserBase#convertLinkListToAnnotatedLinkList} for an example.
   *
   * @param annotatedLinkListPropertyName the name of the annotated link list struct property
   * @param legacyLinkListPropertyName the name of the plain old link list property, or null.
   *
   * @return a nested map/list object tree according to the structure above
   */
  protected Map<String, List<Map<String, Object>>> getAnnotatedLinkList(String annotatedLinkListPropertyName, @Nullable String legacyLinkListPropertyName) {
    Struct targetsValue = getContent().getStruct(annotatedLinkListPropertyName);
    List<Map<String, Object>> linksAsBeans = null;
    if (targetsValue == null) {
      if (legacyLinkListPropertyName != null) {
        linksAsBeans = convertLinkListToAnnotatedLinkList(legacyLinkListPropertyName);
      }
    } else {
      List<Struct> links = targetsValue.getStructs(ANNOTATED_LINKS_STRUCT_ROOT_PROPERTY_NAME);
      linksAsBeans = new ArrayList<>(links.size());
      for (Struct targetStruct : links) {
        // the validation service needs a content bean, so extract the target content and create a bean for it
        CMLinkable bean = createBeanFor(targetStruct.getLink(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME), CMLinkable.class);
        if (bean != null && getValidationService().validate(bean)) {
          linksAsBeans.add(createBeanMapFor(targetStruct));
        }
      }
    }
    return createLinksStructMap(linksAsBeans);
  }

  /**
   * Convert a plain link list property into the nested structure of an annotated link list.
   *
   * @param linkListPropertyName the name of the link list property
   * @return a list of maps, each map containing a {@link #ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME}
   * property pointing to a CMLinkable bean. The list is filtered to hold only valid content beans
   * according to the configured {@link #setValidationService(ValidationService) validation service}.
   */
  protected List<Map<String, Object>> convertLinkListToAnnotatedLinkList(String linkListPropertyName) {
    List<Content> targets = getContent().getLinks(linkListPropertyName);
    if (targets.isEmpty()) {
      return Collections.emptyList();
    }
    List<Map<String, Object>> linksAsBeans = new ArrayList<>(targets.size());
    for (int i = 0; i < targets.size(); i++) {
      Content target = targets.get(i);
      CMLinkable bean = createBeanFor(target, CMLinkable.class);
      if (bean != null && getValidationService().validate(bean)) {
        Map<String, Object> linkStructMap = createAnnotatedLinkStructMap(bean, i + 1);
        linksAsBeans.add(linkStructMap);
      }
    }
    return linksAsBeans;
  }

  private static Map<String, List<Map<String, Object>>> createLinksStructMap(List<Map<String, Object>> targetStructMaps) {
    if (targetStructMaps == null) {
      return null;
    }
    return Collections.singletonMap(ANNOTATED_LINKS_STRUCT_ROOT_PROPERTY_NAME, targetStructMaps);
  }

  /**
   * Create annotated link struct map. Method can be overridden to add additional map entries.
   * @param target the target
   * @param index the index of the target
   * @return annotated link struct map
   */
  protected Map<String, Object> createAnnotatedLinkStructMap(CMLinkable target, int index) {
    Map<String, Object> targetStructMap = new LinkedHashMap<>(3);
    targetStructMap.put(ANNOTATED_LINK_STRUCT_TARGET_PROPERTY_NAME, target);
    return targetStructMap;
  }
}
  
