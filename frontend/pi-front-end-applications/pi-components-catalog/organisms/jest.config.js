// eslint-disable-next-line @typescript-eslint/no-var-requires
const jestConfig = require('../config/jest.config');
module.exports = Object.assign({}, jestConfig, {
  testTimeout: 50000,
});
