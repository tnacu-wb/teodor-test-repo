import { Box, Flex, FlexProps, Heading, HeadingProps, Text } from '@chakra-ui/react';
import { keepPreviousData } from '@tanstack/react-query';
import {
  AMEND_PAYMENT_OPTIONS,
  HeaderInformationQuery,
  AmendRoomsAndGuestsLabels,
  Area,
  BOOKING_SUBCHANNEL,
  BookingConfirmationType,
  Channel,
  GET_BOOKING_CONFIRMATION_AMEND,
  GET_STATIC_CONTENT,
  GET_SUMMARY_OF_PAYMENTS,
  PaymentOptionsCriteria,
  MealItemExtension,
  MealKids,
  RoomSelection,
  SelectedMealsPerRoom,
  SITE_LEISURE,
  SummaryOfPaymentsLabels,
  Query,
  GET_HOTEL_INFORMATION,
  GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
  ExtrasItem,
  ExtrasId,
  Claims,
} from '@whitbread-eos/api';
import { PageLoader } from '@whitbread-eos/molecules';
import {
  AgentMemo,
  BookingSummaryWrapper,
  AmendPaymentCCUI,
  ChangePaymentCCUI,
} from '@whitbread-eos/organisms';
import {
  adultsMealsSelector,
  childrenMealsSelector,
  extrasPackagesMapperSelector,
  formatDataTestId,
  formatFindBookingToken,
  getAmendSectionTranslations,
  getFindBookingToken,
  getMaxValueFromRoomStays,
  getNightsNumber,
  mealsMapperSelector,
  roomInformationSelector,
  roomPackageSelection,
  sortMealsByReservationId,
  useBookingConfimationData,
  useCustomLocale,
  usePackages,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useState } from 'react';

export interface Props {
  user: Claims;
}

