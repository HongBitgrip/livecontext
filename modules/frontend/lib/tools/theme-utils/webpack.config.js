const flow = require("lodash/fp/flow");
const { workspace: { getThemeConfig } } = require("@coremedia/tool-utils");

const clean = require("./configs/clean");
const themeZip = require("./configs/themeZip");
const production = require("./configs/production");
const development = require("./configs/development");
const javascript = require("./configs/javascript");
const exposeModules = require("./configs/exposeModules");
const sass = require("./configs/sass");
const staticResources = require("./configs/staticResources");

const themeConfig = getThemeConfig();

// merge different webpack configurations (order matters!)
const base = flow(
  clean(),
  staticResources(),
  sass(),
  javascript(),
  exposeModules(),
  process.env.NODE_ENV === "production" ? production() : development(),
  themeZip(),
);

const entry = {};
entry[themeConfig.name] = [];

module.exports = base({
  entry: entry,
  context: themeConfig.path,
  output: {
    path: themeConfig.themeTargetPath,
  },
  // see https://webpack.js.org/configuration/stats/
  stats: "minimal",
});
