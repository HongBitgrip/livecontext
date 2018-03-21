package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.logging.Logger;

import ext.data.Model;
import ext.panel.Panel;

/**
 * Displays a list of available videos from the external content platform.
 * Filter will be applied here when set.
 */
public class PreviewPanelBase extends Panel {

  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  private var selectedValueExpression:ValueExpression;
  private var defaultNameExpression:ValueExpression;
  private var contentTypeExpression:ValueExpression;

  public var previewType:String;

  public function PreviewPanelBase(config:PreviewPanel = null) {
    super(config);

    this.filterValueExpression = config.filterValueExpression;
    this.dataSourceValueExpression = config.dataSourceValueExpression;
    this.selectedValueExpression = config.selectedValueExpression;

    this.selectedValueExpression.addChangeListener(selectionChanged);
    this.dataSourceValueExpression.addChangeListener(dataSourceChanged);

  }

  /**
   * Fired when the data source has been changed. This means that
   * the preview type may have also changed.
   */
  private function dataSourceChanged(ve:ValueExpression):void {
    var dataSource:Model = ve.getValue();
    if (dataSource) {
      previewType = dataSource.data.previewType;
      if (!previewType) {
        throw new Error('Invalid preview definition for index ' + dataSource.data.index + ': ' + previewType);
      }
    }
  }


  /**
   * Fired when an item of the list view has been selected.
   * The selected item will be displayed in the configured preview type.
   */
  private function selectionChanged(ve:ValueExpression):void {
    var selection:Model = ve.getValue();
    if (selection) {
      //set the default name and the content type to create
      defaultNameExpression.setValue(formatDefaultName(selection));
      var contentType:String = dataSourceValueExpression.getValue()["data"].contentType;
      contentTypeExpression.setValue(contentType);
    }
  }

  /**
   * Combines the given ValueExpressions into an object.
   *
   * @param selectedValueExpression ValueExpression
   * @param dataSourceValueExpression ValueExpression
   * @return Object
   */
  protected function getCombinedValueExpression(selectedValueExpression:ValueExpression, dataSourceValueExpression:ValueExpression):Object {
    return {
      content: selectedValueExpression ? selectedValueExpression.getValue() : undefined,
      dataContent: dataSourceValueExpression ? dataSourceValueExpression.getValue() : undefined,
      getId: function ():String {
        var id:String = '';
        if (selectedValueExpression && selectedValueExpression.getValue()) {
          id += selectedValueExpression.getValue()['internalId']
        }
        if (dataSourceValueExpression && dataSourceValueExpression.getValue()) {
          id += dataSourceValueExpression.getValue()['internalId']
        }
        return id;
      }
    };
  }

  /**
   * Formats the default name that should be displayed
   * in the new content dialog.
   * @param record The selected record.
   * @return The formatted name.
   */
  private static function formatDefaultName(record:Model):String {
    var name:String = record.data.name;
    if (name) {
      var pattern:RegExp = /\//g;
      name = name.replace(pattern, '-');
      if (name.length > 80) {
        name = name.substring(0, 80);
        name = name.substring(0, name.lastIndexOf(' '));
      }
      return name;
    }
    return null;
  }

  /**
   * Callback handler of the new content dialog. The id of the content is post to
   * the post processing method of the third party REST controller, so that the specific provider
   * can put additional values into the content.
   *
   * @param content Content The new content that will be filled with 3rd party data afterwards.
   * @param data ProcessingData
   * @param callback Function
   */
  protected function postProcessExternalContent(content:Content, data:ProcessingData, callback:Function):void {
    var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod("externallibrary/postProcess", 'POST');
    var params:* = makeRequestParameters(content);
    remoteServiceMethod.request(params, function (response:RemoteServiceMethodResponse):void {
      var content:Content = response.getResponseJSON()["createdContent"];
      if (content) {
        data.setContent(content);
      }
      var additionalContent:Array = response.getResponseJSON()["additionalContent"];
      if (additionalContent) {
        for (var i:int = 0; i < additionalContent.length; i++) {
          data.addAdditionalContent(additionalContent[i]);
        }
      }
      callback.call(null);
    }, function (response:RemoteServiceMethodResponse):void {
      Logger.error('Request failed: ' + response.getError().errorName + '/' + response.getError().errorCode);
      callback.call(null);
    });
  }

  /**
   * Creates a JSON object with the REST parameters to pass to
   * the creation service.
   * @return The JSON object with the REST parameters.
   */
  private function makeRequestParameters(content:Content):Object {
    var dataSourceRecord:Model = dataSourceValueExpression.getValue();
    var itemRecord:Model = selectedValueExpression.getValue();
    var providerId:int = dataSourceRecord.data.index;
    var preferredSiteId:String = editorContext.getSitesService().getPreferredSiteId();
    var itemId:String = itemRecord.data.id;
    var dataUrl:String = dataSourceRecord.data.dataUrl;
    return {
      dataUrl: dataUrl,
      id: itemId,
      capId: content.getId(),
      providerId: providerId,
      preferredSite: preferredSiteId
    }
  }

  //noinspection JSMethodCanBeStatic
  protected function disableCreateButton(selection:*):Boolean {
    return !selection;
  }

  /**
   * The value expression contains the content type that will be created for
   * the current selection.
   * @return
   */
  protected function getContentTypeExpression():ValueExpression {
    if (!contentTypeExpression) {
      contentTypeExpression = ValueExpressionFactory.create('type', beanFactory.createLocalBean());
    }
    return contentTypeExpression;
  }

  /**
   * The value expression contains formatted default name of the selected content.
   * @return
   */
  protected function getDefaultNameExpression():ValueExpression {
    if (!defaultNameExpression) {
      defaultNameExpression = ValueExpressionFactory.create('name', beanFactory.createLocalBean());
    }
    return defaultNameExpression;
  }
}
}
