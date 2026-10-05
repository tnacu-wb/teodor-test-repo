import type { Area, BookingChannelCriteria } from '@whitbread-eos/api';
import { BOOKING_HISTORY_CANCEL_BOOKING } from '@whitbread-eos/api';
import {
  analytics,
  formatDate,
  useCustomLocale,
  useMutationRequest,
  useAuthToken,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import type { Dispatch, SetStateAction } from 'react';
import { useCallback, useEffect, useState } from 'react';

import CancelBookingModal from './CancelBookingModal.component';

export interface Props {
  isModalVisible: boolean;
  onModalClose: Dispatch<SetStateAction<boolean>>;
  refetchManageBooking: any;
  basketReference: string | null;
  bookingReference: string;
  area?: Area;
  hotelName: string;
  bookedFor: string;
  arrivalDate: string;
  noOfRooms?: number;
  noNights: number;
  hotelId: string;
  bookedBy: string;
  bookingChannel: BookingChannelCriteria;
}

export default function BookingHistoryCancelBookingModalContainer({
  isModalVisible,
  onModalClose,
  refetchManageBooking,
  basketReference,
  bookingReference,
  area,
  hotelName,
  bookedFor,
  arrivalDate,
  noOfRooms,
  noNights,
  hotelId,
  bookedBy,
  bookingChannel,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const ANALYTICS_TRACKING_DASH_CANCEL = 'dashCancel';

  const [cancelBookingResponse, setCancelBookingResponse] = useState<string | undefined | null>(
    undefined
  );

  const [disabledCancelButton, setDisabledCancelButton] = useState<boolean>(false);

  const { token } = useAuthToken();

  const {
    mutation: cancelBookingMutation,
    isError: cancelBookingIsError,
    error: cancelBookingError,
    data: cancelBookingData,
  } = useMutationRequest(BOOKING_HISTORY_CANCEL_BOOKING, false, token);

  useEffect(() => {
    if (cancelBookingIsError) {
      setCancelBookingResponse(null);
    }
  }, [cancelBookingIsError]);

  const cancelBooking = () => {
    setDisabledCancelButton(!disabledCancelButton);
    cancelBookingMutation.mutate({
      basketReference: basketReference,
      bookingReference: bookingReference,
      hotelId: hotelId,
      arrivalDate: arrivalDate,
      country: country,
      language: language,
      bookingChannel: bookingChannel,
    });
  };

  useEffect(() => {
    if (cancelBookingData) {
      if (cancelBookingData.cancelBooking.cancellationId) {
        setCancelBookingResponse(cancelBookingData.cancelBooking.cancellationId);
      } else if (cancelBookingData.cancelBooking.bookingReference) {
        setCancelBookingResponse(cancelBookingData.cancelBooking.bookingReference);
      }
      refetchManageBooking();
      updateAnalyticsDashboard(bookingReference);
    }
  }, [cancelBookingData]);

  function updateAnalyticsDashboard(bookingReference: any) {
    window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_DASH_CANCEL);
    analytics.update({
      dashboard: {
        ...(window?.analyticsData?.dashboard ?? {}),
        cancelBookingID: bookingReference,
        cancelNights: noNights,
        cancelRooms: noOfRooms,
      },
    });
  }

  const onClickKeepBooking = useCallback(() => onModalClose(false), []);

  return (
    <CancelBookingModal
      bookingReference={''}
      cancelReservationData={cancelBookingResponse}
      isError={cancelBookingIsError}
      error={cancelBookingError}
      onClickKeepBooking={onClickKeepBooking}
      hotelName={hotelName}
      bookedFor={bookedFor}
      arrivalDate={formatDate(arrivalDate, 'EEEE d MMMM yyyy', language)}
      noNights={noNights}
      isModalVisible={isModalVisible}
      onModalClose={() => onModalClose(false)}
      onClickCancelBooking={cancelBooking}
      area={area}
      backBtnText={t('amend.backButtonText')}
      onClickBack={onClickKeepBooking}
      bookedBy={bookedBy}
      isCancelDisabled={disabledCancelButton}
    />
  );
}
