import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, Heading, Text } from '@chakra-ui/react';
import { HIHotelInventoryResponse, ROOM_TYPE } from '@whitbread-eos/api';
import { Alert, Notification, RadioButton } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState, useRef } from 'react';
import type { Dispatch, SetStateAction } from 'react';

interface Props {
  rooms: Room[];
  onRoomTypeSelection: (roomIndex: number, selection: string) => void;
  onAccessibleRoomSelection: (roomIndex: number, selection: string) => void;
  accessibleRoomSelections?: string[];
  roomTypeSelections?: string[];
  hotelInventoryResponse: HIHotelInventoryResponse;
  selectedPMSRoomTypes: string[];
}
interface HandleRenderWithReSelectionsPropsType {
  rooms: Room[];
  accessibleRoomSelections: string[];
  availableAccessibleRoomsPerRoom: {
    hasAccessibleRoom: boolean;
    hasBarrierFreeRoom: boolean;
  }[];
  onAccessibleRoomSelection: (roomIndex: number, selection: string) => void;
  onRoomTypeSelection: (roomIndex: number, selection: string) => void;
  hotelInventoryResponse: HIHotelInventoryResponse; // type this properly
  currentRoomIndex: number;
  setAvailableAccessibleRoomsPerRoom: Dispatch<
    SetStateAction<ReturnType<typeof getAvailableAccessibleroomsPerRoom>>
  >;
  initialRoomTypeCounts: {
    barrierFreeCount: number;
    standardAccesssibleCount: number;
  };
}

export const accessiblePrefixes = ['Accessible', 'Barrierefreies', 'Rollstuhlgerechtes'];
export interface Room {
  roomTypes: string[];
  adults: number;
  children: number;
  accessibleRoomTypes: (string | undefined)[] | undefined;
  pmsRoomTypes: { str: string[]; bfr: string[] };
  mappedRoomTypes?: string[];
}

export type BedRoomType = {
  barrierFreeCount: number;
  standardAccesssibleCount: number;
};

/**
 * Checks if selection is required for a room.
 */
export function isSelectionRequired(room: Room, accessiblePrefixes: string[]): boolean {
  return room?.roomTypes?.some((roomType: string) =>
    accessiblePrefixes?.some((prefix: string) => roomType?.startsWith(prefix))
  );
}

/**
 * Calculates total available inventory of accessible rooms.
 */

export function getAccessibleRoomCounts(hotelInventoryResponse: HIHotelInventoryResponse) {
  const roomTypeInventories =
    hotelInventoryResponse?.dataHotelInventory?.hotelInventory?.roomTypeInventories ?? [];

  let bfrCountInventoryTotal = 0;
  let strCountInventoryTotal = 0;

  roomTypeInventories.forEach((room: { code: string; availableCount: number }) => {
    switch (room.code) {
      case ROOM_TYPE.BRFDBL:
      case ROOM_TYPE.BRFZPL:
      case ROOM_TYPE.BRFTWN:
        bfrCountInventoryTotal += room.availableCount;
        break;
      case ROOM_TYPE.WET_DOUBLE:
      case ROOM_TYPE.LOWERED_DOUBLE:
        strCountInventoryTotal += room.availableCount;
        break;
      default:
        break;
    }
  });

  return {
    bfrCountInventoryTotal,
    strCountInventoryTotal,
  };
}

/**
 * Counts barrier-free and standard accessible room selections.
 */
export function countBarrierFreeAndStdAccessible(
  bedRoomTypesData: string[],
  initialRoomTypeCounts: BedRoomType
): BedRoomType {
  return bedRoomTypesData?.reduce(
    (counts, item) => {
      switch (item) {
        case ROOM_TYPE.BARRIER_FREE:
          counts.barrierFreeCount++;
          break;
        case ROOM_TYPE.STANDARD_ACCESSIBLE:
          counts.standardAccesssibleCount++;
          break;
        default:
          break;
      }
      return counts;
    },
    { ...initialRoomTypeCounts }
  );
}

export function updateRoomsSelections(updatedSelections: any, updatedRooms: any) {
  updatedSelections.forEach((selection: string, roomIndex: number) => {
    const room = updatedRooms[roomIndex];
    if (!room.accessibleRoomTypes) {
      room.accessibleRoomTypes = [];
    }
    if (
      selection === ROOM_TYPE.STANDARD_ACCESSIBLE &&
      !room.accessibleRoomTypes.includes(ROOM_TYPE.STANDARD_ACCESSIBLE)
    ) {
      room.accessibleRoomTypes = [...room.accessibleRoomTypes, ROOM_TYPE.STANDARD_ACCESSIBLE];
    } else if (
      selection === ROOM_TYPE.BARRIER_FREE &&
      !room.accessibleRoomTypes.includes(ROOM_TYPE.BARRIER_FREE)
    ) {
      room.accessibleRoomTypes = [...room.accessibleRoomTypes, ROOM_TYPE.BARRIER_FREE];
    }
  });
}

