// eslint-disable-next-line @typescript-eslint/no-var-requires
module.exports = {
  collectCoverageFrom: ['src/snowdrop-queries/*.{ts,tsx}'],
  coverageThreshold: {
    global: {
      statements: 75,
      branches: 75,
      functions: 75,
      lines: 75,
    },
  },
};
