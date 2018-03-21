import $ from "jquery";

import * as bem from "./utils.bem";

export { bem };

/*! Utils Plugin | Copyright (c) CoreMedia AG */

/**
 * Executes a jQuery find on the given $self but also takes $self into account (in addition to its children).
 *
 * @param $self {jQuery} the jQuery result to search on
 * @param selector {string} the selector to search for
 * @return {jQuery} the search result as jQuery result
 */
export function findAndSelf($self, selector) {
  return $self.filter(selector).add($self.find(selector));
}

/**
 * Finds relative to the given $self or in whole DOM based on existence of ">" in selector.
 *
 * @param $self {jQuery} the element to perform the relative search from
 * @param selector the selector
 * @returns {jQuery} the search result of the given selector
 */
export function findRelativeOrAbsolute($self, selector) {
  if (typeof selector === typeof "string" && (/^\s*[>|+~]/).test(selector)) {
    return $self.find(selector);
  }
  return $self.constructor(selector);
}

/**
 * Add pseudo for selection by data attribute managed by jQuery (not equal to search for [data-...])
 */
$.extend($.expr[":"], {
  data: $.expr.createPseudo ?
          $.expr.createPseudo(function( dataName ) {
            return function( elem ) {
              return !!$.data( elem, dataName );
            };
          }) :
    // support: jQuery <1.8
          function( elem, i, match ) {
            return !!$.data( elem, match[ 3 ] );
          }
});
