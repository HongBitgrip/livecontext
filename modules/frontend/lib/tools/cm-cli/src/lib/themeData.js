"use strict";

/**
 * Returns the string in title case
 * @param  {string} str
 * @return {string}
 */
const titleCase = str => str.replace(str[0], str[0].toUpperCase());

/**
 * returns content for package.json
 * @param  {string} themeName
 * @return {string}
 */
const initPackageJson = (themeName, mainJSFile) => {
  const content = {
    name: `@coremedia/${themeName}-theme`,
    version: "1.0.0",
    private: true,
    scripts: {
      build: "webpack",
      start: "cm monitor",
      "theme-importer": "cm theme-importer",
    },
    __comment__dependencies__: {
      __comment__:
        "List of all brick dependencies. In order to add a dependency, move the entry to 'dependencies'",
      "@coremedia/brick-360-spinner": "^1.0.0",
      "@coremedia/brick-bootstrap": "^1.0.0",
      "@coremedia/brick-cta": "^1.0.0",
      "@coremedia/brick-default-teaser": "^1.0.0",
      "@coremedia/brick-download-portal": "^1.0.0",
      "@coremedia/brick-dynamic-include": "^1.0.0",
      "@coremedia/brick-elastic-social": "^1.0.0",
      "@coremedia/brick-footer": "^1.0.0",
      "@coremedia/brick-fragment-scenario": "^1.0.0",
      "@coremedia/brick-generic-templates": "^1.0.0",
      "@coremedia/brick-hero-teaser": "^1.0.0",
      "@coremedia/brick-image-maps": "^1.0.0",
      "@coremedia/brick-legacy-templates": "^1.0.0",
      "@coremedia/brick-livecontext": "^1.0.0",
      "@coremedia/brick-navigation": "^1.0.0",
      "@coremedia/brick-pdp-augmentation": "^1.0.0",
      "@coremedia/brick-preview": "^1.0.0",
      "@coremedia/brick-quick-info": "^1.0.0",
      "@coremedia/brick-responsive-images": "^1.0.0",
      "@coremedia/brick-search": "^1.0.0",
      "@coremedia/brick-shoppable-video": "^1.0.0",
      "@coremedia/brick-video": "^1.0.0",
      "@coremedia/js-logger": "^1.0.0",
      "@coremedia/sass-utils": "^1.0.0",
      jquery: "3.2.1",
    },
    dependencies: {
      "@coremedia/cm-cli": "^2.0.0",
      "@coremedia/theme-utils": "^3.0.0",
      webpack: "3.10.0",
    },
    main: mainJSFile,
    coremedia: {
      type: "theme",
      name: themeName,
    },
    browserslist: [
      "last 1 Chrome version",
      "last 1 Firefox version",
      "last 1 Edge version",
      "Explorer >= 11",
    ],
  };
  return JSON.stringify(content, null, 2);
};

/**
 * returns content for webpack.config.js
 * @return {string}
 */
const initWebpackConfigJs = () => `const webpackTheme = require("@coremedia/theme-utils/webpack.config.js");
module.exports = webpackTheme;
`;

/**
 * returns content for theme descriptor
 * @param  {string} themeName
 * @return {string}
 */
const initThemedescriptorXml = themeName => `<?xml version="1.0" encoding="UTF-8"?>
<themeDefinition modelVersion="1">
<name>${themeName}</name>
<!-- <description>a short description</description> -->
<!-- <thumbnail>img/theme-preview.jpg</thumbnail> -->
<javaScriptLibraries>
  <!-- add thirdparty js files that are not bundled via webpack here -->
  <!-- <javaScript>vendor/some-vendor-script.js</javaScript> -->
</javaScriptLibraries>
<javaScripts>
  <!-- add own js files that are not bundled via webpack here -->
  <!-- <javaScript>js/your-code.js</javaScript> -->
  <javaScript disableCompress="true">js/${themeName}.js</javaScript>
</javaScripts>
<styleSheets>
  <css disableCompress="true">css/${themeName}.css</css>
</styleSheets>
<templateSets>
  <templateSet>templates/${themeName}-templates.jar</templateSet>
  <templateSet>templates/bricks-templates.jar</templateSet>
</templateSets>
<resourceBundles>
  <!-- add theme resource bundles here -->
  <resourceBundle>l10n/${titleCase(themeName)}_en.properties</resourceBundle>
  <!-- merged resource bundles of all bricks, activate this if you are using bricks -->
  <!-- <resourceBundle>l10n/Bricks_en.properties</resourceBundle> -->
</resourceBundles>
</themeDefinition>
`;

/**
 * returns content for <themeName>.sass
 * @param  {string} themeName
 * @return {string}
 */
