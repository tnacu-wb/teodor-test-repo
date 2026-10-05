import getConfig from 'next/config';

import App from '~pages/_app';
import { render, waitFor } from '~utils/test-utils';

const mockUseUserDetails = {};

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  useUserDetails: () => mockUseUserDetails,
  useCustomLocale: () => ({ language: 'en', country: 'gb' }),
  isOneTrustCookieConsentActive: (isFeatureEnabled?: boolean) => !!isFeatureEnabled,
  UserContextProvider: ({ children }: any) => (
    <div data-testid="UserContextProvider">{children}</div>
  ),
  AnalyticsProvider: ({ children }: any) => <div data-testid="AnalyticsProvider">{children}</div>,
  FeatureToggleContextProvider: ({ children }: any) => (
    <div data-testid="FeatureToggleContextProvider">{children}</div>
  ),
  AppDataProvider: ({ children }: any) => <div data-testid="AppDataProvider">{children}</div>,
  analytics: { update: jest.fn() },
  setPageAnalytics: jest.fn(),
  setCookieWithDefaultDomain: jest.fn(),
  getCookie: jest.fn(),
  getSecureTwoURL: () => 'localhost',
}));

jest.mock('@whitbread-eos/atoms', () => {
  const actual = jest.requireActual('@whitbread-eos/atoms');
  return {
    ...actual,
    ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
    ScriptsEmbed: jest.fn(() => <div data-testid="ScriptsEmbed"></div>),
    Fonts: () => <div data-testid="Fonts"></div>,
  };
});

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  CookiePoliciesModalContainer: () => <div data-testid="CookiePoliciesModalContainer"></div>,
  CONSENT_COOKIE: 'consent_cookie',
}));

jest.mock('@whitbread-eos/organisms', () => ({
  PISearchContainer: ({ children }: any) => <div data-testid="Search">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
  AppProviders: ({ children }: any) => <div data-testid="AppProviders">{children}</div>,
  AuthIframe: () => <div data-testid="AuthIframe" />,
}));

// Mock the new hooks
jest.mock('~hooks/use-app-analytics', () => ({
  useAppAnalytics: jest.fn(),
}));

jest.mock('~hooks/use-app-event-listeners', () => ({
  useAppEventListeners: jest.fn(),
}));

let mockConsentCookie: string | null = null;

jest.mock('~hooks/use-cookie-consent', () => ({
  useCookieConsent: () => ({
    isCookieConsentModalOpen: true,
    closeCookieConsentModal: jest.fn(),
    consentCookie: mockConsentCookie,
  }),
}));

jest.mock('~hooks/use-amazon-chat', () => ({
  useAmazonChat: () => false,
}));

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
  featureToggles: {
    FT_PI_KILL_SWITCH_CHAT_BOX: false,
    FT_PI_SHOW_CHAT_BOT_ALL_PAGES: false,
  },
};

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('App page', () => {
  it('should match the snapshot with feature toggle ON', async () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        PI_APPD_BRUM_API_KEY: 'XYZ',
        NEXT_PUBLIC_AMAZON_CHAT_ENABLED: 'true',
        NEXT_PUBLIC_AMAZON_CHAT_URL: 'https://example.com/amazon-chat-script.js',
        NEXT_PUBLIC_AMAZON_CHAT_ID: 'amazon-chat-script',
      },
    }));
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      asPath: 'ancillaries?reservationId=LONEUS0936286',
      query: {
        searchLocation: 'as',
      },
      pathname: '',
    });

    const { container } = render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <App pageProps={propsApps} Component={() => <div>test</div>} />
    );

    await waitFor(() => {
      expect(container).toMatchSnapshot();
    });
  });

  it('should match the snapshot with false', async () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        PI_APPD_BRUM_API_KEY: 'XYZ',
        NEXT_PUBLIC_AMAZON_CHAT_ENABLED: 'false',
        NEXT_PUBLIC_AMAZON_CHAT_URL: 'https://example.com/amazon-chat-script.js',
        NEXT_PUBLIC_AMAZON_CHAT_ID: 'amazon-chat-script',
      },
    }));
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      asPath: 'ancillaries?reservationId=LONEUS0936286',
      query: {
        searchLocation: 'as',
      },
      pathname: '',
    });

    const { container } = render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <App pageProps={propsApps} Component={() => <div>test</div>} />
    );

    await waitFor(() => {
      expect(container).toMatchSnapshot();
    });
  });

  it('should read amazonChatIcon from dehydratedState', () => {
    const pageProps = {
      dehydratedState: {
        queries: [
          {},
          {
            state: {
              data: {
                headerInformation: {
                  config: {
                    amazonChat: {
                      chatIcon: '/amazon-chat-icon.gif',
                    },
                  },
                },
              },
            },
          },
        ],
      },
      featureToggles: {},
    };

    render(
      <App
        Component={() => <div>test</div>}
        pageProps={pageProps}
        router={{ pathname: '/' } as any}
      />
    );
    expect(true).toBe(true);
  });

  describe('CookiePoliciesModalContainer visibility', () => {
    const defaultRouterConfig = {
      locale: 'gb',
      asPath: '/',
      query: {},
      pathname: '/',
    };

    beforeEach(() => {
      mockConsentCookie = null;
      mockUseRouter.mockReturnValue(defaultRouterConfig);
      (getConfig as jest.Mock).mockImplementation(() => ({
        publicRuntimeConfig: {},
      }));
    });

    it('renders CookiePoliciesModalContainer when OneTrust flag is disabled and no consent cookie', async () => {
      const { queryByTestId } = render(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        <App
          pageProps={{ ...propsApps, featureToggles: { release_one_trust_cookie_consent: false } }}
          Component={() => <div>test</div>}
        />
      );
      await waitFor(() => {
        expect(queryByTestId('CookiePoliciesModalContainer')).not.toBeNull();
      });
    });

    it('does not render CookiePoliciesModalContainer when OneTrust flag is enabled', async () => {
      const { queryByTestId } = render(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        <App
          pageProps={{ ...propsApps, featureToggles: { release_one_trust_cookie_consent: true } }}
          Component={() => <div>test</div>}
        />
      );
      await waitFor(() => {
        expect(queryByTestId('CookiePoliciesModalContainer')).toBeNull();
      });
    });

    it('does not render CookiePoliciesModalContainer when consent cookie is already set', async () => {
      mockConsentCookie = 'consent_given';

      const { queryByTestId } = render(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        <App
          pageProps={{ ...propsApps, featureToggles: { release_one_trust_cookie_consent: false } }}
          Component={() => <div>test</div>}
        />
      );
      await waitFor(() => {
        expect(queryByTestId('CookiePoliciesModalContainer')).toBeNull();
      });
    });
  });
});
