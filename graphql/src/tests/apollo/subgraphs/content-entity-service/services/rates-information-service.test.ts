import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import {
  fetchRateInformation,
  getRatesInformation
} from '../../../../../apollo/subgraphs/content-entity-service/services/rates-information-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/booking-information-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('getRatesInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const brand = 'pi';
  const country = 'gb';
  const language = 'en';
  const hotelId = 'HOTEL_1';
  const channel = 'PI';
  const ratePlans = ['FLEXRATE', 'SEMIFLEX'];

  it('should call the get function with correct parameters when getting rates information', async () => {
    await getRatesInformation({ brand, country, language, hotelId, channel, ratePlans }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.RATES_INFORMATION,
      getRatesInformation,
      {
        brand: 'pi',
        country: 'gb',
        language: 'en',
        hotelId: 'HOTEL_1',
        channel: 'PI',
        ratePlans: 'FLEXRATE,SEMIFLEX'
      },
      context
    );
  });

  it('should handle errors gracefully when getting rates information fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getRatesInformation({ brand, country, language, hotelId, channel, ratePlans }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('fetchRateInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const args = {
    language: 'en',
    country: 'gb',
    bookingChannelCriteria: {
      channel: 'PI',
      subchannel: 'WEB'
    }
  };

  const pipelineContext = new PipelineContext();
  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.BOOKING_INFORMATION_BY_BASKET) {
      return {
        hotelId: 'HOTEL_1',
        reservationByIdList: [
          {
            roomStay: {
              ratePlanCode: 'RATE',
              arrivalDate: '2025-01-01',
              departureDate: '2025-01-02',
              roomType: 'TYPE1',
              adultsNumber: 2,
              childrenNumber: 0
            }
          }
        ]
      };
    } else if (key === ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING) {
      return {
        brand: 'pi',
        hotelId: 'HOTEL_1'
      };
    }
  });

  it('should call the get function with correct parameters when fetching rate information', async () => {
    await fetchRateInformation(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(
      endpoints.GET_RATE_INFORMATION,
      fetchRateInformation,
      {
        language: 'en',
        country: 'gb',
        brand: 'pi',
        channel: 'PI',
        ratePlans: 'RATE',
        hotelId: 'HOTEL_1'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching rate information fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(fetchRateInformation(args, context, pipelineContext)).rejects.toThrow(
      'Test error'
    );
  });
});
