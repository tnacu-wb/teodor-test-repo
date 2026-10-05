import { Box, BoxProps, MenuButtonProps } from '@chakra-ui/react';
import {
  SearchRoomType,
  ScreenSize,
  RoomPickerLabels,
  SearchRoomCodes,
  SearchRoomOccupancyLimitationsType,
  RoomPickerPlaceholderType,
  BOOKING_CHANNEL,
  FT_PI_NO_ROOM_TYPE_SEARCH,
} from '@whitbread-eos/api';
import { OneAdult, Dropdown, InfoMessage, Icon } from '@whitbread-eos/atoms';
import {
  createOptionsAdultsChildrenDropdown,
  getAdultsChildrenOptions,
  getCookie,
  BUNDLE_CHOICE,
  useFeatureToggle,
  BUNDLE_CHOICE_OPTIONS,
  tabbingAccessibility,
} from '@whitbread-eos/utils';
import { nanoid } from 'nanoid';
import { useState, useRef } from 'react';

import DropdownContent, { DropdownContentProps } from './DropdownContent.component';
import {
  CHILDREN_KEY,
  ADULTS_NUMBER_KEY,
  CHILDREN_NUMBER_KEY,
  ADULTS_KEY,
  SHOULD_BE_ACCESSIBLE,
} from './constants';

export interface Props {
  onSubmit: (rooms: SearchRoomType[]) => void;
  boxWrapperStyles?: BoxProps;
  roomPickerInputElementStyles?: MenuButtonProps;
  roomPickerSize?: BoxProps;
  initialState: SearchRoomType[];
  labels: RoomPickerLabels;
  maxNumberOfRooms: number;
  dataRoomOccupancyLimitations: SearchRoomOccupancyLimitationsType;
  roomCodes: SearchRoomCodes;
  screenSize: ScreenSize;
  showErrorMessage?: boolean;
  errorMessage?: string;
  showMultipleRooms?: boolean;
  channel?: string;
}

export default function RoomPicker({
  onSubmit,
  boxWrapperStyles,
  roomPickerInputElementStyles,
  roomPickerSize,
  initialState,
  labels,
  maxNumberOfRooms,
  dataRoomOccupancyLimitations,
  roomCodes,
  screenSize,
  showErrorMessage,
  errorMessage,
  showMultipleRooms,
  channel,
}: Readonly<Props>) {
  const focusableRefs = useRef<HTMLElement[]>([]);
  const { [FT_PI_NO_ROOM_TYPE_SEARCH]: isNoRoomTypeSearchEnabled } = useFeatureToggle();
  const noRoomTypeSearch =
    isNoRoomTypeSearchEnabled &&
    getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.noRoomTypeSearch;

  const createRoom = (labels: RoomPickerLabels): SearchRoomType => ({
    id: nanoid(),
    adults: 1,
    children: 0,
    shouldIncludeCot: false,
    shouldBeAccessible: false,
    roomType: labels.double,
  });
  const rooms = initialState;
  const [isContentDisplayed, setIsContentDisplayed] = useState(false);
  const addRoom = () => {
    if (rooms.length < maxNumberOfRooms) {
      const newRooms = [...rooms, createRoom(labels)];
      onSubmit(newRooms);
    }
  };
  const removeRoom = (roomId: string) => {
    const newRooms = rooms.filter((room) => room.id !== roomId);
    onSubmit(newRooms);
  };
  const updateRoom = (
    roomId: string,
    option: string,
    value: number | string | boolean,
    labels: RoomPickerLabels
  ) => {
    const newRooms = rooms.map((room) => {
      if (room.id !== roomId) {
        return room;
      } else {
        let extraChanges = {};
        // CCUI - agent is allowed to select any adult combination for room types
        if (option === ADULTS_KEY && Number(value) > 0 && channel !== BOOKING_CHANNEL.CCUI) {
          if (room.roomType === labels?.twin && value === 1) {
            extraChanges = { roomType: labels?.double };
          }
          if (room.roomType === labels?.single && value === 2) {
            extraChanges = { roomType: labels?.double };
          }
        }

        if (option === CHILDREN_KEY) {
          if (value === 0) {
            extraChanges = { roomType: labels?.double };
          } else {
            extraChanges = { roomType: labels?.family, [SHOULD_BE_ACCESSIBLE]: false };
          }
        }

        if (option === SHOULD_BE_ACCESSIBLE) {
          room.roomType = labels.accessible;
        }

        return {
          ...room,
          [option]: value,
          ...extraChanges,
        };
      }
    });
    onSubmit(newRooms);
  };

  const _onSubmit = () => {
    onSubmit(rooms);
  };

  const { isLessThanSm } = screenSize;

  const dropdownWrapperStyles = {
    ...wrapperStyles,
    ...boxWrapperStyles,
    ...(showErrorMessage && !isContentDisplayed && { ...errorWrapperStyles }),
  };
  const menuBtnStyles = {
    ...menuButtonStyles,
    ...roomPickerInputElementStyles,
    ...(showErrorMessage && !isContentDisplayed && { ...errorMenuButtonStyles }),
  };

  return (
    <Box position="relative" {...roomPickerSize}>
      <Dropdown
        dropdownStyles={{
          wrapperStyles: dropdownWrapperStyles,
          menuButtonStyles: menuBtnStyles,
          menuButtonWrapperStyles: menuButtonWrapperStyles,
          errorHoverMenuButtonStyles:
            showErrorMessage && !isContentDisplayed ? errorHoverMenuButtonStyles : {},
        }}
        icon={<Icon svg={<OneAdult />} />}
        skipChevron
        placeholder={getPlaceholder(rooms, labels)}
        dataTestId="roomPicker-dropdown"
        onDisplayContent={(isOpen) => {
          if (isOpen && focusableRefs) {
            focusableRefs.current = [];
          }
          handleDisplayContent(isOpen);
        }}
        tabbingAccessibility={tabbingAccessibility}
        focusableRefs={focusableRefs}
      >
        {(props: BoxProps | DropdownContentProps) => (
          <DropdownContent
            {...props}
            showMultipleRooms={showMultipleRooms}
            onSubmit={_onSubmit}
            rooms={rooms}
            addRoom={addRoom}
            removeRoom={removeRoom}
            updateRoom={updateRoom}
            labels={labels}
            maxNumberOfRooms={maxNumberOfRooms}
            dataRoomOccupancyLimitations={dataRoomOccupancyLimitations}
            adultsOptions={createOptionsAdultsChildrenDropdown(
              getAdultsChildrenOptions(dataRoomOccupancyLimitations, ADULTS_NUMBER_KEY),
              labels.adult,
              labels.adults
            )}
            childrenOptions={createOptionsAdultsChildrenDropdown(
              getAdultsChildrenOptions(dataRoomOccupancyLimitations, CHILDREN_NUMBER_KEY),
              labels.child,
              labels.children
            )}
            dataTestId="roomPicker-dropdownContent"
            roomCodes={roomCodes}
            screenSize={screenSize}
            channel={channel}
            className={'roompicker-room-type'}
            noRoomTypeSearch={noRoomTypeSearch}
            tabbingAccessibility={tabbingAccessibility}
            focusableRefs={focusableRefs}
          />
        )}
      </Dropdown>
      {!isLessThanSm && showErrorMessage && errorMessage && (
        <InfoMessage infoMessage={errorMessage} otherStyles={alertStyles} />
      )}
    </Box>
  );

  function handleDisplayContent(isContentDisplayed: boolean) {
    setIsContentDisplayed(isContentDisplayed);
  }
}

