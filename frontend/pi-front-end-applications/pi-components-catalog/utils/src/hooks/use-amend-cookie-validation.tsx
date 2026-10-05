'use client';

import { AmendCookieValue } from '@whitbread-eos/api';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import { getFindBookingToken } from '../getters/findBookingToken';

interface UseAmendCookieValidationResult {
  isValid: boolean;
  token: string | null;
  basketReference: string | null;
  bookingReference: string | null;
  error: 'missing-token' | 'missing-basket-reference' | 'booking-reference-mismatch' | null;
}

const INITIAL_COOKIE_VALUE: AmendCookieValue = {
  token: null,
  basketReference: null,
  bookingReference: null,
};

/**
 * Custom hook to validate amend booking cookie data.
 * Extracts and validates token, basket reference, and booking reference from cookies.
 *
 * @returns {UseAmendCookieValidationResult} Validation result with cookie values and error state
 */
const useAmendCookieValidation = (): UseAmendCookieValidationResult => {
  const router = useRouter();
  const [cookieValue, setCookieValue] = useState<AmendCookieValue>(INITIAL_COOKIE_VALUE);

  const bookingRef = router.query.bookingReference;
  const ref = bookingRef ? String(bookingRef) : '';

  useEffect(() => {
    if (ref) {
      const { token, basketReference, bookingReference } = getFindBookingToken();
      setCookieValue({
        token: token ?? null,
        basketReference: basketReference ?? null,
        bookingReference: bookingReference ?? null,
      });
    }
  }, [ref]);

  const { token, basketReference, bookingReference } = cookieValue;

  // Validate cookie values.
  let error: UseAmendCookieValidationResult['error'] = null;
  let isValid = true;

  if (!token) {
    error = 'missing-token';
    isValid = false;
  } else if (!basketReference) {
    error = 'missing-basket-reference';
    isValid = false;
  } else if (ref !== bookingReference) {
    error = 'booking-reference-mismatch';
    isValid = false;
  }

  return {
    isValid,
    token,
    basketReference,
    bookingReference,
    error,
  };
};

export default useAmendCookieValidation;