/**
 * Auto-selects barrier-free or standard accessible rooms basesd on certain scenarios.
 */
export function handleRenderWithReSelections(
  handleRenderWithReSelectionsProps: HandleRenderWithReSelectionsPropsType
): void {
  const {
    rooms,
    accessibleRoomSelections,
    onAccessibleRoomSelection,
    onRoomTypeSelection,
    hotelInventoryResponse,
    currentRoomIndex,
    setAvailableAccessibleRoomsPerRoom,
    initialRoomTypeCounts,
  } = handleRenderWithReSelectionsProps;

  const { bfrCountInventoryTotal, strCountInventoryTotal } =
    getAccessibleRoomCounts(hotelInventoryResponse);
  let { standardAccesssibleCount: scrCount, barrierFreeCount: bfrCount } =
    countBarrierFreeAndStdAccessible(accessibleRoomSelections, initialRoomTypeCounts);

  const updatedRooms = [...rooms];
  const updatedSelections = [...accessibleRoomSelections];
  const onRenderSelection = updatedSelections[currentRoomIndex];

  if (onRenderSelection === ROOM_TYPE.STANDARD_ACCESSIBLE && scrCount >= strCountInventoryTotal) {
    updatedRooms.forEach((room: Room, roomIndex: number) => {
      if (roomIndex === currentRoomIndex) return; // skip current room
      if (!room.accessibleRoomTypes) {
        room.accessibleRoomTypes = [];
      }
      if (
        scrCount > strCountInventoryTotal &&
        updatedSelections[roomIndex] === ROOM_TYPE.STANDARD_ACCESSIBLE
      ) {
        // Switch from STR ➝ BFR
        updatedSelections[roomIndex] = ROOM_TYPE.BARRIER_FREE;
        room.accessibleRoomTypes = [...room.accessibleRoomTypes, ROOM_TYPE.BARRIER_FREE];
        onAccessibleRoomSelection(roomIndex, ROOM_TYPE.BARRIER_FREE);
        onRoomTypeSelection(roomIndex, room.roomTypes[0]);
        scrCount--;
      }
    });
  } else if (onRenderSelection === ROOM_TYPE.BARRIER_FREE && bfrCount >= bfrCountInventoryTotal) {
    updatedRooms.forEach((room: Room, roomIndex: number) => {
      if (roomIndex === currentRoomIndex) return; // skip current room
      if (!room.accessibleRoomTypes) {
        room.accessibleRoomTypes = [];
      }
      if (
        bfrCount > bfrCountInventoryTotal &&
        updatedSelections[roomIndex] === ROOM_TYPE.BARRIER_FREE
      ) {
        // Switch from BFR ➝ STR
        updatedSelections[roomIndex] = ROOM_TYPE.STANDARD_ACCESSIBLE;
        room.accessibleRoomTypes = [...room.accessibleRoomTypes, ROOM_TYPE.STANDARD_ACCESSIBLE];
        onAccessibleRoomSelection(roomIndex, ROOM_TYPE.STANDARD_ACCESSIBLE);
        onRoomTypeSelection(roomIndex, room.roomTypes[0]);
        bfrCount--;
      }
    });
  }
  updateRoomsSelections(updatedSelections, updatedRooms);
  setAvailableAccessibleRoomsPerRoom(getAvailableAccessibleroomsPerRoom(updatedRooms));
}

/**
 * Computes accessible room availability for each room in the list
 */
export function getAvailableAccessibleroomsPerRoom(rooms: Room[]): {
  hasAccessibleRoom: boolean;
  hasBarrierFreeRoom: boolean;
}[] {
  return rooms?.map((room: Room) => {
    if (!isSelectionRequired(room, accessiblePrefixes)) {
      return { hasAccessibleRoom: false, hasBarrierFreeRoom: false };
    }
    const accessibleRoomTypes = room?.accessibleRoomTypes || [];
    const hasAccessibleRoom = accessibleRoomTypes?.includes(ROOM_TYPE.STANDARD_ACCESSIBLE);
    const hasBarrierFreeRoom = accessibleRoomTypes?.includes(ROOM_TYPE.BARRIER_FREE);

    return {
      hasAccessibleRoom,
      hasBarrierFreeRoom,
    };
  });
}

