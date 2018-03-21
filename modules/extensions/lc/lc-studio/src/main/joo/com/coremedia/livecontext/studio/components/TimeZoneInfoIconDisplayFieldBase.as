package com.coremedia.livecontext.studio.components {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtilInternal;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.CatalogObjectPropertyNames;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.components.IconDisplayField;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.StringUtil;

[ResourceBundle('com.coremedia.livecontext.studio.AddTimeZoneInfoPlugin')]
public class TimeZoneInfoIconDisplayFieldBase extends IconDisplayField {

  private var wcsTimeZoneValueExpression:ValueExpression;

  public function TimeZoneInfoIconDisplayFieldBase(config:TimeZoneInfoIconDisplayField = null) {
    super(config);
    getTooltipExpression(config).loadValue(function(newTooltip:String):void {
      tooltip = newTooltip;
    });
  }

  private function getWcsTimeZoneValueExpression(config:TimeZoneInfoIconDisplayField):ValueExpression {
    if (!wcsTimeZoneValueExpression) {
      wcsTimeZoneValueExpression = ValueExpressionFactory.createFromFunction(function ():String {
        var text:String;
        var entityExpression:ValueExpression = config.previewPanel.getCurrentPreviewContentValueExpression();
        var storeExpression:ValueExpression;
        if (entityExpression.getValue() is Content) {
          storeExpression = CatalogHelper.getInstance().getStoreForContentExpression(WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION);
        } else if (entityExpression.getValue() is CatalogObject) {
          storeExpression = entityExpression.extendBy(CatalogObjectPropertyNames.STORE);
        }
        if (storeExpression && storeExpression.getValue() && Store(storeExpression.getValue()).getWcsTimeZone()) {
          text = Store(storeExpression.getValue()).getWcsTimeZone()["id"];
        }
        return text;
      });
    }
    return wcsTimeZoneValueExpression;
  }

  private function getTooltipExpression(config:TimeZoneInfoIconDisplayField):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var commerceTimeZoneId:String = getWcsTimeZoneValueExpression(config).getValue();
      if (!commerceTimeZoneId) return undefined;
      return StringUtil.format(resourceManager.getString('com.coremedia.livecontext.studio.AddTimeZoneInfoPlugin', 'Preview_Wcs_Timezone_Divergation_Warning_Message'),
              ContentLocalizationUtilInternal.localizeTimeZoneID(commerceTimeZoneId));
    });
  }

  internal function getVisibilityExpression(config:TimeZoneInfoIconDisplayField):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var commerceTimeZoneId:String = getWcsTimeZoneValueExpression(config).getValue();
      if (!commerceTimeZoneId) return false;
      var dateTimeModel:Bean = config.model;
      var timeZoneId:String = dateTimeModel.get("timeZone");
      return timeZoneId !== commerceTimeZoneId;
    });
  }
}
}