package com.coremedia.blueprint.studio.externallibraryproviders;

import com.coremedia.blueprint.studio.rest.ExternalLibraryItemListRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemRepresentationComparator;
import com.coremedia.blueprint.studio.rest.ExternalLibraryPostProcessingRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryProvider;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.BlobService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.xml.MarkupFactory;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Third party provider for reading RSS feeds.
 */
public class RSSExternalLibraryProvider implements ExternalLibraryProvider {
  private static final int DEFAULT_MAX_IMAGES = 5;
  private static final int TEASER_TEXT_MAX_LENGTH = 100;
  private static final String RSS_ENRICHER_INDICATOR = "This entry passed through the Full-Text RSS service";

  private static final String PICTURE_CONTENT_TYPE = "CMPicture";

  private static final int MIN_IMG_WIDTH = 100;
  private static final int MIN_IMG_HEIGHT = 50;

  private static final Logger LOG = LoggerFactory.getLogger(RSSExternalLibraryProvider.class);

  private List<String> feeds;
  private ExecutorService service = Executors.newCachedThreadPool();
  private MimeTypeService mimeTypeService;
  private int maxImages = DEFAULT_MAX_IMAGES;

  @Override
  public void init(String preferredSite, Map<String, Object> parameters) {
    feeds = tokenizeFeedUrls((String) parameters.get("dataUrl"));
  }

  @Required
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  //maximum amount of images to add to an article
  public void setMaxImages(int maxImages) {
    this.maxImages = maxImages;
  }

  /**
   * The RSS feed is read using an execution service, so that possible timeouts
   * won't block the whole REST call.
   *
   * @param filter The filter string that is applied to the RSS items.
   * @return The JSON representation of the RSS items.
   */
  @Override
  public ExternalLibraryItemListRepresentation getItems(String filter) {
    ExternalLibraryItemListRepresentation result = new ExternalLibraryItemListRepresentation();
    try {
      List<Future<List<ExternalLibraryItemRepresentation>>> futures = new ArrayList<>();

      //trigger callables for the executor service
      for (String feed : feeds) {
        RSSCallable callable = new RSSCallable(feed, filter);
        futures.add(service.submit(callable));
      }

      //...and collect them
      for (Future<List<ExternalLibraryItemRepresentation>> entry : futures) {
        try {
          List<ExternalLibraryItemRepresentation> callableResult = entry.get(20, TimeUnit.SECONDS); //NOSONAR //timeout of n seconds
          result.addAll(callableResult);
        } catch (TimeoutException e) {
          LOG.error("Timeout during waiting for callable " + entry + ": " + e.getMessage(), e);
          result.setErrorMessage("Timeout waiting for RSS feed response.");
        }
      }

      Collections.sort(result.getItems(), new ExternalLibraryItemRepresentationComparator(ExternalLibraryItemRepresentationComparator.SORT_DATE));
      return result;
    } catch (Exception e) {
      LOG.error("Error retrieving RSS item representations for data url '" + feeds + "': " + e.getMessage(), e);
      result.setErrorMessage(e.getMessage());
    }
    return result;
  }

  /**
   * Returns the third party item for the given id.
   *
   * @param id The provider ID of the RSS entry.
   */
  @Override
  public ExternalLibraryItemRepresentation getItem(String id) {
    ExternalLibraryItemListRepresentation list = getItems(null);
    for (int i = 0; i < list.getSize(); i++) {
      ExternalLibraryItemRepresentation item = list.getItems().get(i);
      if (item.getId().equals(id)) {
        return item;
      }
    }
    return null;
  }

