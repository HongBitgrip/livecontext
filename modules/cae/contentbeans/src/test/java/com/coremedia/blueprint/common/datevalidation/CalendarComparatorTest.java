package com.coremedia.blueprint.common.datevalidation;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarComparatorTest {

  @Test
  public void testMin() throws Exception {
    Calendar now = GregorianCalendar.getInstance();
    now.set(Calendar.YEAR, 2010);
    Calendar future = GregorianCalendar.getInstance();
    future.set(Calendar.YEAR, 2012);
    List<Calendar> cals = new ArrayList<>();
    cals.add(now);
    cals.add(future);
    Assert.assertEquals(now, Collections.min(cals, new CalendarComparator()));
  }

  @Test
  public void testDates() throws Exception {
    Calendar now = GregorianCalendar.getInstance();
    now.set(Calendar.YEAR, 2010);
    now.set(Calendar.MONTH, 10);
    now.set(Calendar.DATE, 10);
    now.set(Calendar.HOUR_OF_DAY, 10);
    now.set(Calendar.MINUTE, 10);
    now.set(Calendar.SECOND, 9);
    Calendar future = GregorianCalendar.getInstance();
    future.set(Calendar.YEAR, 2012);
    future.set(Calendar.MONTH, 10);
    future.set(Calendar.DATE, 10);
    future.set(Calendar.HOUR_OF_DAY, 10);
    future.set(Calendar.MINUTE, 10);
    future.set(Calendar.SECOND, 10);

    CalendarComparator comp = new CalendarComparator();
    Assert.assertEquals(-1, comp.compare(now, null));
    Assert.assertEquals(-1, comp.compare(now, future));
    Assert.assertEquals(1, comp.compare(future, now));
    Assert.assertEquals(1, comp.compare(null, future));
    Assert.assertEquals(0, comp.compare(future, future));
    Assert.assertEquals(0, comp.compare(null, null));
  }
}