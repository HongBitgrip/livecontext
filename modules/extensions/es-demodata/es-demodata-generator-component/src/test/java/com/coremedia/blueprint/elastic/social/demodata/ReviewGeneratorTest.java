package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.social.api.blacklist.BlacklistService;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.reviews.ReviewService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static com.coremedia.elastic.core.api.SortOrder.ASCENDING;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewGeneratorTest {
  @InjectMocks
  private ReviewGenerator reviewGenerator = new ReviewGenerator();

  @Mock
  private UserGenerator userGenerator;

  @Mock
  private ReviewService reviewService;

  @Mock
  private CommentService commentService;

  @Mock
  private BlobService blobService;

  @Mock
  private BlacklistService blacklistService;

  @Mock
  private Object target;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private Review review;

  @Mock
  private Blob blob;

  @Before
  public void setup() {
    when(blobService.put(nullable(InputStream.class), anyString(), eq("att16.jpg"))).thenThrow(new RuntimeException());
    reviewGenerator.initialize();
  }

  @Test
  public void createCommentPostModeration() {
    Collection<String> categories = new ArrayList<>();
    when(reviewService.createReview(eq(communityUser), anyString(), eq(target), eq(categories), anyString(), anyInt())).thenReturn(review);

    Review createdReview = reviewGenerator.createReview(POST_MODERATION, communityUser, "test", target, categories, true);
    assertNotNull(createdReview);
    assertEquals(1, reviewGenerator.getCommentWithAttachmentCount());
    assertEquals(1, reviewGenerator.getPostModerationCommentCount());
    assertEquals(0, reviewGenerator.getPreModerationCommentCount());
    assertEquals(0, reviewGenerator.getNoModerationCommentCount());
    assertEquals(1, reviewGenerator.getCommentCount());
    verify(commentService, never()).getComments(any(), any(CommunityUser.class), eq(ASCENDING), eq(Integer.MAX_VALUE));
    verify(reviewService).createReview(eq(communityUser), anyString(), eq(target), eq(categories), anyString(), anyInt());
    verify(reviewService).save(review, POST_MODERATION);
    verify(review, never()).setAuthorName(anyString());
  }

  @Test
  public void createCommentPreModeration() {
    Collection<String> categories = new ArrayList<>();
    when(reviewService.createReview(eq(communityUser), anyString(), eq(target), eq(categories), anyString(), anyInt())).thenReturn(review);

    Review createdReview = reviewGenerator.createReview(PRE_MODERATION, communityUser, "test", target, categories, true);
    assertNotNull(createdReview);
    assertEquals(1, reviewGenerator.getCommentWithAttachmentCount());
    assertEquals(0, reviewGenerator.getPostModerationCommentCount());
    assertEquals(1, reviewGenerator.getPreModerationCommentCount());
    assertEquals(0, reviewGenerator.getNoModerationCommentCount());
    assertEquals(1, reviewGenerator.getCommentCount());
    verify(commentService, never()).getComments(any(), any(CommunityUser.class), eq(ASCENDING), eq(Integer.MAX_VALUE));
    verify(reviewService).createReview(eq(communityUser), anyString(), eq(target), eq(categories), anyString(), anyInt());
    verify(reviewService).save(review, PRE_MODERATION);
    verify(review, never()).setAuthorName(anyString());
  }
}
