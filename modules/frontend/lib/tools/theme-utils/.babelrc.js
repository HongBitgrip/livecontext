// babel < 7 does not allow .babelrc.js yet, we are importing this file explicitly in the webpack.config.js
// and provide it to the babel-loader
// see also: https://github.com/babel/babel-preset-env/issues/108
// -> remove this comment when we are upgrading to babel 7

const browserslist = require('browserslist');

// pickEnv is adopted from
// https://github.com/ai/browserslist/blob/bc78b5d635344796c77a7e23b66f311ad0d9ab5e/index.js#L99-L114
function pickEnv(config) {
  if ( typeof config !== 'object' ) return config;

  let env;
  if ( typeof process.env.BROWSERSLIST_ENV === 'string' ) {
    env = process.env.BROWSERSLIST_ENV;
  } else if ( typeof process.env.NODE_ENV === 'string' ) {
    env = process.env.NODE_ENV;
  } else {
    env = 'development';
  }

  return config[env] || config.defaults;
}

const browserslistConfig = pickEnv(browserslist.findConfig('.'));

let targetBrowsers = undefined;
if (browserslistConfig) {
  targetBrowsers =  Array.isArray(browserslistConfig) ? browserslistConfig : [browserslistConfig];
}

module.exports = {
  // Do not use .babelrc file
  babelrc: false,
  "comments": false,
  "passPerPreset": true,
  "plugins": ["add-module-exports"],
  // transform-runtime currently does not work properly with "export * from". added a temporary fix as suggested in:
  // https://github.com/babel/babel/issues/2877
  // when the issue is fixed in babel-plugin-transform-runtime, we can revert the fix in the configuration.
  "presets": [
    {
      "plugins": ["transform-runtime"]
    },
    {
      "passPerPreset": false,
      "presets": [
        [
          "env", {
            "targets": {
              "browsers": targetBrowsers
            }
          }
        ]
      ]
    }
  ]
};
