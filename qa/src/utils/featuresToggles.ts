import type { Page } from '@playwright/test';

export type FeatureToggleValue = boolean | string;

export interface FeatureToggle {
  name: string;
  value: FeatureToggleValue;
}

/**
 * Unleash feature toggles support for reading and overriding web application feature toggles.
 */
export class FeaturesToggles {
  private constructor() {}

  private static get page(): Page {
    if (!global.page) {
      throw new Error('global.page is not available. Make sure the Playwright fixture has initialized the page before using feature toggles.');
    }

    return global.page;
  }

  static readonly FEATURE_TOGGLE_NAME1 = 'featureToggleName1';

  /**
   * Global feature toggle name/value list.
   * Example: [{ name: 'featureToggleName1', value: false }, { name: 'featureToggleName2', value: true }]
   */
  static featuresTogglesToOverride: FeatureToggle[] = [];

  /**
   * Default global feature toggles used to avoid in-flight releases affecting deterministic tests.
   */
  static readonly DEFAULT_FEATURES_TOGGLES_TO_OVERRIDE: FeatureToggle[] = [
    { name: 'release_bb_payment_redesign', value: false },
    { name: 'release_pi_payment_redesign', value: false },
    { name: 'release_bb_accompanying_guest_details', value: false },
    { name: 'release_pi_bb_ccui_barrier_free_label', value: false },
    { name: 'release_pi_room_upgrade_popup', value: false },
    { name: 'release_ib_pay_piba_euro', value: true },
    { name: 'release_pi_ccui_consolidate_mobile_landline', value: false },
    { name: 'release_pi_web_push_notifications', value: false },
    { name: 'release_pi_promo_code_site_wide', value: false },
    { name: 'release_pi_pib_swap_payment_options', value: false },
    { name: 'release_ccui_indigo_gdp-single-booking', value: true },
    { name: 'release_pi_pib_ccui_enable_hub_hotels_poa', value: true },
    { name: 'release_pi_bb_booking_history_redesign', value: false },
    { name: 'release_srp_dynamic_filters', value: false },
    { name: 'release_pi_srp_split_map_view', value: false },
    { name: 'release_pi_auth0_login', value: false },
    { name: 'release_one_trust_cookie_consent', value: false },
    { name: 'release_datatrans_integration', value: false },
    { name: 'release_ib_user_pilot', value: false },
  ];

  /**
   * Get featureToggles from sessionStorage for the current page.
   * sessionStorage stores feature toggles as JSON: {"featureToggleName1": true, "featureToggleName2": false}
   * @returns Feature toggle name/value objects from the current page.
   */
  static async getPageFeaturesTogglesList(): Promise<FeatureToggle[]> {
    let pageFeaturesToggles = '';

    try {
      pageFeaturesToggles = await FeaturesToggles.page.evaluate(() => sessionStorage.getItem('featureToggles') ?? '');
    } catch {
      // Some environments/pages can block sessionStorage access before the app is fully loaded.
    }

    const featuresToggles = pageFeaturesToggles ? JSON.parse(pageFeaturesToggles) as Record<string, FeatureToggleValue> : {};
    return Object.entries(featuresToggles).map(([name, value]) => ({ name, value }));
  }

  /**
   * Check if a feature toggle is enabled. The current page must be one affected by the toggle.
   * @param featureToggleName Feature toggle name to check.
   * @param throwIfNotExists Throw when the toggle does not exist; otherwise return false.
   * @returns True if the feature toggle is enabled, otherwise false.
   */
  static async isFeatureToggleEnabled(featureToggleName: string, throwIfNotExists = true): Promise<boolean> {
    const pageFeaturesTogglesList = await FeaturesToggles.getPageFeaturesTogglesList();
    const featureToggle = pageFeaturesTogglesList.find((toggle) => toggle.name === featureToggleName);

    if (featureToggle) {
      return featureToggle.value === true || featureToggle.value === 'true';
    }

    if (throwIfNotExists) {
      throw new Error(`${featureToggleName} feature toggle doesn't exist!`);
    }

    return false;
  }

  /**
   * Override feature toggles using the ftOverride cookie.
   * The ftOverride cookie stores values as: featureToggleName1=value1,featureToggleName2=value2
   * @param featuresTogglesList Feature toggle name/value list.
   * @param resetOverrides True to replace the ftOverride cookie with only the provided list.
   */
  static async overrideFeaturesToggles(featuresTogglesList: FeatureToggle[], resetOverrides = false): Promise<void> {
    const [cookieData] = resetOverrides ? [] : await FeaturesToggles.page.context().cookies().then((cookies) => cookies.filter((cookie) => cookie.name === 'ftOverride'));
    const existingFtOverrideValue = cookieData?.value ?? '';
    const featuresTogglesFromCookie = !resetOverrides && existingFtOverrideValue ? existingFtOverrideValue.split(',') : [];

    const featuresTogglesFromCookieList = featuresTogglesFromCookie
      .map((featureToggle) => {
        const [name, value] = featureToggle.trim().split('=');
        return { name: name.trim(), value: value.trim() };
      })
      .filter((featureToggle) => !featuresTogglesList.find((toggle) => toggle.name === featureToggle.name));

    const featuresTogglesToOverride = [...featuresTogglesFromCookieList, ...featuresTogglesList];
    const ftOverrideValue = featuresTogglesToOverride
      .map((featureToggle) => `${featureToggle.name}=${featureToggle.value}`)
      .join(',');

    console.log(`Override feature toggles using ftOverride cookie: ${ftOverrideValue}`);

    const env = browser.options.env;
    if (!env) {
      throw new Error('global.browser.options.env is not available. Make sure the Playwright fixture has initialized browser options before using feature toggles.');
    }

    await FeaturesToggles.page.context().addCookies([{
      name: 'ftOverride',
      value: ftOverrideValue,
      domain: `.${env}.premierinn.digital`,
      path: '/',
    }]);
  }

  /**
   * Apply global feature-toggle overrides for all tests from the home page.
   * FEATURES_TOGGLES can override defaults using: featureToggleName1=true,featureToggleName2=false
   */
  static async applyDefaultFeaturesTogglesOverrides(featuresTogglesParameter = global.browser?.options?.featuresToggles ?? ''): Promise<void> {
    FeaturesToggles.featuresTogglesToOverride = [...FeaturesToggles.DEFAULT_FEATURES_TOGGLES_TO_OVERRIDE];

    const featuresTogglesFromParameter = featuresTogglesParameter ? featuresTogglesParameter.split(',') : [];
    for (const featureToggle of featuresTogglesFromParameter) {
      const [featureToggleName, featureToggleValue] = featureToggle.trim().split('=').map((token) => token.trim());
      if (!featureToggleName || featureToggleValue === undefined) {
        continue;
      }

      FeaturesToggles.featuresTogglesToOverride = FeaturesToggles.featuresTogglesToOverride
        .filter((featureToggleToOverride) => featureToggleToOverride.name !== featureToggleName);
      FeaturesToggles.featuresTogglesToOverride.push({ name: featureToggleName, value: featureToggleValue });
    }

    await FeaturesToggles.overrideFeaturesToggles(FeaturesToggles.featuresTogglesToOverride, true);
  }
}
