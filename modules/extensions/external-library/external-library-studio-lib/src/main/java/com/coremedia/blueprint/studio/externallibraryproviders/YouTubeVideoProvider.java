package com.coremedia.blueprint.studio.externallibraryproviders;

import com.coremedia.blueprint.studio.rest.ExternalLibraryItemListRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryPostProcessingRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryProvider;
import com.coremedia.cap.content.Content;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class YouTubeVideoProvider implements ExternalLibraryProvider {

  private static final Logger LOG = LoggerFactory.getLogger(YouTubeVideoProvider.class);

  //settings properties
  private static final String CHANNEL_ID = "channel.id";
  private static final String MAX_RESULTS = "maxNumberOfResultPages";
  private static final String NAME = "name";
  private static final String HTTPS_WWW_GOOGLEAPIS_COM_AUTH_YOUTUBE_FORCE_SSL = "https://www.googleapis.com/auth/youtube.force-ssl";
  private static final String CREDENTIALS_JSON = "credentialsJson";

  private YouTube youtube;
  private String channelId;
  private String name;
  private int maxNumberOfResultPages = 1;
  private String credentialsJson;

  public void init(String preferredSite, Map<String, Object> configuration) {
    this.channelId = (String) configuration.get(CHANNEL_ID);
    this.name = (String) configuration.get(NAME);
    this.credentialsJson = (String) configuration.get(CREDENTIALS_JSON);

    Object maxResults = configuration.get(MAX_RESULTS);
    if (maxResults != null && maxResults instanceof Integer) {
      this.maxNumberOfResultPages = (int) maxResults;
    }
  }

  public ExternalLibraryItemListRepresentation getItems(String filter) {
    ExternalLibraryItemListRepresentation representationList = new ExternalLibraryItemListRepresentation();

    if(StringUtils.isEmpty(channelId)) {
      representationList.setErrorMessage("No channel.id configured for YouTube provider '" + name + "'");
      return representationList;
    }

    if(StringUtils.isEmpty(credentialsJson)) {
      representationList.setErrorMessage("No credentialsJson configured for YouTube provider '" + name + "'");
      return representationList;
    }

    try {
      ChannelListResponse channelResponse =
              getYouTube().channels().list("contentDetails").setId(channelId).setFields("items/contentDetails,nextPageToken,pageInfo").execute();
      List<Channel> channelsList = channelResponse.getItems();

      if(channelsList.isEmpty()) {
        representationList.setErrorMessage("No videos found for YouTube provider '" + name + "'");
        return representationList;
      }

      //differ between search and channel ist depending on the UI filter value
      String uploadPlaylistId = channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();
      if (StringUtils.isBlank(filter)) {
        addPlaylistItems(representationList, uploadPlaylistId);

      }
      else {
        addSearchItems(filter, representationList);
      }
    } catch (Exception e) {
      LOG.warn("Error loading youtube video list. ", e);
      representationList.setErrorMessage(e.getMessage());
    }
    return representationList;
  }

  private void addSearchItems(String filter, ExternalLibraryItemListRepresentation representationList) throws Exception {
    YouTube.Search.List search = getYouTube().search().list("id,snippet");
    search.setChannelId(channelId).setMaxResults(50l);
    search.setFields("items(id/kind,id/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
    search.setType("video");
    search.setQ(filter);
    SearchListResponse searchListResponse = search.execute();
    List<SearchResult> items = searchListResponse.getItems();

    //If there are more videos in the channel, retrieve them as well
    for (int i = 0; i < maxNumberOfResultPages && searchListResponse.getNextPageToken() != null; i++) {
      searchListResponse = search.setPageToken(searchListResponse.getNextPageToken()).execute();
      items.addAll(searchListResponse.getItems());
    }

    for (SearchResult item : items) {
      ResourceId rId = item.getId();
      if (rId.getKind().equals("youtube#video")) {
        String videoId = rId.getVideoId();
        Video video = getVideo(videoId);
        ExternalLibraryItemRepresentation representation = buildVideoRepresentation(video);
        representationList.add(representation);
      }
    }
  }

  private void addPlaylistItems(ExternalLibraryItemListRepresentation representationList, String uploadPlaylistId) throws Exception {
    // Retrieve the playlist of the channel's uploaded videos.
    YouTube.PlaylistItems.List playlistItemRequest = getYouTube().playlistItems().list("id,contentDetails,snippet");

    //sadly, 50 is the maximum number of videos that can be requested at once.
    playlistItemRequest.setPlaylistId(uploadPlaylistId).setMaxResults(50l);
    playlistItemRequest.setFields("items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
    PlaylistItemListResponse playlistItemListResponse = playlistItemRequest.execute();
    List<PlaylistItem> playlistItems = playlistItemListResponse.getItems();

    //If there are more videos in the channel, retrieve them as well
    for (int i = 0; i < maxNumberOfResultPages && playlistItemListResponse.getNextPageToken() != null; i++) {
      playlistItemListResponse = playlistItemRequest.setPageToken(playlistItemListResponse.getNextPageToken()).execute();
      playlistItems.addAll(playlistItemListResponse.getItems());
    }
    //Build the video representation of the playlist
    for (PlaylistItem playlistItem : playlistItems) {
      String videoId = playlistItem.getContentDetails().getVideoId();
      Video video = getVideo(videoId);
      ExternalLibraryItemRepresentation representation = buildVideoRepresentation(video);
      representationList.add(representation);
    }
  }

  private Video getVideo(String videoId) throws Exception {
    VideoListResponse videoListResponse = getYouTube().videos().list("id,contentDetails,snippet").setId(videoId).execute();
    List<Video> items = videoListResponse.getItems();
    Video video = null;
    if (!items.isEmpty()) {
      video = items.get(0);
    }
    return video;
  }

  private ExternalLibraryItemRepresentation buildVideoRepresentation(Video video) {
    if (video == null) {
      return null;
    }
    ExternalLibraryItemRepresentation representation = new ExternalLibraryItemRepresentation();
    representation.setId(video.getId());
    representation.setName(video.getSnippet().getTitle());
    representation.setDescription(video.getSnippet().getDescription());
    representation.setDataUrl("https://www.youtube.com/watch?v=" + video.getId());
    representation.setDownloadUrl("https://www.youtube.com/watch?v=" + video.getId());

    String url = video.getSnippet().getThumbnails().getMedium().getUrl();
    representation.setThumbnailUri(url);
        /*
        In case you prefer to use a picture instead of the video at the moment of calling to the provider
        representation.setRawData("<img src=" + url + ">");
        */
    representation.setRawData("<iframe src=\"//www.youtube.com/embed/" + video.getId() + "\" " +
            "class=\"cm-video cm-video--youtube\" " +
            "frameborder=\"0\" " +
            "width=\"400\" " +
            "height=\"225\" " +
            "webkitAllowFullScreen=\"\" " +
            "mozallowfullscreen=\"\" " +
            "allowFullScreen=\"\">\n" +
            "</iframe>");
    representation.setTags(buildTagsString(video.getSnippet().getTags()));
    representation.setPublicationDate(new Date(video.getSnippet().getPublishedAt().getValue()));
    representation.setCreatedAt(new Date(video.getSnippet().getPublishedAt().getValue()));
    return representation;
  }

  /**
   * Creates the youtube client using the credentials provided in the content.
   * @return the youtube client
   */
  private YouTube getYouTube() throws Exception {
    if(this.youtube == null) {
      List<String> scopes = Lists.newArrayList(HTTPS_WWW_GOOGLEAPIS_COM_AUTH_YOUTUBE_FORCE_SSL);
      GoogleCredential credential = GoogleCredential.fromStream(new ByteArrayInputStream(credentialsJson.getBytes()));
      if (credential.createScopedRequired()) {
        credential = credential.createScoped(scopes);
      }

      youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("youtubeProvider").build();
    }

    return youtube;
  }

  /**
   * Creates the tag list for the video
   * @param tags the youtube tags that have been applied to a video
   */
  private String buildTagsString(List<String> tags) {
    if (tags == null) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    for (String tag : tags) {
      if (builder.length() != 0) {
        builder.append(',').append(tag);
      }
      else {
        builder.append(tag);
      }
    }
    return builder.toString();
  }

  public ExternalLibraryItemRepresentation getItem(String id) {
    try {
      return buildVideoRepresentation(getVideo(id));
    } catch (Exception e) {
      LOG.warn("Error loading youtube video. ", e);
    }
    return null;
  }

  public void postProcessNewContent(ExternalLibraryItemRepresentation item, ExternalLibraryPostProcessingRepresentation representation) {
    Content createdContent = representation.getCreatedContent();
    createdContent.set("dataUrl", item.getDataUrl());
    createdContent.set("detailText", item.getDescription());
  }
}
