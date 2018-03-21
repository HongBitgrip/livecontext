"use strict";

const fs = require("fs");
const path = require("path");
const mkdirp = require("mkdirp");

const themeData = require("./themeData");

/**
 * converts the theme name to match restrictions.
 * returns an empty string, if given name was not valid.
 * @param  {string} themeName
 * @return {string}
 */
const convertThemeName = themeName => {
  if (typeof themeName !== "string") {
    return "";
  }
  return themeName
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9-]/g, "");
};

/**
 * verifies, if the given theme path is already in use
 * @param  {string}  themePath
 * @return {Boolean}
 */
const isThemeNameInUse = themePath => fs.existsSync(themePath);

/**
 * creates folder structure of a new theme
 * @param  {string}  themePath
 * @param  {cm-logger} log cm-logger instance
 */
const createFolderStructure = (wsConfig, themePath, log) => {
  const directories = [
    path.join(themePath, "src", "js"),
    path.join(themePath, "src", "sass"),
    path.join(themePath, "src", "img"),
    path.join(themePath, "src", "fonts"),
    path.join(themePath, "src", "l10n"),
    path.join(
      themePath,
      "src",
      "templates",
      "com.coremedia.blueprint.common.contentbeans"
    ),
  ];
  directories.forEach(dir => {
    log.debug(
      `Create directory "themes/${path.relative(wsConfig.themesPath, dir)}"`
    );
    mkdirp.sync(dir);
  });
};

/**
 * creates files of a new theme
 * @param  {string}  themePath
 * @param  {string}  themeName
 * @param  {cm-logger} log cm-logger instance
 */
const createFiles = (themePath, themeName, log) => {
  const files = [
    {
      path: "package.json",
      data: themeData.initPackageJson(themeName, "src/js/index.js"),
    },
    {
      path: "webpack.config.js",
      data: themeData.initWebpackConfigJs(),
    },
    {
      path: `${themeName}-theme.xml`,
      data: themeData.initThemedescriptorXml(themeName),
    },
    {
      path: "src/js/index.js",
      data: themeData.initThemeIndexJs(themeName),
    },
    {
      path: `src/js/${themeName}.js`,
      data: themeData.initThemeJs(themeName),
    },
    {
      path: `src/sass/${themeName}.scss`,
      data: themeData.initThemeSass(themeName),
    },
    {
      path: "src/sass/preview.scss",
      data: themeData.initPreviewSass(themeName),
    },
    {
      path: `src/l10n/${themeData.titleCase(themeName)}_en.properties`,
      data: "",
    },
  ];
  files.forEach(file => {
    log.debug(`Create file "${file.path}"`);
    fs.writeFileSync(path.join(themePath, file.path), file.data);
  });
};

/**
 * creates folder structure and files of a new theme
 * @param  {Object}  wsConfig
 * @param  {string}  themePath
 * @param  {string}  themeName
 * @param  {cm-logger} log cm-logger instance
 */
const createTheme = (wsConfig, themePath, themeName, log) => {
  log.debug(`Creating theme "${path.basename(themePath)}"...`);
  createFolderStructure(wsConfig, themePath, log);
  createFiles(themePath, themeName, log);
};

module.exports = {
  convertThemeName,
  isThemeNameInUse,
  createFolderStructure,
  createFiles,
  createTheme,
};