function getTotalPeople(state: SearchRoomType[]) {
  return state.reduce(
    (acc: { adults: number; children: number }, cur: SearchRoomType) => {
      acc.adults += +cur.adults;
      acc.children += +cur.children;
      return acc;
    },
    { adults: 0, children: 0 }
  );
}

function getPlaceholderString(obj: RoomPickerPlaceholderType, labels: RoomPickerLabels) {
  const { adult, adults, child, children, room, rooms } = labels;

  const adultsLabel = obj.adults === 1 ? adult : adults;
  const childLabel = obj.children === 1 ? child : children;
  const childrenLabel = obj.children === 0 ? '' : childLabel;
  const roomsLabel = obj.rooms === 1 ? room : rooms;

  return obj.children === 0
    ? `${obj.adults} ${adultsLabel}, ${obj.rooms} ${roomsLabel}`
    : `${obj.adults} ${adultsLabel}, ${obj.children} ${childrenLabel}, ${obj.rooms} ${roomsLabel}`;
}

function getPlaceholder(state: SearchRoomType[], labels: RoomPickerLabels) {
  const totalRooms = state.length;
  const totalPeople = getTotalPeople(state);

  return getPlaceholderString({ ...totalPeople, rooms: totalRooms }, labels);
}

const menuButtonStyles = {
  bgColor: 'transparent',
  border: '0.063rem solid transparent',
  _hover: {
    border: '0.063rem solid var(--chakra-colors-darkGrey1)',
  },
  _active: {
    border: '0.125rem solid var(--chakra-colors-primary)',
  },
  _focusVisible: {
    border: '0.125rem solid var(--chakra-colors-primary)',
  },
};

const errorHoverMenuButtonStyles = {
  _hover: {
    border: 'none',
  },
};

const errorMenuButtonStyles = {
  borderWidth: '0',
  _active: {
    border: 'none',
  },
};

const wrapperStyles = {
  mt: '3rem',
};

const errorWrapperStyles = {
  borderRadius: 'var(--chakra-space-radiusSmall)',
  border: '2px solid var(--chakra-colors-error)',
};

const menuButtonWrapperStyles = {
  display: 'block',
  height: '100%',
  marginLeft: '-1px',
};

const alertStyles = {
  w: 'full',
  zIndex: 1,
};
