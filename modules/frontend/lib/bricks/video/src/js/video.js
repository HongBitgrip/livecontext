import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import * as logger from "@coremedia/js-logger";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import * as basic from "@coremedia/js-basic";
import "mediaelement";
const flashFallback = require("mediaelement/build/flashmediaelement.swf");

/*! Video Feature | Copyright (c) CoreMedia AG */
/**
 *  CoreMedia Blueprint Javascript Framework Extension for Videos
 *
 *  Provides a common video API to work with different integrations like html5, youtube and vimeo.
 *
 *  {@see start}
 *  {@see EVENT_VIDEO_ENDED}
 *  {@see EVENT_VIDEO_TIME_UPDATED}
 */
const $window = $(window);
const $document = $(document);

/**
 * @event "videoStart" triggered to start the video
 * @deprecated use {@link start} instead, event will be private API soon.
 */
// todo: make private API in the next release as start function should be used.
export const EVENT_VIDEO_START = "start";

/**
 * @event "videoEnded" triggered when a video has ended.
 */
export const EVENT_VIDEO_ENDED = "videoEnded";

/**
 * @event "videoTimeUpdated" triggered when the current playtime has been updated.
 * @type {object}
 * @property {int} position - indicates the current video position in milliseconds
 */
export const EVENT_VIDEO_TIME_UPDATED = "videoTimeUpdated";

/**
 * Starts a given cm-video element
 * @param {jQuery} $video the cm-video element as jquery wrapped object
 */
export function start($video) {
  // for now trigger the old event
  $video.trigger(EVENT_VIDEO_START);
}

// init html5 videos
nodeDecorationService.addNodeDecorator(function ($target) {
  const identifier = "cm-video--html5";
  const selector = ".cm-video--html5";
  findAndSelf($target, selector).each(function () {
    const $video = $(this);

    const me = new MediaElement(
            this,
            {
              plugins: ["flash"],
              pluginPath: "", // needs to be empty
              flashName: flashFallback,
              success: function (mediaElement) {
                mediaElement.addEventListener("loadeddata", function () {
                  logger.log("Video found with duration of " + mediaElement.duration + "ms");
                  $document.trigger(basic.EVENT_LAYOUT_CHANGED);
                }, false);

                // delegate to own event, so the implementation does not rely on MediaElement Plugin
                // additionally youtube/vimeo/.. players could be able to trigger videoEnded event
                mediaElement.addEventListener("ended", function () {
                  logger.log("Video playback ended.");
                  $video.trigger(EVENT_VIDEO_ENDED);
                }, false);

                mediaElement.addEventListener('timeupdate', function () {
                  $video.trigger(EVENT_VIDEO_TIME_UPDATED, {position: Math.floor(this.currentTime) * 1000});
                }, false);
              }
            });
    // when playback is canceled on mobiles, videoEnded is not triggered...
    $video.on("webkitendfullscreen", function () {
      $video.trigger(EVENT_VIDEO_ENDED);
    });
    $video.on(EVENT_VIDEO_START, function () {
      basic.responsive.updateNonAdaptiveVideo($video[0]);
      me.play();
    });
  });
});

