package com.coremedia.blueprint.common.datevalidation;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DateValidationPredicateTest {

  /**
   * Method: evaluate(Map<String, Calendar> validityDates)
   * True needs to be returned
   * In this case both dates (validFrom and validTo) will be null
   *
   * @throws Exception exception
   */
  @Test
  public void testNull() throws Exception {
    Calendar now = new GregorianCalendar();
    Calendar validFrom = null;
    Calendar validTo = null;

    Map<String, Calendar> dates = DateValidationPredicate.createValidityDates(validFrom, validTo);
    DateValidationPredicate predicate = new DateValidationPredicate(now);
    boolean result = predicate.apply(dates);
    Assert.assertTrue(result);
  }

  @Test
  public void testNowNull() {
    Map<String, Calendar> dates = new HashMap<>();
    DateValidationPredicate predicate = new DateValidationPredicate(null);
    boolean result = predicate.apply(dates);
    Assert.assertTrue(result);
  }

  /**
   * Method: evaluate(Map<String, Calendar> validityDates)
   * True needs to be returned
   * In this case now is in between validFrom and validTo
   *
   * @throws Exception exception
   */
  @Test
  public void testNowInBetween() throws Exception {
    Calendar now = new GregorianCalendar();
    Calendar validFrom = new GregorianCalendar();
    validFrom.set(Calendar.YEAR, validFrom.get(Calendar.YEAR) - 1);
    Calendar validTo = new GregorianCalendar();
    validTo.set(Calendar.YEAR, validTo.get(Calendar.YEAR) + 1);
    Assert.assertTrue(validFrom.before(now));
    Assert.assertTrue(validTo.after(now));

    Map<String, Calendar> dates = DateValidationPredicate.createValidityDates(validFrom, validTo);
    DateValidationPredicate predicate = new DateValidationPredicate(now);
    boolean result = predicate.apply(dates);
    Assert.assertTrue(result);
  }

  /**
   * Method: evaluate(Map<String, Calendar> validityDates)
   * True needs to be returned
   * In this case now is in between validFrom and validTo
   *
   * @throws Exception exception
   */
  @Test
  public void testDatesNotSet() throws Exception {
    Calendar now = new GregorianCalendar();

    Map<String, Calendar> dates = new HashMap<>();
    DateValidationPredicate predicate = new DateValidationPredicate(now);
    boolean result = predicate.apply(dates);
    Assert.assertTrue(result);
  }


  /**
   * Method: evaluate(Map<String, Calendar> validityDates)
   * False needs to be returned
   * In this case both dates (validFrom and validTo) will be before now
   *
   * @throws Exception exception
   */
  @Test
  public void testBothBefore() throws Exception {
    Calendar now = new GregorianCalendar();
    Calendar validFrom = new GregorianCalendar();
    validFrom.set(Calendar.YEAR, validFrom.get(Calendar.YEAR) - 2);
    Calendar validTo = new GregorianCalendar();
    validTo.set(Calendar.YEAR, validTo.get(Calendar.YEAR) - 1);
    Assert.assertTrue(validFrom.before(validTo));
    Assert.assertTrue(validTo.before(now));

    Map<String, Calendar> dates = DateValidationPredicate.createValidityDates(validFrom, validTo);
    DateValidationPredicate predicate = new DateValidationPredicate(now);
    boolean result = predicate.apply(dates);
    Assert.assertFalse(result);
  }

  /**
   * Method: evaluate(Map<String, Calendar> validityDates)
   * False needs to be returned
   * In this case both dates (validFrom and validTo) will be after now
   *
   * @throws Exception exception
   */
  @Test
  public void testBothAfter() throws Exception {
    Calendar now = new GregorianCalendar();
    Calendar validFrom = new GregorianCalendar();
    validFrom.set(Calendar.YEAR, validFrom.get(Calendar.YEAR) + 1);
    Calendar validTo = new GregorianCalendar();
    validTo.set(Calendar.YEAR, validTo.get(Calendar.YEAR) + 2);
    Assert.assertTrue(now.before(validFrom));
    Assert.assertTrue(validFrom.before(validTo));

    Map<String, Calendar> dates = DateValidationPredicate.createValidityDates(validFrom, validTo);
    DateValidationPredicate predicate = new DateValidationPredicate(now);
    boolean result = predicate.apply(dates);
    Assert.assertFalse(result);
  }
}