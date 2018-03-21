package com.coremedia.blueprint.common.datevalidation;

import com.google.common.base.Predicate;

import java.util.Calendar;
import java.util.Map;

/**
 * This predicate is used to filter contents by validFrom and validTo fields read from a map.
 * it will delegate to DateValidationPredicate
 */
class ValidationPeriodPredicate implements Predicate<ValidityPeriod> {
  /**
   * This will contain the date and time to compare with.
   */
  private DateValidationPredicate predicate = null;

  /**
   * Constructor.
   *
   * @param date the date to use for comparison
   */
  public ValidationPeriodPredicate(Calendar date) {
    super();
    predicate = new DateValidationPredicate(date);
  }

  /**
   * Returns true if the input object matches this predicate.
   *
   * @param item The object to check
   * @return <tt>true</tt> if the input object matches this predicate, or if no reference date was provided.
   */
  @Override
  public boolean apply(ValidityPeriod item) {
    if (item == null) {
      return false;
    }
    Map<String, Calendar> dates = DateValidationPredicate.createValidityDates(item.getValidFrom(), item.getValidTo());
    return predicate.apply(dates);
  }
}
