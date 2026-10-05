import { post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/base-service';
import { addApplicationCard } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/application-card-service';

jest.mock('../../../../../apollo/client/rest-client');

const headers = { 'Content-Type': 'application/json' };
const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
const context = { headers, res };

const request = {
  applicationGuid: '8e2c7c30-9b71-48e2-ae47-c6b1e2b139fa',
  applicationId: '1234567890',
  scheme: 'DE',
  cardDetails: {
    myCard: true,
    cardLimit: 121,
    cardName: 'Test Card'
  }
};

describe('addApplicationCard', () => {
  it('should call the get function with correct parameters when addApplicationCard is called', async () => {
    await addApplicationCard({ addApplicationCardCriteria: request }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.ADD_APPLICATION_CARD,
      addApplicationCard,
      request,
      context
    );
  });

  it('should handle errors gracefully when addApplicationCard throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      post(endpoints.ADD_APPLICATION_CARD, { addApplicationCardCriteria: request }, context)
    ).rejects.toThrow('Test error');
  });
});
