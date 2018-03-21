package com.coremedia.blueprint.studio.model {
public class ExternalLibraryDataItem {

  private var _type:String;
  private var _mode:String;
  private var _value:String;
  private var _dataType:String;
  private var _length:Number;

  public function ExternalLibraryDataItem(data:Object) {
    this._dataType = data.dataType;
    this._length = data.length;
    this._value = data.value;
    this._type = data.type;
    this._mode = data.mode;
  }

  public function getType():String {
    return _type;
  }

  public function getMode():String {
    return _mode;
  }

  public function getValue():String {
    return _value;
  }

  public function getDataType():String {
    return _dataType;
  }

  public function getLength():Number {
    return _length;
  }  
}
}