import { type Page, type Locator } from '@playwright/test';

/**
 * Shared Cookie Consent Component — handles the Premier Inn cookie banner
 * (a Chakra dialog) that appears across all channels (PI, CCUI, PIB).
 */
export class CookieConsentComponent {
  private readonly page: Page = global.page;

  /** "Accept all cookies" button on the consent dialog. */
  readonly acceptAllButton: Locator = this.page.locator('#accept-all-cookies-button');
  /** OneTrust "Accept all cookies" button used when the OneTrust rollout is enabled. */
  readonly oneTrustAcceptAllButton: Locator = this.page.locator('#onetrust-accept-btn-handler');

  /**
   * Pre-set consent cookies before navigation so the cookie banner never renders.
   * These are the Premier Inn consent cookies (not OneTrust).
   */
  async preSetConsentCookies(): Promise<void> {
    console.log('Pre-setting consent cookies');
    const env = (global.browser?.options?.env || process.env.ENV || 'uat').toLowerCase();
    const cookieDomain = `.${env}.premierinn.digital`;
    const localeString = global.browser?.options?.locale ?? process.env.LOCALE ?? 'gb-en';
    const [country = 'gb', language = 'en'] = localeString.toLowerCase().split('-');
    const cookiePaths = ['/', `/${country}`, `/${country}/${language}`, `/${language}-${country}`];
    const now = new Date().toISOString();
    const oneTrustConsentValue = `isGpcEnabled=0&datestamp=${encodeURIComponent(now)}&version=202406.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=playwright&interactionCount=1&landingPath=NotLandingPage&groups=C0001:1,C0002:1,C0003:1,C0004:1&AwaitingReconsent=false`;

    const cookies = cookiePaths.flatMap((path) => [
      { name: 'consent_cookie', value: '1', domain: cookieDomain, path, secure: true },
      { name: 'permissionPerformance', value: 'true', domain: cookieDomain, path, secure: true },
      { name: 'permissionExperience', value: 'true', domain: cookieDomain, path, secure: true },
      { name: 'permissionMarketing', value: 'true', domain: cookieDomain, path, secure: true },
      { name: 'OptanonAlertBoxClosed', value: now, domain: cookieDomain, path, secure: true },
      { name: 'OptanonConsent', value: oneTrustConsentValue, domain: cookieDomain, path, secure: true },
    ]);

    await this.page.context().addCookies(cookies);
  }

  /**
   * Dismiss the cookie consent banner if displayed by clicking "Accept all cookies".
   * No-op if the banner is not present (e.g. already accepted or pre-set).
   */
  async dismissIfPresent(): Promise<void> {
    console.log('Dismissing cookie consent banner if present');
    for (const acceptButton of [this.acceptAllButton, this.oneTrustAcceptAllButton]) {
      try {
        await acceptButton.click({ timeout: 5000 });
        return;
      } catch {
        // Try the next known cookie consent implementation.
      }
    }
  }
}
