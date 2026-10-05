import { defineConfig } from '@playwright/test';
import * as dotenv from 'dotenv';
import { resolve } from 'node:path';
import { getEnvironmentConfig, type Environment, type App } from './config/environments';
import { getViewport, getLocalProjects, BrowserName } from './config/browsers';
import type { BaseOptions, RuntimeOptions } from './src/fixtures/base.fixture';

dotenv.config({ path: resolve(__dirname, '.env') });

type PlaywrightRuntimeOptions = RuntimeOptions & { browserName: BrowserName };

function readBoolean(value: string | undefined, defaultValue: boolean): boolean {
  return value === undefined ? defaultValue : value === 'true';
}

function readNumber(value: string | undefined, defaultValue: number): number {
  return value === undefined ? defaultValue : parseInt(value);
}

function toBrowserLocale(locale: string): string {
  const [country = 'gb', language = 'en'] = locale.toLowerCase().split('-');
  return `${language}-${country.toUpperCase()}`;
}

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * RUNTIME CONFIGURATION — controlled via environment variables
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * ┌──────────────────┬──────────────────────────────────┬─────────────┐
 * │ Variable         │ Options                          │ Default     │
 * ├──────────────────┼──────────────────────────────────┼─────────────┤
 * │ APP              │ pi, pib, ccui                    │ pi          │
 * │ ENV              │ dit, uat                         │ uat         │
 * │ BROWSER          │ chrome, firefox, edge, safari    │ chrome      │
 * │ VIEWPORT         │ desktop, laptop, tablet, mobile  │ desktop     │
 * │ LOCALE           │ gb-en, de-de                     │ gb-en       │
 * │ HEADED           │ true, false                      │ true        │
 * │ GREP             │ @smoke, @regression, @e2e        │ (all tests) │
 * │ EXECUTION_ENV    │ local                            │ lambdatest  │
 * └──────────────────┴──────────────────────────────────┴─────────────┘
 *
 * Examples:
 *   npm test                                          → PI, UAT, LambdaTest (default)
 *   APP=pib ENV=dit BROWSER=firefox npm test          → PIB, DIT, Firefox, LambdaTest
 *   EXECUTION_ENV=local npm test                      → PI, UAT, Chrome, local browser
 * ─────────────────────────────────────────────────────────────────────────────
 */

