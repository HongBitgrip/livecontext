import $ from "jquery";
import responsiveImages from "@coremedia/js-responsive-images";

/**
 * jQuery Plugin that hooks into Bootstrap carousel functionality via the `slid.bs.carousel` to add
 * Responsive Image Resizer Plugin and pagination.
 *
 * @function "$.fn.cmCarousel"
 * @summary jQuery Plugin that hooks for Bootstrap carousel
 * @example
 * CoreMedia carousels can be automatically initialized simply by adding the data attribute `data-cm-carousel`
 * to your carousel container element.
 * ```html
 * <div class="cm-carousel carousel slide" data-cm-carousel='{"interval":"6000"}' ...
 * ```
 * CoreMedia will automatically find and initialize a carousel for any element that contains this data attribute.
 * If you do not want this behavior then do not add the data attribute to your carousel and instead initalize the
 * carousel programmatically by invoking the method on the carousel container element:
 * ```javascript
 * $('[data-cm-carousel]').cmCarousel();
 * ```
 * Auto-initialization is not supported for carousels that are added to the DOM after jQuery's ready event has
 * fired. In this case you will need to programmatically initialize your carousel by invoking the method as shown
 * above.
 *
 * The carousel can be configured by assigning an object with the following properties to the `data-cm-carousel`
 * attribute.
 *
 * | Name     | Type                 | Default            | Description                                 |
 * | -------- | -------------------- | ------------------ | ------------------------------------------- |
 * | pause    | <code>boolean</code> | <code>false</code> | Pause the carousel from sliding, if needed. |
 * | interval | <code>number</code>  | <code>5000</code>  | Interval used for each sliding.             |
 */

function cmCarousel($carousel) {
  if (!$carousel || $carousel.length === 0) {
    return;
  }
  const data = $carousel.data('cm-carousel');
  // pause the carousel from sliding if needed.
  const pause = Boolean(data.pause) || false;

  $carousel.carousel({
    interval: Number(data.interval) || 5000
  });

  if (pause) {
    $carousel.carousel('pause');
  }

  // EVENT BOOTSTRAP CAROUSEL, see http://getbootstrap.com/javascript/#carousel-events
  $carousel.on('slid.bs.carousel', function () {
    const $theCarousel = $(this);
    const $slides = $theCarousel.find('.item');
    const $activeSlide = $theCarousel.find('.item.active');
    const index = $slides.index($activeSlide);
    const $pagination = $theCarousel.find(".cm-carousel__pagination-index");
    //set pagination
    $pagination.text(String(index + 1));
    // reload responsive image for active slide, hidden slides had no image because of height/width=0
    // (not in superhero, they are already loaded, avoid flickering in studio)
    if (!$theCarousel.parent().hasClass('.cm-container--superhero')) {
      responsiveImages($activeSlide.find(".cm-image--responsive"));
    }
  });

  // Stop carousel, if an item has an open quickinfo
  $carousel.on('slide.bs.carousel', function (e) {
    if ($carousel.children(".carousel-inner").hasClass("cm-quickinfo__parent--active")){
      e.preventDefault();
    }
  })
}

$.fn.cmCarousel = function () {
  return this.each(function () {
    cmCarousel($(this));
  });
};

// --- DOCUMENT READY ---
$(function () {
  const $carousels = $('[data-cm-carousel]');
  cmCarousel($carousels);
  // Touch support for carousel
  $carousels.bcSwipe({ threshold: 50 });
});
