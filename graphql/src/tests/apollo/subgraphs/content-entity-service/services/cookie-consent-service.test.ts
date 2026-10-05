import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getCookieConsent } from '../../../../../apollo/subgraphs/content-entity-service/services/cookie-consent-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getCookieConsent', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const country = 'gb';
  const language = 'en';
  const brand = 'pi';

  it('should call the get function with correct parameters when getting cookie consent', async () => {
    await getCookieConsent({ country, language, brand }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.COOKIE_CONSENT,
      getCookieConsent,
      {
        country: 'gb',
        language: 'en',
        brand: 'pi'
      },
      context
    );
  });

  it('should handle errors gracefully when getting cookie consent fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getCookieConsent({ country, language, brand }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
