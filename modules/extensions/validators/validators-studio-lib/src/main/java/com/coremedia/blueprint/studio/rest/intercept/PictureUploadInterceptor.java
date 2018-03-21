package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.cap.common.Blob;
import com.coremedia.image.ImageDimensionsExtractor;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.transform.BlobTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PictureUploadInterceptor extends ContentWriteInterceptorBase {

  public static final int DEFAULT_MAX_DIMENSION = 4000;

  private static final Logger LOG = LoggerFactory.getLogger(PictureUploadInterceptor.class);
  private static final String WIDTH_PROPERTY = "width";
  private static final String HEIGHT_PROPERTY = "height";

  private BlobTransformer blobTransformer;
  private ImageDimensionsExtractor extractor;
  private int maxDimension = DEFAULT_MAX_DIMENSION;
  private String imageProperty;

  @Override
  public void intercept(ContentWriteRequest request) {
    Map<String,Object> properties = request.getProperties();
    if (properties.containsKey(imageProperty)) {
      Object value = properties.get(imageProperty);
      if (value instanceof Blob) {
        scale((Blob) value, properties);
      }
    }
  }

  public void setImageProperty(String imageProperty) {
    this.imageProperty = imageProperty;
  }

  private void scale(Blob blob, Map<String, Object> properties) {
    int[] pictureDimensions = this.getPictureDimensions(blob);
    int fullWidth = pictureDimensions[0];
    int fullHeight = pictureDimensions[1];

    // Only scale it needed
    if (fullWidth > maxDimension || fullHeight > maxDimension) {
      // Scale dimensions for landscape/square and portrait pictures
      float aspectRatio = (float) fullWidth / (float) fullHeight;
      // Scale dimensions for landscape/square and portrait pictures
      int scaledWidth;
      int scaledHeight;
      String operations;
      if (fullWidth >= fullHeight) {
        operations = String.format("s;w=%d", maxDimension);
        scaledWidth = maxDimension;
        scaledHeight = Math.round(maxDimension / aspectRatio);
      } else {
        operations = String.format("s;h=%d", maxDimension);
        scaledWidth = Math.round(maxDimension * aspectRatio);
        scaledHeight = maxDimension;
      }

      Blob result;
      try {
        result = blobTransformer.transformBlob(blob, operations);
        updatePicture(properties, result, scaledWidth, scaledHeight);
      } catch (Exception e) {
        LOG.error("Could not scale picture " + blob, e);
      }
    }
    else {
      updatePicture(properties, blob, fullWidth, fullHeight);
    }
  }

  /* Visible for testing */
  int[] getPictureDimensions(Blob blob) {
    // width = 0, height = 0
    int[] result = new int[2];
    InputStream input = null;
    try {
      input = blob.getInputStream();
      String mimeType = blob.getContentType().getBaseType();
      int[] dimensions = extractor.getImageDimensions(mimeType, input);
      if (dimensions != null) {
        result = dimensions;
      }
    } catch (Exception e) {
      LOG.warn("Could not read dimensions. Width and height will be set to '0' for picture " + blob, e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException e) { //NOSONAR
        /* Ignore closing exceptions */
      }
    }
    return result;
  }

  private void updatePicture(Map<String,Object> properties, Blob blob, int width, int height) {
    properties.put(WIDTH_PROPERTY, width);
    properties.put(HEIGHT_PROPERTY, height);
    if (blob != null) {
      properties.put(imageProperty, blob);
    }
  }

  @Required
  public void setBlobTransformer(BlobTransformer blobTransformer) {
    this.blobTransformer = blobTransformer;
  }

  @Required
  public void setExtractor(ImageDimensionsExtractor extractor) {
    this.extractor = extractor;
  }

  public void setMaxDimension(int maxDimension) {
    this.maxDimension = maxDimension;
  }

}
