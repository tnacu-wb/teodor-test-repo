import { BoxProps, Flex, Heading } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import {
  AmendHotelAvailabilityData,
  AmendRoomsAndGuestsLabels,
  AmendRoomType,
  Area,
  Channel,
  Customer,
  SearchRoomOccupancyLimitationsType,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import { getGQLClient, PromotionsInformation, useFeatureToggle } from '@whitbread-eos/utils';

import RoomModal from '../RoomModal';
import { getAmendPromotionsInfo } from '../utilities';

interface Props {
  styles?: { roomWrapper: BoxProps };
  roomRules: SearchRoomOccupancyLimitationsType;
  labels: AmendRoomsAndGuestsLabels;
  language: string;
  baseDataTestId: string;
  hotelAvailabilityParams: AmendHotelAvailabilityData;
  onSaveNewRoom: (room: AmendRoomType) => void;
  variant: Area;
  brand: string;
  channel: Channel;
  hotelCountry: string;
  userDetails?: Customer;
  setPromoRoomsData: (data: PromotionsInformation | null) => void;
  isModalOpen: boolean;
  openModal: () => void;
  closeModal: () => void;
  isPromoCodeLandingPageEnabled: boolean;
  isAddRoomActionInProgress: boolean;
  setIsAddRoomActionInProgress: (isLoading: boolean) => void;
}

export default function AddRoomCard({
  styles,
  roomRules,
  labels,
  language,
  baseDataTestId,
  hotelAvailabilityParams,
  onSaveNewRoom,
  variant,
  brand,
  channel,
  hotelCountry,
  userDetails,
  setPromoRoomsData,
  isModalOpen,
  openModal,
  closeModal,
  isPromoCodeLandingPageEnabled,
  isAddRoomActionInProgress,
  setIsAddRoomActionInProgress,
}: Readonly<Props>) {
  const {
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const addARoomLabel = labels.roomModalLabels.roomAvailabilityLabels.addRoom;
  const cardStyles = { ...cardContainerStyles, ...styles?.roomWrapper };
  const queryClient = useQueryClient();
  const client = getGQLClient();

  const handleIsModalOpen = async () => {
    if (isAddRoomActionInProgress || isModalOpen) return;
    setIsAddRoomActionInProgress(true);

    try {
      const resAmendPromotionsInfo = await getAmendPromotionsInfo({
        isPromotionsInHotelAvailabilityEnabled,
        isPromoCodeLandingPageEnabled,
        channel,
        hotelId: hotelAvailabilityParams.hotelId,
        arrival: hotelAvailabilityParams.arrival,
        departure: hotelAvailabilityParams.departure,
        country: hotelAvailabilityParams.country,
        language,
        brand,
        rooms:
          hotelAvailabilityParams.rooms && hotelAvailabilityParams.rooms.length > 0
            ? hotelAvailabilityParams.rooms
            : [
                {
                  adultsNumber: 1,
                  childrenNumber: 0,
                  cotRequired: false,
                  roomType: '',
                },
              ],
        originalBasketReference: hotelAvailabilityParams.originalBasketReference ?? '',
        queryClient,
        client,
        ratePlanCodes: hotelAvailabilityParams.ratePlanCodes,
        isPromoBox: false,
      });

      setPromoRoomsData(resAmendPromotionsInfo);

      if (
        !resAmendPromotionsInfo?.showPromo &&
        !resAmendPromotionsInfo?.promoBookingInfo?.promotionCode
      ) {
        openModal();
      }
    } catch (e) {
      console.log(e);
    } finally {
      setIsAddRoomActionInProgress(false);
    }
  };

  return (
    <>
      <Flex {...cardStyles} data-testid="add-room-card-amend" onClick={handleIsModalOpen}>
        <Heading as="h3" textAlign="center" style={cardTextStyles}>
          {`+ ${addARoomLabel}`}
        </Heading>
        <RoomModal
          isOpen={isModalOpen}
          onClose={closeModal}
          title={addARoomLabel}
          roomRules={roomRules}
          labels={labels.roomModalLabels}
          baseDataTestId={baseDataTestId}
          hotelAvailabilityParams={hotelAvailabilityParams}
          onSaveNewRoom={onSaveNewRoom}
          variant={variant}
          brand={brand}
          channel={channel}
          hotelCountry={hotelCountry}
          language={language}
          userDetails={userDetails}
          isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
        />
      </Flex>
    </>
  );
}

const cardContainerStyles = {
  cursor: 'pointer',
  alignItems: 'center',
  justifyContent: 'center',
  border: '1px dashed',
  borderRadius: 'var(--chakra-space-1)',
  borderColor: 'var(--chakra-colors-tertiary)',
  w: {
    mobile: '100%',
    sm: '14.8125rem',
    md: '20.4375rem',
    lg: '11.25rem',
    xl: '12.125rem',
  },
  minHeight: {
    base: '15.25rem',
    sm: '16.75rem',
    lg: '18.25rem',
    xl: '16.75rem',
  },
  h: '100%',
};

const cardTextStyles = {
  color: 'var(--chakra-colors-tertiary)',
  fontSize: 'var(--chakra-fontSizes-lg)',
  lineHeight: 'var(--chakra-lineHeights-3)',
  fontWeight: 'var(--chakra-fontWeights-semibold)',
};
