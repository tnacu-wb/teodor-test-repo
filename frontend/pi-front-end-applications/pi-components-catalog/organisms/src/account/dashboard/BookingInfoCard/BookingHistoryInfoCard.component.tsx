import type { BoxProps, ChakraProps, FlexProps } from '@chakra-ui/react';
import { Box, Flex, Text, useMediaQuery } from '@chakra-ui/react';
import {
  Area,
  BASKET_STATUS,
  BOOKING_TYPE,
  BookingChannelCriteria,
  GET_DASHBOARD_BASKET,
} from '@whitbread-eos/api';
import { Card, Error, Notification, Success } from '@whitbread-eos/atoms';
import { SecureBookingButton } from '@whitbread-eos/molecules';
import { formatDataTestId, upperOnlyFirst, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import BookingActions from './BookingActions';
import { BookingDetailsComponent } from './BookingDetails';
import { BookingDetailsProp } from './BookingDetails/BookingDetails.container';
import HotelDetails from './HotelDetails';

interface InvoiceMessage {
  displaySentInvoiceMsg: boolean;
  notificationMessage: string;
}

export interface Props {
  bookingDetails: BookingDetailsProp;
  noOfRooms?: number;
  skipContainerRendering?: boolean;
  bookingReference: string;
  basketReference: string | null;
  area: Area;
  basketStatus: string;
  paymentOption: string;
  baseDataTestId: string;
  hotelId: string;
  onCancelBooking?: () => void;
  invoiceSentMsg: InvoiceMessage;
  handleResendInvoiceAction: () => void;
  handleDownloadInvoiceAction: () => void;
  isDownloadingInvoice?: boolean;
  downloadInvoiceError?: string;
  handleResendConfirmationAction: () => void;
  bookingChannel: BookingChannelCriteria;
  sourceSystem: string;
  isAmendPage?: boolean;
  bookingSurname: string;
  isReadOnly?: boolean;
  shouldShowTypeOfBooking?: boolean;
  hotelName?: string;
  leadGuestName?: string;
}

export default function BookingHistoryInfoCardComponent(props: Readonly<Props>) {
  const [isMobileView] = useMediaQuery('(max-width: 765px)');
  const status =
    props.basketStatus === 'FUTURE'
      ? BOOKING_TYPE.UPCOMING
      : BOOKING_TYPE[props.basketStatus as keyof typeof BOOKING_TYPE];

  const { t } = useTranslation();

  const showHotelDetails = props.basketStatus !== BASKET_STATUS.CANCELLED || props?.isReadOnly;

  // basket status
  const { data: getBasketStatus } = useQueryRequest(
    ['basket'],
    GET_DASHBOARD_BASKET,
    {
      basketReference: props?.basketReference,
    },
    { enabled: !!props?.basketReference, staleTime: 0, cacheTime: 0 }
  );

  const secureBookingData = {
    basketReference: props?.basketReference as string,
    bookingReference: props?.bookingReference as string,
    arrivalDate: props?.bookingDetails?.arrivalDate as string,
    area: props?.area,
    paymentOption: props?.bookingDetails?.paymentOption,
    bookingStatus: getBasketStatus?.basket?.status as string,
    hotelInfo: {
      hotelId: props?.hotelId,
      bookingFlowId: '',
    },
  };

  return (
    <>
      {!isMobileView && props?.basketStatus !== BASKET_STATUS.CANCELLED && (
        <SecureBookingButton data={secureBookingData} />
      )}
      <Card {...(cardWrapperStyle(props.basketStatus, props?.isReadOnly) as BoxProps)}>
        <Flex {...(hotelDetailsWrapperStyle(props?.isReadOnly) as FlexProps)}>
          {!props?.isReadOnly ? (
            <BookingActions
              {...props}
              bookingStatus={props.basketStatus}
              hideBookingStatus={true}
              role=""
              bookingType={status}
              shouldRenderStatusAndActions={props.isAmendPage}
            />
          ) : (
            <Box {...bookerDetailsStyles}>
              <Box>
                <Text {...labelTextStyle}>{t('ccui.manageBooking.hotelName')}</Text>
                <Text {...textStyle}> {props?.hotelName}</Text>
              </Box>
              <Box>
                <Text {...labelTextStyle}>{t('ccui.idv.personalInformation.bookerName')}</Text>
                <Text {...textStyle}>{props?.bookingDetails?.bookedBy}</Text>
              </Box>
            </Box>
          )}
          {isMobileView && props?.basketStatus !== BASKET_STATUS.CANCELLED && (
            <SecureBookingButton data={secureBookingData} />
          )}
          {showHotelDetails && (
            <HotelDetails
              {...props}
              basketReference={props.basketReference}
              bookingReference={props.bookingReference}
            />
          )}
        </Flex>
        <BookingDetailsComponent
          bookingReference={props.bookingReference}
          basketReference={props.basketReference}
          bookingStatus={upperOnlyFirst(props.basketStatus)}
          bookingDetails={props.bookingDetails}
          noOfRooms={props.noOfRooms}
          skipContainerRendering={props.skipContainerRendering}
          baseDataTestId={props.baseDataTestId}
          getBookingStatus={props.onCancelBooking}
          bookingChannel={props.bookingChannel}
          sourceSystem={props.sourceSystem}
          area={props.area}
          showExtras={!!props?.sourceSystem || !!props?.isReadOnly}
          bookingSurname={props.bookingSurname}
          shouldShowTypeOfBooking={props?.shouldShowTypeOfBooking}
          isReadOnly={props?.isReadOnly}
        />
      </Card>
      {props.invoiceSentMsg.displaySentInvoiceMsg && (
        <Box mt="lg" data-testid={formatDataTestId(props.baseDataTestId, 'Notification-Invoice')}>
          <Notification
            svg={<Success />}
            status="success"
            description={props.invoiceSentMsg.notificationMessage}
            variant="success"
          />
        </Box>
      )}
      {props.downloadInvoiceError && (
        <Box
          mt="lg"
          data-testid={formatDataTestId(props.baseDataTestId, 'Notification-Download-Error')}
        >
          <Notification
            svg={<Error />}
            status="error"
            description={props.downloadInvoiceError}
            variant="error"
          />
        </Box>
      )}
    </>
  );
}

const textStyle = {
  color: 'darkGrey2',
  fontSize: 'lg',
  lineHeight: '3',
};
const bookerDetailsStyles = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'flex-start',
  gap: '1.43rem',
} as ChakraProps;

