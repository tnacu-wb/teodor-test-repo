import type { Room as RoomOccupancy } from '../../test-data/room';
import type { Room } from './room';
import { RoomType } from './roomType';

/**
 * One room rate object from hotel availability API response.
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
export class RoomRate {
  [key: string]: unknown;
  ratePlanCode?: string;
  roomTypes: RoomType[] = [];

  /**
   * RoomRate constructor
   * @param data object data
   * @param data.roomRateListApiResponse response from API
   */
  constructor(data: { roomRateListApiResponse?: Record<string, unknown> } = {}) {
    const roomRateListApiResponse = data.roomRateListApiResponse ?? {};
    this.ratePlanCode = roomRateListApiResponse.ratePlanCode as string | undefined;
    const roomTypes = Array.isArray(roomRateListApiResponse.roomTypes)
      ? (roomRateListApiResponse.roomTypes as Array<Record<string, unknown>>)
      : [];
    this.roomTypes = roomTypes.map((roomType) => new RoomType({ roomTypeApiResponse: roomType }));
  }

  static fromResponse(data: { roomRateListApiResponse?: Record<string, unknown> }): RoomRate {
    return new RoomRate(data);
  }

  /**
   * Return the sum of daily prices the rooms
   * Leaving rooms undefined will result in the sum off all the rooms from hotelDetails
   * Rooms should be set only for the ones present in the reservation at the moment of the call
   * All types of rooms called in this function need to be present in hotelDetails
   * getRoomTypePricesSum( 3, 0, [Rooms.DOUBLE_2_ADULTS_0_CHILDREN] )
   * @param numberOfDays numberOfDays
   * @param startingDayPosition startingPosition
   * @param rooms rooms
   * @param pmsRoomType pmsRoomType
   * @returns sum of daily prices for the rooms
   */
  getRoomTypePricesSum(numberOfDays?: number, startingDayPosition = 0, rooms?: RoomOccupancy[], pmsRoomType?: string): number {
    let sum = 0;

    if (rooms) {
      for (const room of rooms) {
        for (const roomType of this.roomTypes) {
          if (roomType.adults === room.adultsNumber && roomType.children === room.childrenNumber && roomType.roomType === room.roomType.id) {
            if (pmsRoomType) {
              const matchedRoom = roomType.rooms.find((r: Room) => r.pmsRoomType === pmsRoomType);
              sum += matchedRoom ? matchedRoom.roomPriceBreakdown!.getDailyPricesSumForDays(numberOfDays, startingDayPosition) : 0;
            } else {
              sum += roomType.rooms[0].roomPriceBreakdown!.getDailyPricesSumForDays(numberOfDays, startingDayPosition);
            }
          }
        }
      }
    } else {
      for (const roomType of this.roomTypes) {
        if (pmsRoomType) {
          const matchedRoom = roomType.rooms.find((r: Room) => r.pmsRoomType === pmsRoomType);
          sum += matchedRoom ? matchedRoom.roomPriceBreakdown!.getDailyPricesSumForDays(numberOfDays, startingDayPosition) : 0;
        } else {
          sum += roomType.rooms[0].roomPriceBreakdown!.getDailyPricesSumForDays(numberOfDays, startingDayPosition);
        }
      }
    }
    return sum;
  }

  /**
   * Return the sum of daily prices for certain room type indexes.
   * @param data room type index
   * @param data.roomIndex room type index. It is 0 by default.
   * @param data.numberOfRooms the number of rooms. It will count all the rooms.
   * @param data.numberOfDays numberOfDays. undefined value means it will count all the days.
   * @param data.startingDayPosition startingDayPosition. It is 0 by default.
   * @param data.pmsRoomType pmsRoomType
   * @returns sum of daily prices for all the rooms
   */
  getRoomTypePricesSumFromRoomTypeIndex(data: {
    roomIndex?: number;
    numberOfRooms?: number;
    numberOfDays?: number;
    startingDayPosition?: number;
    pmsRoomType?: string;
  }): number {
    const { roomIndex = 0, numberOfRooms = this.roomTypes.length, numberOfDays, startingDayPosition = 0, pmsRoomType } = data;
    let sum = 0;
    for (let index = roomIndex; index < roomIndex + numberOfRooms; index++) {
      if (pmsRoomType) {
        const matchedRoom = this.roomTypes[index].rooms.find((r: Room) => r.pmsRoomType === pmsRoomType);
        sum += matchedRoom ? matchedRoom.roomPriceBreakdown!.getDailyPricesSumForDays(numberOfDays, startingDayPosition) : 0;
      } else {
        sum += this.roomTypes[index].rooms[0].roomPriceBreakdown!.getDailyPricesSumForDays(numberOfDays, startingDayPosition);
      }
    }
    return sum;
  }

}
