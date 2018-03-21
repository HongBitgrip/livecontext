package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

public class PaginationDocument extends AbstractHybrisDocument {

  @JsonProperty("currentPage")
  private int currentPage;

  @JsonProperty("pageSize")
  private int pageSize;

  @JsonProperty("totalPages")
  private int totalPages;

  @JsonProperty("totalResults")
  private int totalResults;

  @JsonProperty("sort")
  private String sort;

  public int getCurrentPage() {
    return currentPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public int getTotalResults() {
    return totalResults;
  }

  public String getSort() {
    return sort;
  }
}
