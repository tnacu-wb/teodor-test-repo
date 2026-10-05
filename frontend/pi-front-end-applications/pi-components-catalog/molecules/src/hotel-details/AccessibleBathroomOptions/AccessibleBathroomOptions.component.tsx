import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, Heading, Text } from '@chakra-ui/react';
import {
  HIHotelInventoryResponse,
  ROOM_TYPE,
  ROOM_TYPE_TO_BATHROOM_MAPPING,
} from '@whitbread-eos/api';
import {
  Accessible24,
  Alert,
  Dropdown,
  Icon,
  Info,
  Notification,
  RadioButton,
} from '@whitbread-eos/atoms';
import { renderSanitizedHtml, useCustomLocale } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useEffect, useMemo } from 'react';
import { v4 as uuidv4 } from 'uuid';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

interface Props {
  rooms: Room[];
  onRoomTypeSelection: (roomIndex: number, selection: string) => void;
  onBathroomSelection: (roomIndex: number, selection: string) => void;
  bathroomSelections?: string[];
  roomTypeSelections?: string[];
  hotelInventoryResponse: HIHotelInventoryResponse;
  selectedPMSRoomTypes: string[];
}

type AvailableCountPerPMSRoomType = Record<string, number>;
interface Room {
  roomTypes: string[];
  adults: number;
  children: number;
  bathroomTypes: (string | undefined)[] | undefined;
  isRoomAccessible?: boolean;
}

