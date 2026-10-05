'use client';

import {
  Area,
  BOOKING_CHANNEL,
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  GET_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PAYMENT_INFO_MESSAGES_QUERY,
  GET_TERMS_AND_CONDITIONS_QUERY,
  HotelBrand,
  MessagesPaymentType,
  PackagesCriteria,
  PaymentMethod,
  PaymentOption,
  PiCardType,
  QueryHotelInformationArgs,
} from '@whitbread-eos/api';
import { useMemo } from 'react';

import { createReservationDetails, formatUrlTermsConditions } from '../formatters';
import {
  getBookingSummaryData,
  getCityTaxMessages,
  getIsBillingAddressDisplayed,
  getMaxValueFromRoomStays,
  getNightsNumber,
} from '../getters';
import { getTotalCost } from '../helpers';
import {
  adultsMealsSelector,
  childrenMealsSelector,
  formatImportantNotes,
  mealsMapperSelector,
} from '../selectors';
import { useUpdateRateName } from './use-discounted-rate';
import usePackages from './use-packages';
import { useQueryRequest } from './use-request';

interface BookingSummaryBasketDetails {
  rateDescription?: string;
  rateTags?: string[];
}

interface UsePaymentDataArgs {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
  language: string;
  country: string;
  selectedPaymentDetail: PaymentOption;
  selectedPaymentType: PaymentMethod;
  paymentStepState: string;
  basketDetailsState: BookingSummaryBasketDetails;
  formData: any;
  onclickBillingFormHandler: () => void;
  t: (key: string, options?: any) => string;
}

interface UsePaymentDataReturn {
  bkngData: any;
  hiData: any;
  termsAndConditionsData: any;
  packages: any;
  bookingInformation: {
    hotelId?: string;
    adults?: number;
    children?: number;
    nrNights: number;
    ratePlanCode?: string;
    totalCost?: string;
  };
  reservationDetails: BookingDataReservationDetailsProps;
  bookingSummaryData: BookingSummaryDataProps;
  cityTaxMessages: { summaryText?: string };
  infoMessages: string[];
  orderedInfoMessages: { infoMsg: string; indexOrder: number }[];
  orderedListOfMessagesPaymentType: { index: number; messagesNotif: string }[];
  roomSelection: any;
  rooms: { adultsNumber: number; rate: string; type: string }[];
  hotelBrand: HotelBrand;
  isGermanHotel: boolean;
  isBillingAddressDisplayed: boolean;
  termsAndConditionsText: string;
  isLoading: boolean;
}

