import $ from "jquery";
import { findAndSelf, findRelativeOrAbsolute } from "@coremedia/js-jquery-utils";
import { debounce } from "@coremedia/js-utils";
import * as logger from "@coremedia/js-logger";
import * as deviceDetector from "@coremedia/js-device-detector";
import * as basic from "@coremedia/js-basic";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import * as quickInfo from "@coremedia/brick-quick-info";
import {default as magnificPopup, api as magnificPopupApi} from "@coremedia/js-magnific-popup";

import "./cycle2";
import "./jquery.coremedia.equalheight";

/**
 * @type {number} stores the last navigation width in order die identify if mega-menu width adjustments need to be recalculated.
 */
let lastNavWidth = 0;

/**
 * @type {string} specifies the selector for mega menu for adjustment of its children's widths to fit the whole space
 */
const megaMenuSelector = ".mega-menu > li > ul";

/**
 * @type {string} specifies the selector for marketing spot items for equalization of heights
 */
const marketingSpotItemSelector = ".cm-collection--marketingspot .cm-teasable--text";

/**
 * @type {string} defines the internal name for the mobile device
 */
export const DEVICE_MOBILE = "mobile";

/**
 * @type {string} defines the internal name of the tablet device
 */
export const DEVICE_TABLET = "tablet";

/**
 * @type {string} defines the internal name of the desktop device
 */
export const DEVICE_DESKTOP = "desktop";

/**
 *
 * @type {string} defines the event type that signals that the pdp's image carousel is fully initialized
 */
export const PDP_ASSET_READY_EVENT = "carouselReady";

/**
 * set width of each item in the mega-menu depending to screen-size
 */
export function setMegaMenuItemsWidth() {
  const $navMegaMenu = $(megaMenuSelector);
  // detect width of navigation
  const currentNavWidth = $navMegaMenu.width();
  // set width only if changed
  if (currentNavWidth != lastNavWidth) {
    // detect how much space each navigation item has keeping taking border of 1px for each element into account
    const $children = $navMegaMenu.children("li");
    const $childrenExceptLast = $children.not(":last");
    const $lastChild = $children.last();
    const numChildren = $children.length;
    const singleWidth = Math.floor(currentNavWidth / numChildren - 1);
    // calculate how much width is left for the last element after rounding the base width down
    const restWidth = Math.floor(currentNavWidth - numChildren * singleWidth);
    // adjust width for all elements exept the last element
    $childrenExceptLast.css("width", Math.floor(currentNavWidth / numChildren - 1));
    // last element gets rest space
    $lastChild.css("width", (singleWidth + restWidth));
    // sub-menus with at least same width (but can wider)
    $children.children("ul").css("min-width", "100%");

    // save currentNavWidth as lastNavWidth
    lastNavWidth = currentNavWidth;
  }
}

/**
 * unset width of each item in the mega-menu
 */
export function unsetMegaMenuItemsWidth() {
  const $navMegaMenu = $(megaMenuSelector);
  const $children = $navMegaMenu.children("li");
  // reset mega-menu-items widths
  $children.css("width", "");
  // reset mega-menu-items sub-menu widths
  $children.children("ul").css("min-width", "");

  // reset lastNavWidth so setMegaMenuItemsWidth recalculates if used again
  lastNavWidth = 0;
}

/**
 * equalize height of each item in marketing spot
 */
export function setMarketingSpotItemsHeight() {
  // first unset all heights, otherwise the height can only decrease
  unsetMarketingSpotItemsHeight();
  const $marketingSpotItems = $(marketingSpotItemSelector);

  // calculate biggest height by iterating over all marketing spot items
  let biggest = 0;
  $marketingSpotItems.each(function () {
    const current = $(this).height();
    if (current > biggest) {
      biggest = current;
    }
  });

  // calculate new height based on biggest height for each element
  $marketingSpotItems.each(function () {
    const diff = biggest - $(this).height();
    $(this).css({
      "height": biggest + "px",
      "padding-top": (diff / 2) + "px"
    });
  });
}

/**
 * unset height of each item in the marketing spot
 */
export function unsetMarketingSpotItemsHeight() {
  const $marketingSpotItems = $(marketingSpotItemSelector);
  $marketingSpotItems.css({
    "height": "",
    "padding-top": ""
  });
}

/**
 * updates a control element for a shopping cart
 * @param {String} control the control element
 */
export function updateCartControl(control) {
  const $control = $(control);
  const config = $.extend({symbol: undefined, badge: undefined, cart: undefined}, $control.data("cm-cart-control"));

  // only apply cart control if configuration is sufficient
  if (config.symbol !== undefined && config.badge !== undefined && config.cart !== undefined) {
    // read config of the attached cart
    const cartConfig = $.extend({itemCount: 0}, $control.find(config.cart).data("cm-cart"));

    // find dom element representing the cart symbol
    const $symbol = $control.find(config.symbol);

    // update cart symbol based on item count
    if (cartConfig.itemCount > 0) {
      $symbol.removeClass("icon-cart-empty");
      $symbol.addClass("icon-cart-with-items");
    } else {
      $symbol.addClass("icon-cart-empty");
      $symbol.removeClass("icon-cart-with-items");
    }
    // add item count to cart badge
    $control.find(config.badge).html(cartConfig.itemCount);
  }
}

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function () {
  const $window = $(window);
  const $document = $(document);

  logger.log("LiveContext DOM RDY");

  // init device detection
  deviceDetector.init();

  // move to top button
  nodeDecorationService.addNodeDecorator(function ($target) {
    const selector = ".cm-icon--button-top";

    findAndSelf($target, selector).click(function () {
      $("html, body").animate({scrollTop: 0}, "slow");
      return false;
    });
  });

  // init jquery.elevateZoom
  nodeDecorationService.addNodeDecoratorBySelector(".cm-product-assets__slideshow", function ($target) {

    let magnifierPluginEventsInitialized = false;

    // prepare functions, to be used when event listeners are added and removed
    const instance = {};
    instance.findZoomImage = function () {
      const $activeImg = $(this).find(".cycle-slide-active").find("img[data-zoom-image]");
      resetMagnifierPlugin($activeImg);
    };
    instance.findZoomImageByTarget = debounce(function () {
      const $activeSlide = $target.find(".cycle-slide-active").find("img[data-zoom-image]");
      resetMagnifierPlugin($activeSlide);
    });
    instance.findZoomImageBySlide = function (event, optionHash, outgoingSlideEl, incomingSlideEl) {
      const $activeImg = $(incomingSlideEl).find("img[data-zoom-image]");
      const $prevImg = $(outgoingSlideEl).find("img[data-zoom-image]");
      resetMagnifierPlugin($activeImg, $prevImg);
    };
    instance.disablePointerEvents = function() {
      // disabling pointer events for zoomWindow means enabling them for the image
      $(this).css("pointer-events", "none");
    };
    instance.enablePointerEvents = function() {
      // re-enable pointer-events after finishing the click on the image
      $(".zoomContainer").css("pointer-events","");
    };
    $target.data("cm-product-assets", instance);


    if (deviceDetector.getLastDevice().isTouch && deviceDetector.getLastDevice().type !== "desktop") { // do not use zoom plugin on touch devices
      return;
    }

    // positioning the zoom window requires incorporating the border size
    const borderSize = 5;

    /**
     * Calculates the width and height dimension that is available for the zoom window in the current viewport
     * @param $img the image that is currently shown in the slideshow
     * @returns {*}
     */
    function calculateZoomWindowDimension($img) {
      let $row = $img.closest(".row");
      //fix for sfcc: no class row exists
      if ($row.offset() === undefined) {
        $row = $img.closest(".pdp-main");
      }
      const imgPos = $img[0].getBoundingClientRect();
      return $.extend({}, {
        width: ($row.width() + $row.offset().left ) - (imgPos.right + borderSize * 2), // also remove the 20px offset
        height: Math.min((window.pageYOffset + window.innerHeight) - ($img.offset().top - borderSize / 2), $row.height())
      });
    }

    /**
     * Initializes the elevateZoom plugin for the given image and removes
     * the plugin data from a previous image.
     * @param $img jQuery object representing the img DOM element
     * @param $previousImg jQuery object representing the img DOM element of the previous image
     */
    function resetMagnifierPlugin($img, $previousImg) {

      // make sure that previous instances of the plugin are properly removed before ...
      $(".zoomContainer").remove(); //
      if ($previousImg && $previousImg.length > 0) {
        $previousImg.removeData("elevateZoom");
      }

      // the img is not set if the slideshow shows a spinner or a video
      if ($img && $img.length > 0) {
        $img.removeData("elevateZoom");

        const zoomWindowDim = calculateZoomWindowDimension($img);
        if (zoomWindowDim.width > 400) {
          $img.elevateZoom({
            scrollZoom: true,
            zoomWindowWidth: zoomWindowDim.width,
            zoomWindowHeight: zoomWindowDim.height,
            borderSize: borderSize,
            borderColour: "#fff",
            zoomWindowFadeIn: 200,
            zoomWindowFadeOut: 200,
            zoomWindowOffety: borderSize / -2,
            responsive: false
          });

        } else { // if there is not enough space on the right side for the zoom window then use the lens zoom type
          $img.elevateZoom({
            zoomType: "inner",
            cursor: "crosshair"
          });
        }

        if (!magnifierPluginEventsInitialized) {
          // disable mouse click events on zoom window to support PDE and saving of images
          $("body").on("mousedown", ".zoomContainer", instance.disablePointerEvents);
          $img.on("click", instance.enablePointerEvents);
          magnifierPluginEventsInitialized = true;
        }
      }
    }

    // trigger initialization after cycle has been fully initialized. Only then the dimension of the zoom window
    // can be calculated
    $target.on(PDP_ASSET_READY_EVENT, instance.findZoomImage);

    $window.on("scroll", {}, instance.findZoomImageByTarget);

    // when resizing the window the dimensions of the slideshow image and the image itself might change and as such
    // the zoom window has to be recalculated
    $window.on("resize", {}, instance.findZoomImageByTarget);

    // trigger re-init after the image of the slideshow has changed
    $target.on('cycle-after', instance.findZoomImageBySlide);

  }, function ($target) {
    const instance = $target.data("cm-product-assets");
    $("body").off("mousedown", ".zoomContainer", instance.disablePointerEvents);
    $target.off(PDP_ASSET_READY_EVENT, instance.findZoomImage);
    $window.off("scroll", instance.findZoomImageByTarget);
    $window.off("resize", instance.findZoomImageByTarget);
    $target.off("cycle-after", instance.findZoomImageBySlide);
  });


  // assign accordion-item functionality
  nodeDecorationService.addNodeDecorator(function ($target) {
    const selector = ".cm-accordion-item";
    findAndSelf($target, selector).each(function () {
      const $item = $(this);
      const $accordion = $item.closest(".cm-collection--accordion");
      const $itemHeader = $item.find(".cm-accordion-item__header").first();

      $itemHeader.on("click", function () {
        basic.accordion.change($accordion, $item);
      });
    });
  });

  // init slideshows
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifier = "cm-slideshow";
    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function () {
      const $slider = $(this);

      const defaultConfig = {
        itemSelector: undefined, // DEPRECATED
        container: undefined,
        item: undefined,
        timeout: 5000,
        prev: undefined,
        next: undefined,
        maxZ: 99
      };
      const config = $.extend(defaultConfig, $slider.data(identifier));

      // backward compatibility
      if (config.itemSelector !== undefined) {
        const classes = config.itemSelector.split(">", 2);
        if (classes.length == 2) {
          config.container = "> " + classes[0].trim();
          config.item = "> " + classes[1].trim();
        }
      }

      if (config.container !== undefined && config.item !== undefined) {
        // move quickinfos of imagemap outside slideshow container because they will hide the overflow causing quickinfo
        // to be cut if it too large. This also avoids the creation of additional dom elements
        const $slidesContainer = $slider.find(config.container);
        const $imageMapQuickInfos = $slidesContainer.find(config.item + " " + ".cm-imagemap__quickinfo");
        $imageMapQuickInfos.insertAfter($slidesContainer);

        const cycleConfig = {
          log: false,
          slides: config.item,
          timeout: config.timeout,
          // 600 for mobile, 1000 for desktop
          speed: (deviceDetector.getLastDevice().type == DEVICE_DESKTOP) ? 1000 : 600,
          pauseOnHover: true,
          fx: "scrollHorz",
          swipe: true,
          maxZ: config.maxZ - 1 // config.maxZ is for prev and next (see below)
        };
        if (config.prev !== undefined) {
          cycleConfig.prev = config.prev;
        }
        if (config.next !== undefined) {
          cycleConfig.next = config.next;
        }

        // detect if current slide has an active quickinfo
        function hasActiveSlideActiveQuickInfo() {
          let result = false;
          const $areas = $slider.find(".cycle-slide-active .cm-imagemap__areas");
          $areas.find(".cm-imagemap__hotzone").each(function () {
            const $button = $(this);
            const config = $.extend({target: undefined}, $button.data("cm-button--quickinfo"));
            result = result || $("#" + config.target).is(".cm-quickinfo--active:not(.cm-quickinfo--main)");
          });
          return result;
        }
        // hide all quickinfos
        function hideAllQuickinfos() {
          $slider.children(".cm-quickinfo").css({"visibility": "hidden", "position": "absolute"});
        }
        // show quickinfos on active slide
        function showQuickinfosInActiveSlide() {
          // show quickinfos for active slide only
          const $areas = $slider.find(".cycle-slide-active .cm-imagemap__areas");
          $areas.find(".cm-imagemap__hotzone").each(function () {
            const $button = $(this);
            const config = $.extend({target: undefined}, $button.data("cm-button--quickinfo"));
            const $quickinfo = $("#" + config.target);
            $quickinfo.css({"position": "", "visibility": ""});
          });
          const areasConfig = $.extend({quickInfoMainId: undefined}, $areas.data("cm-areas"));
          if (areasConfig.quickInfoMainId !== undefined) {
            $("#" + areasConfig.quickInfoMainId).css({"position": "", "visibility": ""});
          }
        }
        // pause auto cycling
        function pause() {
          $slidesContainer.cycle("pause");
        }
        // resume auto cycling
        function resume() {
          $slidesContainer.cycle("resume");
        }

        /*
         * Behaviour of slideshow dependend on quickinfo state
         * 1) current slide stops auto cycling if quickinfo is opened (ignoring main quickinfo)
         * 2) current slide resumes auto cycling if quickinfo is closed (ignoring main quickinfo)
         * 3) if manual interactions are performed (prev/next) auto cycling is resumed
         * 4) if slide with open quickinfo becomes active slide auto cycling is stopped
         */

        // implements 1) + 2)
        $slider.find(".cm-quickinfo:not(.cm-quickinfo--main)").on(quickInfo.EVENT_QUICKINFO_CHANGED, function () {
          if (hasActiveSlideActiveQuickInfo()) {
            pause();
          } else {
            resume();
          }
        });

        // implements 3)
        $slidesContainer.on("cycle-prev", resume);
        $slidesContainer.on("cycle-next", resume);

        // implements 4)
        $slidesContainer.on("cycle-after", function () {
          // if slideshow contains an active quickinfo stop auto cycling
          if (hasActiveSlideActiveQuickInfo()) {
            pause();
          }
        });
        $slidesContainer.on("cycle-initialized", function () {
          hideAllQuickinfos();
          showQuickinfosInActiveSlide();
          $document.trigger(basic.EVENT_LAYOUT_CHANGED);
        });
        $slidesContainer.on("cycle-before", function () {
          hideAllQuickinfos();
          $document.trigger(basic.EVENT_LAYOUT_CHANGED);
        });
        $slidesContainer.on("cycle-after", function () {
          showQuickinfosInActiveSlide();
          // on tablet slideshow varies in height if imagemap is attached
          $document.trigger(basic.EVENT_LAYOUT_CHANGED);
        });
        // always show arrows on touch enabled devices
        if (deviceDetector.getLastDevice().isTouch) {
          $slider.addClass("cm-collection--slideshow-touch");
        }

        $slidesContainer.cycle(cycleConfig);

        // apply z-index to prev/next if defined
        config.prev && findRelativeOrAbsolute($slidesContainer, config.prev).css("z-index", config.maxZ);
        config.next && findRelativeOrAbsolute($slidesContainer, config.next).css("z-index", config.maxZ);
      }
    });
  });

  // lightbox-gallery
  nodeDecorationService.addNodeDecoratorBySelector(".cm-lightbox--gallery", function ($target) {
    magnificPopup($target, {
      gallery: {enabled: true},
      delegate: ":not(.cycle-sentinel) a[data-cm-popup]",
      type: "image"
    });
  });

  // lightbox-inline element (for 360° spinner)
  nodeDecorationService.addNodeDecoratorBySelector(".cm-lightbox--inline", function ($target) {
    magnificPopup($target, {
      type: 'inline',
      mainClass: $target.attr("data-cm-popup-class"),
      delegate: ":not(.cycle-sentinel) a",
      preloader: false,
      callbacks: {
        // trigger event for layout change to get new responsive images in popup
        open: function () {
          if ($target.attr("data-stopopening") == "true") {
            // close immediately if it should not be opened only effects webkit and IE < 11
            magnificPopupApi.close();
          }
          $(document).trigger(basic.EVENT_LAYOUT_CHANGED);

          // overwrite close method to prevent closing by dragging outside from lightbox view
          if ($target.attr("data-stopopening")) {

            // this  overrides "close" method in MagnificPopup object
            magnificPopupApi.instance.close = function () {
              if ($(".mfp-container").attr("data-stopclosing") == "true") {
                logger.log("closing prevented");

                return false;
              }

              /* "proto" variable holds MagnificPopup class prototype
              The above change that we did to instance is not applied to the prototype,
              which allows to call parent method: */
              magnificPopupApi.proto.close.call(this);
            };

          }
        },
        resize: function () {
          $(document).trigger(basic.EVENT_LAYOUT_CHANGED);
        },
        // magnific popup adds a class to hide the inlined element, remove it again on close
        close: function () {
          $target.find(".cm-spinner__canvas").removeClass("mfp-hide");
          $(document).trigger(basic.EVENT_LAYOUT_CHANGED);
        }
      }
    });
  });

  nodeDecorationService.addNodeDecoratorBySelector(".cm-product-assets", function ($target) {
    const $slideshow = $target.find(".cm-product-assets__slideshow");
    const $carousel = $target.find(".cm-product-assets__carousel");
    const $slideshows = $target.find(".cm-product-assets__slideshow, .cm-product-assets__carousel");

    const slideshowDeferrer = $.Deferred(); //indicates if the product asset's slideshow is fully initialized
    const carouselDeferrer = $.Deferred(); // indicates if the product asset's carousel is fully initialized

    const defaultConfig = {
      maxZ: 99
    };
    const config = $.extend(defaultConfig, $target.data("cm-product-assets"));

    // trigger "layoutChanged" event after assets carousel has been fully initialized
    $.when(slideshowDeferrer, carouselDeferrer).done(function() {
      $(document).trigger(basic.EVENT_LAYOUT_CHANGED);
      $slideshow.trigger(PDP_ASSET_READY_EVENT);
    });

    /**
     * Signal a full initialization of the carousel by means of the deferrers. "Fully initialized" means that
     * also the active image are shown.
     * @param event the event containing the deferrer object that represents the carousel component, i.e. the slideshow or the carousel
     */
    function finishProductAssetsInitialization(event) {
      const $activeImg = $(event.target).find(".cycle-slide-active").find("img");
      if ($activeImg) {
        if ($activeImg.height() > 0) { // assuming that the height of the element is zero if the image has not been loaded yet
          event.data.deferrer.resolve();
        } else { // if the image is not loaded yet then we need to wait for the "srcChanged" event
          $activeImg.one("srcChanged", function () {
            event.data.deferrer.resolve();
          });
        }
      }
    }

    // signal "fully initialized" for the slideshow
    $slideshow.on("cycle-post-initialize", {
      deferrer: slideshowDeferrer
    }, finishProductAssetsInitialization);

    // init detail image slideshow
    $slideshow.cycle({
      slides: "> .cm-lightbox",
      timeout: 0,
      fx: "scrollHorz",
      //swipe: true,
      log: false,
      maxZ: config.maxZ - 1
    });

    // carousel is only available if there is more then one asset
    if ($carousel && $carousel.length > 0) {

      // arrows for navigation are only available if there are more then four asset
      const $prevSlideArrow = findRelativeOrAbsolute($carousel, "> .cm-direction-arrow--left");
      const $nextSlideArrow = findRelativeOrAbsolute($carousel, "> .cm-direction-arrow--right");

      /**
       * Adds and removes the "disabled" class to the errors depending on the currently selected slide of
       * the carousel
       */
      function setDisabledState() {
        const opts = $carousel.data('cycle.API').getSlideOpts();
        const currSlide = opts.currSlide;
        const slideCount = opts.slideCount;
        if (currSlide === slideCount - 1) {
          $nextSlideArrow.addClass("disabled");
        } else {
          $nextSlideArrow.removeClass("disabled")
        }
        if (currSlide === 0) {
          $prevSlideArrow.addClass("disabled");
        } else {
          $prevSlideArrow.removeClass("disabled");
        }
      }

      // everything that needs to happen after the initialization of the carousel...
      $carousel.on("cycle-post-initialize", {
        deferrer: carouselDeferrer
      }, function(event) {
        $(this).find(".cycle-slide").css("opacity", 1); // sometimes cycle carousel plugin does not remove opacity of elements
        setDisabledState(); // set initial disable state - which should be that the left arrow is disabled
        finishProductAssetsInitialization(event); // signal "fully initialized" for the carousel
      });

      $carousel.cycle({
        slides: "> .cycle-slide",
        timeout: 0,
        fx: "carousel",
        carouselVisible: 4,
        carouselFluid: true,
        allowWrap: false,
        log: false,
        maxZ: config.maxZ - 1 // for consistency although no next/prev controls exist
      });

      // custom implementation of "prev" command as the cycle2 implementation only advances until all slides of the carousel
      // are visible.
      function prevSlide() {
        const opts = $slideshow.data('cycle.API').getSlideOpts();
        const currSlide = opts.currSlide;
        if (currSlide > 0) {
          $slideshows.cycle("goto", currSlide - 1);
        }
        setDisabledState();
      }
      $prevSlideArrow.on("click", prevSlide);
      $slideshows.on("swiperight", prevSlide); // bind to cycle2.swipe's swipe event

      // custom implementation of "next" command as the cycle2 implementation only advances until all slides of the carousel
      // are visible.
      function nextSlide() {
        const opts = $slideshow.data('cycle.API').getSlideOpts();
        const currSlide = opts.slideNum; // one-based
        const slideCount = opts.slideCount;
        if (currSlide < slideCount) {
          $slideshows.cycle("goto", currSlide); // goto takes zero-based index
        }
        setDisabledState();
      }
      $nextSlideArrow.on("click", nextSlide);
      $slideshows.on("swipeleft", nextSlide); // bind to cycle2.swipe's swipe event

      // apply z-index to prev/next if defined
      $prevSlideArrow.css("z-index", config.maxZ);
      $nextSlideArrow.css("z-index", config.maxZ);

      // synchronize carousel with slideshow - clicking on a carousel's slide means switching the slideshow's image as well
      $carousel.find('.cycle-slide').click(function () {
        const index = $carousel.data('cycle.API').getSlideIndex(this);
        $slideshows.cycle('goto', index);
        setDisabledState();
      });

    } else {

      carouselDeferrer.resolve(); // if there is no carousel then it is just ready
    }

  });

  // init popups
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifier = "cm-popup-control";
    const classButtonActive = "cm-popup-button--active";
    const baseConfig = {
      button: undefined,
      popup: undefined
    };

    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function () {
      const $this = $(this);
      const config = $.extend(baseConfig, $this.data(identifier));

      if (config.button !== undefined && config.popup !== undefined) {
        const $button = $this.find(config.button);
        const $popup = $this.find(config.popup);

        // bind button state to popup state
        $popup.on(basic.popup.EVENT_POPUP_CHANGED, function (event, opened) {
          if (opened) {
            $button.addClass(classButtonActive);
          } else {
            $button.removeClass(classButtonActive);
          }
        });
        $button.on("click", function () {
          // check if popup control is not disabled
          if (!($.extend({disabled: false}, $this.data(identifier)).disabled)) {
            // Toggle popup state
            basic.popup.toggle($popup);
            return false;
          }
        });
      }
    });
  });

  // close all popups if clicked outside popup or ESC is pressed
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifierPopup = ".cm-popup";
    const $body = findAndSelf($target, "body");

    //outside
    $body.on("click", function (event) {
      if ($(event.target).closest(identifierPopup).length === 0) {
        basic.popup.close($body.find(identifierPopup));
      }
    });
    // ESC
    $body.on("keydown", function (event) {
      if (event.keyCode === 27) {
        basic.popup.close($body.find(identifierPopup));
      }
    });
  });

  // initialize cart control (popup + cart indicator icon)
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifier = "cm-cart-control";
    const baseConfig = {cart: undefined};

    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function () {
      const $control = $(this);
      const config = $.extend(baseConfig, $control.data(identifier));

      if (config.cart !== undefined) {
        $control.find(config.cart).on("cartUpdated", function () {
          updateCartControl($control);
        });
      }
      updateCartControl($control);
    });
  });

  // initialize remove from cart buttons
  nodeDecorationService.addNodeDecoratorBySelector(".cm-cart", function ($target) {
    const identifier = "cm-cart-remove-item";
    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function () {
      const $button = $(this);
      const buttonConfig = $.extend({
        id: undefined,
        link: undefined,
        cart: undefined,
        item: undefined,
        quantity: 0
      }, $button.data(identifier));
      const $cart = $button.closest(buttonConfig.cart);
      const cartConfig = $.extend({token: undefined}, $cart.data("cm-cart"));

      if (buttonConfig.id !== undefined && buttonConfig.link !== undefined && cartConfig.token !== undefined) {

        //button clicked
        $button.on("click", function (e) {
          // don't let the add-to-cart button trigger the teaser link
          e.preventDefault();

          if (!$button.hasClass(identifier + "--disabled")) {
            const url = buttonConfig.link;
            basic.ajax({
              type: "POST",
              url: url,
              data: {
                orderItemId: buttonConfig.id,
                action: "removeOrderItem",
                _CSRFToken: cartConfig.token
              },
              dataType: 'text'
            }).done(function () {
              $(".cm-icon--cart").each(function () {
                basic.refreshFragment($(this));
              });
            });
          }
        });
      }
    });
  });

  // add to cart functionality
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifier = "cm-cart-add-item";
    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function () {
      const $button = $(this);
      const buttonConfig = $.extend({id: undefined, link: undefined, cart: undefined}, $button.data(identifier));

      if (buttonConfig.id !== undefined && buttonConfig.link !== undefined) {

        //button clicked
        $button.on("click", function (e) {
          // don't let the add-to-cart button trigger the teaser link
          e.preventDefault();

          const $cart = $(buttonConfig.cart);
          const cartConfig = $.extend({token: undefined}, $cart.data("cm-cart"));
          const url = buttonConfig.link;
          const $icon = $button.find("i");

          if (!$button.hasClass("cm-button--loading")) {

            //disable button and show spinner
            $button.addClass("cm-button--loading");
            $icon.removeClass("icon-checkmark").removeClass("icon-warning");

            // send add-to-cart call
            basic.ajax({
              type: "POST",
              url: url,
              data: {
                externalTechId: buttonConfig.id,
                action: "addOrderItem",
                _CSRFToken: cartConfig.token
              },
              dataType: 'text'
            }).done(function () {
              //show success icon
              $icon.addClass("icon-checkmark");
              window.setTimeout(function () {
                $icon.fadeOut(400, function () {
                  $icon.removeClass("icon-checkmark").removeAttr("style");
                });
              }, 1500);
              //refresh cart
              $(".cm-icon--cart").each(function () {
                basic.refreshFragment($(this));
              });
            }).fail(function () {
              $icon.addClass("icon-warning");
            }).always(function () {
              $button.removeClass("cm-button--loading");
            });
          }
        });
      }
    });
  });

  // initialize search form
  nodeDecorationService.addNodeDecorator(function ($target) {
    const baseConfig = {urlSuggestions: undefined, minLength: undefined};
    findAndSelf($target, ".cm-search-form").each(function () {
      const $search = $(this);
      const config = $.extend(baseConfig, $search.data("cm-search"));
      const $popupSuggestions = $(this).find(".cm-popup--search-suggestions");
      const $listSuggestions = $(this).find(".cm-search-suggestions");
      const $suggestion = $listSuggestions.find(".cm-search-suggestions__item").clone();
      const $prototypeSuggestion = $suggestion.clone();
      let lastQuery = undefined;

      // remove the sample suggestion from dom
      $suggestion.remove();
      $search.find(".search_input").bind("input", function () {
        const $input = $(this);
        const value = $input.val();
        basic.popup.close($popupSuggestions);
        // only show suggestions if the search text has the minimum length
        if (value.length >= config.minLength) {
          // clear suggestions
          nodeDecorationService.undecorateNode($listSuggestions);
          $listSuggestions.html("");
          // save last query
          lastQuery = value;
          basic.ajax({
            url: config.urlSuggestions,
            dataType: "json",
            data: {
              type: "json",
              query: value
            }
          }).done(function (data) {
            // in case ajax calls earlier ajax calls receive their result later, only show most recent results
            if (lastQuery == value) {
              const classNonEmpty = "cm-search-suggestions--non-empty";
              $listSuggestions.removeClass(classNonEmpty);
              // transform search suggestions into dom elements
              $.map(data, function (item) {
                $listSuggestions.addClass(classNonEmpty);
                const $suggestion = $prototypeSuggestion.clone();
                $listSuggestions.append($suggestion);
                $suggestion.html("<b>" + value + "</b>" + item.label.substr(value.length));
                // attribute must exist, otherwise selector will not match
                $suggestion.attr("data-cm-search-suggestion", "");
                // set attribute for jquery (not visible in dom)
                $suggestion.data("cm-search-suggestion", {
                  form: ".cm-search-form",
                  target: ".search_input",
                  value: item.value,
                  popup: ".cm-popup--search-suggestions"
                });
                nodeDecorationService.decorateNode($suggestion);
              });
              // show search suggestions
              basic.popup.open($popupSuggestions);
              // set focus back to input element
              $input.focus();
              $document.trigger(basic.EVENT_NODE_APPENDED, [$suggestion]);
            }
          });
        }
      });
    });
  });

  // update tabs in wcs (e.g. pdp)
  nodeDecorationService.addNodeDecoratorBySelector(".tab_container", function ($target) {
    $target.on("click", function () {
      $document.trigger(basic.EVENT_LAYOUT_CHANGED);
    });
  });

  // initializes search suggestions
  nodeDecorationService.addNodeDecorator(function ($target) {
    // read configuration
    const baseConfig = {form: undefined, target: undefined, value: undefined, popup: undefined};
    const identifier = "cm-search-suggestion";
    const selector = "[data-" + identifier + "]";

    findAndSelf($target, selector).each(function () {
      const $suggestion = $(this);
      const config = $.extend(baseConfig, $suggestion.data(identifier));
      const $popup = $(config.popup);
      // when clicking search suggestions form should be filled with the suggestion and be submitted
      $suggestion.bind("click", function () {
        basic.popup.close($popup);
        $(config.target).val(config.value);
        $(config.form).submit();
      });
    });
  });

  // adjust layout if richtext images (which currently are not adaptive) are loaded
  let updateTimer = 0;
  nodeDecorationService.addNodeDecoratorBySelector(".cm-richtext img", function ($target) {
    $target.on("load", function () {
      if (updateTimer !== 0) {
        clearTimeout(updateTimer);
      }
      updateTimer = setTimeout(function () {
        $document.trigger(basic.EVENT_LAYOUT_CHANGED);
      }, 100);
    });
  });

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  // show/hide "move to top" button
  $window.scroll(function () {
    // display icon after scrolling 1/3 of the document height
    const $buttonTop = $(".cm-icon--button-top");
    if ($window.scrollTop() > $document.height() / 3) {
      $buttonTop.removeClass("cm-hidden");
    } else {
      $buttonTop.addClass("cm-hidden");
    }
  });

  // Synchronizes the layout process. Only one layout event at a time is allowed
  let isLayoutInProgress = false;
  // trigger all functions that should recalculate if the layout has changed
  function layout () {
    logger.log("Layout changed");
    $('.cm-collection--productlisting .cm-category-item__title').equalHeights();
    // only on desktop
    if (deviceDetector.getLastDevice().type == DEVICE_DESKTOP) {
      setMegaMenuItemsWidth();
    }
    // on desktop and tablet
    if (deviceDetector.getLastDevice().type == DEVICE_DESKTOP
            || deviceDetector.getLastDevice().type == DEVICE_TABLET) {
      setMarketingSpotItemsHeight();
    }
    isLayoutInProgress = false;
  }

  $document.on(basic.EVENT_LAYOUT_CHANGED, function () {
    if (!isLayoutInProgress) {
      setTimeout(layout, 500);
    }
    isLayoutInProgress = true;
  });
});
