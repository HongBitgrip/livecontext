package com.coremedia.blueprint.elastic.social.demodata;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class GeneratorUtils {

  private static final Logger LOG = LoggerFactory.getLogger(GeneratorUtils.class);

  private GeneratorUtils() {
    // empty constructor
  }

  public static List<String> loadListFromFile(String resourceName) {
    List<String> result = new ArrayList<>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(GeneratorUtils.class.getResourceAsStream(resourceName), "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        result.add(line);
      }
    } catch (IOException e) {
      LOG.error("Cannot load list from '" + resourceName + "'", e);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        LOG.error("Cannot close input stream " + reader, e);
      }
    }
    return result;
  }

  @SuppressWarnings("squid:S2189") // Loops should not be infinite, suppress false positive
  public static List<Blob> loadImages(String prefix, String mimeType, String extension, BlobService blobService) {
    final List<Blob> list = new ArrayList<>();
    int imageCount = 1;
    while (true) {
      try {
        final String filename = prefix + imageCount + "." + extension;
        list.add(blobService.put(CommentGenerator.class.getResourceAsStream(filename), mimeType, filename));
        imageCount++;
      } catch (Exception e) { // NOSONAR
        if(imageCount == 1) {
          // we cannot even load the first image, so something bad happend
          throw new RuntimeException("cannot load image", e); // NOSONAR
        } else {
          // we've loaded all images
          break;
        }
      }
    }

    return list;
  }
}
