import { retrieveChangesLog } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/change-log-service';
import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveChangesLog', () => {
  const context = {};

  beforeEach(() => {
    jest.clearAllMocks();
  });
  const hotelId = 'hotel123';
  const reservationId = 'res123';
  const limit = 10;
  const offset = 0;

  it('should call get with correct parameters when retrieving changes log', async () => {
    await retrieveChangesLog({ hotelId, reservationId, limit, offset }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.RETRIEVE_CHANGES_LOG,
      retrieveChangesLog,
      {
        hotelId,
        reservationId,
        limit,
        offset
      },
      context
    );
  });

  it('should call get without optional parameters when retrieving changes log', async () => {
    await retrieveChangesLog({ hotelId, reservationId }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.RETRIEVE_CHANGES_LOG,
      retrieveChangesLog,
      {
        hotelId,
        reservationId
      },
      context
    );
  });

  it('should handle errors gracefully when retrieving changes log', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(retrieveChangesLog({ hotelId, reservationId }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
