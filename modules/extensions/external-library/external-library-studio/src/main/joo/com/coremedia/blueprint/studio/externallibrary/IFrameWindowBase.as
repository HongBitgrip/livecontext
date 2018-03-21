package com.coremedia.blueprint.studio.externallibrary {

import ext.form.Label;
import ext.window.Window;

/**
 * Window with an iframe.
 */
public class IFrameWindowBase extends Window {

  private var url:String;

  public function IFrameWindowBase(config:IFrameWindow = null) {
    this.url = config.url;
    super(config);
    addListener('afterlayout', initFrame);
  }

  private function initFrame():void {
    removeListener('afterlayout', initFrame);
    var label:Label = queryById('embedded') as Label;
    label.setText('<iframe width="100%" height="100%" src="' + url + '" />', false);
  }
}
}