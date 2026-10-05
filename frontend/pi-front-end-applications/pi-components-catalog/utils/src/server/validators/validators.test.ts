import { isHotelOpeningSoon, validateRoomOccupancyConditions, isPIBACardType } from '..';
import { BUSINESS_BOOKER_USER_ROLES, ROOM_CODES, URLParams } from '@whitbread-eos/api';
import { add, addDays } from 'date-fns';
import { cookies } from 'next/headers';

import { isInnBusinessApp, isMoreThan364DaysInFuture, validateRoomOccupancy } from './validators';

const mockFetchResponse = {
  data: {},
};

const mockOkStatus = { value: true };

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
  })
);

const mockUseRouter = jest.fn();
jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => mockUseRouter(),
}));

const mockSuperRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    accessLevel: mockSuperRole,
    tethered: false,
  },
};
const mockCookieData = {
  value: mockToken,
};
const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;
const mockHeadersWbUrl: string | null = 'http://test.com?a=1';

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({ get: () => mockHeadersWbUrl }),
}));

jest.mock('nanoid', () => ({
  nanoid: () => 'id',
}));

jest.mock('../../utils/decodeIdToken', () => (token: string) => token);

jest.mock('../../utils/unleash', () => ({
  getUnleashTogglesServerOrClient: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));
describe('hotelOpeningSoon function', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return false if the hotel opening date is empty', () => {
    const hotelOpeningDate = '';
    const selectedDate = new Date();
    expect(isHotelOpeningSoon(hotelOpeningDate, selectedDate)).toBe(false);
  });
  it('should return true if hotel is opening soon', () => {
    const selectedDate = new Date();
    const hotelOpeningDate = add(selectedDate, { days: 1 }).toISOString();
    expect(isHotelOpeningSoon(hotelOpeningDate, selectedDate)).toBe(true);
  });
});

describe('validateRoomOccupancyConditions', () => {
  it('should return false for valid inputs', () => {
    expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, '0')).toBe(false);
    expect(validateRoomOccupancyConditions('2', '1', ROOM_CODES.family, '0')).toBe(false);
    expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.twin, '0')).toBe(false);
    expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.accessible, '0')).toBe(false);
  });

  it('should return true for invalid adult count', () => {
    expect(validateRoomOccupancyConditions('0', '0', ROOM_CODES.double, '0')).toBe(true);
    expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.single, '0')).toBe(true);
    expect(validateRoomOccupancyConditions('3', '2', ROOM_CODES.family, '0')).toBe(true);
    expect(validateRoomOccupancyConditions('3', '0', ROOM_CODES.twin, '0')).toBe(true);
    expect(validateRoomOccupancyConditions('3', '0', ROOM_CODES.accessible, '0')).toBe(true);
  });
});

describe('isMoreThan364DaysInFuture', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const today = new Date();
  it('should return false for a date exactly 364 days in future', () => {
    const dateInFuture = addDays(today, 364);
    const result = isMoreThan364DaysInFuture(dateInFuture, today);

    expect(result).toBe(false);
  });
  it('should return true for a date more than 364 days in future', () => {
    const dateInFuture = addDays(today, 365);
    const result = isMoreThan364DaysInFuture(dateInFuture, today);

    expect(result).toBe(true);
  });
});

describe('validateRoomOccupancy', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return true if adults parameter is missing and isNaN', () => {
    const roomIndex = 1;
    const searchParams = {
      [`${URLParams.child}${roomIndex}`]: '2',
      [`${URLParams.cot}${roomIndex}`]: '1',
      [`${URLParams.roomType}${roomIndex}`]: 'DB',
    };

    const result = validateRoomOccupancy(roomIndex, searchParams);
    expect(result).toBe(true);
  });
  it('should return false if all parameters are valid', () => {
    const roomIndex = 1;
    const searchParams = {
      [`${URLParams.adult}${roomIndex}`]: '2',
      [`${URLParams.child}${roomIndex}`]: '1',
      [`${URLParams.cot}${roomIndex}`]: '1',
      [`${URLParams.roomType}${roomIndex}`]: 'FAM',
    };

    const result = validateRoomOccupancy(roomIndex, searchParams);
    expect(result).toBe(false);
  });
});

describe('isInnBusinessApp', () => {
  it('should return true if hostname is innbusiness', () => {
    const result = isInnBusinessApp('innbusiness');

    expect(result).toBe(true);
  });
  it('should return true if NODE_ENV is development', () => {
    process.env.NODE_ENV = 'development';
    const result = isInnBusinessApp('innbusiness');

    expect(result).toBe(true);
  });
});

describe('is piba card type', () => {
  it('should return true if card type is piba', () => {
    const result = isPIBACardType('PI');

    expect(result).toBe(true);
  });
  it('should return FALSE if card type is NOT piba', () => {
    const result = isPIBACardType('test');

    expect(result).toBe(false);
  });
  it('should return FALSE if card type is undefined', () => {
    const result = isPIBACardType();

    expect(result).toBe(false);
  });
});
