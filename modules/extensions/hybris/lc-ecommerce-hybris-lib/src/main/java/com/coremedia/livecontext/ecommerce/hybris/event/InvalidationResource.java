package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

public class InvalidationResource {

  private static final String INVALIDATIONS_PATH = "/cacheinvalidation/{timestamp}";

  private HybrisRestConnector connector;

  public InvalidationsDocument getInvalidations(long timestamp, int maxWaitInMilliseconds, int chunkSize) {
    List<String> uriTemplateParameters = Collections.singletonList(timestamp + "");
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("maxWait", maxWaitInMilliseconds + "");
    queryParams.add("chunkSize", chunkSize + "");
    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();
    queryParams.add("site", StoreContextHelper.getStoreId(storeContext));
    return connector.performGet(INVALIDATIONS_PATH, storeContext, InvalidationsDocument.class, uriTemplateParameters, queryParams, true);
  }

  @Required
  public void setConnector(HybrisRestConnector connector) {
    this.connector = connector;
  }

  public HybrisRestConnector getConnector() {
    return connector;
  }
}
