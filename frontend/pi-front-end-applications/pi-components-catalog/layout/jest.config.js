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
  testTimeout: 50000,
  testEnvironment: 'jest-environment-jsdom',
  collectCoverageFrom: [
    'src/components/**/*.{js,jsx,ts,tsx}',
    '!src/index.js',
    '!src/utils/*.{js,jsx,ts,tsx}',
    '!src/theme/*.{js,jsx,ts,tsx}',
    '!src/theme/**/*.{js,ts,tsx,jsx}',
    '!src/components/index.{js,ts}',
    '!src/components/**/index.{js,ts}',
    '!src/components/**/*.stories.{js,jsx,ts,tsx}',
    '!src/components/**/*.constants.{js,jsx,ts,tsx}',
    '!src/components/ColorModeSwitcher/*',
  ],
  transform: {
    '^.+\\.(t|j)sx?$': '@swc/jest',
  },
  transformIgnorePatterns: [`/node_modules/(?!${esModules})`],

  setupFiles: ['./src/utils/setupTests.js'],
  setupFilesAfterEnv: ['./setup-after-env.ts'],
};
