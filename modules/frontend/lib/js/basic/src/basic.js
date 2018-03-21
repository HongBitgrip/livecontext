import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import { debounce } from "@coremedia/js-utils";
import * as logger from "@coremedia/js-logger";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import * as accordion from "./basic.accordion";
import * as infiniteScroll from "./basic.infiniteScroll";
import * as popup from "./basic.popup";
import * as dropdown from "./basic.dropdown";
import * as responsive from "./basic.responsive";

export {accordion, infiniteScroll, popup, dropdown, responsive};

/**
 *  CoreMedia Blueprint Javascript Framework
 *  including following functions
 *
 *  - infiniteScroll
 *  - toggle
 *  - dropdown
 *  - accordion
 */
const $document = $(document);

// Events
const EVENT_PREFIX = "coremedia.blueprint.basic.";
export const EVENT_LAYOUT_CHANGED = EVENT_PREFIX + "layoutChanged";
export const EVENT_NODE_APPENDED = EVENT_PREFIX + "nodeAppended";

/**
 * Redirects the user to given redirectUrl if the redirectUrl is not part of the current
 * url to prevent infinite loops.
 *
 * @param redirectUrl
 */
export function redirectTo(redirectUrl) {
  // prevent infinite loop of redirects
  if (window.location.href.indexOf(redirectUrl) < 0) {
    window.location.href = redirectUrl + "?next=" + encodeURI(window.location.href);
  }
}

/**
 * Replace "$nextUrl$" in all data-href and store as href attribute.
 * Assumes that if the page contains a form with a nextUrl hidden input field, the form is already loaded.
 *
 * @param {jQuery} $target
 */
export function renderFragmentHrefs($target) {
  let nextUrl;
  if (window.location.pathname.match(/^\/dynamic\//) || window.location.pathname.match(/^\/blueprint\/servlet\/dynamic\//)) {
    // we are inside a web flow, try to find "nextUrl" hidden input field value, else leave nextUrl blank
    nextUrl = $('input:hidden[name="nextUrl"]').val() || "";
  } else {
    // for all other pages, take the current page as the next page after login
    nextUrl = window.location.href;
    //remove current scheme in case the scheme is changed before the redirect
    nextUrl = nextUrl.replace(/^(http|https):(.+)/, "$2");
  }

  const selector = "a[data-href]";
  findAndSelf($target, selector).each(function () {
    const $this = $(this);
    $this.attr("href", $this.data("href").replace(/\$nextUrl\$/g, encodeURIComponent(nextUrl)));
  });
}

/**
 * Changes a given target
 * @param $target The target the update is to be applied to
 * @param $update The update to add to DOM
 * @param replaceTarget if TRUE target will be replaced with the given target, otherwise only inner nodes will be removed
 */
export function updateTarget($target, $update, replaceTarget) {
  if (replaceTarget) {
    nodeDecorationService.undecorateNode($target);
    $target.replaceWith($update);
  } else {
    $target.children().each(function () {
      nodeDecorationService.undecorateNode(this);
    });
    $target.empty().append($update);
  }
  nodeDecorationService.decorateNode($update);
  $document.trigger(EVENT_NODE_APPENDED, [$update]);
}

/**
 * Extend jQuery Ajax Function
 *
 * @param {object} options
 * @returns $.ajax()
 */
export function ajax(options) {
  /* always set xhr headers for CORS */
  const cmOptions = {
    headers: {'X-Requested-With': 'XMLHttpRequest'},
    xhrFields: {withCredentials: true},
    global: false,
    url: undefined
  };

  options = $.extend({}, cmOptions, options);

  // IE9 does not support CORS w/ credentials, so make sure the host matches the current host
  const isIE9 = /MSIE (9.\d+);/.test(navigator.userAgent);
  if (isIE9 && options.url !== undefined) {
    options.url = options.url.replace(/\/\/([^/]+)\/(.+)/, "//" + window.location.host + "/$2");
    // set Origin header if not present and url is absolute
    const isAbsolute = new RegExp("^([a-z]+://|//)");
    if (options.headers["Origin"] === undefined && isAbsolute.test(options.url)) {
      options.headers["Origin"] = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port : "");
    }
  }

  return $.ajax(options);
}

/**
 * Updates a given target with the result of the provided url.
 *
 * Only the last triggered update will have an effect if multiple updates are triggered without waiting for
 * the ajax request to be finished.
 *
 * @param $target target to be updated
 * @param requestConfig the request config to be used containing
 *        url: the url to retrieve the new target from
 *        params: additional request params (optional)
 *        method: the request method (optional, defaults to GET)
 * @param replaceTarget (default) true, if false replaces only the child elements of the target
 * @param {updateTargetWithAjaxResponseCallback} callback to be triggered on success
 */
export function updateTargetWithAjaxResponse($target, requestConfig, replaceTarget, callback) {
  requestConfig = $.extend({url: undefined, params: {}, method: "GET"}, requestConfig);

  if (typeof replaceTarget === "undefined") {
    replaceTarget = true;
  }
  if (requestConfig.url !== undefined) {
    const FRAGMENT_REQUEST_COUNTER = "cm-fragment-request-counter";
    const FRAGMENT_LOADING_CLASS = "cm-fragment--loading";
    const requestId = ($target.data(FRAGMENT_REQUEST_COUNTER) || 0) + 1;
    $target.data(FRAGMENT_REQUEST_COUNTER, requestId);

    const isOutdated = function () {
      // if $target is no longer in DOM or the request is not the current latest request: ignore update
      return (!$.contains(document.documentElement, $target[0]))
              || (requestId !== $target.data(FRAGMENT_REQUEST_COUNTER));
    };

    $target.addClass(FRAGMENT_LOADING_CLASS);
    ajax({
      type: requestConfig.method,
      url: requestConfig.url,
      data: requestConfig.params,
      dataType: "text"
    }).done(function (data, _, jqXHR) {
      if (isOutdated()) {
        return;
      }
      let $html = undefined;
      if (jqXHR.status === 200) {
        $html = $(data);
        updateTarget($target, $html, replaceTarget);
      }
      if (callback) {
        callback(jqXHR, $html);
      }
    }).fail(function (jqXHR) {
      if (callback) {
        callback(jqXHR);
      }
    }).always(function () {
      if (isOutdated()) {
        return;
      }
      $target.removeClass(FRAGMENT_LOADING_CLASS);
    });
  }
}

/**
 * @callback updateTargetWithAjaxResponseCallback
 * @param jqXHR the jQuery XHR object
 * @param {jQuery} $html the new html if the request was successful
 */

/**
 * Refreshes a refreshable fragment by reading its configuration.
 *
 * @param $fragment the refreshable fragment to refresh
 * @param callback to be triggered on success
 * @param requestParams additional request params
 */
