'use client';

import { APGP_PLANET_MESSAGE_AP, APGP_PLANET_MESSAGE_GP } from '@whitbread-eos/api';
import { useCallback, useEffect, useState } from 'react';

import analytics from '../services/analyticsService/analytics';

const isPaymentCompletionMessage = (event: MessageEvent) => {
  if (typeof event.data === 'string') {
    try {
      return Boolean(JSON.parse(event.data)?.paymentId);
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log(e);
    }
  }
  return false;
};

export const useIPageSubmission = () => {
  const [transactionState, setTransactionState] = useState({
    paymentId: undefined,
    isPaymentComplete: false,
    merchantReference: undefined,
    transactionId: undefined,
    authCode: undefined,
    cardType: undefined,
    tokenNo: undefined,
    paymentStatus: undefined,
  });

  const setIsPaymentComplete = (isPaymentComplete: boolean) =>
    setTransactionState({
      ...transactionState,
      isPaymentComplete,
    });

  const handler = useCallback((event: any) => {
    if (event.data === APGP_PLANET_MESSAGE_GP) {
      analytics.update({
        gp: true,
        ap: false,
      });
    } else if (event.data === APGP_PLANET_MESSAGE_AP) {
      analytics.update({
        gp: false,
        ap: true,
      });
    }
    if (isPaymentCompletionMessage(event)) {
      const data = JSON.parse(event.data);
      const {
        paymentId,
        cardType,
        merchantReference,
        transactionId,
        authCode,
        tokenNo,
        paymentStatus,
      } = data;
      setTransactionState({
        paymentId,
        isPaymentComplete: true,
        cardType,
        merchantReference,
        transactionId,
        authCode,
        tokenNo,
        paymentStatus,
      });
    }
  }, []);
  useEffect(() => {
    window.addEventListener('message', handler);
    return () => window.removeEventListener('message', handler);
  }, []);

  return { ...transactionState, setIsPaymentComplete, handler };
};
