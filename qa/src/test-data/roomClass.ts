import { type StringBase } from './stringBase';
import { Strings } from './strings';

/** Room class for hotel rooms. */
export interface RoomClass {
  id: string;
  name: StringBase;
}

/** The room classes for hotel rooms. */
export class RoomClasses {
  private constructor() {}

  static readonly STANDARD_ROOM: RoomClass = { name: Strings.STANDARD_ROOM, id: 'ST' };
  static readonly STANDARD_WITH_VIEW: RoomClass = { name: Strings.STANDARD_WITH_VIEW, id: 'SV' };
  static readonly STANDARD_ROOM_WITH_VIEW: RoomClass = { name: Strings.STANDARD_ROOM_WITH_VIEW, id: 'SV' };
  static readonly STANDARD_WITH_A_CITY_VIEW: RoomClass = { name: Strings.STANDARD_WITH_A_CITY_VIEW, id: 'SC' };
  static readonly STANDARD_EXTRA_ROOM: RoomClass = { name: Strings.STANDARD_EXTRA_ROOM, id: 'SE' };
  static readonly PREMIER_PLUS_ROOM: RoomClass = { name: Strings.PREMIER_PLUS_ROOM, id: 'PP' };
  static readonly PREMIER_PLUS_ROOM_WITH_VIEW: RoomClass = { name: Strings.PREMIER_PLUS_ROOM_WITH_VIEW, id: 'PV' };
  static readonly PREMIER_PLUS_ROOM_WITH_A_CITY_VIEW: RoomClass = { name: Strings.PREMIER_PLUS_ROOM_WITH_A_CITY_VIEW, id: 'PC' };
  static readonly BIGGER_ROOM: RoomClass = { name: Strings.BIGGER_ROOM, id: 'BG' };
  static readonly STANDARD_WINDOWLESS: RoomClass = { name: Strings.STANDARD_ROOM_NO_WINDOW, id: 'SW' };
  static readonly BIGGER_WINDOWLESS: RoomClass = { name: Strings.BIGGER_ROOM_NO_WINDOW, id: 'BW' };

  /**
   * Get room class based on name.
   * @param roomClassName Class of room as a string.
   * @returns Matching room class.
   */
  static async getRoomClassByName(roomClassName: string): Promise<RoomClass> {
    let found: RoomClass | null = null;
    for (const roomClass of Object.values(RoomClasses)) {
      if (typeof roomClass === 'object' && 'name' in roomClass && await roomClass.name.name === roomClassName) {
        found = roomClass;
        break;
      }
    }
    if (!found) {
      throw new Error(`${roomClassName} room class name is not found in the list!`);
    }
    return found;
  }
}
