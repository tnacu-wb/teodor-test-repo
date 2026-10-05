import {
  type HIRoomType,
  ROOM_TYPE,
  HITwinRoomPrice,
  twinRoomImprovedSpecialRequests,
  twinRoomStandardSpecialRequests,
} from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';

import TwinroomOptionsComponent from './TwinroomOptions.component';

interface Props {
  data: HIRoomType[] | undefined;
  twinroomSelections: string[];
  twinRoomPrices: HITwinRoomPrice[] | undefined;
  onTwinroomSelection: (roomIndex: number, selection: string) => void;
}

interface Room {
  roomType: string;
  adults: number;
  children: number;
  twinroomTypes: (string | undefined)[] | undefined;
}

export default function TwinroomOptionsContainer({
  data,
  twinroomSelections,
  onTwinroomSelection,
  twinRoomPrices,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  if (!data) {
    return null;
  }

  let rooms: Room[] = [];

  // for twin roomType set twin option value based on special request type TW2S/TWDS
  rooms = data.map((room) => {
    let twinrooms: string[] | [] = [];
    if (room?.roomType === ROOM_TYPE.TWIN) {
      twinrooms = room.rooms.map((roomInfo) => {
        const specialRequests: string | undefined = roomInfo.specialRequests.toString();
        if (twinRoomStandardSpecialRequests.includes(specialRequests)) {
          return ROOM_TYPE.TWIN_DOUBLE_SOFA;
        } else if (twinRoomImprovedSpecialRequests.includes(specialRequests)) {
          return ROOM_TYPE.TWIN_TWO_BEDS;
        } else {
          return '';
        }
      });
    }

    return {
      roomType: getRoomTypeLabel(room?.roomType),
      adults: room.adults,
      children: room.children,
      twinroomTypes:
        twinrooms.filter((item) => item !== undefined).length > 0 ? twinrooms : undefined,
    };
  });

  return (
    <TwinroomOptionsComponent
      rooms={rooms}
      twinroomSelections={twinroomSelections}
      onTwinroomSelection={onTwinroomSelection}
      twinRoomPrices={twinRoomPrices}
    />
  );

  // Set the roomType label for each room block, DIS roomType will not enter twin room flow
  function getRoomTypeLabel(roomType: string | undefined) {
    switch (roomType) {
      case 'SB':
        return t('dashboard.bookings.singleRoom');

      case 'DB':
        return t('dashboard.bookings.doubleRoom');

      case 'FAM':
        return t('pihotelinfo.familyTitle');

      case 'TWIN':
        return t('pihotelinfo.chooseTwinRoom.title');

      default:
        return '';
    }
  }
}
