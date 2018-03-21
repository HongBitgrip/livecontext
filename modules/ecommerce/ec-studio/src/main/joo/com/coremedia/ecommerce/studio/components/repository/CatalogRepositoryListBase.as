package com.coremedia.ecommerce.studio.components.repository {
import com.coremedia.cms.editor.sdk.collectionview.CollectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ecommerce.studio.components.AbstractCatalogList;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.RemoteBeanUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.EventUtil;

import ext.grid.column.Column;
import ext.grid.header.HeaderContainer;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
public class CatalogRepositoryListBase extends AbstractCatalogList {

  private var selectedNodeExpression:ValueExpression;
  private var sortInfo:Object = {};


  public function CatalogRepositoryListBase(config:CatalogRepositoryList = null) {
    super(config);
    on('afterrender', bindStoreAndView);
    on("containerclick", clearSelection);
  }


  override protected function beforeDestroy():void {
    if (selectedNodeExpression) {
      selectedNodeExpression.removeChangeListener(selectionChanged);
    }
    super.beforeDestroy();
  }

  internal function getSelectedNodeExpression():ValueExpression {
    if (!selectedNodeExpression) {
      selectedNodeExpression = ComponentContextManager.getInstance().getContextExpression(this, CollectionView.SELECTED_FOLDER_VARIABLE_NAME);
      selectedNodeExpression.addChangeListener(selectionChanged);
    }

    return selectedNodeExpression;
  }

  internal function createSelectedItemsValueExpression():ValueExpression {
    return ComponentContextManager.getInstance().getContextExpression(this, CollectionView.SELECTED_REPOSITORY_ITEMS_VARIABLE_NAME);
  }

  protected function clearSelection():void {
    selectedItemsValueExpression.setValue([]);
  }

  private function selectionChanged():void {
    var value:RemoteBean = selectedNodeExpression.getValue();
    if(value is Marketing) {
      getView()['emptyText'] = resourceManager.getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'CatalogView_spots_selection_empty_text');
    }
    else {
      getView()['emptyText'] = resourceManager.getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'CatalogView_empty_text');
    }
    getView().refresh();
  }

  private function bindStoreAndView():void {
    on('sortchange', sortChanged);
    getCatalogItemsValueExpression().addChangeListener(catalogItemsChanged);
    // TODO Ext 6 list sorters, see CMS-7895
    // getStore().setDefaultSort('id', 'ASC');
  }

  //noinspection JSUnusedLocalSymbols
  private function sortChanged(headerContainer:HeaderContainer, column:Column, direction:String, eOpts:Object):void {
    sortInfo.field = column.getSortParam();
    sortInfo.direction = direction;
    if (sortInfo.field === 'name'|| sortInfo.direction === "DESC") {
      loadCurrentBeans();
    }
  }

  private function catalogItemsChanged():void {
    if (sortInfo.field === "name" || sortInfo.direction === "DESC") {
      loadCurrentBeans();
    }
  }

  private function loadCurrentBeans():void {
    // start loading all RemoteBeans in this view...
    // and afterwards sort the Store...
    var beans:Array = getCatalogItemsValueExpression().getValue();
    if (beans && beans.length > 0) {
      RemoteBeanUtil.loadAll(function():void {
        EventUtil.invokeLater(function():void {
          getStore().sort(sortInfo.field, sortInfo.direction);
        });
      }, beans);
    }
  }

  internal function getCatalogItemsValueExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Array {
      return CatalogHelper.getInstance().getChildren(getSelectedNodeExpression().getValue());
    });
  }

}
}