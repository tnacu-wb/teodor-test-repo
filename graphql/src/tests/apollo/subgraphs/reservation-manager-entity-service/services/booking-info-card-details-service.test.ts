import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveBookingInfoCardDetails } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/booking-info-card-details-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveBookingInfoCardDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const bookingInfoCardRequest = {
    bookingReference: '123456789',
    country: 'gb',
    hotelId: 'LONARC',
    language: 'en',
    sourceSystem: '',
    surname: 'user',
    bookingChannel: {
      channel: 'PI',
      language: 'en',
      subchannel: 'WEB'
    },
    arrival: '2025-04-25',
    token: ''
  };

  it('should call the get function when correct parameters are provided', async () => {
    await retrieveBookingInfoCardDetails({ bookingInfoCardRequest }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.BOOKING_INFO_CARD_DETAILS,
      retrieveBookingInfoCardDetails,
      {
        bookingReference: '123456789',
        country: 'gb',
        hotelId: 'LONARC',
        language: 'en',
        surname: 'user',
        channel: 'PI',
        subchannel: 'WEB',
        arrival: '2025-04-25'
      },
      context
    );
  });

  it('should handle errors when an error occurs', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveBookingInfoCardDetails({ bookingInfoCardRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
