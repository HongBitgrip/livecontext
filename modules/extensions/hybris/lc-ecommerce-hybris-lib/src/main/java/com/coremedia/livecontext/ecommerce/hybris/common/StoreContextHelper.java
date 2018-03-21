package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_VERSION;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.PREVIEW_DATE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;

public class StoreContextHelper {

  private StoreContextHelper() {
  }

  /**
   * Adds the given values to a store context.
   * All potential values are possible. You can use a "null" value to omit single values.
   *
   * @param storeId        the store id or null
   * @param storeName      the store name or null
   * @param catalogId      the catalog id or null
   * @param locale         the locale id or null
   * @param currency       the currency id or null
   * @param catalogVersion the catalogVersion or null
   * @return the new built store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if locale or currency has wrong format
   */
  @Nonnull
  public static StoreContext createContext(@Nullable String configId, @Nullable String storeId,
                                           @Nullable String storeName, @Nullable String catalogId,
                                           @Nullable Locale locale, @Nullable String currency,
                                           @Nullable String catalogVersion) {
    StoreContext context = StoreContextImpl.newStoreContext();

    if (configId != null) {
      if (StringUtils.isBlank(configId)) {
        throw new InvalidContextException("configId has wrong format: \"" + storeId + "\"");
      }
      context.put(CONFIG_ID, configId);
    }

    if (storeId != null) {
      if (StringUtils.isBlank(storeId)) {
        throw new InvalidContextException("storeId has wrong format: \"" + storeId + "\"");
      }
      context.put(STORE_ID, storeId);
    }

    if (storeName != null) {
      if (StringUtils.isBlank(storeName)) {
        throw new InvalidContextException("storeName has wrong format: \"" + storeId + "\"");
      }
      context.put(STORE_NAME, storeName);
    }

    if (catalogId != null) {
      if (StringUtils.isBlank(catalogId)) {
        throw new InvalidContextException("catalogId has wrong format: \"" + catalogId + "\"");
      }
      context.put(CATALOG_ID, catalogId);
    }

    if (locale != null) {
        context.put(LOCALE, locale);
    }

    if (currency != null) {
      try {
        context.put(CURRENCY, Currency.getInstance(currency));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }

    if (catalogVersion != null) {
      if (StringUtils.isBlank(catalogVersion)) {
        throw new InvalidContextException("catalogVersion has wrong format: \"" + catalogVersion + "\"");
      }
      context.put(CATALOG_VERSION, catalogVersion);
    }

    return context;
  }

  /**
   * Set the given store context in the current request (thread).
   * <p>
   * Read the current context with #getCurrentContext().
   *
   * @param context the current context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException
   */
  public static void setCurrentContext(@Nonnull StoreContext context) {
    CurrentCommerceConnection.get().setStoreContext(context);
  }

  /**
   * Gets the current store context within the current request (thread),
   * or nothing if not set.
   * <p>
   * Set the current context with #setCurrentContext();
   *
   * @return the current store context, or nothing
   */
  @Nonnull
  public static Optional<StoreContext> findCurrentContext() {
    return CurrentCommerceConnection.find().map(CommerceConnection::getStoreContext);
  }

  /**
   * Gets the current store context within the current request (thread).
   * <p>
   * Set the current context with #setCurrentContext();
   *
   * @return the current store context, or {@code null}
   */
  @Nullable
  public static StoreContext getCurrentContext() {
    return findCurrentContext().orElse(null);
  }

  /**
   * Gets the current store context within the current request (thread),
   * or throws an exception if not set.
   * <p>
   * Set the current context with #setCurrentContext();
   *
   * @return the current store context
   */
  @Nonnull
  public static StoreContext getCurrentContextOrThrow() {
    return findCurrentContext().orElseThrow(() -> new InvalidContextException("Current store context not available"));
  }

  public static String getCatalogId() {
    return getCatalogId(getCurrentContextOrThrow());
  }

  public static String getCatalogVersion() {
    return getCatalogVersion(getCurrentContextOrThrow());
  }

  public static String getCatalogId(@Nonnull StoreContext storeContext) {
    return storeContext.getCatalogId();
  }

  public static String getCatalogVersion(@Nonnull StoreContext storeContext) {
    return storeContext.getCatalogVersion();
  }

  public static void setSiteId(@Nullable StoreContext context, String siteId) {
    if (context != null) {
      context.put("site", siteId);
    }
  }

  @Nonnull
  public static String getStoreId(@Nonnull StoreContext context) {
    Object value = context.get(STORE_ID);

    if (!(value instanceof String)) {
      throw new InvalidContextException("missing " + STORE_ID + " (" + context + ")");
    }

    return (String) value;
  }

  @Nonnull
  public static String getStoreId() {
    return getStoreId(getCurrentContextOrThrow());
  }

  @Nonnull
  public static String getStoreNameInLowerCase(@Nonnull StoreContext context) {
    return getStoreName(context).toLowerCase();
  }

  @Nonnull
  public static String getStoreName(@Nonnull StoreContext context) {
    Object value = context.get(STORE_NAME);

    if (!(value instanceof String)) {
      throw new InvalidContextException("missing " + STORE_NAME + " (" + context + ")");
    }

    return (String) value;
  }

  @Nonnull
  public static Locale getLocale(@Nonnull StoreContext context) {
    Object value = context.get(LOCALE);

    if (!(value instanceof Locale)) {
      throw new InvalidContextException("missing " + LOCALE + " (" + context + ")");
    }

    return (Locale) value;
  }

  @Nonnull
  public static Locale getLocale() {
    return getLocale(getCurrentContextOrThrow());
  }

  public static String getPreviewDate(@Nonnull StoreContext context) {
    return (String) context.get(PREVIEW_DATE);
  }
}
