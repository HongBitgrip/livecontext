package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMMarketingSpotImplTest {

  private static final String MY_MARKETING_SPOT_NAME = "myMarketingSpotName";

  @Mock
  private CommerceConnection connection;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private Content content;

  @Mock
  private MarketingSpot marketingSpot;

  @Mock
  private MarketingSpotService marketingSpotService;

  @Mock
  private LiveContextNavigationFactory liveContextNavigationFactory;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Mock
  private SitesService sitesService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private Site site;

  @Mock
  private CommerceObject commerceObject;

  @Mock
  private Product product;

  @Mock
  private Category category;

  @Mock
  private ProductInSite productInSite;

  @Mock
  private CategoryInSite categoryInSite;

  private CMMarketingSpotImpl testling;

  @Before
  public void init() {
    initMocks(this);
    when(commerceConnectionSupplier.findConnectionForContent(any(Content.class))).thenReturn(Optional.of(connection));

    when(connection.getStoreContext()).thenReturn(storeContext);
    when(connection.getCommerceBeanFactory()).thenReturn(commerceBeanFactory);
    when(connection.getMarketingSpotService()).thenReturn(marketingSpotService);

    when(marketingSpotService.findMarketingSpotById(any(), any(StoreContext.class))).thenReturn(marketingSpot);
    when(marketingSpotService.withStoreContext(storeContext)).thenReturn(marketingSpotService);
    when(marketingSpot.getName()).thenReturn(MY_MARKETING_SPOT_NAME);

    when(content.getString(CMMarketingSpotImpl.EXTERNAL_ID)).thenReturn("test:///me/marketingspot/myExternalId");

    testling = new TestCMMarketingSpotImpl();
    testling.setSitesService(sitesService);
    testling.setLiveContextNavigationFactory(liveContextNavigationFactory);
    testling.setCommerceConnectionSupplier(commerceConnectionSupplier);
    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    when(contentSiteAspect.findSite()).thenReturn(Optional.ofNullable(site));
  }

  @Test
  public void testGetItems() {
    assertThat(testling.getItems()).as("There should be no item").isEmpty();

    List<CommerceObject> entities = newArrayList(commerceObject, product, category);
    when(marketingSpot.getEntities()).thenReturn(entities);
    when(liveContextNavigationFactory.createProductInSite(product, site)).thenReturn(productInSite);
    when(liveContextNavigationFactory.createCategoryInSite(category, site)).thenReturn(categoryInSite);

    List<CommerceObject> items = testling.getItems();
    assertThat(items).as("There should be 3 items").hasSize(3);
    assertThat(items.get(0)).isEqualTo(commerceObject);
    assertThat(items.get(1)).isEqualTo(productInSite);
    assertThat(items.get(2)).isEqualTo(categoryInSite);
  }

  @Test
  public void testGetTeaserTitle() {
    //teaser title is set
    String myTeaserTitle = "myTeaserTitle";
    when(content.getString(CMTeasable.TEASER_TITLE)).thenReturn(myTeaserTitle);
    assertThat(testling.getTeaserTitle())
            .as("the teaser title of the CMMarketingSpot is the same as the teaserTitle string property of the content")
            .isEqualTo(myTeaserTitle);

    //teaser title is not set
    when(content.getString(CMTeasable.TEASER_TITLE)).thenReturn(null);
    assertThat(testling.getTeaserTitle())
            .as("the teaser title of the CMMarketingSpot is the same as the teaserTitle string property of the content")
            .isEqualTo(MY_MARKETING_SPOT_NAME);
  }

  private class TestCMMarketingSpotImpl extends CMMarketingSpotImpl {
    @Override
    public Content getContent() {
      return content;
    }
  }
}
