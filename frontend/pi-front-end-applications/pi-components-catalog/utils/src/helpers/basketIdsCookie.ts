import { BASKET_IDS_COOKIE, BASKET_COOKIE_EXPIRY_HOURS } from '../global-constants';
import { decodeFromBase64, encodeToBase64 } from './base64';
import { getCookie, setCookie } from './cookies';

/**
 * Validates if a basket ID is valid (non-empty string).
 *
 * @param basketId - The basket ID to validate
 * @returns true if valid, false otherwise
 */
const isValidBasketId = (basketId: string): boolean => {
  return Boolean(basketId && typeof basketId === 'string');
};

/**
 * Adds a basket ID to the basketIds cookie with 1-hour expiry.
 * The cookie stores an array of base64-encoded basket IDs for security.
 *
 * @param basketId - The basket reference ID to add
 */
export const addBasketIdToCookie = (basketId: string): void => {
  if (!isValidBasketId(basketId)) {
    return;
  }

  const existingIds = getBasketIdsFromCookie();

  // Only add if not already present
  if (!existingIds.includes(basketId)) {
    existingIds.push(basketId);
  }

  const encodedIds = existingIds.map(encodeToBase64).filter((id): id is string => Boolean(id));

  // Set cookie with 1-hour expiry
  setCookie(
    BASKET_IDS_COOKIE,
    encodeURIComponent(JSON.stringify(encodedIds)),
    BASKET_COOKIE_EXPIRY_HOURS * 60 // Convert hours to minutes
  );
};

/**
 * Retrieves all basket IDs from the basketIds cookie.
 * Decodes the base64-encoded IDs and returns them as an array.
 *
 * @returns Array of basket IDs, or empty array if cookie not found or invalid
 */
export const getBasketIdsFromCookie = (): string[] => {
  const cookie = getCookie(BASKET_IDS_COOKIE);

  if (!cookie || typeof cookie !== 'string') {
    return [];
  }

  try {
    const decodedCookie = decodeURIComponent(cookie);
    const encodedIds: string[] = JSON.parse(decodedCookie);

    if (!Array.isArray(encodedIds)) {
      return [];
    }

    return encodedIds.map(decodeFromBase64).filter((id): id is string => Boolean(id));
  } catch {
    return [];
  }
};

/**
 * Validates if a basket ID exists in the basketIds cookie.
 * Used for authorization checks on confirmation and registration pages.
 *
 * @param basketId - The basket reference ID to validate
 * @returns true if basket ID is found in cookie, false otherwise
 */
export const validateBasketIdInCookie = (basketId: string): boolean => {
  if (!isValidBasketId(basketId)) {
    return false;
  }

  return getBasketIdsFromCookie().includes(basketId);
};

/**
 * Server-side validation of basket ID from cookie string.
 * Used in getServerSideProps to validate basket ownership.
 *
 * @param basketId - The basket reference ID to validate
 * @param cookieValue - The raw cookie value from server-side request
 * @returns true if basket ID is found in cookie, false otherwise
 */
export const validateBasketIdFromServer = (
  basketId: string,
  cookieValue: string | undefined
): boolean => {
  if (!isValidBasketId(basketId) || !cookieValue) {
    return false;
  }

  try {
    const decodedCookieValue = decodeURIComponent(cookieValue);
    const encodedIds: string[] = JSON.parse(decodedCookieValue);

    if (!Array.isArray(encodedIds)) {
      return false;
    }

    return encodedIds
      .map(decodeFromBase64)
      .filter((id): id is string => Boolean(id))
      .includes(basketId);
  } catch {
    return false;
  }
};

/**
 * Retrieves the basketIds cookie and returns it as a JSON string for GraphQL authorization header.
 * Returns the encoded basket IDs array from the cookie as a JSON string.
 * Used in confirmation and register pages to pass basket IDs in the authorization header.
 *
 * @returns JSON string of encoded basket IDs array, or undefined if cookie not found or invalid
 */
export const getBasketIdsJsonFromCookie = (): string | undefined => {
  const cookieValue = getCookie(BASKET_IDS_COOKIE);

  if (!cookieValue) {
    return undefined;
  }

  try {
    const decodedCookie = decodeURIComponent(cookieValue);
    const encodedIds = JSON.parse(decodedCookie);
    return Array.isArray(encodedIds) ? JSON.stringify(encodedIds) : undefined;
  } catch {
    return undefined;
  }
};
