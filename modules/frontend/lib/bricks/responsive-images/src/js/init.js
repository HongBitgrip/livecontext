import $ from "jquery";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import responsiveImages from "@coremedia/js-responsive-images";
import { EVENT_LAYOUT_CHANGED } from "@coremedia/js-basic";

const RESPONSIVE_IMAGES_SELECTOR = ".cm-image--responsive";

$(function () {
  // initializes responsive images
  nodeDecorationService.addNodeDecoratorBySelector(RESPONSIVE_IMAGES_SELECTOR, function ($target) {
    responsiveImages($target);
  });

  $(document).on(EVENT_LAYOUT_CHANGED, function () {
    responsiveImages($(RESPONSIVE_IMAGES_SELECTOR));
  });
});
