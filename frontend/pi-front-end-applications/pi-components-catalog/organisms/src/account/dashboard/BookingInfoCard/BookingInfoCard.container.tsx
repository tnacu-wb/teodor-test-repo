import { Box, BoxProps, Flex } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  Area,
  BC_RESERVATION_STATUS,
  BOOKING_CHANNEL,
  BOOKING_SUBCHANNEL,
  BOOKING_TYPE,
  DASHBOARD_MANAGE_BOOKING,
  DpaInfo,
  GET_OVERRIDE_REASONS,
  OverridenUserInfo,
  SBForm,
  FindBookingCriteria,
  COPY_BOOKING,
  FIND_BOOKING,
  Query,
  GuaranteeCodes,
  DATE_TYPE,
  GET_DASHBOARD_BASKET,
} from '@whitbread-eos/api';
import { Alert, ErrorBoundary, LoadingSpinner, Notification, Success } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  formatFindBookingToken,
  getFindBookingToken,
  mappingBookingStatus,
  useBookingConfimationData,
  useCustomLocale,
  useQueryRequest,
  useMutationRequest,
  graphQLRequest,
  setBookingCookie,
  useLocalStorage,
} from '@whitbread-eos/utils';
import { format, formatISO } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useRef, useState } from 'react';

import { IS_BACK_FROM_CHANGE_PAYMENT_PAGE_CCUI } from '../../../amend/ChangePaymentCCUI/constants';
import AgentOverrideModal from './AgentOverrideModal';
import BookingInfoCardComponent from './BookingInfoCard.component';
import { ResendConfirmationModal } from './ResendConfirmationModal';

