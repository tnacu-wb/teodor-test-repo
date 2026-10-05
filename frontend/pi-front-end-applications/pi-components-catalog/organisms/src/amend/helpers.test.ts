import '@testing-library/jest-dom';
import { Channel } from '@whitbread-eos/api';

import {
  getChangedPreCheckedInReservations,
  valuesAreEqual,
  getRemovedReservations,
  getDateChangedReservationIds,
  getGuestListChanged,
  getRoomStayChanged,
  isStayDatesSectionUpdated,
  shouldAmendStayDates,
  showNotificationNoPromotion,
  shouldShowAmendStayDatesError,
  shouldShowAmendStayDatesErrorNoPromo,
  getPromoCondition,
  handleEditRemoveRoomReset,
  getIsPromoCodeLandingPageEnabled,
} from './helpers';

const tempDataRef = {
  bookingFlowId: 'booking-ct-a1',
  hotelId: 'STUAIR',
  hotelName: 'Stuttgart Airport Messe',
  infoMessages: ['<p>Free cancellation up to 6pm on the day of arrival</p>\n'],
  currencyCode: 'EUR',
  totalCost: 2997,
  previousTotal: 0,
  newTotal: 2997,
  channel: 'PI',
  companyId: null,
  reservationByIdList: [
    {
      reservationId: '1967601',
      originalReservationId: '1967601_0',
      preCheckInStatus: true,
      reservationGuestList: [
        {
          givenName: 'John',
          surName: 'Doe',
          nameTitle: 'Mr',
          email: 'doe@gmail.com',
          address: {
            addressLine1: 'ring road',
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            cityName: 'London',
            postalCode: 'LU5 5XE',
            countryCode: 'GB',
          },
        },
      ],
      billing: {
        address: {
          addressLine1: 'Porz Avenue',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          companyName: '',
          country: 'GB',
          postalCode: 'LU5 5XE',
        },
        email: 'manjunath.sc@gmail.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-07-31',
        departureDate: '2024-08-01',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'FMTHRE',
          roomName: 'Family room',
          groupId: 'family',
        },
        accessibleRoom: {
          phoneNumber: '+49 711 7224 9001',
          isAccessible: false,
        },
        roomPrice: 999,
      },
    },
    {
      reservationId: '1967602',
      originalReservationId: '1967602_0',
      preCheckInStatus: true,
      reservationGuestList: [
        {
          givenName: 'Gordon',
          surName: 'SS',
          nameTitle: 'Mr',
          email: 'pehigeg324@dovesilo.com',
          address: {
            addressLine1: null,
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            cityName: null,
            postalCode: null,
            countryCode: null,
          },
        },
      ],
      billing: {
        address: {
          addressLine1: 'Porz Avenue',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          companyName: '',
          country: 'GB',
          postalCode: 'LU5 5XE',
        },
        email: 'manjunath.sc@gmail.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-07-31',
        departureDate: '2024-08-01',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'FMTHRE',
          roomName: 'Family room',
          groupId: 'family',
        },
        accessibleRoom: {
          phoneNumber: '+49 711 7224 9001',
          isAccessible: false,
        },
        roomPrice: 999,
      },
    },
    {
      reservationId: '1967603',
      originalReservationId: '1967603_0',
      preCheckInStatus: true,
      reservationGuestList: [
        {
          givenName: 'Manjunath',
          surName: 'SS',
          nameTitle: 'Mr',
          email: 'manjunath.sc@gmail.com',
          address: {
            addressLine1: 'Porz Avenue',
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            cityName: 'London',
            postalCode: 'LU5 5XE',
            countryCode: 'GB',
          },
        },
      ],
      billing: {
        address: {
          addressLine1: 'Porz Avenue',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          companyName: '',
          country: 'GB',
          postalCode: 'LU5 5XE',
        },
        email: 'manjunath.sc@gmail.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-07-31',
        departureDate: '2024-08-01',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'FMTHRE',
          roomName: 'Family room',
          groupId: 'family',
        },
        accessibleRoom: {
          phoneNumber: '+49 711 7224 9001',
          isAccessible: false,
        },
        roomPrice: 999,
      },
    },
  ],
};