const labelTextStyle = {
  fontSize: 'md',
  fontWeight: 'semibold',
  lineHeight: '3',
  color: 'darkGrey1',
};

const hotelDetailsWrapperStyle = (isReadOnly?: boolean) =>
  isReadOnly
    ? {
        flexDirection: {
          mobile: 'row',
        },
        columnGap: '4.12rem',
        minWidth: 'max-content',
      }
    : {
        flexDirection: {
          mobile: 'column',
          lg: 'row',
        },
        gap: 'lg',
      };

const cardWrapperStyle = (basketStatus: string, isReadOnly?: boolean) =>
  isReadOnly
    ? {
        backgroundColor: '#F9F9F9',
        display: 'grid',
        flexDirection: 'column',
        boxShadow: 'none',
        gridTemplateColumns: { lg: '1fr 1fr' },
        gap: '4.12rem',
        border: 'none',
        padding: '2.1rem 0rem 0rem 2.5rem',
        minHeight: '28rem',
      }
    : {
        display: basketStatus === BASKET_STATUS.CANCELLED ? { mobile: 'flex', lg: 'grid' } : 'flex',
        gridTemplateColumns: { lg: '1fr 1fr' },
        boxShadow: 'none',
        justifyContent: 'space-between',
        flexDirection: { mobile: 'column', lg: 'row' },
        border: 'none',
        padding: '0',
      };
