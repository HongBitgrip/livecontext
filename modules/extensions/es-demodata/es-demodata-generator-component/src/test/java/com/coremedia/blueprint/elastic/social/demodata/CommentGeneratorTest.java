package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.blacklist.BlacklistService;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.coremedia.elastic.core.api.SortOrder.ASCENDING;
import static com.coremedia.elastic.social.api.ModerationType.NONE;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentGeneratorTest {
  @InjectMocks
  private CommentGenerator commentGenerator = new CommentGenerator();

  @Mock
  private UserGenerator userGenerator;

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
  private Comment comment;

  @Mock
  private Blob blob;

  @Before
  public void setup() {
    when(blobService.put(nullable(InputStream.class), anyString(), eq("att16.jpg"))).thenThrow(new RuntimeException());
    commentGenerator.initialize();
  }

  @Test
  public void initialize() {
    verify(blacklistService, atLeastOnce()).addEntry(anyString());
    verify(blobService, atLeastOnce()).put(any(InputStream.class), anyString(), anyString());
  }

  @Test
  public void addTargetPostModeration() {
    commentGenerator.addTarget(target, true, true, true, true, POST_MODERATION);
    assertEquals(1, commentGenerator.getCommentingEnabledTargets().size());
    assertEquals(1, commentGenerator.getAnonymousCommentingEnabledTargets().size());
    assertEquals(1, commentGenerator.getPostModerationTargets().size());
    assertEquals(0, commentGenerator.getPreModerationTargets().size());
    assertEquals(0, commentGenerator.getNoModerationTargets().size());
    assertEquals(1, commentGenerator.getComplainingEnabledTargets().size());
    assertEquals(1, commentGenerator.getAnonymousComplainingEnabledTargets().size());
  }

  @Test
  public void addTargetPreModeration() {
    commentGenerator.addTarget(target, true, false, false, false, PRE_MODERATION);
    assertEquals(1, commentGenerator.getCommentingEnabledTargets().size());
    assertEquals(0, commentGenerator.getAnonymousCommentingEnabledTargets().size());
    assertEquals(0, commentGenerator.getPostModerationTargets().size());
    assertEquals(1, commentGenerator.getPreModerationTargets().size());
    assertEquals(0, commentGenerator.getNoModerationTargets().size());
    assertEquals(0, commentGenerator.getComplainingEnabledTargets().size());
    assertEquals(0, commentGenerator.getAnonymousComplainingEnabledTargets().size());
  }

  @Test
  public void addTargetNoModeration() {
    commentGenerator.addTarget(target, true, true, true, true, NONE);
    assertEquals(1, commentGenerator.getCommentingEnabledTargets().size());
    assertEquals(1, commentGenerator.getAnonymousCommentingEnabledTargets().size());
    assertEquals(0, commentGenerator.getPostModerationTargets().size());
    assertEquals(0, commentGenerator.getPreModerationTargets().size());
    assertEquals(1, commentGenerator.getNoModerationTargets().size());
    assertEquals(1, commentGenerator.getComplainingEnabledTargets().size());
    assertEquals(1, commentGenerator.getAnonymousComplainingEnabledTargets().size());
  }

  @Test
  public void addTargetCommentingDisabled() {
    commentGenerator.addTarget(target, false, true, true, true, NONE);
    assertEquals(0, commentGenerator.getCommentingEnabledTargets().size());
    assertEquals(0, commentGenerator.getAnonymousCommentingEnabledTargets().size());
    assertEquals(0, commentGenerator.getPostModerationTargets().size());
    assertEquals(0, commentGenerator.getPreModerationTargets().size());
    assertEquals(0, commentGenerator.getNoModerationTargets().size());
    assertEquals(0, commentGenerator.getComplainingEnabledTargets().size());
    assertEquals(0, commentGenerator.getAnonymousComplainingEnabledTargets().size());
  }

  @Test
  public void createCommentPostModeration() {
    Collection<String> categories = new ArrayList<>();
    when(commentService.createComment(eq(communityUser), anyString(), eq(target), eq(categories), nullable(Comment.class))).thenReturn(comment);

    Comment createdComment = commentGenerator.createComment(POST_MODERATION, communityUser, "test", target, categories, true, false);
    assertNotNull(createdComment);
    assertEquals(1, commentGenerator.getCommentWithAttachmentCount());
    assertEquals(1, commentGenerator.getPostModerationCommentCount());
    assertEquals(0, commentGenerator.getPreModerationCommentCount());
    assertEquals(0, commentGenerator.getNoModerationCommentCount());
    assertEquals(1, commentGenerator.getCommentCount());
    verify(commentService, never()).getComments(any(), any(CommunityUser.class), eq(ASCENDING), eq(Integer.MAX_VALUE));
    verify(commentService).createComment(eq(communityUser), anyString(), eq(target), eq(categories), nullable(Comment.class));
    verify(commentService).save(comment, POST_MODERATION);
    verify(comment, never()).setAuthorName(anyString());
  }

  @Test
  public void createCommentPreModeration() {
    Collection<String> categories = new ArrayList<>();
    when(commentService.createComment(eq(communityUser), anyString(), eq(target), eq(categories), nullable(Comment.class))).thenReturn(comment);

    Comment createdComment = commentGenerator.createComment(PRE_MODERATION, communityUser, "test", target, categories, true, false);
    assertNotNull(createdComment);
    assertEquals(1, commentGenerator.getCommentWithAttachmentCount());
    assertEquals(0, commentGenerator.getPostModerationCommentCount());
    assertEquals(1, commentGenerator.getPreModerationCommentCount());
    assertEquals(0, commentGenerator.getNoModerationCommentCount());
    assertEquals(1, commentGenerator.getCommentCount());
    verify(commentService, never()).getComments(any(), any(CommunityUser.class), eq(ASCENDING), eq(Integer.MAX_VALUE));
    verify(commentService).createComment(eq(communityUser), anyString(), eq(target), eq(categories), nullable(Comment.class));
    verify(commentService).save(comment, PRE_MODERATION);
    verify(comment, never()).setAuthorName(anyString());
  }

  @Test
  public void createCommentNoModerationEmptyReply() {
    Collection<String> categories = new ArrayList<>();
    categories.add("default");
    when(commentService.createComment(eq(communityUser), anyString(), eq(target), eq(categories), isNull())).thenReturn(comment);
    List<Blob> blobs = new ArrayList<>();
    blobs.add(blob);
    when(comment.getAttachments()).thenReturn(blobs);

    Comment createdComment = commentGenerator.createComment(NONE, communityUser, "test", target, categories, true, true);
    assertNotNull(createdComment);
    assertEquals(1, commentGenerator.getCommentWithAttachmentCount());
    assertEquals(0, commentGenerator.getPostModerationCommentCount());
    assertEquals(0, commentGenerator.getPreModerationCommentCount());
    assertEquals(1, commentGenerator.getNoModerationCommentCount());
    assertEquals(1, commentGenerator.getCommentCount());
    verify(commentService).getComments(target, null, ASCENDING, Integer.MAX_VALUE);
    verify(commentService).createComment(eq(communityUser), anyString(), eq(target), eq(categories), isNull());
    verify(commentService).save(comment, NONE);
    verify(comment, never()).setAuthorName(anyString());
  }

  @Test
  public void createCommentAnonymousUserNoAttachmentsReply() {
    Collection<String> categories = new ArrayList<>();
    when(communityUser.isAnonymous()).thenReturn(true);
    when(commentService.createComment(eq(communityUser), anyString(), eq(target), eq(categories), any(Comment.class))).thenReturn(comment);
    when(commentService.getComments(target, null, ASCENDING, Integer.MAX_VALUE)).thenReturn(Arrays.asList(comment));
    Comment createdComment = commentGenerator.createComment(POST_MODERATION, communityUser, "test", target, categories, false, true);
    assertNotNull(createdComment);
    assertEquals(0, commentGenerator.getCommentWithAttachmentCount());
    assertEquals(1, commentGenerator.getPostModerationCommentCount());
    assertEquals(0, commentGenerator.getPreModerationCommentCount());
    assertEquals(0, commentGenerator.getNoModerationCommentCount());
    assertEquals(1, commentGenerator.getCommentCount());
    verify(commentService).getComments(target, null, ASCENDING, Integer.MAX_VALUE);
    verify(commentService).createComment(eq(communityUser), anyString(), eq(target), eq(categories), any(Comment.class));
    verify(commentService).save(comment, POST_MODERATION);
    verify(comment).setAuthorName("test");
  }

  @Test
  public void complainOnComment() {
    when(userGenerator.getRandomUser()).thenReturn(communityUser);
    commentGenerator.addTarget(target, true, true, true, true, POST_MODERATION);

    commentGenerator.complainOnComment(target, comment, false);
    verify(userGenerator, never()).createAnonymousUser();
    verify(userGenerator, atLeastOnce()).getRandomUser();
    assertTrue(commentGenerator.getCommentComplaintCount() > 0);
  }

  @Test
  public void complainOnCommentAnonymous() {
    when(userGenerator.createAnonymousUser()).thenReturn(communityUser);
    commentGenerator.addTarget(target, true, true, true, true, POST_MODERATION);

    commentGenerator.complainOnComment(target, comment, true);
    verify(userGenerator, atLeastOnce()).createAnonymousUser();
    verify(userGenerator, never()).getRandomUser();
    assertTrue(commentGenerator.getCommentComplaintCount() > 0);
  }

  @Test
  public void complainOnCommentNoTarget() {
    commentGenerator.complainOnComment(target, comment, true);
  }

  @Test
  public void getRandomTarget() {
    commentGenerator.addTarget(target, true, false, true, true, NONE);
    assertEquals(1, commentGenerator.getCommentingEnabledTargets().size());

    Object randomTarget = commentGenerator.getRandomTarget(false);
    assertEquals(target, randomTarget);
  }

  @Test
  public void getRandomTargetAnonymous() {
    commentGenerator.addTarget(target, true, true, true, true, NONE);
    assertEquals(1, commentGenerator.getAnonymousCommentingEnabledTargets().size());

    Object randomTarget = commentGenerator.getRandomTarget(true);
    assertEquals(target, randomTarget);
  }

  @Test
  public void getRandomTargetNull() {
    Object randomTarget = commentGenerator.getRandomTarget(false);
    assertEquals(null, randomTarget);
  }

  @Test
  public void getCommentModerationTypePostModeration() {
    commentGenerator.addTarget(target, true, true, true, true, POST_MODERATION);
    assertEquals(1, commentGenerator.getPostModerationTargets().size());

    ModerationType moderationType = commentGenerator.getModerationType(target);
    assertEquals(POST_MODERATION, moderationType);
  }

  @Test
  public void getCommentModerationTypePreModeration() {
    commentGenerator.addTarget(target, true, true, true, true, PRE_MODERATION);
    assertEquals(1, commentGenerator.getPreModerationTargets().size());

    ModerationType moderationType = commentGenerator.getModerationType(target);
    assertEquals(PRE_MODERATION, moderationType);
  }

  @Test
  public void getCommentModerationTypeNoModeration() {
    commentGenerator.addTarget(target, true, true, true, true, NONE);
    assertEquals(1, commentGenerator.getNoModerationTargets().size());

    ModerationType moderationType = commentGenerator.getModerationType(target);
    assertEquals(NONE, moderationType);
  }

  @Test
  public void getAnonymousNoModerationTargets() {
    commentGenerator.addTarget(target, true, true, false, false, NONE);
    Collection<Object> targets = commentGenerator.getAnonymousNoModerationTargets();
    assertEquals(1, targets.size());
    assertTrue(targets.contains(target));
  }

  @Test
  public void getAnonymousNoModerationTargetsEmpty() {
    commentGenerator.addTarget(target, true, false, false, false, NONE);
    Collection<Object> targets = commentGenerator.getAnonymousNoModerationTargets();
    assertEquals(0, targets.size());
  }

  @Test
  public void getAnonymousPostModerationTargets() {
    commentGenerator.addTarget(target, true, true, false, false, POST_MODERATION);
    Collection<Object> targets = commentGenerator.getAnonymousPostModerationTargets();
    assertEquals(1, targets.size());
    assertTrue(targets.contains(target));
  }

  @Test
  public void getAnonymousPostModerationTargetsEmpty() {
    commentGenerator.addTarget(target, true, false, false, false, POST_MODERATION);
    Collection<Object> targets = commentGenerator.getAnonymousPostModerationTargets();
    assertEquals(0, targets.size());
  }

  @Test
  public void getAnonymousPreModerationTargets() {
    commentGenerator.addTarget(target, true, true, false, false, PRE_MODERATION);
    Collection<Object> targets = commentGenerator.getAnonymousPreModerationTargets();
    assertEquals(1, targets.size());
    assertTrue(targets.contains(target));
  }

  @Test
  public void getAnonymousPreModerationTargetsEmpty() {
    commentGenerator.addTarget(target, true, false, false, false, PRE_MODERATION);
    Collection<Object> targets = commentGenerator.getAnonymousPreModerationTargets();
    assertEquals(0, targets.size());
  }
}
