package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.cap.common.Blob;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.UploadedBlob;

import java.util.Map;

/**
 * Makes use of the {@link UploadedBlob} class when a {@link Blob} is uploaded via Studio to store the filename of the
 * uploaded file in the configured filename property.
 */
public class BlobFilenameWriteInterceptor extends ContentWriteInterceptorBase {

  private String blobProperty;
  private String filenameProperty;

  public String getBlobProperty() {
    return blobProperty;
  }

  /**
   * The name of the blob property to store the filename for.
   */
  public void setBlobProperty(String blobProperty) {
    this.blobProperty = blobProperty;
  }

  public String getFilenameProperty() {
    return filenameProperty;
  }

  /**
   * The name of the string property to store the filename.
   */
  public void setFilenameProperty(String filenameProperty) {
    this.filenameProperty = filenameProperty;
  }

  @Override
  public void intercept(ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();
    if (properties != null && properties.containsKey(blobProperty)) {
      Blob blob = (Blob) properties.get(blobProperty);

      String filename = null;
      if (blob != null && blob instanceof UploadedBlob) {
        filename = ((UploadedBlob) blob).getFileName();
      }

      properties.put(filenameProperty, filename);
    }
  }
}
