import { ApiCalls } from '../graphql/apiCalls';

/**
 * Response Example:
 * {
    "data": {
        "anonymousNewsletterPreferences": {
            "optIn": false,
            "secondOptIn": false,
            "secondOptInReq": false,
            "secondPartyOptIn": false,
            "thirdPartyVendorsOptIn": false,
            "suppressMarketingCheckbox": false
        }
    }
}
 */
export class AnonymousNewsletterPreferences {
  [key: string]: unknown;
  optIn?: boolean;
  secondOptIn?: boolean;
  secondOptInReq?: boolean;
  secondPartyOptIn?: boolean;
  suppressMarketingCheckbox?: boolean | string;
  thirdPartyVendorsOptIn?: boolean;

  /**
   * Anonymous Newsletter Preferences Constructor
   * @param data object data
   * @param data.anonymousNewsletterPreferences anonymous newsletter preferences
   */
  constructor(data: { anonymousNewsletterPreferences?: Record<string, unknown> } = {}) {
    const anonymousNewsletterPreferences = data.anonymousNewsletterPreferences ?? {};
    this.optIn = anonymousNewsletterPreferences.optIn as boolean | undefined;
    this.secondOptIn = anonymousNewsletterPreferences.secondOptIn as boolean | undefined;
    this.secondOptInReq = anonymousNewsletterPreferences.secondOptInReq as boolean | undefined;
    this.secondPartyOptIn = anonymousNewsletterPreferences.secondPartyOptIn as boolean | undefined;
    this.thirdPartyVendorsOptIn = anonymousNewsletterPreferences.thirdPartyVendorsOptIn as boolean | undefined;
    this.suppressMarketingCheckbox = anonymousNewsletterPreferences.suppressMarketingCheckbox as boolean | undefined;
  }

  static fromResponse(data: { anonymousNewsletterPreferences?: Record<string, unknown> }): AnonymousNewsletterPreferences {
    return new AnonymousNewsletterPreferences(data);
  }

  /**
   * Get and Validate Anonymous Newsletter Preferences
   * @param data object data
   * @param data.marketingPreferencesCriteria marketingPreferences request
   * @param data.expectedResult optIn \& suppressMarketingCheckbox expected values
   */
  static async getAndValidateAnonymousNewsletterPreferences(data: {
    marketingPreferencesCriteria?: Record<string, unknown>;
    expectedResult: { optIn?: boolean; suppressMarketingCheckbox?: boolean | string };
  }): Promise<void> {
    const { marketingPreferencesCriteria, expectedResult } = data;
    const { optIn } = expectedResult;
    const suppressMarketingCheckbox = expectedResult.suppressMarketingCheckbox ?? 'undefined';
    const marketingPreferences = (await ApiCalls.graphqlGetAnonymousNewsletterPreferences(
      marketingPreferencesCriteria,
    )) as Record<string, unknown>;

    if (marketingPreferences.optIn !== optIn) {
      throw new Error(`The expected optIn value is ${optIn}, while ${marketingPreferences.optIn} has been received.`);
    }
    const actualSuppressMarketingCheckbox = marketingPreferences.suppressMarketingCheckbox ?? 'undefined';
    if (actualSuppressMarketingCheckbox !== suppressMarketingCheckbox) {
      throw new Error(
        `The suppressMarketingCheckbox value is ${suppressMarketingCheckbox}, while ${actualSuppressMarketingCheckbox} has been received.`,
      );
    }
  }

}
