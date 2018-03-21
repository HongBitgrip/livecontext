package com.coremedia.blueprint.elastic.social.cae.user;

import com.coremedia.elastic.social.api.users.CommunityUser;

import javax.annotation.Nonnull;

/**
 * A {@link com.coremedia.blueprint.elastic.social.cae.user.PasswordExpiryPolicy password expiry policy}
 * that never answers <code>true</code>.
 */
public class PasswordNeverExpiresPolicy implements PasswordExpiryPolicy {
  @Override
  public boolean isExpiredFor(@Nonnull CommunityUser user) {
    return false;
  }
}
