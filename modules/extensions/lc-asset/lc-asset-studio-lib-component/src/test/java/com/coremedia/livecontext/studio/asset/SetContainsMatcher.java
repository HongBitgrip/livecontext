package com.coremedia.livecontext.studio.asset;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Collection;
import java.util.Set;

class SetContainsMatcher extends BaseMatcher<Set<String>> {

  private Iterable<String> items;

  SetContainsMatcher(Iterable<String> items) {
    this.items = items;
  }

  @Override
  public boolean matches(Object o) {
    if (o == null) {
      return false;
    }
    if (!(o instanceof Collection)) {
      return false;
    }
    Collection collection = (Collection) o;
    for (String item : items) {
      if (!collection.contains(item)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void describeTo(Description description) {
  }
}
