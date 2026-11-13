import { expect, type Page } from '@playwright/test';
import { type GuestDetailsModel } from '../test-data/guestDetails';
import { Locales } from '../test-data/locales';

/**
 * Local Storage, Session Storage and Cookies methods.
 */
export class StorageUtils {
  private constructor() {}

  /**
   * Delete Local Storage key.
   * @param page active Playwright page
   * @param key key to be deleted
   */
  static async deleteLocalStorageKey(page: Page, key: string): Promise<void> {
    await page.evaluate((storageKey) => localStorage.removeItem(storageKey), key);
  }

  /** Delete sign in cookies. */
  static async deleteSignInCookies(page: Page): Promise<void> {
    await StorageUtils.deleteCookies(page, ['access_token', 'id_token_cookie']);
  }

  /** Delete consent cookies. */
  static async deleteConsentCookies(page: Page): Promise<void> {
    await StorageUtils.deleteCookies(page, [
      'consent_cookie',
      'permissionPerformance',
      'permissionExperience',
      'permissionMarketing',
    ]);
  }

  /** Delete all cookies. */
  static async deleteAllCookies(page: Page): Promise<void> {
    await page.context().clearCookies();
  }

  /**
   * Get cookie value by cookie name.
   * @param page active Playwright page
   * @param cookieName the name of the cookie to return the value for
   * @returns the cookie value, when present
   */
  static async getCookieValue(page: Page, cookieName: string): Promise<string | undefined> {
    return (await page.context().cookies()).find((cookie) => cookie.name === cookieName)?.value;
  }

  /** Delete CCUI locale cookie. */
  static async deleteCcuiLocaleCookie(page: Page): Promise<void> {
    await StorageUtils.deleteCookies(page, ['CCUI_LOCALE']);
  }

  /** Add consent cookies to avoid showing accept-cookies alerts. */
  static async addConsentCookies(page: Page): Promise<void> {
    const expires = Math.round(Date.now() / 1_000 + 10_800);
    const url = page.url();
    const cookies = [
      { name: 'consent_cookie', value: '1' },
      { name: 'permissionPerformance', value: 'true' },
      { name: 'permissionExperience', value: 'true' },
      { name: 'permissionMarketing', value: 'true' },
    ].map((cookie) => ({ ...cookie, expires, httpOnly: false, secure: true, url }));

    await StorageUtils.deleteConsentCookies(page);
    await page.context().addCookies(cookies);
  }

  /** Add accompanying-guest cookies used for Adobe Target A/B testing. */
  static async addAccompanyingGuestCookies(page: Page): Promise<void> {
    await page.context().addCookies([{
      name: 'guestDetailsAccompanyingGuest',
      value: 'variant',
      url: page.url(),
    }]);
  }

  /**
   * Validate Form Details cookie.
   * @param page active Playwright page
   * @param guestDetails expected guest details
   */
  static async validateFormDetails(page: Page, guestDetails: GuestDetailsModel): Promise<void> {
    console.log('Validate Form Details cookie');
    const formDetails = await page.evaluate(() => JSON.parse(localStorage.getItem('formDetails') ?? '{}') as Record<string, unknown>);
    const { booker, reasonForStay } = guestDetails;
    const { address } = booker;
    const isGerman = address.countryCode === 'DE';
    const isEnglishWebsite = Locales.isEnglishWebsite(global.browser?.options?.locale ?? 'gb-en');
    const cityName = isGerman ? address.cityName :
      (isEnglishWebsite && address.postalCode && !isGerman ? address.addressLine4 : '');

    expect.soft(Object.keys(formDetails), 'Form Details number of keys').toHaveLength(18);
    expect.soft(formDetails.cityName, 'Location/City name should match').toBe(cityName ?? '');
    expect.soft(formDetails.postcodeAddress, 'Address Postal Code should match').toBe(address.postalCode ?? '');
    expect.soft(formDetails.addressSelection, 'Address Type should match').toBe(address.addressType ?? '');
    expect.soft(formDetails.companyName, 'Company name should match').toBe(address.companyName ?? '');
    expect.soft(formDetails.countryCode, 'Country Code should match').toBe(address.countryCode ?? '');
    expect.soft(formDetails.email, 'Email Address should match').toBe(booker.emailAddress ?? '');
    expect.soft(formDetails.firstName, 'First Name should match').toBe(booker.firstName ?? '');
    expect.soft(formDetails.landline, 'Landline should match').toBe(`${booker.landlinePrefix ?? ''}${booker.landline ?? ''}`);
    expect.soft(formDetails.lastName, 'Last Name should match').toBe(booker.lastName ?? '');
    expect.soft(formDetails.addressLine1, 'Address Line 1 should match').toBe(address.addressLine1 ?? '');
    expect.soft(formDetails.addressLine2, 'Address Line 2 should match').toBe(address.addressLine2 ?? '');
    expect.soft(formDetails.addressLine3, 'Address Line 3 should match').toBe(address.addressLine3 ?? '');
    expect.soft(formDetails.addressLine4, 'Address Line 4 should match').toBe(isGerman ? '' : address.addressLine4 ?? '');
    expect.soft(formDetails.phone, 'Mobile should match').toBe(`${booker.mobilePrefix ?? ''}${booker.mobile ?? ''}`);
    expect.soft(formDetails.postalCode, 'Postal Code should match').toBe(address.postalCode ?? '');
    expect.soft(formDetails.reasonForStay, 'Reason for Stay should match').toBe(reasonForStay ?? '');
    expect.soft(formDetails.title, 'Title should match').toBe(booker.title ?? '');
    expect.soft(formDetails.manualAddressToggle, 'Manual address toggle').toBe('manualAddress');
  }

  /**
   * Validate cookie permissions.
   * @param page active Playwright page
   * @param permissions expected performance, experience, and marketing permissions
   */
  static async validateCookiesPermissions(
    page: Page,
    { performance = true, experience = true, marketing = true }: { performance?: boolean; experience?: boolean; marketing?: boolean } = {},
  ): Promise<void> {
    console.log('Validate cookies permissions');
    for (const { name, expected, label } of [
      { name: 'permissionPerformance', expected: performance, label: 'Performance' },
      { name: 'permissionExperience', expected: experience, label: 'Experience' },
      { name: 'permissionMarketing', expected: marketing, label: 'Marketing' },
    ]) {
      expect((await StorageUtils.getCookieValue(page, name)) === 'true', `${label} cookie value should be=${expected}`).toBe(expected);
    }
  }

  /** Delete only the named cookies while retaining the rest of the browser context. */
  private static async deleteCookies(page: Page, names: string[]): Promise<void> {
    const cookies = await page.context().cookies();
    await page.context().clearCookies();
    await page.context().addCookies(cookies.filter((cookie) => !names.includes(cookie.name)));
  }
}