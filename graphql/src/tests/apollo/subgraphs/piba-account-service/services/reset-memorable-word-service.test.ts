import { post } from '../../../../../../src/apollo/client/rest-client';
import { resetMemorableWord } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/reset-memorable-word-service';
import { endpoints } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('resetMemorableWord Resolver', () => {
  const resetMemorableWordRequest = {
    userId: '12345',
    newMemorableWord: 'newSecureWord123',
    scheme: 'DE'
  };

  it('should call the post function when correct parameters are provided', async () => {
    const mockResponse = { status: 'success' };
    (post as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await resetMemorableWord({ resetMemorableWordRequest }, {});

    expect(post).toHaveBeenCalledWith(
      endpoints.RESET_MEMORABLE_WORD,
      resetMemorableWord,
      resetMemorableWordRequest,
      {}
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when it is returned gracefully', async () => {
    (post as jest.Mock).mockResolvedValueOnce(null);

    const response = await resetMemorableWord({ resetMemorableWordRequest }, {});

    expect(response).toEqual(null);
  });
});
