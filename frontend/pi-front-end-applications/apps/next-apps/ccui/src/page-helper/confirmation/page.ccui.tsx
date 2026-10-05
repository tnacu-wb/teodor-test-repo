import { Box, BoxProps, Button, Divider, Flex, SimpleGrid, Text } from '@chakra-ui/react';
import styled from '@emotion/styled';
import {
  Area,
  BASKET_STATUS,
  BCReservationListItem,
  BOOKING_CHANNEL,
  GET_BASKET,
  GET_BOOKING_CONFIRMATION,
  GET_HOTEL_INFORMATION,
  QueryHotelInformationArgs,
  PAYMENT_ANALYTICS_KEY,
  MealItem,
  MealKids,
  PackagesCriteria,
  SelectedMealsPerRoom,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  type Claims,
} from '@whitbread-eos/api';
import {
  Alert,
  Card,
  ConfirmationDetails,
  FailConfirmation,
  HotelDirections,
  Info,
  LoadingSpinner,
  Notification,
  RoomDetails,
  ThanksForBooking,
  TotalCost,
} from '@whitbread-eos/atoms';
import { CreateMyPiAccountContainer } from '@whitbread-eos/molecules';
import { AgentMemo } from '@whitbread-eos/organisms';
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
  selectedMealsPerRoomSelector,
  useBookingSpinner,
  useCustomLocale,
  usePackages,
  useQueryRequest,
  usePollBasketStatus,
  updateConfirmationAnalytics,
  updateConfirmationPageAnalytics,
  roomPackageSelection,
  extrasPackagesMapperSelector,
  useFeatureToggle,
  useLocalStorage,
  getCookie,
  deleteCookie,
  renderSanitizedHtml,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { NextRouter } from 'next/router';
import { useEffect, useState, useMemo } from 'react';

interface Props {
  router: NextRouter;
  setAnalyticsUser: any;
  user: Claims | undefined;
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
}
interface ReservationPackage {
  computedPrice: number;
  description: string;
  totalQuantity: number;
  unitPrice: number;
}

