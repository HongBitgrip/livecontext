import $ from "jquery";
import * as logger from "@coremedia/js-logger";
import "imagesloaded/imagesloaded.pkgd.js";

function defaultGetCandidateRatio(id, responsiveImages, containerRatio) {
  const format = responsiveImages[id];
  const ratioWidth = format.ratioWidth;
  const ratioHeight = format.ratioHeight;
  return {
    name: format.name,
    difference: Math.abs(containerRatio - (ratioWidth / ratioHeight)),
    linksForWidth: format.linksForWidth
  };
}

function legacyGetCandidateRatio(id, responsiveImages, containerRatio) {
  const regexp = /^[a-zA-Z_]*(\d+)x(\d+)$/;
  if (!responsiveImages.hasOwnProperty(id)) {
    return null;
  }

  const match = regexp.exec(id);
  if (match !== null) {
    const ratioWidth = parseInt(match[1]);
    const ratioHeight = parseInt(match[2]);
    return {
      name: id,
      difference: Math.abs(containerRatio - (ratioWidth / ratioHeight)),
      linksForWidth: responsiveImages[id]
    };
  }
  return null;
}

function responsiveImage(image) {
  const $image = image instanceof $ ? image : $(image);

  function triggerSrcChanged() {
    $image.trigger({
      type: "srcChanged",
      src: $image.attr("src"),
      maxWidth: $image.data("lastMaxWidth"),
      ratio: $image.data("lastRatio")
    });
  }

  const imagesLoadedPluginExists = typeof $.fn.imagesLoaded === "function";

  if ($image.data("cm-responsive-image-state") === undefined) {
    $image.data("cm-responsive-image-state", "initialized");
    // check if imagesLoaded plugin exists
    if (imagesLoadedPluginExists) {
      if ($image.src && $image.src.length > 0) {
        $image.imagesLoaded(triggerSrcChanged);
      }
    } else {
      $image.on("load", triggerSrcChanged);
    }
  }

  const responsiveImages = $image.data("cm-responsive-image");

  // only run if there is at least one aspect ratio defined
  if (typeof responsiveImages !== "undefined") {

    const $imageContainer = $image.parent();
    let containerWidth = $imageContainer.width();
    let containerHeight = $imageContainer.height();
    if (!containerWidth || !containerHeight) {
      logger.log("Could not load hidden Responsive Image.", $imageContainer);
      return; // image is not visible, do not touch
    }

    // CMS-2905: use retina images, if enabled
    const deviceRatio = window.devicePixelRatio;
    let retinaImagesEnabled = false;
    if (deviceRatio > 1 && $image.data("cm-retina-image")) {
      retinaImagesEnabled = true;
      containerHeight *= deviceRatio;
      containerWidth *= deviceRatio;
    }

    // detect best fitting aspect ratio for box
    const containerRatio = containerWidth / containerHeight;
    let fittingRatio = {
      name: undefined,
      difference: undefined,
      linksForWidth: []
    };

    // @since 1.6
    // default method to retrieve ratio from responsive images data
    let getCandidateRatio = defaultGetCandidateRatio;

    // continue support of the old format that derived the ratio from the name. will be removed in the next major
    // release
    if (!$.isArray(responsiveImages)) {
      logger.log("Using legacy data structure for responsive image setting. Please consider changing to the new format");
      getCandidateRatio = legacyGetCandidateRatio;
    }

    // determine the best fit in respect of the defined ratios and the container ratio
    for (let id in responsiveImages) {
      if (responsiveImages.hasOwnProperty(id)) {
        const candidateRatio = getCandidateRatio(id, responsiveImages, containerRatio);
        if (candidateRatio !== null
                /* jshint ignore:start */
                && typeof fittingRatio.name === "undefined"
                || typeof fittingRatio.difference === "undefined"
                || (fittingRatio.difference > candidateRatio.difference)
        /* jshint ignore:end */
        ) {
          fittingRatio = candidateRatio;
        }
      }
    }

    // only run if a valid ratio is defined
    if (typeof fittingRatio.name !== "undefined") {

      // find fitting link
      let fittingImage = {
        maxWidth: undefined,
        link: undefined
      };
      for (let maxWidth in fittingRatio.linksForWidth) {

        if (!fittingRatio.linksForWidth.hasOwnProperty(maxWidth)) {
          continue;
        }

        const candidateImage = {
          maxWidth: parseInt(maxWidth),
          link: fittingRatio.linksForWidth[maxWidth]
        };

        // calculate fitting image, allow no quality loss
        /* jshint ignore:start */
        if (// case: no fitting image is set
        // -> take the candidate image
        typeof fittingImage.maxWidth === "undefined"
        || typeof fittingImage.link === "undefined"
        // case: fittingImage and candidate are smaller than the container
        // -> take candidate if the image is bigger (lesser quality loss)
        || (fittingImage.maxWidth < containerWidth
        && candidateImage.maxWidth < containerWidth
        && candidateImage.maxWidth > fittingImage.maxWidth)
        // case: fittingImage is smaller and candidate is bigger than the container
        // -> take candidate image (no quality loss is better than any quality loss)
        || (fittingImage.maxWidth < containerWidth
        && candidateImage.maxWidth >= containerWidth)
        // case: fittingImage and candidate are bigger than the container
        // -> take candidate if the image is smaller (no quality loss and smaller size)
        || (fittingImage.maxWidth >= containerWidth
        && candidateImage.maxWidth >= containerWidth
        && candidateImage.maxWidth < fittingImage.maxWidth)) {
          fittingImage = candidateImage;
        }
        /* jshint ignore:end */
      }

      // @since 1.3
      // image can be an <img> tag
      const retinaSuffix = retinaImagesEnabled ? ` (Retina Images enabled with deviceRatio: ${deviceRatio})` : "";
      if ($image.is("img")) {
        // replace link if not the same
        if (fittingImage.link !== $image.attr("src")) {
          logger.log(`Change Responsive Image to aspect ratio: '${fittingRatio.name}' and maxWidth: '${fittingImage.maxWidth}'${retinaSuffix}`, $imageContainer);
          $image.trigger({
            type: "srcChanging",
            src: $image.attr("src"),
            maxWidth: fittingImage.maxWidth,
            ratio: fittingRatio.name
          });
          $image.data("lastMaxWidth", fittingImage.maxWidth);
          $image.data("lastRatio", fittingRatio.name);
          $image.attr("src", fittingImage.link);
          if (imagesLoadedPluginExists) {
            $image.imagesLoaded(triggerSrcChanged);
          }
        }
        // or a background image via style attribute
      } else {
        // replace link if not the same
        if ("background-image: url('" + fittingImage.link + "');" !== $image.attr("style")) {
          logger.log(`Change Responsive Background Image to aspect ratio: '${fittingRatio.name}' and maxWidth: '${fittingImage.maxWidth}'${retinaSuffix}`, $imageContainer);
          $image.data("lastMaxWidth", fittingImage.maxWidth);
          $image.data("lastRatio", fittingRatio.name);
          $image.attr("style", "background-image: url('" + fittingImage.link + "');");
        }
      }
    }
  }
}

export default function (domElementOrJQueryResult) {
  if (domElementOrJQueryResult instanceof $) {
    $.each(domElementOrJQueryResult, function (index, item) {
      responsiveImage(item);
    });
  } else {
    responsiveImage(domElementOrJQueryResult);
  }
}
