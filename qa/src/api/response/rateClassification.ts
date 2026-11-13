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
export class RateClassification {
  [key: string]: unknown;
  rateClassification?: string;
  rateDescription?: string;
  rateLongDescription?: string;
  rateName?: string;
  rateNotes?: string;
  rateOrder?: string;

  /**
   * Rate Classification constructor
   * @param data object data
   * @param data.rateClassificationObject rateClassificationObject
   */
  constructor(data: { rateClassificationObject?: Record<string, unknown> } = {}) {
    const rateClassificationObject = data.rateClassificationObject ?? {};
    this.rateClassification = rateClassificationObject.rateClassification as string | undefined;
    this.rateDescription = rateClassificationObject.rateDescription as string | undefined;
    this.rateName = rateClassificationObject.rateName as string | undefined;
    this.rateOrder = rateClassificationObject.rateOrder as string | undefined;
    this.rateLongDescription = rateClassificationObject.rateLongDescription as string | undefined;
    this.rateNotes = rateClassificationObject.rateNotes as string | undefined;
  }

  static fromResponse(data: { rateClassificationObject?: Record<string, unknown> }): RateClassification {
    return new RateClassification(data);
  }
}
