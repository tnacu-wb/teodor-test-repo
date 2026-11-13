import { RoomClassConfigItem } from './RoomClassConfigItem';

/**
 * One room class object from roomClassConfig API response.
 */
export class RoomClassConfig {
  [key: string]: unknown;
  roomClassConfig: RoomClassConfigItem[] = [];

  /**
   * Room class config constructor
   * @param data object data
   * @param data.roomClassConfig response from API
   */
  constructor(data: { roomClassConfig?: { roomClassConfig?: Array<Record<string, unknown>> } } = {}) {
    const roomClassConfigList = data.roomClassConfig?.roomClassConfig ?? [];
    this.roomClassConfig = roomClassConfigList.map(
      (roomClassOrderItem) => new RoomClassConfigItem({ roomClassConfigItemObject: roomClassOrderItem }),
    );
  }

  static fromResponse(data: { roomClassConfig?: { roomClassConfig?: Array<Record<string, unknown>> } }): RoomClassConfig {
    return new RoomClassConfig(data);
  }

}
