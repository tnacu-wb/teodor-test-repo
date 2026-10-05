/* eslint-disable @typescript-eslint/no-explicit-any */

export const getCookie = (cookieName: string): any => {
  let val = null;
  try {
    val = document?.cookie
      ?.split('; ')
      ?.reverse()
      ?.find?.((row) => row?.startsWith?.(cookieName))
      ?.split('=')[1];
  } catch (e) {
    return e;
  }
  return val;
};

export const setCookie = (
  name: string,
  value: any,
  time: number | undefined,
  path = '/',
  overrideDomain = 'www.',
  domain = ''
) => {
  let expires = '';
  if (time) {
    const d = new Date();
    // Sets to minutes
    d.setTime(d.getTime() + time * 60 * 1000);
    expires = `${'expires='}${d.toUTCString()}`;
  }

  const cookiePath = `path=${path}`;
  const cookieDomain = domain
    ? `domain=${domain}`
    : `${'domain='}${window.location.hostname.replace('www.', overrideDomain)}`;
  document.cookie = `${name}=${value};${expires};${cookiePath};${cookieDomain}`;
};

export const setCookieWithDefaultDomain = (name: string, value: any, time: number | undefined) => {
  const cookiePath = `path=/`;
  let expires = '';
  if (time) {
    const d = new Date();
    d.setTime(d.getTime() + time * 60 * 1000);
    expires = `${'expires='}${d.toUTCString()}`;
  }
  document.cookie = `${name}=${value};${expires};${cookiePath}`;
};

export const deleteCookie = (cookieName: string): any => {
  const expires = 'expires=Thu, 01 Jan 1970 00:00:01 GMT';
  const path = 'path=/';
  const domain = `${'domain='}${window.location.hostname}`;
  document.cookie = `${cookieName}=;${expires};${path};${domain}`;
};

export const deleteCookieWithDefaultDomain = (cookieName: string): any => {
  const expires = 'expires=Thu, 01 Jan 1970 00:00:01 GMT';
  const path = 'path=/';
  document.cookie = `${cookieName}=;${expires};${path};`;
};
