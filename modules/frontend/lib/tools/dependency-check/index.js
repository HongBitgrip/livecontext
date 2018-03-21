const closestPackage = require("closest-package");
const deepmerge = require("deepmerge");
const { getInstalledPathSync } = require('get-installed-path');
const nodeSass = require("node-sass");
const path = require("path");

function getPkgName(file) {
  const pkg = require(closestPackage.sync(file));
  return pkg && pkg.name;
}

function getDependenciesByName(file) {
  const pkg = require(closestPackage.sync(file));

  const pkgDependencies = [];
  if (pkg.dependencies) {
    for (let pkgDependency of Object.keys(pkg.dependencies)) {
      pkgDependencies.push(pkgDependency);
    }
  }
  return pkgDependencies;
}

function hasPkgDependencyToCached(sourceFile, requiredFile, cache) {
  const sourcePkgName = getPkgName(sourceFile);
  const requiredPkgName = getPkgName(requiredFile);

  // ignoring babel-runtime here...consider plugging into another phase where the runtime is not yet attached
  if (sourcePkgName === requiredPkgName || requiredPkgName === "babel-runtime") {
    return true;
  }

  cache[sourcePkgName] = cache[sourcePkgName] || getDependenciesByName(sourceFile);

  return cache[sourcePkgName].indexOf(requiredPkgName) > -1;
}

/**
 * Checks if the pkg that contains the given sourcefile has a dependency of the pkg that contains the given requiredFile
 *
 * @param sourceFile {string} The file in the depending package
 * @param requiredFile {string} The file in the depended package
 */
function hasPkgDependencyTo(sourceFile, requiredFile) {
  return hasPkgDependencyToCached(sourceFile, requiredFile, {});
}

/**
 * Checks if a path is included, based on a given includes and excludes list.
 * If includes were given the path must match at least one of the includes.
 * If excludes are given the path must not match any of the excludes.
 *
 * @param path the file path to be checked
 * @param includes {Array<String|RegExp>} an array of includes
 * @param excludes {Array<String|RegExp>} an array of excludes
 * @return {boolean} specifies if the file is to be included
 */
function isIncluded(path, includes, excludes) {
  const patternOrStringMatches = patternOrString =>
          (patternOrString instanceof RegExp && patternOrString.test(path))
          || (typeof patternOrString === "string" && path.indexOf(patternOrString) > -1);
  return (!includes || includes.length === 0 || includes.some(patternOrStringMatches))
          && (!excludes || !excludes.some(patternOrStringMatches));
}

function toArray(o) {
  if (o instanceof Array) {
    return o;
  }
  if (o) {
    return [o];
  }
  return [];
}

function getMissingDependencyErrorMessage(sourceFile, sourcePackage, requiredFile, requiredPackage) {
  return `'${sourceFile}'\nModule:\t\t\t'${sourcePackage}'\nFile To Import:\t\t'${requiredFile}'\nMissing Dependency:\t'${requiredPackage}'`;
}

function gatherUnmanagedDependencies(sourceModule) {

  const result = [];
  const sourceFile = sourceModule && sourceModule.resource;
  if (!sourceFile) {
    // cannot test, so assuming everything is ok
    return result;
  }

  const cache = {};
  for (let dependency of sourceModule.dependencies) {
    const requiredModule = dependency.module;
    const requiredFile = requiredModule && requiredModule.resource;
    if (!requiredFile) {
      // cannot test, so assuming everything is ok
      continue;
    }

    if (!hasPkgDependencyToCached(sourceFile, requiredFile, cache)) {
      result.push({
        sourceFile: sourceFile,
        sourcePackage: getPkgName(sourceFile),
        requiredFile: requiredFile,
        requiredPackage: getPkgName(requiredFile)
      })
    }
  }

  return result;
}

class DependencyCheckWebpackPlugin {
  constructor(options) {
    this.options = deepmerge({ include: undefined, exclude: undefined}, options);
    this.includes = toArray(this.options.include);
    this.excludes = toArray(this.options.exclude);
  }

  apply(compiler) {
    const plugin = this;

    compiler.plugin('done', function(stats) {
      const modules = stats.compilation.modules;

      for (let module of modules) {
        const unmanagedDependencies = gatherUnmanagedDependencies(module);
        for (let unmanagedDependency of unmanagedDependencies) {
          if (isIncluded(unmanagedDependency.sourceFile, plugin.includes, plugin.excludes)) {
            stats.compilation.errors.push(
                    new Error(getMissingDependencyErrorMessage(
                            unmanagedDependency.sourceFile,
                            unmanagedDependency.sourcePackage,
                            unmanagedDependency.requiredFile,
                            unmanagedDependency.requiredPackage
                    ))
            );
          }
        }
      }
    })
  }
}

/**
 * Creates node-sass custom importer that checks if an import of a sass file from a module has a dependency of the
 * package.json of the dependending sass file. It will not make any transformation, so after the check it will always be
 * skipped by returning 'NULL' which will skip to the next custom importer or the default node-sass importer.
 *
 * @param options
 */
function getDependencyCheckNodeSassImporter(options) {
  options = deepmerge({ include: undefined, exclude: undefined}, options);
  const includes = toArray(options.include);
  const excludes = toArray(options.exclude);

  /**
   * @see https://github.com/sass/node-sass#importer--v200---experimental
   * (not so experimental anymore as tools like the webpack sass-loader also won't work without this)
   *
   * @param url the path in import as-is, which LibSass encountered
   * @param prev the previously resolved path
   * @param done a callback function to invoke on async completion
   */
  return function(url, prev, done) {
    if (isIncluded(prev, includes, excludes)) {
      const prefixPattern = /^~/;
      if (prefixPattern.test(url)) {
        url = url.replace(prefixPattern, '');

        const modulePattern = /^((@[^\/]+\/)*[^\/])+/;
        if (modulePattern.test(url)) {
          const moduleName = modulePattern.exec(url)[0];
          const nodeModulePaths = [path.join(path.dirname(closestPackage.sync(prev)), "node_modules")].concat(process.mainModule.paths);
          try {
            const moduleInstallationPath = getInstalledPathSync(moduleName, { paths: nodeModulePaths });
            if (moduleInstallationPath) {
              // check if dependency is specified
              if (!hasPkgDependencyTo(prev, moduleInstallationPath)) {
                done(new Error(getMissingDependencyErrorMessage(prev, url, getPkgName(prev), moduleName)));
                return;
              }
            }
          } catch (e) {
            done(new Error(`Could not find installation folder for dependency '${moduleName}' of '${prev}', searched in ${nodeModulePaths}`));
            return;
          }
        }
      }
    }
    done(nodeSass.types.NULL);
  };
}

module.exports = {
  DependencyCheckWebpackPlugin,
  getDependencyCheckNodeSassImporter
};
