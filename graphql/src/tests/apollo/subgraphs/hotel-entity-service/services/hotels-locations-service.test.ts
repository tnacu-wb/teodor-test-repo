import { endpoints } from '../../../../../../src/apollo/subgraphs/hotel-entity-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';
import { getHotelsLocations } from '../../../../../../src/apollo/subgraphs/hotel-entity-service/services/hotels-locations-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelDistanceFromSearch', () => {
  const context = {};
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const hotelsSearchCriteria = {
    location: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    locationFormat: 'placeId',
    radius: '10',
    radiusUnit: 'km'
  };

  it('should call get with correct parameters when fetching hotel locations', async () => {
    await getHotelsLocations({ hotelsSearchCriteria }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.HOTELS_LOCATIONS,
      getHotelsLocations,
      expect.objectContaining({
        location: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        locationFormat: 'placeId',
        radius: '10',
        radiusUnit: 'km'
      }),
      context
    );
  });

  it('should log error and call handleError when fetching hotel locations fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(getHotelsLocations({ hotelsSearchCriteria }, context)).rejects.toThrow(
      new Error(
        '{"message":"Test error","errors":[{"field":"Error","message":"Test error"}],"errorType":404}'
      )
    );
  });
});
