package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.id.IdProvider;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.objectserver.web.HttpError;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CommercePreviewHandlerTest {
  private static final String KNOWN_SITE_ID = "4711";

  @InjectMocks
  private PreviewHandler previewHandler;

  @Mock
  private IdProvider idProvider;

  @SuppressWarnings("unused") // injected into Preview Handler, don't remove
  @Mock
  private LinkFormatter linkFormatter;

  private MockHttpServletRequest request;

  @Before
  public void setup() {
    request = new MockHttpServletRequest();
  }

  @Test
  public void handleProductPreview() throws IOException {
    String productReferenceId = "vendor:///catalog/product/123";
    when(idProvider.parseId(productReferenceId)).thenReturn(mock(Product.class));
    ModelAndView modelAndView = previewHandler.handleId(productReferenceId, "", KNOWN_SITE_ID, "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof Product);
    assertTrue(modelAndView.getViewName().contains("redirect:"));
  }

  @Test
  public void handleCategoryPreview() throws IOException {
    String categoryReferenceId = "vendor:///catalog/category/456";
    when(idProvider.parseId(categoryReferenceId)).thenReturn(mock(Category.class));
    ModelAndView modelAndView = previewHandler.handleId(categoryReferenceId, "", KNOWN_SITE_ID, "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof Category);
    assertTrue(modelAndView.getViewName().contains("redirect:"));
  }

  @Test
  public void handleNoBean() throws IOException {
    when(idProvider.parseId("unknown_id")).thenReturn(mock(IdProvider.UnknownId.class));
    ModelAndView modelAndView = previewHandler.handleId("unknown_id", "", KNOWN_SITE_ID, "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof HttpError);
  }
}

