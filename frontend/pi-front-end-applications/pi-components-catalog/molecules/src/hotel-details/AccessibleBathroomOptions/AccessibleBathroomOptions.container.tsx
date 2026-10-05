import {
  type HIRoomType,
  ROOM_TYPE,
  ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING,
  loweredBathRoomTypes,
  wetBathRoomTypes,
} from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';

import AccessibleBathroomOptionsComponent from './AccessibleBathroomOptions.component';

interface Props {
  data: HIRoomType[] | undefined;
  bathroomSelections?: string[];
  roomTypeSelections?: string[];
  onRoomTypeSelection: (roomIndex: number, selection: string) => void;
  onBathroomSelection: (roomIndex: number, selection: string) => void;
  hotelInventoryResponse: any;
  selectedPMSRoomTypes: string[];
}

export default function AccessibleBathroomOptionsContainer({
  data,
  bathroomSelections,
  roomTypeSelections,
  onRoomTypeSelection,
  onBathroomSelection,
  hotelInventoryResponse,
  selectedPMSRoomTypes,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  if (!data) {
    return null;
  }

  let rooms = [];
  let bathrooms = [];

  rooms = data.map((room) => {
    bathrooms = room.rooms.map((roomInfo) => {
      if (wetBathRoomTypes.includes(roomInfo?.pmsRoomType)) {
        return ROOM_TYPE.WET;
      } else if (loweredBathRoomTypes.includes(roomInfo?.pmsRoomType)) {
        return ROOM_TYPE.LOWERED;
      }
    });

    return {
      isRoomAccessible: room?.isRoomAccessible,
      roomTypes: Array.from(
        new Set(
          room.rooms.map((roomInfo) => {
            return ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING[roomInfo?.pmsRoomType]
              ? t(ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING[roomInfo?.pmsRoomType])
              : '';
          })
        )
      ),
      adults: room.adults,
      children: room.children,
      bathroomTypes:
        bathrooms.filter((item) => item !== undefined).length > 0 ? bathrooms : undefined,
    };
  });

  return (
    <AccessibleBathroomOptionsComponent
      rooms={rooms}
      roomTypeSelections={roomTypeSelections}
      bathroomSelections={bathroomSelections}
      onRoomTypeSelection={onRoomTypeSelection}
      onBathroomSelection={onBathroomSelection}
      hotelInventoryResponse={hotelInventoryResponse}
      selectedPMSRoomTypes={selectedPMSRoomTypes}
    />
  );
}
