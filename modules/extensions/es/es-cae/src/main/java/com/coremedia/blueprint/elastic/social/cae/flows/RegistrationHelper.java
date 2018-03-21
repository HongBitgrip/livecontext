package com.coremedia.blueprint.elastic.social.cae.flows;

import com.coremedia.blueprint.base.elastic.common.ImageHelper;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.elastic.social.cae.controller.BlobRefImpl;
import com.coremedia.blueprint.elastic.social.cae.springsocial.SpringSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobException;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.core.api.settings.Settings;
import com.coremedia.elastic.core.api.users.DuplicateEmailException;
import com.coremedia.elastic.core.api.users.DuplicateNameException;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.mail.MailException;
import com.coremedia.elastic.social.api.registration.RegistrationService;
import com.coremedia.elastic.social.api.registration.TokenExpiredException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.SocialAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import static com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper.addErrorMessage;
import static com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper.addErrorMessageWithSource;
import static com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper.addInfoMessage;
import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;
import static com.coremedia.common.logging.BaseMarker.UNCLASSIFIED_PERSONAL_DATA;
import static com.coremedia.elastic.social.api.ModerationType.PRE_MODERATION;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * A helper used by the registration web flow
 */
@Named
public class RegistrationHelper {

  private static final Logger LOG = getLogger(RegistrationHelper.class);

  private static final String PROFILE_IMAGE_ID = "profileImage";

  public static final String ELASTIC_AUTOMATIC_USER_ACTIVATION = "elastic.automatic.user.activation";

  @Inject
  private RegistrationService registrationService;

  @Inject
  private BlobService blobService;

  @Inject
  private CommunityUserService communityUserService;

  @Inject
  private LoginHelper loginHelper;

  @Inject
  private Settings settings;

  @Inject
  private ElasticSocialPlugin elasticSocialPlugin;

  @Inject
  private SpringSocialConfiguration springSocialConfiguration;

  private ProviderSignInUtils providerSignInUtils;

  @Inject
  @Named("httpClientAutoRedirect")
  private HttpClient httpClient;

  private boolean automaticActivationEnabled;

  @PostConstruct
  void initialize() {
    automaticActivationEnabled = settings.getBoolean(ELASTIC_AUTOMATIC_USER_ACTIVATION, false);

    providerSignInUtils = new ProviderSignInUtils(springSocialConfiguration.connectionFactoryLocator(),
            springSocialConfiguration.usersConnectionRepository());
  }

