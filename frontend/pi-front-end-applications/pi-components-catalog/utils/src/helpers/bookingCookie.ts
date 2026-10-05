import { FIND_BOOKING_COOKIE_NAME_KEY } from '@whitbread-eos/api';

import { setCookie } from './cookies';

export const setBookingCookie = (
  cookieName: string,
  cookieValue: object,
  minutesTillExpiry: string
) => {
  if (typeof window !== 'undefined' && cookieName) {
    window.localStorage.setItem(FIND_BOOKING_COOKIE_NAME_KEY, cookieName);
    setCookie(cookieName, window.btoa(JSON.stringify(cookieValue)), Number(minutesTillExpiry));
  }
};
