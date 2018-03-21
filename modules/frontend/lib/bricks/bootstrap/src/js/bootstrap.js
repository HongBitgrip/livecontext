import $ from "jquery";

/**
 * The back-to-top button enables the user to scroll to the top of the current page.
 *
 * @name back-to-top button
 * @summary The back-to-top button enables the user to scroll to the top of the page.
 * @example
 * ```html
 * <a id="back-to-top" href="#" class="btn btn-primary cm-back-to-top" role="button"  ...
 * ```
 * CoreMedia will automatically find and initialize a back-to-top button for any element that contains the id.
 * Auto-initialization is not supported for a back-to-top button that is added to the DOM after jQuery's ready event
 * has fired.
 */
const $backToTop = $('#back-to-top');
const $window = $(window);

function toggleButton() {
  if ($window.scrollTop() > 50) {
    $backToTop.fadeIn();
  } else {
    $backToTop.fadeOut();
  }
}

function scrollToTop() {
  // call Bootstrap jQuery Plugin tooltip
  $backToTop.tooltip('hide');
  $('body,html').animate({
    scrollTop: 0
  }, 800);
  return false;
}

// --- DOCUMENT READY ---
$(function () {
  $window.scroll(toggleButton);
  $backToTop.click(scrollToTop);
  // call Bootstrap jQuery Plugin tooltip
  $backToTop.tooltip('show');
});