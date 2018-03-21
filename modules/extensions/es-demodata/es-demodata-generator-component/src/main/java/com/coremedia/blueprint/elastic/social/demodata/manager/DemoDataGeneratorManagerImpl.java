package com.coremedia.blueprint.elastic.social.demodata.manager;

import com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator;
import com.coremedia.elastic.social.api.ModerationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Locale;

import static java.util.Locale.ENGLISH;

@Named
@ManagedResource(description = "Management operations of the DemoDataGenerator")
public class DemoDataGeneratorManagerImpl implements DemoDataGeneratorManager {

  @Inject
  private DemoDataGenerator demoDataGenerator;

  @Override
  public void start() {
    demoDataGenerator.start();
  }

  @Override
  public void stop() {
    demoDataGenerator.stop();
  }

  @Override
  public void restart() {
    demoDataGenerator.restart();
  }

  @Override
  public String getStatus() {
    return demoDataGenerator.getStatus();
  }

  @Override
  public void resetAllSettings() {
    demoDataGenerator.resetAllSettings();
  }

  @Override
  public int getInterval() {
    return demoDataGenerator.getInterval();
  }

  @Override
  public void setInterval(int interval) {
    demoDataGenerator.setInterval(interval);
  }

  @Override
  public int getCommentComplaintRate() {
    return demoDataGenerator.getCommentComplaintRate();
  }

  @Override
  public void setCommentComplaintRate(int rate) {
    demoDataGenerator.setCommentComplaintRate(rate);
  }

  @Override
  public int getAnonymousCommentRate() {
    return demoDataGenerator.getAnonymousCommentRate();
  }

  @Override
  public void setAnonymousCommentRate(int rate) {
    demoDataGenerator.setAnonymousCommentRate(rate);
  }

  @Override
  public int getCommentReplyRate() {
    return demoDataGenerator.getReplyToCommentRate();
  }

  @Override
  public void setCommentReplyRate(int rate) {
    demoDataGenerator.setReplyToCommentRate(rate);
  }

  @Override
  public int getNewUserRate() {
    return demoDataGenerator.getNewUserRate();
  }

  @Override
  public void setNewUserRate(int rate) {
    demoDataGenerator.setNewUserRate(rate);
  }

  @Override
  public void setAnonymousUserRate(int rate) {
    demoDataGenerator.setAnonymousUserRate(rate);
  }

  @Override
  public int getAnonymousUserRate() {
    return demoDataGenerator.getAnonymousUserRate();
  }

  @Override
  public int getUserComplaintRate() {
    return demoDataGenerator.getUserComplaintRate();
  }

  @Override
  public void setUserComplaintRate(int rate) {
    demoDataGenerator.setUserComplaintRate(rate);
  }

  @Override
  public int getUserChangesRate() {
    return demoDataGenerator.getUserChangesRate();
  }

  @Override
  public void setUserChangesRate(int rate) {
    demoDataGenerator.setUserChangesRate(rate);
  }

  @Override
  public int getCommentWithAttachmentsRate() {
    return demoDataGenerator.getAttachmentOnCommentRate();
  }

  @Override
  public void setCommentWithAttachmentsRate(int rate) {
    demoDataGenerator.setAttachmentOnCommentRate(rate);
  }

  @Override
  public void setUserModerationType(String moderationType) {
    demoDataGenerator.setUserModerationType(Enum.valueOf(ModerationType.class, moderationType.toUpperCase(ENGLISH)));
  }

  @Override
  public String getUserModerationType() {
    return demoDataGenerator.getUserModerationType().toString();
  }

  @Override
  public int getLikeRate() {
    return demoDataGenerator.getCreateLikeRate();
  }

  @Override
  public void setLikeRate(int rate) {
    demoDataGenerator.setCreateLikeRate(rate);
  }

  @Override
  public int getAnonymousLikeRate() {
     return demoDataGenerator.getCreateAnonymousLikeRate();
  }

  @Override
  public void setAnonymousLikeRate(int rate) {
    demoDataGenerator.setCreateAnonymousLikeRate(rate);
  }

