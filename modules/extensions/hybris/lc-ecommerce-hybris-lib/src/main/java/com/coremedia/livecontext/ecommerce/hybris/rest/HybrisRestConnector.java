package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnauthorizedException;
import com.coremedia.objectserver.dataviews.DataViewHelper;
import com.coremedia.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * REST connector for Hybris REST API.
 */
@Service
public class HybrisRestConnector {

  protected static final Logger LOG = LoggerFactory.getLogger(HybrisRestConnector.class);

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private static final String DEFAULT_PROTOCOL = "http";
  private static final String DEFAULT_PROTOCOL_SECURE = "https";
  private static final String DEFAULT_HOST = "co6scdm09";
  private static final int DEFAULT_PORT = 9001;
  private static final int DEFAULT_PORT_SECURE = 9002;
  private static final String DEFAULT_BASE_PATH = "/ws410/rest";
  private static final String DEFAULT_API_VERSION = "v2";

  protected String protocol = DEFAULT_PROTOCOL;
  protected String protocolSecure = DEFAULT_PROTOCOL_SECURE;
  protected String host = DEFAULT_HOST;
  protected int port = DEFAULT_PORT;
  protected int portSecure = DEFAULT_PORT_SECURE;
  protected String basePath = DEFAULT_BASE_PATH;
  protected String apiVersion = DEFAULT_API_VERSION;

  private String user;
  private String password;
  private AUTHENTICATION_TYPE authenticationType = AUTHENTICATION_TYPE.UNKNOWN;

  private String authToken = null;
  private Stopwatch stopwatch;
  private RestTemplate restTemplate;
  private HttpClient httpClient;

  private int connectionRequestTimeoutMillis = -1;
  private int connectionTimeoutMillis = -1;
  private int socketTimeoutMillis = -1;

  private Map<String, HttpHeaders> authenticationHeaderMap = new HashMap<>();

  @Nullable
  public <T> T performGet(String resourcePath, @Nonnull StoreContext storeContext, Class<T> responseType) {
    List<String> templateParameters = new ArrayList<>();
    return performGet(resourcePath, storeContext, responseType, templateParameters, null, false);
  }

  @Nullable
  public <T> T performGet(String resourcePath, @Nonnull StoreContext storeContext, Class<T> responseType,
                          @Nullable List<String> uriTemplateParameters) {
    return performGet(resourcePath, storeContext, responseType, uriTemplateParameters, null, false);
  }

