const esModules = [
  'uuid',
  'nanoid',
  'sanitize-html',
  'htmlparser2',
  'dom-serializer',
  'domhandler',
  'domutils',
  'domelementtype',
  'entities',
].join('|');

module.exports = {
  testEnvironment: 'jest-environment-jsdom',
  collectCoverageFrom: [
    'src/**/*.{ts,tsx}',
    '!src/components/**/*.constants.{js,jsx,ts,tsx}',
    '!src/index.ts',
    '!src/utils/setupTestsjs',
    '!src/utils/test-utils.tsx',
    '!src/**/index.{js,ts}',
    '!src/**/styles.{js,ts,tsx,jsx}',
    'src/**/server/index.{js,ts,tsx}',
  ],
  transformIgnorePatterns: [`/node_modules/(?!${esModules})`],

  setupFiles: ['./src/utils/setupTests.js'],
  setupFilesAfterEnv: ['./setup-after-env.ts'],
  testTimeout: 60 * 1000,
  // coverageThreshold: {
  //   global: {
  //     statements: 75,
  //     branches: 75,
  //     functions: 75,
  //     lines: 75,
  //   },
  // },
};
