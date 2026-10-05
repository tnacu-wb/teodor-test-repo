import { expect, type BrowserContext, type ConsoleMessage, type Page } from '@playwright/test';
import { Locales } from '../test-data/locales';

/** A browser console entry captured during a Playwright test. */
export interface BrowserLog {
  level: string;
  message: string;
  timestamp: number;
}

/** Options for filtering captured API errors. */
export interface ApiErrorOptions {
  logs?: BrowserLog[];
  errorCodes?: Array<string | number>;
  logLevels?: string[];
  includeAllDetails?: boolean;
  throwOnError?: boolean;
}

/**
 * Basic browser specific methods.
 */
export class BrowserUtils {
  private constructor() {}

  private static readonly logs = new WeakMap<Page, BrowserLog[]>();

  /**
   * Return the current page URL.
   * @param page active Playwright page
   * @returns current page URL
   */
  static getCurrentPageURL(page: Page): string {
    return page.url();
  }

  /**
   * Return the current page URL without username and password.
   * @param page active Playwright page
   * @returns URL with URL credentials removed
   */
  static getCurrentPageURLWithoutUsernameAndPassword(page: Page): string {
    const url = new URL(page.url());
    url.username = '';
    url.password = '';
    return url.toString();
  }

  /**
   * Return the number of active browser tabs.
   * @param context active Playwright browser context
   * @returns number of browser tabs
   */
  static getNumberOfBrowserTabs(context: BrowserContext): number {
    return context.pages().length;
  }

  /**
   * Return the reservation ID from the current URL.
   * @param page active Playwright page
   * @returns reservation ID, when present
   */
  static getReservationIdFromUrl(page: Page): string | null {
    const reservationId = new URL(page.url()).searchParams.get('reservationId');
    console.log(` The Reservation Id from the URL: ${reservationId}`);
    return reservationId;
  }

  /**
   * Return the n-th browser tab; callers use the returned page for subsequent actions.
   * @param context active Playwright browser context
   * @param index one-based tab index
   * @returns the requested page
   */
  static switchToTabByIndex(context: BrowserContext, index: number): Page {
    expect(context.pages().length, 'Validate the number of opened browser tabs is greater than or equal to the required index').toBeGreaterThanOrEqual(index);
    return context.pages()[index - 1]!;
  }

  /**
   * Return the one-based index of the current browser tab.
   * @param context active Playwright browser context
   * @param page current Playwright page
   * @returns tab index, or zero when the page does not belong to the context
   */
  static getIndexOfCurrentTab(context: BrowserContext, page: Page): number {
    return context.pages().indexOf(page) + 1;
  }

  /**
   * Return the current browser URL with its locale route replaced.
   * @param page active Playwright page
   * @param newLocale locale route segment to apply
   * @returns localized browser URL
   */
  static getBrowserUrlWithChangedLocale(page: Page, newLocale: string): string {
    console.log('Get browser url with changed locale');
    const isInnBusiness = global.browser?.options?.app === 'pib';
    const currentLocaleUrl = Locales.isEnglishWebsite(global.browser?.options?.locale ?? 'gb-en') ?
      (isInnBusiness ? Locales.EN_GB_IB_URL : Locales.GB_EN_URL) :
      (isInnBusiness ? Locales.DE_DE_IB_URL : Locales.DE_DE_URL);
    return BrowserUtils.getCleanCurrentURL(page).replace(currentLocaleUrl, newLocale);
  }

  /**
   * Return the current browser URL without URL credentials.
   * @param page active Playwright page
   * @returns cleaned current URL
   */
  static getCleanCurrentURL(page: Page): string {
    return BrowserUtils.getCurrentPageURLWithoutUsernameAndPassword(page);
  }

  /**
   * Start recording browser console logs for later API-error assertions.
   * @param page active Playwright page
   */
  static captureBrowserLogs(page: Page): void {
    if (BrowserUtils.logs.has(page)) return;
    const logs: BrowserLog[] = [];
    BrowserUtils.logs.set(page, logs);
    page.on('console', (message: ConsoleMessage) => {
      logs.push({ level: message.type().toUpperCase(), message: message.text(), timestamp: Date.now() });
    });
  }

