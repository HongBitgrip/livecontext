import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";

/**
 * Same as jQuery.find but also includes the target(s) on which findAndSelf was called.
 *
 * @this {jQuery}
 * @param selector {string} the selector to search for
 * @return {jQuery} the search result as jQuery result
 */
$.fn.findAndSelf = function (selector) {
  return findAndSelf(this, selector);
};
