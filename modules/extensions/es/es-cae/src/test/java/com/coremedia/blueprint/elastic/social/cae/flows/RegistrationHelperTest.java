package com.coremedia.blueprint.elastic.social.cae.flows;

import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.elastic.social.cae.springsocial.SpringSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobException;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.core.api.settings.Settings;
import com.coremedia.elastic.core.api.users.DuplicateEmailException;
import com.coremedia.elastic.core.api.users.DuplicateNameException;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.mail.MailTemplateNotFoundException;
import com.coremedia.elastic.social.api.registration.RegistrationService;
import com.coremedia.elastic.social.api.registration.TokenExpiredException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.SocialAuthenticationToken;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.DefaultMessageResolver;
import org.springframework.binding.message.MessageResolver;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys.ACTIVATE_REGISTRATION_SUCCESS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationHelperTest {
  private static final HashMap<String, Object> USER_PROPERTIES = new HashMap<>();
  private static final String USERNAME = "knacki";
  private static final String GIVENNAME = "backi";
  private static final String SURNAME = "hacki";
  private static final String PASSWORD = "geheim";
  private static final String EMAIL = USERNAME + "@bologna.it";
  private static final Locale LOCALE = Locale.ENGLISH;
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final String ACTIVATION_KEY = "1234";
  private Registration registration;

  @InjectMocks
  private RegistrationHelper registrationHelper;

  @Mock
  private RegistrationService registrationService;

  @Mock
  private RequestContext requestContext;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private ExternalContext externalContext;

  @Mock
  private DefaultMessageContext messageContext;

  @Mock
  private BlobService blobService;

  @Mock
  private Blob blob;

  @Mock
  private InputStream inputStream;

  @Mock
  private CommonsMultipartFile file;

  @Mock
  private HttpServletRequest request;

  @Mock
  private Page page;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private Connection connection;

  @Mock
  private org.springframework.social.connect.UserProfile profile;

  @Mock
  private HttpSession httpSession;

  @Mock
  private ProviderSignInAttempt providerSignInAttempt;

  @Mock
  private HttpClient httpClient;

  @Mock
  private HttpResponse httpResponse;

  @Mock
  private StatusLine statusLine;

  @Mock
  private HttpEntity entity;

  @Mock
  private Header contentType;

  @Mock
  private SharedAttributeMap sessionMap;

  @Mock
  private PasswordPolicy passwordPolicy;

  @Mock
  private Settings settings;

  @Mock
  private ParameterMap parameterMap;

  @Mock
  private LoginHelper loginHelper;

  @Mock
  private Enumeration<String> headerNames;

  @Mock
  private Site site;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private SpringSocialConfiguration springSocialConfiguration;

  @SuppressWarnings("unchecked")
  @Before
  public void init() throws IOException, URISyntaxException {
    USER_PROPERTIES.put("givenName", GIVENNAME);
    USER_PROPERTIES.put("surName", SURNAME);
    registration = initRegistration();
    when(requestContext.getMessageContext()).thenReturn(messageContext);
    RequestContextHolder.setRequestContext(requestContext);
    when(externalContext.getLocale()).thenReturn(LOCALE);
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getNativeRequest()).thenReturn(request);
    when(request.getAttribute("cmpage")).thenReturn(page);
    when(messageContext.hasErrorMessages()).thenReturn(false);

    when(blobService.put(inputStream, CONTENT_TYPE, null)).thenReturn(blob);
    when(blobService.put(inputStream, CONTENT_TYPE, USERNAME)).thenReturn(blob);
    when(file.getInputStream()).thenReturn(inputStream);
    when(file.getContentType()).thenReturn(CONTENT_TYPE);

    when(elasticSocialPlugin.getElasticSocialConfiguration(any())).thenReturn(elasticSocialConfiguration);
    when(elasticSocialConfiguration.getMaxImageFileSize()).thenReturn(512000);

    when(request.getAttribute("cmpage")).thenReturn(page);

    when(profile.getEmail()).thenReturn(EMAIL);
    when(profile.getUsername()).thenReturn(USERNAME);

    when(connection.fetchUserProfile()).thenReturn(profile);
    when(connection.getImageUrl()).thenReturn("http://download.oracle.com/index.html");

    when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);
    when(httpResponse.getStatusLine()).thenReturn(statusLine);
    when(httpResponse.getEntity()).thenReturn(entity);
    when(statusLine.getStatusCode()).thenReturn(org.apache.http.HttpStatus.SC_OK);
    when(entity.getContent()).thenReturn(inputStream);
    when(entity.getContentType()).thenReturn(contentType);
    when(contentType.getValue()).thenReturn(CONTENT_TYPE);

    when(providerSignInAttempt.getConnection(any(ConnectionFactoryLocator.class))).thenReturn(connection);
    when(httpSession.getAttribute(ProviderSignInAttempt.class.getName())).thenReturn(providerSignInAttempt);

    when(registrationService.getUserByToken(nullable(String.class))).thenReturn(communityUser);
    when(loginHelper.authenticate(any(SocialAuthenticationToken.class), any(RequestContext.class))).thenReturn(true);

    when(request.getSession(false)).thenReturn(httpSession);
    RequestAttributes attributes = new ServletRequestAttributes(request);
    when(request.getAttributeNames()).thenReturn(headerNames);
    when(headerNames.hasMoreElements()).thenReturn(false);

    when(attributes.getAttribute(ProviderSignInAttempt.class.getName(), RequestAttributes.SCOPE_SESSION)).thenReturn(providerSignInAttempt);

    registrationHelper.initialize();
  }

  @Test
  public void register() {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);

    boolean isRegistered = registrationHelper.register(registration, requestContext, null);

    assertTrue(isRegistered);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, never()).addMessage(any(MessageResolver.class));
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  public void registerButUsernameNotAvailable() {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenThrow(new DuplicateNameException(null, null));

    boolean isRegistered = registrationHelper.register(registration, requestContext, null);

    assertFalse(isRegistered);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, atLeast(1)).addMessage(any(MessageResolver.class));
    verify(communityUser, never()).save();
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  public void registerButEmailNotAvailable() {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenThrow(new DuplicateEmailException(null, null));

    boolean isRegistered = registrationHelper.register(registration, requestContext, null);

    assertFalse(isRegistered);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, atLeast(1)).addMessage(any(MessageResolver.class));
    verify(communityUser, never()).save();
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  public void registerWithMailException() {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenThrow(new MailTemplateNotFoundException("", LOCALE));

    boolean isRegistered = registrationHelper.register(registration, requestContext, null);

    assertFalse(isRegistered);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, atLeast(1)).addMessage(any(MessageResolver.class));
    verify(communityUser, never()).save();
  }

  @Test
  public void testRegisterWithImage() throws IOException {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);
    when(file.getSize()).thenReturn(1000L);

    boolean userSaved = registrationHelper.register(registration, requestContext, file);
    assertTrue(userSaved);

    verify(communityUser).save();
    verify(blobService).put(inputStream, CONTENT_TYPE, null);
    verify(communityUser).setImage(blob);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, never()).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testRegisterWithSite() throws IOException {
    HashMap<String, Object> properties = new HashMap<>();
    properties.put("givenName", GIVENNAME);
    properties.put("surName", SURNAME);
    properties.put("site", site);

    Locale locale = Locale.CANADA;
    when(site.getLocale()).thenReturn(locale);

    when(registrationService.register(USERNAME, PASSWORD, EMAIL, locale, TimeZone.getTimeZone("UTC"), properties)).thenReturn(communityUser);
    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(site);

    boolean userSaved = registrationHelper.register(registration, requestContext, file);
    assertTrue(userSaved);

    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, locale, TimeZone.getTimeZone("UTC"), properties);
    verify(messageContext, never()).addMessage(any(MessageResolver.class));
  }

  @Test
  public void registerWithAutomaticActivation() {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);
    when(settings.getBoolean("elastic.automatic.user.activation", false)).thenReturn(true);
    when(registrationService.activateRegistration(nullable(String.class), nullable(ModerationType.class))).thenReturn(true);

    registrationHelper.initialize();
    boolean isRegistered = registrationHelper.register(registration, requestContext, null);

    assertTrue(isRegistered);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, Locale.ENGLISH, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(registrationService).activateRegistration(nullable(String.class), nullable(ModerationType.class));
    verify(messageContext, atLeastOnce()).addMessage(message(ACTIVATE_REGISTRATION_SUCCESS));
  }  
  
  @Test
  public void testRegisterWithBlobException() {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);
    when(file.getSize()).thenReturn(1000L);
    when(blobService.put(inputStream, CONTENT_TYPE, null)).thenThrow(new BlobException("I/O error"));

    boolean userSaved = registrationHelper.register(registration, requestContext, file);
    assertTrue(userSaved);

    verify(communityUser, never()).save();
    verify(blobService).put(inputStream, CONTENT_TYPE, null);
    verify(communityUser, never()).setImage(blob);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testRegisterWithIOException() throws IOException {
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);
    when(file.getSize()).thenReturn(1000L);
    when(file.getInputStream()).thenThrow(new IOException("I/O error"));

    boolean userSaved = registrationHelper.register(registration, requestContext, file);
    assertTrue(userSaved);

    verify(communityUser, never()).save();
    verify(blobService, never()).put(inputStream, CONTENT_TYPE, null);
    verify(communityUser, never()).setImage(blob);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testRegisterWithImageTooBig() throws IOException {
    when(file.getSize()).thenReturn(512001L);
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);

    boolean userSaved = registrationHelper.register(registration, requestContext, file);
    assertTrue(userSaved);

    verify(communityUser, never()).save();
    verify(blobService, never()).put(inputStream, CONTENT_TYPE, null);
    verify(communityUser, never()).setImage(blob);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }


  @Test
  public void testRegisterWithImageUnsupportedContentType() throws IOException {
    when(file.getSize()).thenReturn(512000L);
    when(file.getContentType()).thenReturn("abcd");
    when(registrationService.register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES)).thenReturn(communityUser);

    boolean userSaved = registrationHelper.register(registration, requestContext, file);
    assertTrue(userSaved);

    verify(communityUser, never()).save();
    verify(blobService, never()).put(inputStream, CONTENT_TYPE, null);
    verify(communityUser, never()).setImage(blob);
    verify(registrationService).register(USERNAME, PASSWORD, EMAIL, LOCALE, TimeZone.getTimeZone("UTC"), USER_PROPERTIES);
    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void activateModerationTypeNone() {
    ModerationType moderationType = ModerationType.NONE;
    when(registrationService.activateRegistration(ACTIVATION_KEY, moderationType)).thenReturn(true);
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(elasticSocialConfiguration.getUserModerationType()).thenReturn(moderationType);
    when(request.getAttribute("cmpage")).thenReturn(page);
    when(requestContext.getExternalContext().getNativeRequest()).thenReturn(request);

    boolean isActivated = registrationHelper.activate(ACTIVATION_KEY, requestContext);
    assertTrue(isActivated);

    verify(messageContext, atLeastOnce()).addMessage(message(ACTIVATE_REGISTRATION_SUCCESS));
  }

  @Test
  public void activateModerationTypePost() {
    ModerationType moderationType = ModerationType.POST_MODERATION;
    when(registrationService.activateRegistration(ACTIVATION_KEY, moderationType)).thenReturn(true);
    when(elasticSocialConfiguration.getUserModerationType()).thenReturn(moderationType);

    boolean isActivated = registrationHelper.activate(ACTIVATION_KEY, requestContext);
    assertTrue(isActivated);

    verify(messageContext, atLeastOnce()).addMessage(message(ACTIVATE_REGISTRATION_SUCCESS));
  }

  @Test
  public void activateModerationTypePre() {
    ModerationType moderationType = ModerationType.PRE_MODERATION;
    when(registrationService.activateRegistration(ACTIVATION_KEY, moderationType)).thenReturn(true);
    when(elasticSocialConfiguration.getUserModerationType()).thenReturn(moderationType);

    boolean isActivated = registrationHelper.activate(ACTIVATION_KEY, requestContext);
    assertTrue(isActivated);

    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @Test
  public void activateNotSuccessful() {
    ModerationType moderationType = ModerationType.PRE_MODERATION;
    when(registrationService.activateRegistration(ACTIVATION_KEY, moderationType)).thenReturn(false);
    when(elasticSocialConfiguration.getUserModerationType()).thenReturn(moderationType);

    boolean isActivated = registrationHelper.activate(ACTIVATION_KEY, requestContext);
    assertFalse(isActivated);

    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void activateTokenExpired() {
    ModerationType moderationType = ModerationType.PRE_MODERATION;
    when(registrationService.activateRegistration(ACTIVATION_KEY, moderationType)).thenThrow(new TokenExpiredException(""));

    when(elasticSocialConfiguration.getUserModerationType()).thenReturn(moderationType);
    when(request.getAttribute("cmpage")).thenReturn(page);
    when(requestContext.getExternalContext().getNativeRequest()).thenReturn(request);

    boolean isActivated = registrationHelper.activate(ACTIVATION_KEY, requestContext);
    assertFalse(isActivated);

    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @Test
  public void redirectLoggedInUserToHomePage() {
    UserContext.setUser(communityUser);
    registrationHelper.redirectLoggedInUserToHomePage(requestContext);

    verify(externalContext).requestExternalRedirect("contextRelative:");
  }

  @Test
  public void redirectLoggedInUserToHomePageNoUser() {
    UserContext.clear();
    registrationHelper.redirectLoggedInUserToHomePage(requestContext);

    verify(externalContext, never()).requestExternalRedirect("contextRelative:");
  }

  @Test
  public void preProcess() {
    Registration registration = initRegistration();

    registrationHelper.preProcess(registration, requestContext);

    verify(communityUserService).getUserByEmail(EMAIL);
    verify(communityUserService).getUserByName(USERNAME);
    verify(messageContext, never()).addMessage(any());
    assertTrue(registration.isRegisteringWithProvider());
  }

  @Test
  public void preProcessNameAndEmailAlreadyUsed() {
    when(communityUserService.getUserByEmail(EMAIL)).thenReturn(communityUser);
    when(communityUserService.getUserByName(USERNAME)).thenReturn(communityUser);

    registrationHelper.preProcess(initRegistration(), requestContext);
    verify(communityUserService).getUserByEmail(EMAIL);
    verify(communityUserService).getUserByName(USERNAME);
    verify(messageContext, times(2)).addMessage(any());
  }

  @Test
  public void preProcessNameAndEmailBlank() {
    when(profile.getEmail()).thenReturn("");
    when(profile.getUsername()).thenReturn(null);

    registrationHelper.preProcess(initRegistration(), requestContext);
    verify(communityUserService, never()).getUserByEmail(anyString());
    verify(communityUserService, never()).getUserByName(anyString());
    verify(messageContext, never()).addMessage(any());
  }

  @Test
  public void preProcessConnectionNull() {
    when(providerSignInAttempt.getConnection(any(ConnectionFactoryLocator.class))).thenReturn(null);

    registrationHelper.preProcess(initRegistration(), requestContext);
    verify(communityUserService, never()).getUserByEmail(EMAIL);
    verify(communityUserService, never()).getUserByName(USERNAME);
    verify(messageContext, never()).addMessage(any());
  }

  @Test
  public void testPostProcessProviderConnectionDuplicateConnection() {
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getSessionMap()).thenReturn(sessionMap);
    when(sessionMap.remove("providerLogin.messageKey")).thenReturn("some.key");
    when(requestContext.getRequestParameters()).thenReturn(parameterMap);

    registrationHelper.postProcessProviderRegistration(requestContext);

    verify(messageContext).addMessage(any());
    verify(parameterMap).contains("error");
  }

  @Test
  public void testPostProcessProviderConnectionError() {
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getSessionMap()).thenReturn(sessionMap);
    when(requestContext.getRequestParameters()).thenReturn(parameterMap);
    when(parameterMap.contains("error")).thenReturn(true);

    registrationHelper.postProcessProviderRegistration(requestContext);

    verify(messageContext).addMessage(any());
    verify(parameterMap).contains("error");
  }

  private Registration initRegistration() {
    Registration registration = new Registration();
    registration.setUsername(USERNAME);
    registration.setGivenname(GIVENNAME);
    registration.setSurname(SURNAME);
    registration.setEmailAddress(EMAIL);
    registration.setPassword(PASSWORD);
    registration.setConfirmPassword(PASSWORD);
    registration.setTimeZoneId(TimeZone.getTimeZone("UTC").getID());
    registration.setPasswordPolicy(passwordPolicy);
    registration.setAcceptTermsOfUse(true);
    return registration;
  }

  private static MessageResolver message(final String code) {
    return argThat(argument -> argument instanceof DefaultMessageResolver && asList(((DefaultMessageResolver) argument).getCodes()).equals(asList(code)));
  }
}
