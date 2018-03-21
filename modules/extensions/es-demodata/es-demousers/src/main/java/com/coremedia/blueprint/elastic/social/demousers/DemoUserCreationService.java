package com.coremedia.blueprint.elastic.social.demousers;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.core.api.settings.Settings;
import com.coremedia.elastic.core.api.users.DuplicateEmailException;
import com.coremedia.elastic.core.api.users.DuplicateNameException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.mimetype.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;
import static com.coremedia.elastic.social.api.users.CommunityUser.State.ACTIVATED;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Creates Demo-Users when registering a tenant. Users can be defined using ordinary component properties prefixed
 * with <code>es.demo.users.KEY</code> where KEY is an arbitrary unique identifier of the the user to lookup or create.
 * Users consists of
 * <ul>
 * <li>Username</li>
 * <li>Givenname</li>
 * <li>Surname</li>
 * <li>E-Mail</li>
 * <li>Password</li>
 * <li>Image</li>
 * </ul>
 * See es-demousers-example.properties for example configurations.
 */
@Named
public class DemoUserCreationService {

  private static final Logger LOG = LoggerFactory.getLogger(DemoUserCreationService.class);

  private static final String PASSWORD = "password"; // NOSONAR false positive: Credentials should not be hard-coded
  private static final String ES_DEMO_USERS_KEY = "es.demo.users";

  @Inject
  private CommunityUserService communityUserService;
  @Inject
  private BlobService blobService;
  @Inject
  private Settings settings;
  @Inject
  private ResourceLoader resourceLoader;
  @Inject
  private MimeTypeService mimeTypeService;

  void createDemoUsersFor(String tenant) {
    final Properties configuredDemoUsers = settings.getSettingsWithPrefix(ES_DEMO_USERS_KEY);
    configuredDemoUsers.putAll(settings.getSettingsWithPrefix(tenant + '.' + ES_DEMO_USERS_KEY));
    if (!configuredDemoUsers.isEmpty()) {
      final Map<String, Map<String, String>> usersByKey = newHashMap();
      for (Object key : configuredDemoUsers.keySet()) {
        if(key instanceof String) {
          final String keyString = (String) key;
          final String propertyValue = configuredDemoUsers.getProperty(keyString);
          addConfiguredDemoUserProperty(usersByKey, keyString, propertyValue);
        } else {
          LOG.warn("cannot handle configured demo user value for key {}", key);
        }
      }

      for(Map<String, String> userProperties : usersByKey.values()) {
        final CommunityUser communityUser = getOrCreateCommunityUser(userProperties);
        LOG.debug(PERSONAL_DATA, "Found demo user: {}", communityUser);
      }
    }
  }

  private void addConfiguredDemoUserProperty(Map<String, Map<String, String>> usersByKey, String keyString, String propertyValue) {
    final String[] strings = keyString.split("\\.");
    if(strings.length == 2) {
      final String prefix = strings[0];
      final String propertyName = strings[1];
      Map<String, String> userConfig = usersByKey.get(prefix);
      if(null == userConfig) {
        userConfig = newHashMap();
        usersByKey.put(prefix, userConfig);
      }
      userConfig.put(propertyName, propertyValue);
    } else {
      LOG.warn(PERSONAL_DATA, "cannot handle configured demo user value {} for key {}", propertyValue, keyString);
    }
  }

  private CommunityUser getOrCreateCommunityUser(Map<String, String> userProperties) {
    final String username = userProperties.get("username");
    CommunityUser communityUser = communityUserService.getUserByName(username);
    if (null == communityUser) {
      String email = userProperties.get("email");
      try {
        communityUser = communityUserService.createUser(username, userProperties.get(PASSWORD), email);
        communityUser.setProperty("state", ACTIVATED);
        communityUser.setImage(getConfiguredImageBlob(userProperties));
        communityUser.setProperties(new HashMap<String, Object>(userProperties));
        communityUser.setLocale(Locale.getDefault());
        communityUser.save();
        LOG.info(PERSONAL_DATA, "Created communityUser with name {}: {}", username, communityUser);
      } catch (DuplicateEmailException e) {
        LOG.warn(PERSONAL_DATA, "User with duplicate email {}", e.getEmail());
      } catch (DuplicateNameException e) {
        LOG.warn(PERSONAL_DATA, "User with duplicate name {}", e.getName());
      }
    } else {
      LOG.info(PERSONAL_DATA, "User with name {} already exists", username);
    }
    return communityUser;
  }

  private Blob getConfiguredImageBlob(Map<String, String> userProperties) {
    final String imageLocation = userProperties.remove("image");
    if(imageLocation != null) {
      Resource imageResource = resourceLoader.getResource(imageLocation);
      if (imageResource.exists() && imageResource.isReadable()) {
        final String filename = imageResource.getFilename();
        try {
          // buffer stream to make it markable
          @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
          InputStream bufferedInputStream = new BufferedInputStream(imageResource.getInputStream());
          String mimeType = mimeTypeService.detectMimeType(bufferedInputStream, filename, null);
          return blobService.put(bufferedInputStream, mimeType, filename);
        } catch (IOException e) {
          LOG.warn("cannot create blob from configured image {}: {}", imageResource, e.getMessage());
        }
      } else {
        LOG.warn("configured image location {} is not readable", imageLocation);
      }
    }
    return null;
  }
}
