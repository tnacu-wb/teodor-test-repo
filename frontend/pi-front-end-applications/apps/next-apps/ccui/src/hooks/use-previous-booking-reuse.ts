import { QueryClient, UseMutationResult } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  HIBasketBookConfirmation,
  PackagesCriteria,
  ReservationById,
  BookingInformation,
  BillingResponse,
  BIResponse,
  GET_BOOKING_INFORMATION,
  SAVE_RESERVATION,
  GET_PACKAGES,
  CREATE_RESERVATION_GUEST_CCUI,
  Channel,
  RoomReservation,
  BookingChannelCriteria,
} from '@whitbread-eos/api';
import {
  useCustomLocale,
  useMutationRequest,
  useQueryRequest,
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
} from '@whitbread-eos/utils';
import { useCallback } from 'react';

interface UsePreviousBookingReuseOptions {
  prevReservationId: string;
  variant: string;
  queryClient: QueryClient;
}

interface UsePreviousBookingReuseReturn {
  shouldSavePreviousData: boolean;
  executeBookingWithReuse: (
    bookMutation: UseMutationResult<any, Error, any, unknown>,
    reservations: RoomReservation[],
    bookingChannel: BookingChannelCriteria,
    bookingFlowId?: string
  ) => void;
  isLoading: boolean;
  isError: boolean;
  error: Error | null;
  isSuccess: boolean;
}

export default function usePreviousBookingReuse({
  prevReservationId,
  variant,
  queryClient,
}: UsePreviousBookingReuseOptions): UsePreviousBookingReuseReturn {
  const { language, country } = useCustomLocale();

  // 1. Fetch previous booking data
  const biQueryInput: QueryBookingInformationArgs = {
    basketReference: prevReservationId,
    country,
    language,
    bookingChannelCriteria: {
      channel: Channel.Ccui,
      subchannel: 'WEB',
      language: language === 'en' ? 'EN' : 'DE',
    },
  };

  const { data: previousBookingData } = useQueryRequest(
    ['GetBookingInformation', language, country, prevReservationId],
    GET_BOOKING_INFORMATION,
    biQueryInput,
    {
      enabled: !!prevReservationId,
    }
  );

  // 2. Calculate shouldSavePreviousData
  const shouldSavePreviousData =
    !!prevReservationId &&
    !!(previousBookingData as BIResponse)?.bookingInformation?.reservationByIdList?.length &&
    variant === 'CCUI';

  // 3. Fetch packages data
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
      prevReservationId,
    ],
    GET_PACKAGES,
    pcksQueryInput,
    {
      enabled: shouldSavePreviousData && !!pcksQueryInput?.hotelId,
    }
  );

  // 4. Set up mutations
  const {
    mutation: updateAncillariesMutation,
    isLoading: updateAncillariesIsLoading,
    isError: updateAncillariesIsError,
    error: updateAncillariesError,
    isSuccess: updateAncillariesIsSuccess,
  } = useMutationRequest(SAVE_RESERVATION);

  const {
    mutation: updateGuestsMutation,
    isLoading: updateGuestsIsLoading,
    isError: updateGuestsIsError,
    error: updateGuestsError,
    isSuccess: updateGuestsSuccess,
  } = useMutationRequest(CREATE_RESERVATION_GUEST_CCUI);

  // 5. Define handleGuests function
  const handleGuests = useCallback(
    (params: HIBasketBookConfirmation, bookingInformation: BookingInformation) => {
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
    },
    [previousBookingData, updateGuestsMutation]
  );

  // 6. Define handleAncillaries function
  const handleAncillaries = useCallback(
    (params: HIBasketBookConfirmation, bookingInformation: BookingInformation) => {
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
    },
    [dataPcks, updateAncillariesMutation, handleGuests]
  );

  // 7. Create wrapped booking handler
  const executeBookingWithReuse = useCallback(
    (
      bookMutation: any,
      reservations: RoomReservation[],
      bookingChannel: BookingChannelCriteria,
      bookingFlowId?: string
    ) => {
      bookMutation.mutate(
        {
          reservations,
          bookingChannel,
          bookingFlowId,
        },
        {
          onSuccess: async (params: HIBasketBookConfirmation) => {
            const basketReference = params?.createReservation?.basketReference;

            if (basketReference && shouldSavePreviousData) {
              const biQueryInput: QueryBookingInformationArgs = {
                basketReference,
                country,
                language,
                bookingChannelCriteria: {
                  channel: Channel.Ccui,
                  subchannel: 'WEB',
                  language: language === 'en' ? 'EN' : 'DE',
                },
              };

              const { bookingInformation } = await queryClient.fetchQuery({
                queryKey: ['GetBookingInformation', language, country, basketReference],
                queryFn: () => graphQLRequest(GET_BOOKING_INFORMATION, biQueryInput),
              });

              handleAncillaries(params, bookingInformation);
            }
          },
        }
      );
    },
    [shouldSavePreviousData, queryClient, language, country, handleAncillaries]
  );

  // 8. Return combined state
  return {
    shouldSavePreviousData,
    executeBookingWithReuse,
    isLoading: shouldSavePreviousData ? updateAncillariesIsLoading || updateGuestsIsLoading : false,
    isError: shouldSavePreviousData ? updateAncillariesIsError || updateGuestsIsError : false,
    error: updateAncillariesError || updateGuestsError,
    isSuccess: shouldSavePreviousData ? updateAncillariesIsSuccess && updateGuestsSuccess : true,
  };
}
