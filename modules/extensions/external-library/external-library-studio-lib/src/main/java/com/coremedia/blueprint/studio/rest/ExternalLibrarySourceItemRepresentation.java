package com.coremedia.blueprint.studio.rest;

import java.util.Map;

public class ExternalLibrarySourceItemRepresentation {
  private int index;
  private String name;
  private String providerClass;
  private String dataUrl;
  private String previewType;
  private String contentType;
  private boolean markAsRead;
  private Map<String, Object> parameters;

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProviderClass() {
    return providerClass;
  }

  public void setProviderClass(String providerClass) {
    this.providerClass = providerClass;
  }

  public String getDataUrl() {
    return dataUrl;
  }

  public void setDataUrl(String dataUrl) {
    this.dataUrl = dataUrl;
  }

  public String getPreviewType() {
    return previewType;
  }

  public void setPreviewType(String previewType) {
    this.previewType = previewType;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public boolean isMarkAsRead() {
    return markAsRead;
  }

  public void setMarkAsRead(boolean markAsRead) {
    this.markAsRead = markAsRead;
  }

  public Map<String, Object> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }
}
