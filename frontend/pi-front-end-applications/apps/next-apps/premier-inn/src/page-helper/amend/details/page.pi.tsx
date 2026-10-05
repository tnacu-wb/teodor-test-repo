import { Box, Text } from '@chakra-ui/react';
import {
  ADD_NEW_ROOM,
  AMEND_EDIT_ROOM,
  AMEND_STAY_DATES,
  AmendConfInput,
  HeaderInformationQuery,
  Area,
  BookingConfirmationType,
  CONFIRM_AMEND,
  CONFIRM_AMEND_STATUS,
  COPY_BOOKING,
  GET_BOOKING_CONFIRMATION_AMEND,
  GET_EMPLOYEE_STAY_RULES_QUERY,
  GET_HOTEL_INFORMATION,
  GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
  GET_STATIC_CONTENT,
  GET_STAY_RULES_QUERY,
  OfferEnum,
  PaymentMethod,
  PackagesCriteria,
  PaymentOption,
  REMOVE_ROOM,
  SAVE_RESERVATION,
  SITE_LEISURE,
  UserType,
} from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { PageLoader } from '@whitbread-eos/molecules';
import { AmendContainer } from '@whitbread-eos/organisms';
import {
  formatDataTestId,
  getIsBillingAddressDisplayed,
  getPaymentError,
  useMutationRequest,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useEffect, useState, useRef } from 'react';

/* eslint-disable @typescript-eslint/no-unused-vars */
interface Props {
  confirmationInput: AmendConfInput;
  pcksQueryInput: PackagesCriteria;
  tempBookingReference?: string | null;
  status?: string | null;
}

