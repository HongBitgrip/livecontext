package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;

public class HybrisCommerceConnection extends BaseCommerceConnection {
  public HybrisCommerceConnection() {
    setVendor(HybrisCommerceIdProvider.HYBRIS);
  }
}