  @Nullable
  public <T> T performGet(String resourcePath, @Nonnull StoreContext storeContext, Class<T> responseType,
                          @Nullable List<String> uriTemplateParameters,
                          @Nullable MultiValueMap<String, String> queryParams, boolean isSecure) {
    // Log if current code is executed in context of a stored DataView evaluation
    DataViewHelper.warnIfCachedInDataview();

    // Need to generate auth headers first:
    generateAuthenticationHeader(storeContext);

    UriComponentsBuilder uriComponentsBuilder = getBaseUri(resourcePath, queryParams, isSecure);

    Object[] vars = uriTemplateParameters != null ? uriTemplateParameters.toArray() : new Object[0];
    UriComponents uriComponents = uriComponentsBuilder.buildAndExpand(vars);

    //UriComponents uriComponents = uriComponentsBuilder.build().encode();
    URI uri = uriComponents.encode().toUri();

    HttpEntity<String> requestEntity = new HttpEntity<>(authenticationHeaderMap.get(storeContext.getStoreId()));

    try {
      if (LOG.isTraceEnabled()) {
        stopwatch = Stopwatch.createStarted();
      }

      ResponseEntity<T> responseEntity;

      try {
        responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, responseType);
      } catch (HttpClientErrorException ex) {
        HttpStatus statusCode = ex.getStatusCode();
        if (statusCode == HttpStatus.NOT_FOUND) {
          LOG.trace("Result from '{}' (response code: {}) will be interpreted as 'no result found'.", uri, statusCode);
          return null;
        }

        throw ex;
      }

      if (LOG.isTraceEnabled() && stopwatch != null && stopwatch.isRunning()) {
        stopwatch.stop();
        LOG.trace("GET request to '{}' returned with HTTP status code {} (took {})", uri,
                responseEntity.getStatusCode().value(), stopwatch);
      }

      return responseEntity.getBody();
    } catch (Exception ex) {
      LOG.warn("REST call to '{}' failed. Exception:\n{}", uri, ex.getMessage());
      throw new CommerceException(
              String.format("REST call to '%s' failed. Exception: %s", uri, ex.getMessage()),
              ex);
    }
  }

  public <T, P> T performPost(String resourcePath,
                              Class<T> responseType,
                              @Nullable List<String> uriTemplateParameters,
                              @Nullable MultiValueMap<String, String> queryParams,
                              @Nullable P bodyData,
                              boolean isSecure) {
    // Log if current code is executed in context of a stored DataView evaluation
    DataViewHelper.warnIfCachedInDataview();

    UriComponentsBuilder uriComponentsBuilder = getBaseUri(resourcePath, queryParams, isSecure);

    Object[] vars = uriTemplateParameters != null ? uriTemplateParameters.toArray() : new Object[0];
    URI uri = uriComponentsBuilder.buildAndExpand(vars).encode().toUri();

    ResponseEntity<T> responseEntity = null;

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      HttpEntity<String> requestEntity = new HttpEntity<>(toJson(bodyData), headers);

      if (LOG.isTraceEnabled()) {
        stopwatch = Stopwatch.createStarted();
      }

      responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, responseType);

      if (LOG.isTraceEnabled() && stopwatch != null && stopwatch.isRunning()) {
        stopwatch.stop();
        LOG.trace("GET request to '{}' returned with HTTP status code {} (took {})", uri,
                responseEntity.getStatusCode().value(), stopwatch);
      }

      return responseEntity.getBody();
    } catch (Exception ex) {
      int statusCode = responseEntity != null ? responseEntity.getStatusCode().value() : -1;
      if (isAuthenticationError(ex, statusCode)) {
        throw new UnauthorizedException(ex.getMessage(), statusCode);
      } else {
        LOG.warn("REST call to '{}' failed. Exception:\n{}", uri, ex);
        throw new CommerceRemoteException(ex.getMessage(), statusCode, statusCode + "", null);
      }
    }
  }

  @Nonnull
  public String getServiceEndpointId() {
    return UriComponentsBuilder.newInstance()
            .scheme(getProtocol())
            .host(getHost())
            .port(getPort())
            .path(getBasePath())
            .toUriString();
  }

  @Nonnull
  private UriComponentsBuilder getBaseUri(String resourcePath, @Nullable MultiValueMap queryParams, boolean isSecure) {
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
            .scheme(isSecure ? getProtocolSecure() : getProtocol())
            .host(getHost())
            .port(isSecure ? getPortSecure() : getPort())
            .path(getBasePath());

    if (queryParams != null) {
      uriComponentsBuilder.queryParams(queryParams);
    }

    // Add resource path
    uriComponentsBuilder.path(resourcePath);

    return uriComponentsBuilder;
  }

  private static boolean isAuthenticationError(@Nonnull Exception ex, int statusCode) {
    return statusCode == HttpStatus.UNAUTHORIZED.value()
            || ex instanceof HttpClientErrorException
            && hasMessageThatContains(ex, HttpStatus.UNAUTHORIZED.getReasonPhrase());
  }

  private static boolean hasMessageThatContains(@Nonnull Exception ex, @Nonnull CharSequence messageSubstring) {
    String message = ex.getMessage();
    return message != null && message.contains(messageSubstring);
  }

  /**
   * Converts the given model to a json string.
   *
   * @param model service model
   * @return string(JSON) representation of model
   * @throws java.io.IOException
   */
  private static String toJson(Object model) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    //mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    //mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    return mapper.writeValueAsString(model);
  }

  private void generateAuthenticationHeader(@Nonnull StoreContext storeContext) {
    String storeId = storeContext.getStoreId();

    HttpHeaders httpHeadersForCurrentStore = authenticationHeaderMap.get(storeId);

    if (httpHeadersForCurrentStore != null) {
      return;
    }

    HttpHeaders authenticationHeader = new HttpHeaders();

    switch (authenticationType) {
      case BASIC:
        LOG.debug("Generating basic authorization header ...");

        String plainCredentials = getUser() + ':' + getPassword();
        byte[] plainCredentialsBytes = plainCredentials.getBytes();
        String basicAuthCredentials = Base64.encode(plainCredentialsBytes);

        authenticationHeader.add(AUTHORIZATION_HEADER, "Basic " + basicAuthCredentials);
        authenticationHeader.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        authenticationHeader.add("Content-type", MediaType.APPLICATION_JSON_VALUE);

        Locale locale = storeContext.getLocale();
        if (locale != null) {
          authenticationHeader.add("Accept-language", locale.getLanguage());
        }

        authenticationHeaderMap.put(storeId, authenticationHeader);
        break;

      case BEARER:

        if (isNullOrEmpty(authToken)) {
          authToken = fetchAuthToken();
        }

        LOG.debug("Generating bearer authorization header ...");
        authenticationHeader.set(AUTHORIZATION_HEADER, "Bearer " + authToken);
        authenticationHeaderMap.put(storeId, authenticationHeader);
        break;

      case NONE:
        // do nothing;

      default:
        LOG.warn("Unknown authentication type value found for [livecontext.hybris.authentication.type], "
                + "proceed w/o authentication.");
        break;
    }
  }

  @Nullable
  public String fetchAuthToken() {
    String url = UriComponentsBuilder.newInstance()
            .scheme(protocol)
            .host(host)
            .path(basePath)
            .path("/" + apiVersion)
            .path("/integration/admin/token")
            .build()
            .encode()
            .toString();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String requestEntityBody = "{\"username\": \"" + user + "\", \"password\": \"" + password + "\"}";
    HttpEntity<String> requestEntity = new HttpEntity<>(requestEntityBody, headers);

    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      LOG.warn("Unable to request authentication token. Check credentials!");
      return null;
    }

    String body = response.getBody();

    if (body == null) {
      LOG.warn("Unable to request authentication token. Response body was empty.");
      return null;
    }

    return body.replaceAll("\"", "");
  }

  @Nonnull
  private HttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = HttpClientFactory.createHttpClient(true, false, HttpStatus.OK.value(), socketTimeoutMillis, connectionTimeoutMillis, connectionRequestTimeoutMillis);
    }

    return httpClient;
  }

  @PostConstruct
  void initialize() {
    HttpClient client = getHttpClient();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
    restTemplate = new RestTemplate(requestFactory);
  }

  public String getApiVersion() {
    return apiVersion;
  }

  @Value("${livecontext.hybris.apiVersion}")
  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getHost() {
    return host;
  }

  @Value("${livecontext.hybris.host}")
  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  @Value("${livecontext.hybris.port:80}")
  public void setPort(int port) {
    this.port = port;
  }

  public String getProtocol() {
    return protocol;
  }

  @Value("${livecontext.hybris.protocol}")
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getBasePath() {
    return basePath;
  }

  @Value("${livecontext.hybris.basePath}")
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  private String getUser() {
    return user;
  }

  @Value("${livecontext.hybris.user}")
  public void setUser(String user) {
    this.user = user;
  }

  private String getPassword() {
    return password;
  }

  @Value("${livecontext.hybris.password}")
  public void setPassword(String password) {
    this.password = password;
  }

  @Value("${livecontext.hybris.authentication.type:UNKNOWN}")
  public void setAuthenticationType(AUTHENTICATION_TYPE authenticationTypeValue) {
    authenticationType = authenticationTypeValue;
  }

  public String getProtocolSecure() {
    return protocolSecure;
  }

  @Value("${livecontext.hybris.protocol.ssl}")
  public void setProtocolSecure(String protocolSecure) {
    this.protocolSecure = protocolSecure;
  }

  public int getPortSecure() {
    return portSecure;
  }

  @Value("${livecontext.hybris.port.ssl:443}")
  public void setPortSecure(int portSecure) {
    this.portSecure = portSecure;
  }

  @Value("${livecontext.hybris.rest.connector.connectionRequestTimeoutMillis:60000}")
  public void setConnectionRequestTimeoutMillis(int connectionRequestTimeoutMillis) {
    this.connectionRequestTimeoutMillis = connectionRequestTimeoutMillis;
  }

  @Value("${livecontext.hybris.rest.connector.connectionTimeoutMillis:60000}")
  public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
    this.connectionTimeoutMillis = connectionTimeoutMillis;
  }

  @Value("${livecontext.hybris.rest.connector.socketTimeoutMillis:60000}")
  public void setSocketTimeoutMillis(int socketTimeoutMillis) {
    this.socketTimeoutMillis = socketTimeoutMillis;
  }
}
