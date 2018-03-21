package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.StoreConfigDocument;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

/**
 * Resource representing a store configuration.
 */
public class StoreConfigResource extends AbstractHybrisResource {

  private final static String STORES_CONFIG_PATH = "/store/storeConfigs";

  /**
   * Returns a list of store configuration documents.
   *
   * @return complete configuration
   */
  public List<StoreConfigDocument> getStoreConfigs() {
    StoreConfigDocument[] docs = getConnector().performGet(STORES_CONFIG_PATH, StoreContextHelper.getCurrentContext(), StoreConfigDocument[].class);
    return Arrays.asList(docs);
  }

  /**
   * Returns the store configuration for the given store code.
   *
   * @param storeCode store code
   * @return given configuration
   */
  public StoreConfigDocument getStoreConfig(String storeCode) {
    MultiValueMap queryParams = new LinkedMultiValueMap();
    queryParams.add("storeCodes[]", storeCode); // Must be provided as an array query param

    StoreConfigDocument[] docs = getConnector().performGet(STORES_CONFIG_PATH, StoreContextHelper.getCurrentContext(), StoreConfigDocument[].class, null, queryParams, false);
    if (docs != null && docs.length > 0) {
      return docs[0];
    }

    return null;
  }

}
