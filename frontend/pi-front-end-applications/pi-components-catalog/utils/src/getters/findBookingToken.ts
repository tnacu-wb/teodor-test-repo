import { FIND_BOOKING_COOKIE_NAME_KEY } from '@whitbread-eos/api';

import { deleteCookie, getCookie } from '../helpers/cookies';
import { isStringValid } from '../validators';

export const getFindBookingToken = () => {
  if (typeof window !== 'undefined') {
    const cookieName = window.localStorage.getItem(FIND_BOOKING_COOKIE_NAME_KEY) ?? '';
    if (isStringValid(cookieName)) {
      const cookieTokenValue = getCookie(cookieName);
      if (cookieTokenValue) {
        try {
          const cookieValue = JSON.parse(window.atob(cookieTokenValue));
          return {
            token: cookieValue.token,
            basketReference: cookieValue.basketReference,
            bookingReference: cookieValue.bookingReference,
            operaConfNumber: cookieValue.operaConfNumber,
          };
        } catch (e) {
          return {};
        }
      }
    }
  }
  return {};
};

export const cleanupFindBookingToken = () => {
  if (typeof window !== 'undefined') {
    const cookieName = window.localStorage.getItem(FIND_BOOKING_COOKIE_NAME_KEY) ?? '';
    deleteCookie(cookieName);
    window.localStorage.removeItem(FIND_BOOKING_COOKIE_NAME_KEY);
  }
};
