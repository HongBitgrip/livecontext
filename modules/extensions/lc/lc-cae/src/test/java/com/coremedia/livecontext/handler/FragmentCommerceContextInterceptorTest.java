package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.ContextBuilder;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FragmentCommerceContextInterceptorTest {

  private static final String REQUEST_PATH_INFO = "/anyShop";

  @Spy
  @InjectMocks
  private FragmentCommerceContextInterceptor testling;

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private BaseCommerceConnection commerceConnection;

  @Mock
  private ContractService contractService;

  @SuppressWarnings("unused")
  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private UserContextProvider userContextProvider;

  private MockHttpServletRequest request;
  private StoreContext storeContext;
  private UserContext userContext;

  @Before
  public void setup() {
    storeContext = newStoreContext();
    userContext = UserContext.builder().build();

    commerceConnection = new BaseCommerceConnection();
    commerceConnection.setVendorName("IBM");
    commerceConnection.setStoreContextProvider(storeContextProvider);
    commerceConnection.setUserContextProvider(userContextProvider);
    commerceConnection.setStoreContext(storeContext);
    commerceConnection.setContractService(contractService);

    when(storeContextProvider.buildContext(any())).thenReturn(new StoreContextBuilderImpl().from(storeContext));
    when(userContextProvider.createContext(any())).thenReturn(userContext);
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(commerceConnection));

    runTestlingInPreviewMode(false);

    FragmentContext fragmentContext = new FragmentContext();
    fragmentContext.setFragmentRequest(true);
    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;catalogId=catalog";
    FragmentParameters fragmentParameters = FragmentParametersFactory.create(url);
    fragmentContext.setParameters(fragmentParameters);

    request = new MockHttpServletRequest("GET", "test/params;placement=header");
    request.setPathInfo(REQUEST_PATH_INFO);
  }

  private void configureFragmentContext(@Nonnull Context fragmentContext) {
    try {
      new FragmentContextProvider().doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
    } catch (IOException | ServletException e) {
      throw new IllegalStateException(e);
    }
  }

  private void runTestlingInPreviewMode(boolean previewMode) {
    testling.setPreview(previewMode);
    doReturn(previewMode).when(testling).isStudioPreviewRequest(request);
  }

  @Test
  public void testInitUserContextProvider() {
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initUserContext(commerceConnection, request);

    UserContext userContext = commerceConnection.getUserContext();
    assertThat(userContext.getUserId()).isEqualTo("userId");
    assertThat(userContext.getUserName()).isEqualTo("loginId");
  }

  @Test
  public void testInitStoreContextWithContractIds() {
    runTestlingInPreviewMode(true);

    Contract contract1 = mock(Contract.class);
    Contract contract2 = mock(Contract.class);
    when(contract1.getExternalTechId()).thenReturn("contract1");
    when(contract2.getExternalTechId()).thenReturn("contract2");

    Collection<Contract> contracts = newArrayList(contract1, contract2);
    when(contractService.findContractIdsForUser(any(UserContext.class), any(StoreContext.class), nullable(String.class)))
            .thenReturn(contracts);

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    fragmentContext.put("wc.preview.contractIds", "contract1 contract2");
    LiveContextContextHelper.setContext(request, fragmentContext);

    configureFragmentContext(fragmentContext);

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    testling.initUserContext(commerceConnection, request);

    assertThat(connection).isPresent();

    StoreContext configuredStoreContext = connection.map(CommerceConnection::getStoreContext).orElse(null);
    assertThat(configuredStoreContext).isNotNull();

    String[] contractIds = configuredStoreContext.getContractIds();
    List<String> storeContextList = Arrays.asList(contractIds);
    assertThat(storeContextList).containsExactlyInAnyOrder("contract1", "contract2");
  }

  @Test
  public void testInitStoreContextWithContractIdsButDisabledProcessing() throws IOException, ServletException {
    runTestlingInPreviewMode(true);

    testling.setContractsProcessingEnabled(false);

    Contract contract1 = mock(Contract.class);
    Contract contract2 = mock(Contract.class);

    Collection<Contract> contracts = newArrayList(contract1, contract2);

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    fragmentContext.put("wc.preview.contractIds", "contract1 contract2");
    configureFragmentContext(fragmentContext);

    testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    testling.initUserContext(commerceConnection, request);
    String[] contractIdsInStoreContext = storeContext.getContractIds();
    assertThat(contractIdsInStoreContext).isNull();
  }

  @Test
  public void testInitStoreContextProviderInPreview() {
    runTestlingInPreviewMode(true);

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "Europe/Berlin");
    fragmentContext.put("wc.preview.workspaceId", "4711");
    LiveContextContextHelper.setContext(request, fragmentContext);
    configureFragmentContext(fragmentContext);

    Optional<CommerceConnection> commerceConnection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(commerceConnection).isPresent();

    StoreContext storeContext = commerceConnection.get().getStoreContext();
    assertThat(storeContext).isNotNull();

    assertThat(storeContext.getUserSegments()).isEqualTo("memberGroup1, memberGroup2");
    assertThat(storeContext.getWorkspaceId()).isEqualTo("4711");

    String previewDate = storeContext.getPreviewDate();
    assertThat(previewDate).isEqualTo("02-07-2014 17:57 Europe/Berlin");

    Calendar calendar = parsePreviewDateIntoCalendar(previewDate);
    SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    assertThat(previewDate).isEqualTo(sdb.format(calendar.getTime()) + " Europe/Berlin");
    assertThat(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE)).isEqualTo(calendar);
  }

  @Test
  public void testInitStoreContextProviderWithTimeShift() {
    runTestlingInPreviewMode(true);

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "US/Pacific");
    LiveContextContextHelper.setContext(request, fragmentContext);
    configureFragmentContext(fragmentContext);

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).isPresent();

    StoreContext configuredStoreContext = connection.map(CommerceConnection::getStoreContext).orElse(null);
    assertThat(configuredStoreContext).isNotNull();

    String previewDate = configuredStoreContext.getPreviewDate();
    assertThat(previewDate).isEqualTo("02-07-2014 17:57 US/Pacific");

    Calendar calendar = parsePreviewDateIntoCalendar(previewDate);
    String requestParam = FragmentCommerceContextInterceptor.convertToPreviewDateRequestParameterFormat(calendar);
    assertThat(previewDate).isEqualTo(requestParam);
    assertThat(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE)).isEqualTo(calendar);
  }

  @Test
  public void testConvertPreviewDate() {
    runTestlingInPreviewMode(true);

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "Europe/Berlin");
    fragmentContext.put("wc.preview.workspaceId", "4711");

    LiveContextContextHelper.setContext(request, fragmentContext);
    configureFragmentContext(fragmentContext);

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).isPresent();

    StoreContext storeContext = connection.get().getStoreContext();
    assertThat(storeContext).isNotNull();

    assertThat(storeContext.getUserSegments()).isEqualTo("memberGroup1, memberGroup2");
    assertThat(storeContext.getWorkspaceId()).isEqualTo("4711");

    SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    String previewDate = storeContext.getPreviewDate();
    assertThat(previewDate).isEqualTo("02-07-2014 17:57 Europe/Berlin");

    Calendar calendar = parsePreviewDateIntoCalendar(previewDate);
    assertThat(previewDate).isEqualTo(sdb.format(calendar.getTime()) + " Europe/Berlin");
  }

  @Test
  public void testInitStoreContextProviderInLive() {
    Context fragmentContext = ContextBuilder.create().build();
    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.workspaceId", "4711");
    configureFragmentContext(fragmentContext);

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).isPresent();

    StoreContext storeContext = connection.get().getStoreContext();
    assertThat(storeContext).isNotNull();

    assertThat(storeContext.getUserSegments()).isNull();
    assertThat(storeContext.getPreviewDate()).isNull();
    assertThat(storeContext.getWorkspaceId()).isNull();
    assertThat(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE)).isNull();
  }

  @Nonnull
  private static Calendar parsePreviewDateIntoCalendar(@Nonnull String previewDate) {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    try {
      calendar.setTime(sdf.parse(previewDate.substring(0, previewDate.lastIndexOf(' '))));
      calendar.setTimeZone(TimeZone.getTimeZone(previewDate.substring(previewDate.lastIndexOf(' ') + 1)));
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }

    return calendar;
  }
}
