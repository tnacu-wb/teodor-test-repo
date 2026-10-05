import { Box, Flex, FlexProps, Text } from '@chakra-ui/react';
import { ExtrasPackages, RoomPackageSelection, RoomsExpanded } from '@whitbread-eos/api';
import { formatCurrency, formatPrice } from '@whitbread-eos/utils';
import React, { Dispatch, SetStateAction, useCallback, useEffect, useState } from 'react';

import RoomCardHeader from './RoomCardHeader.component';
import RoomCardInfo from './RoomCardInfo.component';

interface Props extends FlexProps {
  room: {
    roomReservationStartDate: string;
    roomReservationEndDate: string;
    leadGuestTitle: string;
    leadGuestName: string;
    roomType: string;
    roomTypeDescription: string | null;
    rateType: string;
    rateTypeDescription: string;
    roomGroup: string;
    roomTotalPrice: number;
    ratesPerNight: { pricePerNight: number; startDate: string; cityTaxPerNight: number }[];
    adultMealDescription: string[];
    childrenMealDescription: string[];
    mealPrice: number;
    extrasRoomSelection?: RoomPackageSelection[];
    packagesExtrasItems?: ExtrasPackages[];
  };
  currency: string;
  roomNumber: number;
  t: (x: string, y?: { [key: string]: string }) => string;
  currentLang: string | undefined;
  taxesMessage?: string;
  brand?: string;
  isCardExpanded: boolean;
  cardsExpanded: RoomsExpanded[];
  setCardsExpanded: Dispatch<SetStateAction<RoomsExpanded[]>>;
  handleExpandCollapse: (roomNumber?: number) => void;
}

export default function RoomCard({
  room,
  roomNumber,
  currency,
  t,
  currentLang,
  taxesMessage,
  brand,
  isCardExpanded,
  cardsExpanded,
  setCardsExpanded,
  handleExpandCollapse,
}: Readonly<Props>) {
  const {
    roomReservationStartDate,
    roomReservationEndDate,
    leadGuestTitle,
    leadGuestName,
    roomType,
    roomTypeDescription,
    rateType,
    rateTypeDescription,
    roomGroup,
    roomTotalPrice,
    ratesPerNight,
    adultMealDescription,
    childrenMealDescription,
    mealPrice,
    extrasRoomSelection,
    packagesExtrasItems,
  } = room;

  const [showRoomCardInfo, setShowRoomCardInfo] = useState(false);

  const createCardsArray = useCallback(() => {
    if (cardsExpanded.length === 0) {
      setCardsExpanded((prevState: RoomsExpanded[]) => [
        ...prevState,
        { roomNumber: roomNumber, expanded: showRoomCardInfo },
      ]);
    }
  }, [cardsExpanded.length]);

  useEffect(() => {
    setShowRoomCardInfo(isCardExpanded);
  }, [isCardExpanded]);

  useEffect(() => {
    createCardsArray();
  }, [createCardsArray]);

  const roomCardHeaderData = {
    roomReservationStartDate,
    roomReservationEndDate,
    roomNumber,
    roomTotalPrice,
    currency,
    currentLang,
    t,
    showRoomCardInfo,
    handleExpandCollapse,
  };
  const roomCardInfoData = {
    leadGuestTitle,
    leadGuestName,
    roomType,
    roomTypeDescription,
    rateType,
    rateTypeDescription,
    roomGroup,
    roomReservationEndDate,
    roomTotalPrice,
    roomNumber,
    ratesPerNight,
    adultMealDescription,
    childrenMealDescription,
    mealPrice,
    extrasRoomSelection,
    packagesExtrasItems,
    taxesMessage,
    brand,
    currency,
    currentLang,
    t,
  };

  return (
    <>
      <Box {...roomCardStyle} sx={{ '@media print': { display: 'none' } }}>
        <Flex {...roomCardDetailsStyle}>
          <RoomCardHeader {...roomCardHeaderData} />
          {showRoomCardInfo && <RoomCardInfo {...roomCardInfoData} />}
        </Flex>
      </Box>
      <Box display="none" sx={{ '@media print': { display: 'block', my: 'md' } }}>
        <Text mb="sm" fontWeight="bold">
          {t('booking.confirmation.room').replace('[roomNumber]', roomNumber.toString())}
        </Text>
        <Text fontWeight="bold">
          {leadGuestTitle} {leadGuestName}
        </Text>
        <Text>
          {roomGroup}
          {/* ({t('booking.confirmation.cot')}) */}
        </Text>
        <Flex justifyContent="space-between" fontSize="md">
          <Text>{roomType}</Text>
          <Text fontWeight="bold">
            {formatPrice(formatCurrency(currency), room?.roomTotalPrice.toFixed(2), currentLang)}
          </Text>
        </Flex>
      </Box>
    </>
  );
}

const roomCardStyle = {
  mb: 'md',
  w: 'full',
  border: '1px solid',
  borderColor: 'lightGrey3',
  borderRadius: '3px',
};

const roomCardDetailsStyle = {
  direction: 'column',
  w: 'full',
} as FlexProps;
