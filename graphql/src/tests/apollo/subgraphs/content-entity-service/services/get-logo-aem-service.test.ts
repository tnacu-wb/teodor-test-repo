import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { getLogoAem } from '../../../../../apollo/subgraphs/content-entity-service/services/get-logo-aem-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBookingInfoAem', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const pipelineContext = new PipelineContext();

  it('should call get endpoint with correct parameters', async () => {
    const args = {
      country: 'country',
      language: 'en',
      hotelId: 'HEAPTI'
    };

    const finalMap = {
      country: 'country',
      language: 'en'
    };

    let hotelId = 'HEAPTI';

    const updatedEndPoint = endpoints.GET_LOGO_AEM.endpoint.replace('{hotelId}', hotelId);
    const serviceEndpoint = {
      ...endpoints.GET_LOGO_AEM,
      endpoint: updatedEndPoint
    };
    await getLogoAem(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getLogoAem, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const args = {
      country: 'HEAPTI',
      language: 'en'
    };

    await expect(getLogoAem(args, context, pipelineContext)).rejects.toThrow('Test error');
  });
});