const tempData = {
  bookingFlowId: 'booking-ct-a1',
  hotelId: 'STUAIR',
  hotelName: 'Stuttgart Airport Messe',
  infoMessages: ['<p>Free cancellation up to 6pm on the day of arrival</p>\n'],
  currencyCode: 'EUR',
  totalCost: 2997,
  previousTotal: 0,
  newTotal: 2997,
  channel: 'PI',
  companyId: null,
  reservationByIdList: [
    {
      reservationId: '1967601',
      originalReservationId: '1967601_0',
      preCheckInStatus: true,
      reservationGuestList: [
        {
          givenName: 'John',
          surName: 'Doe',
          nameTitle: 'Mr',
          email: 'doe@gmail.com',
          address: {
            addressLine1: 'ring road',
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            cityName: 'London',
            postalCode: 'LU5 5XE',
            countryCode: 'GB',
          },
        },
      ],
      billing: {
        address: {
          addressLine1: 'Porz Avenue',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          companyName: '',
          country: 'GB',
          postalCode: 'LU5 5XE',
        },
        email: 'manjunath.sc@gmail.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-07-31',
        departureDate: '2024-08-01',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'FMTHRE',
          roomName: 'Family room',
          groupId: 'family',
        },
        accessibleRoom: {
          phoneNumber: '+49 711 7224 9001',
          isAccessible: false,
        },
        roomPrice: 999,
      },
    },
    {
      reservationId: '1967602',
      originalReservationId: '1967602_0',
      preCheckInStatus: true,
      reservationGuestList: [
        {
          givenName: 'Gordon',
          surName: 'SS',
          nameTitle: 'Mr',
          email: 'pehigeg324@dovesilo.com',
          address: {
            addressLine1: null,
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            cityName: null,
            postalCode: null,
            countryCode: null,
          },
        },
      ],
      billing: {
        address: {
          addressLine1: 'Porz Avenue',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          companyName: '',
          country: 'GB',
          postalCode: 'LU5 5XE',
        },
        email: 'manjunath.sc@gmail.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-07-31',
        departureDate: '2024-08-01',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'FMTHRE',
          roomName: 'Family room',
          groupId: 'family',
        },
        accessibleRoom: {
          phoneNumber: '+49 711 7224 9001',
          isAccessible: false,
        },
        roomPrice: 999,
      },
    },
    {
      reservationId: '1967603',
      originalReservationId: '1967603_0',
      preCheckInStatus: true,
      reservationGuestList: [
        {
          givenName: 'Manjunath',
          surName: 'SS',
          nameTitle: 'Mr',
          email: 'manjunath.sc@gmail.com',
          address: {
            addressLine1: 'Porz Avenue',
            addressLine2: null,
            addressLine3: null,
            addressLine4: null,
            cityName: 'London',
            postalCode: 'LU5 5XE',
            countryCode: 'GB',
          },
        },
      ],
      billing: {
        address: {
          addressLine1: 'Porz Avenue',
          addressLine2: '',
          addressLine3: '',
          addressLine4: '',
          companyName: '',
          country: 'GB',
          postalCode: 'LU5 5XE',
        },
        email: 'manjunath.sc@gmail.com',
      },
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2024-07-31',
        departureDate: '2024-08-01',
        ratePlanCode: 'FLEXRATE',
        roomExtraInfo: {
          roomType: 'FMTHRE',
          roomName: 'Family room',
          groupId: 'family',
        },
        accessibleRoom: {
          phoneNumber: '+49 711 7224 9001',
          isAccessible: false,
        },
        roomPrice: 999,
      },
    },
  ],
};

describe('getChangedPreCheckedInReservations', () => {
  test('returns reservation IDs if arrival date changed', () => {
    const modifiedTempData = {
      ...tempData,
      reservationByIdList: tempData.reservationByIdList.map((reservation) =>
        reservation.reservationId === '1966842'
          ? {
              ...reservation,
              roomStay: {
                ...reservation.roomStay,
                arrivalDate: '2024-08-01', // Changed arrival date
              },
            }
          : reservation
      ),
    };

    expect(
      getChangedPreCheckedInReservations(tempDataRef as any, modifiedTempData as any, false)
    ).toEqual([]);
  });
});

