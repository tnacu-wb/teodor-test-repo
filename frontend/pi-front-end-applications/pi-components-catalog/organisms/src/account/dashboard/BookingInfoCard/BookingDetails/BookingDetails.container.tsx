import { BoxProps, Flex } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import {
  Area,
  BCReservationListItem,
  CancellationInfoResponse,
  DonationPackage,
  DpaInfo,
  GET_AMEND_CONFIRMATION_PRICES,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
  GET_DASHBOARD_DONATIONS_PACKAGES,
  GET_PACKAGES,
  MealItemExtension,
  MealKids,
  OverridenUserInfo,
  PurposeOfStay,
  RoomSelection,
  RoomDetails,
  Price,
  LATE_CHECKOUT_IDS,
  EARLY_CHECKIN_IDS,
  WIFI_IDS,
  PROSECCO_IDS,
  ExtrasItem,
  ExtrasPackagePricePerItem,
  SelectedExtrasPackage,
  GET_HOTEL_INFORMATION,
  DISCOUNT_RATE_INFORMATION_QUERY,
  FT_PI_PIB_CCUI_CITY_TAX_AMEND,
} from '@whitbread-eos/api';
import { Alert, FormProps, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import {
  adultsMealsSelector,
  childrenMealsSelector,
  formatFindBookingToken,
  getDefaultDataFromBooking,
  getFindBookingToken,
  getMaxValueFromRoomStays,
  getNightsNumber,
  getSelectedDonationPackage,
  graphQLRequest,
  logger,
  mealsMapperSelector,
  selectedMealsPerRoomSelector,
  sortMealsByReservationId,
  useCustomLocale,
  extrasPackagesMapperSelector,
  analytics,
  useAuthToken,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useCallback, useEffect, useState } from 'react';

import { IDVDataProps } from '../IDVModal';
import BookingDetails from './BookingDetails.component';

export interface BookingDetailsProp {
  shouldDisplayCityTaxMessage: boolean;
  paymentOption: string;
  paymentMethod?: string;
  totalCost: string;
  previousTotal: string;
  balanceOutstanding: string;
  newTotal: string;
  currencyCode: string;
  hotelId: string;
  noOfRooms?: number;
  roomDetails: RoomDetails[];
  donationPkg: DonationPackage | undefined;
  rateType: string;
  cancellationInfoResponse?: CancellationInfoResponse;
  hotelName?: string;
  bookedFor?: string;
  arrivalDate?: string;
  noNights?: number;
  cardType?: string;
  guestSurname?: string;
  reasonForStay?: string;
  dinnerAllowance?: Price | null;
  rateTags?: string[];
  bookedBy?: string;
  cityTaxTotal?: number;
}

export interface Props {
  bookingReference: string;
  basketReference: string | null;
  tempBookingReference?: string;
  shouldShowTypeOfBooking?: boolean;
  area?: Area;
  getBookingStatus?: any;
  bookingStatus: string;
  bookingType?: string;
  paymentOption?: string;
  sourcePms?: string;
  dpaInfo?: DpaInfo;
  setDpaInfo?: (value: DpaInfo) => void;
  overridenUserInfo?: OverridenUserInfo;
  inputValues?: any;
  gdsReferenceNumber?: any;
  distBookingChannel?: any;
  isAmendSuccessful?: boolean;
  arrivalDate?: string;
  bookingSurname?: string;
  isReadOnly?: boolean;
  isAmendPage?: boolean;
  operaConfNumber?: string;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

export default function BookingDetailsContainer({
  bookingReference,
  basketReference,
  tempBookingReference,
  shouldShowTypeOfBooking,
  area = 'pi' as Area.PI,
  getBookingStatus,
  bookingStatus,
  bookingType,
  sourcePms,
  dpaInfo,
  setDpaInfo,
  overridenUserInfo,
  inputValues,
  gdsReferenceNumber,
  distBookingChannel,
  isAmendSuccessful,
  arrivalDate,
  bookingSurname,
  isReadOnly,
  isAmendPage,
  operaConfNumber,
  isRemovePIIDataFromLocalStorageEnabled = false,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const { t } = useTranslation();
  const [bookingState, setBookingState] = useState({
    isLoading: false,
    error: null,
  });
  const { token } = useAuthToken();
  const loggedOrCCUI = token || area === Area.CCUI;
  let rateTags: any[] = [];

  const [basketReferenceValue, setBasketReferenceValue] = useState<string | null>(basketReference);

  const [bookingDetails, setBookingDetails] = useState<BookingDetailsProp>({
    reasonForStay: '',
    currencyCode: '',
    totalCost: '',
    newTotal: '',
    paymentOption: '',
    paymentMethod: '',
    previousTotal: '',
    balanceOutstanding: '',
    hotelId: '',
    roomDetails: [],
    donationPkg: undefined,
    rateType: '',
    shouldDisplayCityTaxMessage: false,
    cardType: '',
    cityTaxTotal: 0,
  });

  const { [FT_PI_PIB_CCUI_CITY_TAX_AMEND]: isCityTaxAmendEnabled } = useFeatureToggle();
  const [defaultDataFromBooking, setDefaultDataFromBooking] =
    useState<FormProps['defaultValues']>();

  const getGuestName = (reservations: BCReservationListItem[]): string => {
    // TO DO: if guest surname is one of the searched criteria, display it instead of the first or second guest Name
    const firstReservation = reservations[0];
    const secondReservation = reservations[1];
    let guestName = `${firstReservation?.reservationGuestList[0]?.surName}\u0020${firstReservation?.reservationGuestList[0]?.givenName}`;

    if (reservations.length > 1) {
      guestName = `${secondReservation?.reservationGuestList[0]?.surName}\u0020${secondReservation?.reservationGuestList[0]?.givenName}`;
    }
    return guestName;
  };

  const [idvData, setIdvData] = useState<IDVDataProps>({
    personalInformation: {
      bookerName: '',
      guestName: '',
      address: '',
      postcode: '',
      telephoneNumber: '',
      cardUsedToMakeBooking: '',
    },
    bookingInformation: {
      reservationNumber: { value: '', partOfSearch: false },
      hotelName: '',
      arrivalDate: '',
      departureDate: '',
      emailAddress: '',
    },
    dpaStatus: {
      dpaPassed: dpaInfo?.dpaPassed as boolean,
      dpaOverride: dpaInfo?.dpaOverride as boolean,
      eCnpPassword: '',
    },
  });

  const queryClient = useQueryClient();

  const getBookingDetails = useCallback(
    async (
      bookingReference: string,
      basketReference: string | null,
      tempBookingReference?: string
    ) => {
      setBookingState({
        ...bookingState,
        isLoading: true,
      });

      let amendConfirmationPrices: {
        previousTotal: string;
        newTotalCost: string;
        outstandingBalance: string;
      };

      if (tempBookingReference) {
        try {
          const token = formatFindBookingToken(getFindBookingToken().token);
          const data = await queryClient.fetchQuery({
            queryKey: ['getAmendConfirmationPrices', basketReference, tempBookingReference],
            queryFn: () =>
              graphQLRequest(GET_AMEND_CONFIRMATION_PRICES, {
                originalBookingRef: basketReference,
                tempBookingRef: tempBookingReference,
                token,
              }),
          });
          amendConfirmationPrices = data.amendConfirmationPrices;
        } catch (error) {
          logger.error(error);
        }
      }

      const bookingConfirmationQuery = queryClient.fetchQuery({
        queryKey: [
          loggedOrCCUI ? 'getBookingConfirmationAuthenticated' : 'getBookingConfirmation',
          loggedOrCCUI ? bookingReference : basketReference,
          language,
          country,
        ],
        queryFn: () =>
          graphQLRequest(
            loggedOrCCUI
              ? GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED
              : GET_DASHBOARD_BOOKING_CONFIRMATION,
            loggedOrCCUI
              ? {
                  bookingReference,
                  language,
                  country,
                  bookingChannel: area?.toUpperCase(),
                }
              : {
                  basketReference,
                  language,
                  country,
                  bookingChannel: area?.toUpperCase(),
                },
            loggedOrCCUI ? token : undefined
          ),
        ...{
          gcTime: 0,
          staleTime: 0,
        },
      });

      await bookingConfirmationQuery
        .then(async (bookingConfirmationData) => {
          const bookingData = loggedOrCCUI
            ? bookingConfirmationData?.bookingConfirmationAuthenticated
            : bookingConfirmationData?.bookingConfirmation;
          // intermediary fix for testing. this use callback has to be rewritten
          const { bookingFlowId, hotelId, reservationByIdList, hotelName } = bookingData || {
            bookingFlowId: -1,
            reservationByIdList: [],
            hotelName: -1,
          };
          const firstRoom = reservationByIdList[0] || {
            roomStay: { arrivalDate: '', departureDate: '' },
          };
          const noNights = getNightsNumber(
            firstRoom.roomStay?.arrivalDate,
            firstRoom.roomStay?.departureDate
          );

          const basketReferenceBC = basketReference ?? (bookingData?.basketReference as string);
          setBasketReferenceValue(basketReferenceBC);

          try {
            rateTags = await fetchRateTags({
              hotelId,
              area: area?.toUpperCase(),
              ratePlanCode: firstRoom.roomStay?.ratePlanCode,
            });
          } catch {
            return [];
          }

          const pcksQueryInput = {
            country,
            language,
            hotelId: hotelId,
            basketReferenceId: basketReferenceBC,
            adultsNumber: getMaxValueFromRoomStays(reservationByIdList, 'adultsNumber'),
            childrenNumber: getMaxValueFromRoomStays(reservationByIdList, 'childrenNumber'),
            startDate: firstRoom.roomStay.arrivalDate,
            endDate: firstRoom.roomStay.departureDate,
            bookingFlowId: bookingFlowId || '',
            nightsNumber: noNights,
            channel: area?.toUpperCase(),
            isManageBookingPage: true, // As part of DNRQ-78024, this enables EC/LCO packages only for dashboard
          };

          const getBookingPackages = queryClient.fetchQuery({
            queryKey: [
              'GetPackages',
              language,
              country,
              pcksQueryInput.hotelId,
              pcksQueryInput.bookingFlowId,
              pcksQueryInput.startDate,
              pcksQueryInput.endDate,
              pcksQueryInput.nightsNumber,
              pcksQueryInput.adultsNumber,
              pcksQueryInput.childrenNumber,
              pcksQueryInput.basketReferenceId,
              pcksQueryInput.channel,
              pcksQueryInput.isManageBookingPage,
            ],
            queryFn: () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }),
          });

          await getBookingPackages
            .then(async (pkgResponse) => {
              const pcksDonationQueryInput = {
                country,
                language,
                hotelId: hotelId,
                rateCode: firstRoom.roomStay.ratePlanCode,
              };

              const getDonationPackages = queryClient.fetchQuery({
                queryKey: [
                  'getDonationPackages',
                  country,
                  language,
                  hotelId,
                  firstRoom.roomStay.ratePlanCode,
                ],
                queryFn: () =>
                  graphQLRequest(GET_DASHBOARD_DONATIONS_PACKAGES, {
                    ...pcksDonationQueryInput,
                    bookingChannel: area?.toUpperCase(),
                  }),
              });
              let sortedMeals: RoomSelection[] = [];

              await getDonationPackages
                .then((pckDonation) => {
                  const { meals, mealsKids, roomSelection, extrasItems } =
                    pkgResponse?.packages?.packages || {};
                  sortedMeals = sortMealsByReservationId(
                    roomSelection,
                    bookingData.reservationByIdList
                  );

                  const adultsMeals: MealItemExtension[] = adultsMealsSelector(meals, noNights);
                  const childrenMeals: MealKids[] = childrenMealsSelector(mealsKids);

                  const mealsPerRoom = selectedMealsPerRoomSelector(
                    mealsMapperSelector(meals, mealsKids, sortedMeals),
                    adultsMeals,
                    childrenMeals
                  );
                  const listExtrasPackagesMapped: ExtrasPackagePricePerItem[] = [];

                  const extrasPackagesPerRoom = extrasPackagesMapperSelector(roomSelection);
                  const eciItem = extrasItems?.find(
                    (item: ExtrasItem) =>
                      item?.id !== undefined &&
                      EARLY_CHECKIN_IDS.includes(item.id as (typeof EARLY_CHECKIN_IDS)[number])
                  );

                  const lcoItem = extrasItems?.find(
                    (item: ExtrasItem) =>
                      item?.id !== undefined &&
                      LATE_CHECKOUT_IDS.includes(item.id as (typeof LATE_CHECKOUT_IDS)[number])
                  );

                  const wifiItem = extrasItems?.find(
                    (item: ExtrasItem) =>
                      item?.id !== undefined &&
                      WIFI_IDS.includes(item.id as (typeof WIFI_IDS)[number])
                  );

                  const priceBOProseccoItem = extrasItems?.find(
                    (item: ExtrasItem) =>
                      item?.id !== undefined &&
                      PROSECCO_IDS.includes(item.id as (typeof PROSECCO_IDS)[number])
                  );
                  extrasPackagesPerRoom?.forEach((extra: SelectedExtrasPackage) => {
                    const extrasPackagesPerRoomWithPrices: ExtrasPackagePricePerItem = {
                      packagesList: extra?.packagesList,
                      reservationId: extra?.reservationId,
                      priceEci: eciItem?.price,
                      priceLco: lcoItem?.price,
                      priceWifi: wifiItem?.price,
                      priceBOProsecco: priceBOProseccoItem?.price,
                    };
                    listExtrasPackagesMapped.push(extrasPackagesPerRoomWithPrices);
                  });

                  const donationPkg =
                    pckDonation?.donations &&
                    getSelectedDonationPackage(
                      pckDonation?.donations?.donationPackages,
                      roomSelection[0]
                    );

                  const reasonForStay =
                    bookingData?.reservationByIdList[0]?.additionalGuestInfo?.purposeOfStay;

                  const shouldDisplayCityTaxMessage =
                    (reasonForStay === PurposeOfStay.LEISURE &&
                      pkgResponse.packages.hotelHasCityTaxForLeisure) ||
                    (reasonForStay === PurposeOfStay.BUSINESS &&
                      pkgResponse.packages.hotelHasCityTaxForBusiness);
                  setBookingDetails({
                    reasonForStay: reasonForStay,
                    shouldDisplayCityTaxMessage: shouldDisplayCityTaxMessage,
                    hotelId: hotelId,
                    // bookingData is from the query
                    paymentOption: bookingData.reservationByIdList[0].guaranteeCode,
                    paymentMethod:
                      bookingData.reservationByIdList[0].paymentCard?.paymentMethod ?? '',
                    currencyCode: bookingData.currencyCode,
                    totalCost: bookingData.totalCost,
                    cityTaxTotal: bookingData.cityTaxTotal,
                    previousTotal: amendConfirmationPrices
                      ? amendConfirmationPrices.previousTotal
                      : bookingData.previousTotal,
                    balanceOutstanding: amendConfirmationPrices
                      ? amendConfirmationPrices.outstandingBalance
                      : bookingData.balanceOutstanding,
                    newTotal: amendConfirmationPrices
                      ? amendConfirmationPrices.newTotalCost
                      : bookingData.newTotal,
                    donationPkg: donationPkg,
                    rateType: firstRoom.roomStay?.ratePlanCode,
                    rateTags: rateTags,
                    roomDetails: reservationByIdList.map(
                      (room: BCReservationListItem, index: number) => {
                        const extrasPackagePerRoom = listExtrasPackagesMapped?.find(
                          (extrasItem: ExtrasPackagePricePerItem) =>
                            extrasItem.reservationId === room?.reservationId
                        );
                        return {
                          leadGuestName: `${room?.reservationGuestList?.[0]?.givenName} ${room?.reservationGuestList?.[0]?.surName}`,
                          roomType: room?.roomStay?.roomExtraInfo?.roomName,
                          roomPrice: room?.roomStay?.roomPrice,
                          adultMealDescription: mealsPerRoom[index]?.adultsMeals,
                          childrenMealDescription: mealsPerRoom[index]?.childrenMeals,
                          extrasPackageRoom: extrasPackagePerRoom,
                          mealPrice: 0,
                          cot: room?.roomStay.cot,
                          noAdults: room?.roomStay.adultsNumber,
                          noChildren: room?.roomStay.childrenNumber,
                          noNights: noNights,
                        };
                      }
                    ) as unknown as RoomDetails[],
                  });

                  setIdvData({
                    personalInformation: {
                      bookerName: reservationByIdList[0]?.billing
                        ? `${reservationByIdList[0]?.billing?.lastName}\u0020${reservationByIdList[0]?.billing?.firstName}`
                        : '',
                      guestName: getGuestName(reservationByIdList),
                      address: reservationByIdList[0]?.billing
                        ? `${reservationByIdList[0]?.billing?.address?.addressLine1}`
                        : '',
                      postcode: reservationByIdList[0]?.billing
                        ? `${reservationByIdList[0]?.billing?.address?.postalCode}`
                        : '',
                      telephoneNumber: reservationByIdList[0]?.billing
                        ? `${reservationByIdList[0]?.billing?.telephone}`
                        : '',
                      cardUsedToMakeBooking: reservationByIdList[0]?.paymentCard?.cardNumberMasked,
                    },
                    bookingInformation: {
                      reservationNumber: {
                        ...idvData.bookingInformation.reservationNumber,
                        value: bookingReference,
                      },
                      hotelName: hotelName,
                      arrivalDate: firstRoom.roomStay?.arrivalDate,
                      departureDate: firstRoom.roomStay?.departureDate,
                      emailAddress: reservationByIdList[0]?.billing
                        ? reservationByIdList[0]?.billing?.email
                        : '',
                    },
                    dpaStatus: {
                      ...idvData.dpaStatus,
                      // TO DO: replace eCnpPassword with data from BE
                    },
                  });

                  const defaultData = getDefaultDataFromBooking(
                    reservationByIdList ?? [],
                    {},
                    basketReferenceValue ?? '',
                    language,
                    '' as any
                  );

                  const billing = reservationByIdList[0]?.billing;
                  setDefaultDataFromBooking(
                    billing
                      ? {
                          ...defaultData,
                          title: defaultData.title || (billing.title ?? ''),
                          firstName: defaultData.firstName || (billing.firstName ?? ''),
                          lastName: defaultData.lastName || (billing.lastName ?? ''),
                          email: defaultData.email || (billing.email ?? ''),
                          phone: defaultData.phone || (billing.telephone ?? ''),
                          addressLine1:
                            defaultData.addressLine1 || (billing.address?.addressLine1 ?? ''),
                          addressLine2:
                            defaultData.addressLine2 || (billing.address?.addressLine2 ?? ''),
                          addressLine3:
                            defaultData.addressLine3 || (billing.address?.addressLine3 ?? ''),
                          addressLine4:
                            defaultData.addressLine4 || (billing.address?.addressLine4 ?? ''),
                          cityName:
                            defaultData.cityName ||
                            billing.address?.cityName ||
                            billing.address?.addressLine4 ||
                            '',
                          postalCode: defaultData.postalCode || (billing.address?.postalCode ?? ''),
                          companyName:
                            defaultData.companyName || (billing.address?.companyName ?? ''),
                        }
                      : defaultData
                  );

                  setBookingState({
                    ...bookingState,
                    isLoading: false,
                  });
                })
                .catch((error) => {
                  setBookingState({
                    isLoading: false,
                    error,
                  });
                  console.log(error);
                });
            })
            .catch((error) => {
              setBookingState({
                isLoading: false,
                error,
              });
              console.log(error);
            });
        })
        .catch((error) => {
          setBookingState({
            isLoading: false,
            error,
          });
          console.log(error);
        });
    },
    []
  );

  useEffect(() => {
    getBookingDetails(bookingReference, basketReference, tempBookingReference);
  }, [bookingReference, basketReference, tempBookingReference]);

  async function fetchRateTags(params: { hotelId: string; area: string; ratePlanCode: string }) {
    const { hotelId, area, ratePlanCode } = params;
    const getHotelBrandRequest = queryClient.fetchQuery({
      queryKey: ['GetHotelInformation', hotelId, country, language],
      queryFn: () => graphQLRequest(GET_HOTEL_INFORMATION, { hotelId, language, country }),
    });

    await getHotelBrandRequest
      .then(async (data) => {
        const rateData = await queryClient.fetchQuery({
          queryKey: [
            'ratesInformationDiscountRate',
            language,
            country,
            data?.hotelInformation?.brand,
            hotelId,
            area?.toUpperCase(),
            ratePlanCode,
          ],
          queryFn: () =>
            graphQLRequest(DISCOUNT_RATE_INFORMATION_QUERY, {
              hotelId: hotelId,
              brand: data?.hotelInformation?.brand.toLowerCase(),
              language,
              country,
              channel: area?.toUpperCase(),
              ratePlans: ratePlanCode,
            }),
        });
        rateTags = rateData.ratesInformation?.rateClassifications?.[0]?.rateTags ?? [];

        if (rateTags && rateTags.length > 0) {
          const currentData = window?.analyticsData ?? {};
          analytics.update({
            ...currentData,
            promo: {
              promoName: rateTags[0],
            },
          });
        }
      })
      .catch(() => {
        return [];
      });

    return rateTags;
  }

  if (bookingState.error) {
    return (
      <Notification
        status="error"
        description={bookingState.error as string}
        variant="alert"
        maxW="full"
        svg={<Alert />}
      />
    );
  }

  if (bookingState.isLoading) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-BookingDetails">
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  return (
    <BookingDetails
      bookingDetails={bookingDetails}
      idvData={idvData}
      setIdvData={setIdvData}
      defaultDataFromBooking={defaultDataFromBooking}
      bookingReference={bookingReference}
      basketReference={basketReferenceValue}
      area={area}
      getBookingStatus={getBookingStatus}
      bookingStatus={bookingStatus}
      bookingType={bookingType}
      shouldShowTypeOfBooking={shouldShowTypeOfBooking}
      sourcePms={sourcePms}
      dpaInfo={dpaInfo}
      setDpaInfo={setDpaInfo}
      baseDataTestId={'BookingDetails'}
      overridenUserInfo={overridenUserInfo}
      inputValues={inputValues}
      gdsReferenceNumber={gdsReferenceNumber}
      distBookingChannel={distBookingChannel}
      isAmendSuccessful={isAmendSuccessful}
      arrival={arrivalDate}
      bookingSurname={bookingSurname}
      isReadOnly={isReadOnly}
      isAmendPage={isAmendPage}
      operaConfNumber={operaConfNumber}
      isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
      isCityTaxAmendEnabled={isCityTaxAmendEnabled}
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
