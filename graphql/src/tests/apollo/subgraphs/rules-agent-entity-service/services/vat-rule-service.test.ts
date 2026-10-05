import { get } from '../../../../../../src/apollo/client/rest-client';
import { getVatRule } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/vat-rule-service';
import { endpoints } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getVatRule', () => {
  it('should return all the VAT rules when given the region', async () => {
    const mockResponse = {
      vatRegion: 'UK',
      transCodes: [
        { pkgCode: 'INCPIO', tranCode: '9026' },
        { pkgCode: 'OBFGGO', tranCode: '9026' }
      ]
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const args = { vatRegion: 'UK', pkgCodeArr: ['INCPIO', 'OBFGGO'] };
    const result = await getVatRule(args, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.VAT_RULE,
      getVatRule,
      { vatRegion: 'UK', pkgCodeArr: 'INCPIO,OBFGGO' },
      context
    );
  });
});
