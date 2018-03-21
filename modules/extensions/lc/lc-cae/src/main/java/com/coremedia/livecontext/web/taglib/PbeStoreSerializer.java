package com.coremedia.livecontext.web.taglib;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.serialization.cap.ParameterBasedLinkableSerializer;

import javax.inject.Named;
import java.util.Map;


/**
 * The StoreSerializer uses {@link org.codehaus.jackson.map.ObjectMapper} (and
 * other chained {@link org.codehaus.jackson.map.JsonSerializer}s too) to serialize objects of
 * type StoreContext into an URI link based on specific parameters. The resulting JSON takes the form:
 * <code>
 *   { "$Ref": $URI_LINK }
 * </code>
 *
 */
@Named
public class PbeStoreSerializer extends ParameterBasedLinkableSerializer<StoreContext> {
  public static final String STORE_URI_TEMPLATE = "livecontext/store/{siteId:[^/]+}/{workspaceId:[^/]+}";

  public PbeStoreSerializer() {
    setUriTemplate(STORE_URI_TEMPLATE);
  }

  @Override
  public void fillLinkParameters(StoreContext storeContext, Map<String, String> params) {
    params.put("siteId", storeContext.getSiteId());
    params.put("workspaceId", String.valueOf(storeContext.getWorkspaceId()));
  }

  @Override
  public Class<StoreContext> handledType() {
    return StoreContext.class;
  }
}
