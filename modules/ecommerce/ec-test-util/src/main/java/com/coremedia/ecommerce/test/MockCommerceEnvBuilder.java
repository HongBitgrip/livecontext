package com.coremedia.ecommerce.test;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.util.LocaleHelper;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityService;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.pricing.PriceService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import org.mockito.Mock;

import java.util.Currency;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @deprecated don't use, mock stuff yourself
 */
@Deprecated
public class MockCommerceEnvBuilder {

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private CatalogService catalogService;

  @Mock
  private MarketingSpotService marketingSpotService;

  @Mock
  private PriceService priceService;

  @Mock
  private SegmentService segmentService;

  @Mock
  private WorkspaceService workspaceService;

  @Mock
  private AvailabilityService availabilityService;

  @Mock
  private UserService userService;

  @Mock
  private UserSessionService userSessionService;

  @Mock
  private CartService cartService;

  @Mock
  private AssetService assetService;

  @Mock
  private AssetUrlProvider assetUrlProvider;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  public static MockCommerceEnvBuilder create() {
    return new MockCommerceEnvBuilder();
  }

  public BaseCommerceConnection setupEnv() {
    return setupEnv("vendor");
  }

  public BaseCommerceConnection setupEnv(String vendor) {
    initMocks(this);
    StoreContext storeContext = newStoreContext();

    storeContext.put(StoreContextImpl.CONFIG_ID, "aurora");
    storeContext.put(StoreContextImpl.STORE_ID, "10001");
    storeContext.put(StoreContextImpl.STORE_NAME, "aurora");
    storeContext.put(StoreContextImpl.CATALOG_ID, "catalog");
    storeContext.put(StoreContextImpl.LOCALE, LocaleHelper.getLocaleFromString("en_US"));
    storeContext.put(StoreContextImpl.CURRENCY, Currency.getInstance("USD"));

    when(storeContextProvider.findContextBySite(any())).thenReturn(storeContext);
    when(storeContextProvider.buildContext(any())).thenReturn(new StoreContextBuilderImpl().from(storeContext));
    when(storeContextProvider.cloneContext(any())).thenReturn(storeContext.getClone());

    UserContext userContext = UserContext.builder().build();
    when(userContextProvider.getCurrentContext()).thenReturn(userContext);
    when(userContextProvider.createContext(any())).thenReturn(userContext);

    BaseCommerceConnection commerceConnection = new BaseCommerceConnection();
    commerceConnection.setIdProvider(TestVendors.getIdProvider(vendor));
    commerceConnection.setStoreContextProvider(storeContextProvider);
    commerceConnection.setUserContextProvider(userContextProvider);
    commerceConnection.setCatalogService(catalogService);
    commerceConnection.setCartService(cartService);
    commerceConnection.setMarketingSpotService(marketingSpotService);
    commerceConnection.setPriceService(priceService);
    commerceConnection.setSegmentService(segmentService);
    commerceConnection.setWorkspaceService(workspaceService);
    commerceConnection.setAvailabilityService(availabilityService);
    commerceConnection.setUserService(userService);
    commerceConnection.setUserSessionService(userSessionService);
    commerceConnection.setCommerceBeanFactory(commerceBeanFactory);
    commerceConnection.setAssetService(assetService);
    commerceConnection.setAssetUrlProvider(assetUrlProvider);
    commerceConnection.setStoreContext(storeContext);
    commerceConnection.setUserContext(userContext);
    CurrentCommerceConnection.set(commerceConnection);

    return commerceConnection;
  }

  public void tearDownEnv() {
    CurrentCommerceConnection.remove();
  }
}
