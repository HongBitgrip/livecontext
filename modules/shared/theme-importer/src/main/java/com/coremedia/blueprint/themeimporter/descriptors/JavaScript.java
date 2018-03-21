package com.coremedia.blueprint.themeimporter.descriptors;

import javax.xml.bind.annotation.XmlAttribute;

public class JavaScript extends Code {
  private boolean inHead;

  public boolean isInHead() {
    return inHead;
  }

  @XmlAttribute
  public void setInHead(boolean inHead) {
    this.inHead = inHead;
  }
}
