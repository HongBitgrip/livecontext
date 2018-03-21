package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.reviews.ReviewService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;

@Scope("tenant")
@Named
public class ReviewGenerator extends CommentGenerator {

  private static final int REVIEW_TITLE_MAX_LENGTH = 50;
  private static final int REVIEW_RATING_RANGE = 5;

  private static final Logger LOG = LoggerFactory.getLogger(ReviewGenerator.class);

  private final Random random = new Random();

  @Inject
  private ReviewService reviewService;

  public Review createReview(ModerationType commentModerationType, CommunityUser user, String userName,
                               Object target, Collection<String> categories, boolean hasAttachments) {

    String commentText = getRandomComment(categories);
    String commentTitle = commentText.substring(0, Math.min(commentText.length(), REVIEW_TITLE_MAX_LENGTH));

    Review review = reviewService.createReview(user, commentText, target, categories, commentTitle, random.nextInt(REVIEW_RATING_RANGE));
    if (user.isAnonymous()) {
      review.setAuthorName(userName);
    }

    if (hasAttachments) {
      List<Blob> attachments = new ArrayList<>();
      int attCount = random.nextInt(DEFAULT_ATTACHMENT_MAXIMUM) + 1;
      for (int i = 0; i < attCount; i++) {
        attachments.add(attachmentImageList.get(random.nextInt(attachmentImageList.size())));
      }
      review.setAttachments(attachments);
      commentWithAttachmentCount ++;
    }

    reviewService.save(review, commentModerationType);

    if (commentModerationType == POST_MODERATION) {
      postModerationCommentCount++;
    } else if (commentModerationType == PRE_MODERATION) {
      preModerationCommentCount++;
    } else {
      noModerationCommentCount++;
    }

    commentCount++;

    LOG.debug(PERSONAL_DATA, "Created comment for {} with id={} with {} attachment(s)", user.isAnonymous() ? "anonymous" : user.getName(),
            review.getId(), review.getAttachments().size());
    return review;
  }

}
