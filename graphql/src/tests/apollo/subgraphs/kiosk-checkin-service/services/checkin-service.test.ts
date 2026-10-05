import { post } from '../../../../../apollo/client/rest-client';
import { checkIn } from '../../../../../apollo/subgraphs/kiosk-checkin-service/services/checkin-service';
import { endpoints } from '../../../../../apollo/subgraphs/kiosk-checkin-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('checkIn', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const checkInCriteria = {
    reservationNumber: 'AJK123456',
    hotelId: 'LONEUS',
    roomType: 'DOUBLE',
    roomId: '123456'
  };

  it('should call the post function when checkIn is called with correct parameters', async () => {
    await checkIn({ checkInCriteria }, context);

    expect(post).toHaveBeenCalledWith(endpoints.CHECKIN, checkIn, checkInCriteria, context);
  });

  it('should handle errors gracefully when checkIn fails', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(checkIn({ checkInCriteria }, context)).rejects.toThrow('Test error');
  });
});
