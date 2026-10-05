import { get } from '../../../../../../src/apollo/client/rest-client';
import { getHotelDistanceFromSearch } from '../../../../../../src/apollo/subgraphs/hotel-entity-service/services/hotel-distance-from-search-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelDistanceFromSearch', () => {
  const context = {};
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const hotelDistanceEndPoint = {
    endpoint: '/v1/hotels/GATGAT/distance',
    flowCode: 'DIGITAL_AVA_007',
    axiosClient: expect.any(Function)
  };

  const hotelDistanceFromSearchCriteria = {
    hotelId: 'GATGAT',
    location: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    locationFormat: 'placeId',
    radius: '10',
    radiusUnit: 'km'
  };

  it('should call get when fetching hotel distance from search with correct parameters', async () => {
    await getHotelDistanceFromSearch({ hotelDistanceFromSearchCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      hotelDistanceEndPoint,
      getHotelDistanceFromSearch,
      expect.objectContaining({
        location: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        locationFormat: 'placeId',
        radius: '10',
        radiusUnit: 'km'
      }),
      context
    );
  });

  it('should log error and call handleError when fetching hotel distance from search fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getHotelDistanceFromSearch({ hotelDistanceFromSearchCriteria }, context)
    ).rejects.toThrow(
      new Error(
        '{"message":"Test error","errors":[{"field":"Error","message":"Test error"}],"errorType":404}'
      )
    );
  });
});
