package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.core.api.users.DuplicateEmailException;
import com.coremedia.elastic.core.api.users.DuplicateNameException;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;
import static com.coremedia.elastic.social.api.ModerationType.POST_MODERATION;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.ACTIVATED;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.ACTIVATED_MODERATION_REQUIRED;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.MODERATION_REQUIRED;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMANY;
import static java.util.Locale.US;

/**
 * An {@link UserGenerator} generates users and complaints on users.
 */
public class UserGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(UserGenerator.class);

  private static final int DEFAULT_USER_COMPLAINT_MAXIMUM = 20;

  private static final String GIVENNAMES_FILE_NAME = "givennames.txt";
  private static final String SURNAMES_FILE_NAME = "surnames.txt";
  private static final String USERNAMES_FILE_NAME = "usernames.txt";

  private List<Blob> userImageList;
  private List<String> usernameList;
  private List<String> givenNameList;
  private List<String> surNameList;
  private final List<CommunityUser> users = new ArrayList<>();

  private int userCount = 0;
  private int postModerationUserCount = 0;
  private int preModerationUserCount = 0;
  private int noModerationUserCount = 0;
  private int userChangesPreModerationCount = 0;
  private int userChangesPostModerationCount = 0;
  private int userComplaintCount = 0;
  private Random random = new Random();

  private List<Locale> possibleLocales = ImmutableList.of(US, GERMANY);

  @Inject
  private CommunityUserService communityUserService;

  @Inject
  private BlobService blobService;

  public void initialize() {
    givenNameList = GeneratorUtils.loadListFromFile(GIVENNAMES_FILE_NAME);
    surNameList = GeneratorUtils.loadListFromFile(SURNAMES_FILE_NAME);
    usernameList = GeneratorUtils.loadListFromFile(USERNAMES_FILE_NAME);
    userImageList = GeneratorUtils.loadImages("", "image/jpeg", "jpg", blobService);

    createInitialUsers();
  }

  private void createInitialUsers() {
    for (String userName : usernameList) {
      int numberOfUsers = userCount;
      CommunityUser user = createUser(userName.toLowerCase(ENGLISH), ACTIVATED);
      if (user != null) {
        synchronized (users) {
          users.add(user);
        }
        if (userCount > numberOfUsers) {
          noModerationUserCount++;
        }
      }
    }
  }

  @Nonnull
  public synchronized CommunityUser getRandomUser() {
    CommunityUser user;
    if(users.size() == 0) {
      throw new IllegalStateException("No users available which could be returned");
    }
    synchronized (users) {
      user = users.get(random.nextInt(users.size()));
    }
    return user;
  }
  
  public synchronized String getRandomUserName() {
    return usernameList.get(random.nextInt(usernameList.size()));
  }
  
  public Locale getRandomLocale() {
    return possibleLocales.get(random.nextInt(possibleLocales.size()));
  }

  public CommunityUser createUser(ModerationType userModerationType) {
    CommunityUser user;
    String userName = getRandomUserName();
    int numberOfUsers = userCount;
    if (userModerationType == POST_MODERATION) {
      user = createUser(userName + random.nextInt(), ACTIVATED_MODERATION_REQUIRED);
      if (userCount > numberOfUsers) {
        postModerationUserCount++;
      }
    } else if (userModerationType == PRE_MODERATION) {
      user = createUser(userName + random.nextInt(), MODERATION_REQUIRED);
      if (userCount > numberOfUsers) {
        preModerationUserCount++;
      }
    } else {
      user = createUser(userName + random.nextInt(), ACTIVATED);
      if (userCount > numberOfUsers) {
        noModerationUserCount++;
      }
    }
    return user;
  }

  private CommunityUser createUser(String userName, CommunityUser.State userState) {
    CommunityUser user = communityUserService.getUserByName(userName);
    if (user == null) {
      String email = userName.replaceAll(" ", "_") + "@example.com";
      try {
        user = communityUserService.createUser(userName, userName, email);
        user.setProperty("state", userState);
        user.setImage(getBlobCopy(userImageList.get(random.nextInt(userImageList.size()))));
        user.setGivenName(givenNameList.get(random.nextInt(givenNameList.size())));
        user.setSurName(surNameList.get(random.nextInt(surNameList.size())));
        user.setLocale(getRandomLocale());
        user.save();
        LOG.debug(PERSONAL_DATA, "Created user with name {}: {}", userName, user);
        userCount++;
      } catch (DuplicateEmailException e) {
        LOG.warn(PERSONAL_DATA, "User with duplicate email {}", e.getEmail());
        return null;
      } catch (DuplicateNameException e) {
        LOG.warn(PERSONAL_DATA, "User with duplicate name {}", e.getName());
        return null;
      }
    } else {
      LOG.info(PERSONAL_DATA, "User with name {} already exists", userName);
      if (!userState.equals(user.getState())) {
        user.setProperty("state", userState);
        try {
          user.save();
        } catch (DuplicateEmailException e) {
          LOG.warn(PERSONAL_DATA, "User with duplicate email {}", e.getEmail());
        } catch (DuplicateNameException e) {
          LOG.warn(PERSONAL_DATA, "User with duplicate name {}", e.getName());
        }
      }
    }
    return user;
  }

  private Blob getBlobCopy(Blob blob) {
    return blob == null ? null : blobService.put(getInputStream(blob), blob.getContentType(), blob.getFileName());
  }

  @Nonnull
  public CommunityUser createAnonymousUser() {
    CommunityUser user = communityUserService.createAnonymousUser();
    user.save();
    LOG.debug(PERSONAL_DATA, "Created anonymous user with id: {}", user.getId());
    return user;
  }

  public void createAnonymousUsers(int count) {
    LOG.info("Creating {} anonymous users", count);
    for (int i=0;i<count;i++) {
      CommunityUser user = communityUserService.createAnonymousUser();
      user.save();
      LOG.trace(PERSONAL_DATA, "Created anonymous user with id: {}", user.getId());
    }
  }

  public void changeUserDetails(ModerationType userModerationType, CommunityUser user) {
    user.setGivenName(user.getGivenName() + random.nextInt());
    LOG.debug(PERSONAL_DATA, "Change given name of user {} in with moderation type {} to {}", user.getName(), userModerationType, user.getGivenName());
    communityUserService.storeChanges(user, userModerationType);
    user.save();

    if (userModerationType == POST_MODERATION) {
      userChangesPostModerationCount++;
    } else if (userModerationType == PRE_MODERATION) {
      userChangesPreModerationCount++;
    }
  }

  public void complainOnUser(CommunityUser user) {
    for (int i = 0, size = random.nextInt(DEFAULT_USER_COMPLAINT_MAXIMUM) + 1; i < size; i++) {
      CommunityUser author = createAnonymousUser();
      communityUserService.addComplaint(author, user);
      userComplaintCount++;
      LOG.debug(PERSONAL_DATA, "Created complaint from {}, anonymous {} for user with id: {}", author.getId(),author.isAnonymous(), user.getId());
    }
  }

// Encapsulation for statistic
  public int getUserCount() {
    return userCount;
  }

  public int getUserComplaintCount() {
    return userComplaintCount;
  }

  public int getPostModerationUserCount() {
    return postModerationUserCount;
  }

  public int getPreModerationUserCount() {
    return preModerationUserCount;
  }

  public int getNoModerationUserCount() {
    return noModerationUserCount;
  }

  public int getUserChangesPreModerationCount() {
    return userChangesPreModerationCount;
  }

  public int getUserChangesPostModerationCount() {
    return userChangesPostModerationCount;
  }

  public InputStream getInputStream(Blob blob) {
    if (blob == null) {
      return null;
    }
    byte[] bytes;
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) blob.getLength());
      blob.writeOn(outputStream);
      // no need to close stream
      bytes = outputStream.toByteArray();
    } catch (IOException e) {
      throw new IllegalArgumentException("cannot read blob", e);
    }
    return new ByteArrayInputStream(bytes);
  }

}
