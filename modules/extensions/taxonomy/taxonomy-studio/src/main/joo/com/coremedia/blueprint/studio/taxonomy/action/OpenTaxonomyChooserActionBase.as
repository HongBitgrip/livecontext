package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.taxonomy.chooser.TaxonomySelectionWindow;
import com.coremedia.cms.editor.sdk.util.AccessControlUtil;
import com.coremedia.ui.actions.DependencyTrackedAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Ext;

/**
 * Shows the dialog for choosing taxonomies for a linklist property.
 */
public class OpenTaxonomyChooserActionBase extends DependencyTrackedAction {

  private var propertyValueExpression:ValueExpression;
  private var taxId:String;
  private var singleSelection:Boolean;

  public var bindTo:ValueExpression;
  public var forceReadOnlyValueExpression:ValueExpression;

  /**
   * @param config
   */
  public function OpenTaxonomyChooserActionBase(config:OpenTaxonomyChooserAction = null) {
    propertyValueExpression = config.propertyValueExpression;
    singleSelection = config.singleSelection;
    bindTo = config.bindTo;
    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    taxId = config.taxonomyId;
    config.handler = showChooser;
    super(config);
  }


  override protected function calculateDisabled():Boolean {
    if(bindTo && forceReadOnlyValueExpression) {
      return bindTo.getValue().isCheckedOutByOther() || AccessControlUtil.isReadOnly(bindTo.getValue()) || forceReadOnlyValueExpression.getValue();
    }
    return false;
  }

  private function showChooser():void {
    var taxChooser:TaxonomySelectionWindow = new TaxonomySelectionWindow(TaxonomySelectionWindow({
      taxonomyId: taxId,
      singleSelection: singleSelection,
      propertyValueExpression: propertyValueExpression
    }));
    taxChooser.show();
  }

}
}
