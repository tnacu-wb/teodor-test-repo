import { endpoints } from '../../../../../apollo/subgraphs/marketing-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveAnonymousNewsletterPreferences } from '../../../../../apollo/subgraphs/marketing-service-opera/services/anonymous-newsletter-preferences-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveAnonymousNewsletterPreferences', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const email = 'test@email.com';
  const brandCode = 'PINN';
  const countryOfResidence = 'gb';
  const language = 'en';

  it('should call the get function when retrieveAnonymousNewsletterPreferences is called with correct parameters', async () => {
    await retrieveAnonymousNewsletterPreferences(
      { email, brandCode, countryOfResidence, language },
      context
    );
    const anonymousNewsletterPreferencesEndpoint =
      endpoints.ANONYMOUS_NEWSLETTER_PREFERENCES.endpoint.replace('{email}', email);
    const serviceEndpoint = {
      ...endpoints.ANONYMOUS_NEWSLETTER_PREFERENCES,
      endpoint: anonymousNewsletterPreferencesEndpoint
    };

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      retrieveAnonymousNewsletterPreferences,
      { brandCode: 'PINN', countryOfResidence: 'gb', language: 'en' },
      context
    );
  });

  it('should handle errors gracefully when retrieveAnonymousNewsletterPreferences fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveAnonymousNewsletterPreferences(
        { email, brandCode, countryOfResidence, language },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
