package com.coremedia.blueprint.studio.rest;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The JSON representation of a single external library item.
 */
public class ExternalLibraryItemRepresentation {
  private String adminTags;
  private String categories;
  private String description;
  //Download URL for the entry
  private String downloadUrl;
  private String id;
  // Entry name (Min 1 chars)
  private String name;
  // Entry external reference id
  private String referenceId;
  // Indexed search text for full text search
  private String searchText;
  private String status;
  private String tags;
  private String thumbnailUri;
  private String type;
  //Version of the entry data
  private String version;
  private String license;
  //The ID of the user who is the owner of this entry
  private String userId;
  private String groupId;
  private long duration;
  private int width;
  private int height;

  //Date properties
  private Date createdAt;
  private Date publicationDate;
  // Entry scheduling start date
  private Date startDate;
  // Entry update date
  private Date updatedAt;
  // Entry scheduling end date
  private Date endDate;

  //Social
  //Number of votes
  private int votes;
  // The total (sum) of all votes
  private int totalRank;
  private int moderationCount;
  private String moderationStatus;

  //Related data
  //Use this for settings the providers data url.
  private String dataUrl;
  private String rawData;
  private List<ExternalLibraryDataItemRepresentation> rawDataList = new ArrayList<>();

  /**
   * Returns true if the given pattern matches
   * on of the fields that is relevant for a search match.
   *
   * @param pattern The pattern to search for.
   * @return True if the pattern is null of a hit was found in one of the object's fields.
   */
  public boolean matches(String pattern) {
    boolean blank = StringUtils.isBlank(pattern);
    boolean nameMatch = StringUtils.containsIgnoreCase(this.name, pattern);
    boolean descMatch = StringUtils.containsIgnoreCase(this.description, pattern);
    boolean searchMatch = StringUtils.containsIgnoreCase(this.searchText, pattern);
    boolean adminMatch = StringUtils.containsIgnoreCase(this.adminTags, pattern);
    boolean tagsMatch = StringUtils.containsIgnoreCase(this.tags, pattern);
    return blank || nameMatch || descMatch || searchMatch || adminMatch || tagsMatch;  // NOSONAR
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getDataUrl() {
    return dataUrl;
  }

  public void setDataUrl(String dataUrl) {
    this.dataUrl = dataUrl;
  }

  public String getRawData() {
    return rawData;
  }

  public void setRawData(String rawData) {
    this.rawData = rawData;
  }

  public List<ExternalLibraryDataItemRepresentation> getRawDataList() {
    return rawDataList;
  }

  public void setRawDataList(List<ExternalLibraryDataItemRepresentation> rawDataList) {
    this.rawDataList = rawDataList;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public Date getPublicationDate() {
    return publicationDate == null ? null : new Date(publicationDate.getTime());
  }

  public void setPublicationDate(Date publicationDate) {
    this.publicationDate = publicationDate == null ? null : new Date(publicationDate.getTime());
  }

  public String getAdminTags() {
    return adminTags;
  }

  public void setAdminTags(String adminTags) {
    this.adminTags = adminTags;
  }

  public String getCategories() {
    return categories;
  }

  public void setCategories(String categories) {
    this.categories = categories;
  }

  public Date getCreatedAt() {
    return createdAt == null ? null : new Date(createdAt.getTime());
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getSearchText() {
    return searchText;
  }

  public void setSearchText(String searchText) {
    this.searchText = searchText;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getThumbnailUri() {
    return thumbnailUri;
  }

  public void setThumbnailUri(String thumbnailUri) {
    this.thumbnailUri = thumbnailUri;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public Date getUpdatedAt() {
    return updatedAt == null ? null : new Date(updatedAt.getTime());
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt == null ? null : new Date(updatedAt.getTime());
  }

  public Date getStartDate() {
    return startDate == null ? null : new Date(startDate.getTime());
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate == null ? null : new Date(startDate.getTime());
  }

  public Date getEndDate() {
    return endDate == null ? null : new Date(endDate.getTime());
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate == null ? null : new Date(endDate.getTime());
  }

  public int getVotes() {
    return votes;
  }

  public void setVotes(int votes) {
    this.votes = votes;
  }

  public int getTotalRank() {
    return totalRank;
  }

  public void setTotalRank(int totalRank) {
    this.totalRank = totalRank;
  }

  public int getModerationCount() {
    return moderationCount;
  }

  public void setModerationCount(int moderationCount) {
    this.moderationCount = moderationCount;
  }

  public String getModerationStatus() {
    return moderationStatus;
  }

  public void setModerationStatus(String moderationStatus) {
    this.moderationStatus = moderationStatus;
  }
}
