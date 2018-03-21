package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.blueprint.studio.model.ExternalLibraryDataItem;
import com.coremedia.ui.data.ValueExpression;

import ext.Ext;
import ext.StringUtil;
import ext.container.Container;
import ext.dom.DomHelper;
import ext.dom.Element;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.blueprint.studio.ExternalLibraryStudioPlugin')]
[ResourceBundle('com.coremedia.blueprint.studio.ExternalLibraryProviderSettings')]
public class PreviewContentContainerBase extends Container {

  private static const PREVIEW_HTML:String = 'html';
  private static const PREVIEW_VIDEO:String = 'video';

  private var config:PreviewContentContainerBase;
  public var combinedVE:ValueExpression;


  private var selectedPreviewType:String = undefined;
  private var mediaUrl:String = undefined;
  private var previewHidden:Boolean = undefined;

  public function PreviewContentContainerBase(config:PreviewContentContainerBase = null) {
    super(config);
    this.config = config;
    previewHidden = undefined;
    selectedPreviewType = undefined;
    mediaUrl = undefined;
  }

// ---------------- Get contents

  /**
   * Get data from ValueExpression based upon previewType
   * @param ve ValueExpression
   * @return String
   */
  protected function getHTMLorVideoContent(ve:ValueExpression):String {
    if (isHTMLPreview(ve)) {
      return getHTMLData(ve);
    }
    if (isVideoPreview(ve)) {
      return getVideoData(ve);
    }
  }

  /**
   * Return the HTML of a media item, if the RSS item contains a e.g. video
   * @param ve ValueExpression
   * @return String
   */
  protected function resolveMediaHTML(ve:ValueExpression):String {
    if (!mediaUrl) {
      mediaUrl = '';

      var rawDataList:Array = getContent('rawDataList', ve) as Array;
      if (rawDataList && rawDataList instanceof Array) {
        for (var i:int = 0; i < rawDataList.length; i++) {
          var dataItem:ExternalLibraryDataItem = new ExternalLibraryDataItem(rawDataList[i]);
          if (dataItem.getType().indexOf('video') != -1) {
            var videoHtml:String = resourceManager.getString('com.coremedia.blueprint.studio.ExternalLibraryProviderSettings', 'preview_video_item_template');
            videoHtml = StringUtil.format(videoHtml, dataItem.getValue(), dataItem.getType());
            mediaUrl = videoHtml;
            break;
          }
          else if (dataItem.getType().indexOf('image') != -1) {
            var imageHtml:String = resourceManager.getString('com.coremedia.blueprint.studio.ExternalLibraryProviderSettings', 'preview_picture_item_template');
            imageHtml = StringUtil.format(imageHtml, dataItem.getValue());
            mediaUrl = imageHtml;
            break;
          }
        }
      }
    }
    return mediaUrl;
  }

  /**
   * Get content from 'selected' ValueExpression
   * @param key String
   * @param ve ValueExpression
   * @return Object
   */
  protected function getContent(key:String, ve:ValueExpression):Object {
    return getVEContent(key, ve, 'content');
  }

  /**
   * Get content from 'dataSource' ValueExpression
   * @param key String
   * @param ve ValueExpression
   * @return Object
   */
  protected function getDataContent(key:String, ve:ValueExpression):Object {
    return getVEContent(key, ve, 'dataContent');
  }


  protected function isPreviewHidden(ve:ValueExpression):Boolean {
    if (!previewHidden) {
      previewHidden = getHidePreviewState(ve);
    }
    return previewHidden;
  }

  protected function showPreviewActionButton(ve:ValueExpression):Boolean {
    return isVideoPreview(ve);
  }

// ---------------- EXTRACT HTML and VIDEO data

  private function getHTMLData(ve:ValueExpression):String {
    var description:String = getContent('description', ve) as String;
    var rawData:String = getContent('rawData', ve) as String;
    var htmlData:String = '';
    if (description) {
      htmlData = description;
    }
    if (rawData) {
      htmlData += rawData;
    }
    return fixMediaWidth(htmlData, 'img');
  }

  private function getVideoData(ve:ValueExpression):String {
    return getContent('rawData', ve) as String;
  }

  private static function fixMediaWidth(htmlData:String, mediaType:String):String {
    var html:Element = Ext.fly(DomHelper.createDom('<div>' + htmlData + '</div>'));
    if (html && mediaType === 'img') {
      var mediaTypes:Array = html.query(mediaType, false);
      if (mediaTypes && mediaTypes.length > 0) {
        mediaTypes[0].setMaxWidth('100%');
        return html.dom.innerHTML;
      }
    }
    return htmlData;
  }


// ---------------- EXTRACT CONTENT

  //noinspection JSMethodCanBeStatic
  /**
   * Extract content from ValueExpression of given veType
   *
   * @param key String
   * @param ve ValueExpression
   * @param veType String
   * @return Object
   */
  private function getVEContent(key:String, ve:ValueExpression, veType:String):Object {
    if (ve) {
      var content:Array = ve[veType];
      if (content) {
        var data:Object = content['data'];
        if (data) {
          return data[key];
        }
      }
    }
  }

// ---------------- DETERMINE PREVIEW TYPE

  protected function isHTMLPreview(ve:ValueExpression):Boolean {
    if (!selectedPreviewType) {
      selectedPreviewType = getPreviewType(ve);
    }
    return selectedPreviewType === PREVIEW_HTML;
  }

  private function isVideoPreview(ve:ValueExpression):Boolean {
    if (!selectedPreviewType) {
      selectedPreviewType = getPreviewType(ve);
    }
    return selectedPreviewType === PREVIEW_VIDEO;
  }

  private function getPreviewType(ve:ValueExpression):String {
    if (!previewHidden) {
      var previewType:String = getDataContent('previewType', ve) as String;
      if (previewType) {
        if (previewType === PREVIEW_HTML) {
          return PREVIEW_HTML;
        }
        else if (previewType === PREVIEW_VIDEO) {
          return PREVIEW_VIDEO;
        }
      }
    }
  }

  //noinspection JSMethodCanBeStatic
  /**
   * Check if ValueExpression is set and has content. If so, return true, false else.
   * @param ve ValueExpression
   * @return Boolean
   */
  private function getHidePreviewState(ve:ValueExpression):Boolean {
    return (ve && ve['content']) ? false : true;
  }
}
}
