package com.coremedia.blueprint.common.navigation.context.selector;

import com.coremedia.blueprint.base.navigation.context.selector.ContextSelector;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * A ContextSelector on ContentBean layer
 * <p>
 * Only responsible for contentbean and dataview wrapping,
 * the actual selection is done by a UAPI based delegate.
 */
public class LinkableBeanContextSelector implements ContextSelector<CMContext> {

  private final ContextSelector<Content> delegate;
  private final ContentBeanFactory contentBeanFactory;
  private final DataViewFactory dataViewFactory;

  public LinkableBeanContextSelector(ContextSelector<Content> cs, ContentBeanFactory contentBeanFactory, DataViewFactory dataViewFactory) {
    this.delegate = cs;
    this.contentBeanFactory = contentBeanFactory;
    this.dataViewFactory = dataViewFactory;
  }

  @Override
  public CMContext selectContext(CMContext currentContext, List<? extends CMContext> candidates) {
    Content content = currentContext != null ? currentContext.getContent() : null;
    Content context = delegate.selectContext(content, Lists.transform(candidates, new CMContextToContentTransformer()));
    return context != null ? dataViewFactory.loadCached(contentBeanFactory.createBeanFor(context, CMContext.class), null) : null;
  }

  private static class CMContextToContentTransformer implements Function<CMContext, Content> {
    @Override
    public Content apply(CMContext input) {
      return input.getContent();
    }
  }
}
