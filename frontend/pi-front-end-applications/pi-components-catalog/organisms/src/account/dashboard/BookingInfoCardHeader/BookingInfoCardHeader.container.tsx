import { BoxProps, Flex } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { Area, FT_PI_BOOKING_STATUS_IN_BIC_HEADER } from '@whitbread-eos/api';
import { Alert, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import {
  checkDateFormatter,
  isStringValid,
  useCustomLocale,
  mappingBookingStatus,
  useFeatureToggle,
  useAuthToken,
  useLoggedOrCCUI,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useCallback, useEffect, useMemo, useState } from 'react';

import { fetchBookingConfirmation, fetchHotelInformation } from '../fetchBookingConfirmation';
import ManageBookingCardHeader from './BookingInfoCardHeader.component';

interface BookingInformationHeader {
  hotelName: string;
  checkInTime: string;
  checkOutTime: string;
  arrivalDate: string;
  departureDate: string;
  reservationStatus?: string;
}

interface Props {
  bookingReference: string;
  basketReference: string | null;
  isAmendPage: boolean;
  area: Area;
}

export default function BookingInfoCardHeaderContainer({
  bookingReference,
  basketReference,
  isAmendPage,
  area,
}: Readonly<Props>) {
  const { [FT_PI_BOOKING_STATUS_IN_BIC_HEADER]: isBICHeaderBookingStatusEnabled } =
    useFeatureToggle();

  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const { language, country } = useCustomLocale();
  const { token } = useAuthToken();
  const loggedOrCCUI = useLoggedOrCCUI(area);
  const [bookingData, setBookingData] = useState<BookingInformationHeader>({
    hotelName: '',
    checkInTime: '',
    checkOutTime: '',
    arrivalDate: '',
    departureDate: '',
    reservationStatus: '',
  });
  const [bookingState, setBookingState] = useState({
    isLoading: false,
    error: null,
  });

  useEffect(() => {
    if (isAmendPage) {
      queryClient.invalidateQueries({
        queryKey: [
          loggedOrCCUI ? 'getBookingConfirmationAuthenticated' : 'getBookingConfirmation',
          loggedOrCCUI ? bookingReference : basketReference,
          language,
          country,
        ],
      });
    }
  }, [isAmendPage]);

  const getDetails = useCallback(
    async (basketReference: string | null, bookingReference: string) => {
      setBookingState({
        ...bookingState,
        isLoading: true,
      });

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

        const hotelId = bookingData?.hotelId;
        const { checkInTime, checkOutTime, arrivalDate, departureDate } =
          bookingData?.reservationByIdList[0]?.roomStay || {};
        const reservationStatus = bookingData?.reservationByIdList[0]?.reservationStatus;

        const hotelData = await fetchHotelInformation(queryClient, hotelId, language, country);
        setBookingData(() => ({
          checkInTime,
          checkOutTime,
          arrivalDate,
          departureDate,
          hotelName: hotelData?.hotelInformation?.name,
          reservationStatus,
        }));
        setBookingState({ ...bookingState, isLoading: false });
      } catch (error: any) {
        setBookingState({
          isLoading: false,
          error: error,
        });
      }
    },
    []
  );

  useEffect(() => {
    if (isStringValid(bookingReference) && isStringValid(basketReference)) {
      getDetails(basketReference, bookingReference);
    }
  }, [basketReference, bookingReference]);

  const checkInLabel =
    useMemo(() => {
      if (isStringValid(bookingData?.checkInTime) && isStringValid(bookingData?.arrivalDate)) {
        return checkDateFormatter(bookingData.checkInTime, bookingData.arrivalDate, language);
      }
    }, [bookingData?.arrivalDate, bookingData?.checkInTime, language]) ?? '';

  const checkOutLabel =
    useMemo(() => {
      if (isStringValid(bookingData?.checkOutTime) && isStringValid(bookingData?.departureDate)) {
        return checkDateFormatter(bookingData.checkOutTime, bookingData.departureDate, language);
      }
    }, [bookingData.checkOutTime, bookingData.departureDate, language]) ?? '';

  const bookingStatus = mappingBookingStatus(
    bookingData?.reservationStatus ?? '',
    bookingData?.departureDate ?? ''
  );

  if (bookingState.error) {
    return (
      <Notification
        status="error"
        description={bookingState.error}
        variant="alert"
        maxW="full"
        svg={<Alert />}
      />
    );
  }

  if (bookingState.isLoading) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-BookingInfoCardHeader">
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  return (
    <ManageBookingCardHeader
      checkInLabel={checkInLabel}
      checkOutLabel={checkOutLabel}
      hotelName={bookingData?.hotelName}
      shouldRenderDashboardButton={isAmendPage}
      area={area}
      bookingStatus={bookingStatus}
      isBICHeaderBookingStatusEnabled={isBICHeaderBookingStatusEnabled}
    />
  );
}

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 999,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;
