package com.coremedia.livecontext.elastic.social.worker;

import com.coremedia.elastic.core.api.serializer.TypeConverter;
import com.coremedia.livecontext.commercebeans.ProductInSite;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

@Named
public class SerializedProductConverter implements TypeConverter<SerializedProductInSite> {

  private static final String PRODUCT_ID = "id";
  private static final String SITE_ID = "site";

  @Override
  public Class<SerializedProductInSite> getType() {
    return SerializedProductInSite.class;
  }

  @Override
  public String getSymbolicName() {
    return uncapitalize(ProductInSite.class.getSimpleName());
  }

  @Override
  public void serialize(@Nonnull SerializedProductInSite product, @Nonnull Map<String, Object> serializedObject) {
    serializedObject.put(PRODUCT_ID, product.getProductId());
    serializedObject.put(SITE_ID, product.getSiteId());
  }

  @Override
  @Nonnull
  public SerializedProductInSite deserialize(@Nonnull Map<String, Object> serializedObject) {
    String productId = (String) serializedObject.get(PRODUCT_ID);
    String siteId = (String) serializedObject.get(SITE_ID);
    return new SerializedProductInSite(productId, siteId);
  }

}

