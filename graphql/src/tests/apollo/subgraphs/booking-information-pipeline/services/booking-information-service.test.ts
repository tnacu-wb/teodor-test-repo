import { getBookingInformationService } from '../../../../../apollo/subgraphs/booking-information-pipeline/services/booking-information-service';
import { get } from '../../../../../apollo/client/rest-client';
import {
  BookingInformationCriteria,
  Channel
} from '../../../../../apollo/subgraphs/booking-information-pipeline/models/booking-information-criteria';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBookingInformationService', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  const bookingInformationCriteria: BookingInformationCriteria = {
    basketReference: 'BAS_123_456',
    upgradeToEmployeeRate: false,
    country: 'gb',
    language: 'en',
    bookingChannelCriteria: {
      channel: Channel.BB,
      subchannel: 'WEB'
    }
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
          ratesPerNight: [{ cityTaxPerNight: 3.0 }, { cityTaxPerNight: 3.0 }]
        },
        additionalGuestInfo: {
          purposeOfStay: 'LEISURE'
        },
        reservationStatus: 'RESERVED'
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
    name: 'Test Hotel',
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
        roomCategory: 'Cat1',
        roomLabel: 'Standard Room',
        roomDescription: 'A standard room'
      }
    ]
  };

  const bookingInfoMessagesMock = {
    brand: 'pi',
    infoMessages: [
      {
        rate: 'RATE',
        rateCategory: 'F',
        rateDisplaySet: 'BFL',
        messages: [
          {
            message: 'This is a test'
          }
        ]
      }
    ]
  };

  const rateCodePricingMock = {
    ratePlanCode: 'FLEXRATE',
    totalNetAmount: null,
    currencyCode: 'GBP'
  };

  it('should handle errors gracefully when fetching booking information', async () => {
    const expectedMessage = 'Booking information by basket response is empty';
    (get as jest.Mock).mockResolvedValueOnce(null);

    await expect(getBookingInformationService(bookingInformationCriteria, context)).rejects.toThrow(
      expectedMessage
    );
  });

  it('should return the correct booking information with cityTaxTotal calculated', async () => {
    (get as jest.Mock).mockResolvedValueOnce(bookingInfoMock);
    (get as jest.Mock).mockResolvedValueOnce(hotelInfoMock);
    (get as jest.Mock).mockResolvedValueOnce(rateInfoMock);
    (get as jest.Mock).mockResolvedValueOnce(roomTypeMock);
    (get as jest.Mock).mockResolvedValueOnce(bookingInfoMessagesMock);
    (get as jest.Mock).mockResolvedValueOnce(rateCodePricingMock);

    const response = await getBookingInformationService(bookingInformationCriteria, context);

    expect(response.hotelId).toEqual('HOTEL_1');
    expect(response.basketReference).toEqual('BAS_123_456');
    expect(response.cityTaxTotal).toEqual(6);
  });
});
