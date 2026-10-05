import '@testing-library/jest-dom';
import { FT_ONE_TRUST_COOKIE_CONSENT } from '@whitbread-eos/api';
import preloadAll from 'jest-next-dynamic';
import getConfig from 'next/config';

import App from '~pages/_app';
import { render } from '~utils/test-utils';

const mockUseUserDetails = {};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  useUserDetails: () => mockUseUserDetails,
  isOneTrustCookieConsentActive: (isFeatureEnabled?: boolean) => !!isFeatureEnabled,
  UserContextProvider: ({ children }: any) => (
    <div data-testid="UserContextProvider">{children}</div>
  ),
  AnalyticsProvider: ({ children }: any) => <div data-testid="AnalyticsProvider">{children}</div>,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  AuthGuard: ({ children }: any) => <div data-testid="AuthGuard">{children}</div>,
  BBSearchContainer: ({ children }: any) => <div data-testid="Search">{children}</div>,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  CookiePoliciesModalContainer: () => <div data-testid="CookiePoliciesModalContainer"></div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL = 'https://onetrust.test/otSDKStub.js';
process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB = 'test-domain-gb';
process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE = 'test-domain-de';

const propsApps = {
  _nextI18Next: {
    initialI18nStore: {
      gb: {
        common: {},
      },
    },
    initialLocale: 'gb',
    userConfig: null,
  },
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  hasRegisteredSuccessfully: false,
};

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));
const mockReplace = jest.fn();

describe('App page', () => {
  beforeAll(async () => {
    await preloadAll();
  });
  beforeEach(() => {
    mockReplace.mockClear();
  });
  it('should match the snapshot', () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        PI_APPD_BRUM_API_KEY: 'XYZ',
      },
    }));
    mockUseRouter.mockReturnValue({
      locale: 'en',
      asPath: 'ancillaries?reservationId=LONEUS0936286',
      query: {
        searchLocation: 'as',
      },
      replace: jest.fn(),
      pathname: '',
    });
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { container } = render(<App pageProps={propsApps} Component={() => <div>test</div>} />);
    expect(container).toMatchSnapshot();
  });

  it('renders CookiePoliciesModalContainer when consentCookie is not set', () => {
    mockUseRouter.mockReturnValue({
      pathname: '/some-path',
      locale: 'en',
      asPath: '/some-path',
      query: {},
      replace: jest.fn(),
    });

    // Remove consent cookie
    jest.spyOn(require('@whitbread-eos/utils'), 'getCookie').mockReturnValue(undefined);

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByTestId } = render(<App pageProps={propsApps} Component={() => <div>test</div>} />);
    expect(getByTestId('CookiePoliciesModalContainer')).toBeInTheDocument();
  });

  it('does not render CookiePoliciesModalContainer when consentCookie is set', () => {
    mockUseRouter.mockReturnValue({
      pathname: '/some-path',
      locale: 'en',
      asPath: '/some-path',
      query: {},
      replace: jest.fn(),
    });

    jest.spyOn(require('@whitbread-eos/utils'), 'getCookie').mockReturnValue('true');

    const { queryByTestId } = render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <App pageProps={propsApps} Component={() => <div>test</div>} />
    );
    expect(queryByTestId('CookiePoliciesModalContainer')).toBeNull();
  });

  it('renders ErrorBoundary and DefaultLayout', () => {
    mockUseRouter.mockReturnValue({
      pathname: '/some-path',
      locale: 'en',
      asPath: '/some-path',
      query: {},
      replace: jest.fn(),
    });

    jest.spyOn(require('@whitbread-eos/utils'), 'getCookie').mockReturnValue('true');

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByTestId } = render(<App pageProps={propsApps} Component={() => <div>test</div>} />);
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
    expect(getByTestId('DefaultLayout')).toBeInTheDocument();
  });

  it('renders AuthGuard', () => {
    mockUseRouter.mockReturnValue({
      pathname: '/some-path',
      locale: 'en',
      asPath: '/some-path',
      query: {},
      replace: jest.fn(),
    });

    jest.spyOn(require('@whitbread-eos/utils'), 'getCookie').mockReturnValue('true');

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByTestId } = render(<App pageProps={propsApps} Component={() => <div>test</div>} />);
    expect(getByTestId('AuthGuard')).toBeInTheDocument();
  });

  it('renders component directly for cookies page', () => {
    mockUseRouter.mockReturnValue({
      pathname: '/business-booker/cookies',
      locale: 'en',
      asPath: '/business-booker/cookies',
      query: {},
      replace: jest.fn(),
    });

    const TestComponent = () => <div data-testid="TestComponent">test</div>;

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { getByTestId } = render(<App pageProps={propsApps} Component={TestComponent} />);
    expect(getByTestId('TestComponent')).toBeInTheDocument();
  });

  it('redirects when window.location.origin includes NEXT_PUBLIC_ASSETS_URL', async () => {
    mockUseRouter.mockReturnValue({
      pathname: '/business-booker/',
      replace: mockReplace,
    });
    Object.defineProperty(window, 'location', {
      value: {
        origin: 'https://www.uat.premierinn.digital/',
        hostname: 'www.uat.premierinn.digital',
      },
      writable: true,
    });
    process.env.NEXT_PUBLIC_ASSETS_URL = 'www.uat.premierinn.digital';
    process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL = 'https://business.uat.premierinn.digital';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    render(<App Component={() => <div>test</div>} pageProps={{}} />);

    expect(mockReplace).toHaveBeenCalledWith(process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL);
  });

  it('does not render CookiePoliciesModalContainer when OneTrust feature flag is enabled', () => {
    mockUseRouter.mockReturnValue({
      pathname: '/some-path',
      locale: 'en',
      asPath: '/some-path',
      query: {},
      replace: jest.fn(),
    });

    jest.spyOn(require('@whitbread-eos/utils'), 'getCookie').mockReturnValue(undefined);

    const propsWithOneTrust = {
      ...propsApps,
      featureToggles: { [FT_ONE_TRUST_COOKIE_CONSENT]: true },
    };

    const { queryByTestId } = render(
      <App pageProps={propsWithOneTrust} Component={(() => <div>test</div>) as any} />
    );
    expect(queryByTestId('CookiePoliciesModalContainer')).toBeNull();
  });

  it('does not redirect if origin does not include NEXT_PUBLIC_ASSETS_URL', async () => {
    mockUseRouter.mockReturnValue({
      pathname: '/business-booker/',
      replace: mockReplace,
    });
    Object.defineProperty(window, 'location', {
      value: {
        origin: 'https://business.uat.premierinn.digital',
        hostname: 'business.uat.premierinn.digital',
      },
      writable: true,
    });
    process.env.NEXT_PUBLIC_ASSETS_URL = 'www.uat.premierinn.digital';
    process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL = 'https://business.uat.premierinn.digital';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    render(<App Component={() => <div>test</div>} pageProps={{}} />);

    expect(mockReplace).not.toHaveBeenCalled();
  });
});
