# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: page.goto: net::ERR_INVALID_AUTH_CREDENTIALS at https://www.uat.premierinn.digital/gb/en/home.html
Call log:
  - navigating to "https://www.uat.premierinn.digital/gb/en/home.html", waiting until "domcontentloaded"

```

# Test source

```ts
  1  | import { type Page } from '@playwright/test';
  2  | import { CookieConsentComponent } from '../../components/shared/cookieConsent.component';
  3  | import { NotificationPopupComponent } from '../../components/shared/notificationPopup.component';
  4  | import { Locales } from '../../test-data/locales';
  5  | 
  6  | /**
  7  |  * Shared base for Premier Inn page objects.
  8  |  *
  9  |  * Provides access to the Playwright page plus cross-page components that need to
  10 |  * run around direct navigation, such as consent cookie setup and notification
  11 |  * prompt dismissal.
  12 |  */
  13 | export abstract class BasePage {
  14 |   /** Playwright page instance shared by the concrete page object. */
  15 |   readonly page: Page = global.page;
  16 |   /** Cookie consent helper used before navigation to avoid banner interference. */
  17 |   readonly cookieConsent: CookieConsentComponent = new CookieConsentComponent();
  18 |   /** Notification permission helper used after navigation when the prompt appears. */
  19 |   readonly notificationPopup: NotificationPopupComponent = new NotificationPopupComponent();
  20 | 
  21 |   /**
  22 |    * Navigate to a Premier Inn localized path.
  23 |    *
  24 |    * Accepts paths with or without a leading slash and prefixes them with the
  25 |    * active locale route segment, such as /gb/en or /de/de.
  26 |    */
  27 |   protected async openLocalizedPath(path: string): Promise<void> {
  28 |     console.log(`Navigating to localized path: ${path}`);
  29 |     await this.cookieConsent.preSetConsentCookies();
  30 | 
  31 |     const cleanPath = path.replace(/^\/+/, '');
  32 |     const targetPath = cleanPath ? `${this.localizedRoutePrefix}/${cleanPath}` : this.localizedRoutePrefix;
  33 |     const navigationTimeout = global.browser?.options?.navigationTimeout;
> 34 |     await this.page.goto(targetPath, { waitUntil: 'domcontentloaded', timeout: navigationTimeout });
     |                     ^ Error: page.goto: net::ERR_INVALID_AUTH_CREDENTIALS at https://www.uat.premierinn.digital/gb/en/home.html
  35 |     await this.notificationPopup.dismissIfPresent();
  36 |   }
  37 | 
  38 |   private get localizedRoutePrefix(): string {
  39 |     const localeString = global.browser?.options?.locale ?? Locales.GB_EN.name;
  40 |     const locale = Locales.getLocaleByString(localeString);
  41 |     return `/${locale.country}/${locale.language}`;
  42 |   }
  43 | }
```