package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Example REST-RESPONSE:
 {
 "catalog": "apparel-ukContentCatalog",
 "catalogVersion": "Staged",
 "language": "en",
 "resourcePath": "https://127.0.0.1:9002/yacceleratorstorefront?site=apparel-uk",
 "ticketId": "6476055625054272c824111e-5a06-491f-bcd0-11fb9ad98d14",
 "time": "2016-08-14T23:15:03+0200",
 "user": "anonymous",
 "userGroup": "regulargroup"
 }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreviewTokenDocument {
  @JsonProperty("ticketId" )
  String  ticketId;
  @JsonProperty("time" )
  Date time;
  @JsonProperty("user" )
  String user;
  @JsonProperty("userGroup" )
  String userGroup;

  public String getTicketId() {
    return ticketId;
  }
}

