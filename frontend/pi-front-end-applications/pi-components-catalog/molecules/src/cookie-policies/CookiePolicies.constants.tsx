import { getNoOfDaysInYear } from '@whitbread-eos/utils';

export const CONSENT_COOKIE = 'consent_cookie';
export const ONE_YEAR_IN_MINUTES = 60 * 24 * getNoOfDaysInYear(new Date().getFullYear());
