import { Box } from '@chakra-ui/react';
import {
  AmendPaymentProps,
  BookingConfirmationType,
  SummaryOfPaymentsType,
} from '@whitbread-eos/api';
import { Alert, Info, Notification } from '@whitbread-eos/atoms';
import { AmendBillingAddress, PaymentDetails } from '@whitbread-eos/molecules';
import { isThisMonth } from 'date-fns';
import { useTranslation } from 'next-i18next';

import PaymentTypeContainer from '../../booking/PaymentType/PaymentType.container';
import TotalCostPayment from '../../payment/TotalCostPayment';
import { AMEND_CARD_NAME, NEW_CARD, NEW_PIBA, PAY_ON_ARRIVAL } from '../constants';

const DISABLED_CARD_OPTIONS = [NEW_CARD, NEW_PIBA];

interface Props extends AmendPaymentProps {
  bookingConfirmation: BookingConfirmationType;
  amendSummary: SummaryOfPaymentsType;
  continueToNextStep: () => void;
  shouldRetryPayment: boolean;
  additionalAmountLabel: string;
  temporaryBasketReference?: string;
}

export default function AmendPayment({
  continueToNextStep,
  selectedPaymentDetail,
  setSelectedPaymentDetail,
  selectedPaymentType,
  hotelName,
  errorMessagePayment,
  setSelectedPaymentType,
  basketReference,
  bookingConfirmation,
  variant,
  userType,
  amendSummary,
  shouldRetryPayment,
  additionalAmountLabel,
}: Props & AmendPaymentProps) {
  const baseDataTestId = 'amend-payment';
  const { t } = useTranslation();
  const billingAddress = bookingConfirmation?.reservationByIdList[0]?.billing.address;
  const billingAddressLabel = `${billingAddress?.postalCode} ${billingAddress?.addressLine1}`;

  const {
    paymentOptions: amendPaymentOptions,
    payOnArrival,
    paymentCardDetails: {
      cardNumberMasked,
      token,
      expirationDate,
      cardType,
      cardHolderName,
      cardName,
      cardLogoSrc,
    },
  } = amendSummary;

  const availablePaymentOptions = Object.entries(amendPaymentOptions).map(([key, value], i) => ({
    type: key.replace(/([A-Z])/g, '_$1').toUpperCase(),
    order: i + 1,
    enabled: value,
  }));

  const disabledCardOptions =
    selectedPaymentDetail?.type === PAY_ON_ARRIVAL
      ? [...DISABLED_CARD_OPTIONS, AMEND_CARD_NAME]
      : DISABLED_CARD_OPTIONS;

  const isCardAvailable = !isThisMonth(new Date(expirationDate)) && amendPaymentOptions.payNow;

  const amendSavedCard = {
    ...selectedPaymentType,
    name: cardName,
    type: AMEND_CARD_NAME,
    acceptedCardTypes: [],
    card: {
      token: token,
      cardNumber: cardNumberMasked,
      expiryMonth: new Date(expirationDate).toLocaleDateString('en-GB', {
        month: '2-digit',
      }),
      expiryYear: new Date(expirationDate).toLocaleDateString('en-GB', {
        year: '2-digit',
      }),
      logoSrc: cardLogoSrc,
      cardHolderName: cardHolderName,
      cnpRequired: false,
      type: cardType,
      cardType: '',
    },
    paymentOptions: availablePaymentOptions,
    enabled: isCardAvailable,
  };

  return (
    <Box
      {...containerStyles}
      data-testid={`${baseDataTestId}-container`}
      id={`${baseDataTestId}-container`}
    >
      {renderPaymentNotifications()}
      <PaymentDetails
        selectedPaymentType={selectedPaymentType}
        selectedPaymentDetail={selectedPaymentDetail}
        setSelectedPaymentDetail={setSelectedPaymentDetail}
        errorMessagePayment={errorMessagePayment}
        t={t}
      />
      <PaymentTypeContainer
        selectedPaymentDetail={selectedPaymentDetail}
        selectedPaymentType={selectedPaymentType}
        onPaymentTypeClick={setSelectedPaymentType}
        userType={userType}
        variant={variant}
        amendBasketReference={basketReference}
        disabledCardOptions={disabledCardOptions}
        amendPaymentCard={amendSavedCard}
      />
      <AmendBillingAddress
        billingAddress={billingAddressLabel}
        selectedPaymentDetail={selectedPaymentDetail}
      />
      <TotalCostPayment
        hotelName={hotelName}
        hotelId={bookingConfirmation.hotelId}
        rateCode={bookingConfirmation?.reservationByIdList?.[0]?.roomStay?.ratePlanCode}
        ratePlan={{
          name: 'POA new',
          totalCost: {
            currency: bookingConfirmation.currencyCode,
            amount: `${payOnArrival}`,
          },
        }}
        isBillingAddressDisplayed={false}
        continueToNextStep={continueToNextStep}
        selectedPaymentDetail={selectedPaymentDetail}
        errorMessagePayment={errorMessagePayment}
        bookingChannel={variant}
        additionalAmountLabel={additionalAmountLabel}
        isAmendPage
      />
    </Box>
  );

  function renderPaymentNotifications() {
    return (
      <Box mb="2xl">
        <Notification
          prefixDataTestId={`${baseDataTestId}-options`}
          status="info"
          variant="info"
          svg={<Info />}
          isInnerHTML
          description={t('amend.notification.paymentOptions')}
        />
        <Notification
          prefixDataTestId={`${baseDataTestId}-do-not-go-back`}
          status="warning"
          variant="alert"
          svg={<Alert />}
          isInnerHTML
          description={t('amend.notification.dontGoBack')}
          wrapperStyles={{ mt: 'var(--chakra-space-lg)' }}
        />
        {shouldRetryPayment && (
          <Box {...containerPaymentErrorStyles}>
            <Notification
              prefixDataTestId={`${baseDataTestId}-payment-error-something-wrong`}
              status="error"
              variant="error"
              svg={<Alert />}
              isInnerHTML
              description={t('amend.error.tryAgain')}
              wrapperStyles={{ mt: 'var(--chakra-space-lg)' }}
            />
          </Box>
        )}
      </Box>
    );
  }
}

const containerStyles = {
  marginTop: 'var(--chakra-space-3xl)',
  w: {
    base: 'full',
    lg: '50.5rem',
    xl: '54rem',
  },
};
const containerPaymentErrorStyles = {
  w: {
    base: 'full',
    lg: '50.5rem',
    xl: '54rem',
  },
};
