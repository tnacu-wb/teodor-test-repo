import { Box, BoxProps, Text } from '@chakra-ui/react';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

interface Props {
  isVisible: boolean;
}

export function PaymentMethodChangeNotification({ isVisible }: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  if (!isVisible) return null;

  return (
    <Box mb="5xl" data-testid="PaypalPaymentType-InfoMessages">
      <Box mt="sm" key="payment_custom_message">
        <Notification
          variant="alert"
          status="warning"
          description={
            <>
              <Text fontWeight="bold">{t('booking.payment.paynow.notification.title')}</Text>
              <Text>{t('booking.payment.paynow.notification.message')}</Text>
            </>
          }
          svg={<Alert />}
          wrapperStyles={paypalPaymentTypeInfoMsgStyle}
        />
      </Box>
    </Box>
  );
}

const paypalPaymentTypeInfoMsgStyle = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '26.3rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mt: '-2xl',
} as BoxProps;
