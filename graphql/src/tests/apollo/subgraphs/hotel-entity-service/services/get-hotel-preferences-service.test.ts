import { get } from '../../../../../apollo/client/rest-client';
import { getHotelPreferences } from '../../../../../apollo/subgraphs/hotel-entity-service/services/get-hotel-preferences-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelDistanceFromSearch', () => {
  const context = {};
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const hotelPreferencesEndPoint = {
    endpoint: '/v1/hotels/GATGAT/preferences',
    flowCode: 'DIGITAL_CON_014',
    axiosClient: expect.any(Function)
  };

  const hotelId = 'GATGAT';
  const language = 'en';
  const preferenceGroupsCodes = 'EVENTS';
  const emptyString = '';

  it('should call get with correct parameters when fetching hotel preferences', async () => {
    await getHotelPreferences({ hotelId, preferenceGroupsCodes, language }, context);

    expect(get).toHaveBeenCalledWith(
      hotelPreferencesEndPoint,
      getHotelPreferences,
      expect.objectContaining({
        preferenceGroupsCodes: 'EVENTS',
        language: 'en'
      }),
      context
    );
  });

  it('should call get with correct parameters when language is an empty string', async () => {
    await getHotelPreferences({ hotelId, preferenceGroupsCodes, language: emptyString }, context);

    expect(get).toHaveBeenCalledWith(
      hotelPreferencesEndPoint,
      getHotelPreferences,
      expect.objectContaining({
        preferenceGroupsCodes: 'EVENTS'
      }),
      context
    );
  });

  it('should log error and call handleError when fetching hotel preferences fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getHotelPreferences({ hotelId, preferenceGroupsCodes, language }, context)
    ).rejects.toThrow(
      new Error(
        '{"message":"Test error","errors":[{"field":"Error","message":"Test error"}],"errorType":404}'
      )
    );
  });
});
