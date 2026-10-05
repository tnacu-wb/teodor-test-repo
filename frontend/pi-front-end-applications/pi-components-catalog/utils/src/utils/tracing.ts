import { nanoid } from 'nanoid';

import { getCookie } from '../helpers/cookies';

const DEFAULT_TRACING_COOKIE_NAME = 'WB-SESSION-ID';

export interface TracingObject {
  [DEFAULT_TRACING_COOKIE_NAME]: string;
}

export const tracingCookie = (cookies: any, name = DEFAULT_TRACING_COOKIE_NAME) => {
  const tracingCookieValue = cookies.get(name);
  if (!tracingCookieValue) {
    const tracingId = nanoid();
    cookies.set(name, tracingId, { httpOnly: false, maxAge: 10 * 60 * 60 * 100 });

    return tracingId;
  }

  return tracingCookieValue;
};

export const getDefaultSessionTracing = (cookies: any): TracingObject => {
  return { [DEFAULT_TRACING_COOKIE_NAME]: tracingCookie(cookies) };
};

export const getClientDefaultSessionTracing = (name = DEFAULT_TRACING_COOKIE_NAME) => {
  return { [DEFAULT_TRACING_COOKIE_NAME]: getCookie(name) };
};
