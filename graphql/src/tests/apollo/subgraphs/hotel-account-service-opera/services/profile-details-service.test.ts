import { endpoints } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getProfileDetails } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/get-profile-details-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getCompanyDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const customerId = 'COMP_1234567';
  const business = true;
  const innBusiness = true;

  it('should call the get function with correct parameters when getting profile details', async () => {
    await getProfileDetails({ customerId: customerId, business, innBusiness }, context);
    const companyDetailsEndpoint = endpoints.GET_PROFILE_DETAILS.endpoint.replace(
      '{customerId}',
      customerId
    );
    const serviceEndpoint = {
      ...endpoints.GET_PROFILE_DETAILS,
      endpoint: companyDetailsEndpoint
    };

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getProfileDetails,
      { business, innBusiness },
      context
    );
  });

  it('should handle errors gracefully when getting profile details fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getProfileDetails(
        {
          customerId: customerId,
          business: business,
          innBusiness: innBusiness
        },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