describe('valuesAreEqual', () => {
  test('should return true for identical values', () => {
    expect(valuesAreEqual('test', 'test')).toBe(true);
    expect(valuesAreEqual(null, null)).toBe(true);
    expect(valuesAreEqual('', '')).toBe(true);
  });

  test('should return true for empty string and null comparison', () => {
    expect(valuesAreEqual('', null)).toBe(true);
    expect(valuesAreEqual(null, '')).toBe(true);
  });

  test('should return false for different values', () => {
    expect(valuesAreEqual('test', 'different')).toBe(false);
    expect(valuesAreEqual(null, 'test')).toBe(false);
    expect(valuesAreEqual('', 'test')).toBe(false);
  });
});

describe('getRemovedReservations', () => {
  const tempData = {
    reservationByIdList: [{ reservationId: '1' }, { reservationId: '2' }],
  };

  const tempDataRef = {
    reservationByIdList: [
      { reservationId: '1', originalReservationId: 'orig1' },
      {
        reservationId: '3',
        originalReservationId: 'orig3',
        preCheckInStatus: true,
        deRegCardCompleted: true,
      },
    ],
  };

  test('should return removed reservation IDs', () => {
    expect(getRemovedReservations(tempData as any, tempDataRef as any, true)).toEqual(['orig3']);
  });

  test('should return empty array if no reservations are removed', () => {
    const tempDataRefNoRemoved = {
      reservationByIdList: [
        { reservationId: '1', originalReservationId: 'orig1' },
        { reservationId: '2', originalReservationId: 'orig2' },
      ],
    };
    expect(getRemovedReservations(tempData as any, tempDataRefNoRemoved as any, true)).toEqual([]);
  });
});

