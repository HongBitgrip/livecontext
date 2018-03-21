"use strict";

jest.mock("fs");
jest.mock("mkdirp");
jest.mock("../paths");

const log = {
  debug: jest.fn(),
  info: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
  success: jest.fn(),
};

const wsConfig = {
  themesPath: "/path/to/themes/",
};

describe("convertThemeName()", () => {
  const { convertThemeName } = require("../themeUtils");
  it("returns converted theme name", () => {
    const themeName =
      ' 1234567890_-Ugly Theme Name! äöüß^°!"§$%&/()=?´`*+#.:,;<>@„¡“¶¢[]|{}”≠¿±‘’æœ•–…∞µ~√ç≈¥≤åº∆π¨Ω†®€∑«»';
    const expected = "1234567890-uglythemename";
    expect(convertThemeName(themeName)).toEqual(expected);
  });
  it("returns empty string", () => {
    const themeName =
      ' äöüß^°!"§$%&/()=?´`*+#.:,;<>@„¡“¶¢[]|{}”≠¿±‘’æœ•–…∞µ~√ç≈¥≤åº∆π¨Ω†®€∑«»';
    expect(convertThemeName(themeName)).toEqual("");
  });
  it("returns empty string", () => {
    const themeName = [1, 2];
    expect(convertThemeName(themeName)).toEqual("");
  });
  it("returns empty string", () => {
    const themeName = {};
    expect(convertThemeName(themeName)).toEqual("");
  });
});

describe("isThemeNameInUse()", () => {
  beforeEach(() => {
    jest.resetModules();
    require("fs").__setMockFiles([
      "/path/to/themes/aurora-theme",
      "/path/to/themes/corporate-theme",
      "/path/to/themes/hybris-theme",
    ]);
    require("../paths").__setThemesDirectory("/path/to/themes");
  });

  it("returns true", () => {
    const { isThemeNameInUse } = require("../themeUtils");
    expect(isThemeNameInUse("/path/to/themes/aurora-theme")).toBe(true);
  });
  it("returns false", () => {
    const { isThemeNameInUse } = require("../themeUtils");
    expect(isThemeNameInUse("/path/to/themes/newest-theme")).toBe(false);
  });
});

describe("createFolderStructure()", () => {
  it("creates folder structure of a new theme", () => {
    const mkdirp = require("mkdirp");
    mkdirp.__resetMockDirectories();

    const { createFolderStructure } = require("../themeUtils");
    createFolderStructure(wsConfig, "/path/to/themes/newest-theme", log);
    const directories = mkdirp.__getMockDirectories();
    expect(directories).toMatchSnapshot();
  });
});

describe("createFiles()", () => {
  it("creates files of a new theme", () => {
    const fs = require("fs");
    fs.__resetMockFiles();

    const { createFiles } = require("../themeUtils");
    createFiles("/path/to/themes/newest-theme", "newest", log);
    const files = fs.__getMockFiles();
    expect(files).toMatchSnapshot();
  });
});

describe("createTheme()", () => {
  it("creates folder structure and files of a new theme", () => {
    const mkdirp = require("mkdirp");
    mkdirp.__resetMockDirectories();
    const fs = require("fs");
    fs.__resetMockFiles();

    const { createTheme } = require("../themeUtils");
    createTheme(wsConfig, "/path/to/themes/newest-theme", "newest", log);

    const directories = mkdirp.__getMockDirectories();
    const files = fs.__getMockFiles();

    expect(directories).toMatchSnapshot();
    expect(files).toMatchSnapshot();
  });
});
