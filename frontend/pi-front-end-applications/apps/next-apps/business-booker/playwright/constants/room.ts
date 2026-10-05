/* eslint-disable prettier/prettier */
import { Constants } from '@WB-playwright/constants';
import { Room } from '@WB-playwright/types';

export const RoomComposition: Record<string, Room> = {
  SINGLE_1_ADULT_0_CHILDREN: { adultsNumber: 1, childrenNumber: 0, roomType: Constants.SINGLE, cotRequired: false, },
  DOUBLE_1_ADULT_0_CHILDREN: { adultsNumber: 1, childrenNumber: 0, roomType: Constants.DOUBLE, cotRequired: false, },
  DOUBLE_2_ADULTS_0_CHILDREN: { adultsNumber: 2, childrenNumber: 0, roomType: Constants.DOUBLE, cotRequired: false, },
  FAMILY_1_ADULT_1_CHILDREN: { adultsNumber: 1, childrenNumber: 1, roomType: Constants.FAMILY, cotRequired: false, },
  FAMILY_1_ADULT_2_CHILDREN: { adultsNumber: 1, childrenNumber: 2, roomType: Constants.FAMILY, cotRequired: false, },
  FAMILY_2_ADULTS_1_CHILDREN: { adultsNumber: 2, childrenNumber: 1, roomType: Constants.FAMILY, cotRequired: false, },
  FAMILY_2_ADULTS_2_CHILDREN: { adultsNumber: 2, childrenNumber: 2, roomType: Constants.FAMILY, cotRequired: false, },
  DIS_1_ADULT_0_CHILDREN: { adultsNumber: 1, childrenNumber: 0, roomType: Constants.ACCESSIBLE, cotRequired: false, },
  DIS_2_ADULTS_0_CHILDREN: { adultsNumber: 2, childrenNumber: 0,  roomType: Constants.ACCESSIBLE, cotRequired: false, },
  SB_1_ADULT_0_CHILDREN: { adultsNumber: 1, childrenNumber: 0, roomType: Constants.SINGLE, cotRequired: false, },
  TWIN_2_ADULTS_0_CHILDREN: { adultsNumber: 2, childrenNumber: 0, roomType: Constants.TWIN, cotRequired: false, },
} as const;

export const createRoom = ({
  adultsNumber = 2,
  childrenNumber = 0,
  roomType = Constants.DOUBLE,
  cotRequired = false,
}: Partial<Room> = {}): Room => {
  return { adultsNumber, childrenNumber, roomType, cotRequired };
};

export const updateRoomInformation = (
  room: Room[],
  updates: Partial<Room>
): Room[] => {
  return { ...room, ...updates, };
};
