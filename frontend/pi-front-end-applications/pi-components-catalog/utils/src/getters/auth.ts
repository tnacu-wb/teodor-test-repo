import { ID_TOKEN_COOKIE } from '../global-constants';
import { getCookie } from '../helpers/cookies';
import decodeIdToken from '../utils/decodeIdToken';
import getLoggedInUserInfo from '../utils/getLoggedInUserInfo';
import { getSecureTwoURL } from './getters';

// both pi and secure2.pi
// set just one time from opera because they share the same domain
export const ACCESS_TOKEN_COOKIE = 'access_token';
export const EMAIL_COOKIE = 'email';
export const OPERA_COMPANY_ID = 'https://premierinn.com/operaCompanyId';
export const CDH_COMPANY_ID = 'https://premierinn.com/companyAccountId';
export const CDH_EMPLOYEE_ID = 'https://premierinn.com/employeeAccountId';
// sessionStorage under secure2.pi will be handled by secure2.pi
// it should have id_token, email and profile
// we don't need sessionStorage for Opera
export const MAX_AGE = 40 * 60 * 1000; // 40 min

export function getAuthCookie(): string {
  return getCookie(ID_TOKEN_COOKIE) || '';
}

export function getAccessTokenCookie(): string {
  return getCookie(ACCESS_TOKEN_COOKIE) || '';
}

export function logout() {
  const authIframe = document.getElementById('authIframe');
  // eslint-disable-next-line
  // @ts-ignore
  if (authIframe?.contentWindow) {
    const message = JSON.stringify({ action: 'logout' });
    // eslint-disable-next-line
    // @ts-ignore
    authIframe.contentWindow.postMessage(message, getSecureTwoURL());
  }
}

export { decodeIdToken, ID_TOKEN_COOKIE, getLoggedInUserInfo };
