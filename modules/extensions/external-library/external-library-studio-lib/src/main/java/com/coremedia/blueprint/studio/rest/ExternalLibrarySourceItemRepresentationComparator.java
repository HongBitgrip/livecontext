package com.coremedia.blueprint.studio.rest;

import java.io.Serializable;
import java.util.Comparator;

public class ExternalLibrarySourceItemRepresentationComparator implements Comparator<ExternalLibrarySourceItemRepresentation>, Serializable {
  @Override
  public int compare(ExternalLibrarySourceItemRepresentation o1, ExternalLibrarySourceItemRepresentation o2) {
    return o1.getIndex() - o2.getIndex();
  }
}
