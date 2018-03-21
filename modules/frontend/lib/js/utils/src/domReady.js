/**
 * @callback readyCallback
 */
/**
 * Executes the callback function, if the DOM is ready for JavaScript code to execute.
 * @param {readyCallback} f - Callback function to be executed.
 * @function
 */
export const domReady = (f) => {
  // DOM is ready
  if (document.readyState !== 'loading') {
    f();
  }
  // modern Browsers (Firefox, Chrome, Safari, Opera, IE 9-up
  else if (document.addEventListener) {
    const handler = () => {
      document.removeEventListener('DOMContentLoaded', handler, false);
      f();
    };
    document.addEventListener('DOMContentLoaded', handler, false);
  }
  // IE <9
  else {
    const handler = () => {
      if (document.readyState !== 'complete') {
        document.detachEvent('onreadystatechange', handler);
        f();
      }
    };
    document.attachEvent('onreadystatechange', handler);
  }
};