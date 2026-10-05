import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { savePreCheckInStatus } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/pre-check-in-status-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('savePreCheckInStatus', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const preCheckInCriteria = {
    arrivalTime: '2025-04-18T14:00:00.000Z',
    reservationId: 'RES_1234567',
    hotelId: 'HOTELID'
  };

  it('should call the post function when savePreCheckInStatus is called with correct parameters', async () => {
    await savePreCheckInStatus({ preCheckInCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.PRE_CHECK_IN_STATUS,
      savePreCheckInStatus,
      {
        arrivalTime: '2025-04-18T14:00:00.000Z',
        reservationId: 'RES_1234567',
        hotelId: 'HOTELID'
      },
      context
    );
  });

  it('should handle errors gracefully when savePreCheckInStatus fails', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(savePreCheckInStatus({ preCheckInCriteria }, context)).rejects.toThrow(
      new Error(
        '{"message":"Test error","errors":[{"field":"Error","message":"Test error"}],"errorType":404}'
      )
    );
  });
});
