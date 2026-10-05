import { Box, Button, Flex, Text } from '@chakra-ui/react';
import {
  BOOKING_CHANNEL,
  GET_TERMS_AND_CONDITIONS_QUERY,
  PAYPAL_PAYMENT,
  PaymentMethod,
  TermsAndCondition,
} from '@whitbread-eos/api';
import { PencePrice, PaypalWBButton, PaypalWBProps } from '@whitbread-eos/atoms';
import {
  formatUrlTermsConditions,
  renderSanitizedHtml,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useEffect, useState } from 'react';

import {
  DatatransPaymentButton,
  WalletType,
} from '~components/payment/datatrans/DatatransPaymentButton/DatatransPaymentButton';

import {
  amountGroupStyle,
  amountRowStyle,
  amountSubtextStyle,
  backButtonInnerStyle,
  backButtonStyle,
  chevronContainerStyle,
  confirmButtonStyle,
  containerStyle,
  pencePriceOverrideStyle,
  termsSx,
  termsStyle,
} from './PaymentConfirmSection.styles';

// Left-pointing chevron — 24×24, matches the Figma "Arrows / Size=24 / Direction=Left / Style=Chevron"
const ChevronLeft = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden
  >
    <path
      d="M15 18L9 12L15 6"
      stroke="#642587"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export interface PaymentConfirmSectionProps {
  /** Raw price amount as a number, e.g. 701.67 */
  totalAmount?: number;
  /** ISO currency code, e.g. "GBP" or "EUR" */
  currency?: string;
  /** Current locale language code, e.g. "en" or "de" */
  language?: string;
  /** Hotel ID — used to fetch terms and conditions */
  hotelId: string;
  /** Rate plan code — used to fetch terms and conditions */
  ratePlanCode?: string;
  /** Called when the user clicks "Confirm booking" */
  onConfirm?: () => void;
  /** Called when the user clicks "Back" */
  onBack?: () => void;
  /** Whether the confirm button is in a loading/disabled state */
  isLoading?: boolean;
  /** The currently selected payment method — used to swap the confirm button for the PayPal button */
  selectedPaymentType?: PaymentMethod | null;
  /** PayPal button props — rendered in place of the confirm button when PayPal is selected */
  paypalOptions?: PaypalWBProps;
  /** Datatrans wallet button props — rendered in place of the confirm button when Apple Pay / Google Pay is selected */
  datatransPaymentButtonProps?: {
    basketId: string;
    amount: string;
    currencyCode: string;
    walletType: WalletType;
    onAuthorization: (data: { transactionId: string }) => void;
    onError: (error: Error) => void;
  };
  'data-testid'?: string;
}

/**
 * PaymentConfirmSection
 *
 * Sits at the bottom of the payment form. Shows:
 * - Terms & conditions copy (with a T&C link)
 * - Total amount + "inc VAT and fees" label alongside the primary "Confirm booking" CTA
 * - "Back" tertiary button (full width, below the amount row)
 *
 * Desktop (≥lg): the amount and confirm button sit in a row.
 * Mobile (<lg): the confirm button stacks below the amount.
 */
export const PaymentConfirmSection = ({
  totalAmount = 0,
  currency = 'GBP',
  language = 'en',
  hotelId,
  ratePlanCode,
  onConfirm,
  onBack,
  isLoading = false,
  selectedPaymentType,
  paypalOptions,
  datatransPaymentButtonProps,
  'data-testid': testId = 'PaymentConfirmSection',
}: PaymentConfirmSectionProps) => {
  const { country } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const [termsAndConditions, setTermsAndConditions] = useState('');

  const { isLoading: isLoadingTermsAndConditions, data: termsAndConditionsData } = useQueryRequest(
    ['GetTermsAndConditions', hotelId, country, language, ratePlanCode, BOOKING_CHANNEL.PI],
    GET_TERMS_AND_CONDITIONS_QUERY,
    {
      country,
      language,
      hotelId,
      rateCode: ratePlanCode,
      bookingChannel: BOOKING_CHANNEL.PI,
    }
  );

  useEffect(() => {
    if (!isLoadingTermsAndConditions) {
      setTermsAndConditions(
        formatUrlTermsConditions(
          (termsAndConditionsData as TermsAndCondition)?.termsAndConditions?.text
        )
      );
    }
  }, [termsAndConditionsData, isLoadingTermsAndConditions]);

  return (
    <Flex {...containerStyle} data-testid={testId}>
      {/* Terms & conditions */}
      <Text {...termsStyle} sx={termsSx} data-testid={`${testId}-Terms`} as={'div'}>
        {renderSanitizedHtml(termsAndConditions)}
      </Text>

      {/* Amount + Confirm button row — side by side at all breakpoints */}
      <Flex {...amountRowStyle} data-testid={`${testId}-AmountRow`}>
        {/* Amount group */}
        <Flex {...amountGroupStyle} data-testid={`${testId}-AmountGroup`}>
          <Box sx={pencePriceOverrideStyle}>
            <PencePrice
              currency={currency}
              price={
                totalAmount % 1 === 0 ? String(Math.round(totalAmount)) : totalAmount.toFixed(2)
              }
              language={language}
              size="m"
              data-testid={`${testId}-Amount`}
            />
          </Box>
          <Text {...amountSubtextStyle} data-testid={`${testId}-AmountSubtext`}>
            inc VAT and fees
          </Text>
        </Flex>

        {/* Confirm booking — primary CTA, or PayPal / wallet button when those methods are selected */}
        {PAYPAL_PAYMENT === selectedPaymentType?.name ? (
          paypalOptions ? (
            <Flex align="center" justify="flex-end" flex={1}>
              <Box sx={{ '& > *': { mt: '0 !important' } }}>
                <PaypalWBButton {...paypalOptions} />
              </Box>
            </Flex>
          ) : null
        ) : (selectedPaymentType?.type === 'AP' || selectedPaymentType?.type === 'GP') &&
          datatransPaymentButtonProps ? (
          <Flex align="center" justify="flex-end" flex={1} minW={0}>
            <Box sx={{ '& > *': { mt: '0 !important' } }}>
              <DatatransPaymentButton {...datatransPaymentButtonProps} />
            </Box>
          </Flex>
        ) : (
          <Button
            {...confirmButtonStyle}
            onClick={onConfirm}
            isLoading={isLoading}
            isDisabled={isLoading}
            aria-label={t('ccui.payment.confirmBooking.button')}
            data-testid={`${testId}-ConfirmButton`}
          >
            {t('ccui.payment.confirmBooking.button')}
          </Button>
        )}
      </Flex>

      {/* Back — tertiary CTA */}
      <Button
        {...backButtonStyle}
        onClick={onBack}
        isDisabled={isLoading}
        aria-label="Back"
        data-testid={`${testId}-BackButton`}
      >
        <Flex {...backButtonInnerStyle}>
          <Box {...chevronContainerStyle}>
            <ChevronLeft />
          </Box>
          Back
        </Flex>
      </Button>
    </Flex>
  );
};
