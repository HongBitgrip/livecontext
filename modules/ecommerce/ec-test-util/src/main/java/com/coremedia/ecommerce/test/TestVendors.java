package com.coremedia.ecommerce.test;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.Vendor;

import javax.annotation.Nonnull;

public class TestVendors {

  @Nonnull
  public static BaseCommerceIdProvider getIdProvider(@Nonnull String vendor) {
    return new BaseCommerceIdProvider(Vendor.of(vendor));
  }

}
