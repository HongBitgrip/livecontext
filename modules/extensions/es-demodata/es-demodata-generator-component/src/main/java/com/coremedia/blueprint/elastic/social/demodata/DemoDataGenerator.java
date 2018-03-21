package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.blueprint.base.elastic.common.CategoryExtractor;
import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.serializer.TypeConverter;
import com.coremedia.elastic.core.api.serializer.TypeConverterRegistry;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * An {@link DemoDataGenerator} generates demo user and content like comments, ratings,
 * likes or complaints on comments or users for a specific tenant with specific generators.
 */
public class DemoDataGenerator implements Runnable {
  public static final String STATE_RUNNING = "running";
  public static final String STATE_STOPPED = "stopped";

  private static final Logger LOG = LoggerFactory.getLogger(DemoDataGenerator.class);

  private static final int DEFAULT_INTERVAL_LENGTH = 30;

  private static final int DEFAULT_NEW_USER_RATE = 5;
  private static final int DEFAULT_ANONYMOUS_USER_RATE = 10;

  private static final int DEFAULT_USER_COMPLAINT_RATE = 49; //complaint will not be created if multiple of ANONYMOUS_COMMENT_RATE !
  private static final int DEFAULT_USER_CHANGES_RATE = 7; //change will not be created if multiple of ANONYMOUS_COMMENT_RATE or NEW_USER_RATE!
  private static final int DEFAULT_CREATE_LIKE_RATE = 2;
  private static final int DEFAULT_CREATE_RATING_RATE = 2;
  private static final int DEFAULT_CREATE_ANONYMOUS_LIKE_RATE = 4;
  private static final int DEFAULT_CREATE_ANONYMOUS_RATING_RATE = 4;

  private static final int DEFAULT_CREATE_TEASER_RATE = -1;
  private static final int DEFAULT_COMMENT_COMPLAINT_RATE = 50;
  private static final int DEFAULT_REVIEW_COMPLAINT_RATE = 50;
  private static final int DEFAULT_ANONYMOUS_COMMENT_RATE = 2;
  private static final int DEFAULT_ANONYMOUS_REVIEW_RATE = 2;
  private static final int DEFAULT_REPLY_TO_COMMENT_RATE = 5;
  private static final int DEFAULT_ATTACHMENT_ON_COMMENT_RATE = 5;
  private static final int DEFAULT_ATTACHMENT_ON_REVIEW_RATE = 5;
  private static final int MAXIMUM_ATTEMPTS = 100;

  private static final String DEFAULT_TARGET_DOCTYPE = "CMArticle";
  private static final String PRODUCT_TEASER_DOCTYPE = "CMProductTeaser";

  // Generators
  @Inject
  private UserGenerator userGenerator;

  @Inject
  private CommentGenerator commentGenerator;

  @Inject
  private ReviewGenerator reviewGenerator;

  @Inject
  private LikeGenerator likeGenerator;

  @Inject
  private RatingGenerator ratingGenerator;

  // Services
  @Inject
  private TenantService tenantService;

  @Inject
  private ContentBeanFactory contentBeanFactory;

  // CMS
  @Inject
  private ContentRepository contentRepository;

  // Blueprint
  @Inject
  private CategoryExtractor categoryExtractor;

  @Inject
  private ContributionTargetHelper contributionTargetHelper;

  @Inject
  private TypeConverterRegistry typeConverterRegistry;

  @Inject
  @Named("contentContextStrategy")
  private ContextStrategy<Content, Content> contextStrategy;

  private ElasticSocialPlugin elasticSocialPlugin;

  @Inject
  public void setElasticSocialPlugin(ElasticSocialPlugin elasticSocialPlugin) {
    this.elasticSocialPlugin = elasticSocialPlugin;
  }

  private int interval = DEFAULT_INTERVAL_LENGTH;
  private Integer count = 0;

