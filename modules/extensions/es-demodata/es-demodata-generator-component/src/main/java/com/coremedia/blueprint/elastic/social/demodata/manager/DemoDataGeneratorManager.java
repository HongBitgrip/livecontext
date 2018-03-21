package com.coremedia.blueprint.elastic.social.demodata.manager;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * JMX management interface of the {@link com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator}.
 */
@ManagedResource(description = "JMX management interface of the {@link com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator}.")
public interface DemoDataGeneratorManager {

  /**
   * Starts demo data generation
   */
  @ManagedOperation
  void start();

  /**
   * Stops demo data generation
   */
  @ManagedOperation
  void stop();

  /**
   * Restarts demo data generation
   */
  @ManagedOperation
  void restart();

  /**
   * Returns the status of demo data generation
   *
   * @return the status of demo data generation
   */
  @ManagedOperation
  String getStatus();

  /**
   * Resets all settings to the default values
   */
  @ManagedOperation
  void resetAllSettings();

  /**
   * Returns the number of teasables with commenting enabled with the given moderation type or of all teasables if no moderation type is given
   *
   * @param moderationType the moderation type
   * @return the number of all teasables with commenting enabled with the given moderation type or of all teasables if no moderation type is given
   */
  @ManagedOperation
  @ManagedOperationParameters({
          @ManagedOperationParameter(name = "moderationType", description = "valid values are <EMPTY>, NONE, PRE_MODERATION and POST_MODERATION")
  })
  int getNumberOfTeasablesForCommenting(String moderationType);

  /**
   * Returns the number of teasables with commenting enabled for anonymous users with the given moderation type or of all teasables if no moderation type is given
   *
   * @param moderationType the moderation type
   * @return the number of teasables with commenting enabled for anonymous users with the given moderation type or of all teasables if no moderation type is given
   */
  @ManagedOperation
  @ManagedOperationParameters({
          @ManagedOperationParameter(name = "moderationType", description = "valid values are <EMPTY>, NONE, PRE_MODERATION and POST_MODERATION")
  })
  int getNumberOfTeasablesForAnonymousCommenting(String moderationType);

  /**
   * Sets the interval for demo data generation in seconds. A comment will be generated each interval.
   *
   * @param  interval interval in seconds
   */
  @ManagedAttribute(description = "Sets the interval for demo data generation in seconds")
  void setInterval(int interval);

  /**
   * Returns the interval for demo data generation in seconds. A comment will be generated each interval.
   *
   * @return the interval for demo data generation in seconds
   */
  @ManagedAttribute(description = "Returns the interval for demo data generation in seconds")
  int getInterval();

  /**
   * Returns the comment complaint rate.
   *
   * @return the comment complaint rate
   */
  @ManagedAttribute(description = "Returns the comment complaint rate.")
  int getCommentComplaintRate();

  /**
   * Sets the comment complaint rate.
   *
   * @param rate comment complaint rate
   */
  @ManagedAttribute(description = "Sets the comment complaint rate.")
  void setCommentComplaintRate(int rate);

  /**
   * Returns the anonymous comment rate of newly created comments
   *
   * @return the anonymous comment rate of newly created comments
   */
  @ManagedAttribute(description = "Returns the anonymous comment rate.")
  int getAnonymousCommentRate();

  /**
   * Sets the anonymous comment rate of newly created comments
   *
   * @param rate the anonymous comment rate of newly created comments
   */
  @ManagedAttribute(description = "Sets the anonymous comment rate.")
  void setAnonymousCommentRate(int rate);

  /**
   * Returns the rate for comment replies
   *
   * @return the rate for comment replies
   */
  @ManagedAttribute(description = "Returns the rate for comment replies.")
  int getCommentReplyRate();

  /**
   * Sets the rate for comment replies
   *
   * @param rate the rate for comment replies
   */
  @ManagedAttribute(description = "Sets the rate for comment replies.")
  void setCommentReplyRate(int rate);

