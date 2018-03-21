package com.coremedia.livecontext.ecommerce.hybris.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoStoreContextAvailable;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnauthorizedException;
import com.coremedia.livecontext.ecommerce.hybris.common.AbstractHybrisService;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.AccessToken;
import com.coremedia.livecontext.ecommerce.hybris.rest.OAuthConnector;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PreviewTokenDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.PreviewTokenResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class PreviewTokenService extends AbstractHybrisService {

  private static final Logger LOG = LoggerFactory.getLogger(PreviewTokenService.class);

  @Autowired
  private OAuthConnector oAuthConnector;

  @Autowired
  private PreviewTokenResource tokenResource;

  @Value("${livecontext.hybris.storeFrontUrl}")
  private String previewStoreFrontUrl;

  private static final String REQUEST_ATTRIB_PREVIEW_TOKEN = PreviewTokenService.class.getName() + "#previewToken";

  protected PreviewTokenDocument getPreviewToken() {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    PreviewTokenDocument result = null;
    if (storeContext != null) {
      HttpServletRequest request = getRequest();
      if (request != null) {
        result = (PreviewTokenDocument) request.getAttribute(REQUEST_ATTRIB_PREVIEW_TOKEN);
      }
      if (result == null) {
        try {
          result = requestPreviewToken(oAuthConnector.getOrRequestAccessToken());

          if (request != null) {
            request.setAttribute(REQUEST_ATTRIB_PREVIEW_TOKEN, result);
          }

        } catch (UnauthorizedException e) {
          LOG.warn("Getting \"Unauthorized\" when requesting the preview token for store context: {}, message: {}. Try again...", storeContext, e.getMessage());
          result = requestPreviewToken(oAuthConnector.renewAccessToken());
        }
      }
    }
    return result;
  }

  private PreviewTokenDocument requestPreviewToken(AccessToken accessToken) {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    if (storeContext == null) {
      throw new NoStoreContextAvailable("requesting preview token");
    }
    return tokenResource.getPreviewToken(preparePreviewTokenParams(storeContext), accessToken);
  }

  public String getPreviewTicketId() {
    return getPreviewToken().getTicketId();
  }

  private Map<String, Object> preparePreviewTokenParams(StoreContext storeContext) {
    /*{
      "catalog" : "apparel-ukContentCatalog",
            "catalogVersion" : "Staged",
            "language" : "en",
            "resourcePath" : "https://127.0.0.1:9002/yacceleratorstorefront?site=apparel-uk",
            "time" : "2016-08-14T16:15:03-0500",
            "user" : "anonymous",
            "userGroup" : "regulargroup"
    }*/
    HashMap<String, Object> result = new HashMap<String, Object>();

    // fetch catalogId from specific bean for CMS-9516 (multi catalog support for hybris)

    result.put("catalog", storeContext.getCatalogId());
    result.put("catalogVersion", storeContext.getCatalogVersion());
    result.put("language", storeContext.getLocale().getLanguage());
    result.put("resourcePath", previewStoreFrontUrl + "?site=" + storeContext.getStoreId());

    return result;
  }

  private HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
    return null;
  }

  public OAuthConnector getoAuthConnector() {
    return oAuthConnector;
  }

  public PreviewTokenResource getTokenResource() {
    return tokenResource;
  }
}
