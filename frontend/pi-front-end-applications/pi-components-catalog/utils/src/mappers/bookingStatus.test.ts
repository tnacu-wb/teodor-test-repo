import { BC_RESERVATION_STATUS, BOOKING_TYPE } from '@whitbread-eos/api';
import { add, format } from 'date-fns';

import { mappingBookingStatus } from './bookingStatus';

const currentDatePlus1YearFormated = format(add(new Date(), { years: 1 }), 'yyyy-MM-dd');

describe('mappingBookingStatus function', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should return BOOKING_TYPE.UPCOMING for valid upcoming statuses', () => {
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.PREPAID, currentDatePlus1YearFormated)).toBe(
      BOOKING_TYPE.UPCOMING
    );
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.ARRIVED, currentDatePlus1YearFormated)).toBe(
      BOOKING_TYPE.UPCOMING
    );
    expect(
      mappingBookingStatus(BC_RESERVATION_STATUS.CHECKEDIN, currentDatePlus1YearFormated)
    ).toBe(BOOKING_TYPE.UPCOMING);
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.FUTURE, currentDatePlus1YearFormated)).toBe(
      BOOKING_TYPE.UPCOMING
    );
    expect(
      mappingBookingStatus(BC_RESERVATION_STATUS.UNARRIVED, currentDatePlus1YearFormated)
    ).toBe(BOOKING_TYPE.UPCOMING);
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.RESERVED, currentDatePlus1YearFormated)).toBe(
      BOOKING_TYPE.UPCOMING
    );
  });

  it('should return BOOKING_TYPE.PAST for valid past statuses', () => {
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.NOSHOW, '2023-09-18')).toBe(
      BOOKING_TYPE.PAST
    );
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.RELEASED, '2023-09-18')).toBe(
      BOOKING_TYPE.PAST
    );
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.PAST, '2023-09-18')).toBe(BOOKING_TYPE.PAST);
  });

  it('should return BOOKING_TYPE.CANCELLED for "CANCELLED"', () => {
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.CANCELLED, '2023-09-20')).toBe(
      BOOKING_TYPE.CANCELLED
    );
  });

  it('should return null for invalid statuses', () => {
    expect(mappingBookingStatus('INVALID_STATUS', '2023-09-20')).toBe('');
  });

  it('should handle different date comparisons', () => {
    // Test with a departureDate in the past
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.PREPAID, '202-09-18')).toBe(
      BOOKING_TYPE.PAST
    );
  });
  it('should handle different date comparisons', () => {
    // Test with a departureDate in the future
    expect(mappingBookingStatus(BC_RESERVATION_STATUS.PREPAID, currentDatePlus1YearFormated)).toBe(
      BOOKING_TYPE.UPCOMING
    );
  });
});
