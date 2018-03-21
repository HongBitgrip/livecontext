import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import * as basic from "@coremedia/js-basic";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import * as imagemap from "./imagemap";

const $document = $(document);

$(function () {
  // initializes imagemaps
  nodeDecorationService.addNodeDecorator(function ($target) {
    findAndSelf($target, ".cm-imagemap").each(function () {
      const $imagemap = $(this);
      imagemap.init($imagemap);

      $document.on(basic.EVENT_LAYOUT_CHANGED, function () {
        imagemap.update($imagemap);
      });
    });
  });
});
