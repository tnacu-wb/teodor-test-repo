import { Box, Text } from '@chakra-ui/react';
import { formatDataTestId, formatDate, useSemanticTypography } from '@whitbread-eos/utils';
import { formatDistance } from 'date-fns';
import React from 'react';

interface ConfirmationDetailsData {
  bookingReference: string | string[] | undefined;
  hotelName: string;
  hotelAddress: string;
  hotelTel: string;
  roomReservationStartDate: string;
  roomReservationEndDate: string;
  leadGuestTitle: string;
  leadGuestName: string;
  rateType: string;
  paymentOption: string;
}

interface ConfirmationDetailsProps {
  confirmationDetails: ConfirmationDetailsData;
}

export interface Props {
  data: ConfirmationDetailsProps;
  t: (x: string, y?: { [key: string]: string }) => string;
  currentLang: string | undefined;
}

export default function ConfirmationDetails({ data, t, currentLang }: Readonly<Props>) {
  const { confirmationDetails } = data;
  const baseDataTestId = 'BookingReferenceDetails';
  const getTypographyProps = useSemanticTypography();

  if (!confirmationDetails) {
    return <></>;
  }

  const {
    bookingReference,
    hotelName,
    hotelAddress,
    hotelTel,
    roomReservationStartDate,
    roomReservationEndDate,
    leadGuestTitle,
    leadGuestName,
    rateType,
    paymentOption,
  } = confirmationDetails;

  const getNrNights = () => {
    // replace all leading non-digits with nothing
    return parseInt(
      formatDistance(new Date(roomReservationStartDate), new Date(roomReservationEndDate)).replace(
        /^\D+/g,
        ''
      )
    );
  };

  const getConfirmationBtnLabel = () => {
    switch (paymentOption) {
      case 'PAY_ON_ARRIVAL':
        return t('booking.confirmation.reservedWithCard');
      case 'PAY_NOW':
        return t('booking.confirmation.paymentTaken');
      case 'RESERVE_WITHOUT_CARD':
        return t('booking.confirmation.reservedWithoutCard');
      default:
        return '';
    }
  };

  return (
    <Box {...contentWrapperStyle}>
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'Label')}
        sx={{ '@media print': { mt: 'md', fontSize: 'xxs' } }}
        {...bookingRefLabelLayoutStyles}
        {...getTypographyProps(bookingRefLabelLegacyTypography, bookingRefLabelSemanticTypography)}
      >
        {t('booking.confirmation.bookingReference')}
      </Text>
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'Id')}
        sx={{ '@media print': { mt: '0', fontSize: 'xxs' } }}
        {...bookingRefLayoutStyles}
        {...getTypographyProps(bookingRefLegacyTypography, bookingRefSemanticTypography)}
      >
        {bookingReference}
      </Text>

      <Text
        data-testid={formatDataTestId(baseDataTestId, 'Hotel')}
        {...hotelNameLayoutStyles}
        {...getTypographyProps(hotelNameLegacyTypography, hotelNameSemanticTypography)}
        sx={{ '@media print': { fontWeight: 'semibold' } }}
      >
        {hotelName}
      </Text>
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'HotelAdress')}
        {...hotelAddressLayoutStyles}
        {...getTypographyProps(hotelAddressLegacyTypography, hotelAddressSemanticTypography)}
        sx={{ '@media print': { fontWeight: 'semibold' } }}
      >
        {hotelAddress}
      </Text>
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'HotelPhone')}
        {...bookingTelLayoutStyles}
        {...getTypographyProps(bookingTelLegacyTypography, bookingTelSemanticTypography)}
        sx={{ '@media print': { mb: '0' } }}
      >
        {t('booking.confirmation.tel')} {hotelTel}
      </Text>
      <Box display="none" sx={{ '@media print': { display: 'block', fontSize: 'md' } }}>
        <Text mb="sm">
          {t('booking.hotel.summary.arriving')}:{' '}
          <strong>{formatDate(roomReservationStartDate, 'EEEE, d LLL yyyy', currentLang)}</strong>{' '}
          {t('booking.hotel.summary.after2pm')}
        </Text>
        <Text mb="sm">
          {t('booking.hotel.summary.checkout')}:{' '}
          <strong>{formatDate(roomReservationEndDate, 'EEEE, d LLL yyyy', currentLang)}</strong>{' '}
          {t('booking.confirmation.checkoutTime')}
        </Text>
        <Text mb="sm">
          {t('booking.summary.nights')}: <strong>{getNrNights()}</strong>
        </Text>
        <Text mb="sm">
          {t('booking.summary.rate')} <strong>{rateType}</strong>
        </Text>
        <Text mb="sm" className="sessioncamhidetext assist-no-show">
          {t('booking.confirmation.bookedBy')}:{' '}
          <strong>
            {leadGuestTitle} {leadGuestName}
          </strong>
        </Text>
        <Text mb="md">
          {t('booking.management.bookingReview.paymentStatusTitle')}:{' '}
          <strong>{getConfirmationBtnLabel()}</strong>
        </Text>
      </Box>
    </Box>
  );
}
const contentWrapperStyle = {
  w: 'full',
};

const bookingRefLabelLayoutStyles = {
  mb: 'xs',
};

const bookingRefLabelLegacyTypography = {
  fontSize: 'lg',
  fontWeight: 'bold',
};

const bookingRefLabelSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const bookingRefLayoutStyles = {
  mb: 'lg',
};

const bookingRefLegacyTypography = {
  fontSize: '2xl',
};

const bookingRefSemanticTypography = {
  textStyle: 'body-l-regular',
};

const hotelNameLayoutStyles = {
  mb: 'xs',
};

const hotelNameLegacyTypography = {
  fontSize: '2xl',
  fontWeight: 'bold',
};

const hotelNameSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const hotelAddressLayoutStyles = {
  mb: 'xs',
};

const hotelAddressLegacyTypography = {
  fontSize: '2xl',
};

const hotelAddressSemanticTypography = {
  textStyle: 'body-l-regular',
};

const bookingTelLayoutStyles = {
  mb: '2xl',
};

const bookingTelLegacyTypography = {
  fontSize: 'md',
};

const bookingTelSemanticTypography = {
  textStyle: 'body-l-regular',
};
