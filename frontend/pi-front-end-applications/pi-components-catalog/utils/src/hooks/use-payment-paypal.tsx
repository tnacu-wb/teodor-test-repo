'use client';

import {
  Area,
  GET_PAYMENT_METHODS_QUERY,
  GET_PAYMENT_METHODS_QUERY_BB,
  PAYPAL_PAYMENT,
  PaymentMethod,
  UserType,
  FT_PI_ENABLE_PAYPAL,
  FT_BB_ENABLE_PAYPAL,
  PaymentMethods,
  PaymentMethodsCriteria,
} from '@whitbread-eos/api';
import { useState, useMemo, useEffect } from 'react';

import useCustomLocale from './use-custom-locale';
import useFeatureToggle from './use-feature-toggle';
import { useQueryRequest } from './use-request';
import { useAuthToken } from './useAuthToken';

export function usePaymentPaypal(basketReference: string, channel: string) {
  const { language, country } = useCustomLocale();
  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();
  const [injectPaypalProvider, setInjectPaypalProvider] = useState<boolean>(false);

  const userType = channel === Area.BB.toUpperCase() ? UserType.Business : UserType.Leisure;
  const FT_ENABLE_PAYPAL =
    channel === Area.BB.toUpperCase() ? FT_BB_ENABLE_PAYPAL : FT_PI_ENABLE_PAYPAL;

  const { [FT_ENABLE_PAYPAL]: isPaypalEnabled } = useFeatureToggle();

  const paramsForQueryPaymentMethods: PaymentMethodsCriteria = {
    language,
    country,
    basketReference,
    userType: authToken ? userType : undefined,
    clientChannel: channel,
  };
  const {
    isLoading,
    data: paymentMethodData,
  }: {
    data: PaymentMethods;
    isLoading: boolean;
  } = useQueryRequest(
    ['getPaymentMethods', language, country, basketReference],
    channel === Area.BB.toUpperCase() ? GET_PAYMENT_METHODS_QUERY_BB : GET_PAYMENT_METHODS_QUERY,
    { ...paramsForQueryPaymentMethods },
    { enabled: !isAuthTokenLoading },
    authToken
  );

  const paypalPaymentData = useMemo(() => {
    if (paymentMethodData?.paymentMethods) {
      return (
        paymentMethodData?.paymentMethods.find(
          (item: PaymentMethod) => PAYPAL_PAYMENT === item.name
        ) || null
      );
    } else {
      return null;
    }
  }, [paymentMethodData]);
  useEffect(() => {
    if (
      !isLoading &&
      isPaypalEnabled &&
      paypalPaymentData?.clientToken &&
      paypalPaymentData?.enabled
    ) {
      setInjectPaypalProvider(true);
    }
  }, [isLoading, isPaypalEnabled, paypalPaymentData]);

  return {
    injectPaypalProvider,
    paypalPaymentData,
  };
}
