/**
 * One room class object from roomClassConfig API response.
response example:
{
    "data": {
        "roomClassConfig": {
            "roomClassConfig": [
                {
                    "code": "PP",
                    "order": 1
                }
            ]
        }
    }
}
 */
export class RoomClassConfigItem {
  [key: string]: unknown;
  code?: string;
  order?: number;

  /**
   * Room class order constructor
   * @param data object data
   * @param data.roomClassConfigItemObject roomClassOrderObject
   */
  constructor(data: { roomClassConfigItemObject?: Record<string, unknown> } = {}) {
    const roomClassConfigItemObject = data.roomClassConfigItemObject ?? {};
    this.code = roomClassConfigItemObject.code as string | undefined;
    this.order = roomClassConfigItemObject.order as number | undefined;
  }

  static fromResponse(data: { roomClassConfigItemObject?: Record<string, unknown> }): RoomClassConfigItem {
    return new RoomClassConfigItem(data);
  }
}
