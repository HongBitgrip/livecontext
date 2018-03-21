package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;

import javax.annotation.Nonnull;

public class HybrisCommerceIdProvider extends BaseCommerceIdProvider {

  static final Vendor HYBRIS = Vendor.of("hybris");

  public HybrisCommerceIdProvider() {
    super(HYBRIS);
  }

  public static boolean isHybrisId(@Nonnull CommerceId commerceId) {
    return HYBRIS.equals(commerceId.getVendor());
  }

  @Nonnull
  public static CommerceIdBuilder commerceId(@Nonnull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(HYBRIS, beanType);
  }
}
