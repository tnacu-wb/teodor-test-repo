import { type Claims, HotelBrand, CCUI_ROLES, UserRoles } from '@whitbread-eos/api';
import { parse } from 'date-fns';

export function isStringValid(item: string | null | undefined) {
  return item !== null && item !== undefined && item !== '';
}

export function isDateValid(day: number, month: number, year: number) {
  const date = parse(`${day} ${month} ${year}`, 'dd MM yyyy', new Date());
  return !isNaN(date.getTime());
}

export function isSameDate(firstDate: Date | null, secondDate: Date | null) {
  if (!firstDate || !secondDate) return false;

  return (
    firstDate.getDate() === secondDate.getDate() &&
    firstDate.getMonth() === secondDate.getMonth() &&
    firstDate.getFullYear() === secondDate.getFullYear()
  );
}

export function isNumber(val: string) {
  return !isNaN(Number(val));
}

export function isAlphabetic(val: string) {
  const value = val.trim();
  return /^[A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+(?:[ '-][A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+)*$/.test(
    value
  );
}

export function hasValidCharacters(val: string) {
  const value = val.trim();
  return /^[A-Za-z0-9À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+(?:[ '-][A-Za-z0-9À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+)*$/.test(
    value
  );
}

export function isJsonValid(str: string) {
  try {
    const obj = JSON.parse(str);
    return obj && typeof obj === 'object';
  } catch (e) {
    return false;
  }
}

/* eslint-disable @typescript-eslint/no-explicit-any */
export function ternaryCondition(
  condition: boolean | undefined,
  exprIfTrue: any,
  exprIfFalse: any
) {
  return condition ? exprIfTrue : exprIfFalse;
}

export function logicalAndOperator(...args: any[]) {
  return args?.reduce((acc, current) => acc && current, true);
}

export function logicalOrOperator(...args: any[]) {
  return args?.reduce((acc, current) => acc || current, false);
}

export function isEmailValid(email: string) {
  const regex = RegExp(
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  );
  const isValidLength = email.length <= 50;
  return regex.test(email) && isValidLength;
}

export function isUrl(url: string | undefined) {
  if (url) {
    try {
      return Boolean(new URL(url));
    } catch {
      return false;
    }
  }
  return false;
}

export function isNonEmptyString(str: string | null | undefined): boolean {
  return !!str && str.trim() !== '';
}

function getMidnightInTimeZone(arrivalDate: string, timeZone: string): Date {
  // take arrival date as local midnight in tz
  const fmt = new Intl.DateTimeFormat('en-GB', {
    timeZone,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });

  const parts = fmt.formatToParts(new Date(arrivalDate + 'T12:00:00Z')); // midday to avoid TZ drift
  const year = Number(parts.find((p) => p.type === 'year')?.value);
  const month = Number(parts.find((p) => p.type === 'month')?.value);
  const day = Number(parts.find((p) => p.type === 'day')?.value);

  return new Date(year, month - 1, day, 0, 0, 0); // JS Date = local time
}

export function validateArrivalDate(
  arrivalDate: string,
  hotelBrand: string,
  now: Date = new Date()
): boolean {
  const isGerman = hotelBrand === HotelBrand.PID;
  const timeZone = isGerman ? 'Europe/Berlin' : 'Europe/London';
  const arrival = new Date(arrivalDate + 'T12:00:00Z'); // midday UTC to avoid drift

  if (isNaN(arrival.getTime())) {
    return false;
  }

  const arrivalMidnightLocal = getMidnightInTimeZone(arrivalDate, timeZone);
  const cutoff = new Date(arrivalMidnightLocal.getTime() + (24 * 60 * 60 * 1000 - 1000));

  // Convert "now" into same tz parts
  const nowParts = new Intl.DateTimeFormat('en-GB', {
    timeZone,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).formatToParts(now);

  const year = Number(nowParts.find((p) => p.type === 'year')?.value);
  const month = Number(nowParts.find((p) => p.type === 'month')?.value);
  const day = Number(nowParts.find((p) => p.type === 'day')?.value);
  const hour = Number(nowParts.find((p) => p.type === 'hour')?.value);
  const minute = Number(nowParts.find((p) => p.type === 'minute')?.value);
  const second = Number(nowParts.find((p) => p.type === 'second')?.value);

  const localNow = new Date(year, month - 1, day, hour, minute, second);

  if (localNow < arrivalMidnightLocal) return true; // before arrival day
  if (localNow > cutoff) return false; // after cutoff
  return true; // same-day before cutoff
}

export function isPromoAdmin(user?: Claims | null): boolean {
  const roles = user?.[CCUI_ROLES];

  if (!Array.isArray(roles)) {
    return false;
  }

  return roles.includes(UserRoles.PROMO_ADMIN) || roles.includes(UserRoles.PROMO_ADMIN_PROD);
}
