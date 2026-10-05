'use client';

import {
  AmendConfirmationErrorLS,
  AMEND_REVERT_EXCEPTION_VALUE,
  BASKET_STATUS,
  BookingSpinnerConfig,
  CHECK_BASKET_STATUS,
  INITIAL_CONFIRM_AMEND_ERROR_KEY,
  INITIAL_CONFIRM_AMEND_ERROR_VALUE,
  PAYMENT_FAILURE_CODE_KEY,
  PAYMENT_FAILURE_CODE_INITIAL_VALUE,
  PAYMENT_FAILURE_DESCRIPTION_KEY,
  PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE,
} from '@whitbread-eos/api';
import { useTranslation } from 'next-i18next';
import { useCallback, useEffect, useRef, useState } from 'react';

import { useQueryRequest } from './use-request';
import { useSessionStorage } from './use-session-storage';

const INTERVAL_DELAY = 1;

export default function usePollBasketStatus(
  basketReference: string,
  spinnerConfig: BookingSpinnerConfig[],
  startPolling = true
) {
  const { t } = useTranslation(['common']);
  const [dynamicSpinnerLabel, setDynamicSpinnerLabel] = useState<string>(t('booking.loading'));
  const [pollingInProgress, setPollingInProgress] = useState<boolean>(startPolling);
  const [pollingTimedOut, setPollingTimedOut] = useState<boolean>(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const startTimeRef = useRef<number>(0);
  const errorCode = useRef<string>('');
  const [, setConfirmAmendErrorValue] = useSessionStorage<AmendConfirmationErrorLS>(
    INITIAL_CONFIRM_AMEND_ERROR_KEY,
    INITIAL_CONFIRM_AMEND_ERROR_VALUE
  );
  const [, setPaymentFailureCode] = useSessionStorage<string>(
    PAYMENT_FAILURE_CODE_KEY,
    PAYMENT_FAILURE_CODE_INITIAL_VALUE
  );

  const [, setPaymentFailureDescription] = useSessionStorage<string>(
    PAYMENT_FAILURE_DESCRIPTION_KEY,
    PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE
  );
  // Synchronous polling of basket status until it is COMPLETED or FAILED
  // Query is enabled on when pollingInProgress is truthy
  const { isLoading: isLoadingBasketData, data: basketData } = useQueryRequest(
    ['BasketStatus', basketReference],
    CHECK_BASKET_STATUS,
    {
      basketReference,
    },
    {
      enabled: pollingInProgress && !!basketReference,
      gcTime: 0,
      staleTime: 0,
      refetchInterval: INTERVAL_DELAY * 1000,
      refetchIntervalInBackground: true,
    }
  );

  const getDynamicSpinnerLabel = useCallback(() => {
    const currentTime = Math.round(Date.now() / 1000);
    const elapsedTime = currentTime - startTimeRef.current;

    if (spinnerConfig && spinnerConfig?.length === 3) {
      const [spinnerOneData, spinnerTwoData, spinnerThreeData] = spinnerConfig;

      if (elapsedTime <= +spinnerOneData.seconds) {
        setDynamicSpinnerLabel(spinnerOneData.text);
      } else if (elapsedTime <= +spinnerOneData.seconds + +spinnerTwoData.seconds) {
        setDynamicSpinnerLabel(spinnerTwoData.text);
      } else {
        setDynamicSpinnerLabel(spinnerThreeData.text);
      }
    }
  }, [spinnerConfig]);

  useEffect(() => {
    if (pollingInProgress) {
      if (!intervalRef.current) {
        startTimeRef.current = Math.round(Date.now() / 1000);
        setPollingTimedOut(false);
        getDynamicSpinnerLabel();
        intervalRef.current = setInterval(getDynamicSpinnerLabel, INTERVAL_DELAY * 1000);
      }
    } else {
      startTimeRef.current = 0;
      clearInterval(intervalRef.current ?? '');
      clearTimeout(timeoutRef.current ?? '');
      timeoutRef.current = null;
      intervalRef.current = null;
    }

    return () => {
      clearInterval(intervalRef.current ?? '');
    };
  }, [getDynamicSpinnerLabel, pollingInProgress]);

  useEffect(() => {
    if (!isLoadingBasketData && basketData) {
      setPaymentFailureCode(PAYMENT_FAILURE_CODE_INITIAL_VALUE);
      setPaymentFailureDescription(PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE);
      const { basketStatus = '', basketError = null } = basketData.basketStatus;

      if (basketStatus === BASKET_STATUS.PROCESSING || basketStatus === BASKET_STATUS.AMENDING) {
        if (!timeoutRef.current && spinnerConfig && spinnerConfig?.length === 3) {
          const [spinnerOneData, spinnerTwoData, spinnerThreeData] = spinnerConfig;
          const timeoutPeriod =
            +spinnerOneData.seconds + +spinnerTwoData.seconds + +spinnerThreeData.seconds;

          timeoutRef.current = setTimeout(() => {
            setPollingTimedOut(true);
            setPollingInProgress(false);
          }, timeoutPeriod * 1000);
        }
        if (basketStatus === BASKET_STATUS.AMENDING && basketError?.code) {
          setConfirmAmendErrorValue(basketError.code as AmendConfirmationErrorLS);
          errorCode.current = basketError.code ?? AMEND_REVERT_EXCEPTION_VALUE;
          setPollingInProgress(false);
        }
      }
      if (
        (basketStatus === BASKET_STATUS.FAILED || basketStatus === BASKET_STATUS.SECURE_FAILED) &&
        basketError?.code
      ) {
        setPaymentFailureCode(basketError.code);
        setPaymentFailureDescription(basketError.description);
        errorCode.current = basketError.code;
        setPollingInProgress(false);
      }
      if (
        basketStatus === BASKET_STATUS.COMPLETED ||
        basketStatus === BASKET_STATUS.FAILED ||
        basketStatus === BASKET_STATUS.AMENDED ||
        basketStatus === BASKET_STATUS.SECURE_FAILED ||
        basketStatus === BASKET_STATUS.AMEND_FAILED
      ) {
        setPollingInProgress(false);
      }
    }

    return () => {
      clearTimeout(timeoutRef.current ?? '');
    };
  }, [isLoadingBasketData, basketData]);

  return {
    pollingInProgress,
    basketStatus: basketData?.basketStatus?.basketStatus ?? '',
    retryPayment: basketData?.basketStatus?.retryPayment ?? null,
    dynamicSpinnerLabel,
    pollingTimedOut,
    errorCode: errorCode.current,
  };
}