export default function AmendPagePi({
  confirmationInput,
  tempBookingReference,
  status,
}: Readonly<Props>) {
  const { t } = useTranslation();

  const { bookingReference, basketReference, token, country, language } = confirmationInput;

  const hasRunCopyBooking = useRef(false);
  const [temporaryBasketReference, setTemporaryBasketReference] = useState<string>('');
  const [amendVisited, setAmendVisited] = useState<boolean>(false);
  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState<PaymentOption>({
    type: 'PAY_ON_ARRIVAL',
    order: 0,
    enabled: true,
  });
  const [selectedPaymentType, setSelectedPaymentType] = useState({
    name: '',
    type: '',
    subType: '',
    order: 0,
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: false,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  } as PaymentMethod);

  const channel = 'PI';
  const baseDataTestId = 'Amend';
  const userType = UserType.Leisure;
  const bookingConfInput = { basketReference, country, language };
  const shouldRetryPayment = !!(
    status === CONFIRM_AMEND_STATUS.paymentError && tempBookingReference
  );

  const {
    data: bookingConfirmationData,
    isError: bookingConfirmationIsError,
    isLoading: bookingConfirmationIsLoading,
    error: bookingConfirmationError,
  } = useQueryRequest(
    ['getBookingConfirmationAmend', { ...bookingConfInput }],
    GET_BOOKING_CONFIRMATION_AMEND,
    bookingConfInput,
    { staleTime: 0 }
  );

  const bookingConfirmation: BookingConfirmationType = bookingConfirmationData?.bookingConfirmation;
  const currentReasonForStay =
    bookingConfirmationData?.bookingConfirmation?.reservationByIdList?.[0]?.additionalGuestInfo
      ?.purposeOfStay;

  const {
    data: hotelInformationData,
    isError: hotelInformationIsError,
    isLoading: hotelInformationIsLoading,
    error: hotelInformationError,
  } = useQueryRequest(
    ['getHotelInformation', language, country, bookingConfirmation?.hotelId],
    GET_HOTEL_INFORMATION,
    {
      country,
      language,
      hotelId: bookingConfirmation?.hotelId,
    },
    {
      enabled: !!bookingConfirmation,
    }
  );

  const brand = hotelInformationData?.hotelInformation?.brand;
  const isBillingAddressDisplayed = getIsBillingAddressDisplayed({
    selectedPaymentType,
    hotelInformationData,
    selectedPaymentDetail,
  });

  const errorData = {
    currentReasonForStay,
    brand,
    t,
  };

  const errorMessagePayment = getPaymentError(errorData);

  const {
    data: headerInformationData,
    isError: headerInformationIsError,
    isLoading: headerInformationIsLoading,
    error: headerInformationError,
  }: HeaderInformationQuery = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  const {
    data: stayRulesData,
    isError: stayRulesIsError,
    isLoading: stayRulesIsLoading,
    error: stayRulesError,
  } = useQueryRequest(['getStayRules', channel], GET_STAY_RULES_QUERY, {
    channel,
  });

  const {
    data: employeeStayRulesData,
    isError: employeeStayRulesIsError,
    isLoading: employeeStayRulesIsLoading,
    error: employeeStayRulesError,
  } = useQueryRequest(['getEmployeeStayRules', channel], GET_EMPLOYEE_STAY_RULES_QUERY, {
    channel: OfferEnum.EMPLOYEE_RATE_CODE,
  });

  const {
    data: RoomOccupancyLimitationsData,
    isError: RoomOccupancyLimitationsIsError,
    isLoading: RoomOccupancyLimitationsIsLoading,
    error: RoomOccupancyLimitationsError,
  } = useQueryRequest(
    ['getRoomOccupancyLimitations', channel],
    GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
    {
      channel,
    },
    { enabled: !!hotelInformationData }
  );

  const { mutation: copyBookingMutation, isError: copyBookingIsError } = useMutationRequest(
    COPY_BOOKING,
    true
  );

  const {
    mutation: amendStayDatesMutation,
    isError: amendStayDatesIsError,
    isSuccess: amendStayDatesIsSuccess,
    isLoading: amendStayDatesIsLoading,
  } = useMutationRequest(AMEND_STAY_DATES, true);

  const {
    mutation: addNewRoomMutation,
    isLoading: addNewRoomIsLoading,
    isSuccess: addNewRoomIsSuccess,
  } = useMutationRequest(ADD_NEW_ROOM, true);

  const {
    mutation: amendEditRoomMutation,
    isLoading: amendEditRoomIsLoading,
    isSuccess: amendEditRoomIsSuccess,
  } = useMutationRequest(AMEND_EDIT_ROOM, true);

  const {
    mutation: removeRoomMutation,
    isLoading: removeRoomIsLoading,
    isSuccess: removeRoomIsSuccess,
  } = useMutationRequest(REMOVE_ROOM, true);

  const {
    mutation: amendSaveReservationMutation,
    isLoading: amendSaveReservationIsLoading,
    isSuccess: amendSaveReservationIsSuccess,
  } = useMutationRequest(SAVE_RESERVATION, true);

  const {
    mutation: confirmAmendMutation,
    isLoading: confirmAmendIsLoading,
    isSuccess: confirmAmendIsSuccess,
    isError: confirmAmendIsError,
  } = useMutationRequest(CONFIRM_AMEND, true);

  useEffect(() => {
    if (shouldRetryPayment) {
      setTemporaryBasketReference(tempBookingReference);
      setAmendVisited(true);
    } else if (!hasRunCopyBooking.current) {
      const bookingChannel = { channel, subchannel: 'WEB', language: language?.toUpperCase() };
      copyBookingMutation
        .mutateAsync({
          originalBasketReference: basketReference,
          bookingChannel,
          token: token,
        })
        .then((result: any) => {
          const temporaryBasketRef = result.copyBooking.copyBasketReference;
          setTemporaryBasketReference(temporaryBasketRef);
        })
        .catch(() => setTemporaryBasketReference(''));
      hasRunCopyBooking.current = true;
    }
  }, []);

  const isLoading =
    bookingConfirmationIsLoading ||
    stayRulesIsLoading ||
    employeeStayRulesIsLoading ||
    headerInformationIsLoading ||
    RoomOccupancyLimitationsIsLoading ||
    hotelInformationIsLoading;

  if (isLoading) {
    return <PageLoader text={t('searchresults.list.hotel.loading')} />;
  }

  if (bookingConfirmationIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-booking-confirmation')}>
        {(bookingConfirmationError as Error).message}
      </Text>
    );
  }

  if (stayRulesIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-stay-rules')}>
        {(stayRulesError as Error).message}
      </Text>
    );
  }
  if (employeeStayRulesIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-employee-stay-rules')}>
        {(employeeStayRulesError as Error).message}
      </Text>
    );
  }
  if (headerInformationIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-translations')}>
        {(headerInformationError as Error).message}
      </Text>
    );
  }
  if (RoomOccupancyLimitationsIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-roomOccupancyLimitations')}>
        {(RoomOccupancyLimitationsError as Error).message}
      </Text>
    );
  }
  if (hotelInformationIsError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'error-hotel-information')}>
        {(hotelInformationError as Error).message}
      </Text>
    );
  }

  if (copyBookingIsError) {
    return (
      <Box m="lg" data-testid={formatDataTestId(baseDataTestId, 'error-copy-booking-mutation')}>
        <Notification
          status="warning"
          description={t('errors.sorry')}
          variant="alert"
          maxW="full"
          svg={<Alert />}
        />
      </Box>
    );
  }

  return (
    <AmendContainer
      language={language}
      country={country}
      t={t}
      basketReference={basketReference}
      bookingReference={bookingReference}
      temporaryBasketReference={temporaryBasketReference}
      channel={channel}
      amendVisited={amendVisited}
      status={status}
      data={{
        bookingConfirmationData: bookingConfirmation,
        hotelInformation: hotelInformationData,
        headerInformationData: headerInformationData,
        stayRulesData: stayRulesData,
        employeeStayRulesData: employeeStayRulesData,
        RoomOccupancyLimitationsData: RoomOccupancyLimitationsData,
        brand,
        addNewRoomMutation,
        addNewRoomIsSuccess,
        addNewRoomIsLoading,
        amendStayDates: {
          amendStayDatesIsError,
          amendStayDatesIsSuccess,
          amendStayDatesIsLoading,
          amendStayDatesMutation,
        },
        amendEditRoom: {
          amendEditRoomIsSuccess,
          amendEditRoomIsLoading,
          amendEditRoomMutation,
        },
        removeRoom: {
          removeRoomIsSuccess,
          removeRoomIsLoading,
          removeRoomMutation,
        },
        saveReservation: {
          amendSaveReservationMutation,
          amendSaveReservationIsLoading,
          amendSaveReservationIsSuccess,
        },
        confirmAmend: {
          confirmAmendIsLoading,
          confirmAmendMutation,
          confirmAmendIsSuccess,
          confirmAmendIsError,
        },
      }}
      variant={Area.PI}
      paymentProps={{
        isBillingAddressDisplayed: isBillingAddressDisplayed,
        selectedPaymentDetail: selectedPaymentDetail,
        setSelectedPaymentDetail: setSelectedPaymentDetail,
        selectedPaymentType: selectedPaymentType,
        hotelName: hotelInformationData.hotelInformation?.hotelName,
        errorMessagePayment: errorMessagePayment,
        setSelectedPaymentType: setSelectedPaymentType,
        basketReference: basketReference,
        variant: Area.PI,
        userType: userType,
      }}
    />
  );
}
