import $ from "jquery";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import threeSixtySpinner from "@coremedia/js-360-spinner";

// --- DOCUMENT READY ---
$(function () {

  // initially load 360 spinner
  nodeDecorationService.addNodeDecoratorBySelector(".cm-spinner__canvas", function ($target) {
    threeSixtySpinner($target);
  });
});