  /**
   * Executed once an article is created from an RSS entry. Several properties are initially set.
   *
   * @param item    The third party item the content has been created from.
   * @param representation The created content object representation.
   */
  @Override
  public void postProcessNewContent(ExternalLibraryItemRepresentation item, ExternalLibraryPostProcessingRepresentation representation) {
    Content content = representation.getCreatedContent();
    if(Strings.isNullOrEmpty(content.getString("title"))) {
      content.set("title", item.getName());
    }
    content.set("locale", "de");

    String text = item.getDescription();
    text = text.replaceAll("\\<.*?\\s*.*?\\>", "").replaceAll("&nbsp;", "");
    if (text.contains(RSS_ENRICHER_INDICATOR)) {
      text = text.substring(0, text.indexOf(RSS_ENRICHER_INDICATOR));
    }

    String teaserText = text.substring(0, Math.min(text.length(), TEASER_TEXT_MAX_LENGTH));
    teaserText = teaserText.replaceAll("&nbsp;", "");

    text = "<?xml version=\"1.0\" ?><div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>" + text + "</p></div>";
    teaserText = "<?xml version=\"1.0\" ?><div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>" + teaserText + "</p></div>";

    extractImageUrl(content, representation, content.getParent().getPath(), item.getDescription(), item.getName());

    content.set("teaserText", MarkupFactory.fromString(teaserText));
    content.set("detailText", MarkupFactory.fromString(text));
  }

  private void extractImageUrl(Content content, ExternalLibraryPostProcessingRepresentation representation, String folder, String text, String name) {
    try {
      List<Content> imageList = new ArrayList<>();
      String imgRegex = "src\\s*=\\s*([\\\"'])?([^ \\\"']*)";
      Pattern p = Pattern.compile(imgRegex, Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(text);
      int imageCount = 0;
      while (m.find()) {
        String imageUrl = m.group(2);
        URL url = new URL(imageUrl);
        if (imageUrl.indexOf("%") == -1) {
          // encode correctly e.g. whitespaces
          URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
          url = uri.toURL();
        }
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();

        CapConnection connection = content.getRepository().getConnection();
        BlobService blobService = connection.getBlobService();

        ContentType contentType = connection.getContentRepository().getContentType(PICTURE_CONTENT_TYPE);
        String extension = imageUrl.substring(imageUrl.lastIndexOf('.') + 1, imageUrl.length());

        // buffer stream to make it markable
        @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
        InputStream bufferedInputStream = new BufferedInputStream(in);

        String mimeTypeString = mimeTypeService.detectMimeType(bufferedInputStream, imageUrl, con.getContentType());

        MimeType mimeType = new MimeType(mimeTypeString);
        Blob blob = blobService.fromInputStream(bufferedInputStream, mimeType);
        BufferedImage img;
        try (InputStream blobInputStream = blob.getInputStream()) {
          img = ImageIO.read(blobInputStream);
        }

        if (img.getWidth() > MIN_IMG_WIDTH && img.getHeight() > MIN_IMG_HEIGHT) {
          Content newImageContent = contentType.createByTemplate(connection.getContentRepository().getChild(folder), content.getName() + " - Picture", "{3} ({1})", new HashMap<String, Object>());
          newImageContent.set("data", blob);
          newImageContent.checkIn();
          imageList.add(newImageContent);
          representation.addCreatedContent(newImageContent);
          imageCount++;
        }
        else {
          LOG.info("Skipped image " + imageUrl + " because of size: " + img.getWidth() + "x" + img.getHeight());
        }

        in.close();

        if (maxImages > 0 && imageCount == maxImages) {
          LOG.info("Skipping RSS image linking, maximum amount of " + maxImages + " reached.");
          break;
        }
      }

      content.set("pictures", imageList);
    } catch (MalformedURLException e) {
      LOG.error("Error loading pictures from RSS: " + e.getMessage(), e);
    } catch (MimeTypeParseException e) {
      LOG.error("Error loading pictures from RSS: " + e.getMessage(), e);
    } catch (IOException e) {
      LOG.error("Error loading pictures from RSS: " + e.getMessage(), e);
    } catch (URISyntaxException e) {
      LOG.error("Error loading pictures from RSS: " + e.getMessage(), e);
    }

  }


  /**
   * Tokenizes the url string that has been passed
   * by the client via post parameter as concatenated string.
   *
   * @param feedsString The raw string from the client.
   * @return An array list with RSS urls.
   */
  private List<String> tokenizeFeedUrls(String feedsString) {
    List<String> feedUrls = new ArrayList<>();
    StringTokenizer tokenizer = new StringTokenizer(feedsString, "|#|");
    while (tokenizer.hasMoreElements()) {
      String token = tokenizer.nextToken();
      if (token.length() > 0) {
        feedUrls.add(token);
      }
    }
    return feedUrls;
  }
}
