/**
 * Response example:
 * {
          "roomTypeCode": "FMQUAD",
          "roomCategory": "Family",
          "roomLabel": "Family Room",
          "roomDescription": "[FMQUAD] Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.",
          "roomImage": "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg"
        }
 */
export class RoomTypeInfo {
  [key: string]: unknown;
  groupId?: string;
  roomCategory?: string;
  roomDescription?: string;
  roomImage?: string;
  roomLabel?: string;
  roomTypeCode?: string;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): RoomTypeInfo {
    return new RoomTypeInfo(data);
  }
}
