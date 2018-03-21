package com.coremedia.blueprint.lc.test;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

public interface TestConfig {

  StoreContext getStoreContext();

  StoreContext getGermanStoreContext();

  String getConnectionId();

  String getCatalogName();

  String getStoreName();
}