  @Override
  public int getRatingRate() {
    return demoDataGenerator.getCreateRatingRate();
  }

  @Override
  public void setRatingRate(int rate) {
   demoDataGenerator.setCreateRatingRate(rate);
  }

  @Override
  public int getAnonymousRatingRate() {
    return demoDataGenerator.getCreateAnonymousRatingRate();
  }

  @Override
  public void setAnonymousRatingRate(int rate) {
    demoDataGenerator.setCreateAnonymousRatingRate(rate);
  }

  @Override
  public int getNumberOfTeasablesForCommenting(String moderationType) {
    if (StringUtils.isBlank(moderationType)) {
      return demoDataGenerator.getTeasablesCommentingEnabled().size();
    }
    ModerationType type = ModerationType.valueOf(moderationType.toUpperCase(Locale.ENGLISH));
    if (ModerationType.POST_MODERATION.equals(type)) {
      return demoDataGenerator.getTeasablesCommentingEnabledPostModeration().size();
    } else if (ModerationType.PRE_MODERATION.equals(type)) {
      return demoDataGenerator.getTeasablesCommentingEnabledPreModeration().size();
    } else {
      // ModerationType.NONE
      return demoDataGenerator.getTeasablesCommentingEnabledNoModeration().size();
    }
  }

  @Override
  public int getNumberOfTeasablesForAnonymousCommenting(String moderationType) {
    if (StringUtils.isBlank(moderationType)) {
      return demoDataGenerator.getTeasablesAnonymousCommentingEnabled().size();
    }
    ModerationType type = ModerationType.valueOf(moderationType.toUpperCase(Locale.ENGLISH));
    if (ModerationType.POST_MODERATION.equals(type)) {
      return demoDataGenerator.getTeasablesAnonymousCommentingEnabledPostModeration().size();
    } else if (ModerationType.PRE_MODERATION.equals(type)) {
      return demoDataGenerator.getTeasablesAnonymousCommentingEnabledPreModeration().size();
    } else {
      // ModerationType.NONE
      return demoDataGenerator.getTeasablesAnonymousCommentingEnabledNoModeration().size();
    }
  }

  @Override
  public int getCommentCount() {
    return demoDataGenerator.getCommentCount();
  }

  @Override
  public int getCommentComplaintCount() {
    return demoDataGenerator.getCommentComplaintCount();
  }

  @Override
  public int getCommentWithAttachmentCount() {
    return demoDataGenerator.getCommentWithAttachmentCount();
  }

  @Override
  public int getPostModerationCommentCount() {
    return demoDataGenerator.getPostModerationCommentCount();
  }

  @Override
  public int getPreModerationCommentCount() {
    return demoDataGenerator.getPreModerationCommentCount();
  }

  @Override
  public int getNoModerationCommentCount() {
    return demoDataGenerator.getNoModerationCommentCount();
  }

  @Override
  public int getUserCount() {
    return demoDataGenerator.getUserCount();
  }

  @Override
  public int getUserComplaintCount() {
    return demoDataGenerator.getUserComplaintCount();
  }

  @Override
  public int getPostModerationUserCount() {
    return demoDataGenerator.getPostModerationUserCount();
  }

  @Override
  public int getPreModerationUserCount() {
    return demoDataGenerator.getPreModerationUserCount();
  }

  @Override
  public int getNoModerationUserCount() {
    return demoDataGenerator.getNoModerationUserCount();
  }

  @Override
  public int getLikeCount() {
    return demoDataGenerator.getLikeCount();
  }

  @Override
  public int getRatingCount() {
    return demoDataGenerator.getRatingCount();
  }

  @Override
  public String getTargetDoctype() {
    return demoDataGenerator.getTargetDoctype();
  }

  @Override
  public void setTargetDoctype(String targetDoctype) {
    demoDataGenerator.setTargetDoctype(targetDoctype);
  }

  @Override
  public void createAnonymousUsers(int count) {
    demoDataGenerator.createAnonymousUsers(count);
  }
}