  private int newUserRate = DEFAULT_NEW_USER_RATE;
  private int anonymousUserRate = DEFAULT_ANONYMOUS_USER_RATE;
  private int userComplaintRate = DEFAULT_USER_COMPLAINT_RATE;
  private int userChangesRate = DEFAULT_USER_CHANGES_RATE;

  private int createLikeRate = DEFAULT_CREATE_LIKE_RATE;
  private int createRatingRate = DEFAULT_CREATE_RATING_RATE;
  private int createAnonymousLikeRate = DEFAULT_CREATE_ANONYMOUS_LIKE_RATE;
  private int createAnonymousRatingRate = DEFAULT_CREATE_ANONYMOUS_RATING_RATE;

  private int commentComplaintRate = DEFAULT_COMMENT_COMPLAINT_RATE;
  private int reviewComplaintRate = DEFAULT_REVIEW_COMPLAINT_RATE;
  private int anonymousCommentRate = DEFAULT_ANONYMOUS_COMMENT_RATE;
  private int anonymousReviewRate = DEFAULT_ANONYMOUS_REVIEW_RATE;
  private int replyToCommentRate = DEFAULT_REPLY_TO_COMMENT_RATE;
  private int attachmentOnCommentRate = DEFAULT_ATTACHMENT_ON_COMMENT_RATE;
  private int attachmentOnReviewRate = DEFAULT_ATTACHMENT_ON_REVIEW_RATE;

  private int teaserRate = DEFAULT_CREATE_TEASER_RATE;

  private String targetDoctype = DEFAULT_TARGET_DOCTYPE;
  private String currentTargetDoctype;

  private ScheduledThreadPoolExecutor executor = null;
  private final Object lock = new Object();

  private boolean isInitialized = false;
  private boolean isRunning = false;

  private ModerationType userModerationType = ModerationType.POST_MODERATION;

  private String tenant;

  private List<CMTeasable> teasables = new ArrayList<>();
  private int teasableCount = 0;

  private Random random = new Random();

  public void initialize() {
    if (!isInitialized) {
      collectTeasables();
      userGenerator.initialize();
      commentGenerator.initialize();
      reviewGenerator.initialize();
      isInitialized = true;
      currentTargetDoctype = targetDoctype;
    }
  }

  @Override
  public void run() {
    if (isInitialized) {
      try {
        if (!tenantService.getRegistered().contains(tenant)) {
          tenantService.register(tenant);
        }
        tenantService.setCurrent(tenant);

        if (!currentTargetDoctype.equals(targetDoctype)) {
          commentGenerator.reset();
          reviewGenerator.reset();
          likeGenerator.reset();
          ratingGenerator.reset();
          collectTeasables();
          currentTargetDoctype = targetDoctype;
        }

        tenantService.run(new DemoDataGeneratingRunnable(), tenant);

      } catch (Exception e) {
        LOG.warn("Cannot run generate demo data", e);
      } finally {
        count++;
      }
      LOG.trace(format("Finished %s. iteration", count));
    } else {
      LOG.warn("Not yet initialized or not running anymore");
    }
  }

  private class DemoDataGeneratingRunnable implements Runnable {
    @Override
    public void run() {
      try {

        createComment();

        createReview();

        createLike();

        createRating();

        createUser();

        createUserComplain();

        changeUser();

      } catch (Exception e) {
        LOG.warn("Cannot generate demo data", e);
      }
      LOG.trace("Finished {}. iteration", getCount());
    }
  }

  void changeUser() {
    // change user rate
    // userGenerator.updateLastLogin(user);
    if (userChangesRate > 0 && count % userChangesRate == 0) {
      CommunityUser user = userGenerator.getRandomUser();
      userGenerator.changeUserDetails(userModerationType, user);
    }
  }

  void createUserComplain() {
    // complain
    if (userComplaintRate > 0 && count % userComplaintRate == 0) {
      CommunityUser user = getOnlineUser();
      if (user == null) {
        LOG.warn("Cannot find an online user to complain about, skip complaining");
      } else {
        userGenerator.complainOnUser(user);
      }
    }
  }

