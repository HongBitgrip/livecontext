package com.coremedia.blueprint.studio.rest;

/**
 * Represents an item that is part of the raw data list of a ThirdPartyDataItem.
 */
public class ExternalLibraryDataItemRepresentation {
  public static final String DATA_TYPE_CONTENTS = "contents";
  public static final String DATA_TYPE_ENCLOSURES = "enclosures";
  
  private String type;
  private String mode;
  private String value;
  private String dataType;
  private int width;
  private int height;
  private long length;
  
  public ExternalLibraryDataItemRepresentation(String dataType) {
    this.dataType = dataType;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }
  
  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getHeight() {
    return height;
  }
}
