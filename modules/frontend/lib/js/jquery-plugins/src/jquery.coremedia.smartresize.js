import $ from "jquery";
import { debounce } from "@coremedia/js-utils";

/*! Smart Resize Plugin | Copyright (c) CoreMedia AG */

/**
 * Smart Resize Plugin
 *
 * Fires the resize event only after a treshhold of 200ms
 * see: http://www.paulirish.com/2009/throttled-smartresize-jquery-event-handler/  *
 *
 * Version 1.2
 * Copyright (c) CoreMedia AG
 *
 * Usage:
 * $(window).smartresize(function(){
 *   // code that takes it easy...
 * });
 *
 * @this {jQuery}
 * @param trigger {function} a callback function to execute. if no callback function is provided a "smartresize" event
 *                          will be triggered.
 */
$.fn.smartresize = function (trigger) {
  return trigger ? this.bind('resize', debounce(trigger)) : this.trigger('smartresize');
};
