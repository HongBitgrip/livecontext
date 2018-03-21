package com.coremedia.blueprint.common.datevalidation;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchFilterProvider;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.blueprint.cae.search.solr.SolrSearchFormatHelper;
import com.coremedia.blueprint.common.services.validation.AbstractValidator;
import com.coremedia.cache.Cache;
import com.coremedia.springframework.request.ContextAttributes;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * This validation provider to check if the Object is instance of {@link ValidityPeriod} and returns it if it is valid.
 */
public class ValidityPeriodValidator<T> extends AbstractValidator<T> implements SearchFilterProvider<Condition> {
  public static final String REQUEST_PARAMETER_PREVIEW_DATE = "previewDate";
  public static final String REQUEST_ATTRIBUTE_PREVIEW_DATE = "previewDateObj";
  public static final String UNCACHEABLE = "ValidityPeriodValidator#uncacheable";

  private static final int INTERVAL = 5;
  private static final Logger LOG = LoggerFactory.getLogger(ValidityPeriodValidator.class);

  private Cache cache;

  private boolean preview;

  // --- spring config -------------------------------------------------------------------------------------------------

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @org.springframework.beans.factory.annotation.Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  /**
   * This Validator validates CMLinkable instances, and any subclasses of CMLinkable too
   */
  @Override
  public boolean supports(Class clazz) {
    return ValidityPeriod.class.isAssignableFrom(clazz);
  }

  @Override
  protected Predicate createPredicate() {
    return new ValidationPeriodPredicate(getPreviewDate());
  }

  @Override
  protected void addCustomDependencies(List<? extends T> result) {

    if (validityPeriodIsUsed(result)) {
      if (preview) {
        // we don't want to cache anything in preview if somewhere a validation period is used
        // (the reason is: it could influence the validity decision at any time if later a previewdate is used)
        Cache.dependencyOn(UNCACHEABLE);
        cache.invalidate(UNCACHEABLE);
      }
      else {
        Calendar validUntil = findNearestDate(result);
        if (validUntil != null) {
          // set a timed dependency if a date is available (the minimal time that the list could be cached)
          Cache.cacheUntil(validUntil.getTime());
          if (LOG.isDebugEnabled()) {
            FastDateFormat dateFormat = FastDateFormat.getInstance("dd.MM.yyyy HH:mm:ss.SSS");
            String chosenDateStr = dateFormat.format(validUntil.getTime());
            LOG.debug("Caching these items: '{}' until '{}'", result, chosenDateStr);
          }
        }
      }
    }
  }

  private boolean validityPeriodIsUsed(List<? extends T> result) {
    for (T t : result) {
      if (t instanceof ValidityPeriod) {
        ValidityPeriod vp = (ValidityPeriod) t;
        if (vp.getValidFrom() != null || vp.getValidTo() != null) {
          return true;
        }
      }
    }
    return false;
  }

  private Calendar findNearestDate(List<? extends T> allItems) {
    Calendar validTime = getPreviewDate();
    List<Calendar> dates = new ArrayList<>(Lists.transform(Lists.newArrayList(Iterables.filter(allItems, ValidityPeriod.class)),
            new ContentToEarliestValidationDateFunction(validTime)));
    LOG.debug("Searching the nearest date for these items: {}", allItems);
    if (!dates.isEmpty()) {
      // choose the earliest date from the list
      return Ordering.from(new CalendarComparator()).min(dates);
    }
    return null;
  }

  private Calendar getPreviewDate() {
    //is previewDate stored in the request attributes?
    Calendar previewDate = ContextAttributes.getRequestAttribute(REQUEST_ATTRIBUTE_PREVIEW_DATE, Calendar.class);
    if (previewDate == null) {
      //if not stored in the request attributes
      //retrieve the previewDate from the request parameter
      previewDate = getPreviewDateFromRequestParameter();
      // store previewDate in the request attributes for the following checks within this request
      setPreviewDate(previewDate);
    }
    return previewDate;
  }

  private static Calendar getPreviewDateFromRequestParameter() {
    Calendar calendar = Calendar.getInstance();
    String dateAsString = ContextAttributes.getRequestParameter(REQUEST_PARAMETER_PREVIEW_DATE);
    if (StringUtils.isNotEmpty(dateAsString) && dateAsString.length() > 0) {
      SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      sdb.setTimeZone(TimeZone.getTimeZone(dateAsString.substring(dateAsString.lastIndexOf(' ') + 1)));
      try {
        calendar.setTime(sdb.parse(dateAsString.substring(0, dateAsString.lastIndexOf(' '))));
      } catch (ParseException e) {
        LOG.warn("error parsing previewDate {}", dateAsString, e);
      }
    }
    return calendar;
  }

  private static void setPreviewDate(Calendar previewDate) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      requestAttributes.setAttribute(REQUEST_ATTRIBUTE_PREVIEW_DATE, previewDate, ServletRequestAttributes.SCOPE_REQUEST);
    }
  }

  @Override
  public List<Condition> getFilter(boolean isPreview) {
    Calendar date = isPreview ? getPreviewDate() : Calendar.getInstance();
    date = getDateRounded(date, INTERVAL);
    String formattedDate = SolrSearchFormatHelper.calendarToString(date);

    List<Condition> conditions = new ArrayList<>();

    // condition for valid from
    Condition validFrom = Condition.is("validfrom",
            Value.exactly(SolrSearchFormatHelper.fromPastToValue(formattedDate))
    );
    // condition for valid to
    Condition validTo = Condition.is("validto",
            Value.exactly(SolrSearchFormatHelper.fromValueIntoFuture(formattedDate))
    );

    conditions.add(validFrom);
    conditions.add(validTo);

    return conditions;
  }

  private Calendar getDateRounded(Calendar calendar, int interval) {
    Calendar result = (Calendar) calendar.clone();
    int minutes = result.get(Calendar.MINUTE);
    int mod = interval - (minutes % interval);
    if (mod == interval) {
      mod -= interval;
    }
    minutes += mod;
    result.set(Calendar.MINUTE, minutes);
    result.set(Calendar.SECOND, 0);
    result.set(Calendar.MILLISECOND, 0);
    return result;
  }
}
