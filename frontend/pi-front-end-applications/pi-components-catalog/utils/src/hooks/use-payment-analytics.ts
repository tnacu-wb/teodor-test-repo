'use client';

import {
  PAYMENT_ANALYTICS_KEY,
  PaymentAnalytics,
  PaymentOption,
  paymentOptions,
  paymentSteps,
} from '@whitbread-eos/api';
import { ParsedUrlQuery } from 'querystring';
import { useEffect } from 'react';

import { isSecureBookingPage, type secureBookingType } from '../helpers/payment';
import analytics from '../services/analyticsService/analytics';
import updateDashboardAnalytics from '../services/analyticsService/dashboardAnalytics';
import { useSessionStorage } from './use-session-storage';

export interface UsePaymentAnalyticsParams {
  paymentStepState: string;
  basketReference: string | null;
  paymentCardSelected: string | undefined;
  selectedPaymentDetail: PaymentOption;
  disablePaymentOptions: boolean | undefined;
  isPaypalSuccess: boolean;
  initiatePaypalPaymentMutationData: { initiatePaypalPayment?: { status?: string } } | undefined;
  routerQuery: ParsedUrlQuery;
  isSecureBookingFeatureEnabled: boolean;
  bkngData: any;
  packages: any;
  updateAncillariesAnalytics: (...args: any[]) => void;
}

/**
 * Manages all payment analytics side-effects and the `confAnalytics` session-storage
 * entry that feeds the confirmation page. Extracted from DatatransPage to keep that
 * component free of analytics wiring.
 *
 * Behaviour is preserved exactly from the original DatatransPage implementation,
 * including the render-time `analytics.update({ hasPaymentFailure: undefined })` call.
 */
export function usePaymentAnalytics({
  paymentStepState,
  basketReference,
  paymentCardSelected,
  selectedPaymentDetail,
  disablePaymentOptions,
  isPaypalSuccess,
  initiatePaypalPaymentMutationData,
  routerQuery,
  isSecureBookingFeatureEnabled,
  bkngData,
  packages,
  updateAncillariesAnalytics,
}: UsePaymentAnalyticsParams): {
  confAnalytics: PaymentAnalytics;
  setConfAnalytics: (
    value: PaymentAnalytics | ((prev: PaymentAnalytics) => PaymentAnalytics)
  ) => void;
} {
  const [confAnalytics, setConfAnalytics] = useSessionStorage<PaymentAnalytics>(
    PAYMENT_ANALYTICS_KEY,
    {}
  );

  // Render-time reset — matches the original DatatransPage render-time call so that
  // any stale `hasPaymentFailure` flag is cleared whenever the page renders.
  if (typeof window !== 'undefined') {
    analytics.update({ hasPaymentFailure: undefined });
  }

  // Card-details step: record selected card type + pay-now flag.
  // Payment-details step with reserve-without-card: record the option selection.
  useEffect(() => {
    if (paymentStepState === paymentSteps.CARD_DETAILS && basketReference) {
      window.localStorage.setItem('3cpVisited', basketReference.toString());
      setConfAnalytics({
        ...confAnalytics,
        paymentCardSelected,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
      analytics.update({
        paymentCardSelected,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
    }
    if (
      paymentStepState === paymentSteps.PAYMENT_DETAILS &&
      basketReference &&
      selectedPaymentDetail?.type === paymentOptions.RESERVE_WITHOUT_CARD
    ) {
      setConfAnalytics({
        ...confAnalytics,
        paymentCardSelected: selectedPaymentDetail.type,
        paymentTakenNow: '',
      });
      analytics.update({
        paymentCardSelected: selectedPaymentDetail.type,
        paymentTakenNow: 'false',
      });
    }
  }, [paymentStepState, selectedPaymentDetail]);

  // Payment outage: when payments are disabled record that in analytics.
  useEffect(() => {
    if (disablePaymentOptions) {
      const paymentOutageData = {
        paymentCardSelected: paymentOptions.RESERVE_WITHOUT_CARD,
        paymentOutage: disablePaymentOptions,
        cardType: paymentOptions.RESERVE_WITHOUT_CARD,
      };
      analytics.update({ ...paymentOutageData });
      setConfAnalytics({ ...paymentOutageData });
    }
  }, [disablePaymentOptions]);

  // PayPal success: record paypal flag + pay-now, then hand off to caller for navigation.
  useEffect(() => {
    if (
      isPaypalSuccess &&
      initiatePaypalPaymentMutationData?.initiatePaypalPayment?.status === 'NOT_REQUIRED'
    ) {
      setConfAnalytics({
        ...confAnalytics,
        paypal: true,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
      analytics.update({
        paypal: true,
        paymentTakenNow: (selectedPaymentDetail?.type === paymentOptions.PAY_NOW).toString(),
      });
    }
  }, [isPaypalSuccess, initiatePaypalPaymentMutationData?.initiatePaypalPayment?.status]);

  // Secure booking: record dashboard analytics when the secure-booking query param is present.
  useEffect(() => {
    if (isSecureBookingPage(routerQuery as secureBookingType, isSecureBookingFeatureEnabled)) {
      updateDashboardAnalytics({ secureBookingAction: true });
    }
  }, [routerQuery]);

  // Ancillaries analytics: fire whenever booking data or packages change.
  useEffect(() => {
    if (bkngData && packages) {
      updateAncillariesAnalytics(bkngData.bookingInformation, packages);
    }
  }, [bkngData, packages]);

  return { confAnalytics, setConfAnalytics };
}
