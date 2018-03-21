/**
 * Shim for window.requestAnimationFrame with setTimeout fallback.
 * @see http://www.paulirish.com/2011/requestanimationframe-for-smart-animating
 * @function
 */
const requestAnimFrame = window.requestAnimationFrame ||
                         window.webkitRequestAnimationFrame ||
                         window.mozRequestAnimationFrame ||
                         ((callback) => {
                            window.setTimeout(callback, 1000 / 60);
                          });
export default requestAnimFrame;
