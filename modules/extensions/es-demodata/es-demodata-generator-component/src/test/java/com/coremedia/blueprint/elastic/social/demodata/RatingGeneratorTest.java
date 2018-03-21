package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.social.api.ratings.RatingService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RatingGeneratorTest {
  @InjectMocks
  private RatingGenerator ratingGenerator = new RatingGenerator();

  @Mock
  private RatingService ratingService;

  @Mock
  private Object target;

  @Mock
  private CommunityUser communityUser;

  @Test
  public void addTarget() {
    ratingGenerator.addTarget(target, true, true);
    assertEquals(1, ratingGenerator.getAnonymousRatingEnabledTargets().size());
    assertEquals(1, ratingGenerator.getRatingEnabledTargets().size());
  }

  @Test
  public void addTargetOnlyAnonymousRatingEnabled() {
    ratingGenerator.addTarget(target, false, true);
    assertEquals(1, ratingGenerator.getAnonymousRatingEnabledTargets().size());
    assertEquals(0, ratingGenerator.getRatingEnabledTargets().size());
  }

  @Test
  public void addTargetOnlyNotAnonymousRatingEnabled() {
    ratingGenerator.addTarget(target, true, false);
    assertEquals(0, ratingGenerator.getAnonymousRatingEnabledTargets().size());
    assertEquals(1, ratingGenerator.getRatingEnabledTargets().size());
  }

  @Test
  public void getRandomTargetNotAnonymous() {
    ratingGenerator.addTarget(target, true, false);
    Object randomTarget = ratingGenerator.getRandomTarget(false);
    assertEquals(randomTarget, target);
  }

  @Test
  public void getRandomTargetAnonymous() {
    ratingGenerator.addTarget(target, true, true);
    Object randomTarget = ratingGenerator.getRandomTarget(true);
    assertEquals(randomTarget, target);
  }

  @Test
  public void createRating() {
    Collection<String> categories = new ArrayList<>();
    categories.add("media");
    ratingGenerator.createRating(communityUser, target, categories);

    verify(ratingService).updateRating(eq(communityUser), eq(target), eq(categories), anyInt());
    assertTrue(ratingGenerator.getRatingCount() > 0);
  }
}
