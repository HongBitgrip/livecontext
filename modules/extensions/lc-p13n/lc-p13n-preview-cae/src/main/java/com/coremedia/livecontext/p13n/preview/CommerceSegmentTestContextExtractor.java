package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProfile;
import com.coremedia.personalization.preview.TestContextExtractor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Extracts commerce usersegments from cmUserProfile and enriches the p13n ContextCollection and the StoreContext.
 */
public class CommerceSegmentTestContextExtractor implements TestContextExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceSegmentTestContextExtractor.class);

  private ContentBeanFactory contentBeanFactory;

  static final String PROPERTIES_PREFIX = "properties";
  static String COMMERCE_CONTEXT = "commerce";
  static String USER_SEGMENTS_PROPERTY = "usersegments";

  private static String SEGMENTS_PROPERTY_PATH = PROPERTIES_PREFIX + "." + COMMERCE_CONTEXT + "." + USER_SEGMENTS_PROPERTY;

  @Override
  public void extractTestContextsFromContent(Content content, ContextCollection contextCollection) {
    if (content == null || contextCollection == null) {
      LOG.debug("supplied content or contextCollection are null; cannot extract any contexts");
      return;
    }

    ContentBean cmUserProfileBean = contentBeanFactory.createBeanFor(content);
    if (!(cmUserProfileBean instanceof CMUserProfile)) {
      LOG.debug("cannot extract context from contentbean of type {}", cmUserProfileBean.getClass().toString());
      return;
    }

    Map<String, Object> profileExtensions = ((CMUserProfile) cmUserProfileBean).getProfileExtensions();
    Object userSegments = getProperty(profileExtensions, SEGMENTS_PROPERTY_PATH);

    if (userSegments != null && userSegments instanceof List) {
      List userSegmentList = (List) userSegments;
      if (!userSegmentList.isEmpty()) {
        PropertyProfile propertyProfile = new PropertyProfile();
        propertyProfile.setProperty(USER_SEGMENTS_PROPERTY, StringUtils.join(userSegmentList, ","));
        contextCollection.setContext(COMMERCE_CONTEXT, propertyProfile);

        addUserSegmentsToStoreContext(userSegmentList);
      }
    }
  }

  private void addUserSegmentsToStoreContext(List<String> userSegmentList){
    CommerceConnection currentConnection = CurrentCommerceConnection.find().orElse(null);

    StoreContext storeContext = currentConnection != null ? currentConnection.getStoreContext() : null;
    CommerceIdProvider commerceIdProvider = currentConnection != null ? currentConnection.getIdProvider() : null;

    if (storeContext == null || commerceIdProvider == null) {
      LOG.debug("at least one of storeContext {} or commerceIdProvider {} is null", storeContext, commerceIdProvider);
      return;
    }

    List<String> segmentIds = new ArrayList<>();
    for (String userSegment : userSegmentList) {
      Optional<CommerceId> commerceId = CommerceIdParserHelper.parseCommerceId(userSegment);
      commerceId.flatMap(CommerceId::getExternalId).ifPresent(segmentIds::add);
    }
    storeContext.setUserSegments(StringUtils.join(segmentIds, ","));
  }

  private Object getProperty(Map<String, Object> profileExtensions, String propertyPath) {
    try {
      return PropertyUtils.getNestedProperty(profileExtensions, propertyPath);
    } catch (Exception e) { // NOSONAR
      // it is ok
    }
    return null;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

}
