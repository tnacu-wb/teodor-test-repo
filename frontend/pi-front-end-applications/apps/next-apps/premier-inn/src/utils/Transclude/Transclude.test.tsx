import { render } from '@testing-library/react';
import getConfig from 'next/config';
import React from 'react';

import Transclude, { fetchMicroFrontend } from './Transclude';

// Mocking getConfig to control runtime configuration
jest.mock('next/config', () => jest.fn());

// Mocking fetch globally
global.fetch = jest.fn();

// Mocking renderSanitizedHtml
jest.mock('@whitbread-eos/utils', () => ({
  renderSanitizedHtml: (html: string) => (
    <div data-testid="sanitized-content" dangerouslySetInnerHTML={{ __html: html }} />
  ),
}));

describe('fetchMicroFrontend', () => {
  beforeEach(() => {
    jest.resetAllMocks();
    (getConfig as jest.Mock).mockReturnValue({
      publicRuntimeConfig: {
        NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: 'https://example.com/assets/',
      },
    });
  });

  it('should fetch the micro-frontend content successfully', async () => {
    const mockResponseText = '<h1>Hello World</h1>';
    (fetch as jest.Mock).mockResolvedValueOnce({
      ok: true,
      text: jest.fn().mockResolvedValueOnce(mockResponseText),
    });

    const result = await fetchMicroFrontend('https://example.com/assets/mfe1.html');
    expect(result).toBe(mockResponseText);
    expect(fetch).toHaveBeenCalledWith('https://example.com/assets/mfe1.html');
  });

  it('should throw an error if fetch fails with a non-ok response', async () => {
    (fetch as jest.Mock).mockResolvedValueOnce({ ok: false });

    await expect(fetchMicroFrontend('https://example.com/assets/mfe2.html')).rejects.toThrow(
      'Failed to fetch micro-frontend content from https://example.com/assets/mfe2.html'
    );
  });

  it('should throw an error if the URL is not allowed', async () => {
    await expect(fetchMicroFrontend('https://malicious.com/assets/mfe.html')).rejects.toThrow(
      'The URL https://malicious.com/assets/mfe.html is not allowed.'
    );
    expect(fetch).not.toHaveBeenCalled();
  });
});

describe('Transclude component', () => {
  beforeEach(() => {
    jest.resetAllMocks();
    (getConfig as jest.Mock).mockReturnValue({
      publicRuntimeConfig: {
        NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: 'https://example.com/assets/',
      },
    });
  });

  it('renders loading state initially if no initialMarkup is provided', () => {
    const { container } = render(<Transclude src="https://example.com/assets/mfe3.html" />);
    expect(container).toMatchSnapshot();
  });

  it('does not attempt to fetch if src changes but initialMarkup is defined', async () => {
    const { container } = render(
      <Transclude
        src="https://example.com/assets/mfe7.html"
        initialMarkup="<p data-testid='test-page'>Initial content</p>"
      />
    );

    expect(container).toMatchSnapshot();
    // Still no fetch call since initialMarkup is present
    expect(fetch).not.toHaveBeenCalled();
  });
});