export default function ConfirmationPageCcui({
  router,
  setAnalyticsUser,
  user,
  pcksQueryInput,
  hiQueryInput,
  basketReference,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const onModalClose = () => setIsModalVisible((prevState) => !prevState);

  /** [1/2] when transitioning to this page a confirmation mutation is sent from the previous page [managed by POD-Cyan]... */
  const { isLoading: isLoadingBookingConfirmation, data: bkngData } = useQueryRequest(
    [
      'GetBookingConfirmation',
      language,
      hiQueryInput.country,
      basketReference,
      BOOKING_CHANNEL.CCUI,
    ],
    GET_BOOKING_CONFIRMATION,
    {
      language,
      country: hiQueryInput.country,
      basketReference,
      bookingChannel: BOOKING_CHANNEL.CCUI,
    }
  );

  /** [1/2] on this page a basket status check happens on-demand inside the effect.... */
  const {
    isLoading: isLoadingBasketData,
    data: basketData,
    refetch: refetchBasketDataFn,
  } = useQueryRequest(
    ['GetBasket', basketReference],
    GET_BASKET,
    {
      basketReference,
    },
    {
      gcTime: 0,
      enabled: false,
    }
  );
  const { basketStatus, pollingInProgress } = usePollBasketStatus(
    basketReference ?? '',
    bkngData?.bookingConfirmation?.bookingSpinnerConfig ?? []
  );

  const displaySuccessSection = useMemo(
    () => basketStatus === BASKET_STATUS.COMPLETED,
    [basketStatus]
  );
  const { [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled } = useFeatureToggle();

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  useEffect(() => {
    const currentData = window?.analyticsData ?? {};
    if (displaySuccessSection) {
      analytics.track('promotionBooking');
      analytics.update({
        ...currentData,
        pageName: analyticsConfirmationPageName,
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
  }, [displaySuccessSection]);

  /** [2/2] this custom hook calls the basket status every 1 second and also checks for the confirmation, thereby returning appropriate spinner text to the user; basis on the time elapsed */
  const { isTimerAchieved, dynamicSpinnerLabel } = useBookingSpinner(
    basketData?.basket?.status,
    bkngData,
    refetchBasketDataFn,
    t
  );

  const bookingReference = bkngData?.bookingConfirmation?.bookingReference;

  useEffect(() => {
    if (bookingReference) {
      analyticsConfirmation.update({ bookingId: bookingReference });
    }
  }, [bookingReference]);

  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    GET_HOTEL_INFORMATION,
    {
      ...hiQueryInput,
    }
  );

  const {
    isLoading: isLoadingConfirmationPcks,
    packages,
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

  const meals = packages?.meals;
  const mealsKids = packages?.mealsKids;
  const roomSelection = packages?.roomSelection;
  const extrasItems = packages?.extrasItems;

  const brand = hiData?.hotelInformation?.brand;

  useEffect(() => {
    const paymentAnalytics = sessionStorage.getItem(PAYMENT_ANALYTICS_KEY);
    const paymentAnalyticsData = paymentAnalytics && JSON.parse(paymentAnalytics);
    if (bkngData && packages && paymentAnalyticsData?.basketReference === basketReference) {
      updateConfirmationPageAnalytics(
        bkngData.bookingConfirmation,
        packages,
        paymentAnalyticsData,
        Area.CCUI
      );
    }
  }, [bkngData, packages]);

  useEffect(() => {
    if (roomSelection && meals && mealsKids) {
      setSelectedMeals(mealsMapperSelector(meals, mealsKids, roomSelection));
    }
  }, [roomSelection, meals, mealsKids]);

  const firstRoom = logicalOrOperator(bkngData?.bookingConfirmation?.reservationByIdList[0], {});

  const currentReasonForStay =
    bkngData?.bookingConfirmation?.reservationByIdList?.[0]?.additionalGuestInfo?.purposeOfStay;

  const cityTaxMessages = getCityTaxMessages(
    hotelHasCityTaxForLeisure,
    hotelHasCityTaxForBusiness,
    currentReasonForStay,
    t,
    bkngData?.bookingConfirmation?.currencyCode,
    language,
    bkngData?.bookingConfirmation?.totalCost
  );

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  const getDonationData = () => {
    return bkngData?.bookingConfirmation?.reservationByIdList[0]?.reservationPackageList?.find(
      (pkg: ReservationPackage) => pkg.description.includes('Charity')
    );
  };
  // adultsMealsSelector(meals, noNights, >>>noTotalAdults<<<) to be changed in future
  const adultsMeals: MealItem[] = adultsMealsSelector(meals, noNights, 0);
  const childrenMeals: MealKids[] = childrenMealsSelector(mealsKids);

  const mealsPerRoom = selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals);

  const getHotelAddress = () => {
    const {
      addressLine1 = '',
      addressLine2 = '',
      addressLine3 = '',
      postalCode = '',
    } = (!isLoadingHotelInformation && hiData?.hotelInformation?.address) || {
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      postalCode: '',
    };
    return brand === 'PID'
      ? [addressLine1, postalCode, addressLine2, addressLine3].filter(Boolean).join(', ')
      : [addressLine1, addressLine2, addressLine3, postalCode].filter(Boolean).join(', ');
  };

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
    currentLang: language,
    t,
    sendMail: basketData?.basket?.sendMail,
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
      donations: {
        amount: getDonationData()?.unitPrice,
        currency: bkngData?.bookingConfirmation?.currencyCode,
      },
      currency: bkngData?.bookingConfirmation?.currencyCode,
      bookingTotalCost: bkngData?.bookingConfirmation.totalCost,
      selectedPaymentOption: basketData?.basket?.paymentOption,
      roomDetails: bkngData?.bookingConfirmation?.reservationByIdList.map(
        (rez: BCReservationListItem, index: number) => {
          return {
            roomReservationStartDate: rez?.roomStay?.arrivalDate,
            roomReservationEndDate: rez?.roomStay?.departureDate,
            leadGuestTitle: rez?.reservationGuestList[0]?.nameTitle,
            leadGuestName: `${rez?.reservationGuestList[0]?.givenName} ${rez?.reservationGuestList[0]?.surName}`,
            roomType: rez?.roomStay?.roomExtraInfo?.roomName,
            roomTypeDescription: rez?.roomStay?.roomExtraInfo?.roomDescription,
            rateType: rez?.roomStay?.rateExtraInfo?.rateName,
            rateTypeDescription: rez?.roomStay?.rateExtraInfo?.rateDescription,
            roomGroup: getRoomGroups(rez, t),
            cot: rez?.roomStay?.cot,
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
  }, [pollingInProgress, displaySuccessSection]);

  //  AEM update with translation - Temporary until the real error handle is implemented
  const failConfirmationData = {
    t,
    data: {
      emailAddress: bkngData?.bookingConfirmation?.reservationByIdList[0]?.billing?.email + ' ',
      bookingReference,
      notificationText: t('ccui.booking.confirmation.paymentMessage'),
      paymentOption: basketData?.basket?.paymentOption,
      isCcui: true,
      sendMail: basketData?.basket?.sendMail,
    },
  };

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language]);

  useEffect(() => {
    if (!displaySuccessSection || isTimerAchieved)
      analyticsConfirmation.update({
        validation: 'opera related error',
        bookingId: bookingReference,
      });
  }, [displaySuccessSection, isTimerAchieved]);

  if (
    isLoadingHotelInformation ||
    isLoadingBookingConfirmation ||
    isLoadingConfirmationPcks ||
    isLoadingBasketData ||
    (!isTimerAchieved && basketData?.basket?.status === BASKET_STATUS.PROCESSING)
  ) {
    return (
      <Flex {...loadingStyle} data-testid="Loading">
        <LoadingSpinner loadingText={dynamicSpinnerLabel} />
      </Flex>
    );
  }

  return (
    <SimpleGrid columns={2} spacing={135} {...gridStyle}>
      <Box maxW="54rem" {...pageContentStyle}>
        {displaySuccessSection ? (
          <>
            <ThanksForBooking {...thanksForBookingData} />
            <Divider my="2xl" sx={{ '@media print': { display: 'none' } }} />
            <ConfirmationDetails {...confirmationDetailsData} />
            {!!cityTaxMessages?.confPageBusinessNotif.length && (
              <CustomNotification
                description={cityTaxMessages?.confPageBusinessNotif}
                maxWidth="full"
                variant="infoGrey"
                status="info"
                svg={<Info />}
                isInnerHTML
                wrapperStyles={{ mb: '2xl' }}
              />
            )}

            <Notification
              maxWidth="full"
              variant="alert"
              status="error"
              title={t('ccui.booking.confirmation.incorrectTotalCostNotification.title')}
              description={t(
                'ccui.booking.confirmation.incorrectTotalCostNotification.description'
              )}
              svg={<Alert />}
              wrapperStyles={{ mb: '2xl' }}
            />

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
              donation={getDonationData()}
              selectedPaymentOption={basketData?.basket?.paymentOption}
              isCCUI={true}
              t={t}
              taxesMessage={cityTaxMessages?.summaryText}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
              cityTaxTotal={bkngData?.bookingConfirmation?.cityTaxTotal}
            />
          </>
        ) : (
          <FailConfirmation {...failConfirmationData} />
        )}

        <Box mb="2xl" sx={{ '@media print': { display: 'none' } }}>
          <Button
            size="md"
            variant="primary"
            onClick={() => {
              router.push(`/${country}/${language}`);
            }}
          >
            {t('booking.summary.continuetoHomepage')}
          </Button>
        </Box>
      </Box>

      <Box {...rightPanelStyle} sx={{ '@media print': { display: 'none' } }}>
        <Card direction="column">
          <Button
            {...continueButtonStyle}
            data-testid="rightPanel-continueBtn"
            onClick={() => {
              router.push(`/${country}/${language}`);
            }}
          >
            {t('booking.summary.continuetoHomepage')}
          </Button>

          {displaySuccessSection ? (
            <>
              <Button
                {...printButtonStyle}
                data-testid="rightPanel-rebookBtn"
                isDisabled={false}
                onClick={() => {
                  router.push(
                    `/${country}/${language}/repeat-booking?reservationId=${basketReference}`
                  );
                }}
              >
                <Text>{t('ccui.manageBooking.options.repeatBooking')}</Text>
              </Button>

              <Button
                {...registerButtonStyle}
                data-testid="rightPanel-createPiAccountBtn"
                onClick={() => setIsModalVisible(true)}
              >
                <Text>{t('ccui.account.createAccount')}</Text>
              </Button>
              <CreateMyPiAccountContainer
                isModalVisible={isModalVisible}
                onModalClose={onModalClose}
              />
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
            </>
          ) : (
            <Box mb="lg" data-testid={`notification-fail`} key={`notification-fail`}>
              <Notification
                maxWidth="full"
                variant="info"
                status="info"
                description={failConfirmationData.data.notificationText}
                svg={<Info />}
              />
            </Box>
          )}
        </Card>
      </Box>
      <AgentMemo />
    </SimpleGrid>
  );
}

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

const registerButtonStyle = {
  size: 'full',
  mb: 'lg',
  variant: 'primary',
  _disabled: {
    bgColor: 'lightGrey3',
    color: 'tertiary',
    borderColor: 'lightGrey2',
  },
  cursor: 'not-allowed',
};

const CustomNotification = styled(Notification)`
  div a {
    text-decoration: underline;

    &:link {
      color: #0000ee;
    }

    &:visited {
      color: #551a8b;
    }
  }
`;
