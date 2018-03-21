package com.coremedia.blueprint.cae.search;

public class ValueAndCount {

  private String name;
  private long count;

  public ValueAndCount(String name, long count) {
    this.name = name;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "ValueAndCount[name='" + name + "', count=" + count + ']';
  }
}
