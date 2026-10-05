import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getUpcomingBookings } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/get-upcoming-bookings-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('upcomingBookingsRequest', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const upcomingBookingsRequest = {
    country: 'GB',
    subchannel: 'web',
    channel: 'PI',
    language: 'en'
  };

  it('should call the post function when correct parameters are provided', async () => {
    await getUpcomingBookings({ upcomingBookingsRequest: upcomingBookingsRequest }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.GET_UPCOMING_BOOKINGS,
      getUpcomingBookings,
      upcomingBookingsRequest,
      context
    );
  });

  it('should handle errors gracefully when an error occurs', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getUpcomingBookings({ upcomingBookingsRequest: upcomingBookingsRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
