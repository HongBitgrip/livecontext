package com.coremedia.blueprint.common.datevalidation;

import com.google.common.base.Predicate;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This predicate is used to filter components by validFrom and validTo fields read from a map.
 */
public class DateValidationPredicate implements Predicate<Map<String, Calendar>> {
  /**
   * Logging
   */
  private static final Logger LOG = LoggerFactory.getLogger(DateValidationPredicate.class);

  /**
   * The key for the validFrom Date when stored in the Map
   */
  private static final String VALID_FROM = "validFrom";

  /**
   * The key for the validTo Date when stored in the Map
   */
  private static final String VALID_TO = "validTo";

  /**
   * This will contain the date and time to compare with.
   */
  private Calendar now = null;

  /**
   * Constructor.
   *
   * @param date the date to use for comparison
   */
  public DateValidationPredicate(Calendar date) {
    now = date;
  }

  /**
   * Factory method to create an object of two Calendars which is suitable as
   * argument for {@link #apply}.
   *
   * @param validFrom The validFrom date
   * @param validTo The validTo date
   * @return An interval representation of validFrom and validTo
   */
  public static Map<String, Calendar> createValidityDates(Calendar validFrom, Calendar validTo) {
    Map<String, Calendar> dates = new HashMap<>();
    if (validFrom!=null) {
      dates.put(VALID_FROM, validFrom);
    }
    if (validTo!=null) {
      dates.put(VALID_TO, validTo);
    }
    return dates;
  }

  /**
   * Returns true if the input object matches this predicate.
   *
   * @param validityDates The map including the validFrom and validTo date.
   * @return <tt>true</tt> if the input object matches this predicate, or if no reference date was provided.
   */
  @Override
  public boolean apply(Map<String, Calendar> validityDates) {
    if (now == null) {
      // no date to compare with...
      return true;
    } else {
      Calendar validFrom = validityDates.get(VALID_FROM);
      Calendar validTo = validityDates.get(VALID_TO);
      boolean result = (validFrom==null || validFrom.compareTo(now)<=0) && (validTo==null || validTo.compareTo(now)>=0);
      logDetails(validFrom, validTo, result);
      return result;
    }
  }

  private void logDetails(Calendar validFrom, Calendar validTo, boolean result) {
    if (LOG.isDebugEnabled()) {
      FastDateFormat dateFormat = FastDateFormat.getInstance("dd.MM.yyyy HH:mm:ss.SSS");
      String validFromStr = (validFrom == null) ? "*not set*" : dateFormat.format(validFrom.getTime());
      String validToStr = (validTo == null) ? "*not set*" : dateFormat.format(validTo.getTime());
      String nowStr = dateFormat.format(now);
      String isNotValid = result ? "" : "not ";
      LOG.debug("Object with validity dates 'validFrom: " + validFromStr + "' and 'validTo: " + validToStr + "' is " + isNotValid + " valid at " + nowStr);
    }
  }
}