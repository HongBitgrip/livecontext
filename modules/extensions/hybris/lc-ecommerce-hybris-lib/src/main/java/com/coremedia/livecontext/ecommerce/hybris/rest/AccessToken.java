package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO holding an access token.
 *
 * Example:
 {
   "access_token": "8bad22ff-25a2-4fb5-829d-121f247ef06e",
   "token_type": "bearer",
   "scope": "basic",
   "expires_in": 40005
 }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

  @JsonProperty("access_token")
  private String token;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("token_type")
  private String type;

  @JsonProperty("expires_in")
  private long expiresIn;

  private long createdAt;

  public AccessToken() {
    createdAt = System.currentTimeMillis();
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  /**
   * Checks whether or not the token is expired.
   *
   * @return <code>true</code> if the token is expired, <code>false</code> otherwise
   */
  public boolean isExpired() {
    return System.currentTimeMillis() - createdAt > expiresIn * 1000; // TODO: Add grace period to expire before
  }

  /**
   * Returns the HTTP-Header token value that can be used in an Authorization header.
   *
   * @return token value
   */
  public String toHttpHeaderValue() {
    return type + " " + token;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
            "token=" + token +
            ", type=" + type +
            ", scope=" + scope +
            ", expired=" + isExpired() +
            "}";
  }
}
