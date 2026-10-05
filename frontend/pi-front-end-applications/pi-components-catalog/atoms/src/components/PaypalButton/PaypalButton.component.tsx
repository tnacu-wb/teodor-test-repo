import { Box } from '@chakra-ui/react';
import {
  BraintreePayPalButtons,
  usePayPalScriptReducer,
  BraintreePayPalButtonsComponentProps,
} from '@paypal/react-paypal-js';
import { DISPATCH_ACTION } from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';
import React, { useEffect } from 'react';

import Error from '../../assets/icons/Error';
import Notification from '../Notification';

/*
 * The documentation for PayPal react library is available here -  https://github.com/paypal/react-paypal-js
 */
export interface PaypalWBProps extends BraintreePayPalButtonsComponentProps {
  currency?: string;
  disabled?: boolean;
}

function PaypalWBButton({
  currency,
  onApprove,
  onError,
  createBillingAgreement,
  style,
}: Readonly<PaypalWBProps>) {
  const [{ options, isRejected, isPending }, dispatch] = usePayPalScriptReducer();
  const { t } = useTranslation(['common']);

  useEffect(() => {
    dispatch({
      type: DISPATCH_ACTION.RESET_OPTIONS,
      value: {
        ...options,
        currency: currency,
      },
    });
  }, [currency]);

  return (
    <>
      {isRejected && !isPending ? (
        <Box mt="sm" mb="5xl" key="payment_custom_message">
          <Notification
            variant="error"
            status="error"
            description={t('booking.payment.paypal.loading.error')}
            svg={<Error />}
            wrapperStyles={{ mb: '-2xl', mt: '2xl' }}
          />
        </Box>
      ) : (
        <Box {...buttonStyle}>
          <BraintreePayPalButtons
            style={{ ...style }}
            disabled={false}
            fundingSource="paypal"
            forceReRender={[currency]}
            createBillingAgreement={createBillingAgreement}
            onApprove={onApprove}
            onError={onError}
          />
        </Box>
      )}
    </>
  );
}

export default PaypalWBButton;

const buttonStyle = {
  mt: 'lg',
  w: { mobile: 'full', lg: '18rem', xl: '19.3125rem' },
};
