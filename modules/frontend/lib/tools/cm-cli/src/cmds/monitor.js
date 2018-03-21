"use strict";

const { spawnSync } = require("child_process");
const { getMonitorConfig } = require("@coremedia/tool-utils/workspace");
const themeImporter = require("@coremedia/theme-importer");
const cmLogger = require("@coremedia/cm-logger");

const { PACKAGE_MANAGER_EXECUTABLE } = require("../lib/paths");
const args = require("../lib/args");
const { PKG_NAME } = require("../lib/constants");

let monitorConfig;
try {
  monitorConfig = getMonitorConfig();
} catch (error) {
  const log = cmLogger.getLogger({
    name: PKG_NAME,
    level: "error",
  });
  log.error(error.message);
  process.exit(1);
}

const command = "monitor";
const desc = `Watch file changes and update theme on ${
  monitorConfig.target
} CAE.`;
const builder = yargs =>
  yargs
    .option("verbose", {
      default: false,
      describe: "Enable verbose mode for more information output.",
      type: "boolean",
    })
    .epilogue(args.docs);

const handler = argv => {
  const logLevel = argv.verbose ? "debug" : "info";
  const log = cmLogger.getLogger({
    name: PKG_NAME,
    level: logLevel,
  });

  const startWebpackWatchMode = () => {
    const args = ["run", "build"];
    if (PACKAGE_MANAGER_EXECUTABLE.includes("npm")) {
      args.push("--");
    }
    args.push("--watch");
    if (!argv.verbose) {
      args.push("--display=none");
    }
    const result = spawnSync(PACKAGE_MANAGER_EXECUTABLE, args, {
      cwd: process.cwd(),
      env: Object.assign(process.env, { NODE_ENV: "development" }),
      stdio: "inherit",
    });
    if (result.status !== 0) {
      log.error(
        `Monitor command failed due to listed errors.\n${result.error}`
      );
      process.exit(1);
    }
  };

  const runLoginCmd = () => {
    const args = ["run", "theme-importer", "login"];
    const result = spawnSync(PACKAGE_MANAGER_EXECUTABLE, args, {
      cwd: process.cwd(),
      env: Object.assign(process.env, { NODE_ENV: "development" }),
      stdio: "inherit",
    });
    if (result.status !== 0) {
      process.exit(1);
    }
  };

  if (monitorConfig.target === "remote") {
    log.info("Starting monitor using remote CAE...");
    themeImporter
      .whoami()
      .then(user => {
        log.info(`You are logged in as user '${user.name}' (id=${user.id}).`);
        log.info(`Creating initial theme build...`);
        startWebpackWatchMode();
      })
      .catch(e => {
        log.error(e.message);
        runLoginCmd();
        startWebpackWatchMode();
      });
  } else {
    log.info("Starting monitor using local CAE...");
    log.info(`Creating initial theme build...`);
    startWebpackWatchMode();
  }
};

module.exports = {
  command,
  desc,
  builder,
  handler,
};
