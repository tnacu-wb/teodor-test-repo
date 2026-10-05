import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { cancelAndReplaceInnBPIBACard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/cancel-and-replace-innb-piba-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('cancelAndReplaceInnPIBACard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const tetheredUserId = '123_456_789';
  const cardId = '987_654_321';
  const cancelAndReplaceInnBCardRequest = {
    apiUserGuid: '05b68b91-f7ce-4888-a882-26402631f228',
    issueReplacement: true,
    scheme: 'GB',
    cardCorrespondenceAddress: {
      title: 'Mr',
      forename: 'John',
      surname: 'Doe',
      line1: '123 Main Street',
      line2: 'Apt 4B',
      line3: 'Business Park',
      line4: 'District 9',
      postCode: 'AB12 3CD',
      countryCodeISO: 'GBR',
      associateAddressWithFutureCardholder: false
    },
    cardDeliveryAddressType: 'COMPANY_CORRESPONDENCE_ADDRESS'
  };

  it('should call the post function with correct parameters when cancel or replace the card', async () => {
    await cancelAndReplaceInnBPIBACard(
      { tetheredUserId, cardId, cancelAndReplaceInnBCardRequest },
      context
    );
    const cancelAndReplaceInnPIBACardEndpoint = endpoints.CANCEL_AND_REPLACE_INNB_PIBA_CARD.endpoint
      .replace('{tetheredUserId}', tetheredUserId)
      .replace('{cardId}', cardId);

    const serviceEndpoint = {
      ...endpoints.CANCEL_AND_REPLACE_INNB_PIBA_CARD,
      endpoint: cancelAndReplaceInnPIBACardEndpoint
    };

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      cancelAndReplaceInnBPIBACard,
      cancelAndReplaceInnBCardRequest,
      context
    );
  });

  it('should handle errors gracefully when cancel and replace InnB PIBA card', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      cancelAndReplaceInnBPIBACard(
        { tetheredUserId, cardId, cancelAndReplaceInnBCardRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
