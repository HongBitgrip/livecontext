package com.coremedia.blueprint.elastic.social.cae.user;

import com.coremedia.elastic.social.api.users.CommunityUser;

import javax.annotation.Nonnull;

public interface PasswordExpiryPolicy {
  boolean isExpiredFor(@Nonnull CommunityUser user);
}
