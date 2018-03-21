package com.coremedia.blueprint.studio.externallibrary {
import ext.data.Model;

/**
 * A history command.
 */
public class Command {
  public var record:Model;
  public var filter:String;
  public var index:int;
  public var cmdStack;

  public function Command(record:Model, filter:String, index:int, commandStack:CommandStack) {
    this.record = record;
    this.filter = filter;
    this.index = index;
    this.cmdStack = commandStack;
  }
}
}