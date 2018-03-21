package com.coremedia.blueprint.common.datevalidation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Extends the normal Calendar Comparator for null checks
 */
class CalendarComparator implements Comparator<Calendar>, Serializable {

  private static final long serialVersionUID = 5667734265087336585L;

  /**
   * <p>
   * Compares its two calendar arguments for order. Returns a negative integer, zero, or a positive
   * integer as the first calendar is before, equal, or after the the second calendar.
   * </p>
   * {@inheritDoc}
   */
  @Override
  public int compare(Calendar calendar1, Calendar calendar2) {
    if ((calendar1 != null) && (calendar2 == null)) {
      return -1;
    } else if ((calendar1 == null) && (calendar2 != null)) {
      return 1;
    } else if ((calendar1 == null)) {
      return 0;
    } else {
      return calendar1.compareTo(calendar2);
    }
  }
}