describe('getDateChangedReservationIds', () => {
  const tempData = {
    reservationByIdList: [
      { reservationId: '1', roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10' } },
    ],
  };

  const tempDataRef = {
    reservationByIdList: [
      { reservationId: '1', roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-15' } },
    ],
  };

  test('should return true if dates are changed', () => {
    expect(getDateChangedReservationIds(tempDataRef as any, tempData as any)).toBe(true);
  });

  test('should return false if dates are not changed', () => {
    const tempDataRefUnchanged = {
      reservationByIdList: [
        {
          reservationId: '1',
          roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10' },
        },
      ],
    };
    expect(getDateChangedReservationIds(tempDataRefUnchanged as any, tempData as any)).toBe(false);
  });

  test('should return false if dates are not changed', () => {
    const tempDataRefUnchanged = {
      reservationByIdList: [
        {
          reservationId: '1',
          roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10' },
        },
      ],
    };
    expect(getDateChangedReservationIds(tempDataRefUnchanged as any, tempData as any)).toBe(false);
  });
});

describe('getGuestListChanged', () => {
  const tempReservation = {
    reservationGuestList: [{ givenName: 'John', surName: 'Doe' }],
  };

  const tempReservationRef = {
    reservationGuestList: [{ givenName: 'John', surName: 'Smith' }],
  };

  test('should return true if guest list has changed', () => {
    expect(getGuestListChanged(tempReservation as any, tempReservationRef as any)).toBe(true);
  });

  test('should return false if guest list is unchanged', () => {
    const tempReservationRefUnchanged = {
      reservationGuestList: [{ givenName: 'John', surName: 'Doe' }],
    };
    expect(getGuestListChanged(tempReservation as any, tempReservationRefUnchanged as any)).toBe(
      false
    );
  });
});

describe('getRoomStayChanged', () => {
  const tempReservation = {
    roomStay: { adultsNumber: 2, childrenNumber: 1, roomExtraInfo: { roomType: 'Suite' } },
  };

  const tempReservationRef = {
    roomStay: { adultsNumber: 2, childrenNumber: 1, roomExtraInfo: { roomType: 'Deluxe' } },
  };

  test('should return false if room type details have changed', () => {
    expect(getRoomStayChanged(tempReservation as any, tempReservationRef as any)).toBe(false);
  });

  test('should return false if room stay details are unchanged', () => {
    const tempReservationRefUnchanged = {
      roomStay: { adultsNumber: 2, childrenNumber: 1, roomExtraInfo: { roomType: 'Suite' } },
    };
    expect(getRoomStayChanged(tempReservation as any, tempReservationRefUnchanged as any)).toBe(
      false
    );
  });
});

describe('getChangedPreCheckedInReservations', () => {
  const tempData = {
    reservationByIdList: [
      { reservationId: '1', roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10' } },
      { reservationId: '2', roomStay: { arrivalDate: '2024-07-15', departureDate: '2024-07-20' } },
    ],
  };

  const tempDataRef = {
    reservationByIdList: [
      {
        reservationId: '1',
        roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-12' },
        preCheckInStatus: true,
        deRegCardCompleted: true,
        originalReservationId: 'orig1',
      },
      {
        reservationId: '2',
        roomStay: { arrivalDate: '2024-07-15', departureDate: '2024-07-20' },
        preCheckInStatus: false,
        deRegCardCompleted: false,
        originalReservationId: 'orig2',
      },
      {
        reservationId: '3',
        roomStay: { arrivalDate: '2024-07-22', departureDate: '2024-07-25' },
        preCheckInStatus: true,
        deRegCardCompleted: false,
        originalReservationId: 'orig3',
      },
    ],
  };

  test('should return reservation IDs with changed dates or removed reservations', () => {
    expect(getChangedPreCheckedInReservations(tempDataRef as any, tempData as any, false)).toEqual([
      'orig1',
      'orig3',
    ]);
  });

  test('should return array if childrenNumber changed', () => {
    const tempDataRefData = {
      reservationByIdList: [
        {
          reservationId: '1',
          roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10', childrenNumber: 4 },
          preCheckInStatus: true,
          deRegCardCompleted: true,
          originalReservationId: 'orig1',
        },
        {
          reservationId: '2',
          roomStay: { arrivalDate: '2024-07-15', departureDate: '2024-07-20', childrenNumber: 2 },
          preCheckInStatus: false,
          deRegCardCompleted: false,
          originalReservationId: 'orig2',
        },
      ],
    };
    expect(
      getChangedPreCheckedInReservations(tempDataRefData as any, tempData as any, true)
    ).toEqual(['orig1']);
  });

  test('should rhandle if reservationIds does not match', () => {
    const tempDataRef = {
      reservationByIdList: [
        {
          reservationId: '1',
          roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10', childrenNumber: 4 },
          preCheckInStatus: true,
          deRegCardCompleted: true,
          originalReservationId: 'orig1',
        },
        {
          reservationId: '2',
          roomStay: { arrivalDate: '2024-07-15', departureDate: '2024-07-20', childrenNumber: 2 },
          preCheckInStatus: false,
          deRegCardCompleted: false,
          originalReservationId: 'orig2',
        },
      ],
    };
    const temporaryData = {
      reservationByIdList: [
        {
          reservationId: '3',
          roomStay: { arrivalDate: '2024-07-01', departureDate: '2024-07-10', childrenNumber: 4 },
          preCheckInStatus: true,
          deRegCardCompleted: true,
          originalReservationId: 'orig1',
        },
        {
          reservationId: '4',
          roomStay: { arrivalDate: '2024-07-15', departureDate: '2024-07-20', childrenNumber: 2 },
          preCheckInStatus: false,
          deRegCardCompleted: false,
          originalReservationId: 'orig2',
        },
      ],
    };
    expect(
      getChangedPreCheckedInReservations(tempDataRef as any, temporaryData as any, false)
    ).toEqual(['orig1']);
  });

  it('should return original reservation ID when a pre-checked-in reservation is changed and repurpose is disabled', () => {
    const tempDataRef = {
      reservationByIdList: [
        {
          reservationId: '1',
          originalReservationId: 'orig1',
          preCheckInStatus: true,
          reservationGuestList: [
            {
              givenName: 'John',
              surName: 'Doe',
              nameTitle: 'Mr',
              email: 'john@example.com',
              address: {
                addressLine1: '123 Street',
                addressLine2: null,
                addressLine3: null,
                cityName: 'London',
                postalCode: 'SW1A 1AA',
                countryCode: 'GB',
              },
            },
          ],
          roomStay: {
            adultsNumber: 2,
            childrenNumber: 0,
          },
        },
      ],
    };

    const tempData = {
      reservationByIdList: [
        {
          reservationId: '1',
          originalReservationId: 'orig1',
          preCheckInStatus: true,
          reservationGuestList: [
            {
              givenName: 'Jane',
              surName: 'Doe',
              nameTitle: 'Mr',
              email: 'john@example.com',
              address: {
                addressLine1: '123 Street',
                addressLine2: null,
                addressLine3: null,
                cityName: 'London',
                postalCode: 'SW1A 1AA',
                countryCode: 'GB',
              },
            },
          ],
          roomStay: {
            adultsNumber: 2,
            childrenNumber: 0,
          },
        },
      ],
    };

    expect(getChangedPreCheckedInReservations(tempDataRef as any, tempData as any, false)).toEqual([
      'orig1',
    ]);
  });
});

describe('isStayDatesSectionUpdated', () => {
  const originalArrival = '2024-07-15';
  const originalDeparture = '2024-07-20';
  const newArrival = '2024-07-16';
  const newDeparture = '2024-07-20';

  test('should return false if room type details have changed', () => {
    expect(
      isStayDatesSectionUpdated(originalArrival, originalDeparture, newArrival, newDeparture)
    ).toBe(true);
  });
});

describe('shouldAmendStayDates', () => {
  it('returns true when promo code is disabled, stay dates not loading, and amend is successful', () => {
    expect(shouldAmendStayDates(false, false, true)).toBe(true);
  });

  it('returns false when promo code is enabled', () => {
    expect(shouldAmendStayDates(true, false, true)).toBe(false);
  });

  it('returns false when stay dates are loading', () => {
    expect(shouldAmendStayDates(false, true, true)).toBe(false);
  });

  it('returns false when amendStayDates is not successful', () => {
    expect(shouldAmendStayDates(false, false, false)).toBe(false);
  });

  it('returns false when multiple conditions fail', () => {
    expect(shouldAmendStayDates(true, true, false)).toBe(false);
  });
});

describe('showNotificationNoPromotion', () => {
  it('returns true when promo notification is not shown, stay dates not loading, and amend is successful', () => {
    expect(showNotificationNoPromotion(false, false, true)).toBe(true);
  });

  it('returns false when promo notification is shown', () => {
    expect(showNotificationNoPromotion(true, false, true)).toBe(false);
  });

  it('returns false when stay dates are loading', () => {
    expect(showNotificationNoPromotion(false, true, true)).toBe(false);
  });

  it('returns false when amendStayDates is not successful', () => {
    expect(showNotificationNoPromotion(false, false, false)).toBe(false);
  });

  it('returns false when multiple conditions fail', () => {
    expect(showNotificationNoPromotion(true, true, false)).toBe(false);
  });
});

describe('shouldShowAmendStayDatesError', () => {
  it('returns true when within promo window, not loading, and amend is error', () => {
    expect(shouldShowAmendStayDatesError(true, false, true)).toBe(true);
  });

  it('returns false when not within promo window', () => {
    expect(shouldShowAmendStayDatesError(false, false, true)).toBe(false);
  });

  it('returns false when stay dates are loading', () => {
    expect(shouldShowAmendStayDatesError(true, true, true)).toBe(false);
  });

  it('returns false when amendStayDatesIsError is false', () => {
    expect(shouldShowAmendStayDatesError(true, false, false)).toBe(false);
  });

  it('returns false when multiple conditions fail', () => {
    expect(shouldShowAmendStayDatesError(false, true, false)).toBe(false);
  });

  it('returns false when promoStayData is undefined', () => {
    expect(shouldShowAmendStayDatesError(undefined, false, true)).toBe(false);
  });
});

describe('shouldShowAmendStayDatesErrorNoPromo', () => {
  it('returns true when promo code is disabled, not loading, and amend is error', () => {
    expect(shouldShowAmendStayDatesErrorNoPromo(false, null, false, true)).toBe(true);
  });

  it('returns false when stay dates are loading', () => {
    expect(shouldShowAmendStayDatesErrorNoPromo(false, null, true, true)).toBe(false);
  });

  it('returns false when amendStayDatesIsError is false', () => {
    expect(shouldShowAmendStayDatesErrorNoPromo(false, null, false, false)).toBe(false);
  });

  it('returns false when multiple conditions fail', () => {
    expect(shouldShowAmendStayDatesErrorNoPromo(true, null, true, false)).toBe(false);
  });

  // new test cases
  it('should return true when promo code feature is disabled, stay dates are not loading, and amend stay dates is error', () => {
    const result = shouldShowAmendStayDatesErrorNoPromo(false, 'PROMO123', false, true);
    expect(result).toBe(true);
  });

  it('should return true when promo code feature is enabled but no promotion code is provided, stay dates are not loading, and amend stay dates is error', () => {
    const result = shouldShowAmendStayDatesErrorNoPromo(true, null, false, true);
    expect(result).toBe(true);
  });

  it('should return false when promo code feature is enabled and promotion code is present', () => {
    const result = shouldShowAmendStayDatesErrorNoPromo(true, 'PROMO123', false, true);
    expect(result).toBe(false);
  });

  it('should return false when stay dates are loading, even if other conditions are true', () => {
    const result = shouldShowAmendStayDatesErrorNoPromo(false, null, true, true);
    expect(result).toBe(false);
  });

  it('should return false when amendStayDatesIsError is false', () => {
    const result = shouldShowAmendStayDatesErrorNoPromo(false, null, false, false);
    expect(result).toBe(false);
  });

  it('should handle undefined promotionCode as no promo and return true if other conditions are met', () => {
    const result = shouldShowAmendStayDatesErrorNoPromo(true, undefined, false, true);
    expect(result).toBe(true);
  });
});
describe('getPromoCondition', () => {
  const baseProps = {
    isPromoCodeLandingPageEnabled: true,
    amendEditRoomIsSuccess: false,
    removeRoomIsSuccess: false,
    addNewRoomIsSuccess: false,
    mealsSectionHasUpdates: true,
    promoStayData: {
      showPromo: true,
      isWithinPromoWindow: true,
      promotionCode: 'PROMO123',
      landingPage: 'homepage',
      promoBannerColour: 'red',
      promoBannerIcon: 'icon.png',
      promoBannerTitle: 'Title',
      promoBannerSubtitle: 'Subtitle',
      promoInvalidMessage: 'Invalid',
      promoExpiredMessage: 'Expired',
      promoAmendMessage: 'Amend',
      promoBookingInfo: {
        ratePlanCode: 'FLEXRATE',
        promotionCode: 'PROMO123',
      },
    },
  };

  it('should return true when promo landing page is disabled', () => {
    const result = getPromoCondition({ ...baseProps, isPromoCodeLandingPageEnabled: false });
    expect(result).toBe(true);
  });

  it('should return true if amendEditRoomIsSuccess is true', () => {
    const result = getPromoCondition({ ...baseProps, amendEditRoomIsSuccess: true });
    expect(result).toBe(true);
  });

  it('should return true if removeRoomIsSuccess is true', () => {
    const result = getPromoCondition({ ...baseProps, removeRoomIsSuccess: true });
    expect(result).toBe(true);
  });

  it('should return true if addNewRoomIsSuccess is true', () => {
    const result = getPromoCondition({ ...baseProps, addNewRoomIsSuccess: true });
    expect(result).toBe(true);
  });

  it('should return promoStayData.isWithinPromoWindow when no meals section updates', () => {
    const resultTrue = getPromoCondition({
      ...baseProps,
      mealsSectionHasUpdates: false,
      promoStayData: {
        isWithinPromoWindow: true,
        showPromo: null,
        promotionCode: null,
        landingPage: null,
        promoBannerColour: null,
        promoBannerIcon: null,
        promoBannerTitle: null,
        promoBannerSubtitle: null,
        promoInvalidMessage: null,
        promoExpiredMessage: null,
        promoAmendMessage: null,
        promoBookingInfo: {
          ratePlanCode: null,
          promotionCode: null,
        },
      },
    });
    const resultFalse = getPromoCondition({
      ...baseProps,
      mealsSectionHasUpdates: false,
      promoStayData: {
        isWithinPromoWindow: false,
        showPromo: null,
        promotionCode: null,
        landingPage: null,
        promoBannerColour: null,
        promoBannerIcon: null,
        promoBannerTitle: null,
        promoBannerSubtitle: null,
        promoInvalidMessage: null,
        promoExpiredMessage: null,
        promoAmendMessage: null,
        promoBookingInfo: {
          ratePlanCode: null,
          promotionCode: null,
        },
      },
    });
    expect(resultTrue).toBe(true);
    expect(resultFalse).toBe(false);
  });

  it('should return true if mealsSectionHasUpdates is true and no room success flags', () => {
    const result = getPromoCondition({
      ...baseProps,
      mealsSectionHasUpdates: true,
      amendEditRoomIsSuccess: false,
      removeRoomIsSuccess: false,
      addNewRoomIsSuccess: false,
    });
    expect(result).toBe(true);
  });

  it('should return true if promoStayData is undefined and mealsSectionHasUpdates is false', () => {
    const result = getPromoCondition({
      ...baseProps,
      mealsSectionHasUpdates: false,
      promoStayData: null,
    });
    expect(result).toBe(true);
  });
});

describe('handleEditRemoveRoomReset', () => {
  let amendEditRoomMutation: { reset: jest.Mock };
  let removeRoomMutation: { reset: jest.Mock };

  beforeEach(() => {
    amendEditRoomMutation = { reset: jest.fn() };
    removeRoomMutation = { reset: jest.fn() };
  });

  it('should call reset on both mutations when promo code landing page is disabled', () => {
    handleEditRemoveRoomReset({
      isPromoCodeLandingPageEnabled: false,
      amendEditRoomMutation,
      removeRoomMutation,
    });

    expect(amendEditRoomMutation.reset).toHaveBeenCalledTimes(1);
    expect(removeRoomMutation.reset).toHaveBeenCalledTimes(1);
  });

  it('should NOT call reset when promo code landing page is enabled', () => {
    handleEditRemoveRoomReset({
      isPromoCodeLandingPageEnabled: true,
      amendEditRoomMutation,
      removeRoomMutation,
    });

    expect(amendEditRoomMutation.reset).not.toHaveBeenCalled();
    expect(removeRoomMutation.reset).not.toHaveBeenCalled();
  });
});

describe('getIsPromoCodeLandingPageEnabled test cases', () => {
  it('should return hasPIPromoCodeLandingPageEnabled when channel is Channel.Pi', () => {
    expect(getIsPromoCodeLandingPageEnabled(Channel.Pi, true, false, false)).toBe(true);

    expect(getIsPromoCodeLandingPageEnabled(Channel.Pi, false, true, false)).toBe(false);

    expect(getIsPromoCodeLandingPageEnabled(Channel.Pi, false, true, true)).toBe(false);
  });

  it('should return hasCCUIPromoCodeLandingPageEnabled when channel is Channel.Ccui', () => {
    expect(getIsPromoCodeLandingPageEnabled(Channel.Ccui, false, true, false)).toBe(true);

    expect(getIsPromoCodeLandingPageEnabled(Channel.Ccui, true, false, false)).toBe(false);
    expect(getIsPromoCodeLandingPageEnabled(Channel.Ccui, true, false, true)).toBe(false);
  });

  it('should return hasBbPromoCodeLandingPageEnabled when channel is Channel.Bb', () => {
    expect(getIsPromoCodeLandingPageEnabled(Channel.Bb, false, false, true)).toBe(true);

    expect(getIsPromoCodeLandingPageEnabled(Channel.Bb, true, true, false)).toBe(false);
  });

  it('should return false for unsupported channel values', () => {
    expect(getIsPromoCodeLandingPageEnabled('unknown' as Channel, true, true, false)).toBe(false);
  });

  it('should return false when channel is undefined or null', () => {
    expect(
      getIsPromoCodeLandingPageEnabled(undefined as unknown as Channel, true, true, false)
    ).toBe(false);

    expect(getIsPromoCodeLandingPageEnabled(null as unknown as Channel, true, true, false)).toBe(
      false
    );
  });

  it('should return hasBBPromoCodeLandingPageEnabled when channel is Channel.Bb', () => {
    expect(getIsPromoCodeLandingPageEnabled(Channel.Bb, false, false, true)).toBe(true);

    expect(getIsPromoCodeLandingPageEnabled(Channel.Bb, false, false, true)).toBe(true);
  });
});
