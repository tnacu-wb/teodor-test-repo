import { endpoints } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateProfileDetails } from '../../../../../apollo/subgraphs/hotel-account-service-opera/services/update-profile-details-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateProfileDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const customerId = 'customer_123';
  const business = true;
  const innBusiness = false;
  const payload = { name: 'Test Business', address: '123 Business St' };

  it('should call the put function with correct parameters when updating profile details', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    const companyDetailsEndpoint = endpoints.UPDATE_PROFILE_DETAILS.endpoint.replace(
      '{customerId}',
      customerId
    );
    const serviceEndpoint = {
      ...endpoints.UPDATE_PROFILE_DETAILS,
      endpoint: `${companyDetailsEndpoint}?business=true&innBusiness=false`
    };

    await updateProfileDetails({ customerId, business, innBusiness, payload }, context);

    expect(put).toHaveBeenCalledWith(serviceEndpoint, updateProfileDetails, payload, context);
  });
});
