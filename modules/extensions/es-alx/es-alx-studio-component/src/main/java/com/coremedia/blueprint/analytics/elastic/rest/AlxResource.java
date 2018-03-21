package com.coremedia.blueprint.analytics.elastic.rest;

import com.coremedia.blueprint.base.analytics.elastic.PageViewReportModelService;
import com.coremedia.blueprint.base.analytics.elastic.PageViewResult;
import com.coremedia.blueprint.base.analytics.elastic.PublicationReportModelService;
import com.coremedia.blueprint.base.analytics.elastic.ReportModel;
import com.coremedia.blueprint.base.analytics.elastic.util.RetrievalUtil;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.analytics.elastic.ReportModel.REPORT_DATE_FORMAT;
import static com.coremedia.blueprint.analytics.elastic.rest.AlxRestConstants.ALX_REST_PREFIX;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * A simple REST service for retrieving tracking information for a specific {@link Content}.
 */
@Named
@Produces(MediaType.APPLICATION_JSON)
@Path(AlxResource.PATH)
public class AlxResource {

  public static final String PATH = ALX_REST_PREFIX;

  private static final Logger LOG = LoggerFactory.getLogger(AlxResource.class);

  private static final String PARAM_ID = "id";
  public static final int DEFAULT_TIME_RANGE = 7;
  public static final String CMLINKABLE = "CMLinkable";
  public static final String PARAM_TIME_RANGE = "timeRange";
  private static final String GOOGLE_ALX = "googleAnalytics";

  @Inject
  private ContentRepository contentRepository;

  @Inject
  private SettingsService settingsService;

  @Inject
  private PageViewReportModelService pageViewReportModelService;

  @Inject
  @Named("publicationReportModelService")
  private PublicationReportModelService publicationReportModelService;

  @Inject
  @Qualifier("contentContextStrategy")
  private ContextStrategy<Content, Content> contextStrategy;

  @GET
  @Path("/pageviews/{id}")
  public ReportResult getAlxData(@PathParam(PARAM_ID) String id, @QueryParam(PARAM_TIME_RANGE) Integer timeRange) {
    int realTimeRange = timeRange == null || timeRange < 1 ? DEFAULT_TIME_RANGE : timeRange;
    Content content = contentRepository.getContent(id);
    if (content != null) {
      if (content.getType().isSubtypeOf(CMLINKABLE)) {
        List<Content> channels = contextStrategy.findContextsFor(content);
        List<Content> contents = new ArrayList<>();
        contents.add(content);
        for (Content channel : channels) {
          if (!content.equals(channel)) {
            contents.add(channel);
          }
        }

        String analyticsProvider = settingsService.setting(RetrievalUtil.DOCUMENT_PROPERTY_ANALYTICS_PROVIDER, String.class, contents.toArray());
        if (StringUtils.isEmpty(analyticsProvider)) {
          analyticsProvider = GOOGLE_ALX;
        }

        PageViewResult result = pageViewReportModelService.getPageViewResult(content, analyticsProvider);
        if (result.getTimeStamp() == null) {
          return new ReportResult(Collections.<AlxData>emptyList(), null);
        }
        return new ReportResult(getDataForId(result.getReportModel(), realTimeRange),result.getTimeStamp());
      } else {
        LOG.info("Page views are only available for subtypes of CMLinkable, but the requested type for id={} is {}", id, content.getType());
      }
    }
    throw new WebApplicationException(NOT_FOUND);
  }

  @GET
  @Path("/publications/{id}")
  public ReportResult getPublicationData(@PathParam(PARAM_ID) String id, @QueryParam(PARAM_TIME_RANGE) Integer timeRange) {
    int realTimeRange = timeRange == null || timeRange < 1 ? DEFAULT_TIME_RANGE : timeRange;
    Content content = contentRepository.getContent(id);
    if (content != null) {
      ReportModel reportModel = publicationReportModelService.getReportModel(content);
      List<AlxData> publicationData = getPublicationDataForId(reportModel, realTimeRange);
      if (reportModel.getLastSaved() == 0) {
        return new ReportResult(Collections.<AlxData>emptyList(), null);
      }
      return new ReportResult(publicationData,
              reportModel.getLastSaved() > 0 ? new Date(reportModel.getLastSaved()) : null);
    }
    throw new WebApplicationException(NOT_FOUND);
  }

  private List<AlxData> getDataForId(ReportModel pageViewReportModel, int timeRange) {
    DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
    Date date = new Date();

    List<AlxData> result = new ArrayList<>(timeRange);
    for (int i = 0; i < timeRange; i++) {
      Long pageViews = getPageViewsForDate(pageViewReportModel, date);
      result.add(0, new AlxData(dateFormat.format(date), pageViews));
      date = DateUtils.addDays(date, -1);
    }
    return result;
  }

  private List<AlxData> getPublicationDataForId(ReportModel reportModel, int timeRange) {
    DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
    Date date = new Date();

    List<AlxData> result = new ArrayList<>(timeRange);
    Map<String, Long> reportMap = reportModel.getReportMap();
    for (int i = 0; i < timeRange; i++) {
      String dateString = dateFormat.format(date);
      Long publicationCount = reportMap.get(dateString);
      result.add(0, new AlxData(dateString, publicationCount));
      date = DateUtils.addDays(date, -1);
    }
    return result;
  }

  protected Long getPageViewsForDate(ReportModel reportModel, Date date) {
    DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
    // don't care whether we got an int, a long or nothing at all
    Map<String, Long> reportMap = reportModel.getReportMap();
    Number value = reportMap == null ? null : reportMap.get(dateFormat.format(date));
    return value != null ? value.longValue() : 0 ;
  }
}