export default function AccessibleBathroomOptionsComponent({
  rooms,
  onRoomTypeSelection,
  onBathroomSelection,
  bathroomSelections,
  roomTypeSelections,
  hotelInventoryResponse,
  selectedPMSRoomTypes,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();

  const availableBathroomsPerRoom = useMemo(getAvailableBathroomsPerRoom, [
    rooms,
    hotelInventoryResponse,
    selectedPMSRoomTypes,
  ]);

  useEffect(() => {
    rooms.forEach((room, roomIndex) => {
      const selectionRequired = room?.isRoomAccessible;
      const bathroomSelected =
        bathroomSelections![roomIndex] === ROOM_TYPE.LOWERED ||
        bathroomSelections![roomIndex] === ROOM_TYPE.WET;

      if (selectionRequired) {
        const hasLoweredBathroom = room.bathroomTypes!.includes(ROOM_TYPE.LOWERED);
        const hasWetRoom = room.bathroomTypes!.includes(ROOM_TYPE.WET);

        if (hasLoweredBathroom && !bathroomSelected) {
          onBathroomSelection(roomIndex, ROOM_TYPE.LOWERED);
        } else if (hasWetRoom && !bathroomSelected) {
          onBathroomSelection(roomIndex, ROOM_TYPE.WET);
        }

        onRoomTypeSelection(roomIndex, room.roomTypes[0]);
      }
    });
  }, [rooms, onBathroomSelection, onRoomTypeSelection, bathroomSelections]);

  return (
    <>
      <Heading as="h1" data-testid="accessible-title" {...headingStyles}>
        {t('accessible.chooseBathRoomTitle')}
      </Heading>

      {rooms.map((room, roomIndex) => {
        const selectionRequired = room?.isRoomAccessible;

        return (
          <Box key={uuidv4()} mt={{ base: 'xl', xs: '3xl' }}>
            <Flex {...roomAndGuestsFlexStyles}>
              <Flex direction="column" mr="md">
                <Heading as="h3" data-testid="accessible-room-number" {...roomHeadingStyles}>
                  {t('booking.hotel.summary.room').replace(
                    '[roomNumber]',
                    selectionRequired
                      ? (roomIndex + 1).toString()
                      : `${roomIndex + 1} ${room.roomTypes[0]}`
                  )}
                </Heading>
                {renderGuestNumbers(room)}
              </Flex>
              {selectionRequired && renderRoomTypeSelection(room.roomTypes, roomIndex)}
            </Flex>

            {selectionRequired ? (
              renderSelectionOptions(roomIndex)
            ) : (
              <Text mt={{ base: '1', xs: '0' }} data-testid="accessible-no-selection-required">
                {t('accessible.no.selection.required')}
              </Text>
            )}
          </Box>
        );
      })}
    </>
  );

  function renderGuestNumbers(room: Room) {
    const adultsLabel = `${room.adults} ${
      room.adults > 1 ? t('dashboard.bookings.adults') : t('dashboard.bookings.adult')
    }`;
    const childrenLabel = `, ${room.children} ${
      room.children > 1 ? t('dashboard.bookings.children') : t('dashboard.bookings.child')
    }`;

    return (
      <Text mt="1" lineHeight="3" data-testid="accessible-room-guest-numbers">
        {adultsLabel}
        {room.children ? childrenLabel : ''}
      </Text>
    );
  }

  function renderRoomTypeSelection(roomTypes: string[], roomIndex: number) {
    let unavailableRoomType = '';
    const roomTypeSeelcted = t('accessible.twin');

    if (roomTypes.length === 1) {
      unavailableRoomType = roomTypes?.includes(roomTypeSeelcted)
        ? 'accessible.double.unavailable'
        : 'accessible.twin.unavailable';
    }
    const unavailableBoxStyle = !unavailableRoomType
      ? 'initial'
      : language === 'en'
        ? '3xl'
        : '5xl';

    const roomTypeSelectionBoxStyles = {
      mb: unavailableBoxStyle,
      mt: { base: '4', xs: '0' },
      flexGrow: 1,
    };

    return (
      <Tooltip
        isOpen={!!unavailableRoomType}
        description={t(unavailableRoomType)}
        variant="accessible"
        svg={<Info />}
        modifiers={[
          {
            name: 'flip',
            enabled: false,
          },
        ]}
      >
        <Box {...roomTypeSelectionBoxStyles}>
          <Dropdown
            onChange={(option) => {
              onRoomTypeSelection(roomIndex, option!.label as string);
            }}
            options={roomTypes.map((room, index) => ({
              id: index,
              label: room,
              icon: <Icon svg={<Accessible24 />} />,
            }))}
            selectedId={roomTypes.indexOf(roomTypeSelections![roomIndex]).toString()}
            disabled={roomTypes.length <= 1}
          />
        </Box>
      </Tooltip>
    );
  }

  function renderSelectionOptions(roomIndex: number) {
    const { hasLoweredBathroom, hasWetRoom } = availableBathroomsPerRoom[roomIndex];

    return (
      <Box maxW="3xl" mt="lg" data-testid="accessible-bathroom-dropdown">
        <RadioButton
          type="low-bath"
          value="low-bath"
          onChange={() => onBathroomSelection(roomIndex, ROOM_TYPE.LOWERED)}
          isChecked={bathroomSelections![roomIndex] === ROOM_TYPE.LOWERED}
          isDisabled={!hasLoweredBathroom}
        >
          <>
            <Text fontWeight="bold" data-testid="accessible-low-bathroom">
              {t('accessible.lowBath')}
            </Text>
            <Box className="formatLinks">
              {renderSanitizedHtml(t('accessible.loweredBath.text'))}
            </Box>
          </>
        </RadioButton>

        <Box mt="1em" />

        <RadioButton
          type="wet-room"
          value="wet-room"
          onChange={() => onBathroomSelection(roomIndex, ROOM_TYPE.WET)}
          isChecked={bathroomSelections![roomIndex] === ROOM_TYPE.WET}
          isDisabled={!hasWetRoom}
        >
          <>
            <Text data-testid="accessible-wet-room">{t('accessible.wetRoom')}</Text>
            <Box className="formatLinks">{renderSanitizedHtml(t('accessible.wetRoom.text'))}</Box>
          </>
        </RadioButton>

        <Box mt="md">
          <Notification
            title={t('accessible.wetRoom')}
            description={t('accessible.wetRoom.alertText')}
            prefixDataTestId="accessible-wetRoom"
            status="warning"
            variant="alert"
            svg={<Alert />}
            isClosed={hasWetRoom}
            style={{ maxWidth: 'none' }}
            isInnerHTML
          />
          <Notification
            title={t('accessible.lowBath')}
            description={t('accessible.loweredBath.alertText')}
            prefixDataTestId="accessible-lowBath"
            status="warning"
            variant="alert"
            svg={<Alert />}
            isClosed={hasLoweredBathroom}
            style={{ maxWidth: 'none' }}
            isInnerHTML
          />
        </Box>
      </Box>
    );
  }

  function getAvailableCountPerPMSRoomType() {
    let availableCountPerPMSRoomType: AvailableCountPerPMSRoomType = {
      WETDBL: 0,
      LOWDBL: 0,
      WETTWN: 0,
      LOWTWN: 0,
    };

    if (
      !hotelInventoryResponse.isLoadingHotelInventory &&
      !hotelInventoryResponse.isErrorHotelInventory &&
      hotelInventoryResponse.dataHotelInventory
    ) {
      availableCountPerPMSRoomType =
        hotelInventoryResponse.dataHotelInventory?.hotelInventory?.roomTypeInventories?.reduce(
          (current: any, roomTypeInventory) => {
            if (!Number.isInteger(current[roomTypeInventory?.code])) return current;

            return { ...current, [roomTypeInventory?.code]: roomTypeInventory?.availableCount };
          },
          availableCountPerPMSRoomType
        );
    }

    return availableCountPerPMSRoomType;
  }

  function getAvailableBathroomsPerRoom() {
    const roomCountPerPMSRoomType = selectedPMSRoomTypes.reduce(
      (current: any, pmsRoomType) => {
        if (!Number.isInteger(current[pmsRoomType])) return current;

        return { ...current, [pmsRoomType]: current[pmsRoomType] + 1 };
      },

      { WETDBL: 0, LOWDBL: 0, WETTWN: 0, LOWTWN: 0 }
    );

    const availableCountPerPMSRoomType = getAvailableCountPerPMSRoomType();

    return rooms.map((room, roomIndex) => {
      const response = { hasLoweredBathroom: false, hasWetRoom: false };

      const selectionRequired = room?.isRoomAccessible;

      if (!selectionRequired) return response;

      const selectedRoomType = selectedPMSRoomTypes[roomIndex];

      const mapping = ROOM_TYPE_TO_BATHROOM_MAPPING[selectedRoomType];

      if (!mapping) return response;

      return {
        hasLoweredBathroom:
          room.bathroomTypes!.includes(ROOM_TYPE.LOWERED) &&
          availableCountPerPMSRoomType[mapping.loweredKey] -
            (selectedRoomType === ROOM_TYPE.LOWERED_TWIN || selectedRoomType === ROOM_TYPE.WET_TWIN
              ? roomCountPerPMSRoomType[mapping.loweredKey]
              : 0) >
            0,

        hasWetRoom:
          room.bathroomTypes!.includes(ROOM_TYPE.WET) &&
          availableCountPerPMSRoomType[mapping.wetKey] -
            (selectedRoomType === ROOM_TYPE.WET_DOUBLE || selectedRoomType === ROOM_TYPE.WET_TWIN
              ? 0
              : roomCountPerPMSRoomType[mapping.wetKey]) >
            0,
      };
    });
  }
}

const headingStyles = {
  fontWeight: 'semibold',
  fontSize: { base: '3xl', sm: '3xxl' },
  lineHeight: { base: '4', sm: '5' },
};

const roomAndGuestsFlexStyles = {
  mb: 'sm',
  flexDirection: { base: 'column', xs: 'row' },
  w: { base: '100%', sm: '64%', md: '50%' },
} as FlexProps;

const roomHeadingStyles = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
};
