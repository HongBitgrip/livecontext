package com.coremedia.blueprint.common.datevalidation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ValidationPeriodPredicateTest {
  CMLinkable linkable;

  @Before
  public void setUp() throws Exception {
    Calendar validFrom = Calendar.getInstance();
    validFrom.set(Calendar.YEAR, 2010);
    validFrom.set(Calendar.MONTH, Calendar.JANUARY);
    validFrom.set(Calendar.DAY_OF_MONTH, 1);

    linkable = Mockito.mock(CMLinkable.class);
    Mockito.when(linkable.getValidFrom()).thenReturn(validFrom);
    Calendar validTo = (Calendar) validFrom.clone();
    validTo.set(Calendar.YEAR, 2011);
    Mockito.when(linkable.getValidTo()).thenReturn(validTo);
  }

  /**
   * Method: evaluate(Content content)
   *
   * @throws Exception exception
   */
  @Test
  public void testEvaluate() throws Exception {
    Calendar now = GregorianCalendar.getInstance();
    now.set(Calendar.YEAR, 2010);
    now.set(Calendar.MONTH, Calendar.MARCH);
    //validFrom: 01.01.2010
    //validTo: 01.01.2011
    ValidationPeriodPredicate predicate = new ValidationPeriodPredicate(now);
    Assert.assertTrue(predicate.apply(linkable));
  }
}
