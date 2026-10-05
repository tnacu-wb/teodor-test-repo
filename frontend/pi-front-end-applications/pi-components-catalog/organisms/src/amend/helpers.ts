import { QueryClient } from '@tanstack/react-query';
import {
  CHECK_BASKET_STATUS,
  BookingConfirmationType,
  AmendReservation,
  Channel,
} from '@whitbread-eos/api';
import { graphQLRequest, isSameDate, PromotionsInformation } from '@whitbread-eos/utils';

export function isStayDatesSectionUpdated(
  originalArrival: string,
  originalDeparture: string,
  newArrival: string,
  newDeparture: string
) {
  const isSameArrivalDate = isSameDate(new Date(originalArrival), new Date(newArrival));
  const isSameDepartureDate = isSameDate(new Date(originalDeparture), new Date(newDeparture));

  return !isSameArrivalDate || !isSameDepartureDate;
}

export async function getBasketStatus(queryClient: QueryClient, basketReference: string) {
  try {
    const result = await queryClient.fetchQuery({
      queryKey: ['BasketStatus', basketReference],
      queryFn: () =>
        graphQLRequest(CHECK_BASKET_STATUS, {
          basketReference: basketReference,
        }),
      ...{
        gcTime: 0,
        staleTime: 0,
      },
    });

    return result.basketStatus;
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

export type NullableString = string | null | undefined;

export function valuesAreEqual(value1: NullableString, value2: NullableString): boolean {
  return (
    value1 === value2 || (value1 === '' && value2 === null) || (value1 === null && value2 === '')
  );
}

export type ExtendedBookingConfirmationType = BookingConfirmationType & {
  reservationByIdList: (BookingConfirmationType['reservationByIdList'][number] & {
    originalReservationId?: string;
  })[];
};

export type ExtendedAmendReservation = AmendReservation & {
  originalReservationId?: string;
};

export function getRemovedReservations(
  tempData: BookingConfirmationType,
  tempDataRef: ExtendedBookingConfirmationType,
  isMobilePreRegisteredRepurposeEnabled: boolean
) {
  const tempDataIds = new Set(
    tempData.reservationByIdList.map((reservation) => reservation.reservationId)
  );
  const removedReservations = tempDataRef.reservationByIdList
    .filter(
      (reservation) =>
        !tempDataIds.has(reservation.reservationId) &&
        (isMobilePreRegisteredRepurposeEnabled
          ? reservation.deRegCardCompleted === true
          : reservation.preCheckInStatus === true)
    )
    .map((reservation: ExtendedAmendReservation) => reservation.originalReservationId);

  return removedReservations;
}

export function getDateChangedReservationIds(
  tempDataRef: BookingConfirmationType,
  tempData: BookingConfirmationType
) {
  return tempDataRef?.reservationByIdList?.some((tempReservationRef) => {
    return tempData.reservationByIdList?.some((tempReservation) => {
      return (
        tempReservation?.reservationId === tempReservationRef?.reservationId &&
        (tempReservation?.roomStay?.arrivalDate !== tempReservationRef?.roomStay?.arrivalDate ||
          tempReservation?.roomStay?.departureDate !== tempReservationRef?.roomStay?.departureDate)
      );
    });
  });
}

export function getGuestListChanged(
  tempReservation: AmendReservation,
  tempReservationRef: AmendReservation
) {
  return tempReservation?.reservationGuestList?.some((tempGuest: any, index: number) => {
    const tempGuestRef = tempReservationRef?.reservationGuestList[index];
    return (
      !tempGuestRef ||
      !valuesAreEqual(tempGuest?.givenName, tempGuestRef?.givenName) ||
      !valuesAreEqual(tempGuest?.surName, tempGuestRef?.surName) ||
      !valuesAreEqual(tempGuest?.nameTitle, tempGuestRef?.nameTitle) ||
      !valuesAreEqual(tempGuest?.email, tempGuestRef?.email) ||
      !valuesAreEqual(tempGuest?.address?.addressLine1, tempGuestRef?.address?.addressLine1) ||
      !valuesAreEqual(tempGuest?.address?.addressLine2, tempGuestRef?.address?.addressLine2) ||
      !valuesAreEqual(tempGuest?.address?.addressLine3, tempGuestRef?.address?.addressLine3) ||
      !valuesAreEqual(tempGuest?.address?.cityName, tempGuestRef?.address?.cityName) ||
      !valuesAreEqual(tempGuest?.address?.postalCode, tempGuestRef?.address?.postalCode) ||
      !valuesAreEqual(tempGuest?.address?.countryCode, tempGuestRef?.address?.countryCode)
    );
  });
}

export function getRoomStayChanged(
  tempReservationRef: AmendReservation,
  tempReservation: AmendReservation
) {
  return (
    tempReservation?.roomStay?.adultsNumber !== tempReservationRef?.roomStay?.adultsNumber ||
    tempReservation?.roomStay?.childrenNumber !== tempReservationRef?.roomStay?.childrenNumber
  );
}

export function getChangedPreCheckedInReservations(
  tempDataRef: ExtendedBookingConfirmationType,
  tempData: BookingConfirmationType,
  isMobilePreRegisteredRepurposeEnabled: boolean
) {
  const dateChanged = getDateChangedReservationIds(tempDataRef, tempData);
  if (dateChanged) {
    const statusKey = isMobilePreRegisteredRepurposeEnabled
      ? 'deRegCardCompleted'
      : 'preCheckInStatus';

    return tempDataRef?.reservationByIdList
      .filter((reservation) => reservation?.[statusKey])
      .map((reservation: ExtendedAmendReservation) => reservation?.originalReservationId);
  }

  const changedReservations: string[] = [];
  const removedReservations = getRemovedReservations(
    tempData,
    tempDataRef,
    isMobilePreRegisteredRepurposeEnabled
  );

  tempData?.reservationByIdList.forEach((tempReservation) => {
    const tempReservationRef: any = tempDataRef?.reservationByIdList.find(
      (reservationRef) => reservationRef?.reservationId === tempReservation?.reservationId
    );

    if (!tempReservationRef) {
      return;
    }

    const guestListChanged = getGuestListChanged(tempReservation, tempReservationRef);
    const roomStayChanged = getRoomStayChanged(tempReservationRef, tempReservation);
    if (guestListChanged || roomStayChanged) {
      isMobilePreRegisteredRepurposeEnabled
        ? tempReservationRef?.deRegCardCompleted &&
          changedReservations.push(tempReservationRef?.originalReservationId)
        : tempReservationRef?.preCheckInStatus &&
          changedReservations.push(tempReservationRef?.originalReservationId);
    }
  });
  return [...changedReservations, ...removedReservations];
}

export function shouldAmendStayDates(
  isPromoCodeLandingPageEnabled: boolean,
  isStayDatesLoading: boolean,
  amendStayDatesIsSuccess: boolean
): boolean {
  return !isPromoCodeLandingPageEnabled && !isStayDatesLoading && amendStayDatesIsSuccess;
}

export function showNotificationNoPromotion(
  showPromoNotification: boolean,
  isStayDatesLoading: boolean,
  amendStayDatesIsSuccess: boolean
): boolean {
  return !showPromoNotification && !isStayDatesLoading && amendStayDatesIsSuccess;
}

export function shouldShowAmendStayDatesError(
  isWithinPromoWindow: boolean | null | undefined,
  isStayDatesLoading: boolean,
  amendStayDatesIsError: boolean
): boolean {
  return Boolean(isWithinPromoWindow) && !isStayDatesLoading && amendStayDatesIsError;
}

export function shouldShowAmendStayDatesErrorNoPromo(
  isPromoCodeLandingPageEnabled: boolean,
  promotionCode: string | null | undefined,
  isStayDatesLoading: boolean,
  amendStayDatesIsError: boolean
): boolean {
  return (
    (!isPromoCodeLandingPageEnabled || !promotionCode) &&
    !isStayDatesLoading &&
    amendStayDatesIsError
  );
}
export const getPromoCondition = ({
  isPromoCodeLandingPageEnabled,
  amendEditRoomIsSuccess,
  removeRoomIsSuccess,
  addNewRoomIsSuccess,
  mealsSectionHasUpdates,
  promoStayData,
}: {
  isPromoCodeLandingPageEnabled: boolean;
  amendEditRoomIsSuccess: boolean;
  removeRoomIsSuccess: boolean;
  addNewRoomIsSuccess: boolean;
  mealsSectionHasUpdates: boolean;
  promoStayData: PromotionsInformation | null;
}) => {
  if (!isPromoCodeLandingPageEnabled) return true;

  if (amendEditRoomIsSuccess || removeRoomIsSuccess || addNewRoomIsSuccess) {
    return true;
  }

  if (!mealsSectionHasUpdates) {
    return promoStayData?.isWithinPromoWindow ?? true;
  }

  return true;
};
export const handleEditRemoveRoomReset = ({
  isPromoCodeLandingPageEnabled,
  amendEditRoomMutation,
  removeRoomMutation,
}: {
  isPromoCodeLandingPageEnabled: boolean;
  amendEditRoomMutation: { reset: () => void };
  removeRoomMutation: { reset: () => void };
}) => {
  if (!isPromoCodeLandingPageEnabled) {
    amendEditRoomMutation.reset();
    removeRoomMutation.reset();
  }
};
export function getIsPromoCodeLandingPageEnabled(
  channel: Channel,
  hasPiPromoCodeLandingPageEnabled: boolean,
  hasCcuiPromoCodeLandingPageEnabled: boolean,
  hasBbPromoCodeLandingPageEnabled: boolean
): boolean {
  switch (channel) {
    case Channel.Pi:
      return hasPiPromoCodeLandingPageEnabled;
    case Channel.Ccui:
      return hasCcuiPromoCodeLandingPageEnabled;
    case Channel.Bb:
      return hasBbPromoCodeLandingPageEnabled;
    default:
      return false;
  }
}
