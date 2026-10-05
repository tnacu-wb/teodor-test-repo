import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getBookingFlowInformation } from '../../../../../apollo/subgraphs/content-entity-service/services/booking-flow-information-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBookingFlowInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const bookingFlowId = 'booking-1';
  const country = 'gb';
  const language = 'en';
  const hotelId = 'HOTEL_1';

  it('should call the get function with correct parameters when getting booking flow information', async () => {
    await getBookingFlowInformation({ bookingFlowId, country, language, hotelId }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.BOOKING_FLOW_INFORMATION,
      getBookingFlowInformation,
      {
        bookingFlowId: 'booking-1',
        country: 'gb',
        language: 'en',
        hotelId: 'HOTEL_1'
      },
      context
    );
  });

  it('should handle errors gracefully when getting booking flow information', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getBookingFlowInformation({ bookingFlowId, country, language, hotelId }, context)
    ).rejects.toThrow('Test error');
  });
});
