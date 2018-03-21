import $ from "jquery";

function resetNonAdaptiveContent($content) {
  $content.parent().removeClass("cm-non-adaptive-content-wrapper");
  $content.removeClass("cm-non-adaptive-content");
  $content.removeClass("cm-non-adaptive-content--fit-height");
  $content.css("margin-top", "");
  $content.css("margin-left", "");
}

/**
 * private function to adjust the size of non-adaptive content
 *
 * @param {jQuery} $content jQuery wrapped dom element to be adjusted
 * @param {number} baseRatio ratio that the content should be adjusted to
 * @param {number} boxRatio ratio the content currently has
 * @param {boolean} allowOverflow defines overflow behaviour of the adjustment:
 *                  if true content will be cut to fit the box after proper resizing
 *                  if false content will not be cut after proper resizing creating horizontal or veritical borders
 */
function adjustNonAdaptiveContent($content, baseRatio, boxRatio, allowOverflow) {
  // add class cm-non-adaptive-content
  $content.addClass("cm-non-adaptive-content");
  // add class to parent container
  $content.parent().addClass("cm-non-adaptive-content-wrapper");

  let adjustment;

  // detect if a horizontal repositioning is needed
  if ((allowOverflow && baseRatio > boxRatio) || (!allowOverflow && baseRatio <= boxRatio)) {
    // horizontal repositioning is needed
    adjustment = (1 - baseRatio / boxRatio) / 2;
    $content.addClass("cm-non-adaptive-content--fit-height");
    // adjust positioning to the left to match the expected result using percentage (for responsive layout)
    $content.css("margin-top", "");
    $content.css("margin-left", (adjustment * 100) + "%");
  } else {
    // vertical repositioning is needed
    adjustment = ((1 / boxRatio - 1 / baseRatio) / 2);
    $content.removeClass("cm-non-adaptive-content--fit-height");
    // adjust positioning to the top to match the expected result using percentage (for responsive layout)
    $content.css("margin-top", (adjustment * 100) + "%");
    $content.css("margin-left", "");
  }
}

/**
 * update a single given non-adaptive image
 * @param {Image} image dom node
 */
export function updateNonAdaptiveImage(image) {
  const $image = $(image);

  resetNonAdaptiveContent($image);

  const config = $.extend({overflow: false}, $image.data("cm-non-adaptive-content"));
  const $box = $image.parent();

  const baseImage = new Image();
  baseImage.src = image.src;

  const baseRatio = baseImage.width / baseImage.height;
  const boxRatio = $box.width() / $box.height();

  adjustNonAdaptiveContent($image, baseRatio, boxRatio, config.overflow);
}

/**
 * update a single given non-adaptive video
 * @param {HTMLVideoElement} video
 */
export function updateNonAdaptiveVideo(video) {
  const $video = $(video);

  resetNonAdaptiveContent($video);

  const config = $.extend({overflow: false}, $video.data("cm-non-adaptive-content"));
  const $box = $video.parent();

  let baseRatio = $video.width() / $video.height();
  if ($video.is("video")) {
    baseRatio = video.videoWidth / video.videoHeight;
  }
  const boxRatio = $box.width() / $box.height();

  adjustNonAdaptiveContent($video, baseRatio, boxRatio, config.overflow);
}

/**
 * updates non adaptive images and videos for the whole page
 */
export function updateNonAdaptiveContents() {
  $(document.body).find("img[data-cm-non-adaptive-content]").each(function () {
    updateNonAdaptiveImage(this);
  });
  $(document.body).find("video[data-cm-non-adaptive-content]").each(function () {
    updateNonAdaptiveVideo(this);
  });
}

/**
 * Updates the layout by recalculating responsive images, hotzones and adaptive contents.
 */
export function updateLayout() {
  // recalculate non adaptive contents
  updateNonAdaptiveContents();
}