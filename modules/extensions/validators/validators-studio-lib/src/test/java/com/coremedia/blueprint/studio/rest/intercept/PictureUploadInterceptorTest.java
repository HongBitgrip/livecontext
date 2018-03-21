package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.cap.common.Blob;
import com.coremedia.image.ImageDimensionsExtractor;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.transform.BlobTransformer;
import com.coremedia.transform.TransformedBlob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.activation.MimeType;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PictureUploadInterceptorTest {

  private static final String IMAGE_PROPERTY_NAME = "imagePropertyName";
  private static final String WIDTH_PROPERTY_NAME = "width";
  private static final String HEIGHT_PROPERTY_NAME = "height";

  @Mock
  private BlobTransformer blobTransformer;

  @Mock
  private Blob blob;

  @Mock
  private TransformedBlob transformedBlob;

  @Mock
  private InputStream inputStream;

  @Mock
  private ImageDimensionsExtractor extractor;

  @Mock
  private ContentWriteRequest contentWriteRequest;

  @Before
  public void setUp() throws Exception {

    when(blob.getContentType()).thenReturn(new MimeType("image/jpeg"));
    when(blob.getInputStream()).thenReturn(inputStream);
    when(blobTransformer.transformBlob(eq(blob), anyString())).thenReturn(transformedBlob);
  }

  @Test
  public void test() throws Exception {
    intercept(null, 1, PictureUploadInterceptor.DEFAULT_MAX_DIMENSION+1,
            String.format("s;h=%d", PictureUploadInterceptor.DEFAULT_MAX_DIMENSION),
            1,
            PictureUploadInterceptor.DEFAULT_MAX_DIMENSION);
    intercept(null, PictureUploadInterceptor.DEFAULT_MAX_DIMENSION, 1);
    intercept(null, PictureUploadInterceptor.DEFAULT_MAX_DIMENSION-1, 1);
    intercept(null, PictureUploadInterceptor.DEFAULT_MAX_DIMENSION+1, 1,
            String.format("s;w=%d", PictureUploadInterceptor.DEFAULT_MAX_DIMENSION),
            PictureUploadInterceptor.DEFAULT_MAX_DIMENSION, 1);
    intercept(200, 400, 2, String.format("s;w=200"), 200, 1);
    intercept(200, 2, 400, String.format("s;h=200"), 1, 200);
  }


  public void intercept(Integer maxDimension, int width, int height) throws Exception {
    intercept(maxDimension, width, height, null, width, height);
  }

  public void intercept(Integer maxDimension, int width, int height, String expectedOperations, int scaledWidth, int scaledHeight) throws Exception {
    Map<String, Object> properties = new HashMap<>();
    properties.put(IMAGE_PROPERTY_NAME, blob);
    properties.put(WIDTH_PROPERTY_NAME, width);
    properties.put(HEIGHT_PROPERTY_NAME, height);

    when(contentWriteRequest.getProperties()).thenReturn(properties);

    PictureUploadInterceptor testling = new PictureUploadInterceptor();
    testling.setBlobTransformer(blobTransformer);
    testling.setImageProperty(IMAGE_PROPERTY_NAME);
    if (maxDimension != null) {
      testling.setMaxDimension(maxDimension);
    }
    testling.setExtractor(extractor);

    when(extractor.getImageDimensions(anyString(), any(InputStream.class))).thenReturn(new int[]{width, height});

    testling.intercept(contentWriteRequest);

    if (expectedOperations == null) {
      // no transformation expected
      assertEquals(blob, properties.get(IMAGE_PROPERTY_NAME));
    } else {
      assertEquals(transformedBlob, properties.get(IMAGE_PROPERTY_NAME));
      verify(blobTransformer).transformBlob(blob, expectedOperations);
    }
    assertEquals(scaledWidth, properties.get(WIDTH_PROPERTY_NAME));
    assertEquals(scaledHeight, properties.get(HEIGHT_PROPERTY_NAME));

  }

}
