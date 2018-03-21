'use srict';

const paths = jest.genMockFromModule('../paths');

paths.__setBricksDirectory = value => {
  paths.BRICKS_DIRECTORY = value;
};

paths.__setJSLibsDirectory = value => {
  paths.JSLIBS_DIRECTORY = value;
};

paths.__setToolsDirectory = value => {
  paths.TOOLS_DIRECTORY = value;
};

paths.__setThemesDirectory = value => {
  paths.THEMES_DIRECTORY = value;
};

module.exports = paths;
