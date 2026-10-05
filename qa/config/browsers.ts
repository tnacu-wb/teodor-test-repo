/**
 * Browser & Viewport Configuration
 *
 * Defines available viewports (desktop, laptop, tablet, mobile) and
 * local browser projects (Chrome, Firefox, Edge, Safari) for Playwright.
 *
 * Chrome includes --disable-blink-features=AutomationControlled to bypass
 * Akamai WAF detection on UAT/DIT environments.
 */

import { devices } from '@playwright/test';

// ─── Viewports ──────────────────────────────────────────────────────────────────

export const viewports = {
  desktop: { width: 1920, height: 1080 },
  laptop: { width: 1366, height: 768 },
  tablet: { width: 768, height: 1024 },
  mobile: { width: 393, height: 852 },
};

export type ViewportName = keyof typeof viewports;

/**
 * Resolve the configured viewport size for a Playwright run.
 *
 * @param options.viewport - Optional viewport name. Supported values: desktop, laptop, tablet, mobile.
 * @returns The matching width/height pair for the selected viewport.
 */
export function getViewport(options: { viewport?: unknown }): { width: number; height: number } {
  const name = (options.viewport ?? 'desktop') as ViewportName;

  if (!viewports[name]) {
    throw new Error(`Unknown viewport: "${name}". Valid options: ${Object.keys(viewports).join(', ')}`);
  }

  return viewports[name];
}

// ─── Local Browser Projects ─────────────────────────────────────────────────────

export const localProjects = [
  {
    name: 'chrome',
    use: {
      ...devices['Desktop Chrome'],
      channel: 'chrome',
      launchOptions: {
        args: ['--disable-blink-features=AutomationControlled'],
      },
    },
  },
  {
    name: 'firefox',
    use: { ...devices['Desktop Firefox'] },
  },
  {
    name: 'edge',
    use: {
      ...devices['Desktop Edge'],
      channel: 'msedge',
      launchOptions: {
        args: ['--disable-blink-features=AutomationControlled'],
      },
    },
  },
  {
    name: 'safari',
    use: { ...devices['Desktop Safari'] },
  },
];

export const browserNames = ['chrome', 'firefox', 'edge', 'safari'] as const;
export type BrowserName = (typeof browserNames)[number];

/**
 * Check whether a string matches a supported local Playwright browser name.
 *
 * @param value - Browser value from runtime options or config.
 * @returns True when the browser is one of the configured local projects.
 */
export function isBrowserName(value: string): value is BrowserName {
  return browserNames.includes(value as BrowserName);
}

/**
 * Return the local Playwright project configuration for a selected browser.
 *
 * @param options.browserName - Browser name to resolve. Supported values: chrome, firefox, edge, safari.
 * @returns Matching project configuration entries from the local project list.
 */
export function getLocalProjects(options: { browserName: BrowserName }) {
  const browser = options.browserName;

  if (!isBrowserName(browser)) {
    throw new Error(`Unknown browser: "${browser}". Valid options: ${browserNames.join(', ')}`);
  }

  const projects = localProjects.filter((p) => p.name === browser);
  if (projects.length === 0) {
    throw new Error(`No local projects configured for browser: "${browser}".`);
  }

  return projects;
}
