package com.coremedia.blueprint.cae.view.viewtype;

import com.coremedia.blueprint.cae.ContentBeanTestBase;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import org.junit.Assert;
import org.junit.Test;

public class ViewTypeRenderNodeDecoratorTest extends ContentBeanTestBase {

  private ViewTypeRenderNodeDecorator blueprintRenderNodeDecorator = new ViewTypeRenderNodeDecorator();

  @Test
  public void testDecorateViewName() {
    CMChannel channel = getContentBean(10);
    Assert.assertEquals("detail[viewtype]", blueprintRenderNodeDecorator.decorateViewName(channel, "detail"));
  }

  @Test
  public void testDoNotDecorateViewName() {
    CMChannel channel = getContentBean(10);
    Assert.assertEquals("detail[myCustomViewtype]", blueprintRenderNodeDecorator.decorateViewName(channel, "detail[myCustomViewtype]"));
  }

  @Test
  public void testDecorateViewNameIsNull() {
    CMChannel channel = getContentBean(10);
    Assert.assertEquals("[viewtype]", blueprintRenderNodeDecorator.decorateViewName(channel, null));
  }
}