  public void preProcess(Registration registration, RequestContext context) {
    Connection<?> connection = providerSignInUtils.getConnectionFromSession(getRequestAttributes(context)); // NOSONAR
    registration.setRegisteringWithProvider(connection != null);
    if (connection != null) {
      org.springframework.social.connect.UserProfile userProfile = connection.fetchUserProfile();

      String email = userProfile.getEmail();
      String userName = userProfile.getUsername();

      registration.setUsername(userName);
      registration.setGivenname(userProfile.getFirstName());
      registration.setSurname(userProfile.getLastName());
      registration.setEmailAddress(email);
      String imageUrl = connection.getImageUrl();
      if (imageUrl != null) {
        getProfileImage(connection, context, registration, userProfile.getUsername(), imageUrl);
      }

      // CMS-2581
      if (StringUtils.isNotBlank(email) && communityUserService.getUserByEmail(email) != null) {
        addErrorMessageWithSource(context, "registration.emailAddress.notAvailable", "emailAddress");
      }

      if (StringUtils.isNotBlank(userName) && communityUserService.getUserByName(userName) != null) {
        addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_USERNAME_NOT_AVAILABLE, "username");
      }
    } else if (UserContext.getUser() != null) {
      // The user may already exist during a dual registration.
      CommunityUser user = UserContext.getUser();
      registration.setUsername(user.getName());
      registration.setGivenname(user.getGivenName());
      registration.setSurname(user.getSurName());
      registration.setEmailAddress(user.getEmail());
    }
  }

  private void getProfileImage(Connection<?> connection, RequestContext context, Registration registration,
                               String userName, String imageUrl) {
    try {
      HttpGet request = new HttpGet(new URI(imageUrl));
      HttpResponse response = httpClient.execute(request);
      try {
        if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
          HttpEntity entity = response.getEntity();
          Blob profileImage = blobService.put(entity.getContent(), entity.getContentType().getValue(), userName);
          registration.setProfileImage(new BlobRefImpl(profileImage.getId()));
        }
      } finally {
        request.releaseConnection();
      }
    } catch (URISyntaxException | IOException e) {
      addErrorMessage(context, "registration.imageFetch.error", connection.getKey().getProviderId());
      LOG.error(UNCLASSIFIED_PERSONAL_DATA, "error while retrieving profile image from {}", imageUrl, e);
    }
  }

  /**
   * Register a new user.
   *
   * @param registration         the flow model
   * @param context              the calling flow's {@link RequestContext}
   * @param userProfileImage     the user's profile image
   * @param additionalProperties additional user properties
   * @return true if registering the user succeeded, false otherwise.
   */
  public CommunityUser register(Registration registration, RequestContext context,
                                CommonsMultipartFile userProfileImage, Map<String, Object> additionalProperties) {
    if (context.getMessageContext().hasErrorMessages()) {
      return null;
    }

    try {
      Map<String, Object> userProperties = new HashMap<>();

      if (additionalProperties != null) {
        userProperties.putAll(additionalProperties);
      }

      userProperties.put("givenName", registration.getGivenname());
      userProperties.put("surName", registration.getSurname());
      if (registration.getProfileImage() != null && !registration.isDeleteProfileImage()) {
        userProperties.put("image", blobService.get(registration.getProfileImage().getId()));
      }

      ServletRequest servletRequest = (ServletRequest) context.getExternalContext().getNativeRequest();
      Optional<Site> siteFromRequest = SiteHelper.findSite(servletRequest);

      siteFromRequest.ifPresent(site -> userProperties.put("site", site));

      Locale locale = siteFromRequest
              .map(Site::getLocale)
              .orElseGet(() -> context.getExternalContext().getLocale());

      TimeZone timeZone = null;
      if (StringUtils.isNotBlank(registration.getTimeZoneId())) {
        timeZone = TimeZone.getTimeZone(registration.getTimeZoneId());
      }

      CommunityUser user = registrationService.register(registration.getUsername(), registration.getPassword(),
              registration.getEmailAddress(), locale, timeZone, userProperties);

      saveProfileImage(context, userProfileImage, user);
      providerSignInUtils.doPostSignUp(user.getId(), getRequestAttributes(context));

      if (isAutomaticActivationEnabled(context)) {
        LOG.info(PERSONAL_DATA, "Automatically activate user '{}'", registration.getUsername());
        activate(user.getProperty("token", String.class), context);
      }

      return user;
    } catch (DuplicateEmailException e) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_EMAIL_ADDRESS_NOT_AVAILABLE, "emailAddress");
    } catch (DuplicateNameException e) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_USERNAME_NOT_AVAILABLE, "username");
    } catch (MailException e) {
      LOG.warn(UNCLASSIFIED_PERSONAL_DATA, "Exception during Registration", e);
      addErrorMessage(context, WebflowMessageKeys.REGISTRATION_ACTIVATION_MESSAGE_ERROR);
    }
    return null;
  }

  /**
   * Register a new user.
   *
   * @param registration the flow model
   * @param context      the calling flow's {@link RequestContext}
   * @param file         the user's profile image
   * @return true if registering the user succeeded, false otherwise.
   */
  public boolean register(Registration registration, RequestContext context, CommonsMultipartFile file) {
    return register(registration, context, file, new HashMap<String, Object>()) != null;
  }

  private boolean isAutomaticActivationEnabled(@Nonnull RequestContext context) {
    RequestAttributes requestAttributes = getRequestAttributes(context);
    List<String> scopeAttributeNames = Arrays.asList(requestAttributes.getAttributeNames(SCOPE_REQUEST));

    if (scopeAttributeNames.contains(ELASTIC_AUTOMATIC_USER_ACTIVATION)) {
      Object attributeValue = requestAttributes.getAttribute(ELASTIC_AUTOMATIC_USER_ACTIVATION, SCOPE_REQUEST);
      return Boolean.valueOf(attributeValue + "");
    }

    return automaticActivationEnabled;
  }

  private void saveProfileImage(@Nonnull RequestContext context, CommonsMultipartFile file,
                                @Nonnull CommunityUser user) {
    if (file != null && file.getSize() > 0) {
      ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(context);

      int maxImageFileSize = elasticSocialConfiguration.getMaxImageFileSize();
      if (file.getSize() > maxImageFileSize) {
        addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_IMAGE_FILE_TOO_BIG_ERROR, PROFILE_IMAGE_ID,
                ImageHelper.getBytesAsKBString(maxImageFileSize));
      } else if (!ImageHelper.isSupportedMimeType(file.getContentType())) {
        addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_IMAGE_FILE_UNSUPPORTED_CONTENT_TYPE,
                PROFILE_IMAGE_ID, ImageHelper.getSupportedMimeTypesString());
      } else {
        try {
          user.setImage(blobService.put(file.getInputStream(), file.getContentType(), file.getOriginalFilename()));
          user.save();
        } catch (BlobException | IOException e) {
          addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_IMAGE_FILE_ERROR, PROFILE_IMAGE_ID);
        }
      }
    }
  }

  /**
   * Activate a pending registration request for the given activation key.
   *
   * @param activationKey an activation key
   * @param context       the executing flow's {@link RequestContext}
   * @return true if the activation succeeded, false otherwise
   */
  public boolean activate(String activationKey, @Nonnull RequestContext context) {
    try {
      CommunityUser user = registrationService.getUserByToken(activationKey);

      RequestContext requestContext = RequestContextHolder.getRequestContext();
      ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(requestContext);
      ModerationType userModerationType = elasticSocialConfiguration.getUserModerationType();

      boolean success = registrationService.activateRegistration(activationKey, userModerationType);

      if (!success) {
        addErrorMessage(context, WebflowMessageKeys.ACTIVATE_REGISTRATION_REGISTRATION_KEY_NOT_FOUND);
        return false;
      }

      if (userModerationType == PRE_MODERATION) {
        addInfoMessage(context, WebflowMessageKeys.ACTIVATE_REGISTRATION_SUCCESS_PREMODERATION_REQUIRED);
      } else {
        addInfoMessage(context, WebflowMessageKeys.ACTIVATE_REGISTRATION_SUCCESS);
      }

      Authentication authenticationToken = new SocialAuthenticationToken(user.getName(), "");
      return loginHelper.authenticate(authenticationToken, context);
    } catch (TokenExpiredException e) {
      addErrorMessage(context, WebflowMessageKeys.ACTIVATE_REGISTRATION_REGISTRATION_KEY_EXPIRED);
    }
    return false;
  }

  /**
   * Redirect a logged in user to the home page instead of the registration page.
   *
   * @param context the executing flow's {@link RequestContext}
   */
  public void redirectLoggedInUserToHomePage(@Nonnull RequestContext context) {
    if (UserContext.getUser() != null) {
      context.getExternalContext().requestExternalRedirect("contextRelative:");
    }
  }

  @Nonnull
  public static RequestAttributes getRequestAttributes(@Nonnull RequestContext context) {
    HttpServletRequest servletRequest = getServletRequest(context);
    return new ServletRequestAttributes(servletRequest);
  }

  public void postProcessProviderRegistration(@Nonnull RequestContext context) {
    if (context.getRequestParameters().contains("error")) {
      addErrorMessage(context, WebflowMessageKeys.REGISTRATION_PROVIDER_ERROR);
    }

    SharedAttributeMap sessionMap = context.getExternalContext().getSessionMap();
    String messageKey = (String) sessionMap.remove("providerLogin.messageKey");
    if (messageKey != null) {
      addErrorMessage(context, WebflowMessageKeys.REGISTRATION_ACCOUNT_DEACTIVATED_ERROR);
    }
  }

  @Nonnull
  private ElasticSocialConfiguration getElasticSocialConfiguration(@Nonnull RequestContext context) {
    HttpServletRequest servletRequest = getServletRequest(context);
    Page page = RequestAttributeConstants.getPage(servletRequest);
    return elasticSocialPlugin.getElasticSocialConfiguration(page);
  }

  private static HttpServletRequest getServletRequest(@Nonnull RequestContext context) {
    return (HttpServletRequest) context.getExternalContext().getNativeRequest();
  }
}
