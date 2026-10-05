'use client';

/* eslint-disable @typescript-eslint/no-explicit-any */
import { BASKET_STATUS, BookingSpinnerConfig } from '@whitbread-eos/api';
import { useEffect, useState } from 'react';

export default function useBookingSpinner(
  basketStatus: string,
  bkngData: any,
  refetchBasketDataFn: any,
  t: (id: string) => string
) {
  const [dynamicSpinnerLabel, setDynamicSpinnerLabel] = useState<string>(t('booking.loading'));
  const [timerAchieved, setTimerAchieved] = useState<boolean>(false);
  const [transactionComplete, setTransactionComplete] = useState<boolean>(false);

  useEffect(() => {
    let intervalId: any;

    if (basketStatus !== BASKET_STATUS.COMPLETED && basketStatus !== BASKET_STATUS.FAILED) {
      refetchBasketDataFn();
    } else {
      clearInterval(intervalId);
      return;
    }

    const bookingSpinnerConfig: BookingSpinnerConfig[] =
      bkngData?.bookingConfirmation?.bookingSpinnerConfig;

    if (
      bookingSpinnerConfig &&
      bookingSpinnerConfig?.length > 0 &&
      basketStatus !== BASKET_STATUS.COMPLETED
    ) {
      const [spinnerOneData, spinnerTwoData, spinnerThreeData] = bookingSpinnerConfig;
      const totalSpinningTime =
        +spinnerOneData.seconds + +spinnerTwoData.seconds + +spinnerThreeData.seconds;

      const startTime = new Date().getMilliseconds();
      const initialWaitTime: number = +spinnerOneData.seconds;
      let currentSecondTick = new Date(startTime).getSeconds();

      if (basketStatus === BASKET_STATUS.PROCESSING && initialWaitTime > 0) {
        intervalId = setInterval(() => {
          refetchBasketDataFn();
          currentSecondTick++;
          if (basketStatus === BASKET_STATUS.COMPLETED && currentSecondTick <= totalSpinningTime) {
            setTimerAchieved(false);
            setTransactionComplete(true);
            basketStatus = BASKET_STATUS.COMPLETED;
            clearInterval(intervalId);
          } else if (basketStatus === BASKET_STATUS.PROCESSING) {
            if (currentSecondTick <= +spinnerOneData.seconds) {
              setDynamicSpinnerLabel(spinnerOneData.text);
            } else if (currentSecondTick <= +spinnerTwoData.seconds) {
              setDynamicSpinnerLabel(spinnerTwoData.text);
            } else if (currentSecondTick <= +spinnerThreeData.seconds) {
              setDynamicSpinnerLabel(spinnerThreeData.text);
            } else if (
              currentSecondTick >=
              +spinnerOneData.seconds + +spinnerTwoData.seconds + +spinnerThreeData.seconds
            ) {
              setTimerAchieved(true);
              basketStatus = BASKET_STATUS.COMPLETED;
              clearInterval(intervalId);
            }
          }
        }, 1000);
      }
    }

    return () => {
      clearInterval(intervalId);
    };
  }, [!bkngData?.bookingConfirmation?.bookingSpinnerConfig, basketStatus]);

  return {
    isTimerAchieved: timerAchieved,
    isTransactionComplete: transactionComplete,
    dynamicSpinnerLabel,
  };
}
