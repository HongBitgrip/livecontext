package com.coremedia.livecontext.ecommerce.hybris;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.lc.test.BetamaxTestHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Locale;

public class HybrisITBase {

  @Inject
  private HybrisRestConnector connector;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());

  @Before
  public void setup() {
    StoreContext storeContext = StoreContextHelper.createContext("configid", "apparel-uk", "Apparel-Catalog",
            "apparelProductCatalog", Locale.ENGLISH, "USD", "Staged");

    CommerceConnection connection = new BaseCommerceConnection();
    connection.setStoreContext(storeContext);

    CurrentCommerceConnection.set(connection);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  protected <T> T performGetWithStoreContext(@Nonnull String resourcePath, @Nonnull Class<T> responseType) {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();

    return connector.performGet(resourcePath, storeContext, responseType);
  }
}
