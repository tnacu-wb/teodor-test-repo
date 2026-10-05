import { endpoints } from '../../../../../apollo/subgraphs/company-service-opera/services/base-service';
import { get, put } from '../../../../../apollo/client/rest-client';
import {
  getCompanyDetails,
  updateCompanyDetails
} from '../../../../../apollo/subgraphs/company-service-opera/services/company-details-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getCompanyDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const companyId = 'COMP_1234567';

  it('should call the get function with correct parameters when the company details are fetched', async () => {
    await getCompanyDetails({ companyId }, context);
    const companyDetailsEndpoint = endpoints.COMPANY_DETAILS.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.COMPANY_DETAILS,
      endpoint: companyDetailsEndpoint
    };

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getCompanyDetails, {}, context);
  });

  it('should handle errors gracefully when fetching company details fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getCompanyDetails({ companyId }, context)).rejects.toThrow('Test error');
  });
});

describe('updateCompanyDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const companyId = 'COMP_1234567';
  const companySummary = {
    companyAddress: {
      addressLine1: 'Apollo',
      country: 'GB',
      postCode: '123 456'
    },
    companyName: 'Test_Apollo',
    alternateCompanyName: 'Test_Apollo',
    mainContact: {
      id: 'EMPL_123_456',
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Doe',
      emailAddress: 'test@email.com',
      phoneNumber: '0123456789',
      mobileNumber: '0123456789'
    }
  };

  it('should call the put function with correct parameters when the company details are updated', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    await updateCompanyDetails({ companyId, companySummary }, context);
    const serviceEndpoint = {
      ...endpoints.UPDATE_COMPANY_DETAILS,
      endpoint: '/company/admin/COMP_1234567'
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateCompanyDetails,
      {
        companyAddress: {
          addressLine1: 'Apollo',
          country: 'GB',
          postCode: '123 456'
        },
        companyName: 'Test_Apollo',
        alternateCompanyName: 'Test_Apollo',
        mainContact: {
          id: 'EMPL_123_456',
          title: 'Ms',
          firstName: 'Test',
          lastName: 'Doe',
          emailAddress: 'test@email.com',
          phoneNumber: '0123456789',
          mobileNumber: '0123456789'
        }
      },
      context
    );
  });

  it('should handle errors gracefully when updating company details fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(updateCompanyDetails({ companyId, companySummary }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
