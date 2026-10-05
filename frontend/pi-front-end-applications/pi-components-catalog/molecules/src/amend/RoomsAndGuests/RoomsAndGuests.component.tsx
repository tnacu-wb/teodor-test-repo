import { Box } from '@chakra-ui/react';
import {
  AmendHotelAvailabilityData,
  AmendReservation,
  AmendRoomsAndGuestsData,
  AmendRoomsAndGuestsLabels,
  AmendRoomType,
  Area,
  BUSINESS_BOOKER_USER_ROLES,
  Channel,
  Customer,
  SearchRoomOccupancyLimitationsType,
} from '@whitbread-eos/api';
import { PromotionsInformation, useUserData, useUserDetails } from '@whitbread-eos/utils';
import { useState } from 'react';

import AddRoomCard from '../AddRoomCard';
import RoomInfoCard from '../RoomInfoCard';
import { renderPromoNotification } from '../utilities/helpers';

interface Props {
  baseDataTestId: string;
  data: AmendRoomsAndGuestsData;
  roomRules: SearchRoomOccupancyLimitationsType;
  labels: AmendRoomsAndGuestsLabels;
  language: string;
  hotelAvailabilityParams: AmendHotelAvailabilityData;
  onSaveNewRoom: (room: AmendRoomType) => void;
  onUpdateRoom: (selectedRoom: AmendRoomType, reservationId: string, roomNumber: number) => void;
  onRemoveRoom: (reservationId: string, roomNumber: number) => void;
  maxRooms: number;
  variant: Area;
  isCancellable: boolean;
  brand: string;
  channel: Channel;
  hotelCountry: string;
  isAmendable: boolean;
  userDetails?: Customer;
  isPromoCodeLandingPageEnabled: boolean;
}

export default function RoomsAndGuests({
  baseDataTestId,
  data,
  labels,
  language,
  roomRules,
  hotelAvailabilityParams,
  onSaveNewRoom,
  onUpdateRoom,
  onRemoveRoom,
  maxRooms,
  variant,
  isCancellable,
  isAmendable,
  brand,
  channel,
  hotelCountry,
  userDetails,
  isPromoCodeLandingPageEnabled,
}: Readonly<Props>) {
  const prefixDataTestId = 'rooms-and-guests';
  const { reservations, currencyCode } = data;
  const roomStyles = { roomWrapper: roomCardStyles };
  const addRoomCardsLength = maxRooms - reservations.length;
  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, userDetails);
  const [promoRoomsData, setPromoRoomsData] = useState<PromotionsInformation | null>(null);
  const [openModalIndex, setOpenModalIndex] = useState<number | null>(null);
  const [isAddRoomActionInProgress, setIsAddRoomActionInProgress] = useState(false);

  return (
    <>
      {renderPromoNotification(promoRoomsData)}
      <Box {...wrapperStyles} data-testid={`${baseDataTestId}-${prefixDataTestId}-section`}>
        {renderReservationCards(reservations)}
        {(userData as any)?.business?.accessLevel !== BUSINESS_BOOKER_USER_ROLES.SELF &&
          isCancellable &&
          addRoomCardsLength > 0 &&
          renderAddRoomCards(addRoomCardsLength)}
      </Box>
    </>
  );

  function renderReservationCards(reservations: AmendReservation[]) {
    return reservations.map((reservation: AmendReservation, index) => (
      <RoomInfoCard
        key={`RoomCard-${reservation.reservationId}`}
        styles={roomStyles}
        data={reservation}
        index={index}
        labels={labels}
        currencyCode={currencyCode}
        language={language}
        reservationsNumber={reservations.length}
        baseDataTestId={baseDataTestId}
        hotelAvailabilityParams={hotelAvailabilityParams}
        roomRules={roomRules}
        onUpdateRoom={onUpdateRoom}
        onRemoveRoom={onRemoveRoom}
        reservationId={reservation.reservationId}
        variant={variant}
        isCancellable={isCancellable}
        isAmendable={isAmendable}
        brand={brand}
        channel={channel}
        hotelCountry={hotelCountry}
        userDetails={userDetails}
        isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
      />
    ));
  }

  function renderAddRoomCards(n: number) {
    return [...Array(n)].map((_, index) => (
      <AddRoomCard
        key={`AddRoomCard-${index}`}
        data-testid={`${baseDataTestId}-add-room-card`}
        styles={roomStyles}
        roomRules={roomRules}
        labels={labels}
        language={language}
        baseDataTestId={baseDataTestId}
        hotelAvailabilityParams={hotelAvailabilityParams}
        onSaveNewRoom={onSaveNewRoom}
        variant={variant}
        brand={brand}
        channel={channel}
        hotelCountry={hotelCountry}
        userDetails={userDetails}
        setPromoRoomsData={setPromoRoomsData}
        isModalOpen={openModalIndex === index}
        openModal={() => {
          openModalIndex === null && setOpenModalIndex(index);
        }}
        closeModal={() => setOpenModalIndex(null)}
        isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
        isAddRoomActionInProgress={isAddRoomActionInProgress}
        setIsAddRoomActionInProgress={setIsAddRoomActionInProgress}
      />
    ));
  }
}

const wrapperStyles = {
  px: 'var(--chakra-space-lg)',
  py: 'var(--chakra-space-2xl)',
  cursor: 'default',
  display: 'grid',
  gridTemplateColumns: {
    mobile: 'none',
    sm: 'repeat(2, 1fr)',
    lg: 'repeat(4, 1fr)',
  },
  gap: 'md',
};

const roomCardStyles = {
  _odd: {
    justifySelf: {
      mobile: 'center',
      sm: 'end',
      lg: 'center',
    },
    alignSelf: {
      mobile: 'center',
      sm: 'end',
      lg: 'center',
    },
  },
  _even: {
    justifySelf: {
      mobile: 'center',
      sm: 'start',
      lg: 'center',
    },
    alignSelf: {
      mobile: 'center',
      sm: 'start',
      lg: 'center',
    },
  },
};
