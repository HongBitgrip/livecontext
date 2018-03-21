import svg4everybody from "svg4everybody";
import "imagesloaded/imagesloaded.pkgd.js";
import $ from "jquery";
import { debounce } from "@coremedia/js-utils";
import * as logger from "@coremedia/js-logger";

/**
 * Decode hex strings back to normal
 * @returns {string}
 */
String.prototype.hexDecode = function () {
  let j;
  const hexes = this.match(/.{1,4}/g) || [];
  let back = "";
  for (j = 0; j < hexes.length; j++) {
    back += String.fromCharCode(parseInt(hexes[j], 16));
  }
  return back;
};

/**
 * Encode strings to hex strings
 * @returns {string}
 */
String.prototype.hexEncode = function () {
  let hex, i;
  let result = "";
  for (i = 0; i < this.length; i++) {
    hex = this.charCodeAt(i).toString(16);
    result += ("000" + hex).slice(-4);
  }
  return result;
};

// shim layer with setTimeout fallback
// see http://www.paulirish.com/2011/requestanimationframe-for-smart-animating/
window.requestAnimFrame = (function () {
  return window.requestAnimationFrame ||
          window.webkitRequestAnimationFrame ||
          window.mozRequestAnimationFrame ||
          function (callback) {
            window.setTimeout(callback, 1000 / 60);
          };
})();

/* --- Vars --- */

const $document = $(document);
const $window = $(window);
const deviceAgent = navigator.userAgent.toLowerCase();
let isTouchDevice = (deviceAgent.match(/(iphone|ipod|ipad)/) || deviceAgent.match(/(android)/) || deviceAgent.match(/(iemobile)/) || deviceAgent.match(/iphone/i) || deviceAgent.match(/ipad/i) || deviceAgent.match(/ipod/i) || deviceAgent.match(/blackberry/i) || deviceAgent.match(/bada/i));

/* --- media querie constance --- */
// var XS       = "screen and (max-width: 767px)";
const PORTABLE = "screen and (max-width: 1024px)";
// var SM       = "screen and (min-width: 768px) and (max-width: 1024px)";
// var SMANDUP  = "screen and (min-width: 1024px)";
// var MD       = "screen and (min-width: 1025px)";

/* --- Private Functions --- */

