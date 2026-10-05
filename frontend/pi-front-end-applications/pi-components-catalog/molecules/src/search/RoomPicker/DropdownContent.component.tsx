import type { BoxProps } from '@chakra-ui/react';
import {
  Box,
  Divider,
  Flex,
  Link,
  Menu,
  MenuGroup,
  MenuList,
  Text,
  VStack,
  Checkbox,
} from '@chakra-ui/react';
import type {
  RoomPickerLabels,
  RoomTypeLabels,
  ScreenSize,
  SearchRoomCodes,
  SearchRoomOccupancyLimitationsType,
  SearchRoomType,
} from '@whitbread-eos/api';
import { BOOKING_CHANNEL, SEARCH_ROOM_PICKER, THIRTY_MINUTES } from '@whitbread-eos/api';
import {
  Accessible24,
  Alert,
  Button,
  DescriptionBox,
  Dropdown,
  DropdownProps,
  Icon,
  Notification,
  OneAdult,
  Path,
  Switcher as Switch,
} from '@whitbread-eos/atoms';
import { formatDataTestId, getCookie, getRoomTypeOptions, setCookie } from '@whitbread-eos/utils';
import { useMemo } from 'react';

import {
  ADULTS_KEY,
  CHILDREN_KEY,
  ROOM_TYPE_KEY,
  SHOULD_INCLUDE_COT,
  SHOULD_BE_ACCESSIBLE,
} from './constants';

export interface DropdownContentProps {
  setIsOpen?: (value: boolean) => void;
  rooms: SearchRoomType[];
  addRoom: () => void | null;
  removeRoom: (roomId: string) => void;
  updateRoom: any;
  onSubmit: () => void;
  adultsOptions: { id: number; label: string }[];
  childrenOptions: { id: number; label: string }[];
  labels: RoomPickerLabels;
  maxNumberOfRooms: number;
  dataRoomOccupancyLimitations: SearchRoomOccupancyLimitationsType;
  roomCodes: SearchRoomCodes;
  dataTestId: string;
  otherStyles?: DropdownProps;
  screenSize: ScreenSize;
  showMultipleRooms?: boolean;
  channel?: string;
  className?: string;
  noRoomTypeSearch?: boolean;
  tabbingAccessibility?: (
    event: React.KeyboardEvent,
    focusableRefs: React.MutableRefObject<HTMLElement[]>,
    isOpen?: boolean
  ) => void;
  focusableRefs?: React.MutableRefObject<HTMLElement[]>;
}

interface Mode {
  mode: string;
}

declare global {
  interface Window {
    piConfig: {
      [key: string]: Mode;
      paymentsRedesign: Mode;
      billingAddressCapture: Mode;
      digRegCard: Mode;
      ancillaries: Mode;
      roomPickerRedesign: Mode;
    };
  }
}

