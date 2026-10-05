import { Box } from '@chakra-ui/react';
import type { HIRoomTypeInfoResponse, ReservationRoomType } from '@whitbread-eos/api';

import SilentSubstitutionNotification from '../SilentSubstitutionNotification/SilentSubstitutionNotification.component';

interface Props {
  brand: string;
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  currentClassRoomTypes: ReservationRoomType[];
}

export default function SilentSubstitutionNotificationWrapper({
  brand,
  roomTypeInformationResponse,
  currentClassRoomTypes,
}: Readonly<Props>) {
  const substitutedRooms = getSilentSubstitutedRooms();
  const hasNonSilentSubstitution = substitutedRooms?.length > 0;

  function getSilentSubstitutedRooms() {
    const silentSubstitutedRoom: string[] = [];
    currentClassRoomTypes?.forEach((room: ReservationRoomType) => {
      if (
        room?.silentSubstitution === false &&
        !silentSubstitutedRoom.includes(room?.roomLabelCode)
      ) {
        silentSubstitutedRoom.push(room?.roomLabelCode);
      }
    });
    return silentSubstitutedRoom;
  }

  return (
    <>
      {hasNonSilentSubstitution && (
        <Box>
          <SilentSubstitutionNotification
            brand={brand}
            substitutedRooms={substitutedRooms}
            roomTypeInformationResponse={roomTypeInformationResponse}
          />
        </Box>
      )}
    </>
  );
}
