package com.coremedia.blueprint.elastic.social.cae.user;

import com.coremedia.blueprint.elastic.social.cae.guid.GuidFilter;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import static com.coremedia.blueprint.elastic.social.cae.guid.GuidFilter.extractUuidFromGuid;
import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;

@Named
public class ElasticSocialUserHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSocialUserHelper.class);

  private final CommunityUserService communityUserService;

  @Inject
  public ElasticSocialUserHelper(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService;
  }

  @Nonnull
  public CommunityUser getCurrentOrAnonymousUser() {
    CommunityUser user = getCurrentUser();
    if (null == user) {
      user = getAnonymousUser();
    }
    return user;
  }

  @Nonnull
  public CommunityUser getAnonymousUser() {
    CommunityUser user;
    final String userId = userIdOrNull();
    // there should always be a guid, this is just a workaround
    if (null == userId) {
      user = communityUserService.createAnonymousUser();
    } else {
      user = communityUserService.createAnonymousUser(userId);
    }
    user.save();
    LOGGER.info(PERSONAL_DATA, "created new community user {}", user);
    return user;
  }

  @Nullable
  public CommunityUser getCurrentUser() {
    CommunityUser user = UserContext.getUser();
    if (null == user) {
      final String userId = userIdOrNull();
      if (null != userId) {
        user = communityUserService.getUserById(userId);
      }
    }
    return user;
  }

  @Nullable
  private static String userIdOrNull() {
    final String currentGuid = GuidFilter.getCurrentGuid();
    return Strings.isNullOrEmpty(currentGuid) ? null : extractUuidFromGuid(currentGuid);
  }

  public CommunityUser getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication == null || "anonymousUser".equals(authentication.getName())) ? null : getUser(authentication.getPrincipal());
  }

  public CommunityUser getUser(Object principal) {
    CommunityUser result = null;
    if (principal instanceof String) {
      result = communityUserService.getUserByName((String) principal);
      if (result == null) {
        result = communityUserService.getUserByEmail((String) principal);
      }
    } else if (principal instanceof UserPrincipal) {
      UserPrincipal userPrincipal = (UserPrincipal) principal;
      result = communityUserService.getUserById(userPrincipal.getUserId());
    }
    return result;
  }

}
