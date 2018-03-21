package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.elastic.common.CategoryExtractor;
import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.elastic.core.api.settings.Settings;
import com.coremedia.elastic.core.impl.tenant.TenantServiceImpl;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator.STATE_RUNNING;
import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator.STATE_STOPPED;
import static com.coremedia.elastic.core.test.Injection.inject;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DemoDataGeneratorTest {

  @InjectMocks
  private DemoDataGenerator demoDataGenerator;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private UserGenerator userGenerator;

  @Mock
  private CommentGenerator commentGenerator;

  @Mock
  private ReviewGenerator reviewGenerator;

  @Mock
  private LikeGenerator likeGenerator;

  @Mock
  private RatingGenerator ratingGenerator;

  @Mock
  private CategoryExtractor categoryExtractor;

  @Mock
  private CommunityUser user;

  @Mock
  private Content target;

  @Mock
  private Comment comment;

  @Mock
  private Review review;

  @Mock
  private QueryService queryService;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private SettingsService settingsService;

  @Mock
  private ContextStrategy<Content, Content> contextStrategy;

  @Mock
  private ContributionTargetHelper contributionTargetHelper;
  
  private List<String> categories = ImmutableList.of("one", "two");

  @SuppressWarnings("ConstantConditions")
  private final TenantServiceImpl tenantService = new TenantServiceImpl(mock(Settings.class), null);

  @Before
  public void setup() {
    demoDataGenerator.setElasticSocialPlugin(elasticSocialPlugin);

    when(contentRepository.getQueryService()).thenReturn(queryService);
    List<Content> contents = ImmutableList.of(target);
    when(queryService.poseContentQuery("TYPE CMArticle AND isInProduction")).thenReturn(contents);
    String tenant = "tenant";
    tenantService.initialize();
    tenantService.register(tenant);
    tenantService.start();
    tenantService.setCurrent(tenant);
    inject(demoDataGenerator, tenantService);
    when(contributionTargetHelper.getContentFromTarget(any())).thenCallRealMethod();
  }

  @After
  public void tearDown() {
    demoDataGenerator.stop();
  }

  @Test
  public void newUserRate() {
    assertEquals(5, demoDataGenerator.getNewUserRate());
    demoDataGenerator.setNewUserRate(2);
    assertEquals(2, demoDataGenerator.getNewUserRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(5, demoDataGenerator.getNewUserRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidNewUserRate() {
    demoDataGenerator.setNewUserRate(-1);
  }

  @Test
  public void anonymousUserRate() {
    assertEquals(10, demoDataGenerator.getAnonymousUserRate());
    demoDataGenerator.setAnonymousUserRate(5);
    assertEquals(5, demoDataGenerator.getAnonymousUserRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(10, demoDataGenerator.getAnonymousUserRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidAnonymousUserRate() {
    demoDataGenerator.setAnonymousUserRate(-1);
  }

  @Test
  public void complainUserRate() {
    assertEquals(49, demoDataGenerator.getUserComplaintRate());
    demoDataGenerator.setUserComplaintRate(10);
    assertEquals(10, demoDataGenerator.getUserComplaintRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(49, demoDataGenerator.getUserComplaintRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidComplainUserRate() {
    demoDataGenerator.setUserComplaintRate(-1);
  }

  @Test
  public void changeUserRate() {
    assertEquals(7, demoDataGenerator.getUserChangesRate());
    demoDataGenerator.setUserChangesRate(10);
    assertEquals(10, demoDataGenerator.getUserChangesRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(7, demoDataGenerator.getUserChangesRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidChangeUserRate() {
    demoDataGenerator.setUserChangesRate(-1);
  }

  @Test
  public void createLikeRate() {
    assertEquals(2, demoDataGenerator.getCreateLikeRate());
    demoDataGenerator.setCreateLikeRate(10);
    assertEquals(10, demoDataGenerator.getCreateLikeRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(2, demoDataGenerator.getCreateLikeRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCreateLikeRate() {
    demoDataGenerator.setCreateLikeRate(-1);
  }

  @Test
  public void createAnonymousLikeRate() {
    assertEquals(4, demoDataGenerator.getCreateAnonymousLikeRate());
    demoDataGenerator.setCreateAnonymousLikeRate(10);
    assertEquals(10, demoDataGenerator.getCreateAnonymousLikeRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(4, demoDataGenerator.getCreateAnonymousLikeRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCreateAnonymousLikeRate() {
    demoDataGenerator.setCreateAnonymousLikeRate(-1);
  }

  @Test
  public void createRatingRate() {
    assertEquals(2, demoDataGenerator.getCreateRatingRate());
    demoDataGenerator.setCreateRatingRate(10);
    assertEquals(10, demoDataGenerator.getCreateRatingRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(2, demoDataGenerator.getCreateRatingRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCreateRatingRate() {
    demoDataGenerator.setCreateRatingRate(-1);
  }

  @Test
  public void createAnonymousRatingRate() {
    assertEquals(4, demoDataGenerator.getCreateAnonymousRatingRate());
    demoDataGenerator.setCreateAnonymousRatingRate(10);
    assertEquals(10, demoDataGenerator.getCreateAnonymousRatingRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(4, demoDataGenerator.getCreateAnonymousRatingRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCreateAnonymousRatingRate() {
    demoDataGenerator.setCreateAnonymousRatingRate(-1);
  }

  @Test
  public void getTeasablesCommentingEnabledNoModeration() {
    demoDataGenerator.getTeasablesCommentingEnabledNoModeration();
    verify(commentGenerator).getNoModerationTargets();
  }


  @Test
  public void getTeasablesCommentingEnabledPreModeration() {
    demoDataGenerator.getTeasablesCommentingEnabledPreModeration();
    verify(commentGenerator).getPreModerationTargets();
  }

  @Test
  public void getTeasablesCommentingEnabledPostModeration() {
    demoDataGenerator.getTeasablesCommentingEnabledPostModeration();
    verify(commentGenerator).getPostModerationTargets();
  }

  @Test
  public void getTeasablesAnonymousCommentingEnabledNoModeration() {
    demoDataGenerator.getTeasablesAnonymousCommentingEnabledNoModeration();
    verify(commentGenerator).getAnonymousNoModerationTargets();
  }


  @Test
  public void getTeasablesAnonymousCommentingEnabledPreModeration() {
    demoDataGenerator.getTeasablesAnonymousCommentingEnabledPreModeration();
    verify(commentGenerator).getAnonymousPreModerationTargets();
  }

  @Test
  public void getTeasablesAnonymousCommentingEnabledPostModeration() {
    demoDataGenerator.getTeasablesAnonymousCommentingEnabledPostModeration();
    verify(commentGenerator).getAnonymousPostModerationTargets();
  }

  @Test
  public void userModerationType() {
    assertEquals(ModerationType.POST_MODERATION, demoDataGenerator.getUserModerationType());
    demoDataGenerator.setUserModerationType(PRE_MODERATION);
    assertEquals(PRE_MODERATION, demoDataGenerator.getUserModerationType());
  }

  @Test
  public void commentComplaintRate() {
    assertEquals(50, demoDataGenerator.getCommentComplaintRate());
    demoDataGenerator.setCommentComplaintRate(10);
    assertEquals(10, demoDataGenerator.getCommentComplaintRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(50, demoDataGenerator.getCommentComplaintRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCommentComplaintRate() {
    demoDataGenerator.setCommentComplaintRate(-1);
  }

  @Test
  public void anonymousCommentRate() {
    assertEquals(2, demoDataGenerator.getAnonymousCommentRate());
    demoDataGenerator.setAnonymousCommentRate(10);
    assertEquals(10, demoDataGenerator.getAnonymousCommentRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(2, demoDataGenerator.getAnonymousCommentRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidAnonymousCommentRate() {
    demoDataGenerator.setAnonymousCommentRate(-1);
  }  
  
  @Test
  public void replyToCommentRate() {
    assertEquals(5, demoDataGenerator.getReplyToCommentRate());
    demoDataGenerator.setReplyToCommentRate(10);
    assertEquals(10, demoDataGenerator.getReplyToCommentRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(5, demoDataGenerator.getReplyToCommentRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidReplyToCommentRate() {
    demoDataGenerator.setReplyToCommentRate(-1);
  }

  @Test
  public void attachmentOnCommentRate() {
    assertEquals(5, demoDataGenerator.getAttachmentOnCommentRate());
    demoDataGenerator.setAttachmentOnCommentRate(10);
    assertEquals(10, demoDataGenerator.getAttachmentOnCommentRate());
    demoDataGenerator.resetAllSettings();
    assertEquals(5, demoDataGenerator.getAttachmentOnCommentRate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidAttachmentOnCommentRate() {
    demoDataGenerator.setAttachmentOnCommentRate(-1);
  }

  @Test 
  public void getUserCount() {
    demoDataGenerator.getUserCount();
    verify(userGenerator).getUserCount();
  }

  @Test
  public void getUserComplainCount() {
    demoDataGenerator.getUserComplaintCount();
    verify(userGenerator).getUserComplaintCount();
  }

  @Test
  public void getPostModerationUserCount() {
    demoDataGenerator.getPostModerationUserCount();
    verify(userGenerator).getPostModerationUserCount();
  }

  @Test
  public void getPreModerationUserCount() {
    demoDataGenerator.getPreModerationUserCount();
    verify(userGenerator).getPreModerationUserCount();
  }

  @Test
  public void getNoModerationUserCount() {
    demoDataGenerator.getNoModerationUserCount();
    verify(userGenerator).getNoModerationUserCount();
  }

  @Test
  public void getUserChangesPreModerationCount() {
    demoDataGenerator.getUserChangesPreModerationCount();
    verify(userGenerator).getUserChangesPreModerationCount();
  }

  @Test
  public void getUserChangesPostModerationCount() {
    demoDataGenerator.getUserChangesPostModerationCount();
    verify(userGenerator).getUserChangesPostModerationCount();
  }

  @Test
  public void getCommentCount() {
    demoDataGenerator.getCommentCount();
    verify(commentGenerator).getCommentCount();
  }

  @Test
  public void getCommentComplainCount() {
    demoDataGenerator.getCommentComplaintCount();
    verify(commentGenerator).getCommentComplaintCount();
  }

  @Test
  public void getPostModerationCommentCount() {
    demoDataGenerator.getPostModerationCommentCount();
    verify(commentGenerator).getPostModerationCommentCount();
  }

  @Test
  public void getPreModerationCommentCount() {
    demoDataGenerator.getPreModerationCommentCount();
    verify(commentGenerator).getPreModerationCommentCount();
  }

  @Test
  public void getNoModerationCommentCount() {
    demoDataGenerator.getNoModerationCommentCount();
    verify(commentGenerator).getNoModerationCommentCount();
  }

  @Test
  public void getCommentWithAttachmentCount() {
    demoDataGenerator.getCommentWithAttachmentCount();
    verify(commentGenerator).getCommentWithAttachmentCount();
  }

  @Test
  public void getLikeCount() {
    demoDataGenerator.getLikeCount();
    verify(likeGenerator).getLikeCount();
  }

  @Test
  public void getRatingCount() {
    demoDataGenerator.getRatingCount();
    verify(ratingGenerator).getRatingCount();
  }

  @Test
  public void getTeasablesCommentingEnabled() {
    demoDataGenerator.getTeasablesCommentingEnabled();
    verify(commentGenerator).getCommentingEnabledTargets();
  }

  @Test
  public void getTeasablesAnonymousComplainingEnabled() {
    demoDataGenerator.getTeasablesAnonymousComplainingEnabled();
    verify(commentGenerator).getAnonymousComplainingEnabledTargets();
  }

  @Test
  public void getTeasablesAnonymousCommentingEnabled() {
    demoDataGenerator.getTeasablesAnonymousCommentingEnabled();
    verify(commentGenerator).getAnonymousCommentingEnabledTargets();
  }

  @Test
  public void getTeasablesComplainingEnabled() {
    demoDataGenerator.getTeasablesComplainingEnabled();
    verify(commentGenerator).getComplainingEnabledTargets();
  }

  @Test
  public void getInterval() {
    assertEquals(demoDataGenerator.getDefaultInterval(), demoDataGenerator.getInterval());
    demoDataGenerator.setInterval(10);
    assertEquals(10, demoDataGenerator.getInterval());
    demoDataGenerator.resetAllSettings();
    assertEquals(demoDataGenerator.getDefaultInterval(), demoDataGenerator.getInterval());
  }

  @Test(expected = IllegalArgumentException.class)
  public void setInterval() {
    demoDataGenerator.setInterval(-1);
  }

  @Test
  public void createAnonymousComment() {
    setupForComments(true, true, true, true);

    String userName = "anonymous";
    when(commentGenerator.getRandomTarget(true)).thenReturn(target);
    when(commentGenerator.getModerationType(target)).thenReturn(PRE_MODERATION);
    when(commentGenerator.createComment(PRE_MODERATION, user, userName, target, categories, true, true)).thenReturn(comment);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(userGenerator.getRandomUserName()).thenReturn(userName);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createComment();

    verify(commentGenerator).createComment(PRE_MODERATION, user, userName, target, categories, true, true);
    verify(commentGenerator).complainOnComment(target, comment, true);
  }

  @Test
  public void createComment() {
    setupForComments(false, true, false, false);

    when(commentGenerator.getRandomTarget(anyBoolean())).thenReturn(target);
    when(commentGenerator.getModerationType(target)).thenReturn(PRE_MODERATION);

    when(userGenerator.getRandomUser()).thenReturn(user);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createComment();

    verify(commentGenerator).createComment(eq(PRE_MODERATION), eq(user), isNull(), eq(target), eq(categories), eq(true), eq(false));
    verify(commentGenerator, never()).complainOnComment(any(), any(Comment.class), eq(true));
  }

  @Test
  public void createAnonymousReview() {
    String userName = "anonymous";
    // make sure to create an anonymous review
    demoDataGenerator.setAnonymousReviewRate(1);
    demoDataGenerator.setAttachmentOnReviewRate(0);
    demoDataGenerator.setReviewComplaintRate(1);

    when(reviewGenerator.getRandomTarget(true)).thenReturn(target);
    when(reviewGenerator.createReview(PRE_MODERATION, user, userName, target, categories, false)).thenReturn(review);
    when(reviewGenerator.getModerationType(target)).thenReturn(PRE_MODERATION);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(userGenerator.getRandomUserName()).thenReturn(userName);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createReview();

    verify(reviewGenerator).createReview(PRE_MODERATION, user, userName, target, categories, false);
    verify(reviewGenerator).complainOnComment(eq(target), any(Review.class), eq(true));
  }

  @Test
  public void createReview() {
    // make sure not to create an anonymous review
    demoDataGenerator.setAnonymousReviewRate(0);
    demoDataGenerator.setAttachmentOnReviewRate(0);
    demoDataGenerator.setReviewComplaintRate(0);

    when(reviewGenerator.getRandomTarget(false)).thenReturn(target);
    when(reviewGenerator.getModerationType(target)).thenReturn(PRE_MODERATION);
    when(userGenerator.getRandomUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createReview();

    verify(reviewGenerator).createReview(PRE_MODERATION, user, null, target, categories, false);
  }


  @Test
  public void createCommentWithComplaint() {
    setupForComments(false, true, false, true);

    when(commentGenerator.getRandomTarget(anyBoolean())).thenReturn(target);
    when(commentGenerator.getModerationType(target)).thenReturn(PRE_MODERATION);

    when(userGenerator.getRandomUser()).thenReturn(user);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createComment();

    verify(commentGenerator).createComment(eq(PRE_MODERATION), eq(user), isNull(), eq(target), eq(categories), eq(true), eq(false));
    verify(commentGenerator).complainOnComment(any(), nullable(Comment.class),  eq(false));
  }

  @Test
  public void createAnonymousLike() {
    demoDataGenerator.setCreateAnonymousLikeRate(1);

    when(likeGenerator.getRandomTarget(true)).thenReturn(target);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createLike();

    verify(likeGenerator).createLike(user, target, categories);
  }

  @Test
  public void createComments() {
    setupForComments(false, true, false, false);

    when(commentGenerator.getRandomTarget(anyBoolean())).thenReturn(target);
    when(commentGenerator.getModerationType(target)).thenReturn(PRE_MODERATION);

    when(userGenerator.getRandomUser()).thenReturn(user);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(categoryExtractor.getCategories(target, null)).thenReturn(categories);

    int numberOfComments = 5;
    demoDataGenerator.createComments(numberOfComments);

    verify(commentGenerator, times(numberOfComments)).createComment(eq(PRE_MODERATION), eq(user), isNull(), eq(target), eq(categories), eq(true), eq(false));
    verify(commentGenerator, never()).complainOnComment(any(), any(Comment.class), eq(true));
  }

  @Test
  public void createLike() {
    demoDataGenerator.setCreateAnonymousLikeRate(0);

    demoDataGenerator.setCount(1);
    demoDataGenerator.setCreateLikeRate(1);

    when(likeGenerator.getRandomTarget(false)).thenReturn(target);
    when(userGenerator.getRandomUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createLike();

    verify(likeGenerator).createLike(user, target, categories);
  }

  @Test
  public void createAnonymousRating() {
    demoDataGenerator.setCreateAnonymousRatingRate(1);

    when(ratingGenerator.getRandomTarget(true)).thenReturn(target);
    when(userGenerator.createAnonymousUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createRating();

    verify(ratingGenerator).createRating(user, target, categories);
  }

  @Test
  public void createRating() {
    demoDataGenerator.setCreateAnonymousRatingRate(0);
    demoDataGenerator.setCount(1);
    demoDataGenerator.setCreateRatingRate(1);

    when(ratingGenerator.getRandomTarget(false)).thenReturn(target);
    when(userGenerator.getRandomUser()).thenReturn(user);
    when(categoryExtractor.getCategories(any(Content.class), nullable(Content.class))).thenReturn(categories);

    demoDataGenerator.createRating();

    verify(ratingGenerator).createRating(user, target, categories);
  }

  @Test
  public void createUser() {
    demoDataGenerator.setCount(1);
    demoDataGenerator.setNewUserRate(1);
    demoDataGenerator.setAnonymousUserRate(0);

    demoDataGenerator.createUser();

    verify(userGenerator).createUser(demoDataGenerator.getUserModerationType());
  }

  @Test
  public void createAnonymousUser() {
    demoDataGenerator.setAnonymousUserRate(1);
    demoDataGenerator.setNewUserRate(1);
    demoDataGenerator.setAnonymousUserRate(1);

    demoDataGenerator.createUser();

    verify(userGenerator).createAnonymousUser();
  }

  @Test
  public void createUserComplain() {
    demoDataGenerator.setUserComplaintRate(1);

    when(userGenerator.getRandomUser()).thenReturn(user);
    when(user.isActivated()).thenReturn(true);

    demoDataGenerator.createUserComplain();

    verify(userGenerator).complainOnUser(user);
  }

  @Test
  public void changeUser() {
    demoDataGenerator.setCount(1);
    demoDataGenerator.setUserChangesRate(1);
    demoDataGenerator.setAnonymousUserRate(0);

    when(userGenerator.getRandomUser()).thenReturn(user);

    demoDataGenerator.changeUser();

    verify(userGenerator).changeUserDetails(any(ModerationType.class), eq(user));
  }
  
  @Test
  public void initialize() {
    demoDataGenerator.initialize();

    verify(userGenerator).initialize();
    verify(commentGenerator).initialize();
  }

  @Test
  public void startStop() {
    Assert.assertEquals(STATE_STOPPED, demoDataGenerator.getStatus());
    demoDataGenerator.start();
    Assert.assertEquals(STATE_RUNNING, demoDataGenerator.getStatus());
    demoDataGenerator.stop();
    Assert.assertEquals(STATE_STOPPED, demoDataGenerator.getStatus());
    demoDataGenerator.restart();
    Assert.assertEquals(STATE_RUNNING, demoDataGenerator.getStatus());
    demoDataGenerator.stop();

  }

  private void setupForComments(boolean anonymous, boolean attachment, boolean reply, boolean complaint) {
    demoDataGenerator.setAnonymousCommentRate(anonymous ? 1 : 0);
    demoDataGenerator.setAttachmentOnCommentRate(attachment ? 1 : 0);
    demoDataGenerator.setReplyToCommentRate(reply ? 1 : 0);
    demoDataGenerator.setCommentComplaintRate(complaint ? 1 : 0);
  }
}