export default function DropdownContent({
  setIsOpen,
  rooms,
  addRoom,
  removeRoom,
  updateRoom,
  onSubmit,
  adultsOptions,
  childrenOptions,
  labels,
  maxNumberOfRooms,
  dataRoomOccupancyLimitations,
  roomCodes,
  dataTestId,
  screenSize,
  showMultipleRooms,
  channel,
  className,
  noRoomTypeSearch,
  tabbingAccessibility,
  focusableRefs,
}: Readonly<DropdownContentProps>) {
  const roomOccupancies = dataRoomOccupancyLimitations.roomOccupancyLimitations.roomOccupancies;

  //AB test for Room Picker horizontal redesign - for PI/BB and screen size > 1000px
  const isTestTargetRoomPickerRedesign = () => {
    if (typeof window !== 'undefined') {
      // check if cookie already exists for A/B test variant
      // otherwise set cookie if A/B window.piConfig.roomPickerRedesign value exists
      const horizontalRoomPickerCookie = getCookie(SEARCH_ROOM_PICKER);
      if (horizontalRoomPickerCookie) {
        return true;
      } else {
        if (window?.piConfig?.roomPickerRedesign && channel !== BOOKING_CHANNEL.CCUI) {
          const { mode } = window.piConfig.roomPickerRedesign;
          mode?.length && setCookie(SEARCH_ROOM_PICKER, mode, THIRTY_MINUTES);
          return true;
        }
        return false;
      }
    }
  };
  const isTestTargetRoomPickerRedesignEnabled = isTestTargetRoomPickerRedesign();

  const onButtonClick = () => {
    window.scrollTo({
      top: 0,
      left: 0,
      behavior: 'smooth',
    });
    onSubmit();
    setIsOpen?.(false);
  };

  const {
    removeRoomButtonLabel,
    roomsWarningTitle,
    roomsWarningDescription,
    roomsWarningDescriptionCCUI,
    addMoreRoomsLabel,
    doneButtonLabel,
    adultsLabel,
    adultsMaxPerRoomLabel,
    childrenLabel,
    childrenAgeLabel,
    cotLimit,
    cotLabel,
    roomTypeLabel,
    roomLabel,
    single,
    double,
    accessible,
    twin,
    family,
    accessibleRoom,
  } = labels;

  const roomTypeOccupancies: RoomTypeLabels = {
    single,
    double,
    accessible,
    twin,
    family,
  };

  const { isLessThanXs, isLessThanSm, isLessThanMd } = screenSize;
  const rootPropsMenuList = useMemo(
    () =>
      isLessThanXs
        ? menuListRootPropsLessThanXs
        : isLessThanSm
          ? menuListRootPropsLessThanSm
          : isLessThanMd
            ? menuListRootPropsLessThanMd
            : {},
    [isLessThanXs, isLessThanSm, isLessThanMd]
  );

  return (
    <MenuList
      data-testid={
        isTestTargetRoomPickerRedesignEnabled ? 'roomPickerMenu-variant' : 'roomPickerMenu'
      }
      w={menuListWidth}
      rootProps={rootPropsMenuList}
      overflow="hidden"
      zIndex="1801"
      sx={isTestTargetRoomPickerRedesignEnabled ? horizontalVariantStyles() : ''}
    >
      {rooms.map((room: SearchRoomType, index: number) => {
        const isLastIndex = rooms.length === index + 1;

        return (
          <MenuGroup key={room.id}>
            <Menu flip={false}>
              <Box as="div" flexDirection="column" {...menuItemStyles} role="menuitem">
                <Flex
                  w="full"
                  justifyContent={`${rooms.length > 1 ? 'space-between' : 'start'}`}
                  className="roompicker-room-heading"
                >
                  <Box {...roomLabelStyles}>
                    {roomLabel} {index + 1}
                  </Box>
                  {rooms.length > 1 && (
                    <Link
                      data-testid={`remove-room-${room.id}`}
                      ref={(el) => {
                        if (!el || !focusableRefs) return;
                        focusableRefs.current = [...focusableRefs.current, el];
                      }}
                      color="btnSecondaryEnabled"
                      textDecoration="underline"
                      onClick={() => removeRoom(room.id as string)}
                      onKeyDown={(event) => {
                        if (tabbingAccessibility && focusableRefs) {
                          tabbingAccessibility(event, focusableRefs);
                          if (event.key === ' ') {
                            event.preventDefault();
                            removeRoom(room.id as string);
                          }
                        }
                      }}
                      tabIndex={0}
                    >
                      {removeRoomButtonLabel}
                    </Link>
                  )}
                </Flex>
                <VStack w="full" mt="2rem" className="roompicker-stack">
                  <Flex w="full" justify="space-between" className="roompicker-occupancy">
                    <VStack spacing={0} sx={googleTranslateStyles()}>
                      <Dropdown
                        onChange={(p) => updateRoom(room.id, ADULTS_KEY, p?.id, labels)}
                        dropdownStyles={{ menuButtonStyles: menuButtonStyles }}
                        label={adultsLabel}
                        icon={<Icon svg={<OneAdult />} />}
                        placeholder={String(room.adults)}
                        options={adultsOptions}
                        matchWidth
                        dataTestId={`${dataTestId}-adults`}
                        tabbingAccessibility={tabbingAccessibility}
                        focusableRefs={focusableRefs}
                      />
                      <Text {...helperTextStyles}>{adultsMaxPerRoomLabel}</Text>
                    </VStack>
                    <VStack spacing={0} sx={googleTranslateStyles()}>
                      <Dropdown
                        onChange={(p) => updateRoom(room.id, CHILDREN_KEY, p?.id, labels)}
                        dropdownStyles={{ menuButtonStyles: menuButtonStyles }}
                        placeholder={String(room.children)}
                        label={childrenLabel}
                        options={childrenOptions}
                        matchWidth
                        dataTestId={`${dataTestId}-children`}
                        tabbingAccessibility={tabbingAccessibility}
                        focusableRefs={focusableRefs}
                      />
                      <Text {...helperTextStyles}>{childrenAgeLabel}</Text>
                    </VStack>
                  </Flex>
                  {!noRoomTypeSearch && (
                    <>
                      <Flex {...cotSwitcherStyles} className="roompicker-cot-switcher">
                        <Switch
                          size="lg"
                          isChecked={room.shouldIncludeCot}
                          onChange={(status) =>
                            updateRoom(room.id, SHOULD_INCLUDE_COT, status.isChecked, labels)
                          }
                        />
                        <Text {...cotLabelStyles}>{cotLabel}</Text>
                        <Text {...cotLimitStyles}>{cotLimit}</Text>
                      </Flex>
                      <Dropdown
                        dataTestId={`${dataTestId}-roomTypeDropdown`}
                        className={className}
                        onChange={(p) => updateRoom(room.id, ROOM_TYPE_KEY, p?.id, labels)}
                        dropdownStyles={{
                          menuButtonStyles: { w: '100%' },
                          wrapperStyles: { mt: '1.5rem' },
                        }}
                        placeholder={room.roomType}
                        tabbingAccessibility={tabbingAccessibility}
                        focusableRefs={focusableRefs}
                        label={roomTypeLabel}
                        matchWidth
                        options={getRoomTypeOptions(
                          room,
                          roomOccupancies,
                          roomTypeOccupancies,
                          roomCodes
                        ).flat()}
                      />
                    </>
                  )}
                  {noRoomTypeSearch && (
                    <>
                      <Flex
                        {...cotSwitcherStyles}
                        mb="0"
                        className="roompicker-accessible-switcher"
                        data-testid={formatDataTestId(dataTestId, 'accessibleRoomSwitcher')}
                      >
                        <Checkbox
                          size="lg"
                          isChecked={room.shouldBeAccessible}
                          onKeyDown={(event) => {
                            if (tabbingAccessibility && focusableRefs) {
                              tabbingAccessibility(event, focusableRefs);
                            }
                          }}
                          onChange={(status) =>
                            updateRoom(room.id, SHOULD_BE_ACCESSIBLE, status.target.checked, labels)
                          }
                          disabled={room.children > 0}
                        />
                        <Text
                          data-testid={formatDataTestId(dataTestId, 'accessibleRoomLabel')}
                          {...cotLabelStyles}
                        >
                          {accessibleRoom}
                        </Text>
                        <Icon ml="0.625rem" svg={<Accessible24 />} />
                      </Flex>
                      <Flex
                        {...cotSwitcherStyles}
                        mb="0"
                        mt="0.625rem"
                        className="roompicker-cot-switcher"
                        data-testid={formatDataTestId(dataTestId, 'cotRoomSwitcher')}
                      >
                        <Checkbox
                          data-testid={formatDataTestId(dataTestId, 'cotRoomCheckbox')}
                          size="lg"
                          isChecked={room.shouldIncludeCot}
                          onKeyDown={(event) => {
                            if (tabbingAccessibility && focusableRefs) {
                              tabbingAccessibility(event, focusableRefs);
                            }
                          }}
                          onChange={(status) =>
                            updateRoom(room.id, SHOULD_INCLUDE_COT, status.target.checked, labels)
                          }
                        />
                        <Text {...cotLabelStyles}>{cotLabel}</Text>
                        <Text {...cotLimitStyles}>{cotLimit}</Text>
                      </Flex>
                    </>
                  )}
                  {isLastIndex
                    ? renderButton(
                        addRoom,
                        onButtonClick,
                        rooms,
                        roomsWarningTitle,
                        roomsWarningDescription,
                        roomsWarningDescriptionCCUI,
                        addMoreRoomsLabel,
                        doneButtonLabel,
                        maxNumberOfRooms,
                        showMultipleRooms!,
                        channel,
                        tabbingAccessibility,
                        focusableRefs
                      )
                    : renderDivider()}
                </VStack>
              </Box>
            </Menu>
          </MenuGroup>
        );
      })}
    </MenuList>
  );
}

