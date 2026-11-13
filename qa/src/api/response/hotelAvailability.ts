import { RoomRate } from './roomRate';

/**
 * The hotel availability from API response
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
export class HotelAvailability {
  [key: string]: unknown;
  available?: boolean;
  endDate?: string;
  hotelId?: string;
  roomRates: RoomRate[] = [];
  startDate?: string;

  /**
   * HotelAvailability constructor
   * @param data object data
   * @param data.hotelAvailabilityApiResponse response from API
   */
  constructor(data: { hotelAvailabilityApiResponse?: Record<string, unknown> } = {}) {
    const hotelAvailabilityApiResponse = data.hotelAvailabilityApiResponse ?? {};
    this.hotelId = hotelAvailabilityApiResponse.hotelId as string | undefined;
    this.startDate = hotelAvailabilityApiResponse.startDate as string | undefined;
    this.endDate = hotelAvailabilityApiResponse.endDate as string | undefined;
    this.available = hotelAvailabilityApiResponse.available as boolean | undefined;
    const roomRates = Array.isArray(hotelAvailabilityApiResponse.roomRates)
      ? (hotelAvailabilityApiResponse.roomRates as Array<Record<string, unknown>>)
      : [];
    this.roomRates = roomRates.map((roomRate) => new RoomRate({ roomRateListApiResponse: roomRate }));
  }

  static fromResponse(data: { hotelAvailabilityApiResponse?: Record<string, unknown> }): HotelAvailability {
    return new HotelAvailability(data);
  }

  /**
   * Check if there are accessible room included in hotel availability response
   *
   * @param roomRate roomRate
   * @returns true if there are accessible room included, false otherwise
   */
  async isAccessibleRoomIncludedInRoomRate(roomRate?: string): Promise<boolean> {
    let accessibleRoomFound = false;
    const filteredRoomRates = this.roomRates.filter((item) => item.ratePlanCode === roomRate);
    for (const item of filteredRoomRates) {
      const roomTypes = Array.isArray(item.roomTypes) ? (item.roomTypes as Array<Record<string, unknown>>) : [];
      for (const roomType of roomTypes) {
        const rooms = Array.isArray(roomType.rooms) ? (roomType.rooms as Array<Record<string, unknown>>) : [];
        for (const room of rooms) {
          const pmsRoomType = String(room.pmsRoomType ?? '');
          accessibleRoomFound =
            accessibleRoomFound || pmsRoomType.startsWith('LOW') || pmsRoomType.startsWith('WET') || pmsRoomType.startsWith('ACC');
        }
      }
    }

    return accessibleRoomFound;
  }

}
