package com.coremedia.blueprint.elastic.social.demodata.manager;

import com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static com.coremedia.elastic.social.api.ModerationType.NONE;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataGeneratorManagerImplTest {
  @InjectMocks
  private DemoDataGeneratorManagerImpl demoDataGeneratorManager = new DemoDataGeneratorManagerImpl();

  @Mock
  private DemoDataGenerator demoDataGenerator;

  @Test
  public void start() {
    demoDataGeneratorManager.start();
    verify(demoDataGenerator).start();
  }

  @Test
  public void restart() {
    demoDataGeneratorManager.restart();
    verify(demoDataGenerator).restart();
  }

  @Test
  public void getStatus() {
    demoDataGeneratorManager.getStatus();
    verify(demoDataGenerator).getStatus();
  }

  @Test
  public void stop() {
    demoDataGeneratorManager.stop();
    verify(demoDataGenerator).stop();
  }

  @Test
  public void resetAllSettings() {
    demoDataGeneratorManager.resetAllSettings();
    verify(demoDataGenerator).resetAllSettings();
  }

  @Test
  public void getInterval() {
    demoDataGeneratorManager.getInterval();
    verify(demoDataGenerator).getInterval();
  }

  @Test
  public void setInterval() {
    demoDataGeneratorManager.setInterval(5);
    verify(demoDataGenerator).setInterval(5);
  }

  @Test
  public void getCommentComplaintRate() {
    demoDataGeneratorManager.getCommentComplaintRate();
    verify(demoDataGenerator).getCommentComplaintRate();
  }

  @Test
  public void setCommentComplaintRate() {
    demoDataGeneratorManager.setCommentComplaintRate(5);
    verify(demoDataGenerator).setCommentComplaintRate(5);
  }

  @Test
  public void getAnonymousCommentRate() {
    demoDataGeneratorManager.getAnonymousCommentRate();
    verify(demoDataGenerator).getAnonymousCommentRate();
  }

  @Test
  public void setAnonymousCommentRate() {
    demoDataGeneratorManager.setAnonymousCommentRate(5);
    verify(demoDataGenerator).setAnonymousCommentRate(5);
  }

  @Test
  public void getCommentReplyRate() {
    demoDataGeneratorManager.getCommentReplyRate();
    verify(demoDataGenerator).getReplyToCommentRate();
  }

  @Test
  public void setCommentReplyRate() {
    demoDataGeneratorManager.setCommentReplyRate(5);
    verify(demoDataGenerator).setReplyToCommentRate(5);
  }

  @Test
  public void getNewUserRate() {
    demoDataGeneratorManager.getNewUserRate();
    verify(demoDataGenerator).getNewUserRate();
  }

  @Test
  public void setNewUserRate() {
    demoDataGeneratorManager.setNewUserRate(5);
    verify(demoDataGenerator).setNewUserRate(5);
  }

  @Test
  public void setAnonymousUserRate() {
    demoDataGeneratorManager.setAnonymousUserRate(5);
    verify(demoDataGenerator).setAnonymousUserRate(5);
  }

  @Test
  public void getAnonymousUserRate() {
    demoDataGeneratorManager.getAnonymousUserRate();
    verify(demoDataGenerator).getAnonymousUserRate();
  }

  @Test
  public void getUserComplaintRate() {
    demoDataGeneratorManager.getUserComplaintRate();
    verify(demoDataGenerator).getUserComplaintRate();
  }

  @Test
  public void setUserComplaintRate() {
    demoDataGeneratorManager.setUserComplaintRate(5);
    verify(demoDataGenerator).setUserComplaintRate(5);
  }

  @Test
  public void getUserChangesRate() {
    demoDataGeneratorManager.getUserChangesRate();
    verify(demoDataGenerator).getUserChangesRate();
  }

  @Test
  public void setUserChangesRate() {
    demoDataGeneratorManager.setUserChangesRate(5);
    verify(demoDataGenerator).setUserChangesRate(5);
  }

  @Test
  public void getCommentWithAttachmentsRate() {
    demoDataGeneratorManager.getCommentWithAttachmentsRate();
    verify(demoDataGenerator).getAttachmentOnCommentRate();
  }

  @Test
  public void setCommentWithAttachmentsRate() {
    demoDataGeneratorManager.setCommentWithAttachmentsRate(5);
    verify(demoDataGenerator).setAttachmentOnCommentRate(5);
  }

  @Test
  public void getLikeRate() {
    demoDataGeneratorManager.getLikeRate();
    verify(demoDataGenerator).getCreateLikeRate();
  }

  @Test
  public void setLikeRate() {
    demoDataGeneratorManager.setLikeRate(5);
    verify(demoDataGenerator).setCreateLikeRate(5);
  }

  @Test
  public void getAnonymousLikeRate() {
    demoDataGeneratorManager.getAnonymousLikeRate();
    verify(demoDataGenerator).getCreateAnonymousLikeRate();
  }

  @Test
  public void setAnonymousLikeRate() {
    demoDataGeneratorManager.setAnonymousLikeRate(5);
    verify(demoDataGenerator).setCreateAnonymousLikeRate(5);
  }

  @Test
  public void getRatingRate() {
    demoDataGeneratorManager.getRatingRate();
    verify(demoDataGenerator).getCreateRatingRate();
  }

  @Test
  public void setRatingRate() {
    demoDataGeneratorManager.setRatingRate(5);
    verify(demoDataGenerator).setCreateRatingRate(5);
  }

  @Test
  public void getAnonymousRatingRate() {
    demoDataGeneratorManager.getAnonymousRatingRate();
    verify(demoDataGenerator).getCreateAnonymousRatingRate();
  }

  @Test
  public void setAnonymousRatingRate() {
    demoDataGeneratorManager.setAnonymousRatingRate(5);
    verify(demoDataGenerator).setCreateAnonymousRatingRate(5);
  }

  @Test
  public void setUserModerationType() {
    demoDataGeneratorManager.setUserModerationType("NONE");
    verify(demoDataGenerator).setUserModerationType(NONE);
  }

  @Test
  public void getUserModerationType() {
    when(demoDataGenerator.getUserModerationType()).thenReturn(POST_MODERATION);
    demoDataGeneratorManager.getUserModerationType();
    verify(demoDataGenerator).getUserModerationType();
  }

  @Test
  public void getNumberOfTeasablesForCommentingAll() {
    when(demoDataGenerator.getTeasablesCommentingEnabled()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForCommenting("");
    verify(demoDataGenerator).getTeasablesCommentingEnabled();
  }

  @Test
  public void getNumberOfTeasablesForCommentingPostModeration() {
    when(demoDataGenerator.getTeasablesCommentingEnabledPostModeration()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForCommenting("POST_MODERATION");
    verify(demoDataGenerator).getTeasablesCommentingEnabledPostModeration();
  }

  @Test
  public void getNumberOfTeasablesForCommentingPreModeration() {
    when(demoDataGenerator.getTeasablesCommentingEnabledPreModeration()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForCommenting("PRE_MODERATION");
    verify(demoDataGenerator).getTeasablesCommentingEnabledPreModeration();
  }

  @Test
  public void getNumberOfTeasablesForCommentingNoModeration() {
    when(demoDataGenerator.getTeasablesCommentingEnabledNoModeration()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForCommenting("NONE");
    verify(demoDataGenerator).getTeasablesCommentingEnabledNoModeration();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getNumberOfTeasablesForCommentingInvalidModeration() {
    demoDataGeneratorManager.getNumberOfTeasablesForCommenting("1234");
  }

  @Test
  public void getNumberOfTeasablesForAnonymousCommentingAll() {
    when(demoDataGenerator.getTeasablesAnonymousCommentingEnabled()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForAnonymousCommenting("");
    verify(demoDataGenerator).getTeasablesAnonymousCommentingEnabled();
  }

  @Test
  public void getNumberOfTeasablesForAnonymousCommentingPostModeration() {
    when(demoDataGenerator.getTeasablesAnonymousCommentingEnabledPostModeration()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForAnonymousCommenting("POST_MODERATION");
    verify(demoDataGenerator).getTeasablesAnonymousCommentingEnabledPostModeration();
  }

  @Test
  public void getNumberOfTeasablesForAnonymousCommentingPreModeration() {
    when(demoDataGenerator.getTeasablesAnonymousCommentingEnabledPreModeration()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForAnonymousCommenting("PRE_MODERATION");
    verify(demoDataGenerator).getTeasablesAnonymousCommentingEnabledPreModeration();
  }

  @Test
  public void getNumberOfTeasablesForAnonymousCommentingNoModeration() {
    when(demoDataGenerator.getTeasablesAnonymousCommentingEnabledNoModeration()).thenReturn(Collections.emptyList());
    demoDataGeneratorManager.getNumberOfTeasablesForAnonymousCommenting("NONE");
    verify(demoDataGenerator).getTeasablesAnonymousCommentingEnabledNoModeration();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getNumberOfTeasablesForAnonymousCommentingInvalidModeration() {
    demoDataGeneratorManager.getNumberOfTeasablesForAnonymousCommenting("1234");
  }

  @Test
  public void getCommentCount() {
    demoDataGeneratorManager.getCommentCount();
    verify(demoDataGenerator).getCommentCount();
  }

  @Test
  public void getCommentComplaintCount() {
    demoDataGeneratorManager.getCommentComplaintCount();
    verify(demoDataGenerator).getCommentComplaintCount();
  }

  @Test
  public void getCommentWithAttachmentCount() {
    demoDataGeneratorManager.getCommentWithAttachmentCount();
    verify(demoDataGenerator).getCommentWithAttachmentCount();
  }

  @Test
  public void getPostModerationCommentCount() {
    demoDataGeneratorManager.getPostModerationCommentCount();
    verify(demoDataGenerator).getPostModerationCommentCount();
  }

  @Test
  public void getPreModerationCommentCount() {
    demoDataGeneratorManager.getPreModerationCommentCount();
    verify(demoDataGenerator).getPreModerationCommentCount();
  }

  @Test
  public void getNoModerationCommentCount() {
    demoDataGeneratorManager.getNoModerationCommentCount();
    verify(demoDataGenerator).getNoModerationCommentCount();
  }

  @Test
  public void getUserCount() {
    demoDataGeneratorManager.getUserCount();
    verify(demoDataGenerator).getUserCount();
  }

  @Test
  public void getUserComplaintCount() {
    demoDataGeneratorManager.getUserComplaintCount();
    verify(demoDataGenerator).getUserComplaintCount();
  }

  @Test
  public void getPostModerationUserCount() {
    demoDataGeneratorManager.getPostModerationUserCount();
    verify(demoDataGenerator).getPostModerationUserCount();
  }

  @Test
  public void getPreModerationUserCount() {
    demoDataGeneratorManager.getPreModerationUserCount();
    verify(demoDataGenerator).getPreModerationUserCount();
  }

  @Test
  public void getNoModerationUserCount() {
    demoDataGeneratorManager.getNoModerationUserCount();
    verify(demoDataGenerator).getNoModerationUserCount();
  }

  @Test
  public void getLikeCount() {
    demoDataGeneratorManager.getLikeCount();
    verify(demoDataGenerator).getLikeCount();
  }

  @Test
  public void getRatingCount() {
    demoDataGeneratorManager.getRatingCount();
    verify(demoDataGenerator).getRatingCount();
  }
}
