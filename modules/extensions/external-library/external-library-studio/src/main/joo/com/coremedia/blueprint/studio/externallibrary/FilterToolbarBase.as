package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;

import ext.Ext;
import ext.data.Model;
import ext.data.Store;
import ext.event.Event;
import ext.form.field.ComboBox;
import ext.form.field.Field;
import ext.form.field.TextField;
import ext.toolbar.Toolbar;

/**
 * Displays the filter section of the external content, displaying
 * a content selection combo and a search filter.
 */
public class FilterToolbarBase extends Toolbar {

  [Bindable]
  public var filterValueExpression:ValueExpression;

  [Bindable]
  public var dataSourceValueExpression:ValueExpression;

  private var cmdStack:CommandStack;

  private var dataIndex:Number;

  private static const COMBO_STORE_RECORD:* = Ext.define('com.coremedia.blueprint.studio.externallibrary.SourceComboModel', {
    extend: 'Ext.data.Model',
    fields: [
      'index',
      'name',
      'dataUrl',
      'providerClass',
      'previewType',
      'contentType',
      'markAsRead'
    ]
  });

  public function FilterToolbarBase(config:FilterToolbar = null) {
    super(config);
    this.filterValueExpression = config.filterValueExpression;
    this.dataSourceValueExpression = config.dataSourceValueExpression;
    this.dataIndex = config.dataIndex || 0;

    this.dataSourceValueExpression.addChangeListener(dataSourceChanged);
    cmdStack = new CommandStack(this);
  }


  override protected function afterRender():void {
    super.afterRender();
    initFilter();
  }

  private function dataSourceChanged():void {
    var textfield:TextField = Ext.getCmp('externalLibrarySearchFilter') as TextField;
    textfield.setDisabled(!this.dataSourceValueExpression.getValue());
  }

  /**
   * After layout 'cos buttons must have been rendered.
   */
  private function initFilter():void {
    Ext.getCmp('externalLibrarySearchFilter').setDisabled(!this.dataSourceValueExpression.getValue());
    cmdStack.reset();

    loadChoices(function ():void {
      //sets the initial value after loading
      if (!dataSourceValueExpression.getValue()) {
        var combo:ComboBox = getComboBox() as ComboBox;
        if (combo.getStore().getCount() > 0) {
          var selection:Model = combo.getStore().getAt(dataIndex);
          dataSourceValueExpression.setValue(selection);
          combo.setValue(selection.data.name);
        }
      }
    });
  }

  /**
   * Executes the actual loading of the data, record creation and filling of the store.
   */
  private function loadChoices(callback:Function):void {
    var combo:ComboBox = getComboBox();
    var store:Store = combo.getStore() as Store;
    store.removeAll();
    var remoteBean:RemoteBean = beanFactory.getRemoteBean("externallibrary/sources?" + Ext.urlEncode({preferredSite: editorContext.getSitesService().getPreferredSiteId()}));
    remoteBean.invalidate(function ():void {
      var items:Array = remoteBean.get("items");
      for (var i:int = 0; i < items.length; i++) {
//        store.add(new recordType({}, items[i]));
        store.add(Ext.create('com.coremedia.blueprint.studio.externallibrary.SourceComboModel', {
          index: items[i].index,
          name: items[i].name,
          dataUrl: items[i].dataUrl,
          providerClass: items[i].providerClass,
          previewType: items[i].previewType,
          contentType: items[i].contentType,
          markAsRead: items[i].markAsRead
        }));
      }
      callback.call(null);
    });
  }

  private static function getComboBox():ComboBox {
    return Ext.getCmp('externalDataCombo') as ComboBox;
  }

  protected function forward():void {
    cmdStack.execute(cmdStack.getActiveIndex() + 1);
  }

  protected function back():void {
    cmdStack.execute(cmdStack.getActiveIndex() - 1);
  }

  /**
   * Returns the command stack instance for the filter.
   * @return
   */
  public function getCommandStack():CommandStack {
    return cmdStack;
  }

  /**
   * Registers the listeners for the filter components, like the data source combo.
   */
  override protected function initComponent():void {
    super.initComponent();
    var combo:ComboBox = getComboBox() as ComboBox;
    combo.addListener('select', dataSourceSelectionChange);
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Fired when another data source has been selected
   */
  private function dataSourceSelectionChange(combo:ComboBox, record:Model, index:Number):void {
    dataSourceValueExpression.setValue(record);
    filterValueExpression.setValue('');
    this.cmdStack.addCommand(record, '');
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Executed when the user presses the enter key of the search area.
   * @param field The field the event was triggered from.
   * @param e The key event.
   */
  protected function applyFilterInput(field:Field, e:Event):void {
    if (e.getKey() === Event.ENTER) {
      applyFilter();
      e.stopEvent();
    }
  }

  /**
   * Applies the filter value and creates the corresponding command for the stack.
   */
  public function applyFilter():void {
    var filterField:TextField = Ext.getCmp('externalLibrarySearchFilter') as TextField;
    var combo:ComboBox = getComboBox() as ComboBox;

    var comboRecord:Model = null;
    for (var i:int = 0; i < combo.getStore().getCount(); i++) {
      if (combo.getStore().getAt(i).data.index === combo.getValue()) {
        comboRecord = combo.getStore().getAt(i);
        break;
      }
    }

    var filterString:String = filterField.getValue();
    getCommandStack().addCommand(comboRecord, filterString);
    filterValueExpression.setValue(filterString);
  }
}
}