function renderButton(
  addRoom: () => void,
  onButtonClick: () => void,
  rooms: SearchRoomType[],
  roomsWarningTitle: string,
  roomsWarningDescription: string,
  roomsWarningDescriptionCCUI: string,
  addMoreRoomsLabel: string,
  doneButtonLabel: string,
  maxNumberOfRooms: number,
  showMultipleRooms: boolean,
  channel?: string,
  tabbingAccessibility?: (
    event: React.KeyboardEvent,
    focusableRefs: React.MutableRefObject<HTMLElement[]>,
    isOpen?: boolean
  ) => void,
  focusableRefs?: React.MutableRefObject<HTMLElement[]>
) {
  return (
    <>
      {rooms?.length === maxNumberOfRooms ? (
        <Box mt="xl">
          <Notification
            variant="alert"
            status="warning"
            title={roomsWarningTitle}
            description={
              <DescriptionBox
                html={displayDescription(
                  roomsWarningDescription,
                  roomsWarningDescriptionCCUI,
                  channel
                )}
              />
            }
            svg={<Alert />}
          />
        </Box>
      ) : (
        <Flex
          ref={(el) => {
            if (!el || !focusableRefs) return;
            focusableRefs.current = [...focusableRefs.current, el];
          }}
          onClick={() => addRoom()}
          onKeyDown={(event) => {
            if (tabbingAccessibility && focusableRefs) {
              tabbingAccessibility(event, focusableRefs);
              if (event.key === ' ') {
                event.preventDefault();
                addRoom();
              }
            }
          }}
          w="full"
          pl="md"
          alignItems="center"
          data-testid="addMoreRooms"
          tabIndex={0}
          {...addAnotherRoomWrapperStyles}
        >
          {!showMultipleRooms && (
            <>
              <Icon
                {...addAnotherRoomButtonStyles}
                svg={<Path color="var(--chakra-colors-primary)" />}
              />
              <Text {...addAnotherRoomLabelStyles} data-testid="addMoreRoomsLabel">
                {addMoreRoomsLabel}
              </Text>
            </>
          )}
        </Flex>
      )}
      <Box
        w="full"
        ref={(el) => {
          if (!el || !focusableRefs) return;

          const button = el.querySelector('button');
          if (button) {
            focusableRefs.current = [...focusableRefs.current, button];
          }
        }}
      >
        <Button
          w="full"
          onClick={onButtonClick}
          size="md"
          variant="tertiary"
          data-testid="doneButton"
          onKeyDown={(event) => {
            if (tabbingAccessibility && focusableRefs) {
              tabbingAccessibility(event, focusableRefs);
            }
          }}
          {...doneButtonStyles}
        >
          {doneButtonLabel}
        </Button>
      </Box>
    </>
  );
}

