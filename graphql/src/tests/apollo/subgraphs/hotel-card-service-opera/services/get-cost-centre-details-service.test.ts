import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getCostCentreDetails } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/get-cost-centre-details-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getCostCentreDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const tetheredUserGuid = '123asd456fgh789jkl';

  it('should call the get function with correct parameters when retrieving cost centre details', async () => {
    await getCostCentreDetails({ tetheredUserGuid }, context);
    const getCostCentreDetailsEndpoint = endpoints.COST_CENTRE_DETAILS.endpoint.replace(
      '{tetheredUserGuid}',
      tetheredUserGuid
    );
    const serviceEndpoint = {
      ...endpoints.COST_CENTRE_DETAILS,
      endpoint: getCostCentreDetailsEndpoint
    };

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getCostCentreDetails, {}, context);
  });

  it('should handle errors gracefully when retrieving cost centre details', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getCostCentreDetails({ tetheredUserGuid }, context)).rejects.toThrow('Test error');
  });
});
