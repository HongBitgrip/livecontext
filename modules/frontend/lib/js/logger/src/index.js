/**
 * Coremedia Logger module.
 *
 * The Logger is disabled by default. It will be enabled if developer mode is enabled.
 *
 * usage:
 * 1) enable logging:
 * setLevel(LEVEL.ALL);
 *
 * 2) print to log:
 * log("log this");
 * debug("debug this");
 * info("info this");
 * warn("warn this");
 * error("error this");
 *
 * 3) disable logging:
 * setLevel(LEVEL.OFF);
 *
 * @module logger
 */
import { domReady } from '@coremedia/js-utils';
import * as Logger from './logger.js';

// check cookie if developerMode is active
if (document.cookie.indexOf("cmUserVariant=") > 0) {
  Logger.setLevel(Logger.LEVEL.ALL);
}

// document ready, check for developer mode element in DOM as fallback
domReady(() => {
  if (document.querySelector('[data-cm-developer-mode]') && Logger.getCurrentLevelName() === Logger.LEVEL.OFF.toString()) {
    Logger.setLevel(Logger.LEVEL.ALL);
  }
});

export * from "./logger.js";
