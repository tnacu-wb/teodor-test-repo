import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-entity-service/services/base-service';
import { PipelineContext } from '../../../../../apollo/pipeline/context/PipelineContext';
import { getPackagesOpera } from '../../../../../apollo/subgraphs/hotel-entity-service/services/get-packages-opera-service';
import { ActionContextKeys } from '../../../../../apollo/subgraphs/packages-pipeline/actions/ActionContextKeys';

jest.mock('../../../../../apollo/client/rest-client');

describe('getPackagesOpera', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const pipelineContext = new PipelineContext();

  jest.spyOn(pipelineContext, 'get').mockImplementation((key: string) => {
    if (key === ActionContextKeys.GET_SAVED_PACKAGES_OPERA) {
      return {
        ratePlanCode: 'PI'
      };
    }
  });

  it('should call get endpoint with correct parameters', async () => {
    const args = {
      country: 'country',
      language: 'en',
      hotelId: 'HEAPTI',
      ratePlanCode: 'PI',
      adultsNumber: 'adultsNumber',
      startDate: 'startDate',
      endDate: 'endDate',
      childrenNumber: 'childrenNumber',
      nightsNumber: 'nightsNumber',
      channel: 'channel',
      isManageBookingPage: 'isManageBookingPage'
    };

    const finalMap = {
      country: 'country',
      language: 'en',
      ratePlanCode: 'PI',
      adultsNumber: 'adultsNumber',
      startDate: 'startDate',
      endDate: 'endDate',
      childrenNumber: 'childrenNumber',
      nightsNumber: 'nightsNumber',
      channel: 'channel',
      isManageBookingPage: 'isManageBookingPage'
    };
    let hotelId = 'HEAPTI';

    const updatedEndPoint = endpoints.GET_PACKAGES_OPERA.endpoint.replace('{hotelId}', hotelId);
    const serviceEndpoint = {
      ...endpoints.GET_PACKAGES_OPERA,
      endpoint: updatedEndPoint
    };
    await getPackagesOpera(args, context, pipelineContext);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getPackagesOpera, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const args = {
      country: 'HEAPTI',
      language: 'en'
    };

    await expect(getPackagesOpera(args, context, pipelineContext)).rejects.toThrow('Test error');
  });
});
