package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.elastic.core.api.models.UniqueConstraintViolationException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.services.SessionSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;

/**
 * Implements an UserService to integrate with a commerce system.
 */
public class LiveContextUserSessionSynchronizerImpl implements SessionSynchronizer {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextUserSessionSynchronizerImpl.class);

  private CommunityUserService communityUserService;

  @Override
  public void synchronizeUserSession(HttpServletRequest request, HttpServletResponse response)
          throws GeneralSecurityException {
    UserSessionService commerceUserSessionService = getCommerceUserSessionService();
    boolean authenticatedOnCommerce = commerceUserSessionService != null && commerceUserSessionService.isLoggedIn();

    if (authenticatedOnCommerce) {
      synchronizeUserContext();
    }
  }

  private void synchronizeUserContext() {
    User shopUser = getCommerceUserService().map(UserService::findCurrentUser).orElse(null);

    if (shopUser == null) {
      UserContextProvider userContextProvider = getUserContextProvider();
      String currentUserId = userContextProvider != null ? userContextProvider.getCurrentContext().getUserId() : null;
      LOG.warn(PERSONAL_DATA, "Could not find current user '{}' in shop system", currentUserId);
      return;
    }

    CommunityUser communityUser = getOrCreateCommunityUserWithRetry(shopUser);
    com.coremedia.blueprint.elastic.social.cae.user.UserContext.setUser(communityUser);
  }

  private CommunityUser getOrCreateCommunityUserWithRetry(User shopUser) {
    try {
      return getOrCreateCommunityUser(shopUser);
    } catch (UniqueConstraintViolationException ex) {
      return getOrCreateCommunityUser(shopUser);
    }
  }

  private synchronized CommunityUser getOrCreateCommunityUser(User shopUser) {
    CommunityUser communityUser = communityUserService.getUserByName(shopUser.getLogonId());
    if (communityUser == null) {
      //register communityUser if not existing
      communityUser = communityUserService.createUser(shopUser.getLogonId(), null, shopUser.getEmail1());
      communityUser.setProperty("state", CommunityUser.State.ACTIVATED);
      communityUser.save();
    }
    return communityUser;
  }

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService; //NOSONAR - the setter is called before the service is used.
  }

  private static UserContextProvider getUserContextProvider() {
    return getCommerceConnection().map(CommerceConnection::getUserContextProvider).orElse(null);
  }

  private static UserSessionService getCommerceUserSessionService() {
    return getCommerceConnection().map(CommerceConnection::getUserSessionService).orElse(null);
  }

  private static Optional<UserService> getCommerceUserService() {
    return getCommerceConnection().map(CommerceConnection::getUserService);
  }

  private static Optional<CommerceConnection> getCommerceConnection() {
    return CurrentCommerceConnection.find();
  }
}