function renderDivider() {
  return (
    <Box w="full" mt="2rem" className="roompicker-divider">
      <Divider orientation="horizontal" />
    </Box>
  );
}

function displayDescription(
  roomsWarningDescription: string,
  roomsWarningDescriptionCCUI: string,
  channel?: string
): string {
  if (channel && channel === BOOKING_CHANNEL.CCUI) {
    return roomsWarningDescriptionCCUI;
  }
  return roomsWarningDescription;
}

const menuButtonStyles = {
  w: '8.5rem',
};

const menuListWidth = {
  mobile: '20rem',
  xs: '21.4375rem',
  sm: '20rem',
  md: 'var(--chakra-space-breakpoint-m)',
};

const menuListRootPropsLessThanXs = {
  inset: '0 auto auto calc((100% + var(--chakra-space-8)) * -1)!important',
  transform: 'translate3d(var(--chakra-space-4), calc(var(--chakra-space-14) + 1px), 0)!important',
};

const menuListRootPropsLessThanSm = {
  inset: '0 auto auto calc((100% + var(--chakra-space-5)) * -1)!important',
  transform: 'translate3d(var(--chakra-space-4), calc(var(--chakra-space-14) + 1px), 0)!important',
};

const menuListRootPropsLessThanMd = {
  inset: '0 0 auto auto!important',
  transform: 'translate3d(0, calc(var(--chakra-space-14) + 1px), 0)!important',
};

