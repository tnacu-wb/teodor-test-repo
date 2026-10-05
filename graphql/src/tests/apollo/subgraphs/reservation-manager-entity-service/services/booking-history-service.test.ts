import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { retrieveBookingHistory } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/booking-history-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveBookingHistory', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const bookingHistoryRequest = {
    bookingChannel: {
      subchannel: 'web',
      channel: 'PI',
      language: 'en'
    },
    business: true,
    filterValue: '',
    pageIndex: 1,
    sortOrder: 'DEFAULT',
    pageSize: 40,
    filterType: '',
    typeOfBooking: '',
    continuationToken: ''
  };

  it('should call the post function when correct parameters are provided', async () => {
    await retrieveBookingHistory({ bookingHistoryRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.BOOKING_HISTORY,
      retrieveBookingHistory,
      {
        sortOrder: 'DEFAULT',
        business: true,
        pageSize: 40,
        pageIndex: 1,
        channel: 'PI',
        subchannel: 'web'
      },
      context
    );
  });

  it('should handle errors gracefully when an error occurs', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(retrieveBookingHistory({ bookingHistoryRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
