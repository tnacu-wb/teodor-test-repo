import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { getBookingInfoAem } from '../../../../../apollo/subgraphs/content-entity-service/services/get-booking-info-aem-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/packages-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBookingInfoAem', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const pipelineContext = new PipelineContext();

  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.GET_SAVED_PACKAGES_OPERA) {
      return {
        ratePlanCode: 'code123'
      };
    }
  });

  it('should call getBookingInfoAem with correct parameters', async () => {
    const args = {
      country: 'HEAPTI',
      language: 'en',
      hotelId: 'HEAPTI',
      bookingFlowId: 'bookingFlowId123'
    };

    const finalMap = {
      country: 'HEAPTI',
      language: 'en',
      hotelId: 'HEAPTI',
      bookingFlowId: 'bookingFlowId123',
      reservationRatePlanCode: 'code123'
    };

    const serviceEndpoint = {
      ...endpoints.GET_BOOKING_INFO_AEM,
      endpoint: expect.stringContaining('/v1/content/booking')
    };

    await getBookingInfoAem(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getBookingInfoAem, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const args = {
      hotelId: 'HEAPTI',
      basketReferenceId: 'ref123'
    };

    await expect(getBookingInfoAem(args, context, pipelineContext)).rejects.toThrow('Test error');
  });
});
