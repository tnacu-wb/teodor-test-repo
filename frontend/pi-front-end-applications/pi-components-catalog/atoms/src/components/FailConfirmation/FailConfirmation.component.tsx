import { Box, Divider, Text } from '@chakra-ui/react';
import {
  formatDataTestId,
  renderSanitizedHtml,
  ternaryCondition,
  useCustomLocale,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React from 'react';

import Info from '../Icon';
import Notification from '../Notification';

interface FailConfirmationData {
  emailAddress: string;
  bookingReference: string | string[] | undefined;
  notificationText: string;
  paymentOption: string;
  sendMail?: boolean;
  isCcui?: boolean;
  isGermanHotel?: boolean;
}

interface Props {
  data: FailConfirmationData;
  t: (x: string, y?: { [key: string]: string }) => string;
}

export default function FailConfirmation({ data, t }: Readonly<Props>) {
  const { language } = useCustomLocale();
  const baseDataTestId = 'FailConfirmation';
  const { query } = useRouter();
  if (!data) {
    return null;
  }

  const {
    emailAddress,
    bookingReference,
    notificationText,
    paymentOption,
    sendMail = true,
    isCcui = false,
    isGermanHotel = false,
  } = data;

  const updateEmailMessage = ternaryCondition(
    isCcui,
    ternaryCondition(
      sendMail,
      t('booking.confirmation.updateEmailMessage').replace('[emailAddress]', emailAddress),
      ternaryCondition(
        language !== 'de',
        t('ccui.booking.confirmation.bookingErrorMessage.reservedWithCard.PI'),
        ''
      )
    ),
    t('booking.confirmation.updateEmailMessage').replace('[emailAddress]', emailAddress)
  );

  const payNowUpdateEmailMsg = ternaryCondition(
    paymentOption === 'PAY_NOW',
    ternaryCondition(
      isCcui,
      t('ccui.booking.confirmation.bookingErrorMessage.payNow'),
      t('booking.confirmation.paymentMessage')
    ),
    ''
  );

  const bookingErrorMsgPOA = ternaryCondition(
    isGermanHotel,
    t('ccui.booking.confirmation.bookingErrorMessage.reservedWithCard.PID'),
    t('ccui.booking.confirmation.bookingErrorMessage.reservedWithCard.PI')
  );

  return (
    <>
      <Text {...titleStyle} data-testid={formatDataTestId(baseDataTestId, 'Title-Name')}>
        {query?.['secure-booking'] === 'true'
          ? t('nonguaranteed.booking.secureBookingError')
          : t('booking.confirmation.sorry')}
      </Text>

      {sendMail ? (
        <Text data-testid={formatDataTestId(baseDataTestId, 'Email')} fontSize="md">
          {renderSanitizedHtml(updateEmailMessage + ' ' + payNowUpdateEmailMsg)}
        </Text>
      ) : (
        <Text data-testid={formatDataTestId(baseDataTestId, 'BookingErrorMsgPOA')} fontSize="md">
          {bookingErrorMsgPOA}
        </Text>
      )}

      <Divider my="2xl" />
      <Text data-testid={formatDataTestId(baseDataTestId, 'Label')} {...bookingRefLabelStyle}>
        {t('ccui.booking.confirmation.reference')}
      </Text>
      <Text data-testid={formatDataTestId(baseDataTestId, 'Id')} {...bookingRefStyle}>
        {bookingReference}
      </Text>
      <Box mb="lg" data-testid={formatDataTestId(baseDataTestId, 'Notification')}>
        <Notification
          maxWidth="full"
          variant="info"
          status="info"
          description={notificationText}
          svg={<Info />}
        />
      </Box>
    </>
  );
}

const titleStyle = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  mb: 'md',
};

const bookingRefLabelStyle = {
  mb: 'xs',
  fontSize: 'lg',
  fontWeight: 'bold',
};

const bookingRefStyle = { mb: 'lg', fontSize: '2xl' };
