package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;

public class StoreContextProviderMock implements StoreContextProvider {

  @Nullable
  @Override
  public StoreContext findContextBySiteId(@Nonnull String siteId) {
    if (!"Helios".equals(siteId)) {
      throw new InvalidContextException("Could not find context for " + siteId);
    }

    return createContext();
  }

  @Override
  @Nullable
  public StoreContext findContextBySite(Site site) {
    if (!"Helios".equals(site.getName())) {
      throw new InvalidContextException("Could not find context for " + site.getName());
    }

    return createContext();
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

  private StoreContext createContext(String configId, String storeId, String storeName, String catalogId, String locale,
                                     String currency) {
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

    if (locale != null) {
      try {
        context.put(LOCALE, LocaleUtils.toLocale(locale));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }

    if (currency != null) {
      try {
        context.put(CURRENCY, Currency.getInstance(currency));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }

    return context;
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
