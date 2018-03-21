const UglifyJsPlugin = require("uglifyjs-webpack-plugin");
const deepMerge = require("./utils/deepMerge");

/**
 * @module contains the webpack configuration for the production environment
 */
module.exports = () => config => deepMerge(config,
        {
          plugins: [
            new UglifyJsPlugin({})
          ]
        }
);
