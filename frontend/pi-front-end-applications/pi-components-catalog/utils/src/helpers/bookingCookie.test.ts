import { FIND_BOOKING_COOKIE_NAME_KEY } from '@whitbread-eos/api';

import { setBookingCookie } from './bookingCookie';
import { setCookie } from './cookies';

jest.mock('./cookies', () => ({
  ...jest.requireActual('./cookies'),
  setCookie: jest.fn(),
}));

const cookieName = 'testCookie';
const cookieValue = { key: 'value' };
const minutesTillExpiry = '30';

const mockGetItem = jest.fn();
const mockSetItem = jest.fn();
const mockRemoveItem = jest.fn();
Object.defineProperty(window, 'localStorage', {
  value: {
    getItem: (...args: string[]) => mockGetItem(...args),
    setItem: (...args: string[]) => mockSetItem(...args),
    removeItem: (...args: string[]) => mockRemoveItem(...args),
  },
});

describe('setBookingCookie', () => {
  beforeEach(() => {
    mockSetItem.mockClear();
    mockSetItem.mockClear();
  });

  it('should set localStorage correctly', () => {
    setBookingCookie(cookieName, cookieValue, minutesTillExpiry);
    expect(mockSetItem).toHaveBeenCalledWith(FIND_BOOKING_COOKIE_NAME_KEY, cookieName);
  });

  it('should set cookie correctly', () => {
    setBookingCookie(cookieName, cookieValue, minutesTillExpiry);
    expect(setCookie).toHaveBeenCalledWith(
      cookieName,
      window.btoa(JSON.stringify(cookieValue)),
      Number(minutesTillExpiry)
    );
  });
});