  void createComment() {
    LOG.trace("Create comment with count {}", count);
    boolean anonymous = anonymousCommentRate > 0 && count % anonymousCommentRate == 0;
    Object target = commentGenerator.getRandomTarget(anonymous);
    if (target != null) {
      CommunityUser user;
      String userName = null;
      if (anonymous) {
        userName = userGenerator.getRandomUserName();
        user = userGenerator.createAnonymousUser();
      } else {
        user = userGenerator.getRandomUser();
      }

      boolean hasAttachments = attachmentOnCommentRate > 0 && count % attachmentOnCommentRate == 0;
      boolean isReply = replyToCommentRate > 0 && count % replyToCommentRate == 0;
      boolean hasComplaints = commentComplaintRate > 0 && count % commentComplaintRate == 0;

        Comment comment = commentGenerator.createComment(commentGenerator.getModerationType(target), user, userName, target,
                getCategories(target), hasAttachments, isReply);

      // complaints
      if (hasComplaints) {
        commentGenerator.complainOnComment(target, comment, anonymous);
      }
    } else {
      LOG.info("Could not create comment, no target available with anonymous: " + anonymous);
    }
  }

  void createReview() {
    LOG.trace("Create review with count {}", count);
    boolean anonymous = anonymousReviewRate > 0 && count % anonymousReviewRate == 0;
    Object target = reviewGenerator.getRandomTarget(anonymous);
    if (target != null) {
      CommunityUser user;
      String userName = null;
      if (anonymous) {
        userName = userGenerator.getRandomUserName();
        user = userGenerator.createAnonymousUser();
      } else {
        user = userGenerator.getRandomUser();
      }

      boolean hasAttachments = attachmentOnReviewRate > 0 && count % attachmentOnReviewRate == 0;
      boolean hasComplaints = reviewComplaintRate > 0 && count % reviewComplaintRate == 0;

      Review review = reviewGenerator.createReview(reviewGenerator.getModerationType(target), user, userName, target,
              getCategories(target), hasAttachments);

      // complaints
      if (hasComplaints) {
        reviewGenerator.complainOnComment(target, review, anonymous);
      }
    } else {
      LOG.info("Could not find a target for anonymous={}, so creation of a review was skipped", anonymous);
    }
  }
  
  void createUser() {
    LOG.trace("Create user with count {}", count);
    if (newUserRate > 0 && count % newUserRate == 0) {
      // create user
      if (anonymousUserRate > 0 && count % anonymousUserRate == 0) {
        userGenerator.createAnonymousUser();
      } else {
        userGenerator.createUser(userModerationType);
      }
    }
  }

  private void createTeasable() {
    LOG.trace("Create article with count {}", count);
    if (teaserRate > 0 && count % teaserRate == 0) {
      // create article
      CMTeasable teasableToCopy = teasables.get(random.nextInt(teasables.size()));

      Content contentToCopy = teasableToCopy.getContent();
      Content newContent = contentToCopy.copyTo(contentToCopy.getParent(), contentToCopy.getName(), "{3} ({1})");
      CMTeasable teasable = contentBeanFactory.createBeanFor(newContent, CMTeasable.class);

      List<? extends CMNavigation> navigations = teasable.getContexts();
      for (CMNavigation navigation : navigations) {
        ElasticSocialConfiguration elasticSocialConfiguration = elasticSocialPlugin.getElasticSocialConfiguration(teasable, navigation);
        processTeasable(teasable.getContent(), elasticSocialConfiguration);
      }
      teasableCount++;
    }
  }

  void createLike() {
    if (createLikeRate > 0 && count % createLikeRate == 0) {
      boolean anonymous = createAnonymousLikeRate > 0 && count % createAnonymousLikeRate == 0;
      Object target = likeGenerator.getRandomTarget(anonymous);
      if (target == null) {
        LOG.warn("Could not create like, no target available.");
        return;
      }

      CommunityUser user;
      if (anonymous) {
        user = userGenerator.createAnonymousUser();
      } else {
        user = userGenerator.getRandomUser();
      }
      likeGenerator.createLike(user, target, getCategories(target));
    }
  }

