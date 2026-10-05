import { App, Environment } from '@config/index';
import { test as base, expect as playwrightExpect, type Browser, type Page } from '@playwright/test';
import { ApiReservationCalls } from '../api/graphql/apiReservationCalls';
import { OhipApiCalls } from '../api/ohip/ohipApiCalls';
import { FeaturesToggles } from '../utils/featuresToggles';

export type RuntimeOptions = {
  app: App;
  env: Environment;
  browserName: string;
  viewport: string;
  locale: string;
  executionEnv: string;
  headed: boolean;
  fullyParallel: boolean;
  threadCount: number;
  testRetries: number;
  grep: string;
  linkTm: boolean;
  runNumber: string;
  ci: boolean;
  baseUrl: string;
  secureUrl: string;
  secureUrl2?: string;
  httpAuthUsername: string;
  httpAuthPassword: string;
  oldAuthPassword: string;
  aemAuthUsername: string;
  aemAuthPassword: string;
  ltUsername: string;
  ltAccessKey: string;
  featuresToggles: string;
  entityApiBaseUrl: string;
  eckohWebhookEndpoint: string;
  paymentWebhookXWhitApiKey: string;
  cdhUatEndpoint: string;
  actionTimeout: number;
  navigationTimeout: number;
  expectTimeout: number;
  aemBaseUrl: string;
  // OHIP API configuration
  ohipApiBaseUrl: string;
  ohipClientId: string;
  ohipClientSecret: string;
  ohipXApiKey: string;
};

export type BaseOptions = {
  options: Partial<RuntimeOptions> & { app: App };
};

type BrowserWithOptions = Browser & BaseOptions

declare global {
  var browser: BrowserWithOptions
  var page: Page;
  var expect: typeof playwrightExpect
}
type AutoTestFixtures = {
  forEachTest: void;
};

/**
 * Cancel and clear every reservation registered during the completed test.
 */
async function cancelCreatedReservations(): Promise<void> {
  const createdReservations = [...ApiReservationCalls.createdReservations];
  ApiReservationCalls.createdReservations = [];

  for (const createdReservation of createdReservations) {
    for (const reservation of createdReservation.items ?? []) {
      if (!createdReservation.hotelId || !reservation.sourceId) {
        console.log(`Skipping cancellation for incomplete created reservation "${createdReservation.reference}".`);
        continue;
      }
      try {
        await OhipApiCalls.cancelHotelReservation(
          createdReservation.hotelId,
          reservation.sourceId,
          createdReservation.reference,
        );
      } catch (error) {
        console.log('Error when cancelling reservation:');
        console.log(error);
      }
    }
  }
}

type AutoWorkerFixtures = {
  forEachWorker: void;
};

const testWithOptions = base.extend<BaseOptions>({
  options: [{ app: 'pi' }, { option: true }],
});

const testBeforeEachWorker = testWithOptions.extend<{}, AutoWorkerFixtures>({
  forEachWorker: [async ({ browser }, use, info) => {
    console.log(`Starting test worker ${info.workerIndex}`);

    const projectUse = info.project.use as typeof info.project.use & { options?: BaseOptions['options'] };
    const configuredOptions = projectUse.options;
    const app = configuredOptions?.app ?? 'pi';

    global.browser = browser as BrowserWithOptions;
    global.browser.options = { ...configuredOptions, app };
    global.expect = playwrightExpect;

    await use();

    console.log(`Stopping test worker ${info.workerIndex}`);
  }, { scope: 'worker', auto: true }],
});

// ─── Base Fixture Types ─────────────────────────────────────────────────────────

/**
 * Cross-cutting fixtures shared by all apps (PI, PIB, CCUI).
 * Handles browser-level concerns that are identical regardless of which app is under test.
 */
type BaseFixtures = {
  /** Page with automatic dialog dismissal and LambdaTest auth handling. */
  appPage: Page;
};

// ─── Base Extended Test ─────────────────────────────────────────────────────────

/**
 * Base test fixture — extend this in app-specific fixtures (pi, pib, ccui).
 *
 * Provides:
 * - `appPage`: a Page instance with:
 *   - Dialog auto-dismiss
 *   - LambdaTest auth header injection (for browsers where httpCredentials doesn't propagate)
 *
 * Usage in app fixtures:
 *   import { test as base } from './base.fixture';
 *   export const test = base.extend<PIFixtures>({ ... });
 */
export const test = testBeforeEachWorker.extend<BaseFixtures & AutoTestFixtures>({
  appPage: async ({ page, options }, use) => {
    // Auto-dismiss native browser dialogs (alert, confirm, prompt)
    page.on('dialog', async (dialog) => {
      await dialog.dismiss();
    });

    // Force auth header on LambdaTest where httpCredentials may not propagate
    // to all browser engines (Firefox, WebKit). This intercepts every request
    // and injects the Basic auth header at the network level.
    if (options.executionEnv !== 'local' && options.httpAuthUsername) {
      const credentials = Buffer.from(
        `${options.httpAuthUsername}:${options.httpAuthPassword}`
      ).toString('base64');

      await page.route('**/*', async (route) => {
        await route.continue({
          headers: {
            ...route.request().headers(),
            authorization: `Basic ${credentials}`,
          },
        });
      });
    }

    await use(page);
  },
  /** Per-test fixture — extend this in app-specific fixtures (pi, pib, ccui). 
  *
  */
  forEachTest: [async ({ page }, use, testInfo) => {
    global.page = page;

    await use();

    if (testInfo.status !== 'passed' && testInfo.status !== 'skipped') {
      try {
        const featureToggles = await FeaturesToggles.getPageFeaturesTogglesList();
        console.log(`Failed page URL: ${page.url()}`);
        console.log(`The current page feature toggles: ${JSON.stringify(featureToggles)}`);
      } catch (error) {
        console.log('Unable to log current page feature toggles:');
        console.log(error);
      }
    }
    if (testInfo.status !== 'skipped') {
      await cancelCreatedReservations();
    }
  }, { auto: true }],
});

export { playwrightExpect as expect };
