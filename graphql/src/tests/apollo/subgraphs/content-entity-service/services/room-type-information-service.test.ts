import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import {
  fetchRoomType,
  getRoomTypeInformation
} from '../../../../../apollo/subgraphs/content-entity-service/services/room-type-information-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/booking-information-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('getRoomTypeInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const brand = 'pi';
  const country = 'gb';
  const language = 'en';
  const hotelId = 'HOTEL_1';

  it('should call the get function with correct parameters', async () => {
    await getRoomTypeInformation({ brand, country, language, hotelId }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.ROOM_TYPE_INFORMATION,
      getRoomTypeInformation,
      {
        brand: 'pi',
        country: 'gb',
        language: 'en',
        hotelId: 'HOTEL_1'
      },
      context
    );
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getRoomTypeInformation({ brand, country, language, hotelId }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('fetchRoomType', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const args = {
    language: 'en',
    country: 'gb'
  };

  const pipelineContext = new PipelineContext();
  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING) {
      return {
        brand: 'PI'
      };
    }
  });

  it('should call the get function with correct parameters when fetching room type', async () => {
    await fetchRoomType(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(
      endpoints.ROOM_TYPE,
      fetchRoomType,
      {
        language: 'en',
        country: 'gb',
        brand: 'pi'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching room type', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(fetchRoomType(args, context, pipelineContext)).rejects.toThrow('Test error');
  });
});
