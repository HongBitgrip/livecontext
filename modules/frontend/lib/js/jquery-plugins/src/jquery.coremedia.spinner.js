import $ from "jquery";
import threeSixtySpinner from "../../360-spinner/src/360-spinner";

/**
 * 360° Spinner Plugin
 *
 * Picks a suitable image from a given set of images regarding given dimensions and the maximum size needed.
 * Based on https://github.com/heartcode/360-Image-Slider (MIT License)
 * Copyright (c) 2012 Róbert Pataki heartcode@robertpataki.com
 *
 * Version 1.1
 * Copyright (c) CoreMedia AG
 *
 * Usage: $(".cm-spinner").threeSixtySpinner();
 *
 * Example:
 * <div class="cm-spinner">
 *   <ol>
 *     <li class="cm-spinner__image"><img src="foo/00.jpg"></li>
 *     <li class="cm-spinner__image"><img src="foo/01.jpg"></li>
 *   </ol>
 * </div>
 *
 */

/*! 360° Spinner Plugin | Copyright (c) CoreMedia AG */
$.fn.threeSixtySpinner = function () {
  return this.each(function () {
    threeSixtySpinner($(this));
  });
};
