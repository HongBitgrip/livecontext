package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LiveContextNavigationFactoryTest {

  private LiveContextNavigationFactory testling;

  @Mock
  private CatalogService catalogService;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private LiveContextNavigationTreeRelation treeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  @Mock
  private Content content;

  @Mock
  private LiveContextExternalChannelImpl externalChannel;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private CommerceConnection connection;

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ValidationService validationService;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    CurrentCommerceConnection.set(connection);

    when(connection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(connection.getCatalogService()).thenReturn(catalogService);

    testling = new LiveContextNavigationFactory();
    testling.setTreeRelation(treeRelation);
    testling.setSitesService(sitesService);
    testling.setAugmentationService(augmentationService);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setValidationService(validationService);
    when(site.getId()).thenReturn("deadbeef");
    when(sitesService.getContentSiteAspect(content)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.findSite()).thenReturn(Optional.ofNullable(site));

    when(contentBeanFactory.createBeanFor(content)).thenReturn(externalChannel);
    when(validationService.validate(externalChannel)).thenReturn(true);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testCreateNavigationWithValidCategory() throws Exception {
    Category categoryToCreateFrom = mock(Category.class);

    LiveContextNavigation actual = testling.createNavigation(categoryToCreateFrom, site);
    assertThat(actual).as("The returned Navigation must not be null").isNotNull();

    Category categoryInNavigation = actual.getCategory();
    assertThat(categoryInNavigation)
            .as("The created LiveContextNavigation is expected to contain the category given by the first parameter of this method")
            .isSameAs(categoryToCreateFrom);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNavigationWithCategoryIsNull() {
    testling.createNavigation(null, site);
  }

  @Test
  public void testCreateNavigationWithAugmentingContent() throws Exception {
    Category categoryToCreateFrom = mock(Category.class);
    when(augmentationService.getContent(categoryToCreateFrom)).thenReturn(content);

    LiveContextNavigation actual = testling.createNavigation(categoryToCreateFrom, site);
    assertThat(actual).as("The returned Navigation must not be null").isNotNull();
    assertThat(actual).isInstanceOf(LiveContextExternalChannelImpl.class);
  }

  @Test
  public void testCreateNavigationBySeoSegment() throws Exception {
    String existingSeoSegment = "existingSeoSegment";
    StoreContext storeContext = mock(StoreContext.class);
    Category category = mock(Category.class);

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);
    when(catalogService.findCategoryBySeoSegment(existingSeoSegment, storeContext)).thenReturn(category);

    LiveContextNavigation actual = testling.createNavigationBySeoSegment(content, existingSeoSegment);
    assertThat(actual).as("The returned Navigation must not be null").isNotNull();
    assertThat(actual.getCategory())
            .as("The created LiveContextNavigation is expected to contain the category given by the first parameter of this method")
            .isSameAs(category);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentContentWithoutContext() throws Exception {
    Content content = mock(Content.class);
    String existingSeoSegment = "existingSeoSegment";

    when(storeContextProvider.findContextByContent(content)).thenReturn(null);

    testling.createNavigationBySeoSegment(content, existingSeoSegment);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentNoValidSeoSegment() throws Exception {
    Content content = mock(Content.class);
    String notExistingSeoSegment = "notExistingSeoSegment";
    StoreContext storeContext = mock(StoreContext.class);

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);
    when(catalogService.findCategoryBySeoSegment(notExistingSeoSegment, storeContext)).thenReturn(null);

    testling.createNavigationBySeoSegment(content, notExistingSeoSegment);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateNavigationBySeoSegmentInvalidContextException() throws Exception {
    Content invalidContent = mock(Content.class);
    String anySeo = "anySeo";
    when(storeContextProvider.findContextByContent(invalidContent)).thenThrow(InvalidContextException.class);
    testling.createNavigationBySeoSegment(invalidContent, anySeo);
  }

  @Test(expected = CommerceException.class)
  public void testCreateNavigationBySeoSegmentCommerceException() throws Exception {
    Content content = mock(Content.class);
    String anySeoSegment = "anySeoSegment";
    StoreContext storeContext = mock(StoreContext.class);

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);
    when(catalogService.findCategoryBySeoSegment(anySeoSegment, storeContext)).thenThrow(CommerceException.class);

    testling.createNavigationBySeoSegment(content, anySeoSegment);
  }
}
