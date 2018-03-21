import $ from "jquery";

/**
 * Equal Heights Plugin
 *
 * Equalize the heights of elements. Great for columns or any elements
 * that need to be the same size (floats, etc).
 *
 * Version 1.1
 * Copyright (c) Rob Glazebrook (cssnewbie.com) and CoreMedia AG
 *
 * Usage:
 * $(object).equalHeights();
 *
 */

/*! Equal Heights Plugin | Copyright (c) CoreMedia AG */
$.fn.equalHeights = function () {
  this.each(function () {
    $(this).height('auto');
  });

  let tallest = 0;
  this.each(function () {
    if ($(this).height() > tallest) {
      tallest = $(this).height();
    }
  });
  return this.each(function () {
    $(this).height(tallest);
  });
};
