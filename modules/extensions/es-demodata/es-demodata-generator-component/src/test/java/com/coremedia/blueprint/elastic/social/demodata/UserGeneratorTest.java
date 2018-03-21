package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.core.api.users.DuplicateEmailException;
import com.coremedia.elastic.core.api.users.DuplicateNameException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;

import static com.coremedia.elastic.social.api.ModerationType.NONE;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.ACTIVATED;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.ACTIVATED_MODERATION_REQUIRED;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.MODERATION_REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserGeneratorTest {
  @InjectMocks
  private UserGenerator userGenerator = new UserGenerator();

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private BlobService blobService;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private CommunityUser author;

  @Before
  public void setup() {
    when(blobService.put(nullable(InputStream.class), anyString(), eq("16.jpg"))).thenThrow(new RuntimeException());
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);

    userGenerator.initialize();
  }

  @Test
  public void initialize() {
    verify(communityUserService, atLeastOnce()).getUserByName(anyString());
    verify(communityUserService, atLeastOnce()).createUser(anyString(), anyString(), anyString());
    verify(communityUser, atLeastOnce()).save();
    assertTrue(userGenerator.getUserCount() > 0);
  }

  @Test
  public void createUserPostModeration() {
    reset(communityUserService);
    reset(communityUser);
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(POST_MODERATION);

    assertEquals(1, userGenerator.getPostModerationUserCount());
    assertEquals(initialUserCount + 1, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService).createUser(anyString(), anyString(), anyString());
    verify(communityUser).setProperty("state", ACTIVATED_MODERATION_REQUIRED);
    verify(communityUser).setImage(nullable(Blob.class));
    verify(communityUser).setGivenName(anyString());
    verify(communityUser).setSurName(anyString());
    verify(communityUser).save();
  }

  @Test
  public void createUserPreModeration() {
    reset(communityUserService);
    reset(communityUser);
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(PRE_MODERATION);

    assertEquals(1, userGenerator.getPreModerationUserCount());
    assertEquals(initialUserCount + 1, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService).createUser(anyString(), anyString(), anyString());
    verify(communityUser).setProperty("state", MODERATION_REQUIRED);
    verify(communityUser).setImage(nullable(Blob.class));
    verify(communityUser).setGivenName(anyString());
    verify(communityUser).setSurName(anyString());
    verify(communityUser).save();
  }


  @Test
  public void createUserNoModeration() {
    reset(communityUserService);
    reset(communityUser);
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(NONE);

    assertEquals(initialUserCount + 1, userGenerator.getNoModerationUserCount());
    assertEquals(initialUserCount + 1, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService).createUser(anyString(), anyString(), anyString());
    verify(communityUser).setProperty("state", ACTIVATED);
    verify(communityUser).setImage(nullable(Blob.class));
    verify(communityUser).setGivenName(anyString());
    verify(communityUser).setSurName(anyString());
    verify(communityUser).save();
  }

  @Test
  public void createExistingUser() {
    reset(communityUserService);
    reset(communityUser);
    when(communityUserService.getUserByName(anyString())).thenReturn(communityUser);
    when(communityUser.getState()).thenReturn(ACTIVATED_MODERATION_REQUIRED);
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(POST_MODERATION);

    assertEquals(0, userGenerator.getPostModerationUserCount());
    assertEquals(initialUserCount, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService, never()).createUser(anyString(), anyString(), anyString());
    verify(communityUser, never()).setProperty(eq("state"), any());
    verify(communityUser, never()).setImage(nullable(Blob.class));
    verify(communityUser, never()).setGivenName(anyString());
    verify(communityUser, never()).setSurName(anyString());
    verify(communityUser, never()).save();
  }

  @Test
  public void createExistingUserUpdateState() {
    reset(communityUserService);
    reset(communityUser);
    when(communityUserService.getUserByName(anyString())).thenReturn(communityUser);
    when(communityUser.getState()).thenReturn(MODERATION_REQUIRED);
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(POST_MODERATION);

    assertEquals(0, userGenerator.getPostModerationUserCount());
    assertEquals(initialUserCount, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService, never()).createUser(anyString(), anyString(), anyString());
    verify(communityUser).setProperty("state", ACTIVATED_MODERATION_REQUIRED);
    verify(communityUser, never()).setImage(nullable(Blob.class));
    verify(communityUser, never()).setGivenName(anyString());
    verify(communityUser, never()).setSurName(anyString());
    verify(communityUser).save();
  }

  @Test
  public void createExistingUserDuplicateEmail() {
    reset(communityUserService);
    reset(communityUser);
    doThrow(new DuplicateEmailException("user exists", new RuntimeException())).when(communityUser).save();
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(POST_MODERATION);

    assertEquals(0, userGenerator.getPostModerationUserCount());
    assertEquals(initialUserCount, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService).createUser(anyString(), anyString(), anyString());
    verify(communityUser).setProperty("state", ACTIVATED_MODERATION_REQUIRED);
    verify(communityUser).setImage(nullable(Blob.class));
    verify(communityUser).setGivenName(anyString());
    verify(communityUser).setSurName(anyString());
    verify(communityUser).save();
  }

  @Test
  public void createExistingUserDuplicateName() {
    reset(communityUserService);
    reset(communityUser);
    doThrow(new DuplicateNameException("user exists", new RuntimeException())).when(communityUser).save();
    when(communityUserService.createUser(anyString(), anyString(), anyString())).thenReturn(communityUser);
    int initialUserCount = userGenerator.getUserCount();
    userGenerator.createUser(POST_MODERATION);

    assertEquals(0, userGenerator.getPostModerationUserCount());
    assertEquals(initialUserCount, userGenerator.getUserCount());
    verify(communityUserService).getUserByName(anyString());
    verify(communityUserService).createUser(anyString(), anyString(), anyString());
    verify(communityUser).setProperty("state", ACTIVATED_MODERATION_REQUIRED);
    verify(communityUser).setImage(nullable(Blob.class));
    verify(communityUser).setGivenName(anyString());
    verify(communityUser).setSurName(anyString());
    verify(communityUser).save();
  }

  @Test
  public void complainOnUser() {
    when(communityUserService.createAnonymousUser()).thenReturn(author);
    userGenerator.complainOnUser(communityUser);

    verify(author, atLeastOnce()).save();
    verify(communityUserService, atLeastOnce()).addComplaint(author, communityUser);
    assertTrue(userGenerator.getUserComplaintCount() > 0);
  }

  @Test
  public void changeUserDetailsPostModeration() {
    reset(communityUserService);
    reset(communityUser);
    userGenerator.changeUserDetails(POST_MODERATION, communityUser);

    verify(communityUserService).storeChanges(communityUser, POST_MODERATION);
    verify(communityUser).save();
    verify(communityUser).setGivenName(anyString());
    assertEquals(1, userGenerator.getUserChangesPostModerationCount());
    assertEquals(0, userGenerator.getUserChangesPreModerationCount());
  }

  @Test
  public void changeUserDetailsPreModeration() {
    reset(communityUserService);
    reset(communityUser);
    userGenerator.changeUserDetails(PRE_MODERATION, communityUser);

    verify(communityUserService).storeChanges(communityUser, PRE_MODERATION);
    verify(communityUser).save();
    verify(communityUser).setGivenName(anyString());
    assertEquals(0, userGenerator.getUserChangesPostModerationCount());
    assertEquals(1, userGenerator.getUserChangesPreModerationCount());
  }

  @Test
  public void getRandomUser() {
    CommunityUser user = userGenerator.getRandomUser();
    assertNotNull(user);
  }
}
