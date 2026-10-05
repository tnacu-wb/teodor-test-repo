import '@testing-library/jest-dom';
import { render, fireEvent } from '@testing-library/react';
import { waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import AccountExistsNoManager from './account-exists-no-manager';

const mockAnalyticsUpdate = jest.fn();

const mockProps = {
  icons: {},
  header: null,
  initialState: {
    address: 'test',
  },
  initialStepId: 'ACCOUNT_EXISTS_NO_MANAGER',
  steps: [
    {
      id: 'ACCOUNT_EXISTS_NO_MANAGER',
      component: <AccountExistsNoManager locale={LOCALES.EN} />,
    },
  ],
};

const mockPush = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: mockPush,
  }),
}));

jest.mock('@whitbread-eos/utils', () => {
  const actualUtils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...actualUtils,
    analytics: {
      update: (...args: any[]) => mockAnalyticsUpdate(...args),
    },
    formatAnalyticsFunnelStep: jest.fn((siteType, page, language) => {
      const country = language === 'de' ? 'DE' : 'UK';

      return `Web:${siteType === 'PIB' ? 'PB' : siteType}:${country}:${page}`;
    }),
    getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
    useTranslation: jest.fn(() => ({ t: (key: string) => key })),
  };
});

describe('AccountExistsNoManager', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('AccountExistsNoManager-wrapper')).toBeInTheDocument();
  });

  describe('AccountExistsNoManager', () => {
    it('should render the component', () => {
      const { getByTestId } = render(<Wizard {...mockProps} />);

      expect(getByTestId('wizard-page')).toBeInTheDocument();
      expect(getByTestId('AccountExistsNoManager-wrapper')).toBeInTheDocument();
    });

    it('should render the description', () => {
      const { getByTestId } = render(<Wizard {...mockProps} />);

      expect(getByTestId('AccountExistsNoManager-description')).toBeInTheDocument();
    });

    it('should render the button', () => {
      const { getByTestId } = render(<Wizard {...mockProps} />);

      expect(getByTestId('AccountExistsNoManager-Button')).toBeInTheDocument();
    });

    it('should navigate to login page when button is clicked', async () => {
      const { getByTestId } = render(<Wizard {...mockProps} />);
      const button = getByTestId('AccountExistsNoManager-Button');

      expect(button).toBeEnabled();
      await waitFor(() => {
        fireEvent.click(button);
      });

      expect(mockPush).toHaveBeenCalledWith('/en-gb/account/login');
    });

    it('updates analytics with the existing company funnel step', async () => {
      render(<Wizard {...mockProps} />);

      await waitFor(() => {
        expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
          funnel_step: 'Web:PB:UK:Create Account - Existing Company',
        });
      });
    });

    it('should call analytics track on mount', () => {
      const trackMock = jest.fn();
      global.window = Object.create(window);
      window._satellite = { track: trackMock };

      render(<Wizard {...mockProps} />);
      expect(trackMock).not.toHaveBeenCalledWith('signUpExistingCompany');
    });
  });
});
