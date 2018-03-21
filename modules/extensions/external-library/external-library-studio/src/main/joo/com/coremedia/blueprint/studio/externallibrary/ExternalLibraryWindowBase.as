package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.cms.editor.sdk.components.StudioDialog;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.util.EventUtil;

import ext.LoadMask;

/**
 * The base class of the external content library window, creates all value
 * expression for event handling between the panels.
 */
[ResourceBundle('com.coremedia.blueprint.studio.ExternalLibraryStudioPlugin')]
public class ExternalLibraryWindowBase extends StudioDialog {

  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  private var selectedValueExpression:ValueExpression;
  private var loadMask:LoadMask;

  public function ExternalLibraryWindowBase(config:ExternalLibraryWindow = null) {
    super(config);
  }


  override protected function afterRender():void {
    super.afterRender();

    var loadMaskCfg:LoadMask = LoadMask({
      target: this
    });
    loadMaskCfg.msg = resourceManager.getString('com.coremedia.blueprint.studio.ExternalLibraryStudioPlugin', 'ExternalLibraryWindow_list_loading');
    loadMask = new LoadMask(loadMaskCfg);
    loadMask.disable();
  }

  public function setBusy(busy:Boolean):void {
    if(busy) {
      loadMask.show();
    }
    else {
      EventUtil.invokeLater(function():void {
        loadMask.hide();
      });
    }
  }

  /**
   * Returns the value expression that contains the current filter string value.
   * @return
   */
  protected function getFilterValueExpression():ValueExpression {
    if(!filterValueExpression) {
      filterValueExpression = ValueExpressionFactory.create('searchFilter', beanFactory.createLocalBean());
    }
    return filterValueExpression;
  }

  /**
   * Returns the value expression that contains the current selected external data source record.
   * @return
   */
  protected function getDataSourceValueExpression():ValueExpression {
    if(!dataSourceValueExpression) {
      dataSourceValueExpression = ValueExpressionFactory.create('dataSource', beanFactory.createLocalBean());
    }
    return dataSourceValueExpression;
  }

  /**
   * Returns the value expression that contains selected list record.
   * @return
   */
  protected function getSelectedValueExpression():ValueExpression {
    if(!selectedValueExpression) {
      selectedValueExpression = ValueExpressionFactory.create('selectedValue', beanFactory.createLocalBean());
    }
    return selectedValueExpression;
  }
}
}