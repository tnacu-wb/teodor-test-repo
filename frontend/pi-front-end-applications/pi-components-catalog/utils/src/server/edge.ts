import { BUSINESS_BOOKER_USER_ROLES, Channel } from '@whitbread-eos/api';
import { nanoid } from 'nanoid';

import decodeIdToken from '../utils/decodeIdToken';

export const TOKEN_COMPANY_FIELD = 'https://premierinn.com/companyAccountId';
export const TOKEN_EMPLOYEE_FIELD = 'https://premierinn.com/employeeAccountId';
export const TOKEN_EMAIL_FIELD = 'https://premierinn.com/email';
export const TOKEN_CUSTOMER_FIELD = 'https://premierinn.com/customerAccountId';

export function getChannelByToken(token: string) {
  if (!token) {
    return null;
  }

  const { accessLevel, companyId, employeeId, profile, customerId } = getDetailsFromToken(token);
  if (accessLevel && companyId && employeeId && profile?.isBusiness) {
    return Channel.Bb;
  }

  if (customerId && !profile?.isBusiness && !accessLevel) {
    return Channel.Pi;
  }

  return null;
}

export const getDetailsFromToken = (token: string) => {
  const decodedToken = decodeIdToken(token);
  const companyId = (decodedToken as any)?.[TOKEN_COMPANY_FIELD];
  const employeeId = (decodedToken as any)?.[TOKEN_EMPLOYEE_FIELD];
  const customerId = (decodedToken as any)?.[TOKEN_CUSTOMER_FIELD];
  const email = (decodedToken as any)?.[TOKEN_EMAIL_FIELD];
  const accessLevel = (decodedToken as any)?.profile?.accessLevel;
  const profile = (decodedToken as any)?.profile || {};

  return {
    companyId,
    employeeId,
    customerId,
    email,
    accessLevel,
    profile,
    isTravelManager: accessLevel === BUSINESS_BOOKER_USER_ROLES.SUPER,
    isBooker: accessLevel === BUSINESS_BOOKER_USER_ROLES.BOOKER,
    isSelfBooker: accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF,
    isGuest: accessLevel === BUSINESS_BOOKER_USER_ROLES.STAYER,
    isBusinessPayManager: accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER,
    isBusinessPayUser: accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER,
  };
};

export const getRandomTracingId = () => {
  return nanoid();
};

export { DEFAULT_TRACING_COOKIE_NAME, ID_TOKEN_COOKIE } from '../global-constants';
