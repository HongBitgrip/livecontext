import $ from "jquery";
import { debounce } from "@coremedia/js-utils";

/**
 * Delays the callback execution for at least 200ms. If during this period another event
 * of the same type occurs then the execution is delayed for another 200ms.
 * @param eventType {string} the type of the event, e.g. "resize"
 * @param data {*} data passed to the event object of the callback
 * @param callback {function(Event)} the function being executed when the event occurs
 */
$.fn.onDelayed = function(eventType, data, callback) {
  $(this).on(eventType, data, debounce(callback));
};
