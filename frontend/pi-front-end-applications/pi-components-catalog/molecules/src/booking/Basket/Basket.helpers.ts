import type {
  HIAEMroomType,
  HIRoomType,
  HIRoomTypeInfoResponse,
  ReservationRoomType,
  UserChoice,
} from '@whitbread-eos/api';

export function getStandardRoomLabel(
  roomTypeInformationResponse: HIRoomTypeInfoResponse,
  roomCode: ReservationRoomType,
  twinRoomLabels?: string[] | undefined,
  twinroomSelection?: string | undefined
) {
  if (!roomCode) {
    return null;
  }

  // Get more granular room label for a Twin room
  if (typeof twinroomSelection !== 'undefined' && !roomCode.silentSubstitution) {
    return twinroomSelection === 'twobeds' && twinRoomLabels?.length
      ? twinRoomLabels?.[0]
      : twinRoomLabels?.[1];
  }

  if (roomCode.silentSubstitution) {
    return roomCode.roomLabelCode;
  }

  return roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes?.find(
    (roomType: HIAEMroomType) =>
      roomType.roomTypeCode === roomCode.roomLabelCode ||
      roomType.roomTypeCode.includes(roomCode.roomLabelCode)
  )?.roomLabel;
}

export const getPmsRoomTypeLabel = (
  roomTypeInformationResponse: HIRoomTypeInfoResponse,
  pmsRoomType: string
) =>
  roomTypeInformationResponse?.dataRoomTypeInformation?.roomTypeInformation?.roomTypes?.find(
    (rate) => rate.roomTypeCode.includes(pmsRoomType)
  )?.roomLabel;

export const getRoomLabel = (
  index: number,
  noRoomTypeSearch: boolean,
  roomTypeInformationResponse: HIRoomTypeInfoResponse,
  userChoice: UserChoice[],
  reservationRoomTypes: ReservationRoomType[],
  twinRoomLabels: string[],
  twinroomSelections?: string[]
) => {
  return noRoomTypeSearch
    ? `${getPmsRoomTypeLabel(roomTypeInformationResponse, userChoice[index]?.pmsRoomType)}, ${
        userChoice[index]?.roomType?.label
      }`
    : getStandardRoomLabel(
        roomTypeInformationResponse,
        reservationRoomTypes?.[index],
        twinRoomLabels,
        twinroomSelections?.[index]
      );
};

export const getTotalReservationAmount = (
  roomTypes: HIRoomType[] | undefined,
  noRoomTypeSearch: boolean,
  userChoice: UserChoice[],
  roomsIndexes: number[]
): number => {
  if (!roomTypes?.length) return 0;

  if (noRoomTypeSearch) {
    return roomTypes.reduce((sum, roomType, roomTypeIndex) => {
      const matchClassIndex = roomType.rooms.findIndex(
        (room) => room?.pmsRoomType === userChoice?.[roomTypeIndex]?.pmsRoomType
      );

      return sum + (roomType?.rooms[matchClassIndex]?.roomPriceBreakdown?.totalNetAmount ?? 0);
    }, 0);
  }

  return roomTypes.reduce((sum, roomType, roomTypeIndex) => {
    return (
      sum +
      (roomType?.rooms[roomsIndexes?.[roomTypeIndex]]?.roomPriceBreakdown?.totalNetAmount ?? 0)
    );
  }, 0);
};