  /**
   * Returns the new user rate
   *
   * @return the new user rate
   */
  @ManagedAttribute(description = "Returns the new user rate.")
  int getNewUserRate();

  /**
   * Sets the new user rate
   *
   * @param rate the new user rate
   */
  @ManagedAttribute(description = "Sets the new user rate.")
  void setNewUserRate(int rate);

  /**
   * Returns the anonymous user rate of newly created users
   *
   * @return the anonymous user rate of newly created users
   */
  @ManagedAttribute(description = "Returns the anonymous user rate of newly created users.")
  int getAnonymousUserRate();

  /**
   * Sets the anonymous user rate of newly created users.
   *
   * @param rate the anonymous user rate of newly created users
   */
  @ManagedAttribute(description = "Sets the anonymous user rate of newly created users.")
  void setAnonymousUserRate(int rate);

  /**
   * Returns the rate of complaints about a user.
   *
   * @return the rate of complaints about a user
   */
  @ManagedAttribute(description = "Returns the rate of complaints about a user.")
  int getUserComplaintRate();

  /**
   * Sets the rate of complaints about a user
   *
   * @param rate the rate of complaints about a user
   */
  @ManagedAttribute(description = "Sets the rate of complaints about a user.")
  void setUserComplaintRate(int rate);

  /**
   * Returns the rate for changing users.
   *
   * @return the rate for changing users
   */
  @ManagedAttribute(description = "Returns the rate for changing users.")
  int getUserChangesRate();

  /**
   * Sets the rate for changing users.
   *
   * @param rate the rate for changing users
   */
  @ManagedAttribute(description = "Sets the rate for changing users.")
  void setUserChangesRate(int rate);

  /**
   * Returns the rate for comment attachments of newly created comments.
   *
   * @return the rate for comment attachments of newly created comments
   */
  @ManagedAttribute(description = "Returns the rate for comment attachments.")
  int getCommentWithAttachmentsRate();

  /**
   * Sets the attachment on comment rate of newly created comments.
   *
   * @param rate the rate for comment attachments of newly created comments
   */
  @ManagedAttribute(description = "Sets the rate for comment attachments.")
  void setCommentWithAttachmentsRate(int rate);

  /**
   * Sets the moderation type used for user creation.
   * @param moderationType the moderation type used for user creation
   */
  @ManagedAttribute
  void setUserModerationType(String moderationType);

  /**
   * Returns the moderation type used for user creation.
   * @return the moderation type used for user creation
   */
  @ManagedAttribute
  String getUserModerationType();

  /**
   * Returns the new like rate
   *
   * @return the new like rate
   */
  @ManagedAttribute(description = "Returns the new like rate.")
  int getLikeRate();

  /**
   * Sets the like rate
   *
   * @param rate the like rate
   */
  @ManagedAttribute(description = "Sets the like rate.")
  void setLikeRate(int rate);

  /**
   * Returns the anonymous like rate of newly created likes
   *
   * @return the anonymous like rate of newly created likes
   */
  @ManagedAttribute(description = "Returns the anonymous like rate of newly created likes.")
  int getAnonymousLikeRate();

  /**
   * Sets the anonymous like rate of newly created likes.
   *
   * @param rate the anonymous like rate of newly created likes
   */
  @ManagedAttribute(description = "Sets the anonymous like rate of newly created likes.")
  void setAnonymousLikeRate(int rate);

  /**
   * Returns the rating rate
   *
   * @return the rating rate
   */
  @ManagedAttribute(description = "Returns the new rating rate.")
  int getRatingRate();

  /**
   * Sets the new rating rate
   *
   * @param rate the new rating rate
   */
  @ManagedAttribute(description = "Sets the new rating rate.")
  void setRatingRate(int rate);

