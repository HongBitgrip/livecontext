package com.coremedia.blueprint.themeimporter.descriptors;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ResourceBundles {

  @XmlElement
  private List<ResourceBundle> resourceBundle;

  public List<ResourceBundle> getResourceBundles() {
    return this.resourceBundle;
  }


  public void setResourceBundles(List<ResourceBundle> resourceBundle) {
    this.resourceBundle = resourceBundle;
  }
}
