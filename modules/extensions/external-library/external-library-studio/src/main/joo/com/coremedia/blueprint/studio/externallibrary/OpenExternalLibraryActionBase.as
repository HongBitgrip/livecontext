package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Action;
import ext.Ext;

/**
 * Action for opening the third party content library.
 */
public class OpenExternalLibraryActionBase extends Action {

  private var dataSourceValueExpression:ValueExpression;
  private var dataIndex:Number;

  /**
   * @param config
   */
  public function OpenExternalLibraryActionBase(config:OpenExternalLibraryAction = null) {
    super(config);
    this.dataIndex = config.dataIndex;
    dataSourceValueExpression = ValueExpressionFactory.create('dataSource', beanFactory.createLocalBean());
  }

  override protected function handle():void {
    var window:ExternalLibraryWindow = Ext.getCmp('externalLibrary') as ExternalLibraryWindow;
    if (!window) {
      window = new ExternalLibraryWindow(ExternalLibraryWindow({dataIndex:dataIndex}));
      window.show();
    }
  }
}
}