export function refreshFragment($fragment, callback, requestParams) {
  const config = $.extend({"url": undefined}, $fragment.data("cm-refreshable-fragment"));
  const requestConfig = {
    url: config.url,
    params: requestParams
  };
  updateTargetWithAjaxResponse($fragment, requestConfig, true, callback);
}

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function () {

  const $window = $(window);

  // append to dom ready (will be executed after all dom ready functions have finished)
  $(function () {
    nodeDecorationService.decorateNode(document);
  });

  // load all dynamic fragments. The special header X-Requested-With is needed by the CAE to identify
  // the request as an Ajax request
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifier = "cm-fragment";
    const selector = "[data-" + identifier + "]";
    findAndSelf($target, selector).each(function () {
      const $fragment = $(this);
      const url = $(this).data(identifier);
      ajax({
        url: url,
        dataType: "text"
      }).done(function (html) {
        const $html = $(html);
        nodeDecorationService.undecorateNode($fragment);
        $fragment.replaceWith($html);
        nodeDecorationService.decorateNode($html);
        $document.trigger(EVENT_NODE_APPENDED, [$html]);
      });
    });
  });

  // this will substitute all data-hrefs rendered by ESI
  nodeDecorationService.addNodeDecorator(renderFragmentHrefs);

  // initializes the drop down menu
  nodeDecorationService.addNodeDecorator(function ($target) {
    const selector = ".cm-dropdown";
    findAndSelf($target, selector).each(function () {
      $(this).on(dropdown.EVENT_DROPDOWN_CHANGED, function () {
        $document.trigger(EVENT_LAYOUT_CHANGED);
      });
      dropdown.init(this);
    });
  });

  // adds removes spinner if an image has finished loading
  nodeDecorationService.addNodeDecoratorBySelector(".cm-image--loading", function ($target) {
    const callback = function () {
      $target.removeClass("cm-image--loading");
    };
    if (typeof $.fn.imagesLoaded === typeof callback) {
      $target.imagesLoaded(callback);
    } else {
      $target.on("load", callback);
    }
  });

  // handle closing of notification box
  nodeDecorationService.addNodeDecorator(function ($target) {
    const selector = ".cm-notification__dismiss";
    findAndSelf($target, selector).click(function () {
      $(this).closest(".cm-notification").fadeOut();
    });
  });

  // add readmore functionality if text is too long
  nodeDecorationService.addNodeDecoratorByData({lines: undefined}, "cm-readmore", function ($target, config) {
    const blockReadMore = "cm-readmore";
    // read the line height for the given target
    let lineHeight = $target.css("line-height");
    // only proceed if config is valid and lineHeight could be retrieved
    if (config.lines !== undefined && lineHeight !== undefined) {
      const $wrapper = $target.find("." + blockReadMore + "__wrapper");
      const $buttonbar = $target.find("." + blockReadMore + "__buttonbar");
      const $buttonMore = $buttonbar.find("." + blockReadMore + "__button-more");
      const $buttonLess = $buttonbar.find("." + blockReadMore + "__button-less");

      // calculate line height in px
      if (lineHeight.indexOf("px") > -1) {
        // line height is already in px, just remove the unit
        lineHeight = lineHeight.replace("px", "");
      } else {
        // line height is relative to font-size, calculate line height by multiplying its value with font-size
        lineHeight = lineHeight * $target.css("font-size").replace("px", "");
      }
      const maxHeight = Math.floor(lineHeight * config.lines);
      // enable readmore functionality if text without the readmore button exceeds the maximum height
      // it would make no sense to add a readmore button if it would take more space as rendering the full text
      if ($wrapper.height() - 2 * $buttonbar.height() > maxHeight) {
        $target.addClass(blockReadMore + "--enabled");
        // default without any action by the user ist the non expanded (less) version
        $target.addClass(blockReadMore + "--less");
        $wrapper.css("max-height", maxHeight);
        $buttonMore.on("click", function () {
          $target.removeClass(blockReadMore + "--less");
          $target.addClass(blockReadMore + "--more");
          $wrapper.css("max-height", "");
          $document.trigger(EVENT_LAYOUT_CHANGED);
        });
        $buttonLess.on("click", function () {
          $target.removeClass(blockReadMore + "--more");
          $target.addClass(blockReadMore + "--less");
          $wrapper.css("max-height", maxHeight);
          $document.trigger(EVENT_LAYOUT_CHANGED);
        });
        $buttonLess.on("click", function () {
          $target.removeClass(blockReadMore + "--more");
          $target.addClass(blockReadMore + "--less");
          $wrapper.css("max-height", maxHeight);
          $document.trigger(EVENT_LAYOUT_CHANGED);
        });
        $document.trigger(EVENT_LAYOUT_CHANGED);
      }
    }
  });

  // initially updates new non-adaptive boxes
  nodeDecorationService.addNodeDecorator(function ($target) {
    const identifier = "cm-non-adaptive-content";
    const selector = "[data-" + identifier + "]";
    const imageSelector = "img" + selector;
    findAndSelf($target, imageSelector).each(function () {
      const image = this;
      const $image = $(image);
      const callback = function () {
        responsive.updateNonAdaptiveImage(image);
      };
      if (typeof $.fn.imagesLoaded === typeof callback) {
        $image.imagesLoaded(callback);
      } else {
        $image.on("load", callback);
      }
    });
    const videoSelector = "iframe" + selector + ", video" + selector;
    findAndSelf($target, videoSelector).each(function () {
      responsive.updateNonAdaptiveVideo(this);
    });
  });


  // trigger layout changed event if the size of the window changes using smartresize plugin
  $window.on("resize", {}, debounce(function () {
    logger.log("Window resized");
    $document.trigger(EVENT_LAYOUT_CHANGED);
  }));

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  $document.on(EVENT_LAYOUT_CHANGED, responsive.updateLayout);
});
