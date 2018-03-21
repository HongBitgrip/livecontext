package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;

public class StoreContextProviderMock implements StoreContextProvider {

  @Nullable
  @Override
  public StoreContext findContextBySiteId(@Nonnull String siteId) {
    if (siteId.equals("Helios")) {
      return createContext();
    } else {
      throw new InvalidContextException("Could not find context for " + siteId);
    }
  }

  @Override
  @Nullable
  public StoreContext findContextBySite(Site site) {
    if ("Helios".equals(site.getName())) {
      return createContext();
    } else {
      throw new InvalidContextException("Could not find context for " + site.getName());
    }
  }

  @Override
  @Nullable
  public StoreContext findContextByContent(@Nullable Content content) {
    return createContext();
  }

  @Nullable
  @Override
  public StoreContext createContext(@Nonnull Site site) {
    return createContext();
  }

  private StoreContext createContext() {
    return createContext("myConfigId", "10001", "aurora", "10001", "en_US", "USD");
  }

  private StoreContext createContext(@Nullable String configId, @Nullable String storeId, @Nullable String storeName,
                                     String catalogId, @Nullable String localeStr, @Nullable String currency) {
    StoreContext context = newStoreContext();

    if (configId != null) {
      context.put(CONFIG_ID, parseString(configId, CONFIG_ID));
    }

    if (storeId != null) {
      context.put(STORE_ID, parseString(storeId, STORE_ID));
    }

    if (storeName != null) {
      context.put(STORE_NAME, parseString(storeName, STORE_NAME));
    }

    if (localeStr != null) {
      context.put(LOCALE, parseLocale(localeStr));
    }

    if (currency != null) {
      context.put(CURRENCY, parseCurrency(currency));
    }

    return context;
  }

  @Nonnull
  private static String parseString(@Nonnull String str, @Nonnull String description) {
    if (StringUtils.isBlank(str)) {
      throw new InvalidContextException("'" + description + "' has wrong format: \"" + str + "\"");
    }

    return str;
  }

  @Nonnull
  private static Locale parseLocale(@Nonnull String localeStr) {
    return LocaleHelper.parseLocaleFromString(localeStr)
            .orElseThrow(() -> new InvalidContextException("Locale '" + localeStr + "' is not valid."));
  }

  @Nonnull
  private static Currency parseCurrency(@Nonnull String currencyStr) {
    try {
      return Currency.getInstance(currencyStr);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }
  }

  @Nonnull
  @Override
  public StoreContextBuilder buildContext(@Nonnull StoreContext source) {
    return new StoreContextBuilderImpl().from(source);
  }

  @Nonnull
  @Override
  public StoreContext cloneContext(@Nonnull StoreContext source) {
    return source.getClone();
  }
}
