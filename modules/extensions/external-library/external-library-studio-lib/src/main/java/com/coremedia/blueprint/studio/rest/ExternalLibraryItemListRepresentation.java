package com.coremedia.blueprint.studio.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns the list of available external library items for the configured user.
 */
public class ExternalLibraryItemListRepresentation {
  private List<ExternalLibraryItemRepresentation> items = new ArrayList<>();
  private String errorMessage;

  public void add(ExternalLibraryItemRepresentation item) {
    this.items.add(item);
  }

  public void addAll(List<ExternalLibraryItemRepresentation> items) {
    this.items.addAll(items);
  }
  
  public void setErrorMessage(String msg) {
    this.errorMessage = msg;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public List<ExternalLibraryItemRepresentation> getItems() {
    return items;
  }

  public int getSize() {
    return items.size();
  }
}
