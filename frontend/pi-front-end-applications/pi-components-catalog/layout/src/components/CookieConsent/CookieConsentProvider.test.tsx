import { render, waitFor, screen } from '@testing-library/react';
import { CountryLanguage, PI } from '@whitbread-eos/api';
import { getCookieConsentInfo } from '@whitbread-eos/utils/server';
import React from 'react';

import { CookieConsentProvider, CookieConsentContext } from './CookieConsentProvider';

jest.mock('@whitbread-eos/utils/server', () => ({
  getCookieConsentInfo: jest.fn(),
}));

const mockCountryLanguage: CountryLanguage = { country: 'gb', language: 'en' };

describe('CookieConsentProvider', () => {
  beforeEach(() => {
    (getCookieConsentInfo as jest.Mock).mockReset();
  });

  it('renders children when data is successfully fetched', async () => {
    (getCookieConsentInfo as jest.Mock).mockResolvedValue({ someData: 'test' });

    const { container } = render(
      <CookieConsentProvider countryLanguageResolver={() => mockCountryLanguage}>
        <div data-testid="child">Hello</div>
      </CookieConsentProvider>
    );

    await waitFor(() => {
      expect(getCookieConsentInfo).toHaveBeenCalledWith(
        mockCountryLanguage.country,
        mockCountryLanguage.language,
        PI
      );
    });

    expect(await screen.findByTestId('child')).toBeInTheDocument();
    expect(container).not.toBeEmptyDOMElement();
  });

  it('provides the fetched data via context', async () => {
    const mockCookieConsentData = { someData: 'test' };
    (getCookieConsentInfo as jest.Mock).mockResolvedValue(mockCookieConsentData);

    const { container } = render(
      <CookieConsentProvider countryLanguageResolver={() => mockCountryLanguage}>
        <CookieConsentContext.Consumer>
          {(value) => <div data-testid="context-data">{JSON.stringify(value)}</div>}
        </CookieConsentContext.Consumer>
      </CookieConsentProvider>
    );

    await waitFor(() => {
      expect(getCookieConsentInfo).toHaveBeenCalledWith(
        mockCountryLanguage.country,
        mockCountryLanguage.language,
        PI
      );
    });

    expect(await screen.findByTestId('context-data')).toHaveTextContent(
      JSON.stringify({
        cookieConsentData: mockCookieConsentData,
        countryLanguage: mockCountryLanguage,
      })
    );
    expect(container).not.toBeEmptyDOMElement();
  });
});