export interface Props {
  basketReference: string | null;
  tempBookingReference?: string;
  operaConfNumber?: string;
  bookingReference: string;
  area: Area;
  updateStatusAfterCancel?: (basketReference: string) => void;
  inputValues?: any;
  bookingType?: string;
  isAmendPage?: boolean;
  isAmendSuccessful?: boolean;
  setInputValuesInSessionStorage?: (inputValues: SBForm, bookingReferenceRPB?: string) => void;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

export default function BookingInfoCard({
  basketReference,
  tempBookingReference,
  operaConfNumber,
  bookingReference,
  area,
  updateStatusAfterCancel,
  inputValues,
  bookingType,
  isAmendPage = false,
  isAmendSuccessful = false,
  setInputValuesInSessionStorage,
  isRemovePIIDataFromLocalStorageEnabled = false,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const shouldShowTypeOfBooking = area === Area.CCUI;
  const baseDataTestId = 'BookingInfoCard';
  const router = useRouter();
  const isErrorFindOrCopyBooking = useRef(false);

  const [isChangedPaymentApplied, seIsChangePaymentApplied] = useState<boolean>(false);

  const [dpaInfo, setDpaInfo] = useState<DpaInfo>({ dpaPassed: false, dpaOverride: false });
  const [overridenUserInfo, setOverridenUserInfo] = useState<OverridenUserInfo>({
    reservationOverrideReasons: { reasonName: '', callerName: '', managerName: '', reasonCode: '' },
    reservationOverridden: false,
  });

  const [isAgentOverrideModalVisible, setIsAgentOverrideModalVisible] = useState(false);
  const [isBackFlag, setIsBackFlag] = useLocalStorage(IS_BACK_FROM_CHANGE_PAYMENT_PAGE_CCUI, false);

  // Retrieve reservationOverrideReasons info regarding overriden reservation
  // TODO: error handling in a separate ticket
  const { mutation: copyBookingMutation } = useMutationRequest(COPY_BOOKING, true);
  const copyBookingAndRedirectToAmendPayment = async (bookingToken?: string) => {
    const channel = BOOKING_CHANNEL.CCUI;
    const bookingChannel = {
      channel,
      subchannel: BOOKING_SUBCHANNEL.WEB,
      language: language?.toUpperCase(),
    };
    let temporaryBasketReference = '';
    try {
      const { copyBooking } = await copyBookingMutation.mutateAsync({
        originalBasketReference: bookingData.basketReference,
        bookingChannel,
        token: bookingToken,
      });
      temporaryBasketReference = copyBooking?.copyBasketReference;
      const redirectToAmendPayment = `/${country}/${language}/amend/payment?tempBasketReference=${temporaryBasketReference}&basketReference=${bookingData.basketReference}&bookingReference=${bookingReference}&changePaymentBIC=true`;
      temporaryBasketReference && router.push(redirectToAmendPayment);
    } catch {
      isErrorFindOrCopyBooking.current = true;
    }
  };

  const handleChangePayment = () => {
    if (paymentOption !== GuaranteeCodes.RESERVE_WITHOUT_CARD) return;

    const queryClient = new QueryClient();
    const findBookingCriteria: FindBookingCriteria = {
      country,
      language,
      arrivalDate: format(
        new Date(firstRoom.roomStay.arrivalDate as string),
        DATE_TYPE.YEAR_MONTH_DAY
      ),
      lastName: firstRoom?.billing?.lastName as string,
      resNo: String(bookingReference).toUpperCase(),
    };

    queryClient
      .fetchQuery({
        queryKey: [
          'FindBooking',
          country,
          language,
          findBookingCriteria.arrivalDate,
          findBookingCriteria.lastName,
          findBookingCriteria.resNo,
        ],
        queryFn: () => graphQLRequest(FIND_BOOKING, { findBookingCriteria }),
      })
      .then((response: Query) => {
        if (!response?.findBooking) {
          return;
        }
        const { ref, basketReference, cookieName, token, minutesTillExpiry } = response.findBooking;
        const cookieValue = { token, basketReference, bookingReference: ref };
        setBookingCookie(cookieName as string, cookieValue, minutesTillExpiry as string);
        copyBookingAndRedirectToAmendPayment(response.findBooking.token);
      })
      .catch(() => (isErrorFindOrCopyBooking.current = true));
  };

  const {
    bookingData,
    bookingError,
    bookingIsError,
    bookingIsLoading,
    bookingIsSuccess,
    bookingRefetch,
  } = useBookingConfimationData(area, basketReference ?? '', bookingReference, language, country);

  // Checking if the booking has all the reservations cancelled
  const areAllReservationsCancelled =
    bookingData?.reservationByIdList.filter(
      (reservation: any) => reservation?.reservationStatus === BOOKING_TYPE.CANCELLED
    ).length === bookingData?.reservationByIdList.length;

  // Considering first room from the list reservations excluding the cancelled one(s) (if any)
  // if all the reservations are cancelled, we will take the first item from the bookingData.reservationByIdList
  const firstRoom = areAllReservationsCancelled
    ? bookingData?.reservationByIdList[0]
    : bookingData?.reservationByIdList.find(
        (reservation: any) => reservation?.reservationStatus !== BOOKING_TYPE.CANCELLED
      );
  const basketReferenceValue = basketReference ?? bookingData?.basketReference;
  const gdsReferenceNumber = firstRoom?.gdsReferenceNumber;
  const reservationOverriddenStatus = firstRoom?.reservationOverridden;
  const distBookingChannel = firstRoom?.roomStay?.bookingChannel;

  const bookingStatus = firstRoom?.reservationStatus;
  const paymentOption = firstRoom?.guaranteeCode;

  const departureDate = firstRoom?.roomStay?.departureDate;

  // Retrieve override reasons by hotelID for agent override modal
  const { data: OVData, error: OVError } = useQueryRequest(
    ['getOverrideReasons', bookingData?.hotelId],
    GET_OVERRIDE_REASONS,
    { hotelId: bookingData?.hotelId },
    {
      enabled: !!bookingData?.hotelId && isAgentOverrideModalVisible,
    }
  );

  const date = formatISO(Date.now());

  const {
    data = {
      manageBooking: {
        isCancellable: false,
        isAmendable: false,
        isRuleCompliant: true,
      },
    },
    isError,
    isLoading,
    error,
  } = useQueryRequest(
    ['manageBookingDashBoard', basketReferenceValue, bookingData?.hotelId],
    DASHBOARD_MANAGE_BOOKING,
    {
      cancelInformationCriteria: {
        userDateTime: date,
        bookingChannel: {
          channel: area === Area.PI ? BOOKING_CHANNEL.PI : BOOKING_CHANNEL.CCUI,
          subchannel: BOOKING_SUBCHANNEL.WEB,
          language: language,
        },
        basketReference: basketReferenceValue,
        hotelId: bookingData?.hotelId,
        token: formatFindBookingToken(getFindBookingToken().token),
      },
    },
    {
      enabled:
        ((area === Area.PI && bookingStatus !== BC_RESERVATION_STATUS.CANCELLED) ||
          (area === Area.CCUI && (bookingType === BOOKING_TYPE.UPCOMING || isAmendPage))) &&
        !!bookingData?.hotelId,
      staleTime: 0,
      cacheTime: 0,
    }
  );

  // basket status
  const { data: getBasketStatus } = useQueryRequest(
    ['basket'],
    GET_DASHBOARD_BASKET,
    {
      basketReference: basketReferenceValue,
    },
    { enabled: !!basketReferenceValue, staleTime: 0, cacheTime: 0 }
  );

  const manageBookingParams = {
    data,
    isError,
    isLoading,
    error,
  };
  const [isModalVisible, setIsModalVisible] = useState(false);
  const onModalClose = () => setIsModalVisible((prevState) => !prevState);

  useEffect(() => {
    if (bookingIsSuccess && reservationOverriddenStatus) {
      setOverridenUserInfo({
        reservationOverrideReasons: firstRoom?.reservationOverrideReasons,
        reservationOverridden: reservationOverriddenStatus,
      });
    }
  }, [bookingIsSuccess, setOverridenUserInfo, bookingData?.reservationByIdList]);

  useEffect(() => {
    // set state for confirmation after changing payment successfully
    if (bookingData && isBackFlag) {
      seIsChangePaymentApplied(!!isBackFlag);
    }

    const cleanupBackFlag = () => {
      setIsBackFlag(false);
    };

    return () => {
      cleanupBackFlag();
    };
  }, [bookingData, isBackFlag, getBasketStatus?.basket?.paymentOption]);

  if (bookingIsError) {
    return (
      <Box data-testid={formatDataTestId(baseDataTestId, 'ErrorContainer')}>
        <Notification
          status="error"
          description={String(bookingError)}
          variant="alert"
          maxW="full"
          svg={<Alert />}
        />
      </Box>
    );
  }

  if (bookingIsLoading) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-BookingInfoCard">
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }
  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Container')}>
      <BookingInfoCardComponent
        hotelInfo={{
          hotelId: getBasketStatus?.basket?.hotelId,
          bookingFlowId: bookingData?.bookingFlowId,
        }}
        bookingReference={bookingReference}
        basketReference={basketReferenceValue}
        operaConfNumber={operaConfNumber}
        tempBookingReference={tempBookingReference}
        area={area}
        shouldShowTypeOfBooking={shouldShowTypeOfBooking}
        bookingType={bookingType ?? mappingBookingStatus(bookingStatus, departureDate)}
        bookingStatus={bookingStatus}
        getBookingStatus={() => {
          bookingRefetch();
          area === 'ccui' && updateStatusAfterCancel?.(bookingReference);
        }}
        paymentOption={getBasketStatus?.basket?.paymentOption}
        dpaInfo={dpaInfo}
        setDpaInfo={setDpaInfo}
        setIsAgentOverrideModalVisible={setIsAgentOverrideModalVisible}
        overridenUserInfo={overridenUserInfo}
        inputValues={inputValues}
        gdsReferenceNumber={gdsReferenceNumber}
        distBookingChannel={distBookingChannel}
        isAmendPage={isAmendPage}
        isAmendSuccessful={isAmendSuccessful}
        handleResendConfirmationAction={onModalClose}
        arrivalDate={firstRoom.roomStay.arrivalDate}
        bookingSurname={firstRoom?.billing?.lastName}
        handleRepeatBookingAction={() => handleRepeatBookingAction(setInputValuesInSessionStorage)}
        manageBookingParams={manageBookingParams}
        handleChangePayment={handleChangePayment}
        isErrorFindOrCopyBooking={isErrorFindOrCopyBooking.current}
        basketStatus={getBasketStatus?.basket?.status}
        isChangedPaymentApplied={isChangedPaymentApplied}
        isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
      />
      {area === Area.PI && basketReference && (
        <ResendConfirmationModal
          isModalVisible={isModalVisible}
          onModalClose={onModalClose}
          basketReference={basketReference}
          hotelId={bookingData?.hotelId}
        />
      )}
      {area === Area.CCUI && (
        <Box data-testid={formatDataTestId(baseDataTestId, 'AgentOverrideInfo')}>
          {bookingData && OVData && (
            <ErrorBoundary>
              <AgentOverrideModal
                basketReference={basketReferenceValue}
                hotelId={bookingData?.hotelId}
                reasons={OVData?.cancellationReasons?.cancellationReasons}
                error={OVError as any}
                isVisible={isAgentOverrideModalVisible}
                onClose={handleAgentOverrideModalClose}
                getBookingInfo={() => {
                  bookingRefetch();
                }}
                overridenUserInfo={overridenUserInfo}
              />
            </ErrorBoundary>
          )}
          {bookingType === BOOKING_TYPE.UPCOMING && reservationOverriddenStatus && (
            <Box mt="2xl" data-testid={formatDataTestId(baseDataTestId, 'OverridenNotification')}>
              <Notification
                title={''}
                maxWidth="full"
                description={t(
                  'ccui.manageBooking.options.agentOverrideModal.notification.message'
                )}
                status={'success'}
                variant={'success'}
                svg={<Success />}
              />
            </Box>
          )}
        </Box>
      )}
    </Box>
  );

  function handleAgentOverrideModalClose() {
    setIsAgentOverrideModalVisible(false);
  }

  function handleRepeatBookingAction(
    setInputValuesInSessionStorage?: (inputValues: SBForm, bookingReferenceRPB?: string) => void
  ) {
    if (setInputValuesInSessionStorage) {
      setInputValuesInSessionStorage(inputValues, bookingReference);
    }
    router.push(`/${country}/${language}/repeat-booking?reservationId=${basketReferenceValue}`);
  }
}

//<editor-fold desc="Styles" defaultstate="collapsed">

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
//</editor-fold>
