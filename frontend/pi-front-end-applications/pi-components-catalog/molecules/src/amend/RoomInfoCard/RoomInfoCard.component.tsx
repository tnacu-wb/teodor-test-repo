import type { BoxProps, FlexProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import {
  AmendHotelAvailabilityData,
  AmendReservation,
  AmendRoomsAndGuestsLabels,
  AmendRoomType,
  Area,
  BUSINESS_BOOKER_USER_ROLES,
  SearchRoomOccupancyLimitationsType,
  RoomCodes,
  ACCESSIBLE_ROOM_TYPE,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  CountryCode,
  Customer,
  Channel,
} from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms';
import {
  formatCurrency,
  formatPriceWithDecimal,
  upperOnlyFirst,
  useFeatureToggle,
  useUserData,
  useUserDetails,
} from '@whitbread-eos/utils';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';

import RemoveRoomModal from '../RemoveRoomModal';
import RoomModal from '../RoomModal';
import { getGuestsPlaceholderString } from '../utilities';

interface Props {
  data: AmendReservation;
  currencyCode: string;
  index: number;
  styles?: { roomWrapper: BoxProps };
  labels: AmendRoomsAndGuestsLabels;
  language: string;
  reservationsNumber: number;
  baseDataTestId: string;
  roomRules: SearchRoomOccupancyLimitationsType;
  hotelAvailabilityParams: AmendHotelAvailabilityData;
  onUpdateRoom: (selectedRoom: AmendRoomType, reservationId: string, roomNumber: number) => void;
  onRemoveRoom: (reservationId: string, roomNumber: number) => void;
  reservationId: string;
  variant: Area;
  isCancellable: boolean;
  brand: string;
  channel: Channel;
  hotelCountry: string;
  isAmendable: boolean;
  userDetails?: Customer;
  isPromoCodeLandingPageEnabled: boolean;
}

export default function RoomInfoCard({
  data,
  currencyCode,
  index,
  styles,
  labels,
  language,
  reservationsNumber,
  baseDataTestId,
  roomRules,
  hotelAvailabilityParams,
  onUpdateRoom,
  onRemoveRoom,
  reservationId,
  variant,
  isCancellable,
  isAmendable,
  brand,
  channel,
  hotelCountry,
  userDetails,
  isPromoCodeLandingPageEnabled,
}: Readonly<Props>) {
  const { roomStay, reservationGuestList } = data;
  const { edit, roomLabel, remove, roomModalLabels } = labels;
  const { roomDropdownRoomCodes } = roomModalLabels;
  const { givenName, surName, nameTitle } = reservationGuestList[0];
  const cardStyles = { ...wrapperStyles, ...styles?.roomWrapper };
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { t } = useTranslation();
  const [isModalOpenEdit, setIsModalOpenEdit] = useState<{ roomNumber: number; isOpen: boolean }>({
    roomNumber: 0,
    isOpen: false,
  });
  const roomNameLabel = `${roomLabel} ${index + 1}`;
  const { [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT } = useFeatureToggle();
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;
  const price = formatPriceWithDecimal(
    language,
    formatCurrency(currencyCode),
    roomStay.roomPrice,
    true
  );
  const groupId = roomStay?.roomExtraInfo?.groupId as keyof RoomCodes;
  const roomDetailsEdit = {
    adults: roomStay?.adultsNumber,
    children: roomStay?.childrenNumber,
    roomType:
      roomDropdownRoomCodes[groupId] === ACCESSIBLE_ROOM_TYPE && isBarrierFreeLabelEnabled
        ? t('accessible.room')?.split(' ')[0]
        : roomStay?.roomExtraInfo?.roomName.split(' ')[0],
    operaRoomType: roomStay?.roomExtraInfo?.roomType,
    roomTypeCode: roomDropdownRoomCodes[groupId],
  };
  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, userDetails);

  return (
    <Flex data-testid={`room-info-card-amend-${index + 1}`} {...cardStyles}>
      <Text {...roomIndexStyles}>{roomNameLabel}</Text>
      <Text {...roomDetailsStyles} className="sessioncamhidetext assist-no-show">{`${upperOnlyFirst(
        nameTitle || ''
      )} ${givenName} ${surName}`}</Text>
      <Text {...roomDetailsStyles} pb="0">
        {getGuestsPlaceholderString(
          roomStay.adultsNumber,
          roomStay.childrenNumber,
          labels.roomModalLabels.roomAvailabilityLabels
        )}
      </Text>
      <Text {...roomDetailsStyles}>{roomStay?.roomExtraInfo?.roomName}</Text>
      <Text {...priceDetailsStyles}>{price}</Text>
      <Button
        size="xsm"
        variant="secondary"
        marginTop="auto"
        data-testid={`room-info-card-amend-edit-button-${index + 1}`}
        {...amendButtonsStyles}
        onClick={(e?: React.MouseEvent<HTMLElement>) => {
          setIsModalOpenEdit({ roomNumber: index, isOpen: true });
          e?.currentTarget.blur();
        }}
        isDisabled={variant === Area.CCUI ? !isAmendable : !isCancellable}
      >
        {edit}
      </Button>
      <RoomModal
        isOpen={isModalOpenEdit.isOpen}
        onClose={() => setIsModalOpenEdit({ roomNumber: index, isOpen: false })}
        title={roomNameLabel}
        roomRules={roomRules}
        hotelAvailabilityParams={hotelAvailabilityParams}
        labels={labels.roomModalLabels}
        baseDataTestId={baseDataTestId}
        isEdit={true}
        roomDetailsEdit={roomDetailsEdit}
        price={price}
        reservationGuestList={reservationGuestList[0]}
        roomNumber={index + 1}
        onUpdateRoom={onUpdateRoom}
        reservationId={reservationId}
        variant={variant}
        brand={brand}
        channel={channel}
        hotelCountry={hotelCountry}
        language={language}
        userDetails={userDetails}
        isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
      />
      {reservationsNumber > 1 &&
        userData?.business?.accessLevel !== BUSINESS_BOOKER_USER_ROLES.SELF && (
          <Button
            size="xsm"
            variant={isCancellable ? 'tertiary' : 'primary'}
            data-testid={`room-info-card-amend-remove-button-${index + 1}`}
            marginTop=".5rem"
            transition="none"
            {...amendButtonsStyles}
            onClick={(e?: React.MouseEvent<HTMLElement>) => {
              setIsModalOpen(true);
              e?.currentTarget.blur();
            }}
            isDisabled={!isCancellable}
          >
            {remove}
          </Button>
        )}
      <RemoveRoomModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        labels={labels.removeRoomModalLabels}
        roomNumber={index + 1}
        onRemoveRoom={onRemoveRoom}
        reservationId={reservationId}
      />
    </Flex>
  );
}

const wrapperStyles = {
  w: {
    mobile: '100%',
    sm: '14.8125rem',
    md: '20.4375rem',
    lg: '11.125rem',
    xl: '12rem',
  },
  h: '100%',
  minHeight: {
    base: '15.25rem',
    sm: '16.75rem',
    lg: '18.25rem',
    xl: '16.75rem',
  },
  border: '1px solid var(--chakra-colors-tertiary)',
  borderRadius: 'var(--chakra-space-xs)',
  bg: 'baseWhite',
  padding: 'var(--chakra-space-xl) 0.875rem',
  flexDirection: 'column',
} as FlexProps;

const roomIndexStyles = {
  color: 'darkGrey2',
  fontSize: 'md',
  fontWeight: 'bold',
  lineHeight: 'var(--chakra-lineHeights-2)',
  paddingBottom: 'sm',
};

const roomDetailsStyles = {
  color: 'darkGrey2',
  fontSize: 'md',
  lineHeight: 'var(--chakra-lineHeights-3)',
  fontWeight: 'normal',
  paddingBottom: 'sm',
};

const priceDetailsStyles = {
  ...roomDetailsStyles,
  color: 'darkGrey1',
};

const amendButtonsStyles = {
  minW: {
    mobile: '12.9375rem',
    xs: '16.375rem',
    sm: '12.875rem',
    md: '18.5rem',
    lg: '9.125rem',
    xl: '10rem',
  },
  w: '100%',
};