// private function for creating getter and setter the proper way in js
const _createClass = (function () {
  function defineProperties(target, props) {
    for (let i = 0; i < props.length; i++) {
      const descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;

      if ("value" in descriptor) {
        descriptor.writable = true;
      }

      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  return function (Constructor, protoProps, staticProps) {
    if (protoProps) {
      defineProperties(Constructor.prototype, protoProps);
    }
    if (staticProps) {
      defineProperties(Constructor, staticProps);
    }
    return Constructor;
  };
})();

// private inherits function for extending classes in js
function _inherits(subClass, superClass) {
  if (typeof superClass !== "function" && superClass !== null) {
    throw new TypeError("Super expression must either be null or a function, not " + typeof superClass);
  }

  subClass.prototype = Object.create(superClass && superClass.prototype, {
    constructor: {
      value: subClass,
      enumerable: false,
      writable: true,
      configurable: true
    }
  });

  if (superClass) {
    subClass.__proto__ = superClass;
  }
}

// private checkClass function
function _checkClass(instance, Constructor) {
  if (!(instance instanceof Constructor)) {
    throw new TypeError("Cannot call a class as a function");
  }
}

/* --- Events --- */

const EVENT_PREFIX = "coremedia.blueprint.corporate.";
export const EVENT_LAYOUT_CHANGED = EVENT_PREFIX + "layoutChanged";
export const EVENT_SCROLLED = EVENT_PREFIX + "scrolled";

/* --- Private Modules --- */

/* --- ScollEvent --- */
const ScrollEvent = (function () {
  function ScrollEvent(ele, uid) {
    _checkClass(this, ScrollEvent);

    this.ele = ele;
    this.uid = uid;
    this.options = {};
    logger.log("[" + this.constructor.name + "] Teaser found,  uid: " + this.uid + ", init Parallax Scrolling");
  }

  ScrollEvent.prototype.init = function () {
    $document.on(EVENT_LAYOUT_CHANGED, $.proxy(this.resize, this));
    $document.on(EVENT_SCROLLED, $.proxy(this.scroll, this));
  };

  ScrollEvent.prototype.scroll = function (evt, y) {
    logger.log("[" + this.constructor.name + "] uid: " + this.uid + " scroll position: ", y);
  };

  ScrollEvent.prototype.resize = function () {
    logger.log("[" + this.constructor.name + "] uid: " + this.uid + " resize:");
  };

  ScrollEvent.prototype.animate = function () {
    let animation,
            opacity,
            rotate,
            scale,
            translateX,
            translateY;

    for (let i = 0; i < this.keyframes.length; i++) {
      animation = this.keyframes[i];
      translateY = this.calcPropValue(animation, "translateY");
      translateX = this.calcPropValue(animation, "translateX");
      scale = this.calcPropValue(animation, "scale");
      rotate = this.calcPropValue(animation, "rotate");
      opacity = this.calcPropValue(animation, "opacity");
      this.ele.find(animation.selector).css({
        transform: "translate3d(" + translateX + "px, " + translateY + "px, 0) scale(" + scale + ") rotate(" + rotate + "deg)",
        opacity: opacity
      });
    }
  };

  ScrollEvent.prototype.calcPropValue = function (animation, property) {
    let value = animation[property];

    if (value) {
      value = (((value[1] - value[0]) * this.progress / animation.duration) + value[0]);
    } else {
      value = this.getDefaultPropertyValue(property);
    }
    return value;
  };

  ScrollEvent.prototype.getDefaultPropertyValue = function (property) {
    switch (property) {
      case "translateX":
        return 0;
      case "translateY":
        return 0;
      case "scale":
        return 1;
      case "rotate":
        return 0;
      case "opacity":
        return 1;
      default:
        return null;
    }
  };

  _createClass(ScrollEvent, [{
    key: "keyframes",
    get: function () {
      return this.options.keyframes;
    },
    set: function (arr) {
      if (!(arr instanceof Array)) {
        throw new TypeError('Keyframe is not an Array');
      }
      this.options.keyframes = arr;
    }
  }, {
    key: "progress",
    get: function () {
      return this.options.progress;
    },
    set: function (num) {
      if (num === undefined && num === null) {
        throw new TypeError('Progress shouldnt be null or undefined');
      }
      this.options.progress = num;
    }
  }]);

  return ScrollEvent;
})();


/* --- Superhero Module extends ScrollEvent --- */
const Superhero = (function (module) {
  function Superhero(ele, uid) {
    _checkClass(this, Superhero);
    module.call(this, ele, uid);
  }

  _inherits(Superhero, module);

  Superhero.prototype.init = function () {

    this.keyframes = [
      {
        'selector': ".cm-superhero__image",
        'translateY': [0, (this.ele.height() / 100 * 35)],
        'duration': 1
      }
    ];
    this.resize();

    // init has to be called at last so the listener are initialized after the keyframes are set.
    module.prototype.init.call(this);
  };

  Superhero.prototype.scroll = function (evt, y) {
    if (y < $window.height()) {
      this.progress = y / $window.height();
      this.animate();
    }
    //module.prototype.scroll.call(this, evt, y);
  };

  Superhero.prototype.resize = function () {
    this.ele.closest(".cm-carousel").css({
      'height': $window.height(),
      'width': $window.width()
    });
    this.ele.closest(".cm-carousel-inner").css({
      'height': $window.height(),
      'width': $window.width()
    });
    this.ele.css({
      'height': $window.height(),
      'width': $window.width()
    });
    this.ele.find('.cm-superhero__picture').css({
      'height': $window.height(),
      'width': $window.width()
    });
    this.ele.find('.cm-superhero__dimmer').css({
      'height': $window.height(),
      'width': $window.width()
    });
    //module.prototype.resize.call(this, evt);
  };

  return Superhero;
})(ScrollEvent);


/* --- Gap Module extends ScrollEvent --- */
const Gap = (function (module) {
  function Gap(ele, uid) {
    _checkClass(this, Gap);
    module.call(this, ele, uid);
  }

  _inherits(Gap, module);

  Gap.prototype.init = function () {

    this.keyframes = [
      {
        'selector': ".cm-gap__picture-box",
        'translateY': [-(this.ele.find(".cm-gap__picture").height() - this.ele.height()) / 100 * 35, 0],
        'duration': 1
      },
      {
        'selector': ".cm-gap__headline",
        'translateY': [0, 30],
        'duration': 1
      },
      {
        'selector': ".cm-gap__text",
        'translateY': [0, 30],
        'duration': 1
      }
    ];

    // init has to be called at last so the listener are initialized after the keyframes are set.
    module.prototype.init.call(this);
  };

  Gap.prototype.scroll = function (evt, y) {
    const windowY = y,
            elementY = this.ele.offset().top,
            centerY = (this.ele.height() - $window.height()) / 2,
            diff = elementY - windowY + centerY,
            oDiff = $window.height() + centerY;

    if ((diff + oDiff > 0) && (2 * oDiff > diff + oDiff)) {
      this.progress = Math.abs((diff + oDiff) / (2 * oDiff) - 1);
      this.animate();
    }
    //module.prototype.scroll.call(this, evt, y);
  };

  Gap.prototype.resize = function (/*evt*/) {
    //module.prototype.resize.call(this, evt);
  };

  return Gap;
})(ScrollEvent);

/**
 * media query
 * see https://github.com/paulirish/matchMedia.js/
 */
const breakpoint = function () {

  let styleMedia = (window.styleMedia || window.media);

  // For those that don't support matchMedium
  if (!styleMedia) {
    let style = document.createElement('style'),
            script = document.getElementsByTagName('script')[0],
            info = null;

    style.type = 'text/css';
    style.id = 'matchmediajs-test';

    script.parentNode.insertBefore(style, script);

    // 'style.currentStyle' is used by IE <= 8 and 'window.getComputedStyle' for all other browsers
    info = ('getComputedStyle' in window) && window.getComputedStyle(style, null) || style.currentStyle;

    styleMedia = {
      matchMedium: function (media) {
        const text = '@media ' + media + '{ #matchmediajs-test { width: 1px; } }';

        // 'style.styleSheet' is used by IE <= 8 and 'style.textContent' for all other browsers
        if (style.styleSheet) {
          style.styleSheet.cssText = text;
        } else {
          style.textContent = text;
        }

        // Test if media query is true or false
        return info.width === '1px';
      }
    };
  }

  return function (media) {
    return {
      matches: styleMedia.matchMedium(media || 'all'),
      media: media || 'all'
    };
  };
}();

/* --- Public Modules --- */
/**
 * Scroll Event Trigger, based on rAF
 * see: http://www.html5rocks.com/en/tutorials/speed/animations/
 */
export function scroller() {

  let latestKnownScrollY = 0,
          ticking = false;

  logger.log("Initialize Scroller");
  $window.on("scroll", function () {
    latestKnownScrollY = $window.scrollTop();
    _requestTick();
  });

  /* --- local functions --- */

  function _requestTick() {
    if (!ticking) {
      /*global requestAnimFrame*/
      requestAnimFrame(_update);
    }
    ticking = true;
  }

  function _update() {
    ticking = false;
    $document.trigger(EVENT_SCROLLED, [latestKnownScrollY]);
  }
}

/**
 * Header transparency on scrolling
 */
export function header() {
  const $header = $(".cm-header");
  const $grid = $(".cm-grid");
  const $nav = $('.cm-nav-collapse');
  const $button = $('.cm-header__button');

  // event listener, add Class
  $document.on(EVENT_SCROLLED, function (event, y) {
    if (y > $window.height() - $(".cm-header").height()) {
      $header.addClass("cm-header--scrolled");
    } else {
      $header.removeClass("cm-header--scrolled");
    }
  });

  // EVENT BOOTSTRAP COLLAPSABLE, see http://getbootstrap.com/javascript/#collapse-events
  // used by header navigation
  $nav.on("show.bs.collapse", function () {
    logger.log("header collapse shown");
    $header.addClass("cm-header--open");
    $button.removeClass('collapsed');
    if (breakpoint(PORTABLE).matches) {
      $grid.addClass("cm-grid--disabled-scrolling");
    }
  });

  $nav.on("hide.bs.collapse", function () {
    logger.log("header collapse hidden");
    $header.removeClass("cm-header--open");
    $button.addClass('collapsed');
    $grid.removeClass("cm-grid--disabled-scrolling");
  });
}


/**
 * Superhero Teaser
 */
export function superhero() {

  if (!isTouchDevice) {
    const $container = $('[data-cm-module="superhero"]');

    // Check for Superhero
    if ($container.length) {
      const superhero = new Superhero($container, 0);
      superhero.init();

      $container.each(function () {
        const $this = $(this);
        // CMS-4906: muting videos from youtube and vimeo
        if ($this.hasClass("cm-superhero--video")) {
          logger.log("Superhero Teaser has video");
          const superHeroVideo = $this.find(".cm-superhero__video");
          const superHeroVideoPlayerId = superHeroVideo.attr("id");

          //youtube
          if (superHeroVideo.hasClass("cm-video--youtube")) {
            logger.log("Muting Superhero Teaser with Youtube video");
            // insert youtube api javascript (if not already exist)
            if (!$('script[src*="youtube.com/iframe_api"]').length) {
              const ytTag = document.createElement('script');
              ytTag.src = "//www.youtube.com/iframe_api";
              const ytFirstScriptTag = document.getElementsByTagName('script')[0];
              ytFirstScriptTag.parentNode.insertBefore(ytTag, ytFirstScriptTag);

              window.onYouTubeIframeAPIReady = function () {
                //mute video
                /*global YT*/
                const cmYoutubePlayer = new YT.Player(superHeroVideoPlayerId, {
                  events: {
                    'onReady': function () {
                      cmYoutubePlayer.mute();
                    }
                  }
                });
              };
            }

            // vimeo
          } else if (superHeroVideo.hasClass("cm-video--vimeo")) {
            logger.log("Muting Superhero Teaser with Vimeo video");
            // insert the vimeo api (if not already exist)
            if (!$('script[src*="vimeocdn.com/js/froogaloop2"]').length) {
              const vTag = document.createElement('script');
              vTag.src = "//f.vimeocdn.com/js/froogaloop2.min.js";
              const vFirstScriptTag = document.getElementsByTagName('script')[0];
              vFirstScriptTag.parentNode.insertBefore(vTag, vFirstScriptTag);

              // register load event
              vTag.onload = vTag.onreadystatechange = function () {
                /*global $f*/
                // get the player and set volume to 0 (mute)
                const iframe = $('#' + superHeroVideoPlayerId)[0], player = $f(iframe);
                // mute vimeo
                player.addEvent('ready', function () {
                  player.api('setVolume', 0);
                });
              };
            }
          }
        }
      });
    }
  }
}

/**
 * Gaps Teaser
 */
export function gaps() {

  if (!isTouchDevice) {
    const $container = $('[data-cm-module="gap"]');

    // Check for Gaps
    if ($container.length) {
      $container.each(function (uid) {
        const gap = new Gap($(this), uid);
        gap.init();
      });
    }
  }
}

/**
 * Sticky elements for all elements with class "cm-sticky"
 * @see http://getbootstrap.com/javascript/#affix
 */
export function affix() {
  let compareMain = true;
// desktop = set sticky
  if ($(window).width() > 1024) {
    const $mainContainer = $('#cm-main');
    if ($mainContainer.length) {
      compareMain = true;
    }

    $('.cm-sticky').each(function () {
      const $this = $(this);
      // only if window is taller than sidebar
      if ($this.outerHeight() < $(window).height()) {
        if (compareMain && $this.outerHeight() > $mainContainer.outerHeight()) {
          logger.log(".cm-sticky is taller than main");
          return false;
        }
        if (!$this.hasClass("cm-sticky--offset")) {
          logger.log("add bootstrap affix to .cm-sticky");
          const topOffset = $this.offset().top;
          $this.addClass("cm-sticky--offset").removeClass("cm-sticky--no-sticky");
          // add bootstrap affix
          $this.affix({
            offset: {
              top: topOffset,
              bottom: function () {
                return (this.bottom = $('#cm-footer').outerHeight(true));
              }
            }
          });
        }
      }
    });
    // mobile and tablet
  } else {
    $('.cm-sticky').each(function () {
      const $this = $(this);
      // disable affix on mobile and tablet, if active.
      if ($this.hasClass("cm-sticky--offset")) {
        logger.log("disable bootstrap affix behavior");
        $this.removeClass("cm-sticky--offset affix").addClass("cm-sticky--no-sticky");
      }
    });
  }
}

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function () {

  const $window = $(window);
  const $document = $(document);

  logger.log("Corporate DOM RDY");

  // remove the spinner and event listener, when images are loaded
  $(".cm-image--loading").each(function () {
    const $image = $(this);
    const callback = function () {
      logger.log("Responsive Image loaded, remove spinner");
      $image.removeClass("cm-image--loading");
    };
    if (typeof $.fn.imagesLoaded === typeof callback) {
      $image.imagesLoaded().done(callback);
    } else {
      $image.on("load", callback);
    }
  });

  // fallack for svgs in old browsers, used for play overlay icon
  try {
    logger.log("Enable svg4everybody");
    svg4everybody();
  }
  catch (err) {
    logger.log(err.message);
  }

  // init modules: scroller, superhero, gaps, carousel
  scroller();
  header();
  gaps();
  affix();

  // trigger layout changed event if the size of the window changes using smartresize plugin
  $window.on("resize", {}, debounce(function () {
    $document.trigger(EVENT_LAYOUT_CHANGED);
  }));

  // prevent iOS two click bug

  $('.cm-square__wrapper a, .cm-teasable__text-content a').on('touchstart', function () {
    $(this).data('distance', $('body').scrollTop());
  }).on('touchend', function () {
    if ($(this).data('distance') == $('body').scrollTop()) {
      $(this).click();
    }
  });

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  // Click on scroll indicator

  $('.cm-container--superhero .cm-container__more .glyphicon-chevron-down').on('click', function () {
    $('html, body').animate({
      scrollTop: $('.cm-container--superhero').height()
    }, 'slow');
  });

  // EVENT_LAYOUT_CHANGED
  $document.on(EVENT_LAYOUT_CHANGED, function () {
    logger.log("Window resized");
    // set sidebar sticky on desktop
    affix();
  });
});
