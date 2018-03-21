import $ from "jquery";
import responsiveImages from "@coremedia/js-responsive-images";

/**
 * Picks a suitable image from a given set of images regarding given dimensions and the maximum size needed.
 *
 * @function "$.fn.responsiveImages"
 * @version 1.6
 * @copyright CoreMedia AG
 * @summary Responsive Image Resizer jQuery Plugin
 * @example
 * ###### Usage
 * ```javascript
 * $(".cm-image--responsive").responsiveImage();
 * ```
 *
 * ###### HTML
 * ```html
 * <img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="[
 *  {
 *    "name" : "3x1",
 *    "ratioWidth" : 3,
 *    "ratioHeight" : 1,
 *    "linksForWidth" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"}
 *  },
 *  {
 *    "name" : "2x1",
 *    "ratioWidth" : 2,
 *    "ratioHeight" : 1,
 *    "linksForWidth" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"}
 *  }]" >
 * ```
 *
 * Deprecated legacy format:
 * ```html
 * <img src="image3x1.jpg" class="cm-image--responsive" data-cm-responsive-image="{
 *    "3x1" : {"320": "image3x1_small.jpg", "640": "image_medium.jpg", "1024": "image_large.jpg"},
 *    "2x1" : {"200": "image2x1_small.jpg", "400": "image2x1_other.jpg"}}" >
 * ```
 */
$.fn.responsiveImages = function () {
  responsiveImages(this);
};