// init youtube videos
nodeDecorationService.addNodeDecorator(function ($target) {

  const identifier = "cm-video--youtube";
  const selector = "." + identifier;

  // defines if the youtube api is attached
  let youtubeApiAttached = false;
  // defines if the youtube api is loaded
  let youtubeApiLoaded = false;
  // defines an array of functions to be triggered as soon as the youtube api is ready
  const playerApiQueue = [];

  findAndSelf($target, selector).each(function () {

    const video = this;
    const $video = $(video);

    if (!youtubeApiAttached) {
      youtubeApiAttached = true;

      // attach youtube api
      const tag = document.createElement("script");

      tag.src = "https://www.youtube.com/iframe_api";
      const firstScriptTag = document.getElementsByTagName("script")[0];
      if (firstScriptTag) {
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
      }
    }

    window.onYouTubeIframeAPIReady = function () {
      youtubeApiLoaded = true;

      // trigger all functions in playerApiQueue
      while (playerApiQueue.length > 0) {
        const f = playerApiQueue.pop();
        f();
      }
    };

    function initPlayer() {
      let playOnReady;
      let lastTime = -1;
      let checkTimeTimer;
      /*global YT*/
      let player;

      function checkTime() {
        const currentTime = player.getCurrentTime();
        if (lastTime !== currentTime) {
          lastTime = currentTime;
          $video.trigger(EVENT_VIDEO_TIME_UPDATED, {
            position: Math.floor(currentTime) * 1000
          });
        }
        checkTimeTimer = setTimeout(checkTime, 250);
      }

      player = new YT.Player(video, {
        events: {
          "onReady": function () {
            if (playOnReady) {
              player.playVideo();
              playOnReady = false;
            }
            $document.trigger(basic.EVENT_LAYOUT_CHANGED);
          },
          "onStateChange": function (event) {
            if (event.data === YT.PlayerState.PLAYING) {
              // immidiately trigger checktime after start
              checkTime();
            }
            if (event.data === YT.PlayerState.ENDED) {
              $video.trigger(EVENT_VIDEO_ENDED);
              // immidiately trigger checktime after end
              checkTime();
              if (checkTimeTimer) {
                clearTimeout(checkTimeTimer);
                checkTimeTimer = null;
              }
            }
          }
        }
      });

      $video.on(EVENT_VIDEO_START, function () {
        if (player.playVideo && typeof player.playVideo === "function") {
          player.playVideo();
        } else {
          playOnReady = true;
        }
      });
    }

    if (youtubeApiLoaded) {
      initPlayer();
    } else {
      playerApiQueue.push(initPlayer);
    }
  });
});

// init vimeo videos
nodeDecorationService.addNodeDecorator(function ($target) {

  // Helper function for sending a message to a player
  function postHelper(node, url, action, value) {
    const data = {method: action};
    if (value) {
      data.value = value;
    }

    node.contentWindow.postMessage(JSON.stringify(data), url);
  }

  const identifier = "cm-video--vimeo";
  // vimeo video is and must be iframe
  const selector = "iframe." + identifier;

  findAndSelf($target, selector).each(function () {
    const $video = $(this);
    let protocol = "https";
    // removed check for https for now as vimeo always redirects to https and we cannot catch it from the iframe
    //if (window.location.href.match(/^https:(.+)/)) {
    //  protocol = "https";
    //}
    const url = protocol + ":" + $video.attr("src").split("?")[0];

    $window.on("message", function (e) {

      const data = JSON.parse(e.originalEvent.data);

      if (data["player_id"] == $video.attr("id")) {
        switch (data.event) {
          case 'ready':
            $document.trigger(basic.EVENT_LAYOUT_CHANGED);
            // activate finish event
            postHelper($video[0], url, "addEventListener", "finish");
            postHelper($video[0], url, "addEventListener", "timeupdate");
            break;
          case 'finish':
            $video.trigger(EVENT_VIDEO_ENDED);
            break;
          case 'playProgress':
            $video.trigger(EVENT_VIDEO_TIME_UPDATED, { position: Math.floor(data.data.seconds) * 1000});
        }
      }
    });

    $video.on(EVENT_VIDEO_START, function () {
      postHelper($video[0], url, "play");
    });
  });
});

// handle video teasers
nodeDecorationService.addNodeDecorator(function ($target) {
  const baseConfig = {
    preview: undefined,
    play: undefined,
    player: undefined,
    backlightTimeout: 200,
    features: ['backlight']
  };
  const identifier = "cm-teasable--video";
  const selector = "[data-" + identifier + "]";

  findAndSelf($target, selector).each(function () {
    const $videoTeaser = $(this);
    const config = $.extend(baseConfig, $videoTeaser.data(identifier));
    const $preview = $videoTeaser.find(config.preview);
    const $play = $videoTeaser.find(config.play);
    const $player = $videoTeaser.find(config.player);
    $play.bind("click", function () {
      $preview.addClass("cm-hidden");
      $play.addClass("cm-hidden");
      $player.removeClass("cm-hidden");

      const selector = ".cm-video";
      findAndSelf($player, selector).each(function () {
        const $video = $(this);
        function replacePlayerWithStillImage() {
          $player.addClass("cm-hidden");
          $play.removeClass("cm-hidden");
          $preview.removeClass("cm-hidden");
          // window might have changed while video player was active, e.g. portrait->landscape
          $(document).trigger(basic.EVENT_LAYOUT_CHANGED);
        }
        $video.on(EVENT_VIDEO_ENDED, replacePlayerWithStillImage);
        start($video);
      });

      return false;
    });
  });
});
