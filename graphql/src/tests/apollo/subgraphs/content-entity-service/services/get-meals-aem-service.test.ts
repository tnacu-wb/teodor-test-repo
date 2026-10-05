import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { getMealsAem } from '../../../../../apollo/subgraphs/content-entity-service/services/get-meals-aem-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getMealsAem', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const pipelineContext = new PipelineContext();

  it('should call GET endpoint with correct parameters', async () => {
    const args = {
      country: 'country',
      language: 'en',
      hotelId: 'HEAPTI'
    };

    const finalMap = {
      country: 'country',
      language: 'en',
      hotelId: 'HEAPTI'
    };

    const serviceEndpoint = {
      ...endpoints.GET_MEALS_AEM,
      endpoint: expect.stringContaining('/v1/content/meals')
    };

    await getMealsAem(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getMealsAem, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const args = {
      hotelId: 'HEAPTI',
      basketReferenceId: 'ref123'
    };

    await expect(getMealsAem(args, context, pipelineContext)).rejects.toThrow('Test error');
  });
});
