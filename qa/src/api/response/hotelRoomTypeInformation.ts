import { HotelBrands } from '../../test-data/hotelBrands';
import { Strings } from '../../test-data/strings';
import { RoomTypeInfo } from './roomTypeInfo';

/**
 * Response example:
 * {
  "data": {
    "roomTypeInformation": {
      "roomTypes": [
        {
          "roomTypeCode": "FMQUAD",
          "roomCategory": "Family",
          "roomLabel": "Family Room",
          "roomDescription": "[FMQUAD] Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.",
          "roomImage": "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg"
        }
      ]
    }
  }
}
 */
export class HotelRoomTypeInformation {
  [key: string]: unknown;
  brand?: string;
  roomTypes: RoomTypeInfo[] = [];

  /**
   * HotelRoomTypeInformation constructor
   * @param data object data
   * @param data.roomTypeInformation roomTypeInformation
   */
  constructor(data: { roomTypeInformation?: { roomTypes?: Array<Record<string, unknown>> } } = {}) {
    const roomTypes = data.roomTypeInformation?.roomTypes ?? [];
    this.roomTypes = roomTypes.map((roomType) => new RoomTypeInfo({ roomTypeInfo: roomType }));
  }

  static fromResponse(data: { roomTypeInformation?: { roomTypes?: Array<Record<string, unknown>> } }): HotelRoomTypeInformation {
    return new HotelRoomTypeInformation(data);
  }

  /**
   * Check if room type is premier plus
   * @param code code
   * @returns true if the room type with the given code is premium, false otherwise
   */
  async checkRoomTypeIsPremiumWithCode(code: string): Promise<boolean> {
    const filteredRoomType = this.roomTypes.find((item) => item.roomTypeCode?.includes(code));

    if (filteredRoomType === undefined) {
      throw new Error(`${code} code wasn't found in response.`);
    }

    // TODO: workaround for label from basket container: https://whitbreadis.atlassian.net/browse/DNRQ-52134. Update next line after they are fixed.
    const expectedLabel = this.brand === HotelBrands.HUB.name ? await Strings.BIGGER_ROOM.name : await Strings.PREMIER_PLUS_ROOM_BASKET.name;
    return filteredRoomType.roomLabel === expectedLabel;
  }

}
