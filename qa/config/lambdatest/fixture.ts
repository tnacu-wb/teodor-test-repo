/**
 * LambdaTest Per-Test Session Fixture
 *
 * Mimics the pattern from LambdaTest's official Cucumber sample:
 * https://github.com/LambdaTest/playwright-sample/blob/main/playwright-cucumber-js/features/steps/setup.js
 *
 * Each test gets its own `chromium.connect()` call with the test title as
 * the session name. This produces one entry per test in the LambdaTest
 * Automation dashboard.
 *
 * Hierarchy in LambdaTest:
 *   Build (e.g. "PI | UAT | Smoke | 2026-08-27 | #1345")
 *     └── Session per test (e.g. "TC-805 | @smoke | Homepage loads successfully")
 *
 * When EXECUTION_ENV=local, falls through to the normal Playwright browser.
 */

import { test as base, chromium } from '@playwright/test';
import { buildLambdaTestEndpoint, getLambdaTestBrowserName, formatSessionName } from './index';
import { getViewport } from '../browsers';
import type { BaseOptions } from '../../src/fixtures/base.fixture';

const testWithOptions = base.extend<BaseOptions>({
  options: [{ app: 'pi' }, { option: true }],
});

export const test = testWithOptions.extend({
  // Override context to connect to LambdaTest per test with unique session name + auth headers
  context: async ({ options }, use, testInfo) => {
    let browser;
    let context;

    const isLambdaTest = options.executionEnv !== 'local';
    const viewport = getViewport(options);

    if (!isLambdaTest) {
      // Local: launch default Chromium
      browser = await chromium.launch();
      context = await browser.newContext({ ignoreHTTPSErrors: true, viewport });
    } else {
      // LambdaTest: connect with structured session name
      const sessionName = formatSessionName(testInfo.title);
      const browserName = getLambdaTestBrowserName(options.browserName);

      // Extract @TC-xxx from test title to link with Test Manager when enabled
      const linkToTM = options.linkTm;
      const tcMatch = testInfo.title.match(/@TC-(\d+)/);
      const tcId = linkToTM && tcMatch ? `TC-${tcMatch[1]}` : undefined;

      const wsEndpoint = buildLambdaTestEndpoint(browserName, sessionName, tcId, options);

      browser = await chromium.connect(wsEndpoint, { timeout: 60000 });
      context = await browser.newContext({ ignoreHTTPSErrors: true, viewport });
    }

    // Inject auth headers for WAF-protected environments
    if (options.httpAuthUsername) {
      const credentials = Buffer.from(
        `${options.httpAuthUsername}:${options.httpAuthPassword ?? ''}`
      ).toString('base64');

      await context.route('**/*', async (route) => {
        await route.continue({
          headers: {
            ...route.request().headers(),
            authorization: `Basic ${credentials}`,
          },
        });
      });
    }

    await use(context);
    await context.close();
    await browser.close();
  },

  // Override page with dialog auto-dismiss + LambdaTest status reporting
  page: async ({ context, options }, use, testInfo) => {
    const page = await context.newPage();

    // Auto-dismiss native browser dialogs
    page.on('dialog', async (dialog) => {
      await dialog.dismiss();
    });

    await use(page);

    // Report test status to LambdaTest after test completes
    if (options.executionEnv !== 'local') {
      const status = testInfo.status === 'passed' ? 'passed' : 'failed';
      const remark = testInfo.status === 'passed'
        ? `Passed in ${(testInfo.duration / 1000).toFixed(1)}s`
        : testInfo.error?.message?.slice(0, 200) ?? 'Test failed';
      try {
        await page.evaluate(
          (_: unknown) => {},
          `lambdatest_action: ${JSON.stringify({
            action: 'setTestStatus',
            arguments: { status, remark },
          })}`
        );
      } catch {
        // If page.evaluate fails, try via a new page in the same context
        try {
          const statusPage = context.pages()[0] ?? await context.newPage();
          await statusPage.evaluate(
            (_: unknown) => {},
            `lambdatest_action: ${JSON.stringify({
              action: 'setTestStatus',
              arguments: { status, remark },
            })}`
          );
        } catch {
          // Best effort — status won't update
        }
      }
    }
  },
});

export { expect } from '@playwright/test';
