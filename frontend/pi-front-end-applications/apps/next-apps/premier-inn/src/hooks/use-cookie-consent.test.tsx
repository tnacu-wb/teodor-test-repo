import '@testing-library/jest-dom';
import { CONSENT_COOKIE } from '@whitbread-eos/molecules';

import { act, render, screen, waitFor } from '~utils/test-utils';

import { useCookieConsent } from './use-cookie-consent';

const mockGetCookie = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getCookie: (...args: unknown[]) => mockGetCookie(...args),
}));

// Test component that uses the hook and exposes state
function TestComponent({
  onStateChange,
}: {
  onStateChange?: (state: ReturnType<typeof useCookieConsent>) => void;
}) {
  const state = useCookieConsent();

  // Call onStateChange when state changes (for testing)
  if (onStateChange) {
    onStateChange(state);
  }

  return (
    <div>
      <div data-testid="modal-open">{state.isCookieConsentModalOpen ? 'open' : 'closed'}</div>
      <div data-testid="consent-cookie">{state.consentCookie ?? 'null'}</div>
      <button data-testid="close-button" onClick={state.closeCookieConsentModal}>
        Close Modal
      </button>
    </div>
  );
}

describe('useCookieConsent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetCookie.mockReturnValue(null);
    // Reset data-consent attribute
    document.documentElement.removeAttribute('data-consent');
  });

  afterEach(() => {
    document.documentElement.removeAttribute('data-consent');
  });

  describe('Initial state', () => {
    it('should start with modal open by default', async () => {
      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('open');
      });
    });

    it('should call getCookie with CONSENT_COOKIE', () => {
      render(<TestComponent />);

      expect(mockGetCookie).toHaveBeenCalledWith(CONSENT_COOKIE);
    });

    it('should return consentCookie value from getCookie', async () => {
      mockGetCookie.mockReturnValue('accepted');

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('consent-cookie')).toHaveTextContent('accepted');
      });
    });

    it('should return null for consentCookie when no cookie exists', async () => {
      mockGetCookie.mockReturnValue(null);

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('consent-cookie')).toHaveTextContent('null');
      });
    });
  });

  describe('Modal state based on data-consent attribute', () => {
    it('should close modal when data-consent is "true"', async () => {
      document.documentElement.setAttribute('data-consent', 'true');

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('closed');
      });
    });

    it('should keep modal open when data-consent is "false"', async () => {
      document.documentElement.setAttribute('data-consent', 'false');

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('open');
      });
    });

    it('should keep modal open when data-consent attribute is missing', async () => {
      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('open');
      });
    });

    it('should keep modal open when data-consent has an unexpected value', async () => {
      document.documentElement.setAttribute('data-consent', 'invalid');

      render(<TestComponent />);

      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('open');
      });
    });
  });

  describe('closeCookieConsentModal function', () => {
    it('should close the modal when called', async () => {
      render(<TestComponent />);

      expect(screen.getByTestId('modal-open')).toHaveTextContent('open');

      await act(async () => {
        screen.getByTestId('close-button').click();
      });

      expect(screen.getByTestId('modal-open')).toHaveTextContent('closed');
    });

    it('should remain closed after being called multiple times', async () => {
      render(<TestComponent />);

      await act(async () => {
        screen.getByTestId('close-button').click();
      });

      await act(async () => {
        screen.getByTestId('close-button').click();
      });

      expect(screen.getByTestId('modal-open')).toHaveTextContent('closed');
    });
  });

  describe('Re-render stability', () => {
    it('should maintain state after re-render', async () => {
      const { rerender } = render(<TestComponent />);

      await act(async () => {
        screen.getByTestId('close-button').click();
      });

      rerender(<TestComponent />);

      expect(screen.getByTestId('modal-open')).toHaveTextContent('closed');
    });
  });

  describe('Edge cases', () => {
    it('should handle data-consent with whitespace', async () => {
      document.documentElement.setAttribute('data-consent', ' true ');

      render(<TestComponent />);

      // Strict comparison means whitespace will not match 'true'
      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('open');
      });
    });

    it('should handle case sensitivity in data-consent', async () => {
      document.documentElement.setAttribute('data-consent', 'TRUE');

      render(<TestComponent />);

      // Strict comparison means 'TRUE' will not match 'true'
      await waitFor(() => {
        expect(screen.getByTestId('modal-open')).toHaveTextContent('open');
      });
    });
  });
});
