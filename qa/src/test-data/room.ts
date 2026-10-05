import { RoomTypesData, type RoomType } from './roomTypes';

/**
 * Search room information used by hotel availability calls.
 *
 * Room type codes:
 * CODE | Definition
 * DB   | Double
 * FAM  | Family
 * DIS  | Accessible
 * SB   | Single
 * TWIN | Twin
 */
export interface Room {
  adultsNumber: number;
  childrenNumber: number;
  roomType: RoomType;
  cotRequired: boolean;
  roomNumber?: number;
}

/** Predefined room occupancy combinations used by search and availability tests. */
export class Rooms {
  private constructor() {}

  static get SINGLE_1_ADULT_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 1, childrenNumber: 0, roomType: RoomTypesData.SINGLE }); }
  static get DOUBLE_1_ADULT_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 1, childrenNumber: 0, roomType: RoomTypesData.DOUBLE }); }
  static get DOUBLE_2_ADULTS_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 2, childrenNumber: 0, roomType: RoomTypesData.DOUBLE }); }
  static get FAMILY_1_ADULT_1_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 1, childrenNumber: 1, roomType: RoomTypesData.FAMILY }); }
  static get FAMILY_1_ADULT_2_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 1, childrenNumber: 2, roomType: RoomTypesData.FAMILY }); }
  static get FAMILY_2_ADULTS_1_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 2, childrenNumber: 1, roomType: RoomTypesData.FAMILY }); }
  static get FAMILY_2_ADULTS_2_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 2, childrenNumber: 2, roomType: RoomTypesData.FAMILY }); }
  static get DIS_1_ADULT_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 1, childrenNumber: 0, roomType: RoomTypesData.ACCESSIBLE }); }
  static get DIS_2_ADULTS_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 2, childrenNumber: 0, roomType: RoomTypesData.ACCESSIBLE }); }
  static get SB_1_ADULT_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 1, childrenNumber: 0, roomType: RoomTypesData.SINGLE }); }
  static get TWIN_2_ADULTS_0_CHILDREN(): Room { return Rooms.createRoom({ adultsNumber: 2, childrenNumber: 0, roomType: RoomTypesData.TWIN }); }

  /**
   * Create a room object.
   * @param data.adultsNumber Number of adults.
   * @param data.childrenNumber Number of children.
   * @param data.roomType Room type.
   * @param data.cotRequired Whether a cot is required.
   */
  static createRoom(data: Partial<Room> = {}): Room {
    return {
      adultsNumber: data.adultsNumber ?? 1,
      childrenNumber: data.childrenNumber ?? 0,
      roomType: data.roomType ?? RoomTypesData.DOUBLE,
      cotRequired: data.cotRequired ?? false,
      roomNumber: data.roomNumber,
    };
  }

  /**
   * Method used to update room information.
   * @param room Existing room object.
   * @param updates Room fields to override, including adults, children, cot, type, or roomNumber.
   */
  static updateRoomInformation(room: Room, updates: Partial<Room>): Room {
    Object.assign(room, updates);
    return room;
  }
}