  void createRating() {
    if (createRatingRate > 0 && count % createRatingRate == 0) {
      boolean anonymous = createAnonymousRatingRate > 0 && count % createAnonymousRatingRate == 0;
      Object target = ratingGenerator.getRandomTarget(anonymous);
      if (target == null) {
        LOG.warn("Could not create rating, no target available.");
        return;
      }

      CommunityUser user;
      if (anonymous) {
        user = userGenerator.createAnonymousUser();
      } else {
        user = userGenerator.getRandomUser();
      }
      ratingGenerator.createRating(user, target, getCategories(target));
    }
  }

  @Nullable
  private CommunityUser getOnlineUser() {
    int attempts = 0;
    CommunityUser user;
    do {
      user = userGenerator.getRandomUser();
      attempts++;
    }
    while (!(user.isActivated() || user.isActivatedAndRequiresModeration()) && attempts < MAXIMUM_ATTEMPTS);

    if (!(user.isActivated() || user.isActivatedAndRequiresModeration())) {
      user = null;
    }

    return user;
  }

  private void collectTeasables() {
    QueryService queryService = contentRepository.getQueryService();
    Collection<Content> contents = queryService.poseContentQuery("TYPE " + targetDoctype + " AND isInProduction");

    // Teasable is assigned to attribute set, if one aspect has the attribute
    // This works with Blueprint content but must be refactored for more complex content (e.g. articles with more than one navigation context)
    for (Content teasable : contents) {
      List<Content> navigations = contextStrategy.findContextsFor(teasable);
      for (Content navigation : navigations) {
        ElasticSocialConfiguration elasticSocialConfiguration = elasticSocialPlugin.getElasticSocialConfiguration(teasable, navigation);
        if (tenantService.getCurrent().equals(elasticSocialConfiguration.getTenant())) {
          processTeasable(teasable, elasticSocialConfiguration);
        }
      }
    }
    // products are teasables
    collectProducts();
  }

  private void collectProducts() {
    try {
      QueryService queryService = contentRepository.getQueryService();
      Collection<Content> contents = queryService.poseContentQuery("TYPE " + PRODUCT_TEASER_DOCTYPE + " AND isInProduction");

      for (Content productTeaser : contents) {
        final Site site = contributionTargetHelper.getSite(productTeaser);
        if(null != site) {
          for (Content navigation : contextStrategy.findContextsFor(productTeaser)) {
            ElasticSocialConfiguration elasticSocialConfiguration = elasticSocialPlugin.getElasticSocialConfiguration(productTeaser, navigation);
            if (tenantService.getCurrent().equals(elasticSocialConfiguration.getTenant())) {
              // products are simply Strings - as defined in helios-doctypes.xml
              if (null != productTeaser.getType().getDescriptor("externalId")) {
                final Object externalId = productTeaser.get("externalId");
                final ImmutableMap<String, Object> serializedProduct = ImmutableMap.of("id", externalId, "site", site.getId());
                TypeConverter<?> converter = typeConverterRegistry.getConverter("productInSite");
                if(null != converter) {
                  final Object target = converter.deserialize(serializedProduct);
                  reviewGenerator.addTarget(target, elasticSocialConfiguration.isWritingReviewsEnabled(), elasticSocialConfiguration.isAnonymousReviewingEnabled(),
                          elasticSocialConfiguration.isComplainingEnabled(), elasticSocialConfiguration.isAnonymousReviewingEnabled(),
                          elasticSocialConfiguration.getReviewModerationType());
                } else {
                  LOG.warn("no type converter for 'product' found - is LC extension active?");
                }
              }
            }
          }
        }
      }
    } catch (RuntimeException e) {
      LOG.warn("unable to collect products for reviewing", e);
    }
  }

