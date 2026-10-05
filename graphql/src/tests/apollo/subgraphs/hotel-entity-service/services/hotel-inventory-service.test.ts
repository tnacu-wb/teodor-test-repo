import { get } from '../../../../../apollo/client/rest-client';
import { getHotelInventory } from '../../../../../apollo/subgraphs/hotel-entity-service/services/hotel-inventory-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelInventory', () => {
  const context = {};
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const hotelInventoryEndPoint = {
    endpoint: '/v1/hotels/GATGAT/hotelInventory',
    flowCode: 'DIGITAL_AVA_009',
    axiosClient: expect.any(Function)
  };

  const hotelId = 'GATGAT';
  const dateRangeStart = '2025-08-01';
  const dateRangeEnd = '2025-08-02';
  const emptyString = '';

  it('should call get with correct parameters when fetching hotel inventory', async () => {
    await getHotelInventory({ hotelId, dateRangeEnd, dateRangeStart }, context);

    expect(get).toHaveBeenCalledWith(
      hotelInventoryEndPoint,
      getHotelInventory,
      expect.objectContaining({
        dateRangeStart: '2025-08-01',
        dateRangeEnd: '2025-08-02'
      }),
      context
    );
  });

  it('should log error and call handleError when fetching hotel inventory fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getHotelInventory({ hotelId, dateRangeEnd, dateRangeStart }, context)
    ).rejects.toThrow(
      new Error(
        '{\"message\":\"Test error\",\"errors\":[{\"field\":\"Error\",\"message\":\"Test error\"}],\"errorType\":404}'
      )
    );
  });
});
