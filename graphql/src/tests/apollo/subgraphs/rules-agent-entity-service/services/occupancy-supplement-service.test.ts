import { post } from '../../../../../../src/apollo/client/rest-client';
import { getOccupancySupplement } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/occupancy-supplement-service';
import { endpoints } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getOccupancySupplement', () => {
  it('should return occupancy supplement when given hotel IDs', async () => {
    const mockResponse = {
      dictionary: null,
      list: [
        { hotelId: 'FRAMTI', pricing: 10 },
        { hotelId: 'LONEUS', pricing: 5 }
      ]
    };
    const expectedResponse = [
      { hotelId: 'FRAMTI', pricing: 10 },
      { hotelId: 'LONEUS', pricing: 5 }
    ];
    (post as jest.Mock).mockResolvedValueOnce(mockResponse);
    const hotelIds = { hotelIds: ['FRAMTI', 'LONEUS'] };
    const result = await getOccupancySupplement({ hotelIds }, context);

    expect(result).toEqual(expectedResponse);
    expect(post).toHaveBeenCalledWith(
      endpoints.OCCUPANCY_SUPPLEMENT,
      getOccupancySupplement,
      { hotelIds },
      context
    );
  });
});