function getRuntimeOptions(): PlaywrightRuntimeOptions {
  const app = (process.env.APP ?? 'pi') as App;
  const env = (process.env.ENV ?? 'uat') as Environment;
  const executionEnv = process.env.EXECUTION_ENV ?? 'local';
  const isLambdaTest = executionEnv !== 'local';
  const locale = process.env.LOCALE || 'gb-en';
  const ohipClientId = process.env.UAT_OHIP_CLIENT_ID ?? process.env.OHIP_CLIENT_ID ?? '';
  const ohipClientSecret = process.env.UAT_OHIP_CLIENT_SECRET ?? process.env.OHIP_CLIENT_SECRET ?? '';
  const ohipXApiKey = process.env.UAT_OHIP_X_API_KEY ?? process.env.OHIP_X_API_KEY ?? '';
  const environmentConfig = getEnvironmentConfig({ env, app, locale, ohipClientId, ohipClientSecret, ohipXApiKey });

  return {
    // ==================================
    // Custom parameters
    // ==================================
    app, // current applications: pi, ccui, ib
    env, // dit, uat, demo
    browserName: (process.env.BROWSER as BrowserName) || 'chrome', // chrome, safari, edge or firefox
    viewport: process.env.VIEWPORT || 'desktop', // desktop, laptop, tablet, mobile
    locale, // the locale for which to open the Whitbread web application: gb-en, de-de
    executionEnv, // local, lambdatest
    headed: readBoolean(process.env.HEADED, true), // true if the browser will run in headed mode 
    fullyParallel: readBoolean(process.env.FULLY_PARALLEL, false), // true if the all tests, not only the spec files, will run in parallel mode
    threadCount: readNumber(process.env.THREAD_COUNT, 1), // the number of threads for running the capability
    testRetries: readNumber(process.env.TEST_RETRIES, 0), // the number of retries for a failed test
    grep: process.env.GREP ?? '', // the grep pattern for filtering tests
    linkTm: readBoolean(process.env.LINK_TM, false), // link LambdaTest sessions to Test Manager cases
    runNumber: process.env.RUN_NUMBER ?? new Date().toISOString().slice(11, 16).replace(':', ''), // CI or local run identifier
    ci: !!process.env.CI, // true if running in a CI environment
    baseUrl: environmentConfig.baseUrl,
    secureUrl: environmentConfig.secureUrl,
    secureUrl2: environmentConfig.secureUrl2,
    httpAuthUsername: process.env.HTTP_AUTH_USERNAME ?? '', // the username for HTTP authentication
    httpAuthPassword: process.env.HTTP_AUTH_PASSWORD ?? '', // the password for HTTP authentication
    oldAuthPassword: process.env.OLD_AUTH_PASSWORD ?? '', // old HTTP auth password used by CCUI secure URL
    aemAuthUsername: process.env.AEM_AUTH_USERNAME ?? process.env.HTTP_AUTH_USERNAME ?? '', // the username for AEM authentication
    aemAuthPassword: process.env.AEM_AUTH_PASSWORD ?? process.env.HTTP_AUTH_PASSWORD ?? '', // the password for AEM authentication
    ltUsername: process.env.LT_USERNAME ?? '', // the LambdaTest username
    ltAccessKey: process.env.LT_ACCESS_KEY ?? '', // the LambdaTest access key
    featuresToggles: process.env.FEATURES_TOGGLES ?? '', // the feature toggles for the application
    entityApiBaseUrl: process.env.ENTITY_API_BASE_URL ?? `https://restapi.${env}.premierinn.digital`,
    eckohWebhookEndpoint: process.env.ECKOH_WEBHOOK_ENDPOINT ?? `https://restapi.${env}.premierinn.digital`,
    paymentWebhookXWhitApiKey: process.env.PAYMENT_WEBHOOK_X_WHIT_API_KEY ?? '',
    cdhUatEndpoint: process.env.CDH_UAT_ENDPOINT ?? 'https://cdh-uat.premierinn.com', // CDH endpoint
    actionTimeout: isLambdaTest ? 30000 : 15000, // the timeout for waiting for an element to be visible (in milliseconds)
    navigationTimeout: isLambdaTest ? 60000 : 30000, // the timeout for waiting for a page to load (in milliseconds)
    expectTimeout: isLambdaTest ? 30000 : 15000, // the timeout for waiting for an expectation to be met (in milliseconds)
    aemBaseUrl: environmentConfig.aemBaseUrl, // the base URL for the AEM instance
    ohipApiBaseUrl: environmentConfig.ohipApiBaseUrl, // the base URL for the OHIP API
    ohipClientId: environmentConfig.ohipClientId, // the client ID for the OHIP API
    ohipClientSecret: environmentConfig.ohipClientSecret, // the client secret for the OHIP API
    ohipXApiKey: environmentConfig.ohipXApiKey, // the API key for the OHIP API
  };
}

const options = getRuntimeOptions();
const isLambdaTest = options.executionEnv !== 'local';
const grep = options.grep ? new RegExp(options.grep) : undefined;
const httpCredentials = options.httpAuthUsername
  ? { username: options.httpAuthUsername, password: options.httpAuthPassword }
  : undefined;
const environmentConfig = getEnvironmentConfig(options);

export default defineConfig<BaseOptions>({
  testDir: './tests',
  fullyParallel: options.fullyParallel,
  forbidOnly: options.ci,
  retries: options.testRetries,
  workers: options.threadCount,
  // LambdaTest: longer timeout for CDP connection setup (especially Safari/WebKit)
  timeout: isLambdaTest ? 900000 : 500000, // test timeout
  grep,

  reporter: [
    ['list'],
    ['html', { outputFolder: `./reports/${options.app}/html`, open: 'never' }],
    ['json', { outputFile: `./reports/${options.app}/playwright-results.json` }],
    ['allure-playwright', {
      resultsDir: `./reports/${options.app}/allure-results`,
      suiteTitle: true,
      environmentInfo: {
        App: options.app,
        Environment: options.env,
        Locale: options.locale,
      },
    }],
    ['./config/lambdatest/reporter.ts', {}],
  ],

  use: {
    options,
    baseURL: environmentConfig.baseUrl,
    httpCredentials,
    viewport: getViewport(options),
    locale: toBrowserLocale(options.locale),
    headless: !options.headed,
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
    video: isLambdaTest ? 'off' : 'on-first-retry',
    actionTimeout: options.actionTimeout,
    navigationTimeout: options.navigationTimeout,
    ignoreHTTPSErrors: true,
  },
  expect: {
    timeout: options.expectTimeout,
  },

  globalSetup: require.resolve('./src/fixtures/global-setup'),
  globalTeardown: require.resolve('./src/fixtures/global-teardown'),

  projects: isLambdaTest ? [{ name: 'lambdatest', use: {} }] : getLocalProjects(options),
});