const roomLabelStyles = {
  alignSelf: 'start',
  fontWeight: '600',
  fontSize: 'lg',
  textTransform: 'capitalize',
  as: 'h2',
} as BoxProps;

const menuItemStyles = {
  height: 'auto',
  px: 'var(--chakra-space-md)',
  py: '7px',
  _hover: { backgroundColor: 'transparent' },
  _active: { backgroundColor: 'transparent' },
  _focus: { backgroundColor: 'transparent' },
};

const helperTextStyles = {
  color: 'darkGrey2',
  fontSize: 'var(--chakra-fontSizes-xs)',
  mt: '0.375rem!important',
  w: '7.5rem',
  ml: 'var(--chakra-space-md)!important',
};

const cotSwitcherStyles = {
  w: 'full',
  pl: '1rem',
  alignItems: 'center',
  mt: 'var(--chakra-space-xl) !important',
  mb: 'var(--chakra-space-lg) !important',
};

const cotTextStyles = {
  fontSize: 'md',
  lineHeight: '3',
};

const cotLabelStyles = {
  ...cotTextStyles,
  ml: 'md',
  fontWeight: 'bold',
} as BoxProps;

const cotLimitStyles = {
  ...cotTextStyles,
  ml: 'sm',
  fontWeight: 'normal',
} as BoxProps;

const addAnotherRoomWrapperStyles = {
  _hover: {
    cursor: 'pointer',
  },
};

const addAnotherRoomButtonStyles = {
  border: '0.063rem solid var(--chakra-colors-primary)',
  p: '0.5rem',
  margin: 'var(--chakra-space-lg) 0',
  borderRadius: '50%',
  cursor: 'pointer',
};

const addAnotherRoomLabelStyles = {
  fontSize: 'md',
  fontWeight: 'medium',
  marginLeft: 'md',
} as BoxProps;

const doneButtonStyles = {
  _hover: {
    bg: 'baseWhite',
  },
};

// A/B Test variant overrides -  horizontal roompicker
const horizontalVariantStyles = () => ({
  '@media screen and (min-width: 1000px)': {
    width: '800px',
    overflowY: 'scroll',
    overflowX: 'hidden',
    maxHeight: '80vh',

    '.roompicker-stack': {
      position: 'relative',
      justifyContent: 'normal',
      display: 'block',
      '.roompicker-divider': {
        marginTop: 'md',
        borderColor: 'lightGrey3',
      },
    },
    '.chakra-menu__menuitem': {
      paddingBottom: '6px',
    },
    '.roompicker-occupancy': {
      display: 'inline-block',
      width: 'auto',
      '.chakra-stack': {
        display: 'inline-block',
        paddingRight: 'md',
        '&:last-child': {
          display: 'inline-block',
          paddingRight: '0',
        },
      },
      '.chakra-text': {
        color: 'darkGrey1',
      },
      '.chakra-menu__menu-button': {
        minWidth: '150px',
      },
    },
    '.roompicker-cot-switcher': {
      display: 'inline-block',
      verticalAlign: 'top',
      width: '185px',
      paddingLeft: '0',
      position: 'absolute',
      top: '-1.2rem',
      marginLeft: '260px',
      '.chakra-switch': {
        float: 'left',
        paddingRight: '10px',
      },
      '.chakra-text': {
        fontSize: '15px',
        '&:last-child': {
          fontSize: 'sm',
          paddingLeft: '3.25rem',
        },
      },
    },
    '.roompicker-room-type': {
      width: '230px',
      display: 'inline-block',
      mt: '0',
      verticalAlign: 'top',
      marginLeft: 'md',
    },
    '.roompicker-room-heading': {
      justifyContent: 'normal',
      '.chakra-link': {
        paddingLeft: 'xl',
      },
    },
    '.chakra-button': {
      marginTop: 'lg',
    },
  },
});
// End - A/B Test variant overrides -  horizontal roompicker

const googleTranslateStyles = () => ({
  'button font': {
    pointerEvents: 'none',
  },
});
