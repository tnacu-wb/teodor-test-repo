import { getCancellationReasons } from '../../../../../apollo/subgraphs/hotel-entity-service/services/cancelation-resons-service';
import { get } from '../../../../../../src/apollo/client/rest-client';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelAvailabilities', () => {
  const context = {};
  const cancellationReasonsEndPoint = {
    endpoint: '/v1/hotels/GATGAT/cancellationReasons',
    flowCode: 'DIGITAL_CAN_001',
    axiosClient: expect.any(Function)
  };

  it('should call the get function with correct parameters when getting cancellation reasons', async () => {
    await getCancellationReasons({ hotelId: 'GATGAT' }, context);
    expect(get).toHaveBeenCalledWith(
      cancellationReasonsEndPoint,
      getCancellationReasons,
      null,
      context
    );
  });

  it('should handle errors gracefully when getting cancellation reasons', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getCancellationReasons({ hotelId: 'GATGAT' }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
