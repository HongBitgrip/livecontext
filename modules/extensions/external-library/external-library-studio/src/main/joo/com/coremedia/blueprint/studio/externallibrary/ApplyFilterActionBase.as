package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;
import ext.Ext;

/**
 * Applies the filter string of the filter area, displays all list items afterwards.
 */
public class ApplyFilterActionBase extends Action {
  //noinspection JSFieldCanBeLocal
  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  internal native function get items():Array;

  /**
   * @param config
   */
  public function ApplyFilterActionBase(config:ApplyFilterAction = null) {
    filterValueExpression = config.filterValueExpression;
    dataSourceValueExpression = config.dataSourceValueExpression;
    dataSourceValueExpression.addChangeListener(updateDisabled);
    //noinspection JSUnusedGlobalSymbols
    super(Action(Ext.apply({
      handler: function():void {
        var filter:FilterToolbar = Ext.getCmp('externalLibraryFilterToolbar') as FilterToolbar;
        filter.applyFilter();
      }
    }, config)));
  }


  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    updateDisabled();
  }

  private function updateDisabled():void {
    setDisabled(!dataSourceValueExpression.getValue());
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      dataSourceValueExpression && dataSourceValueExpression.removeChangeListener(updateDisabled);
    }
  }
}
}
