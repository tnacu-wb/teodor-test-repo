import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { dlpInformation } from '../../../../../apollo/subgraphs/content-entity-service/services/dlp-information-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('dlpInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const language = 'en';
  const country = 'gb';
  const dlpPath = '/england/bedfordshire/luton';

  it('should call the get function with correct parameters', async () => {
    const finalMap = { language: 'en', country: 'gb', dlpPath: '/england/bedfordshire/luton' };
    const serviceEndpoint = {
      ...endpoints.DLP_INFORMATION,
      endpoint: expect.stringContaining(`/v1/content/dlp-information`)
    };

    await dlpInformation({ language, country, dlpPath }, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, dlpInformation, finalMap, context);
  });
});

describe('dlpInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const country = 'gb';
  const dlpPath = '/england/bedfordshire/luton';

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    const language = 'en';
    await expect(dlpInformation({ language, country, dlpPath }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
