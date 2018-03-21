package com.coremedia.ecommerce.studio.components.repository {
import com.coremedia.cms.editor.sdk.collectionview.CollectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.container.Container;

public class CatalogRepositoryThumbnailsBase extends Container{
  private var selectedNodeExpression:ValueExpression;
  private var selectedItemsValueExpression:ValueExpression;

  public function CatalogRepositoryThumbnailsBase(config:CatalogRepositoryThumbnails = null) {
    super(config);
  }

  internal function getCatalogItemsValueExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Array {
      return CatalogHelper.getInstance().getChildren(getSelectedNodeExpression().getValue());
    });
  }

  protected function getSelectedItemsValueExpression():ValueExpression {
    if (!selectedItemsValueExpression) {
      selectedItemsValueExpression = ComponentContextManager.getInstance().getContextExpression(this, CollectionView.SELECTED_REPOSITORY_ITEMS_VARIABLE_NAME);
    }
    return selectedItemsValueExpression;
  }

  internal function getSelectedNodeExpression():ValueExpression {
    if (!selectedNodeExpression) {
      selectedNodeExpression = ComponentContextManager.getInstance().getContextExpression(this, CollectionView.SELECTED_FOLDER_VARIABLE_NAME);
    }

    return selectedNodeExpression;
  }

  public function disableBrowserContextMenu():void {
/* TODO Ext6, see CMS-7893
    var thumbViewPanel:* = this.el.down('div.catalog-thumb-data-view-panel');
    thumbViewPanel.on("contextmenu", Ext.emptyFn, null, {
      preventDefault: true
    });
*/
  }

}
}