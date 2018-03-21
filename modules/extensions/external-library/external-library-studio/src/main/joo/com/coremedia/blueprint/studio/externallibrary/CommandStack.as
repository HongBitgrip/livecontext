package com.coremedia.blueprint.studio.externallibrary {
import ext.Ext;
import ext.data.Model;
import ext.form.field.ComboBox;

/**
 * The command stack of the third party's filter section.
 */
public class CommandStack {
  private var filterToolbar:FilterToolbarBase;
  private var history:Array;
  private var activeIndex:int;

  public function CommandStack(filterToolbar:FilterToolbarBase) {
    this.filterToolbar = filterToolbar;
    this.history = [];
    this.activeIndex = 0;
    this.history.push(new Command(null, null, activeIndex, this));
  }

  /**
   * Adds a new command to the stack. The command contains
   * the active selected data source and the active search string.
   * Both are restored when a command re-executed.
   * @param dataSourceRecord The selected data source combo record.
   * @param filter The active search string.
   */
  public function addCommand(dataSourceRecord:Model, filter:String):void {
    if(activeIndex<(history.length-1)) {
      history = history.slice(0, (activeIndex+1));//remove forward commands.
    }
    activeIndex = history.length;
    this.history.push(new Command(dataSourceRecord, filter, activeIndex, this));
    updateButtons();
  }

  public function getCommand(index:int):Command {
    return history[index];
  }

  private function updateButtons():void {
    filterToolbar.queryById('back').setDisabled(false);
    filterToolbar.queryById('forward').setDisabled(false);

    if(activeIndex <= 0) {
      filterToolbar.queryById('back').setDisabled(true);
    }

    if(activeIndex>=(history.length-1)) {
      filterToolbar.queryById('forward').setDisabled(true);
    }
  }

  /**
   * Returns the active command index.
   * @return
   */
  public function getActiveIndex():int {
    return activeIndex;
  }

  /**
   * Invoked by a command to restore the commands status.
   * @param index
   */
  public function execute(index:int):void {
    if (index < 0 || index === history.length) {
      return;
    }

    var cmd:Command = this.history[index];
    if(cmd) {
      this.activeIndex = cmd.index;
      var combo:ComboBox = Ext.getCmp('externalDataCombo') as ComboBox;
      var value:* = null;
      if(cmd.record) {
        value = cmd.record.data.name;
      }
      if(value) {
        combo.setValue(value);
        filterToolbar.dataSourceValueExpression.setValue(cmd.record);
      }

      filterToolbar.filterValueExpression.setValue(cmd.filter);
    }

    updateButtons();
  }

  /**
   * Resets the command stack and the history buttons.
   */
  public function reset():void {
    filterToolbar.queryById('back').setDisabled(true);
    filterToolbar.queryById('forward').setDisabled(true);
    this.activeIndex = 0;
    history = [];
    this.history.push(new Command(null, null, activeIndex, this));
  }
}
}