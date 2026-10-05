import { get } from '../../../../../../src/apollo/client/rest-client';
import {
  getPartialAddress,
  getFormattedAddress
} from '../../../../../../src/apollo/subgraphs/address-lookup-entity-service/services/address-lookup-service';
import { endpoints } from '../../../../../../src/apollo/subgraphs/address-lookup-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getPartialAddress', () => {
  const partialAddressCriteria = {
    searchTerm: 'SW1A 1AA',
    countryCode: 'GB'
  };

  it('should return addresses when a valid postcode is provided', async () => {
    const mockResponse = [
      {
        id: 'GBB|4329f367-f2b0-4654-b681-46198639ed03|7.7308OGBBEQHpBwAAAAABAwEAAAAA9COQUgAgAAAAAAAAMQAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABUVzMgNE5GAAAAAAA-$7',
        addressText: '1 Chamberlain Gardens, HOUNSLOW TW3 4N'
      }
    ];

    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const result = await getPartialAddress({ partialAddressCriteria }, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.PARTIAL_ADDRESS,
      getPartialAddress,
      partialAddressCriteria,
      context
    );
  });
});

describe('getFormattedAddress', () => {
  it('should return address when a valid monker id is provided', async () => {
    const identifier =
      'GBB%7C4329f367-f2b0-4654-b681-46198639ed03%7C7.7308OGBBEQHpBwAAAAABAwEAAAAA9COQUgAgAAAAAAAAMQAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABUVzMgNE5GAAAAAAA-%247';
    const mockResponse = [
      {
        addressLine1: '1 Chamberlain Gardens',
        addressLine2: '',
        addressLine3: '',
        addressLine4: 'HOUNSLOW',
        addressLine5: '',
        companyName: '',
        label: '1 Chamberlain Gardens, HOUNSLOW, TW3 4NF',
        postcode: 'TW3 4NF',
        country: 'GB'
      }
    ];

    const updatedEndPoint = endpoints.FORMAT_ADDRESS.endpoint.replace('{identifier}', identifier);
    const serviceEndpoint = {
      ...endpoints.FORMAT_ADDRESS,
      endpoint: updatedEndPoint
    };

    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const result = await getFormattedAddress({ identifier }, context);

    expect(result).toEqual(mockResponse);
    expect(get).toHaveBeenCalledWith(serviceEndpoint, getFormattedAddress, {}, context);
  });
});
