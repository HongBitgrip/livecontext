import $ from "jquery";
import magnificPopup from "@coremedia/js-magnific-popup";

// --- DOCUMENT READY ---
$(function () {

  /**
   * Initialize magnific popup.
   *
   * @name popup opener
   * @summary Initialize magnific popup
   * @example
   * ```html
   * <a href="..." data-cm-popup="" class="cm-claim__popup-opener"> ...</a>
   * ```
   * CoreMedia will automatically find and initialize a popup opener for any element that contains the
   * data attribute. Auto-initialization is not supported for a popup opener that is added to the DOM after
   * jQuery's ready event has fired.
   */
  magnificPopup($('[data-cm-popup]'), {
    type: 'iframe',
    midClick: true,
    mainClass: 'cm-popup',
    removalDelay: 160,
    preloader: false,
    fixedContentPos: false,
    /* fix CMS-4839, html5 video as iframe not working in IE, switiching to inline popup */
    callbacks: {
      elementParse: function(item) {
        if(item.src.indexOf("resource\/blob") > -1) {
          const url = item.src;
          item.type = "inline";
          item.src = '<div class="cm-popup--scaler"><button title="Close (Esc)" type="button" class="mfp-close">×</button>' +
                  '<video src="'+url+'" class="cm-video cm-video--html5" poster="" autoplay="autoplay" controls="controls"></video>' +
                  '</div>';
        }
      }
    }
  });
});
