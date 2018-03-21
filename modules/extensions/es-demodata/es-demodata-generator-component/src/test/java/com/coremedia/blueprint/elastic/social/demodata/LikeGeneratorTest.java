package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LikeGeneratorTest {
  @InjectMocks
  private LikeGenerator likeGenerator = new LikeGenerator();

  @Mock
  private LikeService likeService;

  @Mock
  private Object target;

  @Mock
  private CommunityUser communityUser;

  @Test
  public void addTarget() {
    likeGenerator.addTarget(target, true, true);
    assertEquals(1, likeGenerator.getAnonymousLikeEnabledTargets().size());
    assertEquals(1, likeGenerator.getLikeEnabledTargets().size());
  }

  @Test
  public void addTargetOnlyAnonymousLikeEnabled() {
    likeGenerator.addTarget(target, false, true);
    assertEquals(1, likeGenerator.getAnonymousLikeEnabledTargets().size());
    assertEquals(0, likeGenerator.getLikeEnabledTargets().size());
  }

  @Test
  public void addTargetOnlyNotAnonymousLikeEnabled() {
    likeGenerator.addTarget(target, true, false);
    assertEquals(0, likeGenerator.getAnonymousLikeEnabledTargets().size());
    assertEquals(1, likeGenerator.getLikeEnabledTargets().size());
  }


  @Test
  public void getRandomTargetNotAnonymous() {
    likeGenerator.addTarget(target, true, false);
    Object randomTarget = likeGenerator.getRandomTarget(false);
    assertEquals(randomTarget, target);
  }

  @Test
  public void getRandomTargetAnonymous() {
    likeGenerator.addTarget(target, true, true);
    Object randomTarget = likeGenerator.getRandomTarget(true);
    assertEquals(randomTarget, target);
  }

  @Test
  public void createLike() {
    Collection<String> categories = new ArrayList<>();
    categories.add("media");
    likeGenerator.createLike(communityUser, target, categories);

    verify(likeService).updateLike(communityUser, target, categories, true);
    assertEquals(1, likeGenerator.getLikeCount());
  }
}
