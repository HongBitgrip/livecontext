package com.coremedia.blueprint.studio.rest;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for sorting different feed entries by date or name.
 */
public class ExternalLibraryItemRepresentationComparator implements Comparator<ExternalLibraryItemRepresentation>, Serializable {

  public static final int SORT_DATE = 0;
  public static final int SORT_NAME = 1;
  private static final long serialVersionUID = 173601006312617694L;

  private int sortType = SORT_DATE;

  public ExternalLibraryItemRepresentationComparator(int type) {
    this.sortType = type;
  }

  @Override
  public int compare(ExternalLibraryItemRepresentation o1, ExternalLibraryItemRepresentation o2) {
    switch (sortType) {
      case SORT_DATE: {
        if (o2.getPublicationDate() != null && o1.getPublicationDate() != null) {
          return o2.getPublicationDate().compareTo(o1.getPublicationDate());
        }
        return 1;
      }
      case SORT_NAME: {
        if (o2.getName() != null && o1.getName() != null) {
          return o2.getName().compareTo(o1.getName());
        }
        return 1;
      }
      default:
        if (o2.getName() != null && o1.getName() != null) {
          return o2.getName().compareTo(o1.getName());
        }
        return 1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExternalLibraryItemRepresentationComparator)) {
      return false;
    }

    ExternalLibraryItemRepresentationComparator that = (ExternalLibraryItemRepresentationComparator) o;

    return sortType == that.sortType;

  }

  @Override
  public int hashCode() {
    return sortType;
  }
}
