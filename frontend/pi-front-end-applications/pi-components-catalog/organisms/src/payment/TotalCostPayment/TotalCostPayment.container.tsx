import {
  GET_TERMS_AND_CONDITIONS_QUERY,
  PaymentOption,
  RatePlanTotalCost,
  AddressGuestInput,
  PaymentMethod,
  TermsAndCondition,
} from '@whitbread-eos/api';
import { PaypalWBProps } from '@whitbread-eos/atoms';
import { TotalCostCard } from '@whitbread-eos/molecules';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import React, { Dispatch, SetStateAction } from 'react';

export interface Props {
  hotelName: string;
  hotelId: string;
  rateCode: string;
  selectedPaymentDetail: PaymentOption;
  ratePlan: RatePlanTotalCost;
  isBillingAddressDisplayed: boolean;
  errorMessagePayment?: string;
  bookingChannel?: string;
  continueToNextStep: (billingAddress?: AddressGuestInput) => void;
  isAmendPage?: boolean;
  setValidateQuestions?: Dispatch<SetStateAction<boolean>>;
  hasError?: boolean;
  selectedPaymentType?: PaymentMethod | null;
  paypalOptions?: PaypalWBProps;
  additionalAmountLabel?: string;
  isDatatransEnabled?: boolean;
  datatransPayPalButton?: React.ReactNode;
  datatransWalletButton?: React.ReactNode;
}

export default function TotalCostPayment({
  hotelName,
  hotelId,
  rateCode,
  selectedPaymentDetail,
  ratePlan,
  isBillingAddressDisplayed,
  errorMessagePayment,
  bookingChannel,
  continueToNextStep,
  isAmendPage,
  setValidateQuestions,
  hasError,
  selectedPaymentType,
  paypalOptions,
  additionalAmountLabel,
  isDatatransEnabled,
  datatransPayPalButton,
  datatransWalletButton,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();

  const { data, isError, error, isLoading } = useQueryRequest(
    ['GetTermsAndConditions', hotelId, country, language, rateCode, bookingChannel],
    GET_TERMS_AND_CONDITIONS_QUERY,
    {
      country,
      language,
      hotelId,
      rateCode,
      bookingChannel,
    }
  );

  return (
    <TotalCostCard
      {...{ data: data as TermsAndCondition, isError, error, isLoading }}
      hotelName={hotelName}
      selectedPaymentDetail={selectedPaymentDetail}
      ratePlan={ratePlan}
      isBillingAddressDisplayed={isBillingAddressDisplayed}
      errorMessagePayment={errorMessagePayment}
      continueToNextStep={continueToNextStep}
      isAmendPage={isAmendPage}
      setValidateQuestions={setValidateQuestions}
      hasError={hasError}
      {...(selectedPaymentType && { selectedPaymentType })}
      {...(paypalOptions && { paypalOptions })}
      additionalAmountLabel={additionalAmountLabel}
      isDatatransEnabled={isDatatransEnabled}
      datatransPayPalButton={datatransPayPalButton}
      datatransWalletButton={datatransWalletButton}
    />
  );
}
