const webpackTheme = require("@coremedia/theme-utils");

webpackTheme.externals = webpackTheme.externals || {};
webpackTheme.externals["jquery"] = "jQuery";
webpackTheme.externals["bootstrap-sass"] = "jQuery";

module.exports = webpackTheme;
