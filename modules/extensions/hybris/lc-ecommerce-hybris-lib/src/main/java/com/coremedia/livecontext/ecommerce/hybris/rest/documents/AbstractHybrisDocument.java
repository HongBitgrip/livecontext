package com.coremedia.livecontext.ecommerce.hybris.rest.documents;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractHybrisDocument {

  @JsonProperty("@uri")
  private String uri;

  @JsonProperty("@pk")
  private String key;

  @JsonProperty("code")
  private String code;

  @JsonProperty("@code")
  private String atCode;

  @JsonProperty("@type")
  private String type;

  /**
   * Map containing all custom attributes.
   */
  @JsonProperty("custom_attributes")
  private List<Object> customAttributes = new ArrayList<>();

  /**
   * Map containing all extension attributes.
   */
//  @JsonProperty("extension_attributes")
  private Map<String, Object> extensionAttributes = new HashMap<>();

  /**
   * Map containing all unmapped attributes.
   */
  private Map<String, Object> unmappedAttributes = new HashMap<>();

  public List<Object> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(List<Object> customAttributes) {
    this.customAttributes = customAttributes;
  }

  public Map<String, Object> getExtensionAttributes() {
    return extensionAttributes;
  }

  public void setExtensionAttributes(Map<String, Object> extensionAttributes) {
    this.extensionAttributes = extensionAttributes;
  }

  @JsonAnyGetter
  public Map<String, Object> unmapped() {
    return unmappedAttributes;
  }

  public Object get(String name) {
    return unmappedAttributes.get(name);
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    unmappedAttributes.put(name, value);
  }

  public String getCode() {
    return code != null ? code : atCode;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
