import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { ConfirmationStep } from '../types';
import Confirmation from './confirmation';

const mockAnalyticsUpdate = jest.fn();

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: ConfirmationStep.CONFIRMATION,
  steps: [
    {
      id: ConfirmationStep.CONFIRMATION,
      component: <Confirmation locale={LOCALES.EN} token="mockToken" />,
    },
  ],
};
const getDetailsFromTokenMock = jest.fn(() => ({
  isTravelManager: true,
  isBusinessPayManager: false,
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ alt, ...props }: any) => <img alt={alt} {...props} />,
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => {
    return '/';
  },
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
jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
    getDetailsFromToken: () => getDetailsFromTokenMock(),
    formatIBAssetsUrl: jest.fn((url) => url),
    useTranslation: jest.fn(() => ({ t: (key: string) => key })),
  };
});

describe('Confirmation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('Confirmation-wrapper')).toBeInTheDocument();

    expect(getByTestId('ApplyInnBusinessPayCard')).toBeInTheDocument();
    expect(getByTestId('TakeATourCard')).toBeInTheDocument();
    expect(getByTestId('AddACardCard')).toBeInTheDocument();
    expect(getByTestId('AddAnEmployeeCard')).toBeInTheDocument();

    expect(getByTestId('Confirmation-skip-link')).toBeInTheDocument();
  });

  it('tracks successful activation and updates the welcome funnel step', async () => {
    const trackMock = jest.fn();
    window._satellite = { track: trackMock };

    render(<Wizard {...mockProps} />);

    await waitFor(() => {
      expect(trackMock).toHaveBeenCalledWith('signUpCompleteEmailVerified');
      expect(mockAnalyticsUpdate).toHaveBeenCalledWith({
        funnel_step: 'Web:PB:UK:Confirm your Account - Welcome',
      });
    });
  });

  it('should render welcome for business pay manager', () => {
    getDetailsFromTokenMock.mockReturnValue({
      isTravelManager: false,
      isBusinessPayManager: true,
    });
    const { getByTestId, queryByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(getByTestId('Confirmation-wrapper')).toBeInTheDocument();

    expect(getByTestId('ApplyInnBusinessPayCard')).toBeInTheDocument();
    expect(getByTestId('TakeATourCard')).toBeInTheDocument();
    expect(queryByTestId('AddACardCard')).not.toBeInTheDocument();
    expect(getByTestId('AddAnEmployeeCard')).toBeInTheDocument();

    expect(getByTestId('Confirmation-skip-link')).toBeInTheDocument();
  });
});
