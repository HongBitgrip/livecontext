"use strict";

const {
  initPackageJson,
  initWebpackConfigJs,
  initThemedescriptorXml,
  initThemeSass,
  initPreviewSass,
  initThemeIndexJs,
  initThemeJs,
} = require("../themeData");

describe("initPackageJson()", () => {
  it("returns data for package.json", () => {
    expect(initPackageJson("test")).toMatchSnapshot();
  });
});

describe("initWebpackConfigJs()", () => {
  it("returns data for webpack.config.js", () => {
    expect(initWebpackConfigJs("test")).toMatchSnapshot();
  });
});

describe("initThemedescriptorXml()", () => {
  it("returns data for theme descriptor", () => {
    expect(initThemedescriptorXml("test")).toMatchSnapshot();
  });
});

describe("initThemeSass()", () => {
  it("returns data for <themeName>.sass", () => {
    expect(initThemeSass("test")).toMatchSnapshot();
  });
});

describe("initPreviewSass()", () => {
  it("returns data for preview.sass", () => {
    expect(initPreviewSass()).toMatchSnapshot();
  });
});

describe("initThemeIndexJs()", () => {
  it("returns data for index.js", () => {
    expect(initThemeIndexJs("test")).toMatchSnapshot();
  });
});

describe("initThemeJs()", () => {
  it("returns data for <themeName>.js", () => {
    expect(initThemeJs("test")).toMatchSnapshot();
  });
});