export default function usePaymentData({
  hiQueryInput,
  pcksQueryInput,
  basketReference,
  language,
  country,
  selectedPaymentDetail,
  selectedPaymentType,
  paymentStepState,
  basketDetailsState,
  formData,
  onclickBillingFormHandler,
  t,
}: UsePaymentDataArgs): UsePaymentDataReturn {
  const { isLoading: isLoadingBookingInformation, data: bkngData } = useQueryRequest(
    ['GetBookingInformation', language, country, basketReference],
    GET_BOOKING_INFORMATION,
    {
      basketReference,
      language,
      country,
      bookingChannelCriteria: {
        channel: 'PI',
        subchannel: 'WEB',
        language: language === 'en' ? 'EN' : 'DE',
      },
    }
  );

  const {
    isLoading: isLoadingPaymentPcks,
    packages,
    hotelHasCityTaxForBusiness,
    hotelHasCityTaxForLeisure,
  } = usePackages({
    adultsNumber: pcksQueryInput.adultsNumber,
    childrenNumber: pcksQueryInput.childrenNumber,
    hotelId: pcksQueryInput.hotelId,
    basketReferenceId: basketReference as string,
    endDate: pcksQueryInput.endDate,
    startDate: pcksQueryInput.startDate,
    bookingFlowId: pcksQueryInput.bookingFlowId,
    nightsNumber: pcksQueryInput.nightsNumber,
    channel: pcksQueryInput.channel,
  });

  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    GET_HOTEL_INFORMATION,
    { ...hiQueryInput }
  );

  const firstRoom = useMemo(
    () => bkngData?.bookingInformation?.reservationByIdList?.[0] || {},
    [bkngData?.bookingInformation?.reservationByIdList]
  );

  const noNights = useMemo(
    () => getNightsNumber(firstRoom.roomStay?.arrivalDate, firstRoom.roomStay?.departureDate),
    [firstRoom.roomStay?.arrivalDate, firstRoom.roomStay?.departureDate]
  );

  const bookingInformation = useMemo(
    () => ({
      hotelId: bkngData?.bookingInformation?.hotelId,
      adults: getMaxValueFromRoomStays(
        bkngData?.bookingInformation?.reservationByIdList,
        'adultsNumber'
      ),
      children: getMaxValueFromRoomStays(
        bkngData?.bookingInformation?.reservationByIdList,
        'childrenNumber'
      ),
      nrNights: noNights,
      ratePlanCode: firstRoom?.roomStay?.ratePlanCode,
      totalCost: bkngData?.bookingInformation?.totalCost,
    }),
    [
      bkngData?.bookingInformation?.hotelId,
      bkngData?.bookingInformation?.reservationByIdList,
      bkngData?.bookingInformation?.totalCost,
      firstRoom?.roomStay?.ratePlanCode,
      noNights,
    ]
  );

  const { isLoading: isLoadingTermsAndConditions, data: termsAndConditionsData } = useQueryRequest(
    [
      'GetTermsAndConditions',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.PI,
    ],
    GET_TERMS_AND_CONDITIONS_QUERY,
    {
      country,
      language,
      hotelId: hiQueryInput.hotelId,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.PI,
    }
  );

  const { isLoading: isLoadingPaymentInfoMessage, data: paymentInfoMessageData } = useQueryRequest(
    [
      'GetPaymentInfoMessages',
      hiQueryInput.hotelId,
      country,
      language,
      bookingInformation?.ratePlanCode,
      BOOKING_CHANNEL.PI,
    ],
    GET_PAYMENT_INFO_MESSAGES_QUERY,
    {
      hotelId: hiQueryInput.hotelId,
      language,
      country,
      rateCode: bookingInformation?.ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.PI,
    },
    { enabled: !!bookingInformation?.ratePlanCode }
  );

  useUpdateRateName(bkngData?.bookingInformation, language, country);

  const rooms = useMemo(
    () =>
      bkngData?.bookingInformation?.reservationByIdList?.map((room: any) => ({
        adultsNumber: room?.roomStay?.adultsNumber,
        rate: room?.roomStay?.ratePlanCode,
        type: room?.roomStay?.roomExtraInfo?.roomType,
      })) ?? [],
    [bkngData?.bookingInformation?.reservationByIdList]
  );

  const meals = packages?.meals;
  const mealsKids = packages?.mealsKids;
  const roomSelection = packages?.roomSelection;

  const selectedMeals = useMemo(
    () =>
      roomSelection && meals && mealsKids
        ? mealsMapperSelector(meals, mealsKids, roomSelection)
        : [],
    [roomSelection, meals, mealsKids]
  );

  const adultsMeals = useMemo(
    () => adultsMealsSelector(packages?.meals, noNights),
    [packages?.meals, noNights]
  );
  const childrenMeals = useMemo(
    () => childrenMealsSelector(packages?.mealsKids),
    [packages?.mealsKids]
  );

  const currentReasonForStay =
    bkngData?.bookingInformation?.reservationByIdList?.[0]?.additionalGuestInfo?.purposeOfStay;
  const hotelBrand = hiData?.hotelInformation?.brand;
  const isGermanHotel = [HotelBrand.PID].includes(hotelBrand);

  const cityTaxMessages = useMemo(
    () =>
      getCityTaxMessages(
        hotelHasCityTaxForLeisure,
        hotelHasCityTaxForBusiness,
        currentReasonForStay,
        t,
        bkngData?.bookingInformation?.currencyCode,
        language,
        bkngData?.bookingInformation?.totalCost
      ),
    [
      hotelHasCityTaxForLeisure,
      hotelHasCityTaxForBusiness,
      currentReasonForStay,
      t,
      bkngData?.bookingInformation?.currencyCode,
      language,
      bkngData?.bookingInformation?.totalCost,
    ]
  );

  const reservationDetails: BookingDataReservationDetailsProps = useMemo(
    () =>
      createReservationDetails(
        firstRoom.roomStay?.arrivalDate,
        firstRoom.roomStay?.departureDate,
        bkngData?.bookingInformation?.currencyCode,
        bkngData?.bookingInformation?.reservationByIdList,
        noNights
      ),
    [
      firstRoom.roomStay?.arrivalDate,
      firstRoom.roomStay?.departureDate,
      bkngData?.bookingInformation?.currencyCode,
      bkngData?.bookingInformation?.reservationByIdList,
      noNights,
    ]
  );

  const bookingSummaryData: BookingSummaryDataProps = useMemo(
    () =>
      getBookingSummaryData({
        hiData,
        bkngData,
        selectedDonation: undefined,
        termsAndConditionsData,
        onclickBillingFormHandler,
        onSubmitBtnText:
          selectedPaymentDetail?.type === PiCardType.RESERVE_WITHOUT_CARD
            ? t('ccui.payment.confirmBooking.button')
            : t('terms.continueText.paymentDetails'),
        firstRoom,
        noNights,
        selectedMeals,
        adultsMeals,
        childrenMeals,
        roomSelection,
        paymentStepState,
        updatedTotalCost: getTotalCost(
          bookingInformation?.ratePlanCode,
          bookingInformation?.totalCost,
          reservationDetails?.currency,
          undefined,
          undefined,
          undefined,
          Area.PI
        ),
        rateDescription:
          firstRoom?.roomStay?.rateExtraInfo?.rateDescription ||
          basketDetailsState?.rateDescription ||
          '',
        rateTags: basketDetailsState?.rateTags,
        cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
      }),
    [
      hiData,
      bkngData,
      termsAndConditionsData,
      onclickBillingFormHandler,
      selectedPaymentDetail?.type,
      t,
      firstRoom,
      noNights,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomSelection,
      paymentStepState,
      bookingInformation?.ratePlanCode,
      bookingInformation?.totalCost,
      reservationDetails?.currency,
      basketDetailsState?.rateDescription,
      basketDetailsState?.rateTags,
    ]
  );

  const listOfImportantMessagesHotel: string[] = useMemo(() => {
    const messages = formatImportantNotes(
      hiData?.hotelInformation?.importantInfo?.infoItems || [],
      firstRoom?.roomStay?.arrivalDate,
      firstRoom?.roomStay?.departureDate
    );
    return messages.length > 0 ? [messages] : [];
  }, [
    hiData?.hotelInformation?.importantInfo?.infoItems,
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate,
  ]);

  const listOfImportantMessagesPaymentType: string[] = useMemo(() => {
    const paymentInfoMessages = paymentInfoMessageData?.paymentInfoMessages;
    return paymentInfoMessages?.length
      ? paymentInfoMessages
          .filter(
            (objMsg: MessagesPaymentType) => objMsg.paymentType === selectedPaymentDetail?.type
          )
          .map((objMsg: MessagesPaymentType) => objMsg.messages)
          .flat()
      : [];
  }, [paymentInfoMessageData?.paymentInfoMessages, selectedPaymentDetail?.type]);

  const orderedListOfMessagesPaymentType = useMemo(
    () =>
      listOfImportantMessagesPaymentType.map((item: string, indexEach: number) => ({
        index: indexEach,
        messagesNotif: item,
      })),
    [listOfImportantMessagesPaymentType]
  );

  const infoMessages = useMemo(
    () => [...(listOfImportantMessagesHotel?.length > 0 ? [...listOfImportantMessagesHotel] : [])],
    [listOfImportantMessagesHotel]
  );

  const orderedInfoMessages = useMemo(
    () =>
      infoMessages.map((infoMsg: string, indexOrder: number) => ({
        indexOrder,
        infoMsg,
      })),
    [infoMessages]
  );

  const termsAndConditionsText = useMemo(
    () => formatUrlTermsConditions(termsAndConditionsData?.termsAndConditions?.text),
    [termsAndConditionsData?.termsAndConditions?.text]
  );

  const isBillingAddressDisplayed = useMemo(
    () =>
      getIsBillingAddressDisplayed({
        selectedPaymentType,
        hiData,
        selectedPaymentDetail,
        formData,
      }),
    [selectedPaymentType, hiData, selectedPaymentDetail, formData]
  );

  const isLoading =
    isLoadingHotelInformation ||
    isLoadingBookingInformation ||
    isLoadingPaymentPcks ||
    isLoadingPaymentInfoMessage ||
    isLoadingTermsAndConditions;

  return {
    bkngData,
    hiData,
    termsAndConditionsData,
    packages,
    bookingInformation,
    reservationDetails,
    bookingSummaryData,
    cityTaxMessages,
    infoMessages,
    orderedInfoMessages,
    orderedListOfMessagesPaymentType,
    roomSelection,
    rooms,
    hotelBrand,
    isGermanHotel,
    isBillingAddressDisplayed,
    termsAndConditionsText,
    isLoading,
  };
}
