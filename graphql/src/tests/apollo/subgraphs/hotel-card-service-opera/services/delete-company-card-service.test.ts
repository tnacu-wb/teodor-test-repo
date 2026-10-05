import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { deleteCompanyCard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/delete-company-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('deleteCompanyCard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const companyId = 'COMP_123_456_789';
  const cardId = 'CARD_123_456_789';

  it('should call the post function with correct parameters when deleting the company card', async () => {
    await deleteCompanyCard({ companyId, cardId }, context);
    const deleteCompanyCardEndpoint = endpoints.DELETE_COMPANY_CARD.endpoint
      .replace('{companyId}', companyId)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.DELETE_COMPANY_CARD,
      endpoint: deleteCompanyCardEndpoint
    };

    expect(post).toHaveBeenCalledWith(serviceEndpoint, deleteCompanyCard, {}, context);
  });

  it('should handle errors gracefully when deleting the company card', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(deleteCompanyCard({ companyId, cardId }, context)).rejects.toThrow('Test error');
  });
});
