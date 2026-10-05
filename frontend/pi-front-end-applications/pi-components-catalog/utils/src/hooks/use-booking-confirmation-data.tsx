'use client';

import {
  Area,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
} from '@whitbread-eos/api';

import { useQueryRequest } from './use-request';
import { useAuthToken } from './useAuthToken';

export default function useBookingConfimationData(
  area: Area,
  basketReference: string | null,
  bookingReference: string,
  language: string,
  country: string
) {
  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();
  const {
    data: BCAuthData,
    error: BCAuthError,
    isError: BCAuthIsError,
    isLoading: BCAuthIsLoading,
    isSuccess: BCAuthIsSuccess,
    refetch: BCAuthRefetch,
  } = useQueryRequest(
    [
      'getBookingConfirmationAuthenticated',
      bookingReference,
      language,
      country,
      area?.toUpperCase(),
    ],
    GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
    {
      language,
      country,
      bookingReference: bookingReference,
      bookingChannel: area?.toUpperCase(),
    },
    { enabled: !isAuthTokenLoading && (!!authToken || area === Area.CCUI) },
    authToken
  );

  const {
    data: BCData,
    error: BCError,
    isError: BCIsError,
    isLoading: BCIsLoading,
    isSuccess: BCIsSuccess,
    refetch: BCRefetch,
  } = useQueryRequest(
    ['getBookingConfirmation', basketReference, language, country, area?.toUpperCase()],
    GET_DASHBOARD_BOOKING_CONFIRMATION,
    {
      language,
      country,
      basketReference: basketReference,
      bookingChannel: area?.toUpperCase(),
    },
    { enabled: !isAuthTokenLoading && !authToken && area === Area.PI }
  );
  const isTokenCookieOrCCUIChannelSet = authToken || area === Area.CCUI;
  return {
    bookingData: isTokenCookieOrCCUIChannelSet
      ? BCAuthData?.bookingConfirmationAuthenticated
      : BCData?.bookingConfirmation,
    bookingError: isTokenCookieOrCCUIChannelSet ? BCAuthError : BCError,
    bookingIsError: isTokenCookieOrCCUIChannelSet ? BCAuthIsError : BCIsError,
    bookingIsLoading:
      isAuthTokenLoading || (isTokenCookieOrCCUIChannelSet ? BCAuthIsLoading : BCIsLoading),
    bookingIsSuccess: isTokenCookieOrCCUIChannelSet ? BCAuthIsSuccess : BCIsSuccess,
    bookingRefetch: isTokenCookieOrCCUIChannelSet ? BCAuthRefetch : BCRefetch,
  };
}