  /**
   * Identify API errors from captured browser logs.
   * @param options log filtering and error-handling options
   * @returns matching errors and their count
   */
  static identifyApiErrors({ logs = [], errorCodes = ['500', '502', '503', '504'], logLevels = ['SEVERE'], includeAllDetails = false, throwOnError = false }: ApiErrorOptions = {}): { hasErrors: boolean; errorCount: number; errors: Array<Record<string, unknown>> } {
    const errors = logs.filter((log) => logLevels.includes(log.level) && errorCodes.some((code) => log.message.includes(String(code)))).map((error) => {
      const errorCode = errorCodes.find((code) => error.message.includes(String(code)));
      const url = error.message.match(/https?:\/\/[^\s"]+/)?.[0] ?? null;
      return includeAllDetails ? { ...error, errorCode, url, fullLog: error } : { ...error, errorCode, url };
    });
    const result = { hasErrors: errors.length > 0, errorCount: errors.length, errors };
    if (throwOnError && result.hasErrors) throw new Error(`API error detected (${String(errors[0]?.errorCode)}): ${String(errors[0]?.message)}`);
    return result;
  }

  /**
   * Get captured browser logs with optional level filtering.
   * @param page active Playwright page
   * @param levels log levels to retain
   * @returns matching browser logs
   */
  static getBrowserLogs(page: Page, levels: string[] | null = null): BrowserLog[] {
    const logs = BrowserUtils.logs.get(page) ?? [];
    return !levels?.length ? logs : logs.filter((log) => levels.includes(log.level));
  }

  /**
   * Validate the number of active browser tabs.
   * @param context active Playwright browser context
   * @param tabsCount expected number of tabs
   */
  static validateNumberOfBrowserTabs(context: BrowserContext, tabsCount: number): void {
    expect(BrowserUtils.getNumberOfBrowserTabs(context), 'Validate number of browser tab that are opened').toBe(tabsCount);
  }

  /**
   * Validate whether the browser URL changed from a previous value.
   * @param page active Playwright page
   * @param browserUrlValue previous browser URL
   * @param shouldBeChanged whether the URL should have changed
   */
  static validateBrowserUrl(page: Page, browserUrlValue = '', shouldBeChanged = true): void {
    const currentUrl = BrowserUtils.getCleanCurrentURL(page);
    if (shouldBeChanged) expect(browserUrlValue, 'The browser url should change').not.toBe(currentUrl);
    else expect(browserUrlValue, 'The browser url should not change').toBe(currentUrl);
  }

  /**
   * Wait until the browser URL includes the expected value.
   * @param page active Playwright page
   * @param expectedValue URL value expected after redirect
   */
  static async validateBrowserUrlIncludesValue(page: Page, expectedValue: string): Promise<void> {
    await expect.poll(() => BrowserUtils.getCleanCurrentURL(page), { message: 'URL was not updated after redirect' }).toContain(expectedValue);
  }

  /** Scroll to the bottom of the page. */
  static async scrollToBottom(page: Page): Promise<void> {
    await page.evaluate(() => window.scrollBy(0, document.body.scrollHeight));
  }

  /** Click the browser back button and navigate to the previous page. */
  static async clickBackButtonFromBrowser(page: Page): Promise<void> {
    await page.goBack();
  }

  /**
   * Get a query parameter value from the current page URL.
   * @param page active Playwright page
   * @param paramName query parameter name
   * @returns parameter value, when present
   */
  static getParamValueFromUrl(page: Page, paramName: string): string | null {
    const value = new URL(page.url()).searchParams.get(paramName);
    console.log(`Value for param ${paramName} is ${value}`);
    return value;
  }

  /**
   * Wait for redirection to a link and validate the final URL.
   * @param page active Playwright page
   * @param pageName expected URL
   */
  static async validateUserIsRedirectedToLink(page: Page, pageName: string): Promise<void> {
    await expect.poll(() => BrowserUtils.getCurrentPageURL(page), { timeout: 150_000, message: `After redirect the URL=${page.url()}` }).toContain(pageName);
    expect(BrowserUtils.getCurrentPageURL(page), 'Link page').toBe(pageName);
  }
}