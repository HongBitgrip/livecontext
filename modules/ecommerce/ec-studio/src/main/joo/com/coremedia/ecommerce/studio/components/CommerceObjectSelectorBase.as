package com.coremedia.ecommerce.studio.components {
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.cms.editor.sdk.premular.Premular;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.components.LocalComboBox;
import com.coremedia.ui.components.StatefulComboBox;
import com.coremedia.ui.data.RemoteBeanUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.form.field.IField;

/**
 * The base class of the commerce objects selector combobox
 * It contains mainly the model logic to retrieve the catalog objects from the commerce system and
 * the string conversion acrobatic to ensure that the catalog object id (which looks like a number) is stored as String.
 */
public class CommerceObjectSelectorBase extends LocalComboBox {

  private var contentExpression:ValueExpression;
  private var selectedCatalogObjectsExpression:ValueExpression;
  private var quote:Boolean;

  public function CommerceObjectSelectorBase(config:CommerceObjectSelector = null) {
    quote = config.quote;
    super(config);
    selectedCatalogObjectsExpression = config.selectedCatalogObjectsExpression;

    // reset the current selection if the store has been modified
    getStore().addListener('add', resetSelection);
    getStore().addListener('update', resetSelection);
    getStore().addListener('datachanged', resetSelection);
  }

  private function resetSelection():void {
    const v:* = getValue();
    if (v && getStore().findExact(valueField, unquote(v)) >= 0) {
      setValue(v);
    }
  }

  internal function getSelectableCatalogObjectsExpression(config:CommerceObjectSelector):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():Array {
      var store:Store = CatalogHelper.getInstance().getStoreForContentExpression(getContentExpression()).getValue();
      if (!store) {
        return undefined;
      } else {
        clearInvalid();
      }

      var catalogObjectsArray:Array = config.getCommerceObjectsFunction.call(null, store) as Array;
      if (catalogObjectsArray && selectedCatalogObjectsExpression) {
        var selectedCatalogObjects:Array = selectedCatalogObjectsExpression.getValue() as Array;
        if (selectedCatalogObjects){
          catalogObjectsArray = catalogObjectsArray.filter(function(catalogObject:CatalogObject):Boolean {
            return selectedCatalogObjects.indexOf(catalogObject) < 0;
          });
        }
      }

      if (!catalogObjectsArray) {
        return [];
      }
      return RemoteBeanUtil.filterAccessible(catalogObjectsArray);
    });
  }

  private function getContentExpression():ValueExpression {
    if (!contentExpression) {
      contentExpression = ComponentContextManager.getInstance().getContextExpression(this, Premular.CONTENT_VARIABLE_NAME);
    }
    return contentExpression;
  }

  override public function setValue(value:*):IField {
    var valueString:String = value as String;
    valueString = unquote(valueString);
    return super.setValue(valueString);
  }

  private function unquote(valueString:String):String {
    if (!quote) return valueString;

    if (valueString) {
      if (valueString.indexOf('"') === 0) {
        valueString = valueString.substr(1);
      }
      if (valueString.lastIndexOf('"') === valueString.length - 1) {
        valueString = valueString.substr(0, valueString.length - 1);
      }
    }
    return valueString;
  }

  public function getUnquotedValue():String {
    return unquote(getValue());
  }
  override public function getValue():* {
    var value:* = super.getValue();
    if (!quote) return value;
    return value ? '"' + value + '"': value;
  }

}
}
