import $ from "jquery";

/**
 * Generic infinite scroll functionality
 *
 * Setup a new scrollbox.
 *
 * @scrollbox {object} scrollbox to be used (must contain child-element with class "scrollwrapper")
 * @hasNext {function()} callback function called to determine if there are more items to be loaded
 * @addData {function(function())} callback function called if there are items to add. has a callback as param indicating that data is added
 * @additionalSpace {number} defines the additionalSpace to be added to the scrollbox to indicate that there is "more" in pixels
 */
export function init(scrollbox, hasNext, addData, additionalSpace) {
  scrollbox.overflow = "overlay";

  /**
   * Refresh the infinite scroll
   * If hasNext() function returns true infinite scroll functionality is added else removed
   *
   * @param {object} scrollbox
   */
  function refresh(scrollbox) {
    const $scrollbox = $(scrollbox);
    // set dimensions of scrollwrapper(s) inside scrollbox
    $scrollbox.find(".scrollwrapper").each(function () {
      const $this = $(this);
      // save old scrolling position
      const backup = this.scrollTop;
      // by default height is set to "auto" indicating that there is no more content
      $this.height("auto");
      // if callback returns that there is more content
      if (hasNext()) {
        // extend height by additional space configured
        $this.height($this.height() + additionalSpace);
      }
      // restore old scrolling position
      this.scrollTop = backup;
    });
  }

  // by default loading of data by scrolling is not locked
  let loadLock = false;

  // bind trigger to scroll event of scrollbox
  $(scrollbox).on("scroll", function () {
    // only perform checks if loading of data is not locked
    if (!loadLock) {
      // detect if scrollBox is scrolled down to the bottom of the wrapper (only react in that case)
      if (hasNext() && (this.scrollHeight - this.scrollTop) === $(this).height()) {
        // lock loading of data
        loadLock = true;
        // trigger given callback function
        addData(function () {
          // refresh scrollbox
          refresh(scrollbox);
          // release the lock
          loadLock = false;
        });
      }
    }
  });

  // initiate scrollbox by refreshing
  refresh(scrollbox);
}