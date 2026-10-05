import { endpoints } from '../../../../../../src/apollo/subgraphs/company-entity-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';
import { companyProfileById } from '../../../../../apollo/subgraphs/company-entity-service/services/company-profile-by-id-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('companyProfileById Resolver', () => {
  const headers = { 'Content-Type': 'application/json' };
  const id = '12345';
  const mockResponse = { id, companyName: 'Test Company' };

  it('should return company profile data when id is valid', async () => {
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const companyProfileEndpoint = endpoints.COMPANY_PROFILE_BY_ID.endpoint.replace('{id}', id);

    const response = await companyProfileById({ id }, headers);

    expect(get).toHaveBeenCalledWith(
      { ...endpoints.COMPANY_PROFILE_BY_ID, endpoint: companyProfileEndpoint },
      companyProfileById,
      {},
      headers
    );
    expect(response).toEqual(mockResponse);
  });

  it('should return empty object when no profile data is found', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);

    const response = await companyProfileById({ id }, headers);

    expect(response).toEqual({});
  });

  it('should handle errors gracefully when company profile retrieval fails', async () => {
    const error = new Error('Company not found');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(companyProfileById({ id }, headers)).rejects.toThrow('Company not found');
  });
});
