import type {
  AcceptedRoomCodes,
  AcceptedRoomTypes,
  AdultsChildrenNumberKey,
  RoomTypeLabels,
  SearchRoomCodes,
  SearchRoomOccupancy,
  SearchRoomOccupancyLimitationsType,
} from '@whitbread-eos/api';

export function createOptionsAdultsChildrenDropdown(
  arr: number[],
  labelSingular: string,
  labelPlural: string
) {
  return arr.map((option: number) => {
    return option === 1
      ? { id: option, label: `${option} ${labelSingular}` }
      : { id: option, label: `${option} ${labelPlural}` };
  });
}

export function getAdultsChildrenOptions(
  roomOccupanciesObj: SearchRoomOccupancyLimitationsType,
  option: AdultsChildrenNumberKey
): number[] {
  const adultsChildrenNumberArr = roomOccupanciesObj.roomOccupancyLimitations.roomOccupancies.map(
    (occupancy) => occupancy[option]
  );

  return adultsChildrenNumberArr
    .filter((element, index, array) => array.indexOf(element) === index)
    .sort((index, nextIndex) => index - nextIndex);
}

export function getRoomTypeOptions(
  room: { adults: number; children: number },
  roomOccupancies: SearchRoomOccupancy[],
  roomTypeOccupancies: RoomTypeLabels,
  roomCodes: SearchRoomCodes
) {
  const roomOccupancy = roomOccupancies.find((occupancy) => {
    return occupancy.adultsNumber === room.adults && occupancy.childrenNumber === room.children;
  });

  const roomTypesArr = roomOccupancy?.acceptedRoomTypes || [];

  return roomTypesArr?.map((option: AcceptedRoomCodes) => {
    const roomType: string = roomTypeOccupancies[roomCodes[option] as AcceptedRoomTypes];
    const roomTypeOption = roomType.charAt(0).toUpperCase() + roomType.slice(1);
    return {
      id: roomTypeOption,
      label: roomTypeOption,
      code: option,
    };
  });
}

export function getMappedRooms(
  roomCodes: Record<string, string>,
  translations: Record<string, string>,
  isBarrierFreeLabelEnabled?: boolean
) {
  const mappedRoomLabelsArray = Object.keys(roomCodes).map((roomCode) => {
    const key =
      isBarrierFreeLabelEnabled && roomCode === 'accessible'
        ? translations['accessibleOrBarrierFree']
        : translations[roomCode];
    return {
      [`${key}`]: roomCodes[roomCode],
    };
  });

  return Object.assign({}, ...mappedRoomLabelsArray);
}
