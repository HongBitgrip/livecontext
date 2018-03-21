import $ from "jquery";
import { addNodeDecoratorBySelector } from "@coremedia/js-node-decoration-service";
import { refreshFragment } from "@coremedia/js-basic";

function getChangeImagesFn($productAssets, productDisplayJS) {
  return function(catEntryId, productId) {
    //reload the fragment with selected product variants.
    //to this end we send the catEntryId, productId and the selected attributes name/value pairs
    //as ';'-separated string as "attributes" to the reloader
    const entitledItemId = "entitledItem_" + productId;
    const selectedAttributes = productDisplayJS.selectedAttributesList[entitledItemId];
    let attributes = "";
    for (let attribute in selectedAttributes) {
      if (selectedAttributes.hasOwnProperty(attribute)) {
        attributes += attribute + ";" + selectedAttributes[attribute] + ";";
      }
    }
    refreshFragment($productAssets, undefined,
            {
              productId: productId,
              catEntryId: catEntryId,
              attributes: attributes
            }
    );
  }
}

$(function () {
  const dojo = window.dojo;
  if (typeof dojo !== 'undefined') {
    addNodeDecoratorBySelector(".cm-product-assets", function ($target) {
      dojo.addOnLoad(function () {
        const productDisplayJS = window.productDisplayJS;
        if (productDisplayJS) {
          const changeImages = getChangeImagesFn($target, productDisplayJS);
          dojo.topic.subscribe('DefiningAttributes_Resolved', changeImages);
          dojo.topic.subscribe('DefiningAttributes_Changed', changeImages);
        }
      });
    });
  }
});
