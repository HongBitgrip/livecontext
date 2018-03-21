package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.web.binding.IETFBCP47LanguageTagToLocaleConverter;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = LoginStatusHandlerTest.LocalConfig.class)
public class LoginStatusHandlerTest {

  private static final String STORE_ID = "4711";
  private static final Locale LOCALE = Locale.US;

  @Inject
  private WebApplicationContext wac;

  @Inject
  private UserSessionService userSessionService;

  @Inject
  private LiveContextSiteResolver liveContextSiteResolver;

  @Inject
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Inject
  private LinkFormatter linkFormatter;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    assertEquals("Test started with CurrentCommerceConnection set. This is probably because another different test " +
                 "run before and did not clean up properly.",
                 Optional.empty(), CurrentCommerceConnection.find());


    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @After
  public void tearDown() throws Exception {
    assertEquals(Optional.empty(), CurrentCommerceConnection.find());
  }

  @Test
  public void testStatusSiteNotFound() throws Exception {
    loginStatus(STORE_ID, LOCALE.toLanguageTag()).andExpect(status().isNotFound());
  }

  @Test
  public void testStatusConnectionNotFound() throws Exception {
    mockSite(STORE_ID, LOCALE.toLanguageTag());
    loginStatus(STORE_ID, LOCALE.toLanguageTag()).andExpect(status().isNotFound());
  }

  @Test
  public void testStatusNotLoggedIn() throws Exception {
    Site site = mockSite(STORE_ID, LOCALE.toLanguageTag());
    mockConnection(site);
    loginStatus(STORE_ID, LOCALE.toLanguageTag())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("{\"loggedIn\":false}"));
  }

  @Test
  public void testStatusLoggedIn() throws Exception {
    Site site = mockSite(STORE_ID, LOCALE.toLanguageTag());
    CommerceConnection connection = mockConnection(site);
    mockIsLoggedIn(connection);
    loginStatus(STORE_ID, LOCALE.toLanguageTag())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("{\"loggedIn\":true}"));
  }

  @Test
  public void testLinkStatus() {
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();
    CommerceConnection commerceConnection = mock(CommerceConnection.class);
    StoreContext storeContext = mock(StoreContext.class);
    when(commerceConnection.getStoreContext()).thenReturn(storeContext);
    when(storeContext.getStoreId()).thenReturn(STORE_ID);
    when(storeContext.getLocale()).thenReturn(LOCALE);

    CurrentCommerceConnection.set(commerceConnection);
    try {
      assertEquals("/dynamic/loginstatus?storeId=" + STORE_ID + "&locale=" + LOCALE.toLanguageTag(),
                   linkFormatter.formatLink(LoginStatusHandler.LinkType.STATUS, null, request, response, false));
    }
    finally {
      CurrentCommerceConnection.remove();
    }
  }

  private Site mockSite(String storeId, String locale) {
    Site site = mock(Site.class);
    when(liveContextSiteResolver.findSiteFor(storeId, Locale.forLanguageTag(locale))).thenReturn(site);
    return site;
  }

  private CommerceConnection mockConnection(Site site) {
    CommerceConnection connection = mock(CommerceConnection.class);
    when(connection.getUserContextProvider()).thenReturn(mock(UserContextProvider.class));
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(connection));
    return connection;
  }

  /** mock logged in user if the given connection is the current connection */
  private void mockIsLoggedIn(CommerceConnection connection) throws CredentialExpiredException {
    when(userSessionService.isLoggedIn()).then(invocation -> isCurrentConnection(connection));
  }

  private static boolean isCurrentConnection(@Nonnull CommerceConnection connection) {
    return CurrentCommerceConnection.find().map(connection::equals).orElse(false);
  }

  private ResultActions loginStatus(String storeId, String locale) throws Exception {
    return mockMvc.perform(get("/dynamic/loginstatus")
                                   .accept("application/json")
                                   .param("storeId", storeId)
                                   .param("locale", locale)
    );
  }

  @Configuration
  @EnableWebMvc
  @ImportResource(locations = {"classpath:/com/coremedia/cae/link-services.xml"},
                  reader = ResourceAwareXmlBeanDefinitionReader.class)
  static class LocalConfig {

    @Inject
    private GenericConversionService conversionService;

    @Bean
    LoginStatusHandler loginStatusHandler() {
      return new LoginStatusHandler(userSessionService(), liveContextSiteResolver(), commerceConnectionInitializer());
    }

    @Bean
    UserSessionService userSessionService() {
      return mock(UserSessionService.class);
    }

    @Bean
    LiveContextSiteResolver liveContextSiteResolver() {
      return mock(LiveContextSiteResolver.class);
    }

    @Bean
    CommerceConnectionInitializer commerceConnectionInitializer() {
      return mock(CommerceConnectionInitializer.class);
    }

    @PostConstruct
    void registerLocaleConverter() {
      // our Blueprint uses a custom converter for Locale objects, which uses language tags as string representation,
      // for example it uses "en-US" instead of "en_US" for Locale.US
      conversionService.addConverter(new IETFBCP47LanguageTagToLocaleConverter());
    }
  }
}