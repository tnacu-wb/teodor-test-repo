import { endpoints } from '../../../../../../src/apollo/subgraphs/company-entity-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';

import { companyProfile } from '../../../../../apollo/subgraphs/company-entity-service/services/company-profile-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('companyProfile Resolver', () => {
  const headers = { 'Content-Type': 'application/json' };
  const companyId = '12345';
  const mockResponse = { companyId, companyName: 'Test Company' };

  it('should return company profile data when companyId is valid', async () => {
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const companyProfileEndpoint = endpoints.COMPANY_PROFILE.endpoint.replace(
      '{companyId}',
      companyId
    );

    const response = await companyProfile({ companyId }, headers);

    expect(get).toHaveBeenCalledWith(
      { ...endpoints.COMPANY_PROFILE, endpoint: companyProfileEndpoint },
      companyProfile,
      {},
      headers
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle errors gracefully when company profile retrieval fails', async () => {
    const error = new Error('Company not found');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(companyProfile({ companyId }, headers)).rejects.toThrow('Company not found');
  });
});
