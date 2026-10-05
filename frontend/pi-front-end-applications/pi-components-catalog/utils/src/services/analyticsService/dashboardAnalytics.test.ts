import analytics from './analytics';
import updateDashboardAnalytics, { getSearchFields, inputsMap } from './dashboardAnalytics';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

const totalResults = 143;
const bookingsTestData = [
  {
    bookingReference: 'GAA2079795',
    currencyCode: 'GBP',
    hotelId: 'FRAMTI',
    sourcePms: 'OPERA',
    status: 'CANCELLED',
    hotelName: 'Frankfurt Messe',
    stayingGuests: [{ firstName: 'Test Auto', lastName: 'Testersons', title: 'Mr' }],
    totalCost: '0',
    booker: { firstName: 'Test Auto', lastName: 'Testersons', title: 'Mr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'GAA0260604',
    currencyCode: 'GBP',
    hotelId: 'FRAMTI',
    sourcePms: 'OPERA',
    status: 'UPCOMING',
    hotelName: 'Frankfurt Messe',
    stayingGuests: [{ firstName: 'John', lastName: 'Pop', title: 'Master' }],
    totalCost: '0',
    booker: { firstName: 'John', lastName: 'Pop', title: 'Master' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'GAA2904894',
    currencyCode: 'GBP',
    hotelId: 'FRAMTI',
    sourcePms: 'OPERA',
    status: 'UPCOMING',
    hotelName: 'Frankfurt Messe',
    stayingGuests: [{ firstName: 'Bill', lastName: 'First', title: 'Mr' }],
    totalCost: '0',
    booker: { firstName: 'Bill', lastName: 'First', title: 'Mr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'GAL6512585',
    currencyCode: 'GBP',
    hotelId: 'DUSOST',
    sourcePms: 'BART',
    status: 'UPCOMING',
    hotelName: 'Duesseldorf City Centre',
    stayingGuests: [{ firstName: 'test', lastName: 'test', title: 'Mr' }],
    totalCost: '0',
    booker: { firstName: 'test', lastName: 'test', title: 'Mr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'GBM9634718',
    currencyCode: 'GBP',
    hotelId: 'BERALX',
    sourcePms: 'OPERA',
    status: 'CANCELLED',
    hotelName: 'Berlin Alexanderplatz',
    stayingGuests: [{ firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' }],
    totalCost: '0',
    booker: { firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'GAA4668163',
    currencyCode: 'GBP',
    hotelId: 'FRAMTI',
    sourcePms: 'OPERA',
    status: 'CANCELLED',
    hotelName: 'Frankfurt Messe',
    stayingGuests: [
      { firstName: 'Test Auto', lastName: 'Testersons', title: 'Mr' },
      { firstName: 'aaa', lastName: 'bbb', title: 'Mr' },
      { firstName: 'ccc', lastName: 'ddd', title: 'Mr' },
      { firstName: 'eee', lastName: 'fff', title: 'Mr' },
      { firstName: 'ggg', lastName: 'hhh', title: 'Mr' },
    ],
    totalCost: '0',
    booker: { firstName: 'Test Auto', lastName: 'Testersons', title: 'Mr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'BKQ3336570',
    currencyCode: 'GBP',
    hotelId: 'DUBSOU',
    sourcePms: 'OPERA',
    status: 'CANCELLED',
    hotelName: 'Dublin City Centre (Temple Bar)',
    stayingGuests: [{ firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' }],
    totalCost: '0',
    booker: { firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-20',
  },
  {
    bookingReference: 'GBM9766317',
    currencyCode: 'GBP',
    hotelId: 'BERALX',
    sourcePms: 'OPERA',
    status: 'CANCELLED',
    hotelName: 'Berlin Alexanderplatz',
    stayingGuests: [{ firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' }],
    totalCost: '0',
    booker: { firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-19',
  },
  {
    bookingReference: 'GAL4331507',
    currencyCode: 'GBP',
    hotelId: 'DUSOST',
    sourcePms: 'OPERA',
    status: 'UPCOMING',
    hotelName: 'Duesseldorf City Centre',
    stayingGuests: [{ firstName: 'Teste', lastName: 'tetet', title: 'Mr' }],
    totalCost: '0',
    booker: { firstName: 'Teste', lastName: 'tetet', title: 'Mr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
  {
    bookingReference: 'GBM5805083',
    currencyCode: 'GBP',
    hotelId: 'BERALX',
    sourcePms: 'OPERA',
    status: 'CANCELLED',
    hotelName: 'Berlin Alexanderplatz',
    stayingGuests: [{ firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' }],
    totalCost: '0',
    booker: { firstName: 'Test Auto', lastName: 'Testersons', title: 'Herr' },
    arrivalDate: '2023-02-08',
    departureDate: '2023-02-09',
  },
];

const inputs = {
  arrivalDate: '2023-07-01',
  bookerLastName: 'Doe',
  bookingReference: 'GAAR107740',
};
const expectedSearchBookingResultsArr: string[] = [];
for (const key in inputsMap) {
  expectedSearchBookingResultsArr.push(inputsMap[key as keyof typeof inputsMap]);
  if (expectedSearchBookingResultsArr.length === 3) {
    break;
  }
}
const expectedSearchBookingResults = expectedSearchBookingResultsArr.join(', ');

describe('DashboardAnalytics', function () {
  describe('getSearchFields Method', () => {
    it('should return analytics searchBookingResults based on search fields', () => {
      expect(getSearchFields(inputs)).toEqual(expectedSearchBookingResults);
    });
  });

  describe('updateDashboardAnalytics Method', () => {
    it('should update dashboard analytics', () => {
      updateDashboardAnalytics({
        bookings: bookingsTestData,
        totalResults,
        inputs,
        newSearch: false,
      });
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        dashboard: {
          cancelledBookings: 6,
          futureBookings: 4,
          totalBookings: 143,
          totalOperaBookings: 9,
          totalBartBookings: 1,
          moreThanNineNights: 2,
          moreThanFourRooms: 1,
          searchBookingResults: expectedSearchBookingResults,
          bookingsReturned: 10,
          userAgentEmail: '',
        },
      });
    });

    it('should update analytics with secureBookings and secureBookingAction', () => {
      (global as any).window = {
        analyticsData: {
          dashboard: {
            cancelledBookings: 1,
            futureBookings: 2,
            totalBookings: 3,
            bookingsReturned: 3,
            userAgentEmail: 'test@example.com',
          },
        },
      };

      const mockSecureBookings = {
        bookingReference: 'BR123',
        arrivalDate: '2025-10-10',
        hotelCode: 'HT123',
        secureBookingAvailable: true,
      };

      updateDashboardAnalytics({
        secureBookings: mockSecureBookings,
        secureBookingAction: true,
        secureBookingComplete: false,
      });

      expect(analyticsUpdateSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          dashboard: expect.objectContaining({
            bookings: mockSecureBookings,
          }),
          secureBookingAction: true,
          secureBookingComplete: false,
        })
      );
    });

    it('should update analytics with only secureBookingComplete if action is undefined', () => {
      (global as any).window = {
        analyticsData: {
          dashboard: {
            cancelledBookings: 0,
            futureBookings: 0,
            totalBookings: 0,
            bookingsReturned: 0,
            userAgentEmail: '',
          },
        },
      };

      const mockSecureBookings = {
        bookingReference: 'BR456',
        arrivalDate: '2025-12-25',
        hotelCode: 'HT456',
        secureBookingAvailable: false,
      };

      updateDashboardAnalytics({
        secureBookings: mockSecureBookings,
        secureBookingComplete: true,
      });

      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        dashboard: expect.objectContaining({
          bookings: mockSecureBookings,
        }),
        secureBookingAction: undefined,
        secureBookingComplete: true,
      });
    });

    it('should update analytics when secureBookings is undefined but secureBookingAction and secureBookingComplete are true', () => {
      (window as any).analyticsData = {
        dashboard: {
          cancelledBookings: 0,
          futureBookings: 0,
          totalBookings: 0,
          bookingsReturned: 0,
          userAgentEmail: 'user@example.com',
        },
      };

      updateDashboardAnalytics({
        secureBookingAction: true,
        secureBookingComplete: true,
        secureBookings: undefined,
      });

      expect(analyticsUpdateSpy).toHaveBeenLastCalledWith({
        dashboard: {
          cancelledBookings: 0,
          futureBookings: 0,
          totalBookings: 0,
          bookingsReturned: 0,
          userAgentEmail: 'user@example.com',
          bookings: undefined,
        },
        secureBookingAction: true,
        secureBookingComplete: true,
      });
    });
  });
});
