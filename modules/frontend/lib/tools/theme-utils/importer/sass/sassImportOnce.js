const closestPackage = require("closest-package");
const fs = require("fs");
const path = require("path");
const nodeSass = require("node-sass");
const { getInstalledPathSync } = require('get-installed-path');

const SassNameExpander = require("./SassNameExpander");

function resolveScss(url) {
  const nameExpander = new SassNameExpander(path.basename(url));
  nameExpander.addLocation(path.dirname(url));
  const possibleFiles = nameExpander.files.values();
  let result;

  while (result = possibleFiles.next().value) {
    if (fs.existsSync(result)) {
      result = fs.realpathSync(result);
      break;
    }
  }

  return result;
}

module.exports = function(url, prev, done) {
  const prefixPattern = /^~/;
  const importedFromPath = path.dirname(prev);

  // Create an import cache if it doesn't exist
  if (!this._importOnceCache) {
    this._importOnceCache = {};
  }

  if (prefixPattern.test(url)) {
    url = url.replace(prefixPattern, "");

    const modulePattern = /^((@[^\/]+\/)*[^\/])+/;
    if (modulePattern.test(url)) {
      url = url.replace(modulePattern, function (moduleName) {
        const nodeModulePaths = [path.join(path.dirname(closestPackage.sync(prev)), "node_modules")].concat(process.mainModule.paths);
        try {
          return getInstalledPathSync(moduleName, { paths: nodeModulePaths });
        } catch (e) {
          // could not find module
          throw new Error(
                  `Could not find installation folder for dependency '${moduleName}' of '${prev}', searched in ${nodeModulePaths}`
          );
        }
        return moduleName;
      });
    } else {
      done(new Error(`Could not resolve url: "${originalUrl}" which was marked for exclude.`));
    }
  }

  const absolutePath = path.resolve(importedFromPath, url);
  const resolvedAbsolutePath = resolveScss(absolutePath);
  if (resolvedAbsolutePath && resolvedAbsolutePath in this._importOnceCache) {
    done({
      'contents': '',
      'filename': 'already-imported:' + resolvedAbsolutePath
    });
  } else {
    this._importOnceCache[resolvedAbsolutePath] = true;
    done(nodeSass.types.NULL);
  }
};
