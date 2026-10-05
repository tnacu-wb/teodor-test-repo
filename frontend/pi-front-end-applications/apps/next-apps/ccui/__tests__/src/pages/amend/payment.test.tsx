import { getI18nLabels, getServerSideCustomLocale } from '@whitbread-eos/utils';

import { createAmendPaymentCCUIDataLoaderFn } from '~page-helper/amend/payment';
import PaymentPage, { getServerSideProps } from '~pages/amend/payment';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';

import { auth0 } from '../../../../src/lib/auth0';
import { getProxyOptions } from '../../../../src/utils/proxyOptions';

const mockUseFeatureSwitch = jest.fn();

jest.mock('../../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/amend/payment', () => ({
  createAmendPaymentCCUIDataLoaderFn: jest.fn(),
  Page: jest.fn(() => <div data-testid="PaymentPageAmend" />),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: jest.fn(),
  isFeatureFlagEnabled: jest.fn(() => true),
  useFeatureSwitch: jest.fn(() => mockUseFeatureSwitch()),
  getI18nLabels: jest.fn(),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(),
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="ErrorBoundary">{children}</div>
  ),
}));

jest.mock('~components', () => ({
  PaymentLayout: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="PaymentLayout">{children}</div>
  ),
}));

jest.mock('../../../../src/utils/proxyOptions', () => ({
  getProxyOptions: jest.fn(),
  setProxyOptionsCookies: jest.fn(),
}));

describe('Amend Payment page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createAmendPaymentCCUIDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'gb', country: '' });
  });

  describe('Component', () => {
    it('should render PaymentPageAmend', () => {
      const { container } = render(<PaymentPage user={{}} />);
      expect(container).toMatchSnapshot();
    });
  });

  describe('getLayout', () => {
    it('should wrap page with PaymentLayout and ErrorBoundary', () => {
      const { container } = render(PaymentPage.getLayout(<PaymentPage user={{}} />));
      expect(container).toMatchSnapshot();
    });
  });

  describe('getServerSideProps', () => {
    const mockContext = {
      locale: 'gb',
      query: { key: 'test' },
      res: {},
      req: { headers: { host: '', connection: '' } },
      resolvedUrl: '/amend/payment',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Famend%2Fpayment',
            permanent: false,
          },
        });
      });

      it('should not call data loader when session is null', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        await getServerSideProps(mockContext as any);

        expect(createAmendPaymentCCUIDataLoaderFn).not.toHaveBeenCalled();
      });
    });
  });
});
