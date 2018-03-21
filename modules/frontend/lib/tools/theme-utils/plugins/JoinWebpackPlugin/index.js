// Based on "join-webpack-plugin" (MIT license) by Serguei Okladnikov <oklaspec@gmail.com>
//
// Customizations (in this file):
// - brought to ES6
// - fixed some inspection errors
// - avoid deprecation warning by using new pattern for Chunk#modules
// - allow [N] in group and output names as described https://github.com/webpack/loader-utils#interpolatename
// -> for this added new "regExp" configuration

const async = require('async');
const glob = require('glob');
const { interpolateName } = require('loader-utils');
const { PrefetchPlugin } = require('webpack');
const { RawSource } = require('webpack-sources');

let NEXT_ID = 0;

class JoinWebpackPlugin {
  constructor(options) {
    if (typeof options !== 'object') {
      throw new Error('options must be object of key:values');
    }

    if (typeof options.join !== 'function') {
      throw new Error("'join' option must be function");
    }

    if (typeof options.save !== 'function') {
      throw new Error("'save' option must be function");
    }

    if (!options.search) {
      options.search = [];
    }

    if (typeof options.search === 'string') {
      options.search = [options.search];
    }

    if (!Array.isArray(options.search)) {
      throw new Error("'search' option must be string or array");
    }

    options.skip = options.skip || [];
    options.skip = Array.isArray(options.skip) ? options.skip : [options.skip];

    options.name = options.name || '[hash]';
    options.group = options.group || null;
    options.regExp = options.regExp || null;
    this.groups = {};

    this.options = options;
    this.state = 'loading';
    this.id = options.id ? options.id : ++NEXT_ID;
  }

  group(groupName) {
    groupName = groupName || '';
    if (!this.groups[groupName]) {
      this.groups[groupName] = {
        sources: {},
        result: null,
        filetmpl: this.options.name,
        filename: 'cm-join-webpack-plugin.default',
      };
    }
    return this.groups[groupName];
  }

  addSource(groupName, source, path, module) {
    const group = this.group(groupName);
    if (this.state === 'loading') {
      group.sources[path] = source;
      return 'cm-join-webpack-plugin.in.process';
    } else {
      return group.filename;
    }
  }

  doPrefetch(compiler) {
    let found = {};

    this.options.search.forEach(item => {
      const globOpts = { cwd: compiler.options.context };
      glob.sync(item, globOpts).forEach(path => (found[path] = null));
    });

    found = Object.keys(found);

    found = found.filter(item => {
      const skip = this.options.skip.filter(
        skip =>
          skip instanceof RegExp ? skip.test(item) : item.indexOf(skip) !== -1
      );
      return 0 === skip.length;
    });

    found.forEach(item => compiler.apply(new PrefetchPlugin(item)));
  }

  buildGroup(group) {
    const files = Object.keys(group.sources);
    let result = null;
    files.forEach(
      file => (result = this.options.join(result, group.sources[file], file))
    );
    group.result = this.options.save(result);
    group.filename = interpolateName(this, group.filetmpl, {
      content: group.result,
    });
  }

  apply(compiler) {
    this.doPrefetch(compiler);

    compiler.plugin('this-compilation', compilation => {
      compilation.plugin('optimize-tree', (chunks, modules, callback) => {
        this.state = 'building';

        Object.keys(this.groups).forEach(groupName => {
          const group = this.group(groupName);
          this.buildGroup(group);
        });

        async.forEach(
          chunks,
          (chunk, callback) => {
            async.forEach(
              Array.from(chunk.mapModules(c => c)).slice(),
              (module, callback) => {
                let group = null;
                Object.keys(this.groups).forEach(groupName => {
                  const g = this.group(groupName);
                  if (module.resource in g.sources) {
                    group = g;
                  }
                });
                if (!group) {
                  return callback();
                }

                compilation.rebuildModule(module, err => {
                  if (err) {
                    compilation.errors.push(err);
                  }
                  callback();
                });
              },
              err => {
                if (err) {
                  return callback(err);
                }
                callback();
              }
            );
          },
          err => {
            if (err) {
              return callback(err);
            }
            this.state = 'loading';
            callback();
          }
        );
      });

      compilation.plugin('additional-assets', callback => {
        Object.keys(this.groups).forEach(groupName => {
          const group = this.group(groupName);
          compilation.assets[group.filename] = new RawSource(group.result);
        });
        callback();
      });
    });
  }

  loader(query = {}) {
    query.id = this.id;
    return (
      require.resolve('../../loaders/JoinWebpackLoader') +
      '?' +
      JSON.stringify(query)
    );
  }
}

module.exports = JoinWebpackPlugin;