export function PaymentPageAmend({ user }: Readonly<Props>) {
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const baseDataTestId = 'amend-payment';

  const [, setEmail] = useState<string>('');

  const router = useRouter();
  const temporaryBasketReference = (router?.query?.tempBasketReference as string) ?? '';
  const bookingReference = (router?.query?.bookingReference as string) ?? '';
  const basketReference = (router?.query?.basketReference as string) ?? '';
  const changePaymentBIC = (router?.query?.changePaymentBIC as string) ?? '';

  const SUBCHANNEL = 'WEB';
  const channel = 'CCUI';
  const area = 'ccui' as Area;

  const handleConfirmChanges = () => console.log('Handle confirm!');

  const {
    bookingSummaryLabels,
    summaryOfPaymentsLabels: _summaryOfPaymentsLabels,
    notificationLabels,
    removeRoomModalLabels,
    stayDatesLabels: _stayDatesLabels,
    leadGuestValidationLabels,
    leadGuestLabels,
    roomAvailabilityLabels,
  } = getAmendSectionTranslations(t);

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
    bookingData: bookingConfirmationData,
    bookingError: bookingConfirmationError,
    bookingIsError: bookingConfirmationIsError,
    bookingIsLoading: bookingConfirmationIsLoading,
  } = useBookingConfimationData(area, null, bookingReference, language, country);

  const paramsForBookingConfirmation = {
    basketReference: temporaryBasketReference,
    country,
    language,
  };

  const paramsForBookingChannel = {
    channel,
    subchannel: SUBCHANNEL,
    language,
  };

  const token = formatFindBookingToken(getFindBookingToken().token);

  const paramsForSummaryOfPayments = {
    originalBasketRef: basketReference,
    copyBasketRef: temporaryBasketReference,
    token: token,
    bookingChannel: paramsForBookingChannel,
    country: country,
  };

  const paymentOptionsCriteria: PaymentOptionsCriteria = {
    token: token,
    originalBookingRef: basketReference,
    tempBookingRef: temporaryBasketReference,
    bookingChannel: {
      channel: Channel.Ccui,
      subchannel: BOOKING_SUBCHANNEL.WEB,
      language: language,
    },
    country: country.toUpperCase(),
  };

  const {
    data: temporaryBookingData,
    isError: temporaryBookingIsError,
    isLoading: temporaryBookingDataIsLoading,
    error: temporaryBookingError,
  } = useQueryRequest(
    ['getBookingConfirmationAmend', temporaryBasketReference, country, language],
    GET_BOOKING_CONFIRMATION_AMEND,
    paramsForBookingConfirmation,
    {
      enabled: !!temporaryBasketReference,
      placeHolderData: keepPreviousData,
    }
  );

  const {
    data: bcAuthData,
    isLoading: isLoadingBcAuthData,
    isError: isErrorBcAuthData,
  } = useQueryRequest(
    ['getBookingConfirmationAuthenticated', bookingReference, language, country],
    GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
    {
      language,
      country,
      bookingReference,
    },
    { enabled: !!token && !!changePaymentBIC },
    token,
    true
  );

  const {
    data: summaryOfPaymentsData,
    isLoading: summaryOfPaymentsIsLoading,
    isError: summaryOfPaymentsIsError,
    error: summaryOfPaymentsError,
  } = useQueryRequest(
    ['AmendSummary', paramsForSummaryOfPayments],
    GET_SUMMARY_OF_PAYMENTS,
    paramsForSummaryOfPayments,
    {
      enabled: !!temporaryBasketReference,
    }
  );

  const {
    data: paymentOptionsData,
    isLoading: paymentOptionsIsLoading,
    isError: paymentOptionsIsError,
    error: paymentOptionsError,
  }: {
    data: Query;
    isLoading: boolean;
    isError: boolean;
    error: unknown;
  } = useQueryRequest(
    ['paymentOptions', token, basketReference, temporaryBasketReference],
    AMEND_PAYMENT_OPTIONS,
    {
      ...paymentOptionsCriteria,
      cacheTime: 0,
    }
  );

  const paramsForHotelInformationhiQuery = {
    country,
    language,
    hotelId: temporaryBookingData?.bookingConfirmation?.hotelId,
  };

  const { data: hiData } = useQueryRequest(
    [
      'GetHotelInformation',
      paramsForHotelInformationhiQuery?.hotelId,
      paramsForHotelInformationhiQuery?.country,
      paramsForHotelInformationhiQuery?.language,
    ],
    GET_HOTEL_INFORMATION,
    {
      ...paramsForHotelInformationhiQuery,
    },
    { enabled: !!temporaryBookingData?.bookingConfirmation?.hotelId }
  );

  const temporaryBookingConfirmation =
    temporaryBookingData?.bookingConfirmation as BookingConfirmationType;

  const temporaryFirstReservation = temporaryBookingConfirmation?.reservationByIdList[0];
  const originalFirstReservation = bookingConfirmationData?.reservationByIdList[0];

  const temporaryArrivalDate = temporaryFirstReservation?.roomStay?.arrivalDate;
  const temporaryDepartureDate = temporaryFirstReservation?.roomStay?.departureDate;
  const originalArrivalDate = originalFirstReservation?.roomStay?.arrivalDate;
  const originalDepartureDate = originalFirstReservation?.roomStay?.departureDate;

  const temporaryNumberOfNights = getNightsNumber(temporaryArrivalDate, temporaryDepartureDate);

  const [firstReservation] = temporaryBookingData?.bookingConfirmation?.reservationByIdList || [];
  const {
    isLoading: packagesIsLoading,
    error: packagesError,
    isError: packagesIsError,
    packages: mealPackagesData,
  } = usePackages({
    adultsNumber: getMaxValueFromRoomStays(
      temporaryBookingData?.bookingConfirmation?.reservationByIdList,
      'adultsNumber'
    ),
    childrenNumber: getMaxValueFromRoomStays(
      temporaryBookingData?.bookingConfirmation?.reservationByIdList,
      'childrenNumber'
    ),
    hotelId: temporaryBookingData?.bookingConfirmation?.hotelId,
    basketReferenceId: temporaryBasketReference,
    endDate: firstReservation?.roomStay?.departureDate,
    startDate: firstReservation?.roomStay?.arrivalDate,
    bookingFlowId: temporaryBookingData?.bookingConfirmation?.bookingFlowId,
    nightsNumber: getNightsNumber(
      firstReservation?.roomStay?.arrivalDate,
      firstReservation?.roomStay?.departureDate
    ),
    channel: Channel.Ccui,
    options: {
      enabled: !!temporaryBasketReference && !!temporaryBookingData,
      keepPreviousData: true,
    },
  });

  const stayDatesLabels = {
    ..._stayDatesLabels,
    invalidNights: headerInformationData?.headerInformation?.form?.invalidNights,
  };

  const summaryOfPaymentsLabels: SummaryOfPaymentsLabels = {
    ..._summaryOfPaymentsLabels,
    additionalAmount: t('amend.balance.pnNew'),
  };

  const roomsAndGuestsLabels: AmendRoomsAndGuestsLabels = {
    roomModalLabels: {
      roomDropdownLabels: {
        single: headerInformationData?.headerInformation?.content?.global?.single ?? '',
        double: headerInformationData?.headerInformation?.content?.global?.double ?? '',
        accessible: headerInformationData?.headerInformation?.content?.global?.accessible ?? '',
        twin: headerInformationData?.headerInformation?.content?.global?.twin ?? '',
        family: headerInformationData?.headerInformation?.content?.global?.family ?? '',
      },
      roomDropdownRoomCodes: headerInformationData?.headerInformation?.config?.roomCodes,
      roomAvailabilityLabels: {
        adult: headerInformationData?.headerInformation?.content?.global?.adult ?? '',
        adults: headerInformationData?.headerInformation?.content?.global?.adults ?? '',
        child: headerInformationData?.headerInformation?.content?.global?.child ?? '',
        children: headerInformationData?.headerInformation?.content?.global?.children ?? '',
        ...roomAvailabilityLabels,
      },
      leadGuestLabels: {
        ...leadGuestLabels,
      },
      leadGuestValidationLabels: {
        ...leadGuestValidationLabels,
      },

      notificationLabels: {
        ...notificationLabels,
      },
    },
    removeRoomModalLabels: {
      ...removeRoomModalLabels,
    },
    roomLabel: headerInformationData?.headerInformation?.content?.global?.roomLabel ?? '',
    edit: t('amend.edit'),
    remove: t('amend.removeRoom'),
  };

  const { meals, mealsKids } = mealPackagesData!;
  let sortedMeals: RoomSelection[] = [];

  const extrasItemsPrices = {
    eciPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) => item?.id === ExtrasId.EARLY_CHECK_IN
    )?.price,
    lcoPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) => item?.id === ExtrasId.LATE_CHECK_OUT
    )?.price,
    wifiPrice: mealPackagesData?.extrasItems?.find(
      (item: ExtrasItem) => item?.id === ExtrasId.ULTIMATE_WIFI
    )?.price,
  };

  let selectedMeals: SelectedMealsPerRoom[] = [];
  if (
    mealPackagesData?.roomSelection?.length &&
    temporaryBookingData?.bookingConfirmation?.reservationByIdList?.length
  ) {
    sortedMeals = sortMealsByReservationId(
      mealPackagesData.roomSelection,
      temporaryBookingData?.bookingConfirmation?.reservationByIdList
    );
  }
  if (sortedMeals.length && meals && mealsKids) {
    const previousSelectedMeals: SelectedMealsPerRoom[] = mealsMapperSelector(
      meals,
      mealsKids,
      sortedMeals
    );
    selectedMeals = previousSelectedMeals;
  }

  function getRoomsPackages() {
    const adultsMeals: MealItemExtension[] = adultsMealsSelector(
      mealPackagesData?.meals,
      temporaryNumberOfNights
    );
    const childrenMeals: MealKids[] = childrenMealsSelector(mealPackagesData?.mealsKids);

    return roomInformationSelector(
      temporaryBookingConfirmation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(extrasPackagesMapperSelector(mealPackagesData?.roomSelectionAmendExtras))
    );
  }

  if (
    temporaryBookingDataIsLoading ||
    summaryOfPaymentsIsLoading ||
    bookingConfirmationIsLoading ||
    packagesIsLoading ||
    headerInformationIsLoading ||
    paymentOptionsIsLoading ||
    isLoadingBcAuthData
  ) {
    return <PageLoader text={t('booking.loading')} />;
  }

  const pageHasError =
    headerInformationIsError ||
    bookingConfirmationIsError ||
    temporaryBookingIsError ||
    summaryOfPaymentsIsError ||
    packagesIsError ||
    paymentOptionsIsError ||
    isErrorBcAuthData;

  const pageError =
    headerInformationError ||
    bookingConfirmationError ||
    temporaryBookingError ||
    summaryOfPaymentsError ||
    packagesError ||
    paymentOptionsError;

  if (pageHasError) {
    return (
      <Text data-testid={formatDataTestId(baseDataTestId, 'page-request-error')}>
        {(pageError as Error).message}
      </Text>
    );
  }

  return (
    <>
      <Flex {...amendContainerStyle} data-testid={`${baseDataTestId}`}>
        <Box data-testid={`${baseDataTestId}_wrapper`}>
          <Box pb={4} data-testid={`${baseDataTestId}_wrapperTitle`}>
            <Heading as="h3" {...headerStyles} data-testid={`${baseDataTestId}_title`}>
              {t('ccui.payment.title')}
            </Heading>
          </Box>
          <Box pb={4} data-testid={`${baseDataTestId}_wrapperTitleDescription`}>
            <Heading
              as="h6"
              {...descriptionStyles}
              data-testid={`${baseDataTestId}_titleDescription`}
            >
              {t('ccui.payment.description')}
            </Heading>
          </Box>
          <Box pb={4} data-testid={`${baseDataTestId}_wrapperData`}>
            {changePaymentBIC ? (
              <ChangePaymentCCUI
                summaryOfPayments={summaryOfPaymentsData?.amendSummary}
                paymentOptionsData={paymentOptionsData}
                basketReference={basketReference}
                user={user}
                hiData={hiData}
                bcData={bcAuthData.bookingConfirmationAuthenticated}
              />
            ) : (
              <AmendPaymentCCUI
                summaryOfPayments={summaryOfPaymentsData?.amendSummary}
                paymentOptionsData={paymentOptionsData}
                bookingReference={bookingReference}
                originalBasketReference={basketReference}
                temporaryBasketReference={temporaryBasketReference}
              />
            )}
          </Box>
        </Box>
        <Box>
          <BookingSummaryWrapper
            language={language}
            bookingInformation={
              changePaymentBIC
                ? bcAuthData.bookingConfirmationAuthenticated
                : temporaryBookingConfirmation
            }
            roomsPackages={getRoomsPackages()}
            originalArrivalDate={originalArrivalDate}
            originalDepartureDate={originalDepartureDate}
            stayDatesLabels={stayDatesLabels}
            roomsAndGuestsLabels={roomsAndGuestsLabels}
            bookingSummaryLabels={bookingSummaryLabels}
            summaryOfPayments={summaryOfPaymentsData?.amendSummary}
            summaryOfPaymentsLabels={summaryOfPaymentsLabels}
            isConfirmButtonEnabled={false}
            onConfirmChanges={handleConfirmChanges}
            variant={Area.CCUI}
            px="0"
            setEmailCallback={setEmail}
            hideConfirmButton={true}
            extrasItemsPrices={extrasItemsPrices}
            isCityTaxEnabled={
              hiData?.hotelInformation?.hotelInformation?.cityTax?.isCityTaxBusinessHotel ?? false
            }
          />
        </Box>
      </Flex>
      <AgentMemo />
    </>
  );
}

const headerStyles = {
  color: 'darkGrey1',
  fontSize: '3xxl',
  lineHeight: '5',
  marginTop: '5xl',
  fontStyle: 'normal',
  fontWeight: 'semibold',
} as HeadingProps;

const descriptionStyles = {
  fontWeight: 'normal',
  fontStyle: 'normal',
  fontSize: 'md',
  lineHeight: 3,
  color: 'darkGrey1',
};

const amendContainerStyle = {
  flexDir: {
    mobile: 'column',
    md: 'column',
    lg: 'row',
  },
  justifyContent: 'space-between',
} as FlexProps;