//Utility function to check if room type re-selection is required
export function reSelectionRoomTypesRequired(currentRoomIndex: number): boolean {
  return currentRoomIndex !== -1;
}

// Handles initial render and pre-selects accessible room types where needed
export function initialRenderWithPreSelections(
  rooms: Room[],
  accessibleRoomSelections: (string | undefined)[],
  availableAccessibleRoomsPerRoom: ReturnType<typeof getAvailableAccessibleroomsPerRoom>,
  onAccessibleRoomSelection: (roomIndex: number, selection: string) => void,
  onRoomTypeSelection: (roomIndex: number, selection: string) => void
): void {
  rooms?.forEach((room: Room, roomIndex: number) => {
    const selectionRequired = isSelectionRequired(room, accessiblePrefixes);
    if (!selectionRequired) return;

    const roomSelected =
      accessibleRoomSelections?.[roomIndex] === ROOM_TYPE.STANDARD_ACCESSIBLE ||
      accessibleRoomSelections?.[roomIndex] === ROOM_TYPE.BARRIER_FREE;

    const { hasBarrierFreeRoom, hasAccessibleRoom } =
      availableAccessibleRoomsPerRoom?.[roomIndex] || {};

    if (!roomSelected) {
      let defaultSelection = null;

      if (hasAccessibleRoom) {
        defaultSelection = ROOM_TYPE.STANDARD_ACCESSIBLE;
      } else if (hasBarrierFreeRoom) {
        defaultSelection = ROOM_TYPE.BARRIER_FREE;
      }

      if (defaultSelection) {
        onAccessibleRoomSelection(roomIndex, defaultSelection);
      }
    }
    onRoomTypeSelection(roomIndex, room.roomTypes[0]);
  });
}

// Renders the formatted number of adults and children for a room
export function renderGuestNumbers(room: Room, t: (t: string) => string) {
  const adultsLabel = `${room?.adults} ${t(
    `dashboard.bookings.adult${room?.adults > 1 ? 's' : ''}`
  )}`;

  const childrenLabel =
    room?.children > 0
      ? `, ${room?.children} ${t(`dashboard.bookings.child${room?.children > 1 ? 'ren' : ''}`)}`
      : '';

  return (
    <Text mt="1" lineHeight="3" data-testid="accessible-room-guests">
      {adultsLabel}
      {childrenLabel}
    </Text>
  );
}