  private void processTeasable(Content teasable, ElasticSocialConfiguration elasticSocialConfiguration) {
    try {
      Object target = contributionTargetHelper.getTarget(teasable);
      commentGenerator.addTarget(target, elasticSocialConfiguration.isWritingCommentsEnabled(), elasticSocialConfiguration.isAnonymousCommentingEnabled(),
              elasticSocialConfiguration.isComplainingEnabled(), elasticSocialConfiguration.isAnonymousComplainingEnabled(),
              elasticSocialConfiguration.getCommentModerationType());
      reviewGenerator.addTarget(target, elasticSocialConfiguration.isWritingReviewsEnabled(), elasticSocialConfiguration.isAnonymousReviewingEnabled(),
              elasticSocialConfiguration.isComplainingEnabled(), elasticSocialConfiguration.isAnonymousComplainingEnabled(),
              elasticSocialConfiguration.getReviewModerationType());
      likeGenerator.addTarget(target, elasticSocialConfiguration.isLikeEnabled(), elasticSocialConfiguration.isAnonymousLikeEnabled());
      ratingGenerator.addTarget(target, elasticSocialConfiguration.isRatingEnabled(), elasticSocialConfiguration.isAnonymousRatingEnabled());
    } catch (IllegalArgumentException e) {
      LOG.error("No target found for contribution");
    }
  }

  // Getters & Setters

  private Integer getCount() {
    return count;
  }

  @VisibleForTesting
  void setCount(int count) {
    this.count = count;
  }

  public void start() {
    synchronized (lock) {
      if (!isInitialized) {
        initialize();
      }
      if (!isRunning) {
        tenant = tenantService.getCurrent();
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(this, 0, interval, TimeUnit.SECONDS);
        isRunning = true;
      } else {
        LOG.warn("Could not start DemoDataGenerator because it is already running");
      }
    }
  }

  public void stop() {
    synchronized (lock) {
      if (executor != null) {
        executor.shutdownNow();
      }
      executor = null;
      isRunning = false;
    }
  }

  public void restart() {
    stop();
    start();
  }

  public String getStatus() {
    return isRunning ? STATE_RUNNING : STATE_STOPPED;
  }

  public int getUserCount() {
    return userGenerator.getUserCount();
  }

  public void createAnonymousUsers(int count) {
    userGenerator.createAnonymousUsers(count);
  }

  public void createComments(int numberOfComments) {
    if (!isInitialized) {
      initialize();
    }
    long start = System.currentTimeMillis();
    long lastBatch = start;
    LOG.info("Start to create {} comments", numberOfComments);
    for(int i = 0; i < numberOfComments; i++) {
      createComment();
      if (i % 10000 == 0) {
        long currentTimeMillies = System.currentTimeMillis();
        LOG.info("Needed {}ms for the last 10.000 comments", currentTimeMillies - lastBatch);
        lastBatch = currentTimeMillies;
      }
    }
    LOG.info("Finished to create {} comments, took {}ms", numberOfComments, System.currentTimeMillis() - start);
  }

  public int getUserComplaintCount() {
    return userGenerator.getUserComplaintCount();
  }

  public int getPostModerationUserCount() {
    return userGenerator.getPostModerationUserCount();
  }

  public int getPreModerationUserCount() {
    return userGenerator.getPreModerationUserCount();
  }

  public int getNoModerationUserCount() {
    return userGenerator.getNoModerationUserCount();
  }

  public int getUserChangesPreModerationCount() {
    return userGenerator.getUserChangesPreModerationCount();
  }

  public int getUserChangesPostModerationCount() {
    return userGenerator.getUserChangesPostModerationCount();
  }

  public int getCommentCount() {
    return commentGenerator.getCommentCount();
  }

  public int getCommentComplaintCount() {
    return commentGenerator.getCommentComplaintCount();
  }

  public int getPostModerationCommentCount() {
    return commentGenerator.getPostModerationCommentCount();
  }

