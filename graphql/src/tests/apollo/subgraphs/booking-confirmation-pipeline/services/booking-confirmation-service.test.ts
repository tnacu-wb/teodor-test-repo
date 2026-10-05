import { getBookingConfirmation } from '../../../../../apollo/subgraphs/booking-confirmation-pipeline/services/booking-confirmation-service';
import { BookingConfirmationCriteria } from '../../../../../apollo/subgraphs/booking-confirmation-pipeline/models/booking-confirmation-criteria';
import { get } from '../../../../../apollo/client/rest-client';
import { resolvers } from '../../../../../apollo/subgraphs/booking-confirmation-pipeline/subgraph-resolvers';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBookingConfirmation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const bookingConfirmationCriteria: BookingConfirmationCriteria = {
    basketReference: 'BAS_123_456',
    country: 'gb',
    language: 'en',
    bookingChannel: 'BB'
  };

  const bookingInfoMock = {
    hotelId: 'HOTEL_1',
    reservationByIdList: [
      {
        roomStay: {
          ratePlanCode: 'RATE',
          arrivalDate: '2025-01-01',
          departureDate: '2025-01-02',
          roomType: 'TYPE1',
          adultsNumber: 2,
          childrenNumber: 0,
          ratesPerNight: [{ cityTaxPerNight: 2.5 }, { cityTaxPerNight: 2.5 }]
        },
        reservationStatus: 'RESERVED',
        preferences: [
          {
            code: 'DBLE'
          }
        ],
        reservationPackageList: [
          {
            packageCode: 'BF'
          },
          {
            packageCode: 'FI24HR'
          }
        ],
        wifiCode: 'WBPRYDB8PB'
      }
    ],
    totalCost: 162.98,
    basketReference: 'BAS_123_456',
    bookingChannel: 'BB',
    basketStatus: 'COMPLETED'
  };

  const hotelInfoMock = {
    brand: 'pi',
    hotelId: 'HOTEL_1',
    bookingFlow: {
      bookingFlowItems: [
        {
          rateCategory: 'F',
          bookingId: 'booking-a1',
          bookingIdBB: 'booking-business'
        }
      ]
    },
    contactDetails: {
      hotelNationalPhone: '123456789'
    }
  };

  const rateInfoMock = {
    rateClassifications: [
      {
        rateClassification: 'RATE',
        ratePlanCode: 'RATE',
        rateDisplaySet: 'BFL',
        rateCategory: 'F',
        rateOrder: '1'
      }
    ]
  };

  const roomTypeMock = {
    roomTypes: [
      {
        roomTypeCode: ['TYPE1'],
        roomCategory: 'Cat1'
      }
    ]
  };

  const bookingInfoMessagesMock = {
    brand: 'pi',
    infoMessages: [
      {
        rate: 'RATE',
        messages: [
          {
            message: 'This is a test'
          }
        ]
      }
    ]
  };

  it('should return the correct booking confirmation when provided with bookingConfirmationCriteria', async () => {
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(bookingInfoMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(hotelInfoMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(rateInfoMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(roomTypeMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(bookingInfoMessagesMock));

    const response = await getBookingConfirmation(bookingConfirmationCriteria, context);

    expect(response.totalCost).toEqual(162.98);
    expect(response.hotelId).toEqual('HOTEL_1');
    expect(response.basketReference).toEqual('BAS_123_456');
    expect(response.bookingChannel).toEqual('BB');
    expect(response.basketStatus).toEqual('COMPLETED');
    expect(response.reservationByIdList[0].reservationStatus).toEqual('RESERVED');
    expect(response.reservationByIdList[0].preferences[0].code).toEqual('DBLE');
    expect(response.reservationByIdList[0].wifiCode).toEqual('WBPRYDB8PB');
    expect(response.cityTaxTotal).toEqual(5);
  });

  it('should handle errors gracefully when fetching booking confirmation', async () => {
    const expectedMessage = 'Booking information by basket response is empty';
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(null));

    await expect(getBookingConfirmation(bookingConfirmationCriteria, context)).rejects.toThrow(
      expectedMessage
    );
  });
});

describe('BookingConfirmation.isThirdPartyBooking (strict)', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const resolverFn = resolvers.BookingConfirmationDetails.isThirdPartyBooking;

  it('should return true only when idContext is exactly "3rd Party"', () => {
    expect(resolverFn({ idContext: '3rd Party' })).toBe(true);
  });

  it('should return false for any other idContext values', () => {
    const invalidCases = [
      { idContext: '3RD PARTY' },
      { idContext: ' 3rd Party ' },
      { idContext: 'DIRECT' },
      { idContext: '' },
      {} as any,
      { idContext: null } as any,
      { idContext: undefined } as any
    ];

    invalidCases.forEach((testCase) => {
      expect(resolverFn(testCase)).toBe(false);
    });
  });
});
