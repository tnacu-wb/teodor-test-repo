import {
  QueryBookingInformationArgs,
  HIBasketBookConfirmation,
  PackagesCriteria,
  ReservationById,
  BookingInformation,
  BillingResponse,
  RoomReservation,
  BookingChannelCriteria,
  BIResponse,
  HIRoomTypeInfoResponse,
  HIGlobalConfigResponse,
  BOOK_MUTATION,
  GET_ROOM_TYPE_INFORMATION_QUERY,
  GET_BOOKING_INFORMATION,
  SAVE_RESERVATION,
  GET_PACKAGES,
  CREATE_RESERVATION_GUEST_CCUI,
  GET_GLOBAL_CONFIG_QUERY,
  Channel,
} from '@whitbread-eos/api';
import {
  useAuthToken,
  useCustomLocale,
  useMutationRequest,
  useQueryRequest,
  useStaticHotelInformation,
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
} from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import RateSelectorComponent from './RateSelector.component';
import { RateSelectorProps } from './RateSelectorProps';

export default function RateSelector({
  channel,
  variant,
  queryClient,
  hotelAvailabilityResponse,
  isParentAnalytics,
  isHotelOpeningSoon,
  arrival,
  departure,
  numberOfUnits,
  numberOfNights,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  prevReservationId,
  mappedRoomLabels,
  isSilentFeatureFlagEnabled,
  thirdParties,
  companyData,
  isPrePopulateBillingAddressEnabled,
  targetRatePlanCode,
  globalTranslationForRooms,
  isCityTaxEnabled,
  promoActions,
  isSoftBundlesVisible,
  isCityTaxBreakdownEnabled,
  hasRateSelectorTitleDescription,
}: Readonly<RateSelectorProps>) {
  const { language, country } = useCustomLocale();

  const { brand, hotelId, bookingFlow, contactDetails, accessibilityInfo } =
    useStaticHotelInformation();

  const biQueryInput: QueryBookingInformationArgs = {
    basketReference: prevReservationId ?? '',
    country,
    language,
    bookingChannelCriteria: {
      channel: Channel.Ccui,
      subchannel: 'WEB',
      language: language === 'en' ? 'EN' : 'DE',
    },
  };
  const { data: previousBookingData } = useQueryRequest(
    ['GetBookingInformation', language, country, prevReservationId ?? ''],
    GET_BOOKING_INFORMATION,
    biQueryInput,
    {
      enabled: !!prevReservationId,
    }
  );

  const shouldSavePreviousData =
    !!prevReservationId &&
    !!(previousBookingData as BIResponse)?.bookingInformation?.reservationByIdList?.length &&
    variant === 'CCUI';

  const pcksQueryInput: PackagesCriteria | undefined = shouldSavePreviousData
    ? {
        country,
        language,
        hotelId: previousBookingData?.bookingInformation?.hotelId,
        adultsNumber: getMaxValueFromRoomStays(
          previousBookingData?.bookingInformation?.reservationByIdList,
          'adultsNumber'
        ),
        childrenNumber: getMaxValueFromRoomStays(
          previousBookingData?.bookingInformation?.reservationByIdList,
          'childrenNumber'
        ),
        startDate:
          previousBookingData?.bookingInformation?.reservationByIdList[0]?.roomStay?.arrivalDate,
        endDate:
          previousBookingData?.bookingInformation?.reservationByIdList[0]?.roomStay?.departureDate,
        bookingFlowId: previousBookingData?.bookingInformation?.bookingFlowId,
        nightsNumber: getNightsNumber(
          previousBookingData?.bookingInformation?.reservationByIdList[0].roomStay.arrivalDate,
          previousBookingData?.bookingInformation?.reservationByIdList[0].roomStay.departureDate
        ),
        basketReferenceId: prevReservationId,
      }
    : undefined;
  const { data: dataPcks } = useQueryRequest(
    [
      'GetPackages',
      language,
      country,
      pcksQueryInput?.hotelId ?? '',
      pcksQueryInput?.bookingFlowId ?? '',
      pcksQueryInput?.startDate ?? '',
      pcksQueryInput?.endDate ?? '',
      pcksQueryInput?.nightsNumber ?? '',
      pcksQueryInput?.adultsNumber ?? '',
      pcksQueryInput?.childrenNumber ?? '',
      prevReservationId ?? '',
    ],
    GET_PACKAGES,
    pcksQueryInput,
    {
      enabled: shouldSavePreviousData && !!pcksQueryInput?.hotelId,
    }
  );

  const {
    isLoading: isLoadingRoomTypeInformation,
    isError: isErrorRoomTypeInformation,
    data: dataRoomTypeInformation,
    error: errorRoomTypeInformation,
  } = useQueryRequest(
    ['getRoomTypeInformation', language, country, brand, hotelId],
    GET_ROOM_TYPE_INFORMATION_QUERY,
    {
      language,
      country,
      brand,
      hotelId,
    }
  );

  const {
    isLoading: isLoadingGlobalConfig,
    isError: isErrorGlobalConfig,
    data: dataGlobalConfig,
    error: errorGlobalConfig,
  } = useQueryRequest(
    ['getGlobalConfig', brand, channel, country, language],
    GET_GLOBAL_CONFIG_QUERY,
    {
      brand,
      channel,
      country,
      language,
    },
    {
      enabled: true,
    }
  );

  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();

  const {
    mutation: bookRsvMutation,
    isLoading: bookRsvIsLoading,
    isError: bookRsvIsError,
    data: bookRsvData,
    error: bookRsvError,
    isSuccess: bookRsvIsSuccess,
  } = useMutationRequest(
    BOOK_MUTATION,
    false,
    ['PI', 'BB'].includes(variant) && !isAuthTokenLoading ? authToken : undefined
  );

  const {
    mutation: updateAncillariesMutation,
    isLoading: updateAncillariesIsLoading,
    isError: updateAncillariesIsError,
    error: updateAncillariesError,
    isSuccess: updateAncillariesIsSuccess,
  } = useMutationRequest(SAVE_RESERVATION);

  const roomTypeInformationResponse = {
    isLoadingRoomTypeInformation,
    isErrorRoomTypeInformation,
    dataRoomTypeInformation,
    errorRoomTypeInformation,
  };

  const globalConfigResponse = {
    isLoadingGlobalConfig,
    isErrorGlobalConfig,
    dataGlobalConfig,
    errorGlobalConfig,
  };

  const {
    mutation: updateGuestsMutation,
    isLoading: updateGuestsIsLoading,
    isError: updateGuestsIsError,
    error: updateGuestsError,
    isSuccess: updateGuestsSuccess,
  } = useMutationRequest(CREATE_RESERVATION_GUEST_CCUI);

  const [isDisabledContinueBtn, setIsDisabledContinueBtn] = useState<boolean>(false);

  useEffect(() => {
    setIsDisabledContinueBtn(false);
  }, []);

  useEffect(() => {
    setIsDisabledContinueBtn(false);
  }, [bookRsvIsError]);

  return (
    <RateSelectorComponent
      {...{
        channel,
        variant,
        isLoading: hotelAvailabilityResponse.isLoadingHotelAvailability,
        isError: hotelAvailabilityResponse.isErrorHotelAvailability,
        data: hotelAvailabilityResponse.dataHotelAvailability,
        error: hotelAvailabilityResponse.errorHotelAvailability,
        queryClient,
        roomTypeInformationResponse: roomTypeInformationResponse as HIRoomTypeInfoResponse,
        globalConfigResponse: globalConfigResponse as HIGlobalConfigResponse,
        isParentAnalytics,
        brand,
        accessibilityInfo,
        bookingFlow,
        bookRsvIsLoading: shouldSavePreviousData
          ? bookRsvIsLoading && updateAncillariesIsLoading && updateGuestsIsLoading
          : bookRsvIsLoading,
        bookRsvIsError: shouldSavePreviousData
          ? bookRsvIsError || updateAncillariesIsError || updateGuestsIsError
          : bookRsvIsError,
        bookRsvData: bookRsvData as HIBasketBookConfirmation,
        bookRsvIsSuccess: shouldSavePreviousData
          ? bookRsvIsSuccess && updateAncillariesIsSuccess && updateGuestsSuccess
          : bookRsvIsSuccess,
        bookRsvError: bookRsvError || updateAncillariesError || updateGuestsError,
        handleBooking,
        phoneNumber: contactDetails?.hotelNationalPhone,
        isHotelOpeningSoon,
        isLessThanSm,
        isLessThanMd,
        isLessThanLg,
        isDisabledContinueBtn: isDisabledContinueBtn || isAuthTokenLoading,
        mappedRoomLabels,
        isSilentFeatureFlagEnabled,
        thirdParties,
        companyData,
        isPrePopulateBillingAddressEnabled,
        globalTranslationForRooms,
        isCityTaxEnabled,
        promoActions,
        isCityTaxBreakdownEnabled,
        hasRateSelectorTitleDescription,
      }}
      basketData={{
        ...{
          hotelId,
          arrival: arrival!,
          departure: departure!,
          numberOfUnits: numberOfUnits!,
          numberOfNights: numberOfNights!,
          ...(prevReservationId && { prevReservationId }),
        },
      }}
      targetRatePlanCode={targetRatePlanCode}
      isSoftBundlesVisible={isSoftBundlesVisible}
    />
  );

  async function handleGuests(
    params: HIBasketBookConfirmation,
    bookingInformation: BookingInformation
  ) {
    const previousBilling: BillingResponse =
      previousBookingData?.bookingInformation?.reservationByIdList[0].billing;
    const numberOfRooms = bookingInformation?.reservationByIdList?.length ?? 0;
    const guests = (
      previousBookingData as BIResponse
    )?.bookingInformation?.reservationByIdList?.flatMap(
      (reservation) => reservation?.reservationGuestList
    );

    if (guests && guests.length > numberOfRooms) {
      guests?.splice(numberOfRooms, guests.length - 1);
    }

    updateGuestsMutation.mutate({
      hotelId: bookingInformation?.hotelId,
      reasonForStay:
        previousBookingData?.bookingInformation?.reservationByIdList[0]?.additionalGuestInfo
          ?.purposeOfStay,
      companyName: previousBilling.address?.companyName ?? '',
      addressLine1: previousBilling.address.addressLine1,
      addressLine2: previousBilling.address.addressLine2,
      addressLine3: previousBilling.address.addressLine3,
      addressLine4: previousBilling.address.addressLine4,
      addressType: previousBilling?.address?.companyName ? 'BUSINESS' : 'HOME',
      cityName: previousBilling?.address?.cityName,
      countryCode: previousBilling?.address?.country,
      postalCode: previousBilling.address.postalCode,
      title: previousBilling.title,
      firstName: previousBilling.firstName,
      lastName: previousBilling.lastName,
      emailAddress: previousBilling.email,
      mobile: previousBilling.telephone,
      landline: previousBilling.landline,
      acceptFutureMailing: false,
      basketReference: params?.createReservation?.basketReference,
      stayingGuests: guests?.map((guest: any) => {
        const sameAsBooker =
          guest.givenName === previousBilling?.firstName &&
          guest.surName === previousBilling?.lastName &&
          guest.nameTitle === previousBilling?.title;

        return {
          sameAsBooker: sameAsBooker,
          stayingGuestDetails: {
            title: guest?.nameTitle,
            firstName: guest?.givenName,
            lastName: guest?.surName,
          },
        };
      }),
    });
  }

  function handleAncillaries(
    params: HIBasketBookConfirmation,
    bookingInformation: BookingInformation
  ) {
    //map previous selection on new reservations
    const previousSelection = bookingInformation?.reservationByIdList?.map(
      (reservation: ReservationById, index: number) => {
        return {
          reservationId: reservation?.reservationId,
          packagesSelection:
            dataPcks?.packages?.packages?.roomSelection[index]?.packagesSelection ?? [],
        };
      }
    );
    const selectionInfo = {
      basketReferenceId: params?.createReservation?.basketReference,
      hotelId: bookingInformation?.hotelId,
      arrivalDate: bookingInformation?.reservationByIdList?.[0]?.roomStay?.arrivalDate,
      departureDate: bookingInformation?.reservationByIdList?.[0]?.roomStay?.departureDate,
      roomsSelections: previousSelection,
      previousRoomsSelections: previousSelection,
    };

    updateAncillariesMutation.mutate(selectionInfo, {
      onSuccess: () => {
        handleGuests(params, bookingInformation);
      },
    });
  }

  function handleBooking(
    reservations: RoomReservation[],
    bookingChannel: BookingChannelCriteria,
    bookingFlowId?: string
  ) {
    setIsDisabledContinueBtn(true);
    bookRsvMutation.mutate(
      {
        reservations,
        bookingChannel,
        bookingFlowId,
      },
      {
        onSuccess: async (params: HIBasketBookConfirmation) => {
          if (params?.createReservation?.basketReference && shouldSavePreviousData) {
            const biQueryInput: QueryBookingInformationArgs = {
              basketReference: params?.createReservation?.basketReference,
              country,
              language,
              bookingChannelCriteria: {
                channel: Channel.Ccui,
                subchannel: 'WEB',
                language: language === 'en' ? 'EN' : 'DE',
              },
            };
            const { bookingInformation } = await queryClient.fetchQuery({
              queryKey: ['GetBookingInformation', language, country, biQueryInput.basketReference],
              queryFn: () => graphQLRequest(GET_BOOKING_INFORMATION, biQueryInput),
            });

            handleAncillaries(params, bookingInformation);
          }
        },
      }
    );
  }
}
