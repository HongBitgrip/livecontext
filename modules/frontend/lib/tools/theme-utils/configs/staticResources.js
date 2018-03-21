const CopyWebpackPlugin = require("copy-webpack-plugin");
const closestPackage = require("closest-package");
const path = require("path");

const deepMerge = require("./utils/deepMerge");
const {
  dependencies: { getFlattenedDependencies },
  workspace: { isBrickDependency, getThemeConfig },
} = require("@coremedia/tool-utils");
const JoinWebpackPlugin = require("../plugins/JoinWebpackPlugin");
const { ZipperWebpackPlugin } = require("../plugins/ZipperWebpackPlugin");

const PROPERTIES_REG_EXP = /.+_(.+).properties$/;
const PROPERTIES_GLOB = "*_*.properties";

const themeConfig = getThemeConfig();

/**
 * Creates a CopyWebpackPlugin instance for every given brick dependencies that will copy the templates to the target
 * folder.
 *
 * Important: We cannot use a single instance as the order the templates are copied is not guaranteed in that case as
 * all patterns are executed simultaneously.
 *
 * @param relativeTemplatesSrc the relative source folder of the templates (must be the same for all bricks)
 * @param relativeTemplatesTarget the relative target folder for the templates
 * @param brickDependencies the brick dependencies
 * @return {Array<CopyWebpackPlugin>} an plugin instance for every brick dependency
 */
function configureCopyWebpackPluginsForBrickTemplates(
  relativeTemplatesSrc,
  relativeTemplatesTarget,
  brickDependencies
) {
  const patterns = brickDependencies
    .map(brickDependency => [
      // templates
      {
        from: path.resolve(
          path.dirname(brickDependency.getPkgPath()),
          relativeTemplatesSrc
        ),
        to: relativeTemplatesTarget,
        force: true,
        cache: true,
      },
    ])
    .reduce(
      (dependencyPatternsA, dependencyPatternsB) =>
        dependencyPatternsA.concat(dependencyPatternsB),
      []
    );
  return patterns.map(pattern => new CopyWebpackPlugin([pattern]));
}

/**
 * Creates single a JoinWebpackPlugin instance for the given brick dependencies to join the resource bundles into a
 * single resource bundle (one property file for every language) which is stored in the target folder.
 *
 * @param relativeResourceBundleSrc the relative source folder of the resource bundles (must be the same in all bricks)
 * @param relativeResourceBundleTarget the relative target folder for the single bundles
 * @param brickDependencies the brick dependencies
 * @returns {JoinWebpackPlugin} the instance of the plugin
 */
function configureJoinWebpackPluginForBrickResourceBundles(
  relativeResourceBundleSrc,
  relativeResourceBundleTarget,
  brickDependencies
) {
  // add search patterns for brick resource bundles, already filtered by actual dependencies to reduce overhead
  const searchPatterns = brickDependencies
    .map(brickDependency => path.dirname(brickDependency.getPkgPath()))
    .map(brickDependencyPkgPath =>
      path.resolve(
        brickDependencyPkgPath,
        relativeResourceBundleSrc,
        PROPERTIES_GLOB
      )
    );

  return new JoinWebpackPlugin({
    name: path.join(relativeResourceBundleTarget, `Bricks_[1].properties`),
    search: searchPatterns,
    join: function(common, addition, filename) {
      // gather resource bundles using a map (with package name as key), so they can be ordered before being stored
      common = common || {};
      const pkgPath = closestPackage.sync(filename);
      const pkg = require(pkgPath);

      // add source of the keys as comment
      const relativeFilePath = path.relative(themeConfig.path, filename);
      addition =
        `# Resource Bundle from: ${pkg.name}\n# File: ${relativeFilePath}\n\n` +
        addition;

      common[pkg.name] = addition;
      return common;
    },
    save: function(common) {
      // join the map by iterating of the flattened dependency order
      const orderedContent = brickDependencies.map(
        brickDependency => common[brickDependency.getName()] || ""
      );
      // join resource bundles, omit empty / non-existing entries
      return orderedContent.filter(content => !!content).join("\n");
    },
    group: "[1]",
    regExp: PROPERTIES_REG_EXP,
  });
}