  /**
   * Returns the anonymous rating rate of newly created ratings
   *
   * @return the anonymous rating rate of newly created ratings
   */
  @ManagedAttribute(description = "Returns the anonymous rating rate of newly created ratings.")
  int getAnonymousRatingRate();

  /**
   * Sets the anonymous rating rate of newly created ratings.
   *
   * @param rate the anonymous rating rate of newly created ratings
   */
  @ManagedAttribute(description = "Sets the anonymous rating rate of newly created ratings.")
  void setAnonymousRatingRate(int rate);

  /**
   * Returns the number of created comments.
   *
   * @return the number of created comments
   */
  @ManagedAttribute(description = "Returns the number of created comments.")
  int getCommentCount();

  /**
   * Returns the number of created comments with complaints.
   *
   * @return the number of created comments with complaints
   */
  @ManagedAttribute(description = "Returns the number of created comments with complaints.")
  int getCommentComplaintCount();

  /**
   * Returns the number of created comments with attachments.
   *
   * @return the number of created comments with attachments
   */
  @ManagedAttribute(description = "Returns the number of created comments with attachments.")
  int getCommentWithAttachmentCount();

  /**
   * Returns the number of created comments with post moderation.
   *
   * @return the number of created comments with post moderation
   */
  @ManagedAttribute(description = "Returns the number of created comments with post moderation.")
  int getPostModerationCommentCount();

  /**
   * Returns the number of created comments with pre moderation.
   *
   * @return the number of created comments with pre moderation
   */
  @ManagedAttribute(description = "Returns the number of created comments with pre moderation.")
  int getPreModerationCommentCount();

  /**
   * Returns the number of created comments with no moderation.
   *
   * @return the number of created comments with no moderation
   */
  @ManagedAttribute(description = "Returns the number of created comments with no moderation.")
  int getNoModerationCommentCount();

  /**
   * Returns the number of created users.
   *
   * @return the number of created users
   */
  @ManagedAttribute(description = "Returns the number of created users.")
  int getUserCount();

  /**
   * Returns the number of users for whom complaints were created.
   *
   * @return the number of created users for whom complaints were created
   */
  @ManagedAttribute(description = "Returns the number of created users for whom complaints were created.")
  int getUserComplaintCount();

  /**
   * Returns the number of created users with post moderation.
   *
   * @return the number of created users with post moderation
   */
  @ManagedAttribute(description = "Returns the number of created users with post moderation.")
  int getPostModerationUserCount();

  /**
   * Returns the number of created users with pre moderation.
   *
   * @return the number of created users with pre moderation
   */
  @ManagedAttribute(description = "Returns the number of created users with pre moderation.")
  int getPreModerationUserCount();

  /**
   * Returns the number of created users with no moderation.
   *
   * @return the number of created users with no moderation
   */
  @ManagedAttribute(description = "Returns the number of created users with no moderation.")
  int getNoModerationUserCount();

  /**
   * Returns the number of created likes.
   *
   * @return the number of created likes
   */
  @ManagedAttribute(description = "Returns the number of created likes.")
  int getLikeCount();

  /**
   * Returns the number of created ratings.
   *
   * @return the number of created ratings
   */
  @ManagedAttribute(description = "Returns the number of created ratings.")
  int getRatingCount();

  /**
   * Returns the target doctype.
   *
   * @return the target doctype
   */
  @ManagedAttribute(description = "Returns target doctype.")
  String getTargetDoctype();

  /**
   * Sets the target doctype.
   *
   * @param targetDoctype the target doctype
   */
  @ManagedAttribute(description = "Sets the target doctype.")
  void setTargetDoctype(String targetDoctype);

  /**
   * Creates the given number of anonymous users.
   *
   * @param count the number of anonymous users to create
   */
  @ManagedOperation
  @ManagedOperationParameters({
          @ManagedOperationParameter(name = "count", description = "the number of anonymous users to create")
  })
  void createAnonymousUsers(int count);
}