import $ from "jquery";
import * as basic from "@coremedia/js-basic";

$(function () {
  // the studio preview works with a different jQuery instance that does not share any events even if it has the
  // same name and is triggered/listened to on the same DOM node.

  // check if preview is loaded (doesn't work with modules but with global variables)
  if (window.coremedia
          && coremedia.preview
          && coremedia.preview.$
          && coremedia.preview.basic
          && coremedia.preview.basic.EVENT_LAYOUT_CHANGED) {

    // connect EVENT_LAYOUT_CHANGED of coremedia.preview.$ and "@coremedia/js-basic"
    const $documentPreview = coremedia.preview.$(document);
    const $documentBlueprint = $(document);

    const SOURCE = this;

    $documentPreview.on(coremedia.preview.basic.EVENT_LAYOUT_CHANGED, function (event, source) {
      if (SOURCE === source) {
        return;
      }
      $documentBlueprint.trigger(basic.EVENT_LAYOUT_CHANGED, [SOURCE]);
    });

    $documentBlueprint.on(basic.EVENT_LAYOUT_CHANGED, function (event, source) {
      if (SOURCE === source) {
        return;
      }
      $documentPreview.trigger(coremedia.preview.basic.EVENT_LAYOUT_CHANGED, [SOURCE]);
    });
  }
});
