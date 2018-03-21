import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import * as logger from "@coremedia/js-logger";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";
import * as basic from "@coremedia/js-basic";
import * as video from "@coremedia/brick-video";

/*! Shoppable Video Feature | Copyright (c) CoreMedia AG */

/**
 *  CoreMedia Blueprint Javascript Framework Extension for Shoppable Video
 */
export default {};

const $document = $(document);

// set teaser for shoppable videos to thesame height, as of the video
nodeDecorationService.addNodeDecorator(function ($target) {
  $document.on(basic.EVENT_LAYOUT_CHANGED, function () {
    findAndSelf($target, ".cm-shoppable").each(function () {
      const $shoppableVideo = $(this);
      const $defaultTeaser = $shoppableVideo.find(".cm-shoppable__default");
      const $allTeasers = $shoppableVideo.find(".cm-shoppable__teaser");
      const $video = $shoppableVideo.find(".cm-shoppable__video");

      if ($defaultTeaser.length > 0 && $allTeasers.length > 0 && $video.length > 0) {

        // initialization
        logger.log("Video is shoppable!");

        const shoppableVideoTeasers = {};
        $allTeasers.each(function () {
          const $teaser = $(this);
          const time = parseInt($teaser.attr("data-cm-video-shoppable-time"));
          if (!isNaN(time)) {
            shoppableVideoTeasers[time] = $teaser;
          }
        });

        $video.on(video.EVENT_VIDEO_ENDED, function () {
          $allTeasers.hide();
          $defaultTeaser.show();
        });

        let $lastTeaser = $defaultTeaser || undefined;
        $video.on(video.EVENT_VIDEO_TIME_UPDATED, function (e, data) {
          const timestamp = data.position;
          const $teaser = shoppableVideoTeasers[timestamp];
          if ($teaser) {
            if ($lastTeaser != $teaser) {
              logger.log("Change Teaser for shoppable Video at timestamp " + timestamp + "ms");
              $allTeasers.hide();
              $teaser.show();
              $lastTeaser = $teaser;
              $document.trigger(basic.EVENT_LAYOUT_CHANGED);
            }
          }
        });
      }
    });
  });
});
