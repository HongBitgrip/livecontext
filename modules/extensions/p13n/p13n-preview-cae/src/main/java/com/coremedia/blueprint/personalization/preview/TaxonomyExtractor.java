package com.coremedia.blueprint.personalization.preview;

import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProfile;
import com.coremedia.personalization.preview.TestContextExtractor;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This test context extractor extracts taxonomy contexts from CMUserProfile's profileExtension property.
 */
public final class TaxonomyExtractor implements TestContextExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyExtractor.class);
  private static final int ONE_HUNDRED = 100;

  private ContentBeanFactory contentBeanFactory;

  private final Map<String,String> propertyToContextMap = new HashMap<>();

  /**
   * The mapping configured here maps a property path within the user profile's
   * 'profileExtension' property to a personalization context. The property can
   * be a nested property relative the 'profileExtension' struct referring
   * to a link list property holding the taxonomies.
   *
   * @param map the mapping to set
   */
  @Required
  public void setPropertyToContextMap(Map<String, String> map) {
    if(map == null || map.isEmpty()) {
      throw new IllegalArgumentException("propertyToContextMap mapping must neither be null nor empty");
    }
    this.propertyToContextMap.putAll(map);
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    if(contentBeanFactory == null) {
      throw new IllegalArgumentException("contentBeanFactory must not be null");
    }
    this.contentBeanFactory = contentBeanFactory;
  }

  @Override
  public void extractTestContextsFromContent(final Content content, final ContextCollection contextCollection) {
    if (content == null || contextCollection == null) {
      LOGGER.debug("supplied content or contextCollection are null; cannot extract any contexts");
      return;
    }

    final ContentBean cmUserProfileBean = contentBeanFactory.createBeanFor(content);
    if(!(cmUserProfileBean instanceof CMUserProfile)) {
      LOGGER.debug("cannot extract context from contentbean of type {}", cmUserProfileBean.getClass().toString());
      return;
    }

    final Map<String, Object> profileExtensions = ((CMUserProfile) cmUserProfileBean).getProfileExtensions();
    for(Map.Entry<String,String> entry : propertyToContextMap.entrySet()) {
      extractTestContexts(entry.getKey(), entry.getValue(), profileExtensions, contextCollection);
    }
  }

  private void extractTestContexts(String propertyPath, String contextName, Map<String,Object> profileExtensions, ContextCollection contextCollection) {
    final List<CMTaxonomy> linkedTaxonomies = getLinkedTaxonomies(profileExtensions, propertyPath);
    final List<Integer> countsForTaxonomies = getCountsForTaxonomies(profileExtensions, propertyPath);
    final PropertyProfile propertyProfile = createContext(linkedTaxonomies, countsForTaxonomies);
    if(LOGGER.isDebugEnabled()) {
      final Object oldContext = contextCollection.removeContext(contextName);
      LOGGER.debug("replacing context {}: old value is {}, new value is{}", new Object[]{contextName, oldContext, propertyProfile});
    }
    contextCollection.setContext(contextName, propertyProfile);
  }

  private List<Integer> getCountsForTaxonomies(Map<String, Object> profileExtensions, String propertyPath) {
    final Object o;
    try {
      o = getProperty(profileExtensions, propertyPath + "_count");
      if(o instanceof List) {
        //noinspection unchecked
        return (List<Integer>) o;
      }
    } catch (Exception e) {
      LOGGER.info("ignoring exception while looking up counts for taxonomies", e);
    }
    LOGGER.debug("no counts for taxonomies available at property {}", propertyPath);
    return Collections.emptyList();
  }

  private PropertyProfile createContext(List<CMTaxonomy> linkedTaxonomies, List<Integer> countsForTaxonomies) {
    final PropertyProfile propertyProfile = new PropertyProfile();

    // iterate over taxonomies and check if there's a count for it
    final int sizeOfCounts = countsForTaxonomies.size();
    for(int i = 0; i < linkedTaxonomies.size(); i++) {
      final CMTaxonomy content = linkedTaxonomies.get(i);
      final String taxonomyPropertyName = content.getContent().getId();
      final double value = i < sizeOfCounts ? ((double)countsForTaxonomies.get(i)) / ONE_HUNDRED : 1;
      propertyProfile.setProperty(taxonomyPropertyName,value);
    }
    return propertyProfile;
  }

  private List<CMTaxonomy> getLinkedTaxonomies(Map<String, Object> profileExtensions, String propertyPath) {
    final Object o = getProperty(profileExtensions, propertyPath);
    if(o instanceof List) {
      //noinspection unchecked
      return (List<CMTaxonomy>) o;
    }
    LOGGER.debug("no taxonomies available at property {}", propertyPath);
    return Collections.emptyList();
  }

  private Object getProperty(Map<String, Object> profileExtensions, String propertyPath) {
    try {
      return PropertyUtils.getNestedProperty(profileExtensions, propertyPath);
    } catch (Exception e) { // NOSONAR
      // it is ok
    }
    return null;
  }

  /**
   * Returns a human-readable representation of the state of this object. The format may change without notice.
   *
   * @return human-readable representation of this object
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() +"{" +
            "contentBeanFactory=" + contentBeanFactory +
            ", propertyToContextMap=" + propertyToContextMap +
            '}';
  }
}
