import { FeaturesToggles } from './featuresToggles';

/**
 * Reset browser runtime state and load the app-owned home page for deterministic E2E setup.
 */
export async function resetApplicationState(): Promise<void> {
  const page = global.page;
  if (!page) {
    throw new Error('global.page is not initialised; run specs with an app fixture (e.g. @fixtures/pi.fixture or @fixtures/ccui.fixture).');
  }
  await page.context().clearCookies();
  await FeaturesToggles.applyDefaultFeaturesTogglesOverrides();
  await page.evaluate(() => {
    try {
      localStorage.clear();
    } catch {
      // Storage is unavailable on restricted documents such as about:blank.
    }

    try {
      sessionStorage.clear();
    } catch {
      // Storage is unavailable on restricted documents such as about:blank.
    }
  });
  if (global.browser?.options?.app === 'ccui') {
    if (!global.ccuiPages?.homePage) {
      throw new Error('CCUI pages are not initialised; run CCUI specs with @fixtures/ccui.fixture so global.ccuiPages is registered.');
    }
    await global.ccuiPages.homePage.open({ isAuthorized: true });
    return;
  }

  if (global.browser?.options?.app === 'pib') {
    if (!global.pibPages?.loginIbPage) {
      throw new Error('PIB pages are not initialised; run PIB specs with @fixtures/pib.fixture so global.pibPages is registered.');
    }
    await global.pibPages.loginIbPage.open({ acceptCookies: false });
    return;
  }

  if (!global.piPages?.bartHomePage) {
    throw new Error('PI pages are not initialised; run PI specs with @fixtures/pi.fixture so global.piPages is registered.');
  }
  await global.piPages.bartHomePage.open();
}

