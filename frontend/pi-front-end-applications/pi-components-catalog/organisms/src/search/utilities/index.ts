import { SearchRoomType, SearchSummaryLabels } from '@whitbread-eos/api';

export * from './searchContainerHelpers';
export * from './singleHotelSearch';

export function getTotalPeople(rooms: SearchRoomType[]) {
  return rooms.reduce(
    (acc: { adults: number; children: number }, cur: SearchRoomType) => {
      acc.adults += +cur.adults;
      acc.children += +cur.children;
      return acc;
    },
    { adults: 0, children: 0 }
  );
}

export function getPlaceholderString(
  obj: { adults: number; children: number; rooms: number },
  labels: SearchSummaryLabels
) {
  const { adult, adults, child, children, room, rooms } = labels;

  const adultsLabel = obj.adults === 1 ? adult : adults;
  let childrenLabel;

  if (obj.children === 0) {
    childrenLabel = '';
  } else if (obj.children === 1) {
    childrenLabel = child;
  } else {
    childrenLabel = children;
  }

  const roomsLabel = obj.rooms === 1 ? room : rooms;

  return obj.children === 0
    ? `${obj.adults} ${adultsLabel}, ${obj.rooms} ${roomsLabel}`
    : `${obj.adults} ${adultsLabel}, ${obj.children} ${childrenLabel}, ${obj.rooms} ${roomsLabel}`;
}

export function getRoomsPlaceholder(rooms: SearchRoomType[], labels: SearchSummaryLabels) {
  if (!rooms?.length) return;
  const totalRooms = rooms?.length;
  const totalPeople = getTotalPeople(rooms);

  const placeholderStr = getPlaceholderString({ ...totalPeople, rooms: totalRooms }, labels);

  return placeholderStr;
}
