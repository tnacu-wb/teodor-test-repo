import { RoomPriceBreakdown } from './roomPriceBreakdown';

/**
 * One room rate object from hotel availability API response.
 * response example: 
{
  "data": {
    "hotelAvailability": {
      "roomRates": [
        {
          "roomTypes": [
            {
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
                      },
                      {
                        "date": "2022-09-17",
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
export class Room {
  [key: string]: unknown;
  pmsRoomType?: string;
  roomPriceBreakdown?: RoomPriceBreakdown;
  silentSubstitution?: boolean;

  /**
   * Room constructor
   * @param data object data
   * @param data.roomApiResponse response from API
   */
  constructor(data: { roomApiResponse?: Record<string, unknown> } = {}) {
    const roomApiResponse = data.roomApiResponse ?? {};
    this.pmsRoomType = roomApiResponse.pmsRoomType as string | undefined;
    this.silentSubstitution = roomApiResponse.silentSubstitution as boolean | undefined;
    this.roomPriceBreakdown = new RoomPriceBreakdown({ roomPriceBreakdownApiResponse: roomApiResponse.roomPriceBreakdown as Record<string, unknown> });
  }

  static fromResponse(data: { roomApiResponse?: Record<string, unknown> }): Room {
    return new Room(data);
  }
}
