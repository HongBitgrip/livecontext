package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.social.api.ratings.RatingService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;

/**
 * An {@link RatingGenerator} generates ratings for a article.
 */
public class RatingGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(RatingGenerator.class);
  private static final int DEFAULT_RATING_INTERVAL = 5;

  @Inject
  private RatingService ratingService;

  private int ratingCount = 0;

  private final List<Object> ratingEnabledTargets = new ArrayList<>();
  private final List<Object> anonymousRatingEnabledTargets = new ArrayList<>();

  private final Random random = new Random();

  public void addTarget(Object target, boolean ratingEnabled, boolean anonymousRatingEnabled) {
    if (ratingEnabled) {
      LOG.debug("Add target {} for rating", target);
      ratingEnabledTargets.add(target);
    }
    if (anonymousRatingEnabled) {
      LOG.debug("Add target {} for anonymous rating", target);
      anonymousRatingEnabledTargets.add(target);
    }
  }

  public void reset() {
    ratingEnabledTargets.clear();
    anonymousRatingEnabledTargets.clear();
  }

  public Object getRandomTarget(boolean anonymous) {
    return anonymous ? getRandomTargetForAnonymous() : getRandomTarget();
  }

  private Object getRandomTarget() {
    if (ratingEnabledTargets.isEmpty()) {
      LOG.debug("No rating enabled targets available!");
      return null;
    }
    return ratingEnabledTargets.get(random.nextInt(ratingEnabledTargets.size()));
  }

  private Object getRandomTargetForAnonymous() {
    if (anonymousRatingEnabledTargets.isEmpty()) {
      LOG.debug("No anonymous rating enabled targets available!");
      return null;
    }
    return anonymousRatingEnabledTargets.get(random.nextInt(anonymousRatingEnabledTargets.size()));
  }

  protected void createRating(CommunityUser user, Object target, Collection<String> categories) {
    int rating = random.nextInt(DEFAULT_RATING_INTERVAL) + 1;  //only values from 1 - 5
    ratingService.updateRating(user, target, categories, rating);
    ratingCount++;
    LOG.debug(PERSONAL_DATA, "Created rating for user {}, anonymous {}, rating {} and target with id: {}", user.getId(), user.isAnonymous(), rating, target);
  }

  public int getRatingCount() {
    return ratingCount;
  }

  public Collection<Object> getRatingEnabledTargets() {
    return ratingEnabledTargets;
  }

  public Collection<Object> getAnonymousRatingEnabledTargets() {
    return anonymousRatingEnabledTargets;
  }
}
