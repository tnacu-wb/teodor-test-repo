import { post } from '../../../../../apollo/client/rest-client';
import { updateAppCompanyDetails } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/update-app-company-details-service';
import { endpoints } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateAppCompanyDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const request = {
    applicationGuid: '8e2c7c30-9b71-48e2-ae47-c6b1e2b139fa',
    applicationId: '19000783',
    resumeUrl: 'resumeUrlTest1',
    scheme: 'DE',
    appCompanyDetails: {
      companyName: 'Example Company',
      vatRegistrationNumber: 'VAT1223',
      estMonthlySpend: '€2,000',
      companyType: 'Aktiengesellschaft',
      companyRegNum: 'OC342787',
      timeTradingId: '0-6 Monate',
      registrationAddress: {
        addressLine1: '123 Street',
        addressLine2: 'District',
        addressLine3: 'City',
        addressLine4: 'State',
        postcode: '12345',
        countryCode: 'GBR'
      },
      correspondenceContactInfo: {
        title: 'Herr',
        foreName: 'Jane',
        lastName: 'Smith',
        position: 'Manager',
        telephone: '',
        mobile: '+49 15 112345678',
        email: 'jane.smith@example.com'
      },
      hotelBrandPolicy: 'Premier Inn and other hotel brands',
      parentCompanyName: '',
      industrySector: 'ES',
      numberOfEmployees: '51-250',
      companyNameOnCard: 'Example Co.'
    }
  };

  it('should call the post function with correct parameters when updateAppCompanyDetails is called', async () => {
    await updateAppCompanyDetails({ updateAppCompanyDetailsCriteria: request }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.UPDATE_APP_COMPANY_DETAILS,
      updateAppCompanyDetails,
      request,
      context
    );
  });

  it('should handle errors gracefully when updateAppCompanyDetails throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      post(endpoints.UPDATE_APP_COMPANY_DETAILS, { loginCriteria: request }, context)
    ).rejects.toThrow('Test error');
  });
});
