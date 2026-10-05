import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';
import { getSavedPackagesOpera } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/get-saved-packages-opera-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/packages-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('globalConfig', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const pipelineContext = new PipelineContext();

  it('should call the get function with correct parameters', async () => {
    const args = {
      hotelId: 'HEAPTI',
      basketReferenceId: 'ref123'
    };

    const finalMap = {
      hotelId: 'HEAPTI',
      basketReferenceId: 'ref123'
    };

    const serviceEndpoint = {
      ...endpoints.GET_SAVED_PACKAGES_OPERA,
      endpoint: expect.stringContaining('/v1/reservations/ancillaries')
    };

    await getSavedPackagesOpera(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getSavedPackagesOpera, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const args = {
      hotelId: 'HEAPTI',
      basketReferenceId: 'ref123'
    };

    await expect(getSavedPackagesOpera(args, context, pipelineContext)).rejects.toThrow(
      'Test error'
    );
  });
});
