package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.transform.TransformImageService;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HttpError;
import com.coremedia.transform.TransformedBlob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMCatalogPictureHandlerTest {
  @InjectMocks
  private ProductCatalogPictureHandler testling = new ProductCatalogPictureHandler();

  @Mock
  private LiveContextSiteResolver siteResolver;
  @Mock
  private Site site;
  @Mock
  private ContentBeanFactory contentBeanFactory;
  @Mock
  private AssetService assetService;
  @Mock
  private TransformImageService transformImageService;
  @Mock
  private CMPicture picture;
  @Mock
  private Content pictureContent;
  @Mock
  private TransformedBlob blob;
  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  private static final CommerceId PRODUCT_REFERENCE = CommerceIdParserHelper.parseCommerceIdOrThrow("vendor:///catalog/product/PC_SUMMER_DRESS");


  @Before
  public void setUp() throws Exception {
    Map<String, String> pictureFormats = new HashMap<>();
    pictureFormats.put("thumbnail", "portrait_ratio20x31/200/310");
    pictureFormats.put("full", "portrait_ratio20x31/646/1000");
    testling.setPictureFormats(pictureFormats);

    when(contentBeanFactory.createBeanFor(pictureContent, CMPicture.class)).thenReturn(picture);
    when(picture.getTransformedData(anyString())).thenReturn(blob);

    testling.setTransformImageService(transformImageService);

    testling.setCatalogAliasTranslationService(catalogAliasTranslationService);
  }


  @Test
  public void testHandleRequestWithSiteNull() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(null);

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", PRODUCT_REFERENCE, "jpg", mock(WebRequest.class)
    );
    assert404(result);
  }

  @Test
  public void testHandleRequestWithPictureFormatsEmpty() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);
    testling.setPictureFormats(Collections.<String, String>emptyMap());

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", PRODUCT_REFERENCE, "jpg", mock(WebRequest.class)
    );
    assert404(result);
  }

  @Test
  public void testHandleRequestNoPictureFound() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", PRODUCT_REFERENCE, "jpg", mock(WebRequest.class)
    );
    assert404(result);
  }

  @Test
  public void testHandleRequestSuccess() throws Exception {
    prepareSuccessRequest();

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", PRODUCT_REFERENCE, "jpg", mock(WebRequest.class)
    );
    assert200(result);
  }

  @Test
  public void testHandleRequestSuccessCached() throws Exception {
    WebRequest request = mock(WebRequest.class);
    when(request.checkNotModified(nullable(String.class))).thenReturn(true);

    prepareSuccessRequest();

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", PRODUCT_REFERENCE, "jpg", request
    );
    assert304(result);
  }

  @Test
  public void testResolveCatalogAliasFromId() {
    StoreContext storeContext = mock(StoreContext.class);
    when(storeContext.getSiteId()).thenReturn("siteId");
    when(storeContext.getCatalogAlias()).thenReturn(DEFAULT_CATALOG_ALIAS);
    when(catalogAliasTranslationService.getCatalogAliasForId(CatalogId.of("catalogId"), "siteId")).thenReturn(Optional.of(CatalogAlias.of("catalogAlias")));

    CatalogAlias catalogAlias = testling.resolveCatalogAliasFromId("catalogId", storeContext);
    assertEquals(CatalogAlias.of("catalogAlias"), catalogAlias);

    CatalogAlias catalogAliasNotFound = testling.resolveCatalogAliasFromId("unknownId", storeContext);
    assertEquals(DEFAULT_CATALOG_ALIAS, catalogAliasNotFound);
  }

  private void prepareSuccessRequest() {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);
    List<Content> cmPictures = new ArrayList<>();
    cmPictures.add(pictureContent);
    when(assetService.findPictures(PRODUCT_REFERENCE, true)).thenReturn(cmPictures);
    when(transformImageService.transformWithDimensions(any(Content.class), nullable(Blob.class), any(TransformedBlob.class), anyString(), anyString(), anyInt(), anyInt()))
            .thenReturn(mock(Blob.class));
  }

  private void assert404(ModelAndView result) {
    assertNotNull(result.getModel().get("self"));
    assertTrue(result.getModel().get("self") instanceof HttpError);
    HttpError error = (HttpError) result.getModel().get("self");
    assertEquals(404, error.getErrorCode());
  }

  private void assert304(ModelAndView result) {
    assertNull(result);
  }

  private void assert200(ModelAndView result) {
    assertNotNull(result.getModel().get("self"));
    assertTrue(result.getModel().get("self") instanceof Blob);
  }
}
