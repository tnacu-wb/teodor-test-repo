// eslint-disable-next-line @typescript-eslint/no-var-requires
const jestConfig = require('../config/jest.config');
module.exports = Object.assign({}, jestConfig, {
  coverageThreshold: {
    global: {
      statements: 80,
      branches: 75,
      functions: 80,
      lines: 80,
    },
  },
  testTimeout: 50000,
});
