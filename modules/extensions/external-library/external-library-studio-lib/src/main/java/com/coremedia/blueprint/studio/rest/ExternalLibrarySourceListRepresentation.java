package com.coremedia.blueprint.studio.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * The list representation wrapper, contains the items
 * that are displayed in the source combo of the external library window.
 */
public class ExternalLibrarySourceListRepresentation {
  private List<ExternalLibrarySourceItemRepresentation> items = new ArrayList<>();

  public List<ExternalLibrarySourceItemRepresentation> getItems() {
    return items;
  }

  public ExternalLibrarySourceItemRepresentation getItemById(int id) {
    for (ExternalLibrarySourceItemRepresentation item : items) {
      if (item.getIndex() == id) {
        return item;
      }
    }
    return null;
  }

  public void addItem(ExternalLibrarySourceItemRepresentation item) {
    this.items.add(item);
  }
}
