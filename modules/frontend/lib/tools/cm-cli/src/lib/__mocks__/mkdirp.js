"use srict";

const mkdirp = jest.genMockFromModule("mkdirp");

let mockDirectories;

mkdirp.__resetMockDirectories = () => {
  mockDirectories = [];
};

mkdirp.__getMockDirectories = () => mockDirectories;

mkdirp.sync = directory => {
  const dir = directory.replace(/\\/g, "/");
  mockDirectories.push(dir);
};

module.exports = mkdirp;
