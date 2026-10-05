import { DailyPrice } from './dailyPrice';

/**
 * One room price breakdown object from hotel availability API response
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
export class RoomPriceBreakdown {
  [key: string]: unknown;
  currencyCode?: string;
  dailyPrices: DailyPrice[] = [];
  totalNetAmount?: number;

  /**
   * Room Price Breakdown constructor
   * @param data object data
   * @param data.roomPriceBreakdownApiResponse roomPriceBreakdownApiResponse
   */
  constructor(data: { roomPriceBreakdownApiResponse?: Record<string, unknown> } = {}) {
    const roomPriceBreakdownApiResponse = data.roomPriceBreakdownApiResponse ?? {};
    this.totalNetAmount = roomPriceBreakdownApiResponse.totalNetAmount as number | undefined;
    this.currencyCode = roomPriceBreakdownApiResponse.currencyCode as string | undefined;
    const dailyPrices = Array.isArray(roomPriceBreakdownApiResponse.dailyPrices)
      ? (roomPriceBreakdownApiResponse.dailyPrices as Array<Record<string, unknown>>)
      : [];
    this.dailyPrices = dailyPrices.map((dailyPrice) => new DailyPrice({ dailyPrice }));
  }

  static fromResponse(data: { roomPriceBreakdownApiResponse?: Record<string, unknown> }): RoomPriceBreakdown {
    return new RoomPriceBreakdown(data);
  }

  /**
   * Retrieve the daily prices sum for specific number of days
   * @param numberOfDays number of days to sum the prices for
   * @param startingPos array starting position
   * @returns sum of daily prices
   */
  getDailyPricesSumForDays(numberOfDays = this.dailyPrices.length, startingPos = 0): number {
    let sum = 0;

    if (startingPos + numberOfDays > this.dailyPrices.length) {
      throw new Error('Exceeding array limits.');
    }

    for (let index = startingPos; index < startingPos + numberOfDays; index++) {
      sum += Number(this.dailyPrices[index]?.netPrice ?? 0);
    }

    return sum;
  }

}
