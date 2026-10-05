import { type HIRoomType, ROOM_TYPE, ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING } from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';

import AccessibleRoomTypeOptionsComponent from './AccessibleRoomTypeOptions.component';

interface Props {
  data?: HIRoomType[];
  accessibleRoomSelections?: string[];
  roomTypeSelections?: string[];
  onRoomTypeSelection: (roomIndex: number, selection: string) => void;
  onAccessibleRoomSelection: (roomIndex: number, selection: string) => void;
  hotelInventoryResponse: any;
  selectedPMSRoomTypes: string[];
}

export default function AccessibleRoomTypeOptionsContainer({
  data,
  accessibleRoomSelections,
  roomTypeSelections,
  onRoomTypeSelection,
  onAccessibleRoomSelection,
  hotelInventoryResponse,
  selectedPMSRoomTypes,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  if (!data) return null;

  const rooms = data.map((room) => {
    const accessibleRoomTypes = room.rooms
      .map((info) =>
        info?.specialRequests.includes('WETR') || info?.specialRequests.includes('LOWB')
          ? ROOM_TYPE.STANDARD_ACCESSIBLE
          : info?.specialRequests.includes('BFRE')
            ? ROOM_TYPE.BARRIER_FREE
            : undefined
      )
      .filter(Boolean);

    const pmsRoomTypes = room.rooms.reduce(
      (acc, info) => {
        if (info?.specialRequests.includes('WETR') || info?.specialRequests.includes('LOWB'))
          acc.str.push(info?.pmsRoomType);
        if (info?.specialRequests.includes('BFRE')) acc.bfr.push(info?.pmsRoomType);
        return acc;
      },
      { str: [] as string[], bfr: [] as string[] }
    );

    const mappedRoomTypes = room.rooms.map(({ pmsRoomType }) => pmsRoomType);

    return {
      roomTypes: [
        ...new Set(
          room.rooms.map((info) =>
            ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING[info?.pmsRoomType]
              ? t(ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING[info?.pmsRoomType])
              : ''
          )
        ),
      ],
      adults: room.adults,
      children: room.children,
      accessibleRoomTypes: accessibleRoomTypes.length ? accessibleRoomTypes : undefined,
      pmsRoomTypes,
      mappedRoomTypes,
    };
  });

  return (
    <AccessibleRoomTypeOptionsComponent
      rooms={rooms}
      roomTypeSelections={roomTypeSelections}
      accessibleRoomSelections={accessibleRoomSelections}
      onRoomTypeSelection={onRoomTypeSelection}
      onAccessibleRoomSelection={onAccessibleRoomSelection}
      hotelInventoryResponse={hotelInventoryResponse}
      selectedPMSRoomTypes={selectedPMSRoomTypes}
    />
  );
}