  public int getPreModerationCommentCount() {
    return commentGenerator.getPreModerationCommentCount();
  }

  public int getNoModerationCommentCount() {
    return commentGenerator.getNoModerationCommentCount();
  }

  public int getCommentWithAttachmentCount() {
    return commentGenerator.getCommentWithAttachmentCount();
  }

  public int getReviewCount() {
    return reviewGenerator.getCommentCount();
  }

  public int getReviewComplaintCount() {
    return reviewGenerator.getCommentComplaintCount();
  }

  public int getPostModerationReviewCount() {
    return reviewGenerator.getPostModerationCommentCount();
  }

  public int getPreModerationReviewCount() {
    return reviewGenerator.getPreModerationCommentCount();
  }

  public int getNoModerationReviewCount() {
    return reviewGenerator.getNoModerationCommentCount();
  }

  public int getReviewWithAttachmentCount() {
    return reviewGenerator.getCommentWithAttachmentCount();
  }

  public int getLikeCount() {
    return likeGenerator.getLikeCount();
  }

  public int getRatingCount() {
    return ratingGenerator.getRatingCount() + reviewGenerator.getCommentCount();
  }

  public Collection<Object> getTeasablesCommentingEnabled() {
    return commentGenerator.getCommentingEnabledTargets();
  }

  public Collection<Object> getTeasablesAnonymousComplainingEnabled() {
    return commentGenerator.getAnonymousComplainingEnabledTargets();
  }

  public Collection<Object> getTeasablesAnonymousCommentingEnabled() {
    return commentGenerator.getAnonymousCommentingEnabledTargets();
  }

  public Collection<Object> getTeasablesComplainingEnabled() {
    return commentGenerator.getComplainingEnabledTargets();
  }

  public int getDefaultInterval() {
    return DEFAULT_INTERVAL_LENGTH;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    if (interval >= 0) {
      this.interval = interval;
      LOG.info("The interval has been set to: {}", interval);
    } else {
      throw new IllegalArgumentException("The interval must be a positive number.");
    }
  }

  public void resetAllSettings() {
    interval = DEFAULT_INTERVAL_LENGTH;

    newUserRate = DEFAULT_NEW_USER_RATE;
    userComplaintRate = DEFAULT_USER_COMPLAINT_RATE;
    userChangesRate = DEFAULT_USER_CHANGES_RATE;

    createLikeRate = DEFAULT_CREATE_LIKE_RATE;
    createAnonymousLikeRate = DEFAULT_CREATE_ANONYMOUS_LIKE_RATE;
    createRatingRate = DEFAULT_CREATE_RATING_RATE;
    createAnonymousRatingRate = DEFAULT_CREATE_ANONYMOUS_RATING_RATE;
    anonymousUserRate = DEFAULT_ANONYMOUS_USER_RATE;

    commentComplaintRate = DEFAULT_COMMENT_COMPLAINT_RATE;
    anonymousCommentRate = DEFAULT_ANONYMOUS_COMMENT_RATE;
    replyToCommentRate = DEFAULT_REPLY_TO_COMMENT_RATE;
    attachmentOnCommentRate = DEFAULT_ATTACHMENT_ON_COMMENT_RATE;

    targetDoctype = DEFAULT_TARGET_DOCTYPE;

    LOG.info("The configuration has been restored to default values");
  }

  public int getCommentComplaintRate() {
    return commentComplaintRate;
  }

  public void setCommentComplaintRate(int commentComplaintRate) {
    if (commentComplaintRate >= 0) {
      this.commentComplaintRate = commentComplaintRate;
      LOG.info("The comment complain rate has been set to: {}", commentComplaintRate);
    } else {
      throw new IllegalArgumentException("The comment complain rate must be a positive number.");
    }
  }

  public int getAnonymousCommentRate() {
    return anonymousCommentRate;
  }

