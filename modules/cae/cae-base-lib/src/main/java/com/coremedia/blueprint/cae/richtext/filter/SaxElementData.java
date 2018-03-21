package com.coremedia.blueprint.cae.richtext.filter;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

class SaxElementData {
  private final String namespaceUri;
  private final String localName;
  private final String qName;
  private final Attributes atts;

  public SaxElementData(String namespaceUri, String localName, String qName, Attributes atts) {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
    this.qName = qName;
    this.atts = new AttributesImpl(atts);
  }

  public String getNamespaceUri() {
    return namespaceUri;
  }

  public String getLocalName() {
    return localName;
  }

  public String getqName() {
    return qName;
  }

  public Attributes getAtts() {
    return atts;
  }

  public boolean isA(String tagName) {
    return "".equals(namespaceUri) ? tagName.equalsIgnoreCase(qName) : tagName.equalsIgnoreCase(localName);
  }
}
