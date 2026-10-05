import { RoomTypeInventory } from './roomTypeInventory';

/**
Example:
{
  "data": {
    "hotelInventory": {
      "roomTypeInventories": [
        {
          "availableCount": 56,
          "code": "DOUBLE"
        },
        {
          "availableCount": 7,
          "code": "FMQUAD"
        },
        {
          "availableCount": 4,
          "code": "WETDBL"
        },
        {
          "availableCount": 5,
          "code": "WETTWN"
        }
      ]
    }
  }
}
 */
export class HotelInventory {
  [key: string]: unknown;
  roomTypeInventories: RoomTypeInventory[] = [];

  /**
   * HotelInventory constructor
   * @param data object data
   * @param data.hotelInventory hotelInventory
   */
  constructor(data: { hotelInventory?: { roomTypeInventories?: Array<Record<string, unknown>> } } = {}) {
    const roomTypeInventories = data.hotelInventory?.roomTypeInventories ?? [];
    this.roomTypeInventories = roomTypeInventories.map(
      (roomTypeInventoryItem) => new RoomTypeInventory({ roomTypeInventory: roomTypeInventoryItem }),
    );
  }

  static fromResponse(data: { hotelInventory?: { roomTypeInventories?: Array<Record<string, unknown>> } }): HotelInventory {
    return new HotelInventory(data);
  }

}
