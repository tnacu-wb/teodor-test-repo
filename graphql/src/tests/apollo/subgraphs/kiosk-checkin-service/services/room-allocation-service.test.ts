import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/kiosk-checkin-service/services/base-service';
import { roomAllocation } from '../../../../../apollo/subgraphs/kiosk-checkin-service/services/room-allocation-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('roomAllocation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const roomAllocationCriteria = {
    reservationId: 'AJK123456',
    hotelId: 'LONEUS',
    roomType: 'DOUBLE',
    roomId: '123456'
  };

  it('should call the post function when roomAllocation is called with correct parameters', async () => {
    await roomAllocation({ roomAllocationCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.ROOM_ALLOCATION,
      roomAllocation,
      roomAllocationCriteria,
      context
    );
  });

  it('should handle errors gracefully when roomAllocation fails', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(roomAllocation({ roomAllocationCriteria }, context)).rejects.toThrow('Test error');
  });
});
