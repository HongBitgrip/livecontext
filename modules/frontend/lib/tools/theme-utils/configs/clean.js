const path = require("path");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const { workspace: { getThemeConfig } } = require("@coremedia/tool-utils");

const deepMerge = require("./utils/deepMerge");

const themeConfig = getThemeConfig();

/**
 * @module contains the webpack configuration for cleaning the target directory
 */
module.exports = () => config =>
  deepMerge(config, {
    plugins: [
      new CleanWebpackPlugin(
        [
          path.relative(themeConfig.targetPath, themeConfig.themeTargetPath),
          path.relative(
            themeConfig.targetPath,
            themeConfig.themeContentTargetPath
          ),
          path.relative(
            themeConfig.targetPath,
            themeConfig.themeArchiveTargetPath
          ),
          path.relative(
            themeConfig.targetPath,
            themeConfig.themeTemplatesTargetPath
          ),
          path.relative(
            themeConfig.targetPath,
            themeConfig.brickTemplatesTargetPath
          ),
        ],
        {
          root: themeConfig.targetPath,
          // do not write any output to the console
          verbose: false,
        }
      ),
    ],
  });
