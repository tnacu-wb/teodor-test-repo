/**
 * roomRequirements: {
       type: 'FAM',
       lettingType: null,
       adults: 2,
       children: 2,
       cotRequired: true,
       hotelBrand: null
     }
 */
export class RoomRequirements {
  [key: string]: unknown;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): RoomRequirements {
    return new RoomRequirements(data);
  }
}
