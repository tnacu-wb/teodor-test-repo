import { BASKET_IDS_COOKIE } from '../global-constants';
import { encodeToBase64 } from './base64';
import {
  addBasketIdToCookie,
  getBasketIdsFromCookie,
  getBasketIdsJsonFromCookie,
  validateBasketIdInCookie,
  validateBasketIdFromServer,
} from './basketIdsCookie';
import * as cookies from './cookies';

jest.mock('./cookies');

const mockedGetCookie = cookies.getCookie as jest.MockedFunction<typeof cookies.getCookie>;
const mockedSetCookie = cookies.setCookie as jest.MockedFunction<typeof cookies.setCookie>;

describe('basketIdsCookie', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('addBasketIdToCookie', () => {
    it('should add a new basket ID to empty cookie', () => {
      mockedGetCookie.mockReturnValue(undefined);

      addBasketIdToCookie('BASKET123');

      expect(mockedSetCookie).toHaveBeenCalledWith(
        BASKET_IDS_COOKIE,
        encodeURIComponent(JSON.stringify([encodeToBase64('BASKET123')])),
        60 // 1 hour in minutes
      );
    });

    it('should add a new basket ID to existing cookie', () => {
      const existingIds = [encodeToBase64('BASKET123')];
      mockedGetCookie.mockReturnValue(encodeURIComponent(JSON.stringify(existingIds)));

      addBasketIdToCookie('BASKET456');

      expect(mockedSetCookie).toHaveBeenCalledWith(
        BASKET_IDS_COOKIE,
        encodeURIComponent(
          JSON.stringify([encodeToBase64('BASKET123'), encodeToBase64('BASKET456')])
        ),
        60
      );
    });

    it('should not add duplicate basket ID', () => {
      const existingIds = [encodeToBase64('BASKET123')];
      mockedGetCookie.mockReturnValue(encodeURIComponent(JSON.stringify(existingIds)));

      addBasketIdToCookie('BASKET123');

      expect(mockedSetCookie).toHaveBeenCalledWith(
        BASKET_IDS_COOKIE,
        encodeURIComponent(JSON.stringify([encodeToBase64('BASKET123')])),
        60
      );
    });

    it('should handle empty basket ID', () => {
      addBasketIdToCookie('');

      expect(mockedSetCookie).not.toHaveBeenCalled();
    });

    it('should handle invalid basket ID type', () => {
      addBasketIdToCookie(null as any);

      expect(mockedSetCookie).not.toHaveBeenCalled();
    });
  });

  describe('getBasketIdsFromCookie', () => {
    it('should return empty array when cookie does not exist', () => {
      mockedGetCookie.mockReturnValue(undefined);

      const result = getBasketIdsFromCookie();

      expect(result).toEqual([]);
    });

    it('should return decoded basket IDs from cookie', () => {
      const encodedIds = [encodeToBase64('BASKET123'), encodeToBase64('BASKET456')];
      mockedGetCookie.mockReturnValue(encodeURIComponent(JSON.stringify(encodedIds)));

      const result = getBasketIdsFromCookie();

      expect(result).toEqual(['BASKET123', 'BASKET456']);
    });

    it('should handle malformed JSON in cookie', () => {
      mockedGetCookie.mockReturnValue('invalid-json');

      const result = getBasketIdsFromCookie();

      expect(result).toEqual([]);
    });

    it('should handle non-array cookie value', () => {
      mockedGetCookie.mockReturnValue(JSON.stringify({ invalid: 'data' }));

      const result = getBasketIdsFromCookie();

      expect(result).toEqual([]);
    });

    it('should filter out empty decoded IDs', () => {
      const encodedIds = [encodeToBase64('BASKET123'), '', encodeToBase64('BASKET456')];
      mockedGetCookie.mockReturnValue(encodeURIComponent(JSON.stringify(encodedIds)));

      const result = getBasketIdsFromCookie();

      expect(result).toEqual(['BASKET123', 'BASKET456']);
    });
  });

  describe('validateBasketIdInCookie', () => {
    it('should return true when basket ID exists in cookie', () => {
      const encodedIds = [encodeToBase64('BASKET123'), encodeToBase64('BASKET456')];
      mockedGetCookie.mockReturnValue(encodeURIComponent(JSON.stringify(encodedIds)));

      const result = validateBasketIdInCookie('BASKET123');

      expect(result).toBe(true);
    });

    it('should return false when basket ID does not exist in cookie', () => {
      const encodedIds = [encodeToBase64('BASKET123')];
      mockedGetCookie.mockReturnValue(encodeURIComponent(JSON.stringify(encodedIds)));

      const result = validateBasketIdInCookie('BASKET999');

      expect(result).toBe(false);
    });

    it('should return false when cookie does not exist', () => {
      mockedGetCookie.mockReturnValue(undefined);

      const result = validateBasketIdInCookie('BASKET123');

      expect(result).toBe(false);
    });

    it('should return false for empty basket ID', () => {
      const result = validateBasketIdInCookie('');

      expect(result).toBe(false);
    });

    it('should return false for invalid basket ID type', () => {
      const result = validateBasketIdInCookie(null as any);

      expect(result).toBe(false);
    });
  });

  describe('validateBasketIdFromServer', () => {
    it('should return true when basket ID exists in server cookie', () => {
      const encodedIds = [encodeToBase64('BASKET123'), encodeToBase64('BASKET456')];
      const cookieValue = JSON.stringify(encodedIds);

      const result = validateBasketIdFromServer('BASKET123', cookieValue);

      expect(result).toBe(true);
    });

    it('should return false when basket ID does not exist in server cookie', () => {
      const encodedIds = [encodeToBase64('BASKET123')];
      const cookieValue = JSON.stringify(encodedIds);

      const result = validateBasketIdFromServer('BASKET999', cookieValue);

      expect(result).toBe(false);
    });

    it('should return false when cookie value is undefined', () => {
      const result = validateBasketIdFromServer('BASKET123', undefined);

      expect(result).toBe(false);
    });

    it('should return false when basket ID is empty', () => {
      const encodedIds = [encodeToBase64('BASKET123')];
      const cookieValue = JSON.stringify(encodedIds);

      const result = validateBasketIdFromServer('', cookieValue);

      expect(result).toBe(false);
    });

    it('should return false for malformed JSON in cookie value', () => {
      const result = validateBasketIdFromServer('BASKET123', 'invalid-json');

      expect(result).toBe(false);
    });

    it('should return false for non-array cookie value', () => {
      const cookieValue = JSON.stringify({ invalid: 'data' });

      const result = validateBasketIdFromServer('BASKET123', cookieValue);

      expect(result).toBe(false);
    });

    it('should filter out empty decoded IDs when validating', () => {
      const encodedIds = [encodeToBase64('BASKET123'), '', encodeToBase64('BASKET456')];
      const cookieValue = JSON.stringify(encodedIds);

      const result = validateBasketIdFromServer('BASKET123', cookieValue);

      expect(result).toBe(true);
    });
  });

  describe('getBasketIdsJsonFromCookie', () => {
    it('should return JSON string of encoded basket IDs from cookie', () => {
      const encodedIds = [encodeToBase64('BASKET123'), encodeToBase64('BASKET456')];
      const cookieValue = encodeURIComponent(JSON.stringify(encodedIds));
      mockedGetCookie.mockReturnValue(cookieValue);

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBe(JSON.stringify(encodedIds));
    });

    it('should return undefined if cookie is not found', () => {
      mockedGetCookie.mockReturnValue(undefined);

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBeUndefined();
    });

    it('should return undefined if cookie is null', () => {
      mockedGetCookie.mockReturnValue(null);

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBeUndefined();
    });

    it('should return undefined for invalid JSON in cookie', () => {
      mockedGetCookie.mockReturnValue('invalid-json');

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBeUndefined();
    });

    it('should return undefined if cookie value is not an array', () => {
      const cookieValue = encodeURIComponent(JSON.stringify({ notAnArray: 'value' }));
      mockedGetCookie.mockReturnValue(cookieValue);

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBeUndefined();
    });

    it('should handle URL-encoded cookie values correctly', () => {
      const encodedIds = [encodeToBase64('BASKET123')];
      const cookieValue = encodeURIComponent(JSON.stringify(encodedIds));
      mockedGetCookie.mockReturnValue(cookieValue);

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBe(JSON.stringify(encodedIds));
    });

    it('should return empty array JSON string for empty cookie array', () => {
      const cookieValue = encodeURIComponent(JSON.stringify([]));
      mockedGetCookie.mockReturnValue(cookieValue);

      const result = getBasketIdsJsonFromCookie();

      expect(result).toBe('[]');
    });
  });
});
