package com.coremedia.blueprint.studio.externallibraryproviders;

import com.coremedia.blueprint.studio.rest.ExternalLibraryDataItemRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemRepresentation;
import com.google.common.annotations.VisibleForTesting;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.coremedia.blueprint.studio.rest.ExternalLibraryDataItemRepresentation.DATA_TYPE_CONTENTS;
import static com.coremedia.blueprint.studio.rest.ExternalLibraryDataItemRepresentation.DATA_TYPE_ENCLOSURES;
import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Collections.emptyList;

/**
 * Callable for requesting a RSS feed.
 */
public class RSSCallable implements Callable<List<ExternalLibraryItemRepresentation>> {
  private static final Logger LOG = LoggerFactory.getLogger(RSSCallable.class);

  public static final int UCS_CHARACTER_LINE_SEPARATOR = 8232;
  public static final int CHARACTER_SPACE = 32;

  private String rssUrl;
  private String filter;

  public RSSCallable(String url, String filter) {
    this.rssUrl = url;
    this.filter = filter;
  }

  /**
   * The callable implementation, reads the RSS stream for the given RSS URL
   * and tokenizes the returning XML representation into third party item objects.
   *
   * @return The list of third party items created for the RSS stream.
   * @throws Exception
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<ExternalLibraryItemRepresentation> call() throws IOException, FeedException {
    URL feedSource = new URL(rssUrl);

    try {
      SyndFeed feed = readFeed(feedSource);
      List<SyndEntry> entries = feed.getEntries();
      return buildThirdPartyRepresentations(entries);
    } catch (Exception e) {
      LOG.error("Failed to load RSS feed '" + rssUrl + "': " + e.getMessage(), e);
      return emptyList();
    }
  }

  @Nonnull
  private static SyndFeed readFeed(@Nonnull URL source) throws IOException, FeedException {
    XmlReader.setDefaultEncoding("utf8");

    try (XmlReader reader = new XmlReader(source)) {
      SyndFeedInput input = new SyndFeedInput();
      return input.build(reader);
    }
  }

  @Nonnull
  private List<ExternalLibraryItemRepresentation> buildThirdPartyRepresentations(@Nonnull Iterable<SyndEntry> entries) {
    List<ExternalLibraryItemRepresentation> representations = new ArrayList<>();

    for (SyndEntry entry : entries) {
      ExternalLibraryItemRepresentation representation = buildThirdPartyRepresentation(entry);
      if (representation.matches(filter)) {
        representations.add(representation);
      }
    }

    return representations;
  }

  /**
   * Conversion of the rome RSS result entry to the third party provider item.
   *
   * @param syndEntry The rome SyndEntry that contains RSS data.
   * @return The common third party data item that contains the RSS data.
   */
  @Nonnull
  private ExternalLibraryItemRepresentation buildThirdPartyRepresentation(@Nonnull SyndEntry syndEntry) {
    ExternalLibraryItemRepresentation item = new ExternalLibraryItemRepresentation();

    item.setDataUrl(rssUrl);
    item.setId(syndEntry.getUri());

    Date publishedDate = syndEntry.getPublishedDate();
    item.setPublicationDate(publishedDate);
    item.setCreatedAt(publishedDate);
    if (item.getCreatedAt() == null) {
      item.setCreatedAt(syndEntry.getUpdatedDate());
    }

    item.setDescription(getDescription(syndEntry));
    item.setDownloadUrl(syndEntry.getUri());
    item.setUserId(syndEntry.getAuthor());
    item.setName(getTitle(syndEntry));

    List contents = syndEntry.getContents();
    if (!contents.isEmpty()) {
      List<ExternalLibraryDataItemRepresentation> contentDataItems = buildContentDataItems(contents);
      item.getRawDataList().addAll(contentDataItems);
    }

    //ok, there are several more list, and some more link this pattern to fill up the REST representation.
    List enclosures = syndEntry.getEnclosures();
    if (!enclosures.isEmpty()) {
      List<ExternalLibraryDataItemRepresentation> enclosureDataItems = buildEnclosureDataItems(enclosures);
      item.getRawDataList().addAll(enclosureDataItems);
    }

    return item;
  }

  @Nonnull
  private static String getDescription(@Nonnull SyndEntry syndEntry) {
    SyndContent descriptionContent = syndEntry.getDescription();
    return descriptionContent != null ? descriptionContent.getValue() : "";
  }

  @Nonnull
  private static String getTitle(@Nonnull SyndEntry syndEntry) {
    String title = firstNonNull(syndEntry.getTitle(), "");
    title = replaceUnicodeLineSeparator(title);
    return title;
  }

  @Nonnull
  @VisibleForTesting
  static String replaceUnicodeLineSeparator(@Nonnull String s) {
    // a character with ASCII code 8232 was passed by bild.de
    while (s.indexOf(UCS_CHARACTER_LINE_SEPARATOR) != -1) {
      char unicodeLineSeparator = UCS_CHARACTER_LINE_SEPARATOR;
      char space = CHARACTER_SPACE;

      s = s.replace(unicodeLineSeparator, space);
    }

    return s;
  }

  @Nonnull
  private static List<ExternalLibraryDataItemRepresentation> buildContentDataItems(@Nonnull Iterable contents) {
    List<ExternalLibraryDataItemRepresentation> contentDataItems = new ArrayList<>();
    for (Object c : contents) {
      SyndContent content = (SyndContent) c;
      ExternalLibraryDataItemRepresentation dataItem = buildContentDataItem(content);
      contentDataItems.add(dataItem);
    }
    return contentDataItems;
  }

  @Nonnull
  private static List<ExternalLibraryDataItemRepresentation> buildEnclosureDataItems(@Nonnull Iterable enclosures) {
    List<ExternalLibraryDataItemRepresentation> enclosureDataItems = new ArrayList<>();
    for (Object c : enclosures) {
      SyndEnclosure content = (SyndEnclosure) c;
      ExternalLibraryDataItemRepresentation dataItem = buildEnclosureDataItem(content);
      enclosureDataItems.add(dataItem);
    }
    return enclosureDataItems;
  }

  @Nonnull
  private static ExternalLibraryDataItemRepresentation buildContentDataItem(@Nonnull SyndContent content) {
    ExternalLibraryDataItemRepresentation item = new ExternalLibraryDataItemRepresentation(DATA_TYPE_CONTENTS);

    item.setType(content.getType());
    item.setMode(content.getMode());
    item.setValue(content.getValue());

    return item;
  }

  @Nonnull
  private static ExternalLibraryDataItemRepresentation buildEnclosureDataItem(@Nonnull SyndEnclosure content) {
    ExternalLibraryDataItemRepresentation item = new ExternalLibraryDataItemRepresentation(DATA_TYPE_ENCLOSURES);

    item.setType(content.getType());
    item.setValue(content.getUrl());
    item.setLength(content.getLength());

    return item;
  }
}
