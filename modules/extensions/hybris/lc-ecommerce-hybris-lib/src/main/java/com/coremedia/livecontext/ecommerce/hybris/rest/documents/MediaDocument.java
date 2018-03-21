package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Strings.isNullOrEmpty;

public class MediaDocument extends AbstractHybrisDocument {

  @JsonProperty("@downloadURL")
  private String downloadUrl;

  public String getDownloadUrl() {
    if (!isNullOrEmpty(downloadUrl)) {
      int index = downloadUrl.indexOf("&attachment=true");
      if (index != -1) {
        return downloadUrl.substring(0, index);
      } else {
        return downloadUrl;
      }
    }
    return null;
  }
}
