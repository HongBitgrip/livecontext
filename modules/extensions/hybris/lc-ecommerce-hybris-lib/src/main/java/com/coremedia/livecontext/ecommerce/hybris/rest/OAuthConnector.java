package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;

/**
 * Open Commerce API OAuth connector.
 */
@Service
public class OAuthConnector {

  protected static final Logger LOG = LoggerFactory.getLogger(OAuthConnector.class);

  // --- Defaults ---
  private static final String DEFAULT_PROTOCOL = "https";
  private static final int DEFAULT_OAUTH_PORT = 9002;
  private static final String DEFAULT_CLIENT_ID = "coremedia_preview";
  private static final String DEFAULT_OAUTH_HOST = "localhost";
  private static final String DEFAULT_OAUTH_TOKEN_PATH = "/authorizationserver/oauth/token";

  // --- HTTP Header constants ---
  private static final String GRANT_TYPE = "password";//"client_credentials";

  private String protocol = DEFAULT_PROTOCOL;
  private String host = DEFAULT_OAUTH_HOST;
  private int port = DEFAULT_OAUTH_PORT;
  private String path = DEFAULT_OAUTH_TOKEN_PATH;
  private String clientId = DEFAULT_CLIENT_ID;
  private String clientSecret = DEFAULT_CLIENT_ID;

  //TODO seems to be duplicate information, but hybris seems to need this (for further investigation see also GRANT_TYPE)
  private String user;
  private String password;

  private RestTemplate restTemplate = new RestTemplate();
  private AccessToken accessToken;
  private HttpClient httpClient;

  /**
   * Example:
   * https://localhost:9002/authorizationserver/oauth/token?grant_type=password&username=admin&password=nimda&client_id=coremedia_preview&client_secret=secret
   */
  private AccessToken requestAccessToken() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .scheme(protocol)
            .host(host)
            .path(path)
            .port(port)
            .queryParam("grant_type", GRANT_TYPE)
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("username", user)
            .queryParam("password", password)
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    String url = uriComponents.toString();
    LOG.info("Requesting access token. {}", url);

    try {
      ResponseEntity<AccessToken> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
              AccessToken.class);

      if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        AccessToken result = responseEntity.getBody();
        LOG.info("Fetched access token. {}", result);
        return result;
      } else {
        LOG.warn("Unable to request authentication token. Check credentials!");
      }
    } catch (HttpClientErrorException e) {
      LOG.error("Token request '{}' returned with HTTP status code {} ({})", url, e.getStatusCode(),
              e.getLocalizedMessage());
    }

    return null;
  }

  public AccessToken getOrRequestAccessToken() {
    if (accessToken == null || accessToken.isExpired()) {
      accessToken = requestAccessToken();
    }
    return accessToken;
  }

  public AccessToken renewAccessToken() {
    accessToken = requestAccessToken();
    return accessToken;
  }

  @PostConstruct
  void initialize() {
    HttpClient client = getHttpClient();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
    restTemplate = new RestTemplate(requestFactory);
  }

  public String getProtocol() {
    return protocol;
  }

  @Value("${livecontext.hybris.oauth.protocol}")
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getHost() {
    return host;
  }

  @Value("${livecontext.hybris.host}")
  public void setHost(String host) {
    this.host = host;
  }

  public String getPath() {
    return path;
  }

  @Value("${livecontext.hybris.oauth.path}")
  public void setPath(String path) {
    this.path = path;
  }

  public String getClientId() {
    return clientId;
  }

  @Value("${livecontext.hybris.oauth.clientId}")
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  @Value("${livecontext.hybris.oauth.clientSecret}")
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public int getPort() {
    return port;
  }

  @Value("${livecontext.hybris.oauth.port}")
  public void setPort(int port) {
    this.port = port;
  }

  @Value("${livecontext.hybris.oauth.user}")
  public void setUser(String username) {
    this.user = username;
  }

  @Value("${livecontext.hybris.oauth.password}")
  public void setPassword(String password) {
    this.password = password;
  }

  private HttpClient getHttpClient() {
    if (httpClient == null) {
      int timeout = -1;
      httpClient = HttpClientFactory.createHttpClient(true, false, HttpStatus.OK.value(), timeout, timeout, timeout);
    }

    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public AccessToken getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }
}
