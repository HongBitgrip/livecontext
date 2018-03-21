import $ from "jquery";
import * as logger from "@coremedia/js-logger";
import { EVENT_LAYOUT_CHANGED } from "@coremedia/js-basic";

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function () {

  const $document = $(document);

  // to load initially hidden images in tabs
  $(".tabs-list a").on("click", function () {
    $document.trigger(EVENT_LAYOUT_CHANGED);
  });

  logger.log("Welcome to CoreMedia Hybris Integration");
});
