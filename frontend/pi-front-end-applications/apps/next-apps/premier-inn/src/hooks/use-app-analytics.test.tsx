import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';

import { useAppAnalytics } from './use-app-analytics';

const mockAnalyticsUpdate = jest.fn();
const mockSetPageAnalytics = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: (...args: unknown[]) => mockAnalyticsUpdate(...args),
  },
  setPageAnalytics: (...args: unknown[]) => mockSetPageAnalytics(...args),
}));

jest.mock('date-fns', () => ({
  ...jest.requireActual('date-fns'),
  format: () => '14:30',
}));

// Test component that uses the hook
function TestComponent({ language, pathname }: { language: string; pathname: string }) {
  useAppAnalytics({ language, pathname });
  return <div data-testid="analytics-consumer">Analytics hook active</div>;
}

describe('useAppAnalytics', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Setup window.location
    Object.defineProperty(window, 'location', {
      value: {
        href: 'https://www.premierinn.com/gb/en/home.html',
      },
      writable: true,
    });
  });

  describe('Language analytics update', () => {
    it('should update analytics with language on mount', async () => {
      render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          language: 'en',
        });
      });
    });

    it('should update analytics when language changes', async () => {
      const { rerender } = render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          language: 'en',
        });
      });

      mockAnalyticsUpdate.mockClear();

      rerender(<TestComponent language="de" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          language: 'de',
        });
      });
    });
  });

  describe('Page name and type analytics', () => {
    it('should set analytics page name and type on mount', async () => {
      render(<TestComponent language="en" pathname="/account/dashboard" />);

      await waitFor(() => {
        expect(mockSetPageAnalytics).toHaveBeenCalledWith(
          '/account/dashboard',
          'PI',
          undefined,
          'en'
        );
      });
    });

    it('should update page name and type when pathname changes', async () => {
      const { rerender } = render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockSetPageAnalytics).toHaveBeenCalledWith('/home', 'PI', undefined, 'en');
      });

      mockSetPageAnalytics.mockClear();

      rerender(<TestComponent language="en" pathname="/search-results" />);

      await waitFor(() => {
        expect(mockSetPageAnalytics).toHaveBeenCalledWith('/search-results', 'PI', undefined, 'en');
      });
    });
  });

  describe('Time and currency analytics', () => {
    it('should update analytics with current time and GBP currency for English', async () => {
      render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          currentTime: '14:30',
          currencyCode: 'gbp',
        });
      });
    });

    it('should update analytics with EUR currency for non-English languages', async () => {
      render(<TestComponent language="de" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          currentTime: '14:30',
          currencyCode: 'eur',
        });
      });
    });
  });

  describe('Page URL analytics', () => {
    it('should update analytics with page URL when window is defined', async () => {
      render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          pageURL: 'https://www.premierinn.com/gb/en/home.html',
        });
      });
    });

    it('should update page URL when pathname changes', async () => {
      const { rerender } = render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          pageURL: 'https://www.premierinn.com/gb/en/home.html',
        });
      });

      Object.defineProperty(window, 'location', {
        value: { href: 'https://www.premierinn.com/gb/en/search.html' },
        writable: true,
      });

      mockAnalyticsUpdate.mockClear();

      rerender(<TestComponent language="en" pathname="/search" />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          pageURL: 'https://www.premierinn.com/gb/en/search.html',
        });
      });
    });
  });

  describe('Multiple analytics updates', () => {
    it('should call analytics.update multiple times for different data on mount', async () => {
      render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        // Should be called for: language, time/currency, pageURL
        expect(mockAnalyticsUpdate).toHaveBeenCalledTimes(3);
      });
    });

    it('should call setPageAnalytics once on mount', async () => {
      render(<TestComponent language="en" pathname="/home" />);

      await waitFor(() => {
        expect(mockSetPageAnalytics).toHaveBeenCalledTimes(1);
      });
    });
  });
});
