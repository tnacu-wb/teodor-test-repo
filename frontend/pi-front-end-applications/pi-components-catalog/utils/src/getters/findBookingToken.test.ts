import { FIND_BOOKING_COOKIE_NAME_KEY } from '@whitbread-eos/api';

import { cleanupFindBookingToken, getFindBookingToken } from './findBookingToken';

let windowSpy;
const cookieName = 'find_booking_token';
const cookieValue = 'eyAidG9rZW4iOiAiYm9va2luZ190b2tlbl92YWx1ZSIgfQ=='; // { "token": "booking_token_value" } encoded in Base64

const mockGetCookie = jest.fn().mockImplementation(() => cookieValue);
const mockDeleteCookie = jest.fn();
jest.mock('../helpers/cookies', () => ({
  ...jest.requireActual('../helpers/cookies'),
  getCookie: (cookieName) => mockGetCookie(cookieName),
  deleteCookie: (cookieName) => mockDeleteCookie(cookieName),
}));

describe('findBookingToken getters', () => {
  beforeAll(() => {
    window.localStorage.setItem(FIND_BOOKING_COOKIE_NAME_KEY, cookieName);
  });

  beforeEach(() => {
    windowSpy = jest.spyOn(window, 'window', 'get');
  });

  afterEach(() => {
    windowSpy.mockRestore();
  });

  describe('getFindBookingToken Method', () => {
    it('should return empty object if window object is not defined', () => {
      windowSpy.mockImplementation(() => undefined);
      const bookingToken = getFindBookingToken();
      expect(bookingToken).toEqual({});
    });

    it('should call getItem from localStorage when retrieving the cookieName', () => {
      const mockLocalStorage = {
        localStorage: {
          getItem: jest.fn(),
        },
      };
      (windowSpy as jest.Mock).mockReturnValue(mockLocalStorage);
      getFindBookingToken();
      expect(window.localStorage.getItem).toHaveBeenCalledWith(FIND_BOOKING_COOKIE_NAME_KEY);
    });

    it('should return the correct token from the find booking cookie', () => {
      const bookingToken = getFindBookingToken();
      const expectedBookingToken = { basketReference: undefined, token: 'booking_token_value' };
      expect(bookingToken).toEqual(expectedBookingToken);
    });

    it('should return empty obj if there is no cookie set with the saved cookieName', () => {
      mockGetCookie.mockImplementation(() => null);
      const bookingToken = getFindBookingToken();
      expect(bookingToken).toEqual({});
    });

    it('should return undefined if the token key is missing from the decoded Base64 cookie', () => {
      const invalidCookieValue = 'eyAia2V5IjogInZhbHVlIiB9'; // { "key": "value" } encoded in Base64
      mockGetCookie.mockImplementation(() => invalidCookieValue);
      const bookingToken = getFindBookingToken();
      expect(bookingToken).toEqual({ basketReference: undefined, token: undefined });
    });

    it('should return empty obj if any error occurs while decoding the Base64 cookie value', () => {
      const invalidCookieValue = 'eyBrZXk6ICJ2YWx1ZSIgfQ=='; // { key: "value" } encoded in Base64 triggers JSON.parse error
      mockGetCookie.mockImplementation(() => invalidCookieValue);
      const bookingToken = getFindBookingToken();
      expect(bookingToken).toEqual({});
    });
  });

  describe('cleanupFindBookingToken Method', () => {
    it('should not delete cookie if window is undefined', () => {
      windowSpy.mockImplementation(() => undefined);
      cleanupFindBookingToken();
      expect(mockDeleteCookie).not.toHaveBeenCalled();
    });

    it('should call deleteCookie when method is called', () => {
      cleanupFindBookingToken();
      expect(mockDeleteCookie).toHaveBeenCalledWith(cookieName);
    });

    it('should call getItem from localStorage when retrieving the cookieName', () => {
      const mockLocalStorage = {
        localStorage: {
          getItem: jest.fn(),
          removeItem: jest.fn(),
        },
      };
      (windowSpy as jest.Mock).mockReturnValue(mockLocalStorage);
      cleanupFindBookingToken();
      expect(window.localStorage.getItem).toHaveBeenCalledWith(FIND_BOOKING_COOKIE_NAME_KEY);
    });

    it('should call removeItem method from localStorage', () => {
      const mockLocalStorage = {
        localStorage: {
          getItem: jest.fn(),
          removeItem: jest.fn(),
        },
      };
      (windowSpy as jest.Mock).mockReturnValue(mockLocalStorage);
      cleanupFindBookingToken();
      expect(window.localStorage.removeItem).toHaveBeenCalledWith(FIND_BOOKING_COOKIE_NAME_KEY);
    });
  });
});
