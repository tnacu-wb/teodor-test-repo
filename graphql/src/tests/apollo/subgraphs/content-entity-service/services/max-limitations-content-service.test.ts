import { get } from '../../../../../apollo/client/rest-client';
import {
  getMaxNightsLimitation,
  getMaxArrivalDateLimitation,
  getRoomOccupancyLimitation
} from '../../../../../apollo/subgraphs/content-entity-service/services/max-limitations-content-service';
import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getMaxNightsLimitation', () => {
  const channel = 'PI';

  it('should return maximum night when given a channel', async () => {
    const mockResponse = {
      maxNightsLimitation: {
        maxNights: 9
      }
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const result = await getMaxNightsLimitation({ channel }, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.MAX_NIGHTS_LIMITATIONS,
      getMaxNightsLimitation,
      { channelId: 'PI' },
      context
    );
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getMaxNightsLimitation({ channel }, context)).rejects.toThrow('Test error');
  });
});

describe('getMaxArrivalDateLimitation', () => {
  const channel = 'PI';

  it('should return maximum arrival date when given a channel', async () => {
    const mockResponse = {
      maxArrivalDateLimitation: {
        maxArrivalDate: 365
      }
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const result = await getMaxArrivalDateLimitation({ channel }, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.MAX_ARRIVAL_DATE_LIMITATIONS,
      getMaxArrivalDateLimitation,
      { channelId: 'PI' },
      context
    );
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getMaxArrivalDateLimitation({ channel }, context)).rejects.toThrow('Test error');
  });
});

describe('getRoomOccupancyLimitation', () => {
  const channel = 'PI';
  const brand = 'PI';

  it('should return room occupancy limitation when given a channel', async () => {
    const mockResponse = {
      roomOccupancyLimitations: {
        roomOccupancies: [
          {
            acceptedRoomTypes: ['FAM'],
            adultsNumber: 2,
            childrenNumber: 2
          }
        ]
      }
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const result = await getRoomOccupancyLimitation({ channel, brand }, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.MAX_ROOM_OCCUPANCY,
      getRoomOccupancyLimitation,
      { channelId: 'PI', brand: 'PI' },
      context
    );
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getRoomOccupancyLimitation({ channel, brand }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
