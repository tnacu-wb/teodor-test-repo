import {
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  BookingChannelCriteria,
  Channel,
  HIRoomType,
  OfferEnum,
  RoomReservation,
  PromoKind,
  SoftBundles,
  ExtrasId,
} from '@whitbread-eos/api';
import { useCustomLocale, useLocalStorage } from '@whitbread-eos/utils';

import ChooseRoomContinueBtnComponent from './ChooseRoomContinueBtn.component';

export interface ChooseRoomContinueBtnProps {
  dataTestId?: string;
  selectedPMSRoomTypes: string[];
  selectedSpecialRequests?: string[][];
  bookRsvIsLoading: boolean;
  bookRsvIsError: boolean;
  bookRsvError: unknown;
  handleBooking: (reservations: RoomReservation[], bookingChannel: BookingChannelCriteria) => void;
  channel: Channel;
  isDisabledContinueBtn?: boolean;
  softBundles?: SoftBundles;
}

export default function ChooseRoomContinueBtnContainer({
  dataTestId,
  selectedPMSRoomTypes,
  selectedSpecialRequests,
  bookRsvIsLoading,
  bookRsvIsError,
  bookRsvError,
  handleBooking,
  channel,
  isDisabledContinueBtn,
  softBundles,
}: Readonly<ChooseRoomContinueBtnProps>) {
  const { language } = useCustomLocale();

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  if (!basketDetailsState.hotelId) {
    return null;
  }

  const { hotelId, arrival, departure } = basketDetailsState;
  const ratePlanCode = setSelectedRatePlanCode();
  const reservations = basketDetailsState.selectedRate?.roomTypes?.map(
    (roomType: HIRoomType, roomTypeIndex: number) => {
      const selectedRoom = roomType?.rooms?.find(
        (room) => room?.pmsRoomType === selectedPMSRoomTypes?.[roomTypeIndex]
      );
      return {
        hotelId,
        arrival,
        departure,
        adultsNumber: roomType?.adults,
        childrenNumber: roomType?.children,
        cotRequired: selectedRoom?.cotAvailable,
        roomRates: {
          ratePlanCode,
          pmsRoomType: selectedPMSRoomTypes?.[roomTypeIndex],
          specialRequests: selectedSpecialRequests?.[roomTypeIndex],
          startDate: arrival,
          endDate: departure,
          promotionCode: basketDetailsState?.selectedRate?.promotionCode,
          promoKind: basketDetailsState?.selectedRate?.promoKind as PromoKind,
        },
        reservationPackages: [
          {
            startDate: arrival,
            endDate: departure,
            quantity: 1,
            packageCode: selectedRoom?.roomPriceBreakdown?.packageCode ?? '',
            unitPrice: selectedRoom?.roomPriceBreakdown?.packageAmount ?? 0,
          },
          ...(softBundles?.softBundleContent ?? []).map((bundle) => ({
            startDate: arrival,
            endDate: departure,
            quantity: Object.values(ExtrasId as unknown as string[]).includes(bundle.id ?? '')
              ? 1
              : roomType?.adults,
            packageCode: bundle.id ?? '',
            unitPrice: bundle.price ?? 0,
          })),
        ].filter((pkg) => pkg.packageCode !== ''),
      };
    }
  );

  return (
    <ChooseRoomContinueBtnComponent
      {...{
        dataTestId,
        onBookReservation,
        bookRsvIsLoading,
        bookRsvIsError,
        bookRsvError,
        isDisabledContinueBtn,
      }}
    />
  );

  function onBookReservation() {
    if (softBundles) {
      sessionStorage.setItem('softBundles', JSON.stringify(softBundles?.softBundleContent ?? []));
    }
    const bookingChannel = { channel, subchannel: 'WEB', language: language?.toUpperCase() };
    handleBooking(reservations, bookingChannel as BookingChannelCriteria);
  }

  function setSelectedRatePlanCode() {
    if (
      basketDetailsState.selectedRate &&
      basketDetailsState.selectedRate?.cellCode === OfferEnum.EMPLOYEE
    ) {
      return OfferEnum.EMPLOYEE_RATE_CODE;
    }
    return basketDetailsState.selectedRate?.ratePlanCode;
  }
}
