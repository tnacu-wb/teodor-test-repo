import { fetchRateCodePricing } from '../../../../../apollo/subgraphs/hotel-entity-service/services/rate-code-pricing-service';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/booking-information-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('fetchRateCodePricing', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const args = {
    bookingChannelCriteria: {
      channel: 'PI',
      subchannel: 'WEB'
    },
    upgradeToEmployeeRate: false
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
          },
          {
            roomStay: {
              ratePlanCode: 'RATE',
              arrivalDate: '2025-01-01',
              departureDate: '2025-01-02',
              roomType: 'TYPE2',
              adultsNumber: 1,
              childrenNumber: 1
            }
          }
        ]
      };
    }
  });

  it('should call the get function with correct parameters when fetching rate code pricing', async () => {
    await fetchRateCodePricing(args, context, pipelineContext);

    const serviceEndpoint = {
      ...endpoints.RATE_CODE_PRICING,
      endpoint: '/v1/hotels/HOTEL_1/rate-code-pricing'
    };
    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      fetchRateCodePricing,
      {
        arrivalDate: '2025-01-01',
        departureDate: '2025-01-02',
        ratePlanCode: 'FLEXRATE',
        roomTypes: 'TYPE1,TYPE2',
        adultsNo: '2,1',
        childrenNo: '0,1',
        reservationRatePlanCode: 'RATE'
      },
      context
    );
  });

  it('should return SKIPPED when booking channel is BB', async () => {
    const argsBB = {
      bookingChannelCriteria: {
        channel: 'BB',
        subchannel: 'WEB'
      }
    };
    const response = await fetchRateCodePricing(argsBB, context, pipelineContext);
    expect(response).toEqual('SKIPPED');
  });

  it('should handle errors gracefully when fetching rate code pricing', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(fetchRateCodePricing(args, context, pipelineContext)).rejects.toThrow(
      'Test error'
    );
  });
});
