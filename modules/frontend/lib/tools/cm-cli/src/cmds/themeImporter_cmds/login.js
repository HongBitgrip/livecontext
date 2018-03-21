"use strict";

const inquirer = require("inquirer");
const cmLogger = require("@coremedia/cm-logger");
const themeImporter = require("@coremedia/theme-importer");
const { getEnv } = require("@coremedia/tool-utils/workspace");

const { isValidURL, isValidStringValue } = require("../../lib/validators");
const args = require("../../lib/args");
const { PKG_NAME } = require("../../lib/constants");

const command = "login [options]";
const desc = "Authenticate user and create API key";
const builder = yargs => {
  let defaults = {};
  try {
    const env = getEnv();
    defaults = Object.assign(defaults, env);
  } catch (e) {
    // env file does not exist yet.
  }

  return yargs
    .options({
      studioUrl: {
        demandOption: false,
        default: defaults.studioUrl,
        describe: "Studio URL",
        type: "string",
      },
      previewUrl: {
        demandOption: false,
        default: defaults.previewUrl,
        describe: "Preview URL",
        type: "string",
      },
      proxyUrl: {
        demandOption: false,
        default: defaults.proxy,
        describe: "Proxy URL",
        type: "string",
      },
      username: {
        alias: "u",
        demandOption: false,
        describe: "Username",
        type: "string",
      },
      password: {
        alias: "p",
        demandOption: false,
        describe: "Password",
        type: "string",
      },
    })
    .check(argv => {
      const checks = [
        {
          key: "Studio URL",
          value:
            typeof argv.studioUrl === "undefined" || isValidURL(argv.studioUrl),
        },
        {
          key: "Preview URL",
          value:
            typeof argv.previewUrl === "undefined" ||
            isValidURL(argv.previewUrl),
        },
        {
          key: "Proxy URL",
          value:
            typeof argv.proxyUrl === "undefined" || isValidURL(argv.proxyUrl),
        },
        {
          key: "Username",
          value:
            typeof argv.username === "undefined" ||
            isValidStringValue(argv.username),
        },
        {
          key: "Password",
          value:
            typeof argv.password === "undefined" ||
            isValidStringValue(argv.password),
        },
      ];
      const errors = checks.filter(check => typeof check.value === "string");
      if (errors.length > 0) {
        return errors.map(error => `${error.key}: ${error.value}`).join(", ");
      }
      return true;
    })
    .epilogue(args.docs);
};

const handler = argv => {
  const log = cmLogger.getLogger({
    name: PKG_NAME,
    level: "info",
  });

  const args = Object.assign({}, argv);

  if (!args.studioUrl || !args.username || !args.password) {
    inquirer
      .prompt([
        {
          type: "input",
          name: "studioUrl",
          message: "Studio URL:",
          default: args.studioUrl,
          validate: input => isValidURL(input),
        },
        {
          type: "input",
          name: "previewUrl",
          message: "Preview URL:",
          default: args.previewUrl,
          validate: input => !input || isValidURL(input),
        },
        {
          type: "input",
          name: "proxyUrl",
          message: "Proxy URL:",
          default: args.proxyUrl,
          validate: input => !input || isValidURL(input),
        },
        {
          type: "input",
          name: "username",
          message: "Username:",
          default: args.username,
          validate: input => isValidStringValue(input),
        },
        {
          type: "password",
          name: "password",
          message: "Password:",
          validate: input => isValidStringValue(input),
        },
      ])
      .then(args => {
        themeImporter
          .login(
            args.studioUrl,
            args.previewUrl,
            args.proxyUrl,
            args.username,
            args.password
          )
          .then(msg => {
            log.success(msg);
          })
          .catch(e => {
            log.error(e.message);
            process.exit(1);
          });
      });
  } else {
    themeImporter
      .login(
        args.studioUrl,
        args.previewUrl,
        args.proxyUrl,
        args.username,
        args.password
      )
      .then(msg => {
        log.success(msg);
      })
      .catch(e => {
        log.error(e.message);
        process.exit(1);
      });
  }
};

module.exports = {
  command,
  desc,
  builder,
  handler,
};
