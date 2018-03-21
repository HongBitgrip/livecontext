"use strict";

const md5File = require("md5-file");
const path = require("path");
const cmLogger = require("@coremedia/cm-logger");
const { uploadTheme, uploadFiles } = require("@coremedia/theme-importer");
const livereload = require("@coremedia/livereload");

const {
  clearConsole,
  printInfo,
  openBrowser,
  printErrorStack,
} = require("./utils");

class CoreMediaWatchPlugin {
  constructor(options) {
    this._pkgName = "@coremedia/watch-webpack-plugin";

    this.options = typeof options === "object" ? options : {};

    if (
      typeof this.options.themeConfig !== "object" ||
      typeof this.options.themeConfig.name !== "string" ||
      typeof this.options.themeConfig.targetPath !== "string"
    ) {
      throw new Error(
        `[${
          this._pkgName
        }] options must be an object which at least contains an object named "themeConfig" with properties "name" and "targetPath"`
      );
    }
    if (
      typeof this.options.monitorConfig !== "object" ||
      typeof this.options.monitorConfig.target !== "string"
    ) {
      throw new Error(
        `[${
          this._pkgName
        }] options must be an object which at least contains an object named "monitorConfig" with property "target"`
      );
    }

    this._log = null;
    this._isWatching = false;
    this._cache = {};
  }

  _isCacheEmpty() {
    return Object.keys(this._cache).length === 0;
  }

  _initializeCache(files) {
    this._log.debug("Initializing file cache");
    files.forEach(this._updateCache, this);
  }

  _getCacheEntry(file) {
    return this._cache[file];
  }

  _updateCache(file) {
    const newMd5Hash = md5File.sync(file);
    this._cache[file] = newMd5Hash;
    return newMd5Hash;
  }

  _hasChanged(file) {
    const oldMd5Hash = this._getCacheEntry(file);
    const newMd5Hash = this._updateCache(file);

    return oldMd5Hash !== newMd5Hash;
  }

  _mapTemplatesToArchive(file) {
    if (file.includes(this.options.themeConfig.themeTemplatesTargetPath)) {
      return this.options.themeConfig.themeTemplatesJarTargetPath;
    }
    if (file.includes(this.options.themeConfig.brickTemplatesTargetPath)) {
      return this.options.themeConfig.brickTemplatesJarTargetPath;
    }
    return file;
  }

  _relativeToThemeFolder(filename) {
    return path.relative(this.options.themeConfig.path, filename);
  }

  _isRemoteWorkflow() {
    return this.options.monitorConfig.target === "remote";
  }

  apply(compiler) {
    const logLevel =
      this.options.logLevel ||
      cmLogger.getLevelFromWebpackStats(compiler.options.stats);
    const logger = cmLogger.getLogger({
      name: this._pkgName,
      level: logLevel,
    });
    this._log = {
      debug: logger.debug,
      info: logger.info,
      error: logger.error,
      finalInfo: msg => {
        logger.info(msg);
        logger.info();
        logger.info("Watching...");
      },
      finalError: msg => {
        logger.error(msg);
        logger.info();
        logger.info("Watching...");
      },
    };

    let init = true;

    compiler.plugin("watch-run", (compiler, callback) => {
      this._isWatching = true;
      callback();
    });

    // invalidations
    compiler.plugin("invalid", () => {
      if (init === false) {
        this._log.info("Detected file changes...");
      }
    });

    compiler.plugin("done", stats => {
      if (this._isWatching) {
        const assets = stats.compilation.assets;

        // Error handling
        if (stats.compilation.errors.length > 0) {
          const errors = [...new Set(stats.compilation.errors)];
          printErrorStack(errors.join("\n"));
          if (init === true) {
            // initialization of watch mode: don´t upload theme build and abort watch mode, if there are compilation errors
            this._log.error(
              "Watch mode aborted due to compilation errors during initial theme build. Please fix the errors first and start the watch mode again."
            );
            process.exit(1);
          }
          if (this._isRemoteWorkflow()) {
            // remote workflow: don´t upload file changes, if there are compilation errors
            this._log.finalError(
              "Processing file changes aborted due to compilation errors."
            );
          } else {
            // local workflow: file changes have been processed in target directory, but may contain errors.
            this._log.finalError(
              "The preview may not work properly due to compilation errors."
            );
          }
          return;
        }

        const emittedFiles = Object.keys(assets).map(
          asset => assets[asset].existsAt
        );
        this._log.debug("Emitted files: ", emittedFiles);

        if (this._isCacheEmpty()) {
          this._initializeCache(emittedFiles);
          livereload.init(logLevel);
          if (this._isRemoteWorkflow()) {
            uploadTheme(this.options.themeConfig)
              .then(() => {
                clearConsole();
                printInfo();
                openBrowser();
                init = false;
              })
              .catch(e => {
                this._log.error("Error during theme upload: ", e);
              });
          }
        }

        const changedFiles = [
          ...new Set(
            emittedFiles
              .filter(this._hasChanged, this)
              .map(this._mapTemplatesToArchive, this)
          ),
        ];

        if (changedFiles.length > 0) {
          if (this._isRemoteWorkflow()) {
            this._log.info(
              "Preparing to upload: ",
              changedFiles.map(this._relativeToThemeFolder, this)
            );
            uploadFiles(this.options.themeConfig, changedFiles, logLevel)
              .then(count => {
                this._log.finalInfo(
                  `Uploaded ${count} file(s) to remote server.`
                );
                livereload.trigger(changedFiles);
              })
              .catch(e => {
                this._log.error("Error during upload of changed files: ", e);
              });
          } else {
            this._log.finalInfo("Changed files processed: ", changedFiles);
            livereload.trigger(changedFiles);
          }
        } else if (init === false) {
          this._log.finalInfo(
            "File changes didn´t affected already processed result, skipping upload."
          );
        }
      }
    });
  }
}

module.exports = { CoreMediaWatchPlugin };
