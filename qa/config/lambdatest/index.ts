/**
 * LambdaTest cloud execution configuration.
 *
 * Uses Playwright CDP (Chrome DevTools Protocol) via WSS to run tests
 * on LambdaTest infrastructure.
 *
 * Naming conventions:
 *   Build:   {App} | {Env} | {Suite} | {Date} | #{Run}
 *   Session: {TC-ID} | {Tags} | {Test Title}
 *
 * Credentials (LT_USERNAME, LT_ACCESS_KEY) come from .env.
 */

// ─── Helpers ──────────────────────────────────────────────────────────────────

/**
 * Derive the suite/purpose name from the GREP env var.
 */
import type { BaseOptions } from '../../src/fixtures/base.fixture';

type RuntimeOptions = BaseOptions['options'];

function getSuiteName(grep = ''): string {
  if (!grep) return 'Full';
  if (grep.includes('@smoke')) return 'Smoke';
  if (grep.includes('@regression')) return 'Regression';
  if (grep.includes('@TC-')) return 'TC';
  return 'Feature';
}

/**
 * Get run number. Sources (in priority order):
 * 1. RUN_NUMBER env var (set by CI: github.run_number)
 * 2. Fallback to HHmm timestamp for local runs
 */
function getRunNumber(runNumber?: string): string {
  return runNumber ?? new Date().toISOString().slice(11, 16).replace(':', '');
}

/**
 * Parse a test title and return a structured session name.
 *
 * Input:  "Homepage loads successfully @smoke @TC-805"
 * Output: "TC-805 | @smoke | Homepage loads successfully"
 */
export function formatSessionName(title: string): string {
  // Extract TC ID
  const tcMatch = title.match(/@TC-\d+/);
  const tcId = tcMatch ? tcMatch[0].replace('@', '') : '';

  // Extract all tags (except TC ID)
  const tags = (title.match(/@[a-zA-Z]\w+/g) ?? [])
    .filter((tag) => !tag.startsWith('@TC'))
    .join(' ');

  // Clean title: remove all @tags (including @TC-xxx)
  const cleanTitle = title.replace(/@TC-\d+/g, '').replace(/@\w+/g, '').trim();

  const parts = [tcId, tags, cleanTitle].filter(Boolean);
  return parts.join(' | ');
}

// ─── Config ───────────────────────────────────────────────────────────────────

/**
 * Get LambdaTest config. Throws only when called — not at import time.
 */
export function getLambdaTestConfig(options: RuntimeOptions) {
  const username = options.ltUsername;
  const accessKey = options.ltAccessKey;

  if (!username || !accessKey) {
    throw new Error(
      'LambdaTest credentials not found. Set LT_USERNAME and LT_ACCESS_KEY in your .env file.'
    );
  }

  const app = (options.app ?? 'pi').toUpperCase();
  const env = (options.env ?? 'uat').toUpperCase();
  const suite = getSuiteName(options.grep);
  const configuredBrowser = options.browserName ?? 'chrome';
  const browser = configuredBrowser.charAt(0).toUpperCase() + configuredBrowser.slice(1);
  const viewport = (options.viewport ?? 'desktop').toUpperCase();
  const date = new Date().toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' }).replace(/\//g, '-');
  const runNumber = getRunNumber(options.runNumber);

  return {
    username,
    accessKey,
    platform: 'Windows 11',
    buildName: `${app} | ${env} | ${suite} | ${browser} | ${viewport} | ${date} | #${runNumber}`,
    suite,
    video: true,
    network: true,
    console: true,
  };
}

// ─── Endpoint Builder ─────────────────────────────────────────────────────────

/**
 * Build a WSS endpoint URL for a single test session.
 *
 * @param browser - 'Chrome', 'pw-firefox', 'MicrosoftEdge', 'pw-webkit'
 * @param sessionName - structured session name (from formatSessionName)
 * @param tcId - optional Test Manager test case ID (e.g. "TC-805") to link automation to TM
 */
export function buildLambdaTestEndpoint(browser: string, sessionName: string, tcId: string | undefined, options: RuntimeOptions): string {
  const config = getLambdaTestConfig(options);

  const ltOptions: Record<string, unknown> = {
    user: config.username,
    accessKey: config.accessKey,
    platform: config.platform,
    build: config.buildName,
    name: sessionName,
    network: config.network,
    video: config.video,
    console: config.console,
    project: 'CORE QA POC',
    playwrightClientVersion: '1.52.0',
  };

  // Link automation session to Test Manager test case
  if (tcId) {
    ltOptions['tms.tc_id'] = tcId;
  }

  const capabilities = {
    'LT:Options': ltOptions,
    browserName: browser,
    browserVersion: 'latest',
  };

  return `wss://cdp.lambdatest.com/playwright?capabilities=${encodeURIComponent(JSON.stringify(capabilities))}`;
}

// ─── Browser ──────────────────────────────────────────────────────────────────

/**
 * Map a Playwright browser option to LambdaTest's browser capability name.
 * Falls back to BROWSER for callers that do not yet provide runtime options.
 */
export function getLambdaTestBrowserName(browserName = 'chrome'): string {
  const browser = browserName.toLowerCase();
  switch (browser) {
    case 'firefox': return 'pw-firefox';
    case 'edge': return 'MicrosoftEdge';
    case 'safari': return 'pw-webkit';
    default: return 'Chrome';
  }
}

// ─── Projects (legacy, kept for backward compatibility) ───────────────────────

/**
 * Get LambdaTest cloud browser projects for playwright config.
 * Note: The fixture-based approach (fixture.ts) is preferred over connectOptions.
 */
export function getLambdaTestProjects(options: RuntimeOptions) {
  const viewport = (options.viewport ?? 'desktop').toUpperCase();

  function buildEndpoint(browser: string, displayName: string): string {
    const config = getLambdaTestConfig(options);
    const capabilities = {
      'LT:Options': {
        user: config.username,
        accessKey: config.accessKey,
        platform: config.platform,
        build: config.buildName,
        name: `${config.suite} | ${displayName} | ${viewport}`,
        network: config.network,
        video: config.video,
        console: config.console,
        playwrightClientVersion: '1.52.0',
      },
      browserName: browser,
      browserVersion: 'latest',
    };
    return `wss://cdp.lambdatest.com/playwright?capabilities=${encodeURIComponent(JSON.stringify(capabilities))}`;
  }

  return [
    {
      name: 'chrome-lambdatest',
      use: { connectOptions: { wsEndpoint: buildEndpoint('Chrome', 'Chrome') } },
    },
    {
      name: 'firefox-lambdatest',
      use: { connectOptions: { wsEndpoint: buildEndpoint('pw-firefox', 'Firefox') } },
    },
    {
      name: 'edge-lambdatest',
      use: { connectOptions: { wsEndpoint: buildEndpoint('MicrosoftEdge', 'Edge') } },
    },
    {
      name: 'safari-lambdatest',
      use: { connectOptions: { wsEndpoint: buildEndpoint('pw-webkit', 'Safari') } },
    },
  ];
}
