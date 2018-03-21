package com.coremedia.blueprint.cae.action.search;

import java.util.regex.Pattern;

/**
 * Bean to store search values
 *
 * @cm.template.api
 */
public class SearchFormBean {

  private String query;
  private String channelId;
  private String docType;
  private int pageNum = 0;

  // written with spaces just to make it readable
  private static final String CHARACTERS_TO_ESCAPE = "+ - ! ( ) { } [ ] ^ \" ~ * ? : \\".replaceAll(" ", "");

  // || and && are handled in separate groups
  private static final String ESCAPE_REGEXP = "([\\Q" + CHARACTERS_TO_ESCAPE + "\\E])|(\\Q||\\E)|(\\Q&&\\E)";

  // Create only one pattern and share it
  private static final Pattern PATTERN = Pattern.compile(ESCAPE_REGEXP);

  // Escape every character or character sequence matched by PATTERN
  private static final String REPLACEMENT = "\\\\$1$2$3";

  /**
   * @cm.template.api
   */
  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getDocType() {
    return docType;
  }

  public String getDocTypeEscaped() {
    if (docType != null) {
      return PATTERN.matcher(docType).replaceAll(REPLACEMENT);
    }
    return null;
  }

  public void setDocType(String docType) {
    this.docType = docType;
  }

  public int getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public String getQueryEscaped() {
    if (query != null) {
      return PATTERN.matcher(query).replaceAll(REPLACEMENT);
    }
    return null;
  }
}
