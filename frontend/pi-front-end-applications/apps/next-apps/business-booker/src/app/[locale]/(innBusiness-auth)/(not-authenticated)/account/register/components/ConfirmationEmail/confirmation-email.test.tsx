import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import ConfirmationEmail from './confirmation-email';

const mockAnalyticsUpdate = jest.fn();

const mockProps = {
  icons: {},
  header: null,
  initialState: {
    address: 'test',
  },
  initialStepId: 'CONFIRMATION_EMAIL',
  steps: [
    {
      id: 'CONFIRMATION_EMAIL',
      component: <ConfirmationEmail locale={LOCALES.EN} />,
    },
  ],
};

const resendActivationMock = jest.fn().mockReturnValue('success');

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
    getPathForLocale: jest.fn((locale) => locale),
    useTranslation: jest.fn(() => ({ t: (str: string) => str })),
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: (locale: string) => locale,
    getCountryLanguageByLocale: (locale: string) => {
      return locale;
    },
    resendActivationEmail: () => resendActivationMock(),
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
  useSearchParams: () => {
    return {
      get: () => '',
    };
  },
}));

describe('ConfirmationEmail', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('ConfirmationEmail-wrapper')).toBeInTheDocument();
  });

  it('updates analytics with the confirm email funnel step', async () => {
    render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        funnel_step: 'Web:PB:UK:Create Account - Confirm Email',
      });
    });
  });

  it('should not show any alert', () => {
    const { queryByTestId } = render(<Wizard {...mockProps} />);

    expect(queryByTestId('ConfirmationEmail-success-alert')).not.toBeInTheDocument();
    expect(queryByTestId('ConfirmationEmail-error-alert')).not.toBeInTheDocument();
  });

  it('should call resendActivationEmail on button click', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      getByTestId('ConfirmationEmail-resend').click();
    });

    expect(resendActivationMock).toHaveBeenCalled();
  });

  it('should show success alert', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      getByTestId('ConfirmationEmail-resend').click();
    });

    await waitFor(() => {
      expect(getByTestId('ConfirmationEmail-success-alert')).toBeInTheDocument();
    });
  });

  it('should show error alert', async () => {
    resendActivationMock.mockReturnValue(null);

    const { getByTestId } = render(<Wizard {...mockProps} />);

    await waitFor(() => {
      getByTestId('ConfirmationEmail-resend').click();
    });

    await waitFor(() => {
      expect(getByTestId('ConfirmationEmail-red-alert')).toBeInTheDocument();
    });
  });
});
