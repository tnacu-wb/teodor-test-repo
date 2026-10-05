import { Box, BoxProps, Button, Divider, Flex, SimpleGrid, Text } from '@chakra-ui/react';
import {
  BASKET_STATUS,
  BCReservationListItem,
  BOOKING_CHANNEL,
  GET_BASKET,
  GET_BOOKING_CONFIRMATION,
  GET_HOTEL_INFORMATION,
  GET_PROMOTION_PANEL,
  GET_STATIC_CONTENT,
  QueryHotelInformationArgs,
  HotelBrand,
  LanguageEnum,
  PageName,
  PAYMENT_FAILED_INITIAL_VALUE,
  PAYMENT_FAILED_KEY,
  PAYMENT_FAILED_VALUE,
  MealItem,
  MealKids,
  PrivacyPolicy,
  PackagesCriteria,
  PromotionQueryInput,
  SelectedMealsPerRoom,
  StaticContentQueryInput,
  Area,
  PAYMENT_ANALYTICS_KEY,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
} from '@whitbread-eos/api';
import {
  Card,
  ConfirmationDetails,
  ConfirmationPromotion,
  FailConfirmation,
  HotelDirections,
  Icon,
  Info,
  LoadingSpinner,
  Newsletter,
  Notification,
  Printer,
  RoomDetails,
  ThanksForBooking,
  TotalCost,
} from '@whitbread-eos/atoms';
import {
  AnnouncementNotification,
  DataSecuritySection,
  SEO as Seo,
} from '@whitbread-eos/molecules';
import {
  adultsMealsSelector,
  analytics,
  analyticsConfirmation,
  analyticsConfirmationPageName,
  childrenMealsSelector,
  getImportantMessages,
  getAdultMealDescription,
  getChildrenMealDescription,
  getCityTaxMessages,
  getMealPrice,
  getNightsNumber,
  getRoomGroups,
  getRoomTotalprice,
  initConfirmationAnalytics,
  logicalOrOperator,
  mealsMapperSelector,
  securityNoticeMoreInfoDataSelector,
  selectedMealsPerRoomSelector,
  updateConfirmationAnalytics,
  useCustomLocale,
  usePackages,
  usePollBasketStatus,
  useQueryRequest,
  updateConfirmationPageAnalytics,
  roomPackageSelection,
  extrasPackagesMapperSelector,
  useSessionStorage,
  isInnBusinessApp,
  renderSanitizedHtml,
  useFeatureToggle,
  useLocalStorage,
  getCookie,
  deleteCookie,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { NextRouter } from 'next/router';
import React, { useEffect, useMemo, useState } from 'react';

interface Props {
  router: NextRouter;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  staticContentQueryInput: StaticContentQueryInput;
  basketReference: string | null;
  promotionQueryInput: PromotionQueryInput;
}

export default function ConfirmationPageBb({
  router,
  pcksQueryInput,
  hiQueryInput,
  staticContentQueryInput,
  basketReference,
  promotionQueryInput,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { language, country } = useCustomLocale();

  const isWindowDefined = typeof window !== 'undefined';
  const isInnBusiness = isWindowDefined
    ? isInnBusinessApp(window.location.host)
    : isInnBusinessApp('');

  const [origin, setOrigin] = useState('');

  useEffect(() => {
    setOrigin(window.location.origin);
  }, [isWindowDefined]);

  const [, setPaymentFailedValue] = useSessionStorage<string>(
    PAYMENT_FAILED_KEY,
    PAYMENT_FAILED_INITIAL_VALUE
  );

  /** [1/2] when transitioning to this page a confirmation mutation is sent from the previous page... */
  const {
    isLoading: isLoadingBookingConfirmation,
    data: bkngData,
    refetch: refetchBookingConfirmationFn,
  } = useQueryRequest(
    ['GetBookingConfirmation', language, hiQueryInput.country, basketReference, BOOKING_CHANNEL.BB],
    GET_BOOKING_CONFIRMATION,
    {
      basketReference,
      language,
      country: hiQueryInput.country,
      bookingChannel: BOOKING_CHANNEL.BB,
    },
    { enabled: false }
  );

  const { [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled } = useFeatureToggle();
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  const bookingReference = bkngData?.bookingConfirmation?.bookingReference;

  useEffect(() => {
    if (bookingReference) {
      analyticsConfirmation.update({ bookingId: bookingReference });
    }
  }, [bookingReference]);

  /** [1/2] on this page a basket status check happens on-demand inside the effect... */
  const { isLoading: isLoadingBasketData, data: basketData } = useQueryRequest(
    ['GetBasket', basketReference],
    GET_BASKET,
    {
      basketReference,
    }
  );

  const { pollingInProgress, basketStatus, dynamicSpinnerLabel, retryPayment } =
    usePollBasketStatus(
      basketReference ?? '',
      bkngData?.bookingConfirmation?.bookingSpinnerConfig ?? []
    );

  const displaySuccessSection = useMemo(
    () => basketStatus === BASKET_STATUS.COMPLETED,
    [basketStatus]
  );

  const shouldRedirectToPaymentsPage = useMemo(
    () =>
      (basketStatus === BASKET_STATUS.FAILED || basketStatus === BASKET_STATUS.SECURE_FAILED) &&
      retryPayment,
    [basketStatus, retryPayment]
  );

  /** [2/2] ...this effect will trigger RQ's-refetch function once and checks for booking confirmation status */
  useEffect(() => {
    refetchBookingConfirmationFn();
    const currentData = window?.analyticsData ?? {};
    analytics.update({
      pageName: analyticsConfirmationPageName,
    });
    if (displaySuccessSection) {
      analytics.track('promotionBooking');
      analytics.update({
        ...currentData,
        promo: {
          promoName: basketDetailsState?.selectedRate?.promoKind,
          promoCode: basketDetailsState?.selectedRate?.promotionCode,
        },
        promoBookingComplete:
          basketDetailsState?.rateTags && basketDetailsState?.rateTags?.length > 0 ? true : false,
      });
      const promotionBoxCodeCookie = getCookie('appliedPromoBoxCode');
      const promotionCode = basketDetailsState?.selectedRate?.promotionCode;
      if (promotionBoxCodeCookie === promotionCode) {
        deleteCookie('appliedPromoBoxCode');
      }
    }
    initConfirmationAnalytics(displaySuccessSection);
  }, [refetchBookingConfirmationFn, displaySuccessSection]);

  useEffect(() => {
    if (shouldRedirectToPaymentsPage) {
      let queryString = '';
      if (router?.query?.['secure-booking'] === 'true') {
        queryString = '&secure-booking=true';
      }
      setPaymentFailedValue(PAYMENT_FAILED_VALUE);
      analytics.update({
        hasPaymentFailure: true,
      });
      router.push(
        `/${country}/${language}/business-booker/booking-business/payment?reservationId=${basketReference}${queryString}`
      );
    }
  }, [shouldRedirectToPaymentsPage]);

  const {
    isError: isLoadingConfirmationPcksError,
    isLoading: isLoadingConfirmationPcks,
    packages,
    privacyPolicy,
    hotelHasCityTaxForLeisure,
    hotelHasCityTaxForBusiness,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber as number,
    childrenNumber: pcksQueryInput.childrenNumber as number,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: basketReference as string,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber as number,
    channel: pcksQueryInput.channel,
  });

  useEffect(() => {
    const paymentAnalytics = sessionStorage.getItem(PAYMENT_ANALYTICS_KEY);
    const paymentAnalyticsData = paymentAnalytics && JSON.parse(paymentAnalytics);
    if (bkngData && packages && paymentAnalyticsData?.basketReference === basketReference) {
      updateConfirmationPageAnalytics(
        bkngData.bookingConfirmation,
        packages,
        paymentAnalyticsData,
        Area.BB
      );
    }
  }, [bkngData, packages]);

  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    GET_HOTEL_INFORMATION,
    {
      ...hiQueryInput,
    }
  );

  const { isSuccess: isNewsletterSuccess, data: newsletterData } = useQueryRequest(
    ['GetStaticContent', staticContentQueryInput.language, staticContentQueryInput.country],
    GET_STATIC_CONTENT,
    {
      ...staticContentQueryInput,
    }
  );

  useEffect(() => {
    analyticsConfirmation.update({ userisSubscribed: isNewsletterSuccess });
  }, [isNewsletterSuccess]);

  const { isSuccess: isPromotionPanelSuccess, data: promotionData } = useQueryRequest(
    [
      'GetPromotionPanel',
      promotionQueryInput.country,
      promotionQueryInput.language,
      promotionQueryInput.hotelId,
      promotionQueryInput.rateCode,
      BOOKING_CHANNEL.BB,
    ],
    GET_PROMOTION_PANEL,
    {
      ...promotionQueryInput,
    }
  );

  const privacyPolicyData: PrivacyPolicy = securityNoticeMoreInfoDataSelector(privacyPolicy ?? {});

  const meals = packages?.meals;
  const mealsKids = packages?.mealsKids;
  const roomSelection = packages?.roomSelection;
  const extrasItems = packages?.extrasItems;

  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);

  const firstRoom = logicalOrOperator(bkngData?.bookingConfirmation?.reservationByIdList[0], {});

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );
  const adultsMeals: MealItem[] = adultsMealsSelector(meals, noNights);
  const childrenMeals: MealKids[] = childrenMealsSelector(mealsKids);
  const currentReasonForStay =
    bkngData?.bookingConfirmation?.reservationByIdList?.[0]?.additionalGuestInfo?.purposeOfStay;

  const cityTaxMessages = getCityTaxMessages(
    hotelHasCityTaxForLeisure!, //!'hasCityTaxForLeisure',
    hotelHasCityTaxForBusiness!, // !!'hasCityTaxForBusiness',
    currentReasonForStay,
    t,
    bkngData?.bookingConfirmation?.currencyCode,
    language,
    bkngData?.bookingConfirmation?.totalCost
  );

  useEffect(() => {
    if (roomSelection && meals && mealsKids) {
      setSelectedMeals(mealsMapperSelector(meals, mealsKids, roomSelection));
    }
  }, [roomSelection, meals, mealsKids]);

  const mealsPerRoom = selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals);

  const getHotelAddress = () => {
    const {
      addressLine1 = '',
      addressLine2 = '',
      addressLine3 = '',
      postalCode = '',
    } = (!isLoadingHotelInformation && hiData?.hotelInformation?.address) || {};
    return brand === 'PID'
      ? [addressLine1, postalCode, addressLine2, addressLine3].filter(Boolean).join(', ')
      : [addressLine1, addressLine2, addressLine3, postalCode].filter(Boolean).join(', ');
  };

  const brand = hiData?.hotelInformation?.brand;
  let notifications = [];
  if (basketData?.basket?.paymentOption === 'PAY_NOW' && language === 'en') {
    notifications.push(t('booking.summary.paymentReceived'));
  }

  notifications = [
    ...notifications,
    ...getImportantMessages(
      hiData?.hotelInformation?.importantInfo?.infoItems,
      bkngData?.bookingConfirmation?.reservationByIdList[0]?.roomStay?.arrivalDate,
      bkngData?.bookingConfirmation?.reservationByIdList[0]?.roomStay?.departureDate
    ),
  ];

  const thanksForBookingData = {
    t,
    currentLang: language,
    data: {
      thanksForBooking: {
        title: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.title,
        lastName: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.lastName,
        firstName: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.firstName,
        emailAddress: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.email,
      },
    },
  };

  const confirmationDetailsData = {
    currentLang: language,
    t,
    data: {
      confirmationDetails: {
        bookingReference,
        hotelName: hiData?.hotelInformation?.name,
        hotelAddress: getHotelAddress(),
        hotelTel: hiData?.hotelInformation?.contactDetails?.hotelNationalPhone,
        roomReservationStartDate:
          bkngData?.bookingConfirmation?.reservationByIdList[0]?.roomStay?.arrivalDate,
        roomReservationEndDate:
          bkngData?.bookingConfirmation?.reservationByIdList[0]?.roomStay?.departureDate,
        leadGuestTitle: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.title,
        leadGuestName: `${bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.lastName} ${bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.firstName}`,
        rateType:
          bkngData?.bookingConfirmation?.reservationByIdList[0]?.roomStay?.rateExtraInfo?.rateName,
        paymentOption: basketData?.basket?.paymentOption,
      },
    },
  };

  useEffect(() => {
    const { postalCode: bookingZipCode } = hiData?.hotelInformation?.address || '';

    analyticsConfirmation.update({
      bookingZipCode,
      price: {
        cartTotal: {
          amount: '',
          currency: '',
        },
        currency: '',
        voucherCode: '',
        voucherDiscount: '',
      },
    });
  }, [confirmationDetailsData.data.confirmationDetails.hotelAddress.length > 0]);

  const roomDetailsData = {
    basketReference: basketReference ?? '',
    currentLang: language,
    t,
    data: {
      selectedPaymentOption: basketData?.basket?.paymentOption,
      bookingTotalCost: bkngData?.bookingConfirmation.totalCost,
      currency: bkngData?.bookingConfirmation?.currencyCode,
      roomDetails: bkngData?.bookingConfirmation?.reservationByIdList.map(
        (rez: BCReservationListItem, index: number) => {
          return {
            roomReservationStartDate: rez?.roomStay?.arrivalDate,
            roomReservationEndDate: rez?.roomStay?.departureDate,
            leadGuestTitle: rez?.reservationGuestList?.[0]?.nameTitle,
            leadGuestName: `${rez?.reservationGuestList?.[0]?.givenName} ${rez?.reservationGuestList?.[0]?.surName}`,
            roomType: rez?.roomStay?.roomExtraInfo?.roomName,
            roomTypeDescription: rez?.roomStay?.roomExtraInfo?.roomDescription,
            rateType: rez?.roomStay?.rateExtraInfo?.rateName,
            rateTypeDescription: rez?.roomStay?.rateExtraInfo?.rateDescription,
            roomGroup: getRoomGroups(rez, t),
            packages: rez?.reservationPackageList,
            roomPrice: rez?.roomStay?.roomPrice,
            roomTotalPrice: getRoomTotalprice(rez),
            ratesPerNight: rez?.roomStay?.ratesPerNight,
            adultMealDescription: getAdultMealDescription(mealsPerRoom[index]?.adultsMeals, t),
            childrenMealDescription: getChildrenMealDescription(
              mealsPerRoom[index]?.childrenMeals,
              t
            ),
            mealPrice:
              getMealPrice(mealsPerRoom[index]) * Number(rez?.roomStay?.ratesPerNight?.length ?? 0),
            extrasRoomSelection: roomPackageSelection(extrasPackagesMapperSelector(roomSelection)),
            packagesExtrasItems: extrasItems,
          };
        }
      ),
      taxesMessage: cityTaxMessages?.summaryText,
      brand,
    },
  };

  useEffect(() => {
    const { totalCost: amount, currencyCode: currency } = bkngData?.bookingConfirmation || '';

    analyticsConfirmation.update({
      price: {
        cartTotal: {
          amount,
          currency,
        },
        currency,
        voucherCode: '',
        voucherDiscount: '',
      },
    });
    analytics.update({
      dailyRates: bkngData?.bookingConfirmation?.reservationByIdList.map(
        (rez: BCReservationListItem) => {
          return rez?.roomStay?.ratesPerNight;
        }
      ),
      currencyCode: currency,
    });
    updateConfirmationAnalytics();

    if (!pollingInProgress) {
      analytics.update({
        hasPaymentFailure: !displaySuccessSection,
      });
    }
  }, [pollingInProgress, displaySuccessSection]);

  const failConfirmationData = {
    t,
    data: {
      emailAddress: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.email + ' ',
      bookingReference,
      notificationText: t('booking.confirmation.paymentMessage'),
      paymentOption: basketData?.basket?.paymentOption,
    },
  };
  const showConfirmationPromo =
    language === LanguageEnum.ENGLISH && brand === HotelBrand.PI && isPromotionPanelSuccess;

  const showNewsletter =
    language === LanguageEnum.GERMAN &&
    isNewsletterSuccess &&
    (brand === HotelBrand.PI || brand === HotelBrand.PID);

  if (
    isLoadingHotelInformation ||
    isLoadingBookingConfirmation ||
    isLoadingConfirmationPcks ||
    isLoadingBasketData ||
    pollingInProgress ||
    shouldRedirectToPaymentsPage
  ) {
    return (
      <Flex {...loadingStyle} data-testid="Loading">
        <LoadingSpinner loadingText={dynamicSpinnerLabel} />
      </Flex>
    );
  }

  return (
    <>
      <Seo
        page={PageName.CONFIRMATION}
        hotelId={bkngData?.bookingConfirmation?.hotelId}
        bookingFlowId={bkngData?.bookingConfirmation?.bookingFlowId}
      />
      <SimpleGrid columns={2} spacing={135} {...gridStyle}>
        <Box maxW="54rem" {...pageContentStyle}>
          {displaySuccessSection ? (
            <>
              <ThanksForBooking {...thanksForBookingData} />
              <AnnouncementNotification
                announcement={hiData?.hotelInformation?.announcement}
                styles={{ maxW: { md: 'full' } }}
              />
              <Divider {...dividerStyles} sx={{ '@media print': { display: 'none' } }} />
              <ConfirmationDetails {...confirmationDetailsData} />
              {!!cityTaxMessages?.confPageBusinessNotif.length && (
                <Notification
                  description={cityTaxMessages?.confPageBusinessNotif}
                  maxWidth="full"
                  variant="infoGrey"
                  status="info"
                  svg={<Info />}
                  isInnerHTML
                  wrapperStyles={{
                    mb: '2.5rem',
                    sx: {
                      a: {
                        textDecoration: 'underline',
                        color: '#0000EE',
                      },
                      'a:visited': {
                        color: '#551A8B',
                      },
                    },
                  }}
                />
              )}
              <Flex
                display={{ lg: 'none' }}
                alignItems="center"
                sx={{ '@media print': { display: 'none' } }}
              >
                <Button
                  {...printButtonStyle}
                  data-testid="printBtn"
                  onClick={() => {
                    window.print();
                  }}
                >
                  <Icon svg={<Printer color="var(--chakra-colors-btnSecondaryEnabled)" />} px="2" />
                  <Text>{t('booking.summary.printDetails')}</Text>
                </Button>
              </Flex>
              <RoomDetails {...roomDetailsData} />

              <HotelDirections
                {...{ data: hiData }}
                apiKey={publicRuntimeConfig.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}
                t={t}
              />

              <TotalCost
                totalCostAmount={bkngData?.bookingConfirmation?.totalCost}
                currency={bkngData?.bookingConfirmation?.currencyCode}
                language={language}
                selectedPaymentOption={basketData?.basket?.paymentOption}
                taxesMessage={cityTaxMessages?.summaryText}
                isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
                cityTaxTotal={bkngData?.bookingConfirmation?.cityTaxTotal}
                t={t}
              />
            </>
          ) : (
            <FailConfirmation {...failConfirmationData} />
          )}
          <Box mb="2xl" sx={{ '@media print': { display: 'none' } }}>
            <Button
              size="md"
              variant="primary"
              data-testid="continueBtn"
              onClick={() => {
                window.location.href = isInnBusiness
                  ? `${origin}/${language}-${country}/homepage`
                  : `${origin}/${country}/${language}/business-booker/home.html`;
              }}
            >
              {t('booking.summary.continuetoHomepage')}
            </Button>
          </Box>
          {!isLoadingConfirmationPcksError && displaySuccessSection && (
            <Box sx={{ '@media print': { display: 'none' } }}>
              <DataSecuritySection
                privacyPolicy={privacyPolicyData}
                prefixDataTestId={'PaymentPage'}
                containerStyle={{ mt: '2xl' }}
              />
            </Box>
          )}
        </Box>

        {displaySuccessSection ? (
          <Box {...rightPanelStyle} sx={{ '@media print': { display: 'none' } }}>
            <Card direction="column">
              <Button
                {...continueButtonStyle}
                data-testid="rightPanel-continueBtn"
                onClick={() => {
                  window.location.href = isInnBusiness
                    ? `${origin}/${language}-${country}/homepage`
                    : `${origin}/${country}/${language}/business-booker/home.html`;
                }}
              >
                {t('booking.summary.continuetoHomepage')}
              </Button>

              <>
                <Button
                  {...printButtonStyle}
                  data-testid="printBtn"
                  onClick={() => {
                    window.print();
                  }}
                >
                  <Icon svg={<Printer color="var(--chakra-colors-btnSecondaryEnabled)" />} px="2" />
                  <Text>{t('booking.summary.printDetails')}</Text>
                </Button>
                {notifications.length > 0 &&
                  notifications.map((notification, index) => (
                    <Box
                      mb="lg"
                      data-testid={`notification-${index + 1}`}
                      key={`notification-${index + 1}`}
                    >
                      <Notification
                        maxWidth="full"
                        variant="info"
                        status="info"
                        description={
                          <Box className="formatLinks">{renderSanitizedHtml(notification)}</Box>
                        }
                        svg={<Info />}
                      />
                    </Box>
                  ))}
                {showConfirmationPromo && (
                  <ConfirmationPromotion {...{ data: promotionData, routerPush: router.push }} />
                )}
                {showNewsletter && (
                  <Newsletter {...{ data: newsletterData.footer, routerPush: router.push }} />
                )}
              </>
            </Card>
          </Box>
        ) : (
          <Box {...rightPanelStyle} sx={{ '@media print': { display: 'none' } }}>
            <Card direction="column">
              <Button
                {...continueButtonStyle}
                data-testid="rightPanel-continueBtn"
                onClick={() => {
                  window.location.href = isInnBusiness
                    ? `${origin}/${language}-${country}/homepage`
                    : `${origin}/${country}/${language}/business-booker/home.html`;
                }}
              >
                {t('booking.summary.continuetoHomepage')}
              </Button>

              <Box mb="lg" data-testid={`notification-fail`} key={`notification-fail`}>
                <Notification
                  maxWidth="full"
                  variant="info"
                  status="info"
                  description={failConfirmationData.data.notificationText}
                  svg={<Info />}
                />
              </Box>
            </Card>
          </Box>
        )}
      </SimpleGrid>
    </>
  );
}

const gridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: {
    mobile: '0px',
    lg: '7',
    xl: '5xl',
  },
  pb: {
    mobile: 'md',
    sm: '5',
    md: 'lg',
    lg: '7',
    xl: '5xl',
  },
  pt: {
    mobile: '0px',
    lg: '5xl',
  },
  m: '0px',
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0px',
    lg: '32',
    xl: '8.5rem',
  },
};

const pageContentStyle = {
  px: {
    mobile: 'md',
    sm: '5',
    md: 'lg',
    lg: '0px',
  },
  pt: {
    mobile: 'lg',
    sm: 'xl',
    md: '2xl',
    lg: '0px',
  },
};

const dividerStyles = {
  my: '2xl',
};

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 1,
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

const rightPanelStyle = {
  display: {
    mobile: 'none',
    lg: 'block',
  },
  w: {
    lg: '18rem',
    xl: '19.313rem',
  },
};

const continueButtonStyle = {
  size: 'full',
  mb: 'lg',
  variant: 'primary',
};

const printButtonStyle = {
  size: 'full',
  mb: 'lg',
  variant: 'tertiary',
};
