"use strict";

jest.mock("tiny-lr");
jest.mock("@coremedia/cm-logger");
jest.mock("@coremedia/tool-utils/workspace");

describe("init()", () => {
  beforeEach(() => {
    jest.resetModules();
  });
  it("should initialize a new LiveReload server with options and started it", () => {
    const livereload = require("..");
    const tinylr = require("tiny-lr");
    const {
      getMonitorConfig,
      getCert,
    } = require("@coremedia/tool-utils/workspace");

    const { livereload: config } = getMonitorConfig();
    const cert = getCert();
    const options = Object.assign({}, config, { key: cert, cert });
    livereload.init();
    expect(tinylr).toHaveBeenCalledWith(options);
  });
  it("should start a LiveReload server with the same host only once", () => {
    const livereload = require("..");
    let servers = livereload.getServers();
    expect(Object.keys(servers)).toHaveLength(0);
    livereload.init();
    servers = livereload.getServers();
    expect(Object.keys(servers)).toHaveLength(1);
    livereload.init();
    servers = livereload.getServers();
    expect(Object.keys(servers)).toHaveLength(1);
  });
  it("should log an error", () => {
    const cmLogger = require("@coremedia/cm-logger");
    const ws = require("@coremedia/tool-utils/workspace");

    const error = new Error("mocked error");
    ws.getMonitorConfig = jest.fn().mockImplementation(() => {
      throw error;
    });

    const livereload = require("..");
    const log = cmLogger.getLogger({
      name: livereload.PKG_NAME,
      level: "warn",
    });

    livereload.init();
    expect(log.error).toHaveBeenCalledWith(error.message);
  });
});

describe("trigger()", () => {
  beforeEach(() => {
    jest.resetModules();
  });
  it("should trigger LiveReload server", () => {
    const livereload = require("..");

    livereload.init();
    const server = livereload.getServer();
    const changedFiles = ["/path/to/changed/file"];
    livereload.trigger(changedFiles);
    expect(server.changed).toHaveBeenCalledWith({
      body: { files: changedFiles },
    });
  });
  it("should log an error", () => {
    const cmLogger = require("@coremedia/cm-logger");
    const livereload = require("..");

    livereload.trigger();
    const log = cmLogger.getLogger({
      name: livereload.PKG_NAME,
      level: "warn",
    });
    expect(log.error).toHaveBeenCalled();
  });
});

describe("getHost()", () => {
  it("should return the host name of the LiveReload server", () => {
    const { getMonitorConfig } = require("@coremedia/tool-utils/workspace");
    const livereload = require("..");

    const { livereload: config } = getMonitorConfig();
    livereload.init();
    const host = livereload.getHost();
    const expected = `${config.host}:${config.port}`;
    expect(host).toEqual(expected);
  });
});
