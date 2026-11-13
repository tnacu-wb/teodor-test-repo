/**
 * One dailyPrice response object from hotel availability API response
 * response example: 
 {
  "data": {
    "hotelAvailability": {
      "hotelId": "LONEUS",
      "startDate": "2022-09-16",
      "endDate": "2022-09-17",
      "available": true,
      "roomRates": [
        {
          "ratePlanCode": "NONFLEX",
          "roomTypes": [
            {
              "roomType": "DB",
              "adults": 1,
              "children": 0,
              "rooms": [
                {
                  "pmsRoomType": "DOUBLE",
                  "silentSubstitution": true,
                  "roomPriceBreakdown": {
                    "totalNetAmount": 1998,
                    "currencyCode": "GBP",
                    "dailyPrices": [
                      {
                        "date": "2022-09-16",
                        "netPrice": 999
                      }
                    ]
                  }
                }
              ]
            }
          ]
        }
      ]
    }
  }
}
 */
export class DailyPrice {
  [key: string]: unknown;
  date?: string;
  netPrice?: number;

  /**
   * DailyPrice constructor
   * @param data object data
   * @param data.dailyPrice dailyPrice
   */
  constructor(data: { dailyPrice?: Record<string, unknown> } = {}) {
    const dailyPrice = data.dailyPrice ?? {};
    this.date = dailyPrice.date as string | undefined;
    this.netPrice = dailyPrice.netPrice as number | undefined;
  }

  static fromResponse(data: { dailyPrice?: Record<string, unknown> }): DailyPrice {
    return new DailyPrice(data);
  }
}
