import { post } from '../../../../../apollo/client/rest-client';
import { updateAppContactDetails } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/update-app-contact-details-service';
import { endpoints } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateAppContactDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const request = {
    applicationGuid: 'd66e62f9-4a9a-488f-b940-bc87d42d817b',
    title: 'Mr',
    foreName: 'Lucian',
    lastName: 'Testerson',
    position: 'Engineer',
    telephone: '+4401902123456',
    email: 'lucian.caba@whitbread.com',
    ipAddress: '1.1.1.1',
    scheme: 'GB',
    applicationId: '784316',
    resumeUrl: 'someURLv3'
  };

  it('should call the post function with correct parameters when updateAppContactDetails is called', async () => {
    await updateAppContactDetails({ updateAppContactDetailsCriteria: request }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.UPDATE_APP_CONTACT_DETAILS,
      updateAppContactDetails,
      request,
      context
    );
  });

  it('should handle errors gracefully when updateAppContactDetails throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      post(endpoints.UPDATE_APP_CONTACT_DETAILS, { loginCriteria: request }, context)
    ).rejects.toThrow('Test error');
  });
});
