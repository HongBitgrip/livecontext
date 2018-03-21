package com.coremedia.blueprint.personalization.forms;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class PersonalizationForm {
  private List<FormField> entries =
          ListUtils.lazyList(new ArrayList<FormField>(), FactoryUtils.instantiateFactory(FormField.class));
  private boolean actionSuccess = false;

  public List<FormField> getEntries() {
    return entries;
  }

  public void setEntries(List<FormField> entries) {
    this.entries = entries;
  }

  public boolean isActionSuccess() {
    return actionSuccess;
  }

  public void setActionSuccess(boolean actionSuccess) {
    this.actionSuccess = actionSuccess;
  }
}
