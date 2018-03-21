import $ from "jquery";
import {api as magnificPopupApi} from "@coremedia/js-magnific-popup";

/**
 * Quickinfo functionality
 *
 */
const prefix = "cm-";
const prefixcss = "." + prefix;
const identifier = prefix + "quickinfo";
const classQuickInfoActive = identifier + "--active";

// prefix/namespace for events in this module
const EVENT_PREFIX = "coremedia.blueprint.quickinfo.";

/**
 * @member {string} EVENT_QUICKINFO_CHANGED - name of the event to be triggered if quick info has changed
 */
export const EVENT_QUICKINFO_CHANGED = EVENT_PREFIX + "quickInfoChanged";

/**
 * Return the configuration of the given quickinfo.
 * @param {jQuery} $quickinfo - quickinfo
 * @returns {object} configuration of the quickinfo
 * @ignore
 */
function getConfig($quickinfo) {
  return $.extend({modal: false, group: undefined}, $quickinfo.data(identifier));
}

function isActive($quickinfo) {
  return $quickinfo.hasClass(classQuickInfoActive);
}

/**
 * Opens a quickinfo.
 *
 * @function show
 * @param {jQuery} $quickinfo - The quickinfo to be opened.
 */
export function show($quickinfo) {
  if (!isActive($quickinfo)) {
    const config = getConfig($quickinfo);
    if (config.group !== undefined) {
      // notify all other quickinfos in group
      $("[data-" + identifier + "]").not($quickinfo).each(function () {
        groupHide($(this), config.group);
      });
    }
    $quickinfo.addClass(classQuickInfoActive)
            .parents('.carousel-inner')
            .addClass(identifier + "__parent--active");
    if (config.modal) {
      // use magnificPopup to open quickinfo as full screen overlay
      magnificPopupApi.open({
        closeBtnInside: false,
        items: {
          src: $quickinfo,
          type: "inline"
        },
        callbacks: {
          close: function () {
            hide($quickinfo);
          }
        }
      });
    }
    $quickinfo.trigger(EVENT_QUICKINFO_CHANGED, [true]);
  }
}

/**
 * Hides a quickinfo.
 *
 * @function hide
 * @param {jQuery} $quickinfo - The quickinfo to be hidden.
 */
export function hide($quickinfo) {
  if (isActive($quickinfo)) {
    const config = getConfig($quickinfo);
    const buttons = $quickinfo.find(prefixcss + "button");
    const transition = [];

    if (config.modal) {
      // close full screen overlay
      magnificPopupApi.close();
    }

    // Disable transition feature for cm-button
    $.each(buttons, function () {
      transition.push($(this).css("transition"));
      $(this).css("transition", "none");
    });

    // Hide quick in
    $quickinfo.removeClass(classQuickInfoActive)
            .parents('.carousel-inner')
            .removeClass(identifier + "__parent--active");
    $quickinfo.trigger(EVENT_QUICKINFO_CHANGED, [false]);

    // Re-enable transition feature for cm-button
    $.each(buttons, function (index, button) {
      transition.push($(this).css("transition"));
      $(button).css("transition", transition[index]);
    });
  }
}

/**
 * Opens a quickinfo, if it is hidden or hides a quickinfo, if it is shown.
 *
 * @function toggle
 * @param {jQuery} $quickinfo - The quickinfo to be toggled.
 */
export function toggle($quickinfo) {
  if (isActive($quickinfo)) {
    hide($quickinfo);
  } else {
    show($quickinfo);
  }
}

/**
 * Hides a quickinfo, if it is in the given group.
 *
 * @function groupHide
 * @param {jQuery} $quickinfo - The quickinfo to be hidden, if it is in the given group.
 * @param {string} group - The given group.
 */
export function groupHide($quickinfo, group) {
  if (group === getConfig($quickinfo).group) {
    hide($quickinfo);
  }
}

/**
 * Hides a quickinfo, if user clicked close button or outside the quickinfo container.
 *
 * @function closeQuickInfo
 * @param {jQuery} $quickinfo - The quickinfo to be hidden.
 */
export function closeQuickInfo($quickinfo) {
  // close quickinfo on X button
  $quickinfo.find(prefixcss + "quickinfo__close").on('click touch', function () {
    hide($quickinfo);
  });
  // close quickinfo on click outside it
  $(document).on('click touch', function (event) {
    const $activeQuickinfo = $(prefixcss + 'quickinfo--active');
    if ($activeQuickinfo.length && !$(event.target).closest($activeQuickinfo).length) {
      hide($quickinfo);
    }
  });
}

/**
 * Opens a quickinfo, if it is hidden or hides a quickinfo, if it is shown.
 *
 * @function toggleQuickInfo
 * @param {jQuery} $button - The button clicked to toggle the quickinfo.
 * @param {jQuery} $config - The given config.
 */
export function toggleQuickInfo($button, $config) {
  $button.click(function () {
    const $quickInfo = $("#" + $config.target);
    toggle($quickInfo);
    return false;
  });
}

/**
 * Opens a quickinfo, if it is hidden or hides a quickinfo, if it is shown.
 *
 * @function switchQuickInfo
 * @param {jQuery} $button - The button clicked to toggle the next oder previous quickinfo.
 * @param {jQuery} $config - The given config.
 */
export function switchQuickInfo($button) {
  $button.click(function(event){
    event.stopPropagation();
    const $nextQuickInfo = $('#' + $button.data('cm-target'));
    show($nextQuickInfo);
  });
}
