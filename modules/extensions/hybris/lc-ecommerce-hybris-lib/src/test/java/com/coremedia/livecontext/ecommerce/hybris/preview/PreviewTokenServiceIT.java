package com.coremedia.livecontext.ecommerce.hybris.preview;

import com.coremedia.blueprint.lc.test.AbstractServiceTest;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import com.coremedia.livecontext.ecommerce.hybris.rest.AccessToken;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PreviewTokenDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class PreviewTokenServiceIT extends AbstractServiceTest {

  @Inject
  private PreviewTokenService testling;

  @Test
  public void testGetPreviewToken() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    PreviewTokenDocument previewToken;

    previewToken = testling.getPreviewToken();
    assertThat(previewToken).as("first getPreviewToken() call").isNotNull();

    AccessToken accessToken = testling.getoAuthConnector().getAccessToken();
    accessToken.setToken(invalidateToken(accessToken.getToken()));

    previewToken = testling.getPreviewToken();
    assertThat(previewToken).as("second getPreviewToken() call (preview token should renewed)").isNotNull();
  }

  @Test
  public void testGetPreviewTicketId() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    String previewTicketId = testling.getPreviewTicketId();
    assertThat(previewTicketId).isNotNull();
  }

  private String invalidateToken(String token) {
    token = token.substring(0, token.length() - 3);
    token = token + "000";
    return token;
  }
}