/**
 * @module contains the webpack configuration for static resources like templates and resource bundles
 */
module.exports = () => config => {
  const brickDependencies = getFlattenedDependencies(
    themeConfig.pkgPath,
    isBrickDependency
  );

  const copyWebpackPluginsBrickTemplates = configureCopyWebpackPluginsForBrickTemplates(
    path.join("src", "templates"),
    path.relative(
      themeConfig.themeTargetPath,
      themeConfig.brickTemplatesTargetPath
    ),
    brickDependencies
  );
  const joinWebpackPlugin = configureJoinWebpackPluginForBrickResourceBundles(
    path.join("src", "l10n"),
    "l10n",
    brickDependencies
  );

  return deepMerge(config, {
    module: {
      rules: [
        {
          test: /\.(svg|png|gif)$/,
          loader: "file-loader",
          options: {
            name: "[name].[ext]",
            outputPath: "img/",
          },
        },
        {
          test: /\.(woff|woff2|ttf|eot)$/,
          loader: "file-loader",
          options: {
            name: "[name].[ext]",
            outputPath: "fonts/",
          },
        },
        {
          test: /\.(swf)$/,
          loader: "file-loader",
          options: {
            name: "[name].[ext]",
            outputPath: "swf/",
          },
        },
        {
          test: PROPERTIES_REG_EXP,
          use: [joinWebpackPlugin.loader()],
        },
      ],
    },
    plugins: [
      ...copyWebpackPluginsBrickTemplates,
      joinWebpackPlugin,
      // configure for themes
      new CopyWebpackPlugin([
        {
          context: themeConfig.srcPath,
          from: "css/**",
          force: true,
          cache: true,
        },
        {
          context: themeConfig.srcPath,
          from: "fonts/**",
          force: true,
          cache: true,
        },
        {
          context: themeConfig.srcPath,
          from: "img/**",
          force: true,
          cache: true,
        },
        {
          context: themeConfig.srcPath,
          from: "images/**",
          force: true,
          cache: true,
        },
        {
          context: themeConfig.srcPath,
          from: "l10n/**",
          force: true,
          cache: true,
        },
        {
          context: path.join(themeConfig.srcPath, "templates"),
          from: "**",
          to: path.relative(
            themeConfig.themeTargetPath,
            themeConfig.themeTemplatesTargetPath
          ),
          force: true,
          cache: true,
        },
        {
          context: themeConfig.srcPath,
          from: "vendor/**",
          force: true,
          cache: true,
        },
        {
          context: themeConfig.path,
          from: path.basename(themeConfig.descriptorTargetPath),
          to: path.relative(
            themeConfig.themeTargetPath,
            path.dirname(themeConfig.descriptorTargetPath)
          ),
          force: true,
          cache: true,
        },
      ]),
      new ZipperWebpackPlugin(
        [
          {
            source: path.relative(
              themeConfig.resourcesTargetPath,
              themeConfig.themeTemplatesTargetPath
            ),
            prefix: path.normalize("META-INF/resources/"),
            context: path.relative(
              themeConfig.themeTargetPath,
              themeConfig.resourcesTargetPath
            ),
          },
        ],
        {
          filepath: path.relative(
            themeConfig.themeTargetPath,
            themeConfig.themeTemplatesJarTargetPath
          ),
          compilerEvent: "after-emit",
        }
      ),
      new ZipperWebpackPlugin(
        [
          {
            source: path.relative(
              themeConfig.resourcesTargetPath,
              themeConfig.brickTemplatesTargetPath
            ),
            prefix: path.normalize("META-INF/resources/"),
            context: path.relative(
              themeConfig.themeTargetPath,
              themeConfig.resourcesTargetPath
            ),
          },
        ],
        {
          filepath: path.relative(
            themeConfig.themeTargetPath,
            themeConfig.brickTemplatesJarTargetPath
          ),
          compilerEvent: "after-emit",
        }
      ),
    ],
  });
};
