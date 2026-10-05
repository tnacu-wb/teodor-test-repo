import { Box, BoxProps, Flex, Text } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import type { BCResponse, RoomTypeInformation, BookingChannelCriteria } from '@whitbread-eos/api';
import {
  Area,
  BC_RESERVATION_STATUS,
  GET_BOOKING_CONFIRMATION,
  GET_HOTEL_INFORMATION,
  GET_ROOM_TYPE_INFORMATION_QUERY,
  GET_STATIC_CONTENT,
  SITE_LEISURE,
  SOURCE_SYSTEM,
  Channel,
} from '@whitbread-eos/api';
import { DescriptionBox, Info, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import { BookingHistoryInfoCardContainer, CCUISearchContainer } from '@whitbread-eos/organisms';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { isWithinInterval, parse } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useMemo } from 'react';

export default function RepeatBookingPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, ROOMS } = router.query;

  const { language, country } = useCustomLocale();
  const { reservationId } = router.query;

  const basketReference = reservationId ?? '';

  useEffect(() => {
    localStorage.removeItem('formDetails');
    localStorage.removeItem('reUseReservation');
  }, []);

  const { isLoading: isLoadingBookingConfirmation, data: bkngData } = useQueryRequest(
    ['GetBookingConfirmation', language, country, basketReference, Area.CCUI],
    GET_BOOKING_CONFIRMATION,
    {
      language,
      country: country,
      basketReference,
      bookingChannel: Area.CCUI,
    }
  );

  const hotelId = bkngData?.bookingConfirmation?.hotelId;
  const bookingReference = bkngData?.bookingConfirmation?.bookingReference;

  const { data: staticContentData, isLoading: staticContentIsLoading } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );

  const { data: hotelInformationData, isLoading: hotelInformationIsLoading } = useQueryRequest(
    ['GetHotelInformation', hotelId, country, language],
    GET_HOTEL_INFORMATION,
    {
      hotelId,
      country,
      language,
    },
    {
      enabled: !!bkngData,
    }
  );
  const hotelBrand = hotelInformationData?.hotelInformation?.brand;

  const { isLoading: roomTypeInformationIsLoading, data: roomTypeInformationData } =
    useQueryRequest(
      ['getRoomTypeInformation', language, country, hotelBrand, hotelId],
      GET_ROOM_TYPE_INFORMATION_QUERY,
      {
        language,
        country,
        brand: hotelBrand,
        hotelId,
      },
      {
        enabled: !!hotelBrand,
      }
    );

  const prefillRooms = (bookingConfirmationData: BCResponse) => {
    const roomTypes = roomTypeInformationData.roomTypeInformation.roomTypes;
    const staticRoomCodes = staticContentData.headerInformation.config.roomCodes;

    return bookingConfirmationData?.bookingConfirmation?.reservationByIdList.map(
      (reservationById) => {
        const reservationRoomType = reservationById.roomStay?.roomType;
        const roomGroup = roomTypes.find((room: RoomTypeInformation) =>
          room.roomTypeCode.includes(reservationRoomType)
        )?.groupId;
        return {
          adults: reservationById?.roomStay?.adultsNumber,
          children: reservationById?.roomStay?.childrenNumber,
          shouldIncludeCot: reservationById?.roomStay?.cot,
          roomType: staticRoomCodes[roomGroup],
        };
      }
    );
  };

  const bookingChannel: BookingChannelCriteria = {
    channel: Channel.Ccui,
    subchannel: 'WEB',
    language: language?.toUpperCase() as 'EN' | 'DE',
  };

  const arrivalDate = bkngData?.bookingConfirmation?.reservationByIdList[0]?.roomStay?.arrivalDate;
  const announcement = hotelInformationData?.hotelInformation?.announcement;
  const notificationText = announcement?.text || announcement?.title;
  const hotelName = hotelInformationData?.hotelInformation?.name;
  const firstReservation = bkngData?.bookingConfirmation?.reservationByIdList[0];
  const leadGuest = firstReservation?.reservationGuestList[0];
  const billing = firstReservation?.billing;
  const leadGuestName = `${leadGuest?.givenName} ${leadGuest?.surName}`;
  const bookerName = `${billing?.firstName} ${billing?.lastName}`;
  const guestSurname = leadGuest?.surName;

  const shouldShowNotification = useMemo(() => {
    try {
      const startDate = parse(announcement?.startDate, 'dd/MM/yyyy', new Date());
      const endDate = parse(announcement?.endDate, 'dd/MM/yyyy', new Date());
      return (
        !!notificationText?.length &&
        announcement?.showAnnouncement === 'true' &&
        isWithinInterval(new Date(), {
          start: new Date(startDate),
          end: new Date(endDate),
        })
      );
    } catch {
      return false;
    }
  }, [hotelInformationData?.hotelInformation?.announcement]);

  const renderSearch = () => {
    return (
      <CCUISearchContainer
        queryClient={queryClient}
        searchLocation={searchLocation?.toString()}
        defaultLocation={hotelInformationData?.hotelInformation?.name}
        ARRdd={Number(ARRdd)}
        ARRmm={Number(ARRmm)}
        ARRyyyy={Number(ARRyyyy)}
        NIGHTS={Number(0)}
        ROOMS={Number(ROOMS)}
        isSummaryActive={false}
        isDatePickerFocus={true}
        defaultRooms={prefillRooms(bkngData)}
        hideErrorForMinNights={true}
        displayDatesNotification={true}
        prevReservationId={String(reservationId)}
      />
    );
  };

  if (
    isLoadingBookingConfirmation ||
    hotelInformationIsLoading ||
    roomTypeInformationIsLoading ||
    staticContentIsLoading
  ) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-RepeatBookingPage">
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  return (
    <>
      {shouldShowNotification && (
        <Box mt="2xl" mb="2xl">
          <Notification
            maxWidth="full"
            variant="info"
            status="info"
            description={<DescriptionBox html={notificationText} />}
            svg={<Info />}
          />
        </Box>
      )}
      <Box mb="2xl" data-testid={'repeat-booking-page-title'}>
        <Text {...pageTitleStyles}>{t('ccui.manageBooking.options.repeatBooking')}</Text>
      </Box>
      <Box>{renderSearch()}</Box>
      <Text {...headingStyles}>{t('ccui.manageBooking.selectedBooking.title')}</Text>
      <Box marginBottom="4.95rem">
        <BookingHistoryInfoCardContainer
          bookingReference={bookingReference as string}
          hotelId={hotelId as string}
          arrival={arrivalDate as string}
          guestSurname={guestSurname as string}
          bookingStatus={BC_RESERVATION_STATUS.CANCELLED}
          hotelName={hotelName as string}
          bookedBy={bookerName}
          bookingChannel={bookingChannel}
          sourceSystem={SOURCE_SYSTEM.OPERA}
          isReadOnly={true}
          shouldShowTypeOfBooking={true}
          leadGuestName={leadGuestName}
        />
      </Box>
    </>
  );
}

const pageTitleStyles = {
  fontSize: '3xxl',
  fontWeight: 'normal',
  lineHeight: '5',
  fontStyle: 'normal',
};

const headingStyles = {
  paddingBottom: '1.25rem',
  fontSize: '3xxl',
  fontWeight: 'semibold',
};

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
