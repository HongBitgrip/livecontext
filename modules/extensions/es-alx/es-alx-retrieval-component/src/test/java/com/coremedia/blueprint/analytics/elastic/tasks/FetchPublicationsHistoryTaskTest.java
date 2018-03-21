package com.coremedia.blueprint.analytics.elastic.tasks;

import com.coremedia.blueprint.base.analytics.elastic.PublicationReportModelService;
import com.coremedia.blueprint.base.analytics.elastic.ReportModel;
import com.coremedia.blueprint.base.analytics.elastic.util.DaysBack;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.elastic.tenant.TenantSiteMapping;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.Version;
import com.coremedia.cap.content.publication.PublicationService;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.xmlrepo.XmlCapConnectionFactory;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.analytics.elastic.ReportModel.REPORT_DATE_FORMAT;
import static com.coremedia.blueprint.analytics.elastic.tasks.FetchPublicationsHistoryTask.PUBLICATION_HISTORY_DOCUMENT_TYPE;
import static com.coremedia.blueprint.analytics.elastic.tasks.FetchPublicationsHistoryTask.PUBLICATION_HISTORY_DOCUMENT_TYPE_KEY;
import static com.coremedia.blueprint.analytics.elastic.tasks.FetchPublicationsHistoryTask.PUBLICATION_HISTORY_INTERVAL;
import static com.coremedia.blueprint.analytics.elastic.tasks.FetchPublicationsHistoryTask.PUBLICATION_HISTORY_INTERVAL_KEY;
import static com.coremedia.blueprint.analytics.elastic.tasks.PublicationsAggregator.QUERY_SERVICE_EXPRESSION_TEMPLATE;
import static com.coremedia.blueprint.base.analytics.elastic.util.DateUtil.getDateWithoutTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FetchPublicationsHistoryTaskTest {

  private static final String TENANT = "tenant";
  private static final String CONTENT_REPO = "classpath:/com/coremedia/testing/contenttest.xml";

  @Mock
  QueryService queryService;
  @Mock
  PublicationService publicationService;
  @Mock
  ReportModel reportModel;
  @Mock
  PublicationReportModelService publicationReportModelService;
  FetchPublicationsHistoryTask fetchPublicationsHistory;
  @Mock
  private Map<String, Long> reportMap;
  @Mock
  private SitesService sitesService;
  @Mock
  private Site site;
  @Mock
  private ContentSiteAspect contentSiteAspect;
  @Mock
  private Content siteFolder;
  @Mock
  private SettingsService settingsService;

  private Date referenceDate;
  private Date referenceDateAfter;
  private static final String REFERENCE_DATE_STRING = "20130601";
  private static final String REFERENCE_DATE_AFTER_STRING = "20130602";

  private Map<String, Long> assertReportMap = new HashMap<>();

  @Before
  public void setup() throws ParseException {
    referenceDate = new SimpleDateFormat(REPORT_DATE_FORMAT).parse(REFERENCE_DATE_STRING);
    referenceDateAfter = new SimpleDateFormat(REPORT_DATE_FORMAT).parse(REFERENCE_DATE_AFTER_STRING);

    XmlCapConnectionFactory factory = new XmlCapConnectionFactory();
    CapConnection connection = factory.prepare(Collections.singletonMap(com.coremedia.cap.undoc.Cap.CONTENT_XML, CONTENT_REPO));
    connection.open();

    final TenantService tenantService = mock(TenantService.class);
    when(tenantService.getCurrent()).thenReturn(TENANT);

    ContentRepository contentRepository = connection.getContentRepository();
    initQueryService(contentRepository);
    final ContentRepository repositoryProxy = createContentRepositoryProxy(contentRepository, publicationService);
    final TenantSiteMapping tenantSiteMapping = mock(TenantSiteMapping.class);
    when(tenantSiteMapping.getRootsForCurrentTenant()).thenReturn(Collections.singleton(contentRepository.getContent(IdHelper.formatContentId(12346))));

    fetchPublicationsHistory = new FetchPublicationsHistoryTask(repositoryProxy, sitesService, tenantSiteMapping, publicationReportModelService, settingsService);

    when(publicationReportModelService.getReportModel(any(Content.class))).thenReturn(reportModel);
    when(reportModel.getLastSaved()).thenReturn(referenceDate.getTime());

    when(publicationService.getPublicationDate(any(Version.class))).thenAnswer(new Answer<Calendar>() {
      @Override
      public Calendar answer(InvocationOnMock invocationOnMock) throws Throwable {
        return getPublicationDate((Version) invocationOnMock.getArguments()[0]);
      }
    });
    when(reportModel.getReportMap()).thenReturn(reportMap);

    when(settingsService.settingWithDefault(eq(PUBLICATION_HISTORY_DOCUMENT_TYPE_KEY), eq(String.class), eq(PUBLICATION_HISTORY_DOCUMENT_TYPE), any(Content.class))).thenReturn(PUBLICATION_HISTORY_DOCUMENT_TYPE);
    when(settingsService.settingWithDefault(eq(PUBLICATION_HISTORY_INTERVAL_KEY), eq(Integer.class), eq(PUBLICATION_HISTORY_INTERVAL), eq(Content.class))).thenReturn(0);
    when(settingsService.settingWithDefault(eq(PUBLICATION_HISTORY_INTERVAL_KEY), eq(Integer.class), eq(PUBLICATION_HISTORY_INTERVAL), any(Content.class))).thenReturn(PUBLICATION_HISTORY_INTERVAL);

    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);
    when(site.getSiteRootFolder()).thenReturn(siteFolder);
  }

  private Calendar getPublicationDate(Version version) {
    Calendar calendar = Calendar.getInstance();
    String simpleDate = REFERENCE_DATE_STRING;

    // first version on reference date, all other versions the day after
    if (IdHelper.parseVersionId(version.getId()) == 1) {
      calendar.setTime(referenceDate);
    } else {
      simpleDate = REFERENCE_DATE_AFTER_STRING;
      calendar.setTime(referenceDateAfter);
    }

    Long currentCount = assertReportMap.get(simpleDate);
    assertReportMap.put(simpleDate, currentCount == null ? 1L : currentCount + 1);

    return calendar;
  }

  private void initQueryService(final ContentRepository contentRepository) {
    when(queryService.poseVersionQuery(anyString(), any())).thenAnswer(new Answer<Collection<Version>>() {
      public Collection<Version> answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        final Collection<Content> contents = contentRepository.getQueryService().poseContentQuery((String) args[0], args[1], args[2]);
        final List<Version> versions = new ArrayList<>();
        for (Content content : contents) {
          versions.addAll(content.getVersions());
        }
        return versions;
      }
    });
  }

  private ContentRepository createContentRepositoryProxy(final ContentRepository contentRepository, final PublicationService publicationService1) {
    return (ContentRepository) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ContentRepository.class}, new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String methodName = method.getName();
        if ("getQueryService".equals(methodName)) {
          return queryService;
        } else if ("getPublicationService".equals(methodName)) {
          return publicationService1;
        }
        return method.invoke(contentRepository, args);
      }
    });
  }

  @Test
  public void getPublicationsTest() {
    when(reportModel.getSettings()).thenReturn(ImmutableMap.<String, Object>of(PUBLICATION_HISTORY_DOCUMENT_TYPE_KEY, PUBLICATION_HISTORY_DOCUMENT_TYPE));
    Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    startTime.setTime(referenceDate);

    fetchPublicationsHistory.run();

    verify(queryService).poseVersionQuery(eq(String.format(QUERY_SERVICE_EXPRESSION_TEMPLATE, PUBLICATION_HISTORY_DOCUMENT_TYPE)),
            eq(startTime), any(Content.class));
    verify(reportModel).setReportMap(assertReportMap);
    verify(reportModel).save();
  }

  @Test
  public void getPublicationsInitially() {
    when(reportModel.getLastSaved()).thenReturn(0L);
    Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Date startDate = getDateWithoutTime(new DaysBack(30).getStartDate());
    startTime.setTime(startDate);

    fetchPublicationsHistory.run();

    verify(queryService).poseVersionQuery(eq(String.format(QUERY_SERVICE_EXPRESSION_TEMPLATE, PUBLICATION_HISTORY_DOCUMENT_TYPE)),
            eq(startTime), any(Content.class));

    verify(reportModel).setReportMap(assertReportMap);
    verify(reportModel).setLastSaved(anyLong());
    verify(reportModel).setLastSavedDate(any(Date.class));
    verify(reportModel).save();
  }

  @Test
  public void getPublicationsInitiallyWithDifferentDocumentType() {

    String documentType = "CMArticle";
    when(settingsService.settingWithDefault(eq(PUBLICATION_HISTORY_DOCUMENT_TYPE_KEY), eq(String.class), eq(PUBLICATION_HISTORY_DOCUMENT_TYPE), any(Content.class))).thenReturn(documentType);
    when(reportModel.getLastSaved()).thenReturn(System.currentTimeMillis());
    when(reportModel.getSettings()).thenReturn(ImmutableMap.<String, Object>of(PUBLICATION_HISTORY_DOCUMENT_TYPE_KEY, PUBLICATION_HISTORY_DOCUMENT_TYPE));

    Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    Date startDate = getDateWithoutTime(new DaysBack(30).getStartDate());
    startTime.setTime(startDate);

    fetchPublicationsHistory.run();

    verify(queryService).poseVersionQuery(eq(String.format(QUERY_SERVICE_EXPRESSION_TEMPLATE, documentType)),
            eq(startTime), any(Content.class));

    verify(reportModel).setReportMap(assertReportMap);
    verify(reportModel).setLastSaved(anyLong());
    verify(reportModel).setLastSavedDate(any(Date.class));
    verify(reportModel).save();
  }


  @Test
  public void getPublicationsNotNecessary() {
    when(reportModel.getLastSaved()).thenReturn(System.currentTimeMillis());

    fetchPublicationsHistory.run();

    verify(queryService, never()).poseVersionQuery(anyString(), anyString(), any(Calendar.class), any(Content.class));
  }
}
