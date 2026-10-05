import {
  Box,
  Flex,
  Grid,
  GridItem,
  GridItemProps,
  GridProps,
  Heading,
  HeadingProps,
  SimpleGrid,
} from '@chakra-ui/react';
import {
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  GET_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  QueryHotelInformationArgs,
  MealItem,
  MealKids,
  PackagesCriteria,
  SelectedMealsPerRoom,
  PAYMENT_FAILED_INFO_KEY,
  PAYMENT_FAILURE_INFO_INITIAL_VALUE,
  PaymentErrorInfo,
  type Claims,
} from '@whitbread-eos/api';
import { Error, Notification } from '@whitbread-eos/atoms';
import { BackToDetails } from '@whitbread-eos/molecules';
import { BookingSummary } from '@whitbread-eos/organisms';
import {
  adultsMealsSelector,
  calculateTotalCostRoomSelection,
  childrenMealsSelector,
  getNightsNumber,
  hotelInformationSelector,
  mealsMapperSelector,
  roomInformationSelector,
  selectedMealsPerRoomSelector,
  setAnalyticsUser,
  useCustomLocale,
  usePackages,
  useQueryRequest,
  extrasPackagesMapperSelector,
  roomPackageSelection,
  useSessionStorage,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export interface PaymentCcuiPropsErrors {
  hiQueryInput?: QueryHotelInformationArgs;
  pcksQueryInput?: PackagesCriteria;
  basketReference: string | null;
  user: Claims;
  accessToken: string;
}

export function PaymentsErrorHandling({
  pcksQueryInput,
  hiQueryInput,
  basketReference,
  user,
}: Readonly<PaymentCcuiPropsErrors>) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars, no-unused-vars
  const discount = '';

  const { t } = useTranslation(['common']);
  const router = useRouter();
  const { language, country } = useCustomLocale();

  const { data: bkngData } = useQueryRequest(
    ['GetBookingInformation', language, country, basketReference],
    GET_BOOKING_INFORMATION,
    {
      language,
      country,
      basketReference,
      bookingChannelCriteria: {
        channel: 'CCUI',
        subchannel: 'WEB',
        language: language === 'en' ? 'EN' : 'DE',
      },
    }
  );
  const [paymentFailure] = useSessionStorage<PaymentErrorInfo>(
    PAYMENT_FAILED_INFO_KEY,
    PAYMENT_FAILURE_INFO_INITIAL_VALUE
  );

  const { data: hiData } = useQueryRequest('GetHotelInformation', GET_HOTEL_INFORMATION, {
    ...hiQueryInput,
  });

  const { packages } = usePackages({
    adultsNumber: pcksQueryInput?.adultsNumber as number,
    childrenNumber: pcksQueryInput?.childrenNumber as number,
    hotelId: pcksQueryInput?.hotelId as string,
    basketReferenceId: basketReference as string,
    endDate: pcksQueryInput?.endDate as string,
    startDate: pcksQueryInput?.startDate as string,
    bookingFlowId: pcksQueryInput?.bookingFlowId as string,
    nightsNumber: pcksQueryInput?.nightsNumber as number,
    channel: pcksQueryInput?.channel,
  });

  const { bookingInformation } = bkngData;
  const firstRoom = bookingInformation?.reservationByIdList[0] || {};
  const arrivalDate = firstRoom.roomStay?.arrivalDate || null;
  const departureDate = firstRoom.roomStay?.departureDate || null;

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  const meals = packages?.meals;
  const mealsKids = packages?.mealsKids;
  const roomSelection = packages?.roomSelection;

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const adultsMeals: MealItem[] = adultsMealsSelector(
    meals,
    noNights,
    bookingInformation.totalAdults
  );
  const childrenMeals: MealKids[] = childrenMealsSelector(mealsKids);

  const reservationDetails: BookingDataReservationDetailsProps = {
    arrivalDate,
    departureDate,
    currency: bookingInformation.currencyCode,
    noRooms: bookingInformation.reservationByIdList.length,
    noNights,
  };

  useEffect(() => {
    if (roomSelection && meals && mealsKids) {
      setSelectedMeals(mealsMapperSelector(meals, mealsKids, roomSelection));
    }
  }, [roomSelection, meals, mealsKids]);

  const bookingSummaryData: BookingSummaryDataProps = {
    hotelInformation: hotelInformationSelector(hiData?.hotelInformation),
    totalCost: {
      discount: +discount,
      currency: bookingInformation.currencyCode,
      initialTotalCost:
        bkngData?.bookingInformation?.totalCost -
        calculateTotalCostRoomSelection(adultsMeals, roomSelection ?? [], noNights),
      newTotalCost: bookingInformation.totalCost - +discount,
      meals: selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals),
    },
    rateInformation: {
      rate: firstRoom.roomStay?.rateExtraInfo.rateName,
      noNights: noNights,
      noRooms: bookingInformation.reservationByIdList.length,
    },
    stayDatesInformation: {
      arrivalDate,
      departureDate,
      noNights: noNights,
    },
    roomInformation: roomInformationSelector(
      bkngData?.bookingInformation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(extrasPackagesMapperSelector(roomSelection))
    ),
  };

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language]);

  const paymentFailedErrorMessage = t(`errors.payment.${paymentFailure?.globalErrTextTemplate}`, {
    defaultValue: t('ccui.paymentErrorPage.notification.message'),
  });

  return (
    <SimpleGrid
      columns={2}
      data-testid="paymentErrorHandling"
      flexDirection={{ md: 'column', lg: 'row' }}
    >
      <Box data-testid="paymentErrorHandling_wrapper">
        <BackToDetails
          goBack={() =>
            router.push(`/${country}/${language}/payment?reservationId=${basketReference}`)
          }
          prefixDataTestId="paymentErrorHandling"
          isPaymentsErrorPage={true}
        />
        <Box pb={4} data-testid="paymentErrorHandling_wrapperTitle">
          <Heading as="h3" {...headerStyles} data-testid="paymentErrorHandling_title">
            {t('ccui.paymentErrorPage.pageTitle')}
          </Heading>
        </Box>
        <Box mt={5}>
          <Notification
            status="error"
            svg={<Error />}
            variant="error"
            data-testid="BookingSummary-PaymentErrorNotification"
            title={t('ccui.paymentErrorPage.notification.title')}
            description={paymentFailedErrorMessage}
          />
        </Box>
      </Box>
      <Box data-testid="paymentErrorHandling_wrapperBS">
        <Grid {...mainPaymentsGridStyle}>
          <GridItem {...bookingSummaryMobileContainerStyle}>
            <Flex {...bookingSummaryMobileTriggerStyle}>
              <BookingSummary
                variant="mobile"
                t={t}
                language={language}
                reservationDetails={reservationDetails}
                bookingSummaryData={bookingSummaryData}
                isExtrasDisplayed={!!packages?.extrasItems}
              />
            </Flex>
          </GridItem>
          <GridItem {...bookingSummaryDesktopStyle}>
            <BookingSummary
              variant="desktop"
              t={t}
              language={language}
              reservationDetails={reservationDetails}
              bookingSummaryData={bookingSummaryData}
              isExtrasDisplayed={!!packages?.extrasItems}
            />
          </GridItem>
        </Grid>
      </Box>
    </SimpleGrid>
  );
}

const headerStyles = {
  fontSize: '3xxl',
  lineHeight: 4,
  marginTop: '5xl',
  fontStyle: 'normal',
  fontWeight: 'semibold',
} as HeadingProps;

const mainPaymentsGridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: {
    mobile: '0',
    lg: 'lg',
    xl: '5xl',
  },
  pb: {
    mobile: 'md',
    md: 'lg',
    lg: 'xl',
    xl: '5xl',
  },
  pt: {
    mobile: '0',
    lg: '5xl',
  },
  m: '0',
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0',
    lg: '32',
    xl: '8.5rem',
  },
} as GridProps;

const bookingSummaryMobileContainerStyle = {
  display: {
    mobile: 'block',
    lg: 'none',
  },
  backgroundColor: 'lightGrey5',
};

const bookingSummaryMobileTriggerStyle = {
  justifyContent: 'center',
  fontWeight: 'bold',
};
const bookingSummaryDesktopStyle = {
  display: {
    mobile: 'none',
    lg: 'block',
  },
  w: {
    lg: '72',
    xl: '19.31rem',
  },
} as GridItemProps;
