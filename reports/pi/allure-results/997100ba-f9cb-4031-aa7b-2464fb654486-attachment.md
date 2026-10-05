# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/ccui/baseline-e2e-agent-uk-hotel-flex-poa-bac-amend-add-adult.spec.ts >> CCUI baseline book as agent POA BAC amendment >> Test Book as Agent: UK Hotel, 1 night, 1 Single room, 1 adult, Flex rate, Leisure as purpose of stay, home address, POA [BAC] and Amend by add adult. TestCase ID: 379925 @regression @TC-379925
- Location: qa/tests/regressions/ccui/baseline-e2e-agent-uk-hotel-flex-poa-bac-amend-add-adult.spec.ts:23:7

# Error details

```
Error: PI pages are not initialised; run PI specs with @fixtures/pi.fixture so global.piPages is registered.
```

# Test source

```ts
  1  | import { FeaturesToggles } from './featuresToggles';
  2  | 
  3  | /**
  4  |  * Reset browser runtime state and load the app-owned home page for deterministic E2E setup.
  5  |  */
  6  | export async function resetApplicationState(): Promise<void> {
  7  |   const page = global.page;
  8  |   if (!page) {
  9  |     throw new Error('global.page is not initialised; run specs with an app fixture (e.g. @fixtures/pi.fixture or @fixtures/ccui.fixture).');
  10 |   }
  11 |   await page.context().clearCookies();
  12 |   await FeaturesToggles.applyDefaultFeaturesTogglesOverrides();
  13 |   await page.evaluate(() => {
  14 |     try {
  15 |       localStorage.clear();
  16 |     } catch {
  17 |       // Storage is unavailable on restricted documents such as about:blank.
  18 |     }
  19 | 
  20 |     try {
  21 |       sessionStorage.clear();
  22 |     } catch {
  23 |       // Storage is unavailable on restricted documents such as about:blank.
  24 |     }
  25 |   });
  26 |   if (global.browser?.options?.app === 'ccui') {
  27 |     if (!global.ccuiPages?.homePage) {
  28 |       throw new Error('CCUI pages are not initialised; run CCUI specs with @fixtures/ccui.fixture so global.ccuiPages is registered.');
  29 |     }
  30 |     await global.ccuiPages.homePage.open({ isAuthorized: true });
  31 |     return;
  32 |   }
  33 | 
  34 |   if (global.browser?.options?.app === 'pib') {
  35 |     if (!global.pibPages?.loginIbPage) {
  36 |       throw new Error('PIB pages are not initialised; run PIB specs with @fixtures/pib.fixture so global.pibPages is registered.');
  37 |     }
  38 |     await global.pibPages.loginIbPage.open({ acceptCookies: false });
  39 |     return;
  40 |   }
  41 | 
  42 |   if (!global.piPages?.bartHomePage) {
> 43 |     throw new Error('PI pages are not initialised; run PI specs with @fixtures/pi.fixture so global.piPages is registered.');
     |           ^ Error: PI pages are not initialised; run PI specs with @fixtures/pi.fixture so global.piPages is registered.
  44 |   }
  45 |   await global.piPages.bartHomePage.open();
  46 | }
  47 | 
  48 | 
```