const initThemeSass = themeName => `/*! Theme ${themeName} */
// ### VARIABLES ###

// Own variables (need to be loaded first, so default values can be overridden)
// @see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#Variable_Defaults___default

//@import "variables/...";

// Dependency variables

//@import "~@coremedia/brick-bootstrap/src/sass/variables";
//@import "~@coremedia/brick-cta/src/sass/variables";
//@import "~@coremedia/brick-default-teaser/src/sass/variables";
//@import "~@coremedia/brick-download-portal/src/sass/variables";
//@import "~@coremedia/brick-dynamic-include/src/sass/variables";
//@import "~@coremedia/brick-elastic-social/src/sass/variables";
//@import "~@coremedia/brick-fragment-scenario/src/sass/variables";
//@import "~@coremedia/brick-generic-templates/src/sass/variables";
//@import "~@coremedia/brick-hero-teaser/src/sass/variables";
//@import "~@coremedia/brick-image-maps/src/sass/variables";
//@import "~@coremedia/brick-livecontext/src/sass/variables";
//@import "~@coremedia/brick-pdp-augmentation/src/sass/variables";
//@import "~@coremedia/brick-preview/src/sass/variables";
//@import "~@coremedia/brick-quick-info/src/sass/variables";
//@import "~@coremedia/brick-responsive-images/src/sass/variables";
//@import "~@coremedia/brick-shoppable-video/src/sass/variables";
//@import "~@coremedia/brick-video/src/sass/variables";
//@import "~@coremedia/sass-utils/src/variables";

// ### PARTIALS ###

// Dependency partials

//@import "~@coremedia/brick-bootstrap/src/sass/partials";
//@import "~@coremedia/brick-cta/src/sass/partials";
//@import "~@coremedia/brick-default-teaser/src/sass/partials";
//@import "~@coremedia/brick-download-portal/src/sass/partials";
//@import "~@coremedia/brick-dynamic-include/src/sass/partials";
//@import "~@coremedia/brick-elastic-social/src/sass/partials";
//@import "~@coremedia/brick-fragment-scenario/src/sass/partials";
//@import "~@coremedia/brick-generic-templates/src/sass/partials";
//@import "~@coremedia/brick-hero-teaser/src/sass/partials";
//@import "~@coremedia/brick-image-maps/src/sass/partials";
//@import "~@coremedia/brick-livecontext/src/sass/partials";
//@import "~@coremedia/brick-pdp-augmentation/src/sass/partials";
//@import "~@coremedia/brick-preview/src/sass/partials";
//@import "~@coremedia/brick-quick-info/src/sass/partials";
//@import "~@coremedia/brick-responsive-images/src/sass/partials";
//@import "~@coremedia/brick-shoppable-video/src/sass/partials";
//@import "~@coremedia/brick-video/src/sass/partials";
//@import "~@coremedia/sass-utils/src/partials";

// Own partials

//@import "partials/...";
`;

/**
 * returns content for preview.sass
 * @param  {string} themeName
 * @return {string}
 */
const initPreviewSass = themeName => `/*! Theme ${themeName}: Preview Styles */
// ### VARIABLES ###
// Own variables (need to be loaded first, so default values can be overridden)
// @see http://sass-lang.com/documentation/file.SASS_REFERENCE.html#Variable_Defaults___default
//@import "variables/...";
// Dependency variables
//@import "~@coremedia/brick-preview/src/sass/variables";
//@import "~@coremedia/sass-utils/src/variables";
// ### PARTIALS ###
// Dependency partials
//@import "~@coremedia/sass-utils/src/partials";
//@import "~@coremedia/brick-preview/src/sass/partials";
// Own partials
//@import "partials/preview";
`;

/**
 * returns content for index.js
 * @param  {string} themeName
 * @return {string}
 */
const initThemeIndexJs = themeName => `/*! Theme ${themeName} */
// activate brick js functionality
//import "@coremedia/brick-360-spinner";
//import "@coremedia/brick-bootstrap";
//import "@coremedia/brick-cta";
//import "@coremedia/brick-default-teaser";
//import "@coremedia/brick-download-portal";
//import "@coremedia/brick-dynamic-include";
//import "@coremedia/brick-elastic-social";
//import "@coremedia/brick-footer";
//import "@coremedia/brick-fragment-scenario";
//import "@coremedia/brick-generic-templates";
//import "@coremedia/brick-hero-teaser";
//import "@coremedia/brick-image-maps";
//import "@coremedia/brick-livecontext";
//import "@coremedia/brick-navigation";
//import "@coremedia/brick-pdp-augmentation";
//import "@coremedia/brick-preview";
//import "@coremedia/brick-quick-info";
//import "@coremedia/brick-responsive-images";
//import "@coremedia/brick-search";
//import "@coremedia/brick-shoppable-video";
//import "@coremedia/brick-video";
import "./${themeName}.js";
`;

/**
 * returns content for <themeName>.js
 * @param  {string} themeName
 * @return {string}
 */
const initThemeJs = themeName => `//import $ from "jquery";
//import logger from "@coremedia/js-logger";
// --- JQUERY DOCUMENT READY -------------------------------------------------------------------------------------------
//$(function () {
//  logger.log("Theme ${themeName} is used.");
//});
`;

module.exports = {
  titleCase,
  initPackageJson,
  initWebpackConfigJs,
  initThemedescriptorXml,
  initThemeSass,
  initPreviewSass,
  initThemeIndexJs,
  initThemeJs,
};
