import { Area } from '@whitbread-eos/api';

import useBookingConfimationData from './use-booking-confirmation-data';

const mockCookieResponse = {
  response: 'the auth cookie',
};

jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useQueryRequest: () => mockUseQueryRequest,
}));

jest.mock('../getters', () => ({
  ...jest.requireActual('../getters'),
  getAuthCookie: () => mockCookieResponse.response,
}));

jest.mock('./useAuthToken', () => ({
  useAuthToken: () => ({
    token: mockCookieResponse.response,
    isAuth0Enabled: false,
    isLoading: false,
  }),
}));

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    bookingConfirmationAuthenticated: {
      reservationByIdList: [
        {
          billing: {
            address: {
              addressLine1: 'Dental Surgery',
              postalCode: 'GU16 7HF',
            },
            title: 'Mr',
            telephone: '+440123123123',
            firstName: 'Catalin',
            lastName: 'Iosif',
            email: 'catalin.iosif@mailinator.com',
          },
          reservationGuestList: [
            {
              givenName: 'Catalin',
              surName: 'Iosif',
            },
          ],
          gdsReferenceNumber: null,
          roomStay: {
            checkInTime: '15:00',
            checkOutTime: '12:00',
            ratePlanCode: 'FLEXRATE',
            arrivalDate: '2023-06-15',
            departureDate: '2023-06-17',
            bookingChannel: 'PI.com',
            roomPrice: 1998,
            cot: false,
            adultsNumber: 1,
            roomExtraInfo: {
              roomName: 'Premier Plus Room',
            },
            childrenNumber: 0,
          },
          paymentCard: {
            cardNumberMasked: 'XXXXXXXXXXXX0017',
          },
          reservationOverrideReasons: {
            reasonCode: 'DTH',
            callerName: 'test',
            managerName: '',
            reasonName: 'Death',
          },
          reservationOverridden: true,
          guaranteeCode: 'CC',
          reservationStatus: 'Reserved',
          additionalGuestInfo: {
            purposeOfStay: 'LEI',
          },
        },
      ],
      balanceOutstanding: 1998,
      currencyCode: 'GBP',
      newTotal: 1998,
      policyCode: 'D1A',
      previousTotal: 0,
      totalCost: 1998,
      hotelId: 'MANOLD',
      hotelName: 'Manchester Old Trafford',
      bookingFlowId: 'booking-a1',
      rateMessage: '<p>Amend or cancel up to 1pm on arrival day</p>\n',
    },
  },
};

const params = {
  basketReference: 'AWM1495858',
  bookingReference: 'AWM1495858-uuid',
  language: 'en',
  country: 'gb',
};

describe('useBookingConfimationData', () => {
  it('should return Booking Confimation Data', () => {
    const expectedHotelName = 'Manchester Old Trafford';
    const bookingDetails = useBookingConfimationData(
      Area.CCUI,
      params.basketReference,
      params.bookingReference,
      params.language,
      params.country
    );
    expect(bookingDetails.bookingData.hotelName).toBe(expectedHotelName);
  });

  it('should return Booking Confimation Data with no cookie', () => {
    mockCookieResponse.response = undefined;
    const bookingDetails = useBookingConfimationData(
      Area.PI,
      params.basketReference,
      params.bookingReference,
      params.language,
      params.country
    );
    expect(bookingDetails.bookingData).toBe(undefined);
  });
});
