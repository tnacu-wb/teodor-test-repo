import {
  BC_RESERVATION_STATUS,
  FIND_BOOKING_SOURCE_PMS,
  GET_PRECHECKIN_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  HotelBrand,
} from '@whitbread-eos/api';
import {
  graphQLRequest,
  getFindBookingToken,
  setCookie,
  isStringValid,
  formatDate,
} from '@whitbread-eos/utils';

import {
  regFormInit,
  dependents,
  DATE_FORMAT,
  formatBookingData,
  replaceNullsWithEmptyStrings,
  formatAdditionalInformation,
  formatStayingGuests,
  isValidBookingStatus,
  formatRegistration,
  fetchBookingConfirmation,
  getHotelDetails,
  formatHotelAddress,
  handleBooking,
  checkPreCheckInStatusForSingleRoom,
  setBookingCookie,
  handleFindBookingError,
  handleSuccessfulFindBooking,
  goToHomePage,
  scrollToElement,
  errorStatusObject,
  handleSave,
  generateFileAttachment,
  pageStatus,
} from './common';

jest.mock('@whitbread-eos/utils', () => ({
  graphQLRequest: jest.fn(),
  getFindBookingToken: jest.fn(),
  setCookie: jest.fn(),
  isStringValid: jest.fn(),
  formatDate: jest.fn((date: string, format: string) => {
    if (format === 'yyyy-MM-dd') {
      return '2025-01-15';
    }
    return date;
  }),
}));

jest.mock('@whitbread-eos/api', () => ({
  BC_RESERVATION_STATUS: {
    COMPLETED: 'COMPLETED',
  },
  FIND_BOOKING_SOURCE_PMS: {
    OPERA: 'OPERA',
  },
  FIND_BOOKING_COOKIE_NAME_KEY: 'FIND_BOOKING_COOKIE_NAME',
  GET_PRECHECKIN_BOOKING_INFORMATION: 'GET_PRECHECKIN_BOOKING_INFORMATION',
  GET_HOTEL_INFORMATION: 'GET_HOTEL_INFORMATION',
  HotelBrand: {
    PID: 'PID',
  },
}));

