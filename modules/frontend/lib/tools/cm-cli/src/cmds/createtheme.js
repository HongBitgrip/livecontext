"use strict";

const path = require("path");
const cmLogger = require("@coremedia/cm-logger");
const { getWorkspaceConfig } = require("@coremedia/tool-utils/workspace");

const args = require("../lib/args");
const {
  convertThemeName,
  isThemeNameInUse,
  createTheme,
} = require("../lib/themeUtils");
const { PKG_NAME } = require("../lib/constants");

const command = "create-theme <name>";
const desc = "Create a blank, minimal theme";
const builder = yargs =>
  yargs
    .option("verbose", {
      default: false,
      describe: "Enable verbose mode for more information output.",
      type: "boolean",
    })
    .epilogue(args.docs);

const handler = argv => {
  const log = cmLogger.getLogger({
    name: PKG_NAME,
    level: argv.verbose ? "debug" : "info",
  });

  let wsConfig;
  try {
    wsConfig = getWorkspaceConfig();
  } catch (error) {
    log.error(error.message);
    process.exit(1);
  }

  const themeName = convertThemeName(argv.name);
  if (!themeName) {
    log.error(
      "No valid theme name was provided. Only characters (A-Z, a-z), numbers (0-9) and hyphens (-) are allowed. Please try again."
    );
    process.exit(1);
  }

  const themePath = path.join(wsConfig.themesPath, `${themeName}-theme`);
  if (isThemeNameInUse(themePath)) {
    log.error(
      `The theme "${themeName}" already exists. Please choose another name.`
    );
    process.exit(1);
  }

  log.info(`Generating new theme "${themeName}".`);
  try {
    createTheme(wsConfig, themePath, themeName, log);
    log.success(`Done.`);
  } catch (e) {
    log.error(
      `An error occured while trying to create theme "${themeName}": ${
        e.message
      }`
    );
  }
};

module.exports = {
  command,
  desc,
  builder,
  handler,
};
