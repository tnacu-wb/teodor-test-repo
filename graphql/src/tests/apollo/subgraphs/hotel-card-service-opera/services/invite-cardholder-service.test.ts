import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { inviteCardHolder } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/invite-cardholder-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('inviteCardHolder', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const tetheredUserGuid = '123_456_789';
  const cardId = '123456';
  const inviteCardHolderRequest = {
    scheme: 'GB',
    registrationInfoTitle: 'Mr',
    registrationInfoForename: 'Forename',
    registrationInfoSurname: 'Surname',
    registrationInfoEmailAddress: 'email@email.com',
    sendMeCopyOfInvite: true
  };

  it('should call the post function with correct parameters when inviting cardholder (resend code)', async () => {
    await inviteCardHolder({ tetheredUserGuid, cardId, inviteCardHolderRequest }, context);
    const inviteCardHolderEndpoint = endpoints.INVITE_CARDHOLDER.endpoint
      .replace('{tetheredUserGuid}', tetheredUserGuid)
      .replace('{cardId}', cardId);

    const serviceEndpoint = {
      ...endpoints.INVITE_CARDHOLDER,
      endpoint: inviteCardHolderEndpoint
    };

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      inviteCardHolder,
      inviteCardHolderRequest,
      context
    );
  });

  it('should handle errors gracefully when inviting cardholder (resend code)', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      inviteCardHolder({ tetheredUserGuid, cardId, inviteCardHolderRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
