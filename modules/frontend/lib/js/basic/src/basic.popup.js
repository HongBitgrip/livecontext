const identifier = "cm-popup";
const classPopupActive = identifier + "--active";

// prefix/namespace for events in this module
const EVENT_PREFIX = "coremedia.blueprint.basic.popup.";

/**
 * @type {string} name of the event to be triggered if popup has changed
 */
export const EVENT_POPUP_CHANGED = EVENT_PREFIX + "popupChanged";

/**
 * Opens the given popup
 * @param {jQuery} $popup popup to be opened
 */
export function open($popup) {
  $popup.addClass(classPopupActive);
  $popup.trigger(EVENT_POPUP_CHANGED, [true]);
}

/**
 * Closes the given popup
 * @param {jQuery} $popup popup to be closed
 */
export function close($popup) {
  $popup.removeClass(classPopupActive);
  $popup.trigger(EVENT_POPUP_CHANGED, [false]);
}

/**
 * Opens the popup if it is closed and closes the popup if it is opened
 * @param $popup popup to be toggled
 */
export function toggle($popup) {
  if ($popup.hasClass(classPopupActive)) {
    close($popup);
  } else {
    open($popup);
  }
}