package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * An {@link LikeGenerator} generates likes for a teasable
 */
public class LikeGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(LikeGenerator.class);

  @Inject
  private LikeService likeService;

  private int likeCount = 0;

  private Random random = new Random();

  private final List<Object> likeEnabledTargets = new ArrayList<>();
  private final List<Object> anonymousLikeEnabledTargets = new ArrayList<>();


  public void addTarget(Object target, boolean likeEnabled, boolean anonymousLikeEnabled) {
    if (likeEnabled) {
      LOG.debug("Add target {} for likes", target);
      likeEnabledTargets.add(target);
    }
    if (anonymousLikeEnabled) {
      LOG.debug("Add target {} for anonymous likes", target);
      anonymousLikeEnabledTargets.add(target);
    }
  }

  public void reset() {
    likeEnabledTargets.clear();
    anonymousLikeEnabledTargets.clear();
  }

  public Object getRandomTarget(boolean anonymous) {
    return anonymous ? getRandomTargetForAnonymous() : getRandomTarget();
  }

  private Object getRandomTarget() {
    if (likeEnabledTargets.isEmpty()) {
      LOG.debug("No like enabled targets available!");
      return null;
    }
    return likeEnabledTargets.get(random.nextInt(likeEnabledTargets.size()));
  }

  private Object getRandomTargetForAnonymous() {
    if (anonymousLikeEnabledTargets.isEmpty()) {
      LOG.debug("No anonymous like enabled targets available!");
      return null;
    }
    return anonymousLikeEnabledTargets.get(random.nextInt(anonymousLikeEnabledTargets.size()));
  }

  public void createLike(CommunityUser user, Object target, Collection<String> categories) {
    likeService.updateLike(user, target, categories, true);
    likeCount++;
    LOG.debug("Created like for user {}, anonymous {} and article with target: {}", user.getId(), user.isAnonymous(), target);
  }

  public int getLikeCount() {
    return likeCount;
  }

  public Collection<Object> getLikeEnabledTargets() {
    return likeEnabledTargets;
  }

  public Collection<Object> getAnonymousLikeEnabledTargets() {
    return anonymousLikeEnabledTargets;
  }
}
