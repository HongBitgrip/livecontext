package com.coremedia.blueprint.common.datevalidation;

import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * <p>
 * This function is able to find the earliest date after a reference date from the {@code validTo} and
 * {@code validFrom} properties of a content.
 * </p>
 */
class ContentToEarliestValidationDateFunction implements Function<ValidityPeriod, Calendar> {
  /**
   * this will contain the date to use for comparison
   */
  private Calendar now = null;

  /**
   * The logger
   */
  private static final Logger LOG = LoggerFactory.getLogger(ContentToEarliestValidationDateFunction.class);

  /**
   * Constructor
   *
   * @param now a reference date to compare to
   */
  public ContentToEarliestValidationDateFunction(Calendar now) {
    this.now = now;
  }

  /**
   * Transforming the incoming content into its earliest validFromdate agter the provided date {@link #now}
   *
   * @param validityPeriod a {@link com.coremedia.cap.content.Content} to transfrom
   * @return a {@link java.util.Calendar} object, or <tt>null</tt> if no valid date could be determined
   */
  @Override
  public Calendar apply(ValidityPeriod validityPeriod) {
    if (now == null) {
      //no date to compare with...
      return null;
    } else {
      Calendar chosenDate = calculateDateForPeriod(validityPeriod);
      if (LOG.isDebugEnabled()) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        String chosenDateStr = (chosenDate != null) ? dateFormat.format(chosenDate.getTime()) : "*null*";
        LOG.debug("ComponentToEarliestValidationDateTransformer.chosenDate: " + chosenDateStr);
      }
      return chosenDate;
    }
  }

  private Calendar calculateDateForPeriod(ValidityPeriod validityPeriod) {
    Calendar chosenDate = null;
    Calendar validFrom = validityPeriod.getValidFrom();
    Calendar validTo = validityPeriod.getValidTo();
    // now compare the two dates with each other and the almighty comparison date "now"
    if ((validFrom != null) && (validTo == null) && (validFrom.after(now))) {
      chosenDate = (Calendar) validFrom.clone();
    } else if ((validFrom == null) && (validTo != null) && (validTo.after(now))) {
      chosenDate = (Calendar) validTo.clone();
    } else if ((validFrom != null) && (validTo != null)) {
      chosenDate = retrieveDate(validFrom, validTo);
    }
    return chosenDate;
  }

  private Calendar retrieveDate(Calendar validFrom, Calendar validTo) {
    if (validFrom.after(now) && validTo.after(now)) {
      return validFrom.before(validTo) ? (Calendar) validFrom.clone() : (Calendar) validTo.clone();
    } else if (validFrom.after(now) && !validTo.after(now)) {
      return (Calendar) validFrom.clone();
    } else if (!validFrom.after(now) && validTo.after(now)) {
      return (Calendar) validTo.clone();
    }
    return null;
  }
}