//Main component to render Accessible Room Type selection UI
export default function AccessibleRoomTypeOptionsComponent({
  rooms,
  onRoomTypeSelection,
  onAccessibleRoomSelection,
  accessibleRoomSelections,
  hotelInventoryResponse,
}: Readonly<Props>) {
  const [hasRun, setHasRun] = useState(false);
  const [currentRoomIndex, setCurrentIndex] = useState(-1);
  const [forceRender, setForceRender] = useState(0);

  const [availableAccessibleRoomsPerRoom, setAvailableAccessibleRoomsPerRoom] = useState(
    getAvailableAccessibleroomsPerRoom(rooms)
  );
  const initialRoomTypeCounts = { barrierFreeCount: 0, standardAccesssibleCount: 0 };
  const { t } = useTranslation(['common']);
  const roomTypeCountsTotalsRef = useRef(initialRoomTypeCounts);

  useEffect(() => {
    if (hasRun || !accessibleRoomSelections?.length) return;
    const { standardAccesssibleCount, barrierFreeCount } = countBarrierFreeAndStdAccessible(
      accessibleRoomSelections,
      initialRoomTypeCounts
    );
    roomTypeCountsTotalsRef.current = { standardAccesssibleCount, barrierFreeCount };
    setHasRun(true);
  }, [accessibleRoomSelections, hasRun]);

  useEffect(() => {
    const isReOrderingRoomTypesRequired = reSelectionRoomTypesRequired(currentRoomIndex);
    if (forceRender && isReOrderingRoomTypesRequired) {
      handleRenderWithReSelections({
        rooms,
        accessibleRoomSelections: accessibleRoomSelections as string[],
        availableAccessibleRoomsPerRoom,
        onAccessibleRoomSelection,
        onRoomTypeSelection,
        hotelInventoryResponse,
        currentRoomIndex,
        setAvailableAccessibleRoomsPerRoom,
        initialRoomTypeCounts,
      });
    }

    initialRenderWithPreSelections(
      rooms,
      accessibleRoomSelections as string[],
      availableAccessibleRoomsPerRoom,
      onAccessibleRoomSelection,
      onRoomTypeSelection
    );
  }, [forceRender, setAvailableAccessibleRoomsPerRoom]);

  return (
    <>
      <Heading as="h1" data-testid="choose-roomtype-title" {...headingStyle}>
        {t('accessible.chooseRoomTypeTitle')}
      </Heading>

      {rooms?.map((room: Room, roomIndex: number) => {
        const selectionRequired = isSelectionRequired(room, accessiblePrefixes);
        return (
          <Box key={room.roomTypes[roomIndex]} mt={{ base: 'xl', xs: '3xl' }}>
            <Flex {...roomAndGuestsFlexStyle}>
              <Flex direction="column" mr="md">
                <Heading as="h3" data-testid="accessible-room-number" {...roomHeadingStyle}>
                  {t('booking.hotel.summary.room').replace(
                    '[roomNumber]',
                    selectionRequired
                      ? (roomIndex + 1).toString()
                      : `${roomIndex + 1} ${room.roomTypes[0]}`
                  )}
                </Heading>
                {renderGuestNumbers(room, t)}
              </Flex>
            </Flex>

            {selectionRequired ? (
              renderSelectionOptions(roomIndex)
            ) : (
              <Text
                mt={{ base: '1', xs: '0' }}
                data-testid="accessible-no-room-type-selection-required"
              >
                {renderSanitizedHtml(t('accessible.no.roomtypeSelection.required'))}
              </Text>
            )}
          </Box>
        );
      })}
    </>
  );

  function renderSelectionOptions(roomIndex: number) {
    const { hasBarrierFreeRoom, hasAccessibleRoom } =
      availableAccessibleRoomsPerRoom?.[roomIndex] || {};
    return (
      <Box maxW="3xl" mt="lg" data-testid="accessible-roomtype-dropdown">
        <RadioButton
          type="standard-accessible-room"
          value="standard-accessible-room"
          onChange={() => {
            setForceRender((prev) => prev + 1);
            setCurrentIndex(roomIndex);
            onAccessibleRoomSelection(roomIndex, ROOM_TYPE.STANDARD_ACCESSIBLE);
          }}
          isChecked={accessibleRoomSelections![roomIndex] === ROOM_TYPE.STANDARD_ACCESSIBLE}
          isDisabled={!hasAccessibleRoom}
        >
          <>
            <Text fontWeight="bold" data-testid="accessible-standard-room">
              {t('accessible.standardRoom')}
            </Text>
            <Box className="formatLinks">
              {renderSanitizedHtml(t('accessible.standardRoom.text'))}
            </Box>
          </>
        </RadioButton>

        <Box mt="1em" />

        <RadioButton
          type="barrier-free-room"
          value="barrier-free-room"
          onChange={() => {
            setForceRender((prev) => prev + 1);
            setCurrentIndex(roomIndex);
            onAccessibleRoomSelection(roomIndex, ROOM_TYPE.BARRIER_FREE);
          }}
          isChecked={accessibleRoomSelections![roomIndex] === ROOM_TYPE.BARRIER_FREE}
          isDisabled={!hasBarrierFreeRoom}
        >
          <>
            <Text fontWeight="bold" data-testid="accessible-barrier-free-room">
              {t('accessible.barrierFree')}
            </Text>
            <Box className="formatLinks">
              {renderSanitizedHtml(t('accessible.barrierFree.text'))}
            </Box>
          </>
        </RadioButton>

        <Box mt="md">
          <Notification
            title={t('accessible.standardRoom')}
            description={t('accessible.standardRoom.alertText')}
            prefixDataTestId="accessible-standardRoom"
            status="warning"
            variant="alert"
            svg={<Alert />}
            isClosed={hasAccessibleRoom}
            style={{ maxWidth: 'none' }}
            isInnerHTML
          />
          <Notification
            title={t('accessible.barrierFree')}
            description={t('accessible.barrierFree.alertText')}
            prefixDataTestId="accessible-barrierFree"
            status="warning"
            variant="alert"
            svg={<Alert />}
            isClosed={hasBarrierFreeRoom}
            style={{ maxWidth: 'none' }}
            isInnerHTML
          />
        </Box>
      </Box>
    );
  }
}

const headingStyle = {
  fontWeight: 'semibold',
  fontSize: { base: '3xl', sm: '3xxl' },
  lineHeight: { base: '4', sm: '5' },
};

const roomAndGuestsFlexStyle = {
  mb: 'sm',
  flexDirection: { base: 'column', xs: 'row' },
  w: { base: '100%', sm: '64%', md: '50%' },
} as FlexProps;

const roomHeadingStyle = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
};
