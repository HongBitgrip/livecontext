package com.coremedia.livecontext.studio.library {
import com.coremedia.cap.content.ContentTypeNames;
import com.coremedia.cms.editor.sdk.ContentTreeRelation;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.library.ECommerceCollectionViewExtension;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ecommerce.studio.model.Store;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
public class LivecontextCollectionViewExtension extends ECommerceCollectionViewExtension {
  private var treeRelation:LivecontextContentTreeRelation = new LivecontextContentTreeRelation();

  protected static const DEFAULT_TYPE_MARKETING_SPOT_RECORD:Object = {
    name: ContentTypeNames.DOCUMENT,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_icon')
  };

  protected static const PRODUCT_VARIANT_TYPE_RECORD:Object = {
    name: CatalogModel.TYPE_PRODUCT_VARIANT,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'ProductVariant_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'ProductVariant_icon')
  };

  protected static const MARKETING_SPOT_TYPE_RECORD:Object = {
    name: CatalogModel.TYPE_MARKETING_SPOT,
    label: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_label'),
    icon: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'MarketingSpot_icon')
  };

  override public function isApplicable(model:Object):Boolean {
    if (model is CatalogObject) {
      var activeStore:Boolean = CatalogHelper.getInstance().isActiveCoreMediaStore();
      if (activeStore === undefined) {
        return undefined;
      }
      return !activeStore;
    }
    return false;
  }

  override public function getContentTreeRelation():ContentTreeRelation {
    return treeRelation;
  }

  override public function getAvailableSearchTypes(folder:Object):Array {
    if (folder is CatalogObject) {
      if (folder is Marketing) {
        return [DEFAULT_TYPE_MARKETING_SPOT_RECORD];
      }
      var availableSearchTypes:Array = [DEFAULT_TYPE_PRODUCT_RECORD, PRODUCT_VARIANT_TYPE_RECORD];
      if (folder is Store) {
        availableSearchTypes.push(MARKETING_SPOT_TYPE_RECORD);
      }
      return availableSearchTypes;
    }
    return null;
  }
}
}