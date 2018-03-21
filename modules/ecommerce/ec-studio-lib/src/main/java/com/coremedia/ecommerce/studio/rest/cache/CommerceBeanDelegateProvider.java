package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.springframework.web.util.UriUtils;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Helper class to provide maps that can be used as delegates for commerce beans satisfying the needs of
 * Studio REST resource linker.
 */
class CommerceBeanDelegateProvider {

  private static final String SITE_ID = "{siteId:.*}";
  private static final String WORKSPACE_ID = "{workspaceId:.*}";
  private static final String ID = "{id:.*}";
  private static final CatalogAlias CATALOG_ALIAS_TEMPLATE_VAR = CatalogAlias.of("CATALOG_ALIAS_TEMPLATE_VAR");
  private static final String CATALOG_ALIAS = "{catalogAlias:.*}";

  private static final CommerceId TEMPLATE_COMMERCE_ID = CommerceIdBuilder.builder(Vendor.of("none"), "none", BaseCommerceBeanType.CATALOG)
          .withExternalId(ID)
          .withCatalogAlias(CATALOG_ALIAS_TEMPLATE_VAR)
          .build();

  private static final ImmutableMap<String, Object> COMMERCE_BEAN_DELEGATE = ImmutableMap.of(
          "id", TEMPLATE_COMMERCE_ID,
          "externalId", ID
  );

  @Nonnull
  static Map<String, Object> get() {
    StoreContext storeContext = createStoreContext();
    return ImmutableMap.<String, Object>builder()
            .putAll(COMMERCE_BEAN_DELEGATE)
            .put("context", storeContext)
            .build();
  }

  @Nonnull
  static StoreContext createStoreContext() {
    StoreContext storeContext = StoreContextImpl.newStoreContext();
    storeContext.setSiteId(SITE_ID);
    storeContext.setWorkspaceId(WORKSPACE_ID);
    return storeContext;
  }

  @Nonnull
  static String postProcess(@Nonnull String commerceBeanUri) {
    return commerceBeanUri.replace(CATALOG_ALIAS_TEMPLATE_VAR.value(), CATALOG_ALIAS);
  }

  @Nonnull
  static String forEncodedExternalId(@Nonnull String commerceBeanUri, @Nonnull CommerceId commerceId) {
    return commerceId.getExternalId()
            .map(CommerceBeanDelegateProvider::encodePartNumber)
            .map(encodedPartNumber -> commerceBeanUri.replace(ID, encodedPartNumber))
            .orElse(commerceBeanUri);
  }

  @Nonnull
  @VisibleForTesting
  static String encodePartNumber(@Nonnull String partNumber) {
    try {
      return UriUtils.encodePath(partNumber, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }
}
