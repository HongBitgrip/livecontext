package com.coremedia.blueprint.common.datevalidation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * There are five content items
 * 1. Valid from 1.1.2000 - 31.12.2005
 * 2. Valid from 1.1.2001 - 31.12.2006
 * 3. Valid from 1.1.2002 - 31.12.2007
 * 4. Valid from 1.1.2003 - 31.12.2008
 * 5. Valid from 1.1.2004 - 31.12.2009
 * <p/>
 * Test will run with these dates as now
 * 1. 15.3.1999
 * 2. 15.3.2001
 * 3. 15.3.2005
 * 4. 15.3.2007
 * 5. 15.3.2010
 */
public class ValidityPeriodValidatorTest {
  List<CMLinkable> itemsUnfiltered = new ArrayList<>();


  protected ValidityPeriodValidator validityPeriodValidator;

  @Before
  public void setUp() throws Exception {
    itemsUnfiltered.add(mockLinkable(2000, 0, 1, 2005, 11, 31));
    itemsUnfiltered.add(mockLinkable(2001, 0, 1, 2006, 11, 31));
    itemsUnfiltered.add(mockLinkable(2002, 0, 1, 2007, 11, 31));
    itemsUnfiltered.add(mockLinkable(2003, 0, 1, 2008, 11, 31));
    itemsUnfiltered.add(mockLinkable(2004, 0, 1, 2009, 11, 31));
    itemsUnfiltered.add(mockLinkableFrom(2012, 3, 14, Mockito.mock(CMLinkable.class)));
  }

  private CMLinkable mockLinkable(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {
    CMLinkable linkable = Mockito.mock(CMLinkable.class);
    linkable = mockLinkableFrom(fromYear, fromMonth, fromDay, linkable);
    linkable = mockLinkableTo(toYear, toMonth, toDay, linkable);
    return linkable;
  }

  private CMLinkable mockLinkableTo(int toYear, int toMonth, int toDay, CMLinkable linkable) {
    Calendar validTo = Calendar.getInstance();
    validTo.set(Calendar.YEAR, toYear);
    validTo.set(Calendar.MONTH, toMonth);
    validTo.set(Calendar.DAY_OF_MONTH, toDay);
    Mockito.when(linkable.getValidTo()).thenReturn(validTo);
    initTime(validTo);
    return linkable;
  }

  private CMLinkable mockLinkableFrom(int fromYear, int fromMonth, int fromDay, CMLinkable linkable) {
    Calendar validFrom = Calendar.getInstance();
    validFrom.set(Calendar.YEAR, fromYear);
    validFrom.set(Calendar.MONTH, fromMonth);
    validFrom.set(Calendar.DAY_OF_MONTH, fromDay);
    Mockito.when(linkable.getValidFrom()).thenReturn(validFrom);
    initTime(validFrom);
    return linkable;
  }

  private ValidityPeriodValidator preparePreviewDate(int day, int month, int year) {
    ContentBeanTestBase.setUpPreviewDate(new MockHttpServletRequest(), year, month, day);
    return new ValidityPeriodValidator<>();
  }

  private static void initTime(Calendar now) {
    now.set(Calendar.HOUR_OF_DAY, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);
    now.set(Calendar.MILLISECOND, 0);
  }

  /**
   * Method: filterList(List<Content> source, Calendar now)
   *
   * @throws Exception exception
   */
  @Test
  public void testFilterListCase1() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.MARCH, 1999);
    List validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(0, validItems.size());
  }

  /**
   * Method: filterList(List<Content> source, Calendar now)
   *
   * @throws Exception exception
   */
  @Test
  public void testFilterListCase2() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.MARCH, 2001);
    List validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(2, validItems.size());
  }


  /**
   * Method: filterList(List<Content> source, Calendar now)
   *
   * @throws Exception exception
   */
  @Test
  public void testFilterListCase3() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.MARCH, 2005);
    List validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(5, validItems.size());
  }

  /**
   * Method: filterList(List<Content> source, Calendar now)
   *
   * @throws Exception exception
   */
  @Test
  public void testFilterListCase4() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.MARCH, 2007);
    List validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(3, validItems.size());
  }

  /**
   * Method: filterList(List<Content> source, Calendar now)
   *
   * @throws Exception exception
   */
  @Test
  public void testFilterListCase5() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.MARCH, 2010);
    List validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(0, validItems.size());
  }

  @Test
  public void testFilterListCase6() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.APRIL, 2012);
    List validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(1, validItems.size());

    validator = preparePreviewDate(13, Calendar.APRIL, 2012);
    validItems = new ArrayList<>(validator.filterList(itemsUnfiltered));
    assertEquals(0, validItems.size());
  }

  /**
   * Method: filterList(List<Content> source, Calendar now)
   *
   * @throws Exception exception
   */
  @Test
  public void testValidate() throws Exception {
    ValidityPeriodValidator validator = preparePreviewDate(15, Calendar.MARCH, 2007);
    Assert.assertFalse(validator.validate(itemsUnfiltered.get(0)));
    Assert.assertFalse(validator.validate(itemsUnfiltered.get(1)));
    Assert.assertTrue(validator.validate(itemsUnfiltered.get(2)));
    Assert.assertTrue(validator.validate(itemsUnfiltered.get(3)));
    Assert.assertTrue(validator.validate(itemsUnfiltered.get(4)));
  }

}