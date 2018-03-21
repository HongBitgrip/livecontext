package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import com.coremedia.personalization.context.collector.AbstractContextSource;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper.format;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * A {@link com.coremedia.personalization.context.collector.ContextSource} that reads the commerce user id
 * from the current commerce user context and asks the commerce system for memberships in customer
 * segments. Such customer segments in which the user is a member will be provided in the context collection
 * to evaluate personalization rules based on commerce segments.
 */
public class CommerceSegmentSource extends AbstractContextSource {

  private static final String SEGMENT_ID_LIST_CONTEXT_KEY = "usersegments";

  private String contextName = "commerce";

  @SuppressWarnings("unused")
  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  @Override
  public void preHandle(HttpServletRequest request, HttpServletResponse response, ContextCollection contextCollection) {
    if (!hasCurrentCommerceConnection()) {
      return;
    }

    StoreContext storeContext = getStoreContext();
    if (storeContext == null) {
      return;
    }

    UserContext userContext = getUserContext();
    if (userContext == null) {
      return;
    }

    String userSegments = storeContext.getUserSegments();

    if (isNullOrEmpty(userSegments) && isEmpty(userContext)) {
      return;
    }

    List<String> segmentIdList = null;
    MapPropertyMaintainer segmentContext = new MapPropertyMaintainer();

    //UserSegments provided by LiveContext Fragment Connector
    if (userSegments != null && !userSegments.isEmpty()) {
      segmentIdList = Arrays.asList(userSegments.split(","));
    }
    if (segmentIdList == null) {
      segmentIdList = readSegmentIdListFromCommerceSystem();
    }

    if (segmentIdList != null) {
      StringBuilder segmentList = new StringBuilder();
      // The following format (comma seperated list if ids) demands that not a id can be part of another id (like
      // 1234 is part of 123456). This is guaranteed if all ids have the same length (as it is the case). If not,
      // the format of ids can be changed to a more robust one.
      for (String segment : segmentIdList) {
        String segmentId = format(getCommerceIdProvider().formatSegmentId(segment));
        segmentList.append(segmentId).append(",");
      }
      segmentContext.setProperty(SEGMENT_ID_LIST_CONTEXT_KEY, segmentList.toString());
      contextCollection.setContext(contextName, segmentContext);
    }
  }

  private static boolean isEmpty(@Nonnull UserContext userContext) {
    return Stream.of(userContext.getUserId(), userContext.getUserName(), userContext.getCookieHeader())
            .allMatch(Objects::isNull);
  }

  protected List<String> readSegmentIdListFromCommerceSystem() {
    SegmentService segmentService = getSegmentService();
    if (segmentService == null) {
      return emptyList();
    }

    return segmentService.findSegmentsForCurrentUser(getStoreContext()).stream()
            .map(CommerceBean::getId)
            .map(c -> c.getExternalId().orElse(null))
            .filter(Objects::nonNull)
            .collect(toList());
  }

  private static boolean hasCurrentCommerceConnection() {
    return CurrentCommerceConnection.find().isPresent();
  }

  public StoreContext getStoreContext() {
    return CurrentCommerceConnection.get().getStoreContext();
  }

  public UserContext getUserContext() {
    return CurrentCommerceConnection.get().getUserContext();
  }

  public SegmentService getSegmentService() {
    return CurrentCommerceConnection.get().getSegmentService();
  }

  public CommerceIdProvider getCommerceIdProvider() {
    return CurrentCommerceConnection.get().getIdProvider();
  }
}
