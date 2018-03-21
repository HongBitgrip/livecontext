package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.ContentBeanTestBase;
import com.coremedia.blueprint.common.contentbeans.CMExternalLink;
import org.junit.Assert;
import org.junit.Test;

public class ExternalLinkHandlerTest extends ContentBeanTestBase {

  @Test
  public void testBuildLinkForExternalLink() throws Exception {
    CMExternalLink cmExternalLink = getContentBean(92);
    Assert.assertTrue(new ExternalLinkHandler().buildLinkForExternalLink(cmExternalLink) != null);
  }
}
