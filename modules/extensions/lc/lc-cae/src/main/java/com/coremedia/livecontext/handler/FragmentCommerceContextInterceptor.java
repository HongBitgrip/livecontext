package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.fragment.links.context.accessors.LiveContextContextAccessor;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;
import static com.coremedia.livecontext.handler.util.CaeStoreContextUtil.updateStoreContextWithFragmentParameters;
import static java.util.stream.Collectors.toList;

/**
 * Suitable for URLs whose second segment denotes the store, e.g. /fragment/10001/...
 */
public class FragmentCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(FragmentCommerceContextInterceptor.class);

  private static final long MILLISECONDS_PER_MINUTE = 60 * 1000L;

  private LiveContextContextAccessor fragmentContextAccessor;
  private LiveContextSiteResolver liveContextSiteResolver;
  private boolean contractsProcessingEnabled = true;

  // Names of context entries
  private String contextNameMemberGroup = "wc.preview.memberGroups";
  private String contextNameTimestamp = "wc.preview.timestamp";
  private String contextNamePreviewUserGroup = "wc.preview.usergroup";
  private String contextNameTimezone = "wc.preview.timezone";
  private String contextNameWorkspaceId = "wc.preview.workspaceId";
  private String contextNameUserId = "wc.user.id";
  private String contextNameUserName = "wc.user.loginid";
  private String contextNameContractIds = "wc.preview.contractIds";
  private String contextNameUserGroupIds = "wc.user.membergroupids";

  private CatalogAliasTranslationService catalogAliasTranslationService;

  // --- AbstractCommerceContextInterceptor -------------------------

  @Override
  public boolean preHandle(@Nonnull HttpServletRequest request, HttpServletResponse response, Object handler) {
    setFragmentContext(request);
    return super.preHandle(request, response, handler);
  }

  @Override
  @Nonnull
  protected Optional<CommerceConnection> getCommerceConnectionWithConfiguredStoreContext(
          @Nonnull Site site, @Nonnull HttpServletRequest request) {
    Optional<CommerceConnection> connection = super.getCommerceConnectionWithConfiguredStoreContext(site, request);

    if (connection.isPresent()) {
      StoreContext storeContext = connection.get().getStoreContext();
      FragmentParameters fragmentParameters = FragmentContextProvider.getFragmentContext(request).getParameters();
      updateStoreContextWithFragmentParameters(catalogAliasTranslationService, storeContext, fragmentParameters, site);

      if (isPreview()) {
        Context fragmentContext = LiveContextContextHelper.fetchContext(request);
        if (fragmentContext != null) {
          initStoreContextPreview(fragmentContext, storeContext, request);
        }
      }
    }

    return connection;
  }

  @Override
  protected void initUserContext(@Nonnull CommerceConnection commerceConnection, @Nonnull HttpServletRequest request) {
    super.initUserContext(commerceConnection, request);

    UserContext userContext = commerceConnection.getUserContext();
    Context fragmentContext = LiveContextContextHelper.fetchContext(request);
    if (userContext == null || fragmentContext == null) {
      return;
    }

    StoreContext storeContext = commerceConnection.getStoreContext();

    UserContext.Builder userContextBuilder = UserContext.buildCopyOf(userContext);
    findStringValue(fragmentContext, contextNameUserId).ifPresent(userContextBuilder::withUserId);
    findStringValue(fragmentContext, contextNameUserName).ifPresent(userContextBuilder::withUserName);
    userContext = userContextBuilder.build();
    commerceConnection.setUserContext(userContext);

    findStringValue(fragmentContext, contextNameUserGroupIds).ifPresent(storeContext::setUserSegments);

    if (contractsProcessingEnabled) {
      ContractService contractService = commerceConnection.getContractService();
      initUserContextContractsProcessing(fragmentContext, storeContext, userContext, contractService);
    }
  }

  @Override
  @Nullable
  protected Site getSite(@Nonnull HttpServletRequest request, String normalizedPath) {
    FragmentParameters parameters = FragmentContextProvider.getFragmentContext(request).getParameters();
    return parameters != null ? liveContextSiteResolver.findSiteFor(parameters) : null;
  }

  @Override
  public SiteResolver getSiteResolver() {
    return liveContextSiteResolver;
  }

  // --- features (expected to be useful) ---------------------------

  protected void setFragmentContext(@Nonnull HttpServletRequest request) {
    // apply the absolute URL flag for fragment requests
    request.setAttribute(ABSOLUTE_URI_KEY, true);
    fragmentContextAccessor.openAccessToContext(request);
  }

  // --- customization hooks (expected to be overridden) ------------

  /**
   * Convert the timestamp to millis.
   * <p>
   * Invoked with the value of the {@link #setContextNameTimestamp(String)}
   * attribute from the fragment context.
   *
   * @return The time represented by the timestamp as millis
   * @throws IllegalArgumentException if the timestamp cannot be converted
   */
  protected long timestampToMillis(@Nonnull String timestamp) {
    return Timestamp.valueOf(timestamp).getTime();
  }

  // --- configure --------------------------------------------------

  @Required
  public void setFragmentContextAccessor(LiveContextContextAccessor fragmentContextAccessor) {
    this.fragmentContextAccessor = fragmentContextAccessor;
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }

  public void setContractsProcessingEnabled(boolean contractsProcessingEnabled) {
    this.contractsProcessingEnabled = contractsProcessingEnabled;
  }

  public void setContextNameMemberGroup(String contextNameMemberGroup) {
    this.contextNameMemberGroup = contextNameMemberGroup;
  }

  public void setContextNameTimestamp(String contextNameTimestamp) {
    this.contextNameTimestamp = contextNameTimestamp;
  }

  public void setContextNameTimezone(String contextNameTimezone) {
    this.contextNameTimezone = contextNameTimezone;
  }

  public void setContextNameWorkspaceId(String contextNameWorkspaceId) {
    this.contextNameWorkspaceId = contextNameWorkspaceId;
  }

  public void setContextNameUserId(String contextNameUserId) {
    this.contextNameUserId = contextNameUserId;
  }

  public void setContextNameUserName(String contextNameUserName) {
    this.contextNameUserName = contextNameUserName;
  }

  public void setContextNameContractIds(String contextNameContractIds) {
    this.contextNameContractIds = contextNameContractIds;
  }

  // --- internal ---------------------------------------------------

  private void initStoreContextPreview(@Nonnull Context fragmentContext, @Nonnull StoreContext storeContext,
                                       @Nonnull HttpServletRequest request) {
    initStoreContextUserSegments(fragmentContext, storeContext);
    initStoreContextPreviewMode(fragmentContext, storeContext, request);
    initStoreContextWorkspaceId(fragmentContext, storeContext);
  }

  private void initStoreContextUserSegments(@Nonnull Context fragmentContext, @Nonnull StoreContext storeContext) {
    findStringValue(fragmentContext, contextNameMemberGroup)
            .ifPresent(storeContext::setUserSegments);
  }

  private void initStoreContextWorkspaceId(@Nonnull Context fragmentContext, @Nonnull StoreContext storeContext) {
    findStringValue(fragmentContext, contextNameWorkspaceId)
            .ifPresent(storeContext::setWorkspaceId);
  }

  private void initStoreContextPreviewMode(@Nonnull Context fragmentContext, @Nonnull StoreContext storeContext,
                                           @Nonnull HttpServletRequest request) {
    if (!isStudioPreviewRequest(request)) {
      return;
    }
    initStoreContextPreviewDate(fragmentContext, storeContext, request);
  }

  @VisibleForTesting
  boolean isStudioPreviewRequest(@Nonnull HttpServletRequest request) {
    return LiveContextPageHandlerBase.isStudioPreviewRequest(request);
  }

  private void initStoreContextPreviewDate(@Nonnull Context fragmentContext, @Nonnull StoreContext storeContext,
                                           @Nonnull HttpServletRequest request) {
    Calendar calendar = createPreviewCalendar(fragmentContext);
    if (calendar != null) {
      storeContext.setPreviewDate(convertToPreviewDateRequestParameterFormat(calendar));
      request.setAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE, calendar);
    }

    findStringValue(fragmentContext, contextNamePreviewUserGroup)
            .ifPresent(storeContext::setUserSegments);
  }

  @Nullable
  private Calendar createPreviewCalendar(@Nonnull Context fragmentContext) {
    String timestamp = findStringValue(fragmentContext, contextNameTimestamp).orElse(null);
    if (timestamp == null) {
      return null;
    }

    long timestampMillis;
    if (NumberUtils.isNumber(timestamp)) {
      timestampMillis = Long.valueOf(timestamp);
    } else {
      try {
        timestampMillis = timestampToMillis(timestamp);
      } catch (IllegalArgumentException e) {
        LOG.warn("Cannot convert timestamp \"{}\", ignore", timestamp, e);
        return null;
      }
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(roundToMinute(timestampMillis));
    findStringValue(fragmentContext, contextNameTimezone)
            .ifPresent(timezone -> calendar.setTimeZone(TimeZone.getTimeZone(timezone)));

    return calendar;
  }

  private void initUserContextContractsProcessing(@Nonnull Context fragmentContext, @Nonnull StoreContext storeContext,
                                                  @Nonnull UserContext userContext,
                                                  @Nullable ContractService contractService) {
    Collection<String> contractIdsFromContext = contractIds(fragmentContext);
    if (contractIdsFromContext.isEmpty()) {
      return;
    }

    // check if user is allowed to execute a call for the passed contracts
    if (contractService != null) {
      String organizationId = findStringValue(fragmentContext, "user.organization.id").orElse(null);

      Collection<String> contractIdsForUser = contractIds(contractService, storeContext, userContext, organizationId);
      Collection intersection = CollectionUtils.intersection(contractIdsForUser, contractIdsFromContext);
      if (!intersection.isEmpty()) {
        storeContext.setContractIds(Arrays.copyOf(intersection.toArray(), intersection.size(), String[].class));
      }
    }
  }

  @Nonnull
  private Collection<String> contractIds(@Nonnull Context fragmentContext) {
    Optional<String> contractIdsStr = findStringValue(fragmentContext, contextNameContractIds);

    return contractIdsStr
            .filter(str -> !str.isEmpty())
            .map(Splitter.on(' ')::splitToList)
            .orElseGet(Collections::emptyList);
  }

  @Nonnull
  private static Collection<String> contractIds(@Nonnull ContractService contractService,
                                                @Nonnull StoreContext storeContext, @Nonnull UserContext userContext,
                                                @Nullable String organizationId) {
    Collection<Contract> contractsForUser = contractService.findContractIdsForUser(userContext, storeContext,
            organizationId);

    return contractsForUser.stream()
            .map(CommerceBean::getExternalTechId)
            .collect(toList());
  }

  @Nonnull
  private static Optional<String> findStringValue(@Nonnull Context fragmentContext, @Nonnull String name) {
    String value = (String) fragmentContext.get(name);
    return Optional.ofNullable(value);
  }

  @Nonnull
  @VisibleForTesting
  static String convertToPreviewDateRequestParameterFormat(@Nonnull Calendar calendar) {
    Format dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm ");
    return dateFormat.format(calendar.getTimeInMillis()) + calendar.getTimeZone().getID();
  }

  private static long roundToMinute(long millis) {
    millis += MILLISECONDS_PER_MINUTE / 2;
    return millis - millis % MILLISECONDS_PER_MINUTE;
  }

  @Required
  public void setCatalogAliasTranslationService(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }
}
