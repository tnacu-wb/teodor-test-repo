import { RateClassification } from './rateClassification';

/**
response example:
{
  "data": {
    "ratesInformation": {
      "rateClassifications": [
        {
          "rateClassification": "A",
          "rateName": "Flex",
          "rateDescription": "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival"
        }
      ]
    }
  }
}
 */
export class RatesInformation {
  [key: string]: unknown;
  rateClassifications: RateClassification[] = [];

  /**
   * Rates Information constructor
   * @param data object data
   * @param data.ratesInformationApiResponse response from API
   */
  constructor(data: { ratesInformationApiResponse?: { rateClassifications?: Array<Record<string, unknown>> } } = {}) {
    const rateClassifications = data.ratesInformationApiResponse?.rateClassifications ?? [];
    this.rateClassifications = rateClassifications.map(
      (rateClassificationItem) => new RateClassification({ rateClassificationObject: rateClassificationItem }),
    );
  }

  static fromResponse(data: { ratesInformationApiResponse?: { rateClassifications?: Array<Record<string, unknown>> } }): RatesInformation {
    return new RatesInformation(data);
  }

  /**
   * Get Rate Clasification By Plan Code
   * @param ratePlanCode ratePlanCode
   * @returns first object that matches the rate plan code
   */
  async getRateClassificationByRatePlanCode(ratePlanCode?: string): Promise<RateClassification | undefined> {
    return this.rateClassifications.find((item) => item.rateClassification === ratePlanCode);
  }

  /**
   * Get Rate Clasification By Rate Name
   * @param rateName rateName
   * @returns first object that matches the given rate name
   */
  async getRateClassificationByRateName(rateName?: string): Promise<RateClassification | undefined> {
    return this.rateClassifications.find((item) => item.rateName === rateName);
  }

}
