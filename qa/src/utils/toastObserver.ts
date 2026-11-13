import { expect, type Page } from '@playwright/test';

/** A toast observed in the browser document. */
export interface ToastHit {
  text: string;
  ts: number;
}

/**
 * Toast observer with methods to validate toast notifications.
 */
export class ToastObserver {
  private constructor() {}

  /**
   * Get toast notifications scanned on the page, waiting until at least one is observed.
   * @param page active Playwright page
   * @returns observed toast notifications
   */
  static async getNumberOfToastNotifications(page: Page): Promise<ToastHit[]> {
    console.log('Getting the number of toast notifications that were scanned on the page...');
    await expect.poll(() => page.evaluate(() => (window as Window & { __toastHits?: ToastHit[] }).__toastHits?.length ?? 0), { timeout: 5_000, message: 'No toast detected' }).toBeGreaterThan(0);
    return page.evaluate(() => (window as Window & { __toastHits?: ToastHit[] }).__toastHits ?? []);
  }

  /**
   * Scan the page for toast notifications and retain them for later retrieval.
   * @param page active Playwright page
   * @param toastSelectorsArray optional custom toast selectors
   */
  static async scanPageForToastNotifications(page: Page, toastSelectorsArray: string[] = []): Promise<void> {
    console.log('Scanning the page for toast notifications...');
    await page.evaluate((selectors) => {
      const toastWindow = window as Window & { __toastHits?: ToastHit[]; __toastLast?: string; __toastStop?: () => void };
      toastWindow.__toastStop?.();
      toastWindow.__toastHits = [];
      toastWindow.__toastLast = '';
      const scan = () => {
        for (const node of document.querySelectorAll(selectors.join(','))) {
          const text = (node.textContent ?? '').replace(/\s+/g, ' ').trim();
          if (text && text !== toastWindow.__toastLast) {
            toastWindow.__toastLast = text;
            toastWindow.__toastHits!.push({ text, ts: Date.now() });
          }
        }
      };
      scan();
      const observer = new MutationObserver(scan);
      observer.observe(document.documentElement, { subtree: true, childList: true, characterData: true, attributes: true });
      const intervalId = window.setInterval(scan, 100);
      toastWindow.__toastStop = () => {
        observer.disconnect();
        window.clearInterval(intervalId);
      };
    }, toastSelectorsArray);
  }

  /**
   * Stop the toast observer and retain the toast notifications already observed.
   * @param page active Playwright page
   */
  static async stopToastObserver(page: Page): Promise<void> {
    console.log('Stopping the toast observer...');
    await page.evaluate(() => (window as Window & { __toastStop?: () => void }).__toastStop?.());
  }

  /**
   * Validate that a toast notification with the expected text was displayed.
   * @param data observed toasts and expected toast text
   */
  static async validateToastNotificationWasDisplayed({ toasts, toastText }: { toasts: ToastHit[]; toastText: string | Promise<string> }): Promise<void> {
    expect(toasts.map((hit) => hit.text).join(' | '), 'Toast message is not included!').toContain(await toastText);
  }
}