import { Box, BoxProps, Flex, Text } from '@chakra-ui/react';
import {
  Area,
  GET_BOOKING_HISTORY_DETAILS,
  RESEND_INVOICE_BOOKING_INFORMATION_CARD,
  RoomTypeLabelCode,
  SOURCE_SYSTEM,
  BookingChannelCriteria,
  ReservationDetails,
  DISCOUNT_RATE_INFORMATION_QUERY,
  GET_HOTEL_INFORMATION,
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  Channel,
  GET_ROOM_TYPE_BOOKING_HISTORY_INFORMATION_QUERY,
} from '@whitbread-eos/api';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import {
  analytics,
  decodeIdToken,
  getAuthCookie,
  getBookingDetailsForCard,
  graphQLRequest,
  useCustomLocale,
  useFeatureToggle,
  useMutationRequest,
  useQueryRequest,
  useAuthToken,
  useAuth0User,
  downloadFromS3PreSignedUrl,
  downloadBookingInvoice,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

import { getIsPromoCodeLandingPageEnabled } from '../../../amend/helpers';
import BookingHistoryInfoCardComponent from './BookingHistoryInfoCard.component';
import { ResendConfirmationModal } from './ResendConfirmationModal';

interface InvoiceItems {
  sentInvoiceItems: Array<string>;
  setInvoiceItems: Dispatch<SetStateAction<string[]>>;
}

interface Props {
  arrival: string;
  bookingReference: string;
  bookingStatus: string;
  guestSurname: string;
  bookedBy: string;
  hotelId: string;
  onCancel?: (bookingReference: string) => void;
  hotelName: string;
  invoiceRecordNumber?: string;
  invoiceItems?: InvoiceItems;
  bookingChannel: BookingChannelCriteria;
  sourceSystem: SOURCE_SYSTEM | undefined;
  area?: Area;
  isReadOnly?: boolean;
  shouldShowTypeOfBooking?: boolean;
  leadGuestName?: string;
  rateName?: string;
}

export const getDiscountRateQueryFn =
  (
    fetchDiscount: boolean,
    hotelId: string,
    brand: string,
    language: string,
    country: string,
    area?: string,
    rateName?: string
  ) =>
  () =>
    fetchDiscount
      ? graphQLRequest(DISCOUNT_RATE_INFORMATION_QUERY, {
          hotelId,
          brand,
          language,
          country,
          channel: area?.toUpperCase(),
          ratePlans: rateName,
        })
      : Promise.resolve(undefined);

export function shouldFetchDiscountRate(
  brand: string | undefined,
  isPromoCodeLandingPageEnabled: boolean,
  area: string | undefined
): boolean {
  if (!brand) return false;
  if (!isPromoCodeLandingPageEnabled) return false;
  if (!area) return false;

  return ['pi', 'bb'].includes(area.toLowerCase());
}

export function getRateTags(dataDiscount?: any): string[] {
  const rateTags = dataDiscount?.ratesInformation?.rateClassifications[0]?.rateTags ?? [];
  return rateTags;
}

export function getAnalyticsChannelId(channel?: Channel): string | undefined {
  return channel === Channel.Bb ? 'PIB' : channel;
}

export const BookingHistoryInfoCardContainer = ({
  bookingReference,
  bookingStatus,
  hotelId,
  arrival,
  guestSurname,
  bookedBy,
  onCancel,
  hotelName,
  invoiceRecordNumber,
  invoiceItems,
  bookingChannel,
  sourceSystem,
  area,
  isReadOnly,
  shouldShowTypeOfBooking,
  leadGuestName,
  rateName,
}: Props) => {
  const baseDataTestId = 'BookingHistoryDetails';
  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [downloadInvoiceError, setDownloadInvoiceError] = useState<string | undefined>(undefined);
  const [isDownloadingInvoice, setIsDownloadingInvoice] = useState(false);
  const onModalClose = () => setIsModalVisible((prevState) => !prevState);
  const {
    [FT_PI_PROMO_CODE_LANDING_PAGE]: hasPiPromoCodeLandingPageEnabled,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: hasBbPromoCodeLandingPageEnabled,
  } = useFeatureToggle();

  const isPromoCodeLandingPageEnabled = getIsPromoCodeLandingPageEnabled(
    bookingChannel?.channel,
    hasPiPromoCodeLandingPageEnabled,
    false,
    hasBbPromoCodeLandingPageEnabled
  );

  const surname = encodeURIComponent(guestSurname);

  const { token, isAuth0Enabled } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0Enabled);
  const emailAddress =
    isAuth0Enabled && auth0User?.email ? auth0User.email : decodeIdToken(getAuthCookie()).email;

  const { data, isLoading, isError, error, refetch } = useQueryRequest(
    [
      'getBookingHistoryCard',
      bookingReference,
      hotelId,
      arrival,
      surname,
      country,
      language,
      bookingChannel,
      ...(sourceSystem ? [sourceSystem] : []),
    ],
    GET_BOOKING_HISTORY_DETAILS,
    {
      bookingReference,
      hotelId,
      arrival,
      surname,
      country,
      language,
      bookingChannel,
      ...(sourceSystem ? { sourceSystem } : {}),
    },
    null,
    token
  );

  const {
    data: hotelInfo,
    isLoading: isLoadingHotelInfo,
    isError: isErrorHotelInfo,
  } = useQueryRequest(['GetHotelInformation', hotelId, country, language], GET_HOTEL_INFORMATION, {
    hotelId,
    language,
    country,
  });
  const brand = hotelInfo?.hotelInformation?.brand?.toLowerCase();

  const { isLoading: isLoadingRoomTypeInformation, data: dataRoomTypeInformation } =
    useQueryRequest(
      ['getRoomTypeInformation', language, country, bookingChannel.channel, brand],
      GET_ROOM_TYPE_BOOKING_HISTORY_INFORMATION_QUERY,
      {
        language,
        country,
        brand: hotelInfo?.hotelInformation?.brand,
      },
      {
        enabled: !!hotelInfo?.hotelInformation?.brand,
      }
    );

  const roomTypeLabels: RoomTypeLabelCode[] =
    dataRoomTypeInformation?.roomTypeInformation?.roomTypes ?? [];

  const fetchDiscount = shouldFetchDiscountRate(brand, isPromoCodeLandingPageEnabled, area);
  const queryFn = getDiscountRateQueryFn(
    fetchDiscount,
    hotelId,
    brand,
    language,
    country,
    area?.toLowerCase(),
    rateName
  );

  const {
    data: dataDiscount,
    isLoading: isLoadingDiscount,
    isError: isErrorDiscount,
  } = useQueryRequest(
    [
      'ratesInformationDiscountRate',
      hotelId,
      brand,
      language,
      country,
      area?.toUpperCase(),
      rateName,
    ],
    DISCOUNT_RATE_INFORMATION_QUERY,
    {
      hotelId,
      brand,
      language,
      country,
      channel: area?.toUpperCase(),
      ratePlans: rateName,
    },
    { enabled: fetchDiscount, queryFn }
  );

  const rateTags = getRateTags(dataDiscount);
  const { mutation: mutationResendInvoice, isSuccess } = useMutationRequest(
    RESEND_INVOICE_BOOKING_INFORMATION_CARD,
    true,
    token
  );

  useEffect(() => {
    if (isSuccess) {
      invoiceItems?.setInvoiceItems([...invoiceItems.sentInvoiceItems, bookingReference]);
    }
  }, [isSuccess]);

  useEffect(() => {
    if (rateTags && rateTags.length > 0) {
      const currentData = window?.analyticsData ?? {};
      analytics.update({
        ...currentData,
        promo: {
          promoName: rateTags[0],
        },
      });
    }
  }, [rateTags]);

  const handleCancelBooking = () => {
    if (!isReadOnly) {
      refetch();
      onCancel?.(bookingReference);
    }
  };

  const handleResendInvoice = () => {
    mutationResendInvoice.mutate({
      hotelId: hotelId,
      email: emailAddress,
      bookingReference: data?.bookingInfoCardDetails?.reservationDetails?.basketReference,
      invoiceRecordNumber: invoiceRecordNumber,
      bookingChannel: bookingChannel,
    });
  };

  const handleDownloadInvoice = async () => {
    if (isDownloadingInvoice) return;

    setIsDownloadingInvoice(true);
    setDownloadInvoiceError(undefined);

    const handleError = (message: string) => {
      analytics.track('error_event', {
        error_message: message,
        journey_name: 'Download Invoice',
      });

      setDownloadInvoiceError(t('dashboard.bookings.downloadInvoiceError'));
    };

    try {
      // Validate required parameters
      if (!token) {
        return handleError('Error downloading invoice: Authentication token is missing');
      }
      if (!bookingChannel?.channel) {
        return handleError('Error downloading invoice: Booking channel is missing');
      }
      if (!brand) {
        return handleError('Error downloading invoice: Hotel brand information is missing');
      }
      if (!bookingChannel?.subchannel) {
        return handleError('Error downloading invoice: Booking subchannel is missing');
      }

      const invoiceBookingRef = bookingReference;
      const response = await downloadBookingInvoice(
        {
          bookingRef: [invoiceBookingRef],
          lang: language?.toUpperCase() ?? 'EN',
          channel: bookingChannel.channel,
          hotelBrand: brand,
          subChannel: bookingChannel?.subchannel,
        },
        token
      );

      if (!response) {
        return handleError('Error downloading invoice: No response received from API');
      }

      if (response.errors?.length) {
        return handleError(`GraphQL error downloading invoice: ${response.errors[0].message}`);
      }

      if (!response.data) {
        return handleError('Error downloading invoice: No data in response');
      }

      const invoice = response.data.invoices?.[0];

      if (!invoice) {
        return handleError('Error downloading invoice: No invoices in response');
      }

      const { url, fileName } = invoice;

      analytics.track('invoice_download', {
        bookingReference,
        hotelCode: hotelId,
        channelID: getAnalyticsChannelId(bookingChannel?.channel),
      });

      downloadFromS3PreSignedUrl(url, fileName);
    } catch (error) {
      handleError(`Error downloading invoice: ${error}`);
    } finally {
      setIsDownloadingInvoice(false);
    }
  };

  if (isLoading || isLoadingDiscount || isLoadingHotelInfo || isLoadingRoomTypeInformation) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-BookingHistoryInfoCard">
        <LoadingSpinner loadingText={'Loading'} />
      </Flex>
    );
  }

  if (isError || isErrorDiscount || isErrorHotelInfo) {
    return <Text>{(error as Error).message}</Text>;
  }

  const reservationDetails: ReservationDetails = data?.bookingInfoCardDetails?.reservationDetails;
  const bookingDetails = getBookingDetailsForCard(
    reservationDetails,
    hotelName,
    bookedBy,
    guestSurname,
    roomTypeLabels,
    t,
    rateTags
  );

  return (
    <Box data-testid={'BookingInfoCardContainer'}>
      {hotelId && (
        <BookingHistoryInfoCardComponent
          area={area as Area}
          bookingDetails={bookingDetails}
          bookingReference={reservationDetails?.bookingReference ?? ''}
          basketReference={reservationDetails?.basketReference ?? ''}
          noOfRooms={reservationDetails?.noOfRooms}
          skipContainerRendering={true}
          basketStatus={bookingStatus.toUpperCase()}
          paymentOption={reservationDetails?.paymentOption ?? ''}
          baseDataTestId={baseDataTestId}
          hotelId={hotelId}
          onCancelBooking={handleCancelBooking}
          invoiceSentMsg={{
            displaySentInvoiceMsg:
              isSuccess || (invoiceItems?.sentInvoiceItems.includes(bookingReference) as boolean),
            notificationMessage: t('dashboard.bookings.sendInvoiceNotification'),
          }}
          handleResendInvoiceAction={handleResendInvoice}
          handleDownloadInvoiceAction={handleDownloadInvoice}
          isDownloadingInvoice={isDownloadingInvoice}
          downloadInvoiceError={downloadInvoiceError}
          handleResendConfirmationAction={onModalClose}
          bookingChannel={bookingChannel}
          sourceSystem={reservationDetails?.sourceSystem ?? SOURCE_SYSTEM.OPERA}
          bookingSurname={guestSurname}
          isReadOnly={isReadOnly}
          shouldShowTypeOfBooking={shouldShowTypeOfBooking}
          hotelName={hotelName}
          leadGuestName={leadGuestName}
        />
      )}
      {reservationDetails?.bookingReference && (
        <ResendConfirmationModal
          isModalVisible={isModalVisible}
          onModalClose={onModalClose}
          basketReference={data?.bookingInfoCardDetails?.reservationDetails?.basketReference}
          hotelId={hotelId}
        />
      )}
    </Box>
  );
};

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'relative',
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

export default BookingHistoryInfoCardContainer;
