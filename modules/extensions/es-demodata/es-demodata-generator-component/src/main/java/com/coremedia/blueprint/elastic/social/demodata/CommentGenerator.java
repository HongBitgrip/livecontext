package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.blacklist.BlacklistService;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;
import static com.coremedia.elastic.core.api.SortOrder.ASCENDING;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

/**
 * An {@link CommentGenerator} generates comments and complaints on comments for a specific tenant.
 */
public class CommentGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(CommentGenerator.class);

  private static final int DEFAULT_COMMENT_COMPLAINT_MAXIMUM = 20;
  protected static final int DEFAULT_ATTACHMENT_MAXIMUM = 5;

  private static final String DEFAULT_CATEGORY = "default";
  private static final String PRODUCTS_CATEGORY = "products";
  private static final String TRAVEL_CATEGORY = "travel";
  private static final String COMMENTS_FILE_NAME = "comments.txt";
  private static final String COMMENTS_PRODUCTS_FILE_NAME = "comments_products.txt";
  private static final String COMMENTS_TRAVEL_FILE_NAME = "comments_travel.txt";
  private static final String BLACKLIST_FILE_NAME = "blacklist_words.txt";

  private final Map<String, List<String>> commentsByCategoryMap = new HashMap<>();

  private final List<Object> commentingEnabledTargets = new ArrayList<>();
  private final List<Object> anonymousCommentingEnabledTargets = new ArrayList<>();
  private final List<Object> complainingEnabledTargets = new ArrayList<>();
  private final List<Object> anonymousComplainingEnabledTargets = new ArrayList<>();

  private final Set<Object> preModerationTargets = new HashSet<>();
  private final Set<Object> postModerationTargets = new HashSet<>();
  private final Set<Object> noModerationTargets = new HashSet<>();

  @Inject
  private UserGenerator userGenerator;

  @Inject
  private CommentService commentService;

  @Inject
  private BlobService blobService;

  @Inject
  private BlacklistService blacklistService;

  protected int commentCount = 0;
  protected int postModerationCommentCount = 0;
  protected int preModerationCommentCount = 0;
  protected int noModerationCommentCount = 0;
  private int commentComplaintCount = 0;
  protected int commentWithAttachmentCount = 0;

  protected List<Blob> attachmentImageList;

  private final Random random = new Random();

  public void initialize() {
    loadComments(COMMENTS_FILE_NAME, DEFAULT_CATEGORY, commentsByCategoryMap);
    loadComments(COMMENTS_PRODUCTS_FILE_NAME, PRODUCTS_CATEGORY, commentsByCategoryMap);
    loadComments(COMMENTS_TRAVEL_FILE_NAME, TRAVEL_CATEGORY, commentsByCategoryMap);

    attachmentImageList = GeneratorUtils.loadImages("att", "image/jpeg", "jpg", blobService);
    List<String> blacklistWords = GeneratorUtils.loadListFromFile(BLACKLIST_FILE_NAME);
    addToBlacklist(blacklistWords);
  }

  public void reset() {
    commentingEnabledTargets.clear();
    anonymousCommentingEnabledTargets.clear();
    complainingEnabledTargets.clear();
    anonymousComplainingEnabledTargets.clear();

    preModerationTargets.clear();
    postModerationTargets.clear();
    noModerationTargets.clear();
  }

  private void loadComments(String resourceName, String categoryName, Map<String, List<String>> commentsByCategory) {
    List<String> commentList = commentsByCategory.get(categoryName);
    if (commentList == null) {
      commentList = new ArrayList<>();
      commentsByCategory.put(categoryName, commentList);
    }
    commentList.addAll(GeneratorUtils.loadListFromFile(resourceName));
  }

  private void addToBlacklist(List<String> blackList) {
    final Set<String> currentBlacklistWords = new HashSet<>(blacklistService.getBlacklist());
    for (String blacklistWord : blackList) {
      if (!currentBlacklistWords.contains(blacklistWord)) {
        blacklistService.addEntry(blacklistWord);
        LOG.debug("Added blacklist word " + blacklistWord);
      }
    }
  }

  protected String getRandomComment(Collection<String> categories) {
    final List<String> comments = new ArrayList<>();
    comments.addAll(commentsByCategoryMap.get(DEFAULT_CATEGORY));

    for (String category : categories) {
      List<String> categoryComments = commentsByCategoryMap.get(category.toLowerCase(ENGLISH));
      if (categoryComments != null) {
        comments.addAll(categoryComments);
      }
    }
    return comments.get(random.nextInt(comments.size()));
  }

  public Comment createComment(ModerationType commentModerationType, CommunityUser user, String userName,
                               Object target, Collection<String> categories, boolean hasAttachments, boolean isReply) {
    Comment replyToComment = null;

    if (isReply) {
      List<Comment> comments = commentService.getComments(target, null, ASCENDING, Integer.MAX_VALUE);
      if (!comments.isEmpty()) {
        replyToComment = comments.get(random.nextInt(comments.size()));
      }
    }

    Comment comment = commentService.createComment(user, getRandomComment(categories), target, categories, replyToComment);
    if (user.isAnonymous()) {
      comment.setAuthorName(userName);
    }

    if (hasAttachments) {
      List<Blob> attachments = new ArrayList<>();
      int attCount = random.nextInt(DEFAULT_ATTACHMENT_MAXIMUM) + 1;
      for (int i = 0; i < attCount; i++) {
        attachments.add(attachmentImageList.get(random.nextInt(attachmentImageList.size())));
      }
      comment.setAttachments(attachments);
      commentWithAttachmentCount ++;
    }
    commentService.save(comment, commentModerationType);
    if (commentModerationType == POST_MODERATION) {
      postModerationCommentCount++;
    } else if (commentModerationType == PRE_MODERATION) {
      preModerationCommentCount++;
    } else {
      noModerationCommentCount++;
    }
    commentCount++;
    LOG.debug(PERSONAL_DATA, "Created comment for {} with id={} with {} attachment(s)", user.isAnonymous() ? "anonymous" : user.getName(),
            comment.getId(), comment.getAttachments().size());
    return comment;
  }

  public void complainOnComment(Object target, Comment comment, boolean anonymous) {
    if (complainingEnabledTargets.contains(target)) {
      for (int i = 0, size = random.nextInt(DEFAULT_COMMENT_COMPLAINT_MAXIMUM) + 1; i < size; i++) {
        if (anonymous) {
          CommunityUser author = userGenerator.createAnonymousUser();
          createComplaintForComment(author, comment);
        } else {
          CommunityUser author = userGenerator.getRandomUser();
          createComplaintForComment(author, comment);
        }
      }
    } else {
      LOG.info("Could not create complaint, because complaining is not enabled for the comment target.");
    }
  }

  private void createComplaintForComment(CommunityUser author, Comment comment) {
    commentService.addComplaint(author, comment);
    commentComplaintCount++;
    LOG.debug(format("Created %s complaint for comment with id: %s", author.isAnonymous() ? "anonymous" : "", comment.getId()));
  }

  public Object getRandomTarget(boolean anonymous) {
    LOG.debug(format("Get comment target with anonymous=%s", anonymous));
    Object target = null;
    if(anonymous && !anonymousCommentingEnabledTargets.isEmpty()) {
      target = anonymousCommentingEnabledTargets.get(random.nextInt(anonymousCommentingEnabledTargets.size()));
    } else if (!anonymous && !commentingEnabledTargets.isEmpty()) {
      target = commentingEnabledTargets.get(random.nextInt(commentingEnabledTargets.size()));
    }
    return target;
  }

  public void addTarget(Object target, boolean commentingEnabled, boolean anonymousCommentingEnabled,
                        boolean complainingEnabled, boolean anonymousComplainingEnabled, ModerationType moderationType) {
    if (commentingEnabled) {
      LOG.debug("Add target {} for commenting", target);
      commentingEnabledTargets.add(target);
      if (anonymousCommentingEnabled) {
        LOG.debug("Add target {} for anonymous commenting", target);
        anonymousCommentingEnabledTargets.add(target);
      }
      if (ModerationType.PRE_MODERATION.equals(moderationType)) {
        LOG.debug("Add target {} for commenting with pre moderation", target);
        preModerationTargets.add(target);
      } else if (ModerationType.POST_MODERATION.equals(moderationType)) {
        LOG.debug("Add target {} for commenting with post moderation", target);
        postModerationTargets.add(target);
      } else {
        LOG.debug("Add target {} for commenting without moderation", target);
        noModerationTargets.add(target);
      }

      if (complainingEnabled) {
        complainingEnabledTargets.add(target);
      }
      if (anonymousComplainingEnabled) {
        anonymousComplainingEnabledTargets.add(target);
      }
    }
  }

  public ModerationType getModerationType(Object target) {
    if (postModerationTargets.contains(target)) {
      return POST_MODERATION;
    } else if (preModerationTargets.contains(target)) {
      return PRE_MODERATION;
    } else {
      return ModerationType.NONE;
    }
  }

  public Collection<Object> getCommentingEnabledTargets() {
    return commentingEnabledTargets;
  }

  public Collection<Object> getAnonymousCommentingEnabledTargets() {
    return anonymousCommentingEnabledTargets;
  }

  public Collection<Object> getComplainingEnabledTargets() {
    return complainingEnabledTargets;
  }

  public Collection<Object> getAnonymousComplainingEnabledTargets() {
    return anonymousComplainingEnabledTargets;
  }

  public int getPreModerationCommentCount() {
    return preModerationCommentCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public int getPostModerationCommentCount() {
    return postModerationCommentCount;
  }

  public int getNoModerationCommentCount() {
    return noModerationCommentCount;
  }

  public int getCommentComplaintCount() {
    return commentComplaintCount;
  }

  public int getCommentWithAttachmentCount() {
    return commentWithAttachmentCount;
  }

  public Collection<Object> getPreModerationTargets() {
    return preModerationTargets;
  }

  public Collection<Object> getPostModerationTargets() {
    return postModerationTargets;
  }

  public Collection<Object> getNoModerationTargets() {
    return noModerationTargets;
  }

  public Collection<Object> getAnonymousNoModerationTargets() {
    List<Object> result = new ArrayList<>(noModerationTargets);
    result.retainAll(anonymousCommentingEnabledTargets);
    return result;
  }

  public Collection<Object> getAnonymousPostModerationTargets() {
    List<Object> result = new ArrayList<>(postModerationTargets);
    result.retainAll(anonymousCommentingEnabledTargets);
    return result;
  }

  public Collection<Object> getAnonymousPreModerationTargets() {
    List<Object> result = new ArrayList<>(preModerationTargets);
    result.retainAll(anonymousCommentingEnabledTargets);
    return result;
  }

}
