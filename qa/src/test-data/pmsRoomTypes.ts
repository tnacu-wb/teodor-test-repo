import { HotelBrands } from './hotelBrands';
import type { StringBase } from './stringBase';
import { Strings } from './strings';

/** PMS room type for hotel rooms. */
export interface PmsRoomType {
  name: StringBase;
  /** PMS room IDs corresponding to the room type. */
  ids: string[];
  /** Hotel brand/type for which the PMS room IDs apply. */
  type: string;
}

/** The PMS room types for hotel rooms. */
export class PmsRoomTypes {
  private constructor() {}

  static readonly HUB_BIGGER_ROOM: PmsRoomType = { name: Strings.BIGGER_ROOM, ids: ['BIGWIN'], type: HotelBrands.HUB.name };
  static readonly HUB_STANDARD_ROOM: PmsRoomType = { name: Strings.STANDARD_ROOM, ids: ['DBLWIN', 'ACCWIN'], type: HotelBrands.HUB.name };
  static readonly HUB_STANDARD_WINDOWLESS: PmsRoomType = { name: Strings.STANDARD_ROOM_NO_WINDOW, ids: ['DBLNWD', 'ACCNWD'], type: HotelBrands.HUB.name };
  static readonly HUB_BIGGER_WINDOWLESS: PmsRoomType = { name: Strings.BIGGER_ROOM_NO_WINDOW, ids: ['BIGNWD'], type: HotelBrands.HUB.name };
  static readonly PI_STANDARD_ROOM: PmsRoomType = {
    name: Strings.STANDARD_ROOM,
    ids: ['LOWTWN', 'WETTWN', 'TWINRM', 'FMFOUR', 'ZPLDBL', 'WETDBL', 'LOWDBL', 'FMTRPL', 'FMTHRE', 'FMQUAD', 'DOUBLE', 'DBLDBL', 'SINGLE'],
    type: HotelBrands.PI.name,
  };
  static readonly PI_STANDARD_WITH_VIEW_ROOM: PmsRoomType = { name: Strings.STANDARD_WITH_VIEW, ids: ['VFMFOR', 'VFMTHR'], type: HotelBrands.PI.name };
  static readonly PI_STANDARD_ROOM_WITH_VIEW_ROOM: PmsRoomType = { name: Strings.STANDARD_ROOM_WITH_VIEW, ids: ['VDOUBL'], type: HotelBrands.PI.name };
  static readonly PI_PREMIER_PLUS_ROOM: PmsRoomType = { name: Strings.PREMIER_PLUS_ROOM, ids: ['PPLDBL', 'PPDLOW', 'PPDWET'], type: HotelBrands.PI.name };
  static readonly PI_PREMIER_PLUS_WITH_VIEW_ROOM: PmsRoomType = {
    name: Strings.PREMIER_PLUS_ROOM_WITH_VIEW,
    ids: ['VPPDBL', 'VPPDLOW', 'VPPDWET'],
    type: HotelBrands.PI.name,
  };
  static readonly PID_STANDARD_ROOM: PmsRoomType = {
    name: Strings.STANDARD_ROOM,
    ids: ['LOWTWN', 'WETTWN', 'TWINRM', 'FMFOUR', 'ZPLDBL', 'WETDBL', 'LOWDBL', 'FMTRPL', 'FMQUAD', 'DOUBLE', 'FMTHRE', 'DBLDBL', 'SINGLE'],
    type: HotelBrands.PID.name,
  };

  /**
   * Get PMS room IDs based on room type name and hotel type.
   * @param params.roomTypeName Room type display name.
   * @param params.hotelType Hotel brand/type.
   * @returns PMS room IDs for the matching room type.
   */
  static async getPmsRoomIdsByNameAndType(params: { roomTypeName: string; hotelType: string }): Promise<string[]> {
    const roomTypes = Object.values(PmsRoomTypes).filter((roomType): roomType is PmsRoomType => typeof roomType === 'object');
    const resolvedRoomTypes = await Promise.all(
      roomTypes.map(async (roomType) => ({
        roomType,
        name: await roomType.name.name,
      })),
    );
    const found = resolvedRoomTypes.find(({ roomType, name }) => name.toUpperCase() === params.roomTypeName.toUpperCase() && roomType.type === params.hotelType)?.roomType;
    if (!found) {
      throw new Error(`${params.roomTypeName} room type name is not found in the list!`);
    }
    return found.ids;
  }
}
