import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { fetchBookingInfoMessages } from '../../../../../apollo/subgraphs/content-entity-service/services/booking-information-service';
import { get } from '../../../../../apollo/client/rest-client';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/booking-information-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('fetchBookingInfoMessages', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const args = {
    language: 'en',
    country: 'gb'
  };

  const pipelineContext = new PipelineContext();
  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.BOOKING_FLOW_ID) {
      return 'BID_123';
    } else if (key === ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING) {
      return {
        hotelId: 'HOTEL_1'
      };
    }
  });

  it('should call the get function with correct parameters when fetching booking info messages', async () => {
    await fetchBookingInfoMessages(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(
      endpoints.BOOKING_INFO_MESSAGES,
      fetchBookingInfoMessages,
      {
        language: 'en',
        country: 'gb',
        hotelId: 'HOTEL_1',
        bookingFlowId: 'BID_123'
      },
      context
    );
  });

  it('should handle errors gracefully when fetching booking info messages', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(fetchBookingInfoMessages(args, context, pipelineContext)).rejects.toThrow(
      'Test error'
    );
  });
});