describe('common.ts', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    (getFindBookingToken as jest.Mock).mockReturnValue({
      bookingReference: 'BOOK123',
      basketReference: 'BASKET123',
    });

    (isStringValid as jest.Mock).mockReturnValue(true);

    Object.defineProperty(window, 'btoa', {
      writable: true,
      value: jest.fn((value: string) => `encoded-${value}`),
    });

    Object.defineProperty(window, 'location', {
      writable: true,
      value: {
        href: '',
      },
    });
  });

  describe('constants', () => {
    it('exports expected constants', () => {
      expect(DATE_FORMAT).toBe('dd MMMM yyyy');

      expect(dependents).toEqual([
        { id: '0', label: 0 },
        { id: '1', label: 1 },
        { id: '2', label: 2 },
        { id: '3', label: 3 },
      ]);

      expect(regFormInit).toEqual(
        expect.objectContaining({
          bookingNumber: '',
          firstName: '',
          lastName: '',
          country: '',
          dependent: '0',
          dependents: [],
        })
      );

      expect(pageStatus.EXPIRED).toBe('EXPIRED');
    });
  });

  describe('replaceNullsWithEmptyStrings', () => {
    it('replaces null and undefined values', () => {
      const input: any = {
        a: null,
        b: undefined,
        c: 'hello',
        d: 'null-value',
        e: 123,
        f: false,
      };

      expect(replaceNullsWithEmptyStrings(input)).toEqual({
        a: '',
        b: '',
        c: 'hello',
        d: '',
        e: 123,
        f: false,
      });
    });

    it('handles arrays and nested objects', () => {
      const input: any = {
        list: [
          null,
          undefined,
          'hello',
          {
            nested: null,
            value: 'test',
          },
        ],
      };

      expect(replaceNullsWithEmptyStrings(input)).toEqual({
        list: [
          '',
          '',
          'hello',
          {
            nested: '',
            value: 'test',
          },
        ],
      });
    });

    it('handles primitive values inside arrays', () => {
      const input: any = {
        list: ['one', null, undefined, 1, false],
      };

      expect(replaceNullsWithEmptyStrings(input)).toEqual({
        list: ['one', '', '', 1, false],
      });
    });
  });

  describe('formatBookingData', () => {
    const createRoom = (overrides: any = {}) => ({
      reservationId: 'RES1',
      reservationStatus: 'COMPLETED',
      reservationGuestList: [
        {
          givenName: 'John',
          surName: 'Smith',
          email: 'john@test.com',
          nameTitle: 'Mr',
          additionalDetails: {
            dob: '1990-01-01',
            passportNumber: 'P123',
            nationality: 'GB',
          },
          homeAddress: {
            addressType: 'HOME',
            addressLine1: '1 Street',
            addressLine2: 'Line 2',
            addressLine3: 'Line 3',
            addressLine4: 'London',
            cityName: 'London',
            countryCode: 'GB',
            postalCode: 'SW1A',
            addressId: 'ADDR1',
          },
          address: {
            addressType: 'HOME',
            addressLine1: '1 Street',
            cityName: 'London',
            countryCode: 'GB',
            postalCode: 'SW1A',
          },
          profileId: 'PROFILE1',
        },
      ],
      roomStay: {
        arrivalDate: '2025-01-15',
        departureDate: '2025-01-20',
        childrenNumber: 0,
        adultsNumber: 1,
        roomType: 'DOUBLE',
        roomExtraInfo: {
          roomName: 'Double Room',
        },
      },
      ...overrides,
    });

    it('formats a single room', () => {
      const booking: any = {
        bookingReference: 'BOOK123',
        hotelId: 'HOTEL1',
        hotelName: 'Test Hotel',
        reservationByIdList: [createRoom()],
      };

      const result = formatBookingData(booking);

      expect(result).toHaveLength(1);

      expect(result[0]).toEqual(
        expect.objectContaining({
          bookingReference: 'BOOK123',
          hotelId: 'HOTEL1',
          hotelName: 'Test Hotel',
          reservationId: 'RES1',
          firstName: 'John',
          lastName: 'Smith',
          roomNo: 0,
          noOfRooms: 1,
          roomName: 'Double Room',
          country: 'GB',
          city: 'London',
          address: '1 Street',
          postalCode: 'SW1A',
          passport: 'P123',
        })
      );

      expect(result[0].arrivalDate).toEqual(new Date('2025-01-15'));
      expect(result[0].dateOfBirth).toEqual(new Date('1990-01-01'));
    });

    it('uses addressLine4 as city when available', () => {
      const booking: any = {
        bookingReference: 'BOOK',
        hotelId: 'HOTEL',
        hotelName: 'Hotel',
        reservationByIdList: [
          createRoom({
            reservationGuestList: [
              {
                ...createRoom().reservationGuestList[0],
                homeAddress: {
                  addressLine1: 'Address',
                  addressLine4: 'Address Line 4',
                  cityName: 'City',
                  countryCode: 'GB',
                  postalCode: 'POST',
                  addressId: 'ID',
                },
              },
            ],
          }),
        ],
      };

      const result = formatBookingData(booking);

      expect(result[0].city).toBe('Address Line 4');
    });

    it('creates dependent fields from reservation guests', () => {
      const room = createRoom({
        roomStay: {
          arrivalDate: '2025-01-15',
          departureDate: '2025-01-20',
          childrenNumber: 1,
          adultsNumber: 2,
          roomType: 'DOUBLE',
          roomExtraInfo: {
            roomName: 'Double',
          },
        },
        reservationGuestList: [
          ...createRoom().reservationGuestList,
          {
            givenName: 'Jane',
            surName: 'Smith',
            email: 'jane@test.com',
            nameTitle: 'Mrs',
            additionalDetails: {
              dob: '1995-02-02',
              passportNumber: 'P456',
              nationality: 'FR',
            },
            homeAddress: {},
            address: {},
            profileId: 'PROFILE2',
          },
        ],
      });

      const booking: any = {
        bookingReference: 'BOOK',
        hotelId: 'HOTEL',
        hotelName: 'Hotel',
        reservationByIdList: [room],
      };

      const result = formatBookingData(booking);

      expect(result[0].dependents).toHaveLength(1);
      expect(result[0].dependents[0]).toEqual(
        expect.objectContaining({
          firstname: 'Jane',
          lastname: 'Smith',
          passport: 'P456',
        })
      );
      expect(result[0].dependent).toBe('1');
    });

    it('handles missing optional booking values', () => {
      const booking: any = {
        bookingReference: 'BOOK',
        hotelId: 'HOTEL',
        hotelName: 'Hotel',
        reservationByIdList: [
          {
            reservationId: 'RES',
            reservationGuestList: [],
            roomStay: {
              childrenNumber: 0,
              adultsNumber: 1,
            },
          },
        ],
      };

      const result = formatBookingData(booking);

      expect(result[0].firstName).toBe('');
      expect(result[0].lastName).toBe('');
      expect(result[0].passport).toBe('');
      expect(result[0].country).toBe('');
      expect(result[0].dependents).toEqual([]);
    });

    it('handles multiple rooms', () => {
      const booking: any = {
        bookingReference: 'BOOK',
        hotelId: 'HOTEL',
        hotelName: 'Hotel',
        reservationByIdList: [
          createRoom(),
          createRoom({
            reservationId: 'RES2',
          }),
        ],
      };

      const result = formatBookingData(booking);

      expect(result).toHaveLength(2);
      expect(result[0].roomNo).toBe(0);
      expect(result[1].roomNo).toBe(1);
      expect(result[0].noOfRooms).toBe(2);
    });
  });

  describe('formatAdditionalInformation', () => {
    it('formats all additional information', () => {
      const result = formatAdditionalInformation(new Date('1990-01-01'), 'P123', { value: 'GB' });

      expect(result).toEqual({
        dob: '2025-01-15',
        passportNumber: 'P123',
        nationality: 'GB',
      });

      expect(formatDate).toHaveBeenCalled();
    });

    it('only adds passport', () => {
      const result = formatAdditionalInformation(undefined as any, 'P123', { value: '' });

      expect(result).toEqual({
        passportNumber: 'P123',
      });
    });

    it('only adds nationality', () => {
      const result = formatAdditionalInformation(undefined as any, '', { value: 'GB' });

      expect(result).toEqual({
        nationality: 'GB',
      });
    });

    it('returns empty object for empty values', () => {
      expect(formatAdditionalInformation(undefined as any, '', { value: '' })).toEqual({});
    });
  });

  describe('formatStayingGuests', () => {
    const booking: any = {
      basketReference: 'BASKET',
      reservationByIdList: [
        {
          reservationId: 'RES1',
          billing: {
            title: 'Mr',
            firstName: 'Booker',
            lastName: 'Smith',
            email: 'test@test.com',
            telephone: '123',
            landline: '456',
          },
          reservationGuestList: [
            {
              givenName: 'John',
              surName: 'Smith',
              nameTitle: 'Mr',
              profileId: 'PROFILE1',
              homeAddress: {
                addressType: 'HOME',
                addressLine2: 'Line 2',
                addressLine3: 'Line 3',
                addressId: 'ADDR1',
              },
            },
            {
              givenName: 'Jane',
              surName: 'Smith',
              profileId: 'PROFILE2',
            },
          ],
        },
      ],
    };

    const form: any = {
      firstName: 'John',
      lastName: 'Smith',
      roomNo: 0,
      reservationId: 'RES1',
      dependents: [
        {
          firstname: 'Jane',
          lastname: 'Smith',
          passport: 'P456',
          nationality: { value: 'FR' },
          dateofbirth: new Date('1995-02-02'),
        },
      ],
      passport: 'P123',
      nationality: { value: 'GB' },
      dateOfBirth: new Date('1990-01-01'),
      city: 'London',
      address: '1 Street',
      country: 'GB',
      postalCode: 'SW1A',
    };

    it('formats main guest and dependents', () => {
      const result = formatStayingGuests(booking, form);

      expect(result).toHaveLength(2);

      expect(result[0]).toEqual(
        expect.objectContaining({
          sameAsBooker: true,
          reservationId: 'RES1',
          isAccompanyingGuest: false,
        })
      );

      expect(result[0].stayingGuestDetails).toEqual(
        expect.objectContaining({
          firstName: 'John',
          lastName: 'Smith',
          profileId: 'PROFILE1',
          additionalDetails: {
            dob: '2025-01-15',
            passportNumber: 'P123',
            nationality: 'GB',
          },
        })
      );

      expect(result[1]).toEqual(
        expect.objectContaining({
          sameAsBooker: false,
          reservationId: 'RES1',
          isAccompanyingGuest: true,
        })
      );

      expect(result[1].stayingGuestDetails.profileId).toBe('PROFILE2');
    });

    it('does not include additional details when main guest has no values', () => {
      const noDetailsForm = {
        ...form,
        passport: '',
        nationality: { value: '' },
        dateOfBirth: undefined,
        dependents: [],
      };

      const result = formatStayingGuests(booking, noDetailsForm);

      expect(result[0].stayingGuestDetails.additionalDetails).toBeUndefined();
    });

    it('removes masked passport', () => {
      const maskedForm = {
        ...form,
        passport: 'XXXXX12345',
        nationality: { value: '' },
        dateOfBirth: undefined,
        dependents: [],
      };

      const result = formatStayingGuests(booking, maskedForm);

      expect(result[0].stayingGuestDetails.additionalDetails).toEqual({});
    });

    it('handles dependent without profile id', () => {
      const bookingWithoutDependentProfile = {
        ...booking,
        reservationByIdList: [
          {
            ...booking.reservationByIdList[0],
            reservationGuestList: [booking.reservationByIdList[0].reservationGuestList[0]],
          },
        ],
      };

      const result = formatStayingGuests(bookingWithoutDependentProfile, form);

      expect(result[1].stayingGuestDetails.profileId).toBeUndefined();
    });

    it('returns empty array without booking', () => {
      expect(formatStayingGuests(undefined as any, form)).toEqual([]);
    });
  });

  describe('isValidBookingStatus', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 2);

    const departureDate = new Date();
    departureDate.setDate(departureDate.getDate() + 4);

    it('returns true for a valid booking', () => {
      expect(
        isValidBookingStatus([
          {
            reservationStatus: BC_RESERVATION_STATUS.COMPLETED,
            arrivalDate: futureDate,
            departureDate,
          },
        ])
      ).toBe(true);
    });

    it('accepts an arrival today', () => {
      const today = new Date();

      const departure = new Date();
      departure.setDate(departure.getDate() + 1);

      expect(
        isValidBookingStatus([
          {
            reservationStatus: BC_RESERVATION_STATUS.COMPLETED,
            arrivalDate: today,
            departureDate: departure,
          },
        ])
      ).toBe(true);
    });

    it('rejects booking more than seven days away', () => {
      const arrival = new Date();
      arrival.setDate(arrival.getDate() + 10);

      const departure = new Date();
      departure.setDate(departure.getDate() + 12);

      expect(
        isValidBookingStatus([
          {
            reservationStatus: BC_RESERVATION_STATUS.COMPLETED,
            arrivalDate: arrival,
            departureDate: departure,
          },
        ])
      ).toBe(false);
    });

    it('rejects past departure', () => {
      const arrival = new Date();
      arrival.setDate(arrival.getDate() - 1);

      const departure = new Date();
      departure.setDate(departure.getDate() - 1);

      expect(
        isValidBookingStatus([
          {
            reservationStatus: BC_RESERVATION_STATUS.COMPLETED,
            arrivalDate: arrival,
            departureDate: departure,
          },
        ])
      ).toBe(false);
    });

    it('rejects invalid reservation status', () => {
      expect(
        isValidBookingStatus([
          {
            reservationStatus: 'CANCELLED',
            arrivalDate: futureDate,
            departureDate,
          },
        ])
      ).toBe(false);
    });

    it('handles string dates', () => {
      const arrival = new Date();
      arrival.setDate(arrival.getDate() + 1);

      const departure = new Date();
      departure.setDate(departure.getDate() + 2);

      expect(
        isValidBookingStatus([
          {
            reservationStatus: BC_RESERVATION_STATUS.COMPLETED,
            arrivalDate: arrival.toISOString(),
            departureDate: departure.toISOString(),
          },
        ])
      ).toBe(true);
    });

    it('checks multiple bookings', () => {
      const invalidArrival = new Date();
      invalidArrival.setDate(invalidArrival.getDate() + 20);

      expect(
        isValidBookingStatus([
          {
            reservationStatus: 'CANCELLED',
            arrivalDate: invalidArrival,
            departureDate,
          },
          {
            reservationStatus: BC_RESERVATION_STATUS.COMPLETED,
            arrivalDate: futureDate,
            departureDate,
          },
        ])
      ).toBe(true);
    });
  });

  describe('formatRegistration', () => {
    const booking: any = {
      basketReference: 'BASKET',
      hotelId: 'HOTEL',
      reservationByIdList: [
        {
          billing: {
            title: 'Mr',
            firstName: 'John',
            lastName: 'Smith',
            email: 'john@test.com',
            telephone: '123',
            landline: '456',
            address: {
              companyName: 'Company',
            },
          },
          additionalGuestInfo: {
            purposeOfStay: 'Business',
          },
          reservationGuestList: [
            {
              nameTitle: 'Mr',
              profileId: 'PROFILE',
              homeAddress: {
                addressType: 'HOME',
                addressLine2: 'Line 2',
                addressLine3: 'Line 3',
                addressId: 'ADDR',
              },
            },
          ],
        },
      ],
    };

    const form: any = {
      roomNo: 0,
      firstName: 'John',
      lastName: 'Smith',
      address: '1 Street',
      city: 'London',
      postalCode: 'SW1A',
      country: 'GB',
      reservationId: 'RES1',
      dependents: [],
      passport: 'P123',
      nationality: { value: 'GB' },
      dateOfBirth: new Date('1990-01-01'),
    };

    it('formats registration', () => {
      const result = formatRegistration(booking, form, 'en');

      expect(result).toEqual(
        expect.objectContaining({
          basketReference: 'BASKET',
          hotelId: 'HOTEL',
          reasonForStay: 'Business',
          sendEmailConfirmation: false,
          sendEmailInvoice: false,
          preCheckIn: true,
          title: 'Mr',
          acceptFutureMailing: false,
          emailAddress: 'john@test.com',
          firstName: 'John',
          lastName: 'Smith',
          companyName: 'Company',
          addressLine1: '1 Street',
          addressLine4: 'London',
          postalCode: 'SW1A',
          mobile: '123',
          landline: '456',
          language: 'en',
          countryCode: 'GB',
          addressId: 'ADDR',
        })
      );
    });

    it('returns false without valid booking data', () => {
      expect(formatRegistration(undefined as any, form, 'en')).toBe(false);

      expect(formatRegistration({}, form, 'en')).toBe(false);

      expect(
        formatRegistration(
          {
            ...booking,
            basketReference: '',
          },
          form,
          'en'
        )
      ).toBe(false);
    });
  });

  describe('fetchBookingConfirmation', () => {
    it('calls graphQLRequest through QueryClient', async () => {
      const response = {
        bookingConfirmation: {
          basketReference: 'BASKET',
        },
      };

      (graphQLRequest as jest.Mock).mockResolvedValue(response);

      const result = await fetchBookingConfirmation({
        basketReference: 'BASKET',
        language: 'en',
        country: 'gb',
      });

      expect(graphQLRequest).toHaveBeenCalledWith(GET_PRECHECKIN_BOOKING_INFORMATION, {
        basketReference: 'BASKET',
        language: 'en',
        country: 'gb',
      });

      expect(result).toEqual(response);
    });
  });

  describe('getHotelDetails', () => {
    it('fetches hotel details', async () => {
      const response = {
        hotelInformation: {
          brand: HotelBrand.PID,
        },
      };

      (graphQLRequest as jest.Mock).mockResolvedValue(response);

      const result = await getHotelDetails('HOTEL1', 'gb', 'en');

      expect(graphQLRequest).toHaveBeenCalledWith(GET_HOTEL_INFORMATION, {
        hotelId: 'HOTEL1',
        language: 'en',
        country: 'gb',
      });

      expect(result).toEqual(response);
    });
  });

  describe('formatHotelAddress', () => {
    it('formats PID address', () => {
      expect(
        formatHotelAddress({
          brand: 'PID',
          address: {
            addressLine1: 'Line 1',
            addressLine2: 'Line 2',
            addressLine3: 'Line 3',
            postalCode: 'POST',
          },
        })
      ).toBe('Line 1, POST, Line 2, Line 3');
    });

    it('formats non-PID address', () => {
      expect(
        formatHotelAddress({
          brand: 'TPT',
          address: {
            addressLine1: 'Line 1',
            addressLine2: 'Line 2',
            addressLine3: 'Line 3',
            postalCode: 'POST',
          },
        })
      ).toBe('Line 1, Line 2, Line 3, POST');
    });

    it('handles empty address', () => {
      expect(
        formatHotelAddress({
          brand: 'PID',
          address: undefined,
        })
      ).toBe('');
    });

    it('filters empty address fields', () => {
      expect(
        formatHotelAddress({
          brand: 'PID',
          address: {
            addressLine1: 'Line 1',
            addressLine2: '',
            addressLine3: undefined,
            postalCode: 'POST',
          },
        })
      ).toBe('Line 1, POST');
    });
  });

  describe('checkPreCheckInStatusForSingleRoom', () => {
    it('returns true for completed pre-check-in', () => {
      const booking: any = {
        reservationByIdList: [
          {
            preCheckInStatus: true,
            deRegCardCompleted: false,
          },
        ],
      };

      expect(checkPreCheckInStatusForSingleRoom(booking, false)).toBe(true);
    });

    it('returns true for completed dereg card when feature is enabled', () => {
      const booking: any = {
        reservationByIdList: [
          {
            preCheckInStatus: false,
            deRegCardCompleted: true,
          },
        ],
      };

      expect(checkPreCheckInStatusForSingleRoom(booking, true)).toBe(true);
    });

    it('returns false when status is incomplete', () => {
      const booking: any = {
        reservationByIdList: [
          {
            preCheckInStatus: false,
            deRegCardCompleted: false,
          },
        ],
      };

      expect(checkPreCheckInStatusForSingleRoom(booking, false)).toBe(false);

      expect(checkPreCheckInStatusForSingleRoom(booking, true)).toBe(false);
    });

    it('returns false for multiple rooms', () => {
      const booking: any = {
        reservationByIdList: [
          {
            preCheckInStatus: true,
            deRegCardCompleted: true,
          },
          {
            preCheckInStatus: true,
            deRegCardCompleted: true,
          },
        ],
      };

      expect(checkPreCheckInStatusForSingleRoom(booking, false)).toBe(false);
    });

    it('returns false for missing booking', () => {
      expect(checkPreCheckInStatusForSingleRoom(undefined as any, false)).toBe(false);
    });
  });

  describe('setBookingCookie', () => {
    it('does nothing without cookie name', () => {
      (setCookie as jest.Mock).mockClear();

      setBookingCookie('', { token: 'TOKEN' }, '10');

      expect(setCookie).not.toHaveBeenCalled();
    });
  });

  describe('handleFindBookingError', () => {
    it('sets room booking error', () => {
      const handler = jest.fn();

      handleFindBookingError(errorStatusObject, handler);

      expect(handler).toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });
  });

  describe('goToHomePage', () => {
    it('closes modal and redirects', () => {
      const setter = jest.fn();

      goToHomePage('/home', setter);

      expect(setter).toHaveBeenCalledWith(false);
      expect(window.location.href).toBe('/home');
    });
  });

  describe('scrollToElement', () => {
    it('scrolls when element exists', () => {
      jest.useFakeTimers();

      const element = {
        scrollIntoView: jest.fn(),
      };

      jest.spyOn(document, 'getElementById').mockReturnValue(element as any);

      scrollToElement('test');

      jest.runAllTimers();

      expect(element.scrollIntoView).toHaveBeenCalledWith({
        block: 'nearest',
        behavior: 'smooth',
      });

      jest.useRealTimers();
    });

    it('does nothing when element does not exist', () => {
      jest.useFakeTimers();

      jest.spyOn(document, 'getElementById').mockReturnValue(null);

      scrollToElement('missing');

      jest.runAllTimers();

      expect(document.getElementById).toHaveBeenCalledWith('missing');

      jest.useRealTimers();
    });
  });

  describe('handleSuccessfulFindBooking', () => {
    const baseResponse: any = {
      findBooking: {
        ref: 'BOOK123',
        basketReference: 'BASKET123',
        cookieName: 'COOKIE',
        token: 'TOKEN',
        sourcePms: FIND_BOOKING_SOURCE_PMS.OPERA,
        redirectBase: 'https://example.com',
        minutesTillExpiry: '10',
      },
    };

    const validBookingConfirmation: any = {
      bookingReference: 'BOOK123',
      basketReference: 'BASKET123',
      hotelId: 'HOTEL',
      hotelName: 'Hotel',
      reservationByIdList: [
        {
          reservationId: 'RES1',
          reservationStatus: 'COMPLETED',
          reservationGuestList: [
            {
              givenName: 'John',
              surName: 'Smith',
              nameTitle: 'Mr',
              additionalDetails: {},
              homeAddress: {},
            },
          ],
          roomStay: {
            arrivalDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
            departureDate: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
            adultsNumber: 1,
            childrenNumber: 0,
            roomExtraInfo: {
              roomName: 'Room',
            },
          },
        },
      ],
    };

    beforeEach(() => {
      (graphQLRequest as jest.Mock).mockImplementation((query: any) => {
        if (query === GET_PRECHECKIN_BOOKING_INFORMATION) {
          return Promise.resolve({
            bookingConfirmation: validBookingConfirmation,
          });
        }

        if (query === GET_HOTEL_INFORMATION) {
          return Promise.resolve({
            hotelInformation: {
              brand: HotelBrand.PID,
              address: {
                addressLine1: 'Address',
                postalCode: 'POST',
              },
            },
          });
        }

        return Promise.resolve({});
      });
    });

    it('returns when findBooking is missing', async () => {
      const spinner = jest.fn();

      await handleSuccessfulFindBooking(
        {} as any,
        'en',
        'gb',
        jest.fn(),
        spinner,
        errorStatusObject,
        jest.fn(),
        '/precheckin',
        jest.fn(),
        false
      );

      expect(spinner).not.toHaveBeenCalled();
    });

    it('handles invalid PMS source', async () => {
      const spinner = jest.fn();

      await handleSuccessfulFindBooking(
        {
          findBooking: {
            ...baseResponse.findBooking,
            sourcePms: 'OTHER',
          },
        } as any,
        'en',
        'gb',
        jest.fn(),
        spinner,
        errorStatusObject,
        jest.fn(),
        '/precheckin',
        jest.fn(),
        false
      );

      expect(spinner).toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });

    it('handles missing ref or basket reference', async () => {
      const spinner = jest.fn();

      await handleSuccessfulFindBooking(
        {
          findBooking: {
            ...baseResponse.findBooking,
            ref: '',
          },
        } as any,
        'en',
        'gb',
        jest.fn(),
        spinner,
        errorStatusObject,
        jest.fn(),
        '/precheckin',
        jest.fn(),
        false
      );

      expect(spinner).toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });

    it('handles invalid redirect base', async () => {
      const spinner = jest.fn();

      (isStringValid as jest.Mock).mockReturnValue(false);

      await handleSuccessfulFindBooking(
        baseResponse,
        'en',
        'gb',
        jest.fn(),
        spinner,
        errorStatusObject,
        jest.fn(),
        '/precheckin',
        jest.fn(),
        false
      );

      expect(spinner).not.toHaveBeenCalledWith({
        ...errorStatusObject,
        reservationErrorStatus: true,
      });
    });

    it('handles undefined booking confirmation', async () => {
      const spinner = jest.fn();

      (graphQLRequest as jest.Mock).mockImplementation((query: any) => {
        if (query === GET_PRECHECKIN_BOOKING_INFORMATION) {
          return Promise.resolve({
            bookingConfirmation: undefined,
          });
        }

        return Promise.resolve({});
      });

      await handleSuccessfulFindBooking(
        baseResponse,
        'en',
        'gb',
        jest.fn(),
        spinner,
        errorStatusObject,
        jest.fn(),
        '/precheckin',
        jest.fn(),
        false
      );

      expect(spinner).not.toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });
  });

  describe('handleBooking', () => {
    const createBooking = (overrides: any = {}) => ({
      bookingReference: 'BOOK',
      basketReference: 'BASKET',
      hotelId: 'HOTEL',
      hotelName: 'Hotel',
      reservationByIdList: [
        {
          reservationId: 'RES',
          reservationStatus: 'COMPLETED',
          preCheckInStatus: false,
          deRegCardCompleted: false,
          reservationGuestList: [
            {
              givenName: 'John',
              surName: 'Smith',
              nameTitle: 'Mr',
              additionalDetails: {},
              homeAddress: {},
            },
          ],
          roomStay: {
            arrivalDate: new Date(Date.now() + 2 * 86400000).toISOString(),
            departureDate: new Date(Date.now() + 3 * 86400000).toISOString(),
            adultsNumber: 1,
            childrenNumber: 0,
            roomExtraInfo: {
              roomName: 'Room',
            },
          },
        },
      ],
      ...overrides,
    });

    const setupGraphQL = (bookingConfirmation: any) => {
      (graphQLRequest as jest.Mock).mockImplementation((query: any) => {
        if (query === GET_PRECHECKIN_BOOKING_INFORMATION) {
          return Promise.resolve({
            bookingConfirmation,
          });
        }

        if (query === GET_HOTEL_INFORMATION) {
          return Promise.resolve({
            hotelInformation: {
              brand: HotelBrand.PID,
              address: {
                addressLine1: 'Address',
                postalCode: 'POST',
              },
            },
          });
        }

        return Promise.resolve({});
      });
    };

    it('does nothing when booking reference is absent', () => {
      const spinner = jest.fn();

      handleBooking({
        query: {},
        handleSpinnersStatus: spinner,
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: jest.fn(),
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      expect(spinner).not.toHaveBeenCalled();
    });

    it('redirects when basket reference is missing', () => {
      const redirect = jest.fn();
      const spinner = jest.fn();

      (getFindBookingToken as jest.Mock).mockReturnValue({
        bookingReference: 'BOOK',
        basketReference: '',
      });

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: spinner,
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: jest.fn(),
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: redirect,
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      expect(redirect).toHaveBeenCalled();
    });

    it('handles single room successfully', async () => {
      const booking = createBooking();

      setupGraphQL(booking);

      const setConfirmation = jest.fn();
      const setForm = jest.fn();
      const setPersonal = jest.fn();
      const setHotel = jest.fn();
      const spinner = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: spinner,
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: setConfirmation,
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: setForm,
        setDisplayPersonalDetailsSection: setPersonal,
        handleRedirect: jest.fn(),
        setHotelAddress: setHotel,
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      await new Promise(process.nextTick);

      expect(setConfirmation).toHaveBeenCalled();
      expect(setForm).toHaveBeenCalled();
      expect(setPersonal).toHaveBeenCalledWith(true);
      expect(setHotel).toHaveBeenCalled();
    });

    it('handles completed single room', async () => {
      const booking = createBooking({
        reservationByIdList: [
          {
            ...createBooking().reservationByIdList[0],
            preCheckInStatus: true,
          },
        ],
      });

      setupGraphQL(booking);

      const setForm = jest.fn();
      const setPersonal = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: jest.fn(),
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: setForm,
        setDisplayPersonalDetailsSection: setPersonal,
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      await new Promise(process.nextTick);

      expect(setForm).not.toHaveBeenCalled();
    });

    it('handles multiple rooms', async () => {
      const room = createBooking().reservationByIdList[0];

      const booking = createBooking({
        reservationByIdList: [
          room,
          {
            ...room,
            reservationId: 'RES2',
          },
        ],
      });

      setupGraphQL(booking);

      const rooms = {
        current: [] as any[],
      };

      const setMultiRoom = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: jest.fn(),
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms,
        setDisplayMultiRoomSection: setMultiRoom,
        setFormDetails: jest.fn(),
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      await new Promise(process.nextTick);

      expect(rooms.current).toHaveLength(2);
      expect(setMultiRoom).toHaveBeenCalledWith(true);
    });

    it('uses dereg card status when feature enabled', async () => {
      const booking = createBooking({
        reservationByIdList: [
          {
            ...createBooking().reservationByIdList[0],
            preCheckInStatus: true,
            deRegCardCompleted: false,
          },
        ],
      });

      setupGraphQL(booking);

      const setForm = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: jest.fn(),
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: setForm,
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: true,
      });

      await new Promise(process.nextTick);

      expect(setForm).toHaveBeenCalled();
    });

    it('handles non-German hotel', async () => {
      const booking = createBooking();

      (graphQLRequest as jest.Mock).mockImplementation((query: any) => {
        if (query === GET_PRECHECKIN_BOOKING_INFORMATION) {
          return Promise.resolve({
            bookingConfirmation: booking,
          });
        }

        if (query === GET_HOTEL_INFORMATION) {
          return Promise.resolve({
            hotelInformation: {
              brand: 'TPT',
              address: {},
            },
          });
        }

        return Promise.resolve({});
      });

      const spinner = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: spinner,
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: jest.fn(),
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      await new Promise(process.nextTick);

      expect(spinner).toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });

    it('handles request failure', async () => {
      (graphQLRequest as jest.Mock).mockRejectedValue(new Error('failure'));

      const spinner = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: spinner,
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: jest.fn(),
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      await new Promise(process.nextTick);

      expect(spinner).toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });

    it('handles invalid booking', async () => {
      const booking = createBooking({
        reservationByIdList: [
          {
            ...createBooking().reservationByIdList[0],
            reservationStatus: 'CANCELLED',
          },
        ],
      });

      setupGraphQL(booking);

      const spinner = jest.fn();

      handleBooking({
        query: {
          bookingReference: 'BOOK',
        },
        handleSpinnersStatus: spinner,
        errorStatusObject,
        currentLang: 'en',
        country: 'gb',
        setBookingConfirmation: jest.fn(),
        rooms: { current: [] },
        setDisplayMultiRoomSection: jest.fn(),
        setFormDetails: jest.fn(),
        setDisplayPersonalDetailsSection: jest.fn(),
        handleRedirect: jest.fn(),
        setHotelAddress: jest.fn(),
        isMobilePreRegisteredRepurposeEnabled: false,
      });

      await new Promise(process.nextTick);

      expect(spinner).toHaveBeenCalledWith({
        ...errorStatusObject,
        roomBookingErrorStatus: true,
      });
    });
  });

  describe('handleSave', () => {
    const bookingConfirmation: any = {
      basketReference: 'BASKET',
      hotelId: 'HOTEL',
      reservationByIdList: [
        {
          reservationId: 'RES',
          billing: {
            title: 'Mr',
            firstName: 'John',
            lastName: 'Smith',
            email: 'test@test.com',
            telephone: '123',
            landline: '456',
          },
          reservationGuestList: [
            {
              nameTitle: 'Mr',
              profileId: 'PROFILE',
              homeAddress: {},
            },
          ],
        },
      ],
    };

    const data: any = {
      roomNo: 0,
      reservationId: 'RES',
      hotelId: 'HOTEL',
      firstName: 'John',
      lastName: 'Smith',
      address: 'Address',
      city: 'London',
      country: 'GB',
      postalCode: 'POST',
      passport: 'P123',
      nationality: {
        value: 'GB',
      },
      dateOfBirth: new Date('1990-01-01'),
      dependents: [],
      arrivalDate: '2025-01-15',
    };

    const setup = () => ({
      svGstMutation: {
        mutateAsync: jest.fn().mockResolvedValue({}),
      },
      attachFileMutation: {
        mutateAsync: jest.fn().mockResolvedValue({}),
      },
      preCheckInStatusMutation: {
        mutateAsync: jest.fn().mockResolvedValue({}),
      },
      setSuccess: jest.fn(),
      setModal: jest.fn(),
      setFailure: jest.fn(),
      setBooking: jest.fn(),
      redirect: jest.fn(),
    });

    it('redirects when booking reference is missing', async () => {
      (getFindBookingToken as jest.Mock).mockReturnValue({
        bookingReference: '',
        basketReference: 'BASKET',
      });

      const setupData = setup();

      await handleSave({
        data,
        type: 'save',
        handleRedirect: setupData.redirect,
        bookingConfirmation,
        currentLang: 'en',
        country: 'gb',
        svGstMutation: setupData.svGstMutation,
        attachFileMutation: setupData.attachFileMutation,
        preCheckInStatusMutation: setupData.preCheckInStatusMutation,
        setSvGstSucessAlert: setupData.setSuccess,
        setIsPreCheckInSuccessModalOpen: setupData.setModal,
        setSvGstFailureAlert: setupData.setFailure,
        regCardUrl: '',
        setBookingConfirmation: setupData.setBooking,
      });

      expect(setupData.redirect).toHaveBeenCalled();
      expect(setupData.svGstMutation.mutateAsync).not.toHaveBeenCalled();
    });

    it('submits without attachment', async () => {
      const setupData = setup();

      await handleSave({
        data,
        type: 'submit',
        handleRedirect: setupData.redirect,
        bookingConfirmation,
        currentLang: 'en',
        country: 'gb',
        svGstMutation: setupData.svGstMutation,
        attachFileMutation: setupData.attachFileMutation,
        preCheckInStatusMutation: setupData.preCheckInStatusMutation,
        setSvGstSucessAlert: setupData.setSuccess,
        setIsPreCheckInSuccessModalOpen: setupData.setModal,
        setSvGstFailureAlert: setupData.setFailure,
        regCardUrl: '',
        setBookingConfirmation: setupData.setBooking,
      });

      expect(setupData.svGstMutation.mutateAsync).toHaveBeenCalled();

      expect(setupData.attachFileMutation.mutateAsync).not.toHaveBeenCalled();

      expect(setupData.preCheckInStatusMutation.mutateAsync).toHaveBeenCalledWith({
        arrivalTime: data.arrivalDate,
        reservationId: data.reservationId,
        hotelId: data.hotelId,
      });

      expect(setupData.setModal).toHaveBeenCalledWith(true);
    });

    it('submits with registration card attachment', async () => {
      const setupData = setup();

      await handleSave({
        data,
        type: 'submit',
        handleRedirect: setupData.redirect,
        bookingConfirmation,
        currentLang: 'en',
        country: 'gb',
        svGstMutation: setupData.svGstMutation,
        attachFileMutation: setupData.attachFileMutation,
        preCheckInStatusMutation: setupData.preCheckInStatusMutation,
        setSvGstSucessAlert: setupData.setSuccess,
        setIsPreCheckInSuccessModalOpen: setupData.setModal,
        setSvGstFailureAlert: setupData.setFailure,
        regCardUrl: 'https://example.com/card.pdf',
        setBookingConfirmation: setupData.setBooking,
      });

      expect(setupData.attachFileMutation.mutateAsync).toHaveBeenCalledWith(
        expect.objectContaining({
          description: 'Pre-check-in registration card',
          fileAttachment: 'https://example.com/card.pdf',
          fileName: 'REG_RESRES_ID000000_PPROFILE.pdf',
          global: true,
          hotelId: 'HOTEL',
          overwriteExistingFile: true,
          reservationId: 'RES',
        })
      );

      expect(setupData.setModal).toHaveBeenCalledWith(true);
    });

    it('handles mutation failure', async () => {
      const setupData = setup();

      setupData.svGstMutation.mutateAsync.mockRejectedValue(new Error('failure'));

      await handleSave({
        data,
        type: 'save',
        handleRedirect: setupData.redirect,
        bookingConfirmation,
        currentLang: 'en',
        country: 'gb',
        svGstMutation: setupData.svGstMutation,
        attachFileMutation: setupData.attachFileMutation,
        preCheckInStatusMutation: setupData.preCheckInStatusMutation,
        setSvGstSucessAlert: setupData.setSuccess,
        setIsPreCheckInSuccessModalOpen: setupData.setModal,
        setSvGstFailureAlert: setupData.setFailure,
        regCardUrl: '',
        setBookingConfirmation: setupData.setBooking,
      });

      expect(setupData.setFailure).toHaveBeenCalledWith(true);
    });

    it('handles false reservation', async () => {
      const setupData = setup();

      const invalidBooking = {
        ...bookingConfirmation,
        basketReference: '',
      };

      await handleSave({
        data,
        type: 'save',
        handleRedirect: setupData.redirect,
        bookingConfirmation: invalidBooking,
        currentLang: 'en',
        country: 'gb',
        svGstMutation: setupData.svGstMutation,
        attachFileMutation: setupData.attachFileMutation,
        preCheckInStatusMutation: setupData.preCheckInStatusMutation,
        setSvGstSucessAlert: setupData.setSuccess,
        setIsPreCheckInSuccessModalOpen: setupData.setModal,
        setSvGstFailureAlert: setupData.setFailure,
        regCardUrl: '',
        setBookingConfirmation: setupData.setBooking,
      });

      expect(setupData.svGstMutation.mutateAsync).not.toHaveBeenCalled();
    });
  });

  describe('generateFileAttachment', () => {
    it('generates a registration card attachment', () => {
      const result = generateFileAttachment({
        reservationId: 'RES123',
        hotelId: 'HOTEL123',
        profileId: 'PROFILE123',
        url: 'https://example.com/card.pdf',
      });

      expect(result).toEqual(
        expect.objectContaining({
          reservationId: 'RES123',
          hotelId: 'HOTEL123',
          fileAttachment: 'https://example.com/card.pdf',
          global: true,
          overwriteExistingFile: true,
          description: 'Pre-check-in registration card',
        })
      );

      expect(result.fileName).toMatch(/^REG_RESRES123_ID\d{6}_PPROFILE123\.pdf$/);
    });

    it('works without URL', () => {
      const result = generateFileAttachment({
        reservationId: 'RES',
        hotelId: 'HOTEL',
        profileId: 'PROFILE',
      });

      expect(result.fileAttachment).toBeUndefined();
      expect(result.fileName).toMatch(/^REG_RESRES_ID\d{6}_PPROFILE\.pdf$/);
    });
  });
});