  public void setAnonymousCommentRate(int anonymousCommentRate) {
    if (anonymousCommentRate >= 0) {
      this.anonymousCommentRate = anonymousCommentRate;
      LOG.info("The anonymous comment rate has been set to: {}", anonymousCommentRate);
    } else {
      throw new IllegalArgumentException("The anonymous comment rate must be a positive number.");
    }
  }

  public int getAnonymousReviewRate() {
    return anonymousReviewRate;
  }

  public void setAnonymousReviewRate(int anonymousReviewRate) {
    if (anonymousReviewRate >= 0) {
      this.anonymousReviewRate = anonymousReviewRate;
      LOG.info("The anonymous review rate has been set to: {}", anonymousReviewRate);
    } else {
      throw new IllegalArgumentException("The anonymous review rate must be a positive number.");
    }
  }

  public int getAttachmentOnReviewRate() {
    return attachmentOnReviewRate;
  }

  public void setAttachmentOnReviewRate(int attachmentOnReviewRate) {
    if (attachmentOnReviewRate >= 0) {
      this.attachmentOnReviewRate = attachmentOnReviewRate;
      LOG.info("The attachment on review rate has been set to: {}", attachmentOnReviewRate);
    } else {
      throw new IllegalArgumentException("The attachment on review rate must be a positive number.");
    }
  }

  public int getReviewComplaintRate() {
    return reviewComplaintRate;
  }

  public void setReviewComplaintRate(int reviewComplaintRate) {
    if (reviewComplaintRate >= 0) {
      this.commentComplaintRate = reviewComplaintRate;
      LOG.info("The review complain rate has been set to: {}", reviewComplaintRate);
    } else {
      throw new IllegalArgumentException("The review complain rate must be a positive number.");
    }
  }

  public int getReplyToCommentRate() {
    return replyToCommentRate;
  }

  public void setReplyToCommentRate(int replyToCommentRate) {
    if (replyToCommentRate >= 0) {
      this.replyToCommentRate = replyToCommentRate;
      LOG.info("The reply to comment rate has been set to: {}", replyToCommentRate);
    } else {
      throw new IllegalArgumentException("The attachment on comment rate must be a positive number.");
    }
  }

  public int getAttachmentOnCommentRate() {
    return attachmentOnCommentRate;
  }

  public void setAttachmentOnCommentRate(int attachmentOnCommentRate) {
    if (attachmentOnCommentRate >= 0) {
      this.attachmentOnCommentRate = attachmentOnCommentRate;
      LOG.info("The attachment on comment rate has been set to: {}", attachmentOnCommentRate);
    } else {
      throw new IllegalArgumentException("The attachment on comment rate must be a positive number.");
    }
  }

  public ModerationType getUserModerationType() {
    return userModerationType;
  }

  public void setUserModerationType(ModerationType userModerationType) {
    this.userModerationType = userModerationType;
  }

  public int getNewUserRate() {
    return newUserRate;
  }

  public void setNewUserRate(int newUserRate) {
    if (newUserRate >= 0) {
      this.newUserRate = newUserRate;
      LOG.info("The new user rate has been set to: {}", newUserRate);
    } else {
      throw new IllegalArgumentException("The new user rate must be a positive number.");
    }
  }

  public int getAnonymousUserRate() {
    return anonymousUserRate;
  }

  public void setAnonymousUserRate(int anonymousUserRate) {
    if (anonymousUserRate >= 0) {
      this.anonymousUserRate = anonymousUserRate;
      LOG.info("The anonymous user rate has been set to: {}", anonymousUserRate);
    } else {
      throw new IllegalArgumentException("The anonymous user rate must be a positive number.");
    }
  }

  public int getUserComplaintRate() {
    return userComplaintRate;
  }

  public void setUserComplaintRate(int userComplaintRate) {
    if (userComplaintRate >= 0) {
      this.userComplaintRate = userComplaintRate;
      LOG.info("The user complaint rate has been set to: {}", userComplaintRate);
    } else {
      throw new IllegalArgumentException("The user complain rate must be a positive number.");
    }
  }

