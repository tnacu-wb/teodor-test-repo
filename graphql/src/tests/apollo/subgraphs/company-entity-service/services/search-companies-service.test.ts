import { endpoints } from '../../../../../../src/apollo/subgraphs/company-entity-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';

import {
  searchCompanies,
  searchCompaniesByProfile,
  searchCompaniesByParams
} from '../../../../../apollo/subgraphs/company-entity-service/services/search-companies-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('searchCompanies Resolver', () => {
  const headers = { 'Content-Type': 'application/json' };
  const searchCompaniesCriteria = {
    companyName: 'TestCompany',
    hotelId: 'Hotel123',
    negotiatedRateCompanies: true,
    arNumber: 'AR123',
    offset: 0,
    limit: 10
  };

  const mockResponse = {
    totalPages: 5,
    offset: 0,
    limit: 10,
    hasMore: true,
    totalResults: 50,
    continuationToken: null,
    companies: []
  };

  it('should call get function with correct parameters when /v1/companies/profile is called', async () => {
    const updatedSearchCriteria = { ...searchCompaniesCriteria, negotiatedRateCompanies: false };

    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await searchCompanies(
      { searchCompaniesCriteria: updatedSearchCriteria },
      headers
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_COMPANIES_PROFILE,
      searchCompaniesByProfile,
      expect.objectContaining({
        hotelId: 'Hotel123',
        arNumber: 'AR123',
        limit: 10,
        companyName: 'TestCompany'
      }),
      headers
    );

    expect(response).toEqual(mockResponse);
  });

  it('should call get function with correct parameters when /v1/companies is called', async () => {
    const updatedCriteria = { ...searchCompaniesCriteria, negotiatedRateCompanies: true };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await searchCompanies({ searchCompaniesCriteria: updatedCriteria }, headers);

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_COMPANIES,
      searchCompaniesByParams,
      expect.objectContaining({
        companyName: 'TestCompany',
        arNumber: 'AR123',
        negotiatedRateCompanies: true,
        limit: 10,
        offset: 0
      }),
      headers
    );

    expect(response).toEqual(mockResponse);
  });

  it('should handle errors gracefully when searchCompanies fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(searchCompanies({ searchCompaniesCriteria }, headers)).rejects.toThrow(
      'Test error'
    );
  });
});
