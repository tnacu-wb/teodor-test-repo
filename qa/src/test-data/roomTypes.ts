import { type StringBase } from './stringBase';
import { Strings } from './strings';
import { IbStrings } from './pib/ibStrings';

/** Room type for hotel rooms. */
export interface RoomType {
  key: string;
  id: string;
  name: StringBase;
}

/** The room types for hotel rooms. */
export class RoomTypesData {
  private constructor() {}

  private static get isInnBusiness(): boolean {
    const options = (global.browser?.options ?? {}) as Record<string, unknown>;
    return String(options.app ?? '') === 'pib';
  }

  static get SINGLE(): RoomType { return { key: 'SINGLE', id: 'SB', name: RoomTypesData.isInnBusiness ? IbStrings.SINGLE_ROOM_IB : Strings.SINGLE_GENERIC }; }
  static get DOUBLE(): RoomType { return { key: 'DOUBLE', id: 'DB', name: RoomTypesData.isInnBusiness ? IbStrings.DOUBLE_IB : Strings.DOUBLE_GENERIC }; }
  static get ACCESSIBLE(): RoomType { return { key: 'ACCESSIBLE', id: 'DIS', name: RoomTypesData.isInnBusiness ? IbStrings.ACCESSIBLE_ROOM_IB : Strings.ACCESSIBLE_GENERIC }; }
  static get FAMILY(): RoomType { return { key: 'FAMILY', id: 'FAM', name: RoomTypesData.isInnBusiness ? IbStrings.FAMILY_IB : Strings.FAMILY_GENERIC }; }
  static get TWIN(): RoomType { return { key: 'TWIN', id: 'TWIN', name: RoomTypesData.isInnBusiness ? IbStrings.TWIN_ROOM_IB : Strings.TWIN_GENERIC }; }

  /**
   * Get room type based on name.
   * @param roomTypeName Type of room as a string.
   * @returns Matching room type.
   */
  static async getRoomTypeByName(roomTypeName: string): Promise<RoomType> {
    let found: RoomType | null = null;
    for (const roomType of RoomTypesData.allRoomTypes()) {
      if ((await roomType.name.name).toUpperCase() === roomTypeName.toUpperCase()) {
        found = roomType;
        break;
      }
    }
    if (!found) {
      throw new Error(`${roomTypeName} room type name is not found in the list!`);
    }
    return found;
  }

  /**
   * Get room type based on id.
   * @param id Room type id.
   * @returns Matching room type.
   */
  static async getRoomTypeById(id: string): Promise<RoomType> {
    const found = RoomTypesData.allRoomTypes().find((roomType) => roomType.id === id);
    if (!found) {
      throw new Error(`${id} room type id is not found in the list!`);
    }
    return found;
  }

  private static allRoomTypes(): RoomType[] {
    return [RoomTypesData.SINGLE, RoomTypesData.DOUBLE, RoomTypesData.ACCESSIBLE, RoomTypesData.FAMILY, RoomTypesData.TWIN];
  }
}
