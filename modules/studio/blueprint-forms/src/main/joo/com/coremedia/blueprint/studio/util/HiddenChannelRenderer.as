package com.coremedia.blueprint.studio.util {
import com.coremedia.cap.content.Content;
import com.coremedia.ui.store.BeanRecord;

import ext.Ext;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.blueprint.studio.Validation')]
public class HiddenChannelRenderer {

  public static function hiddenStatusIconRenderer(isHidden:Boolean, metaData:*, record:BeanRecord):String {
    if(isHidden){
      var message:String = ResourceManager.getInstance().getString('com.coremedia.blueprint.studio.Validation', 'CMNavigation_hidden_text');
      return '<img width="16" height="16" class="icon-is-hidden" data-qtip="' + message + '" src="' + Ext.BLANK_IMAGE_URL + '"/>';
    }
    return "";

  }
}
}