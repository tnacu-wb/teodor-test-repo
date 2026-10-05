# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: qa/tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:7

# Error details

```
Error: page.goto: net::ERR_INVALID_AUTH_CREDENTIALS at https://www.uat.premierinn.digital/gb/en/home.html
Call log:
  - navigating to "https://www.uat.premierinn.digital/gb/en/home.html", waiting until "domcontentloaded"

```

# Test source

```ts
  1  | import { type Page, type Locator } from '@playwright/test';
  2  | import { SearchConsoleComponent } from '../../components/pi/searchConsole/searchConsole.component';
  3  | import { Locales } from '../../test-data/locales';
  4  | import { BasePage } from '../shared/base.page';
  5  | 
  6  | /**
  7  |  * PI Homepage - the main landing page for premierinn.com.
  8  |  * Contains the search console for hotel search.
  9  |  *
  10 |  * Composes shared components:
  11 |  * - CookieConsentComponent (cookie banner handling)
  12 |  * - NotificationPopupComponent (notification permission popup)
  13 |  * - SearchConsoleComponent (location, dates, rooms, submit)
  14 |  */
  15 | export class HomePage extends BasePage {
  16 |   /** Shared components */
  17 |   readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();
  18 | 
  19 |   /** Page-specific locators */
  20 |   readonly searchSubmitButton: Locator = this.searchConsole.submitButton;
  21 |   readonly searchLocationInput: Locator = this.searchConsole.locationInput;
  22 | 
  23 |   constructor() {
  24 |     super();
  25 |   }
  26 | 
  27 |   /**
  28 |    * Navigate to the PI homepage and handle initial popups.
  29 |    * Pre-sets consent cookies to prevent the cookie banner from appearing.
  30 |    */
  31 |   async open() {
  32 |     await this.cookieConsent.preSetConsentCookies();
  33 |     const locale = Locales.getLocaleByString(global.browser?.options?.locale ?? Locales.GB_EN.name);
> 34 |     await this.page.goto(`/${locale.country}/${locale.language}/home.html`, { waitUntil: 'domcontentloaded', timeout: 30000 });
     |                     ^ Error: page.goto: net::ERR_INVALID_AUTH_CREDENTIALS at https://www.uat.premierinn.digital/gb/en/home.html
  35 |     await this.notificationPopup.dismissIfPresent();
  36 |   }
  37 | 
  38 |   /**
  39 |    * Accept all cookies if the banner is displayed.
  40 |    * @deprecated Use `this.cookieConsent.dismissIfPresent()` instead.
  41 |    */
  42 |   async dismissCookieConsent() {
  43 |     await this.cookieConsent.dismissIfPresent();
  44 |   }
  45 | 
  46 |   /**
  47 |    * Dismiss the notification permission popup if displayed.
  48 |    * @deprecated Use `this.notificationPopup.dismissIfPresent()` instead.
  49 |    */
  50 |   async dismissNotificationPopup() {
  51 |     await this.notificationPopup.dismissIfPresent();
  52 |   }
  53 | 
  54 |   /**
  55 |    * Search for a hotel by name using the search console.
  56 |    * @param hotelName - The hotel name or location to search for
  57 |    */
  58 |   async searchForHotel(hotelName: string) {
  59 |     await this.searchConsole.searchForHotel(hotelName);
  60 |   }
  61 | 
  62 |   /**
  63 |    * Click the search/submit button to execute the search.
  64 |    */
  65 |   async submitSearch() {
  66 |     await this.searchConsole.submit();
  67 |   }
  68 | }
  69 | 
```