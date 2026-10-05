import { Box, Text } from '@chakra-ui/react';
import type { AmendConfInput, HeaderInformationQuery } from '@whitbread-eos/api';
import {
  ADD_NEW_ROOM,
  AMEND_EDIT_ROOM,
  AMEND_STAY_DATES,
  Area,
  CONFIRM_AMEND,
  COPY_BOOKING,
  GET_HOTEL_INFORMATION,
  GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
  GET_STATIC_CONTENT,
  GET_STAY_RULES_QUERY,
  HotelBrand,
  REMOVE_ROOM,
  SAVE_RESERVATION,
  SITE_LEISURE,
  TEMPORARY_BASKET_KEY,
} from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { PageLoader } from '@whitbread-eos/molecules';
import { AgentMemo, AmendContainer } from '@whitbread-eos/organisms';
import {
  formatDataTestId,
  useBookingConfimationData,
  useMutationRequest,
  useQueryRequest,
  useSessionStorage,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState, useRef } from 'react';

interface Props {
  confirmationInput: AmendConfInput;
}

export default function AmendPageCCUI({ confirmationInput }: Readonly<Props>) {
  const { t } = useTranslation();
  const { bookingReference, basketReference, token, country, language } = confirmationInput;

  const hasRunCopyBooking = useRef(false);
  const [amendVisited, setAmendVisited] = useState<boolean>(false);
  const [temporaryBasketRefVisited] = useSessionStorage<string>(TEMPORARY_BASKET_KEY, '');

  const router = useRouter();
  const { tempBasketReference, status } = router.query;
  const channel = 'CCUI';
  const area = 'ccui' as Area;
  const baseDataTestId = 'Amend';

  const {
    bookingData: bookingConfirmationData,
    bookingError: bookingConfirmationError,
    bookingIsError: bookingConfirmationIsError,
    bookingIsLoading: bookingConfirmationIsLoading,
  } = useBookingConfimationData(area, basketReference ?? '', bookingReference, language, country);

  const basketReferenceValue = basketReference ?? bookingConfirmationData?.basketReference;

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
    data: hotelInformationData,
    isError: hotelInformationIsError,
    isLoading: hotelInformationIsLoading,
    error: hotelInformationError,
  } = useQueryRequest(
    ['getHotelInformation', language, country, bookingConfirmationData?.hotelId],
    GET_HOTEL_INFORMATION,
    {
      country,
      language,
      hotelId: bookingConfirmationData?.hotelId,
    },
    {
      enabled: !!bookingConfirmationData,
    }
  );

  const brand = hotelInformationData?.hotelInformation?.brand;

  const {
    data: RoomOccupancyLimitationsData,
    isError: RoomOccupancyLimitationsIsError,
    isLoading: RoomOccupancyLimitationsIsLoading,
    error: RoomOccupancyLimitationsError,
  } = useQueryRequest(
    ['getRoomOccupancyLimitations', channel, brand],
    GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
    {
      channel,
      // bug https://whitbreadis.atlassian.net/browse/DNRQ-60613,
      // remove condition when BE will handle brand for this query and leave only brand as param => add brand as param to PI and BB also
      brand: brand === HotelBrand.HUB ? brand : '',
    },
    { enabled: !!hotelInformationData }
  );

  const { mutation: copyBookingMutation, isError: copyBookingIsError } = useMutationRequest(
    COPY_BOOKING,
    true,
    undefined,
    { enabled: !!basketReferenceValue }
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

  const replaceUrlTempBasket = (tempBasketRef: string) => {
    const urlTempBasket = {
      pathname: `/${country}/${language}/amend/details.html`,
      query: { bookingReference, tempBasketReference: tempBasketRef },
    };
    router
      .replace(urlTempBasket)
      // eslint-disable-next-line no-console
      .catch((error) => console.error(error));
  };

  useEffect(() => {
    if (!tempBasketReference && !hasRunCopyBooking.current) {
      const bookingChannel = { channel, subchannel: 'WEB', language: language?.toUpperCase() };
      copyBookingMutation
        .mutateAsync({
          originalBasketReference: basketReferenceValue,
          bookingChannel,
          token: token,
        })
        .then((result: any) => {
          const temporaryBasketRef = result.copyBooking.copyBasketReference;
          replaceUrlTempBasket(temporaryBasketRef);
        })
        // eslint-disable-next-line no-console
        .catch((error) => console.error(error));
      hasRunCopyBooking.current = true;
    } else if (temporaryBasketRefVisited === tempBasketReference) {
      setAmendVisited(true);
    }
  }, [basketReferenceValue]);

  const isLoading =
    bookingConfirmationIsLoading ||
    stayRulesIsLoading ||
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
    <>
      <AmendContainer
        language={language}
        country={country}
        t={t}
        basketReference={basketReference}
        bookingReference={bookingReference}
        temporaryBasketReference={tempBasketReference as string}
        channel={channel}
        amendVisited={amendVisited}
        status={status as string | undefined}
        data={{
          bookingConfirmationData,
          hotelInformation: hotelInformationData,
          headerInformationData: headerInformationData,
          stayRulesData: stayRulesData,
          RoomOccupancyLimitationsData: RoomOccupancyLimitationsData,
          brand: hotelInformationData.hotelInformation.brand,
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
        variant={Area.CCUI}
      />
      <AgentMemo />
    </>
  );
}