  public int getUserChangesRate() {
    return userChangesRate;
  }

  public void setUserChangesRate(int userChangesRate) {
    if (userChangesRate >= 0) {
      this.userChangesRate = userChangesRate;
      LOG.info("The user changes rate has been set to: {}", userChangesRate);
    } else {
      throw new IllegalArgumentException("The user changes rate must be a positive number.");
    }
  }

  public int getCreateLikeRate() {
    return createLikeRate;
  }

  public void setCreateLikeRate(int createLikeRate) {
    if (createLikeRate >= 0) {
      this.createLikeRate = createLikeRate;
      LOG.info("The create like rate has been set to: {}", createLikeRate);
    } else {
      throw new IllegalArgumentException("The create like rate must be a positive number.");
    }
  }

  public int getCreateRatingRate() {
    return createRatingRate;
  }

  public void setCreateRatingRate(int createRatingRate) {
    if (createRatingRate >= 0) {
      this.createRatingRate = createRatingRate;
      LOG.info("The create rating rate has been set to: {}", createRatingRate);
    } else {
      throw new IllegalArgumentException("The create rating rate must be a positive number.");
    }
  }

  public int getCreateAnonymousLikeRate() {
    return createAnonymousLikeRate;
  }

  public void setCreateAnonymousLikeRate(int createAnonymousLikeRate) {
    if (createAnonymousLikeRate >= 0) {
      this.createAnonymousLikeRate = createAnonymousLikeRate;
      LOG.info("The create rating rate has been set to: {}", createAnonymousLikeRate);
    } else {
      throw new IllegalArgumentException("The create anonymous like rate must be a positive number.");
    }
  }

  public int getCreateAnonymousRatingRate() {
    return createAnonymousRatingRate;
  }

  public void setCreateAnonymousRatingRate(int createAnonymousRatingRate) {
    if (createAnonymousRatingRate >= 0) {
      this.createAnonymousRatingRate = createAnonymousRatingRate;
      LOG.info("The create anonymous rating rate has been set to: {}", createAnonymousRatingRate);
    } else {
      throw new IllegalArgumentException("The create anonymous rating rate must be a positive number.");
    }
  }

  public void setTargetDoctype(String targetDoctype) {
    this.targetDoctype = targetDoctype;
  }

  public String getTargetDoctype() {
    return targetDoctype;
  }

  public Collection<Object> getTeasablesCommentingEnabledNoModeration() {
    return commentGenerator.getNoModerationTargets();
  }

  public Collection<Object> getTeasablesCommentingEnabledPreModeration() {
    return commentGenerator.getPreModerationTargets();
  }

  public Collection<Object> getTeasablesCommentingEnabledPostModeration() {
    return commentGenerator.getPostModerationTargets();
  }

  public Collection<Object> getTeasablesAnonymousCommentingEnabledNoModeration() {
    return commentGenerator.getAnonymousNoModerationTargets();
  }

  public Collection<Object> getTeasablesAnonymousCommentingEnabledPreModeration() {
    return commentGenerator.getAnonymousPreModerationTargets();
  }

  public Collection<Object> getTeasablesAnonymousCommentingEnabledPostModeration() {
    return commentGenerator.getAnonymousPostModerationTargets();
  }

  public Collection<Object> getTeasablesLikeEnabled() {
    return likeGenerator.getLikeEnabledTargets();
  }

  public Collection<Object> getTeasablesAnonymousLikeEnabled() {
    return likeGenerator.getAnonymousLikeEnabledTargets();
  }

  public Collection<Object> getTeasablesRatingEnabled() {
    return ratingGenerator.getRatingEnabledTargets();
  }

  public Collection<Object> getTeasablesAnonymousRatingEnabled() {
    return ratingGenerator.getAnonymousRatingEnabledTargets();
  }

  private Collection<String> getCategories(Object target) {
    return categoryExtractor.getCategories(contributionTargetHelper.getContentFromTarget(target), null);
  }
}
