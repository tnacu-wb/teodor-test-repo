// jest.config.js
const nextJest = require('next/jest'); // eslint-disable-line @typescript-eslint/no-var-requires

const createJestConfig = nextJest({
  // Provide the path to your Next.js app to load next.config.js and .env files in your test environment
  dir: './',
});

const esModules = [
  'uuid',
  'nanoid',
  '@auth0/nextjs-auth0',
  'sanitize-html',
  'htmlparser2',
  'dom-serializer',
  'domhandler',
  'domutils',
  'domelementtype',
  'entities',
].join('|');

// Add any custom config to be passed to Jest
const customJestConfig = {
  // Add more setup options before each test is run
  // setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  // if using TypeScript with a baseUrl set to the root directory then you need the below for alias' to work
  moduleDirectories: ['node_modules', '<rootDir>/'],
  modulePathIgnorePatterns: ['<rootDir>/.next/'],
  testEnvironment: 'jest-environment-jsdom',
  moduleNameMapper: {
    '^~public/(.*)$': '<rootDir>/public/$1',
    '^~pages/(.*)$': '<rootDir>/src/pages/$1',
    '^~components/(.*)$': '<rootDir>/src/components/$1',
    '^~components': '<rootDir>/src/components/index.ts',
    '^~types/(.*)$': '<rootDir>/src/types/$1',
    '^~styles/(.*)$': '<rootDir>/src/styles/$1',
    '^~store/(.*)$': '<rootDir>/src/store/$1',
    '^~services/(.*)$': '<rootDir>/src/services/$1',
    '^~services': '<rootDir>/src/services/index.ts',
    '^~hooks/(.*)$': '<rootDir>/src/hooks/$1',
    '^~utils/(.*)$': '<rootDir>/src/utils/$1',
    '^~queries/(.*)$': '<rootDir>/src/queries/$1',
    '^~queries': '<rootDir>/src/queries/index.ts',
    '^~page-helper/(.*)$': '<rootDir>/src/page-helper/$1',
    '^mocks/(.*)$': '<rootDir>/src/mocks/$1',
  },
  transform: {
    '^.+\\.(js|jsx|ts|tsx)$': [
      '@swc/jest',
      {
        jsc: {
          transform: {
            react: {
              runtime: 'automatic',
            },
          },
          parser: {
            syntax: 'typescript',
            tsx: true,
          },
        },
      },
    ],
  },
  collectCoverageFrom: [
    'src/components/**/*.{js,jsx,ts,tsx}',
    '!src/components/**/index.ts',
    'src/lib/**/*.{js,jsx,ts,tsx}',
    'src/middleware.ts',
    'src/hooks/**/*.{js,jsx,ts,tsx}',
    '!src/hooks/use-orientation.tsx', //this file will be moved to the catalog
    '!src/hooks/use-screensize.tsx', //this file will be moved to the catalog
    'src/pages/**/*.{js,jsx,ts,tsx}',
    '!src/pages/**/graphql.tsx',
    'src/services/**/*.{js,jsx,ts,tsx}',
    'src/utils/**/*.{js,jsx,ts,tsx}',
    '!src/utils/**/graphql.ts',
    '!src/utils/test-utils.tsx',
    'src/page-helper/**/*.{js,jsx,ts,tsx}',
    '!src/components/common/I18NLabels/**',
    '!src/mocks/**',
  ],
  coverageProvider: 'v8',
  setupFiles: ['./src/utils/setupTests.js'],
  coverageThreshold: {
    global: {
      statements: 49,
      branches: 45, // TODO: Revert to 50% once more coverage will be added
      functions: 75,
      lines: 49,
    },
  },
};

// createJestConfig is exported this way to ensure that next/jest can load the Next.js config which is async
module.exports = async () => ({
  ...(await createJestConfig(customJestConfig)()),
  transformIgnorePatterns: [`/node_modules/(?!${esModules})`],
});
