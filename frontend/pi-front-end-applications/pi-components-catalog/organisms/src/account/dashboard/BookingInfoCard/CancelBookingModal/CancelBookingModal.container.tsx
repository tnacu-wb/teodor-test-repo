import { useQueryClient } from '@tanstack/react-query';
import type { BookingConfirmation } from '@whitbread-eos/api';
import { Area, DASHBOARD_CANCEL_RESERVATION } from '@whitbread-eos/api';
import {
  analytics,
  formatDate,
  getFindBookingToken,
  getNightsNumber,
  isStringValid,
  useCustomLocale,
  useMutationRequest,
  useAuthToken,
  useLoggedOrCCUI,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import type { Dispatch, SetStateAction } from 'react';
import { useCallback, useEffect, useState } from 'react';

import { fetchBookingConfirmation, fetchHotelInformation } from '../../fetchBookingConfirmation';
import CancelBookingModal from './CancelBookingModal.component';

interface Props {
  isModalVisible: boolean;
  onModalClose: Dispatch<SetStateAction<boolean>>;
  refetchManageBooking: any;
  basketReference: string;
  area?: Area;
  bookingReference?: string;
}

export default function CancelBookingModalContainer({
  isModalVisible,
  onModalClose,
  refetchManageBooking,
  basketReference,
  bookingReference,
  area,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const { token } = useAuthToken();
  const [bookingInformation, setBookingInformation] = useState<BookingConfirmation | null>(null);
  const [hotelName, setHotelName] = useState<string>('');
  const [isError, setIsError] = useState(false);

  const [error, setError] = useState<unknown>(null);
  const [cancelReservationResponse, setCancelReservationResponse] = useState<
    string | undefined | null
  >(undefined);
  const loggedOrCCUI = useLoggedOrCCUI(area);
  const ANALYTICS_TRACKING_DASH_CANCEL = 'dashCancel';

  const [disabledCancelButton, setDisabledCancelButton] = useState<boolean>(false);

  const {
    mutation: cancelReservationMutation,
    isError: cancelReservationIsError,
    isSuccess: cancelReservationIsSuccess,
    data: cancelReservationData,
  } = useMutationRequest(DASHBOARD_CANCEL_RESERVATION);

  useEffect(() => {
    if (cancelReservationIsError) {
      setCancelReservationResponse(null);
    }
  }, [cancelReservationIsError]);

  const cancelBooking = useCallback(() => {
    setDisabledCancelButton(!disabledCancelButton);
    cancelReservationMutation.mutate({
      cancellationCriteria: {
        basketReference: basketReference,
        hotelId: bookingInformation ? bookingInformation.hotelId : '',
        reservationOverrideReason:
          bookingInformation?.reservationByIdList[0].reservationOverrideReasons,
        token: getFindBookingToken().token,
      },
    });
  }, [basketReference, bookingInformation]);

  useEffect(() => {
    if (cancelReservationIsSuccess) {
      updateAnalyticsDashboard(cancelReservationData.cancelReservation.basketReference);
      setCancelReservationResponse(cancelReservationData.cancelReservation.basketReference);
      refetchManageBooking();
    }
  }, [cancelReservationIsSuccess]);

  const queryClient = useQueryClient();
  const getData = useCallback(async (basketReference: string) => {
    try {
      const bookingData = await fetchBookingConfirmation({
        queryClient,
        loggedOrCCUI,
        bookingReference,
        basketReference,
        language,
        country,
        area,
        token,
      });

      setBookingInformation(bookingData);
      const hotelId = bookingData?.hotelId;

      const hotelData = await fetchHotelInformation(queryClient, hotelId, language, country);
      setHotelName(hotelData?.hotelInformation?.name);
      setIsError(false);
    } catch (error) {
      setError(error);
      setIsError(true);
    }
  }, []);

  useEffect(() => {
    if (isStringValid(basketReference)) {
      getData(basketReference);
    }
  }, [basketReference]);

  const onClickKeepBooking = useCallback(() => onModalClose(false), []);

  const firstRoom = bookingInformation?.reservationByIdList[0];

  const bookedFor = firstRoom
    ? `${firstRoom?.reservationGuestList[0].givenName} ${firstRoom.reservationGuestList[0].surName}`
    : '';

  const arrivalDate = firstRoom
    ? formatDate(firstRoom?.roomStay?.arrivalDate, 'EEEE d MMMM yyyy', language)
    : '';

  const noNights = firstRoom
    ? getNightsNumber(firstRoom?.roomStay?.arrivalDate, firstRoom?.roomStay?.departureDate)
    : 0;

  const bookedBy = firstRoom
    ? `${firstRoom?.billing?.firstName} ${firstRoom?.billing?.lastName}`
    : '';

  function updateAnalyticsDashboard(basketReference: any) {
    window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_DASH_CANCEL);
    analytics.update({
      dashboard: {
        ...(window?.analyticsData?.dashboard ?? {}),
        cancelBookingID: bookingReference ?? basketReference,
        cancelNights: noNights,
        cancelRooms: bookingInformation ? bookingInformation.reservationByIdList.length : 0,
      },
    });
  }
  return (
    <CancelBookingModal
      cancelReservationData={cancelReservationResponse}
      onClickKeepBooking={onClickKeepBooking}
      bookedFor={bookedFor}
      arrivalDate={arrivalDate}
      noNights={noNights}
      hotelName={hotelName}
      isError={isError}
      error={error}
      isModalVisible={isModalVisible}
      onModalClose={() => onModalClose(false)}
      onClickCancelBooking={cancelBooking}
      area={area}
      bookedBy={bookedBy}
      backBtnText={t('amend.anonymousBackButtonText')}
      backBtnUrl="home.html"
      bookingReference={bookingReference}
      isCancelDisabled={disabledCancelButton}
    />
  );
}
