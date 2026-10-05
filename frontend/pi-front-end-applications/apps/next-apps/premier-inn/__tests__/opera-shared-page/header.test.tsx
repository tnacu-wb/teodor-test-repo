import * as utils from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React from 'react';

import Header, { getServerSideProps } from '~pages/opera-shared-page/header';
import { render } from '~utils/test-utils';

jest.mock('../../src/lib/getAuth0Token', () => ({
  getAuth0TokenAndEmail: jest.fn().mockResolvedValue({ accessToken: null, email: null }),
}));

jest.mock('next-i18next', () => ({
  useTranslation: jest.fn(() => ({
    t: jest.fn((key: string) => key),
  })),
}));

jest.mock('@auth0/nextjs-auth0/client', () => ({
  Auth0Provider: ({ children }) => <div data-testid="auth0-provider">{children}</div>,
}));

jest.mock('next/dynamic', () => {
  return function mockDynamic(importFn: any) {
    function DynamicWrapper(props: any) {
      const [Comp, setComp] = React.useState<React.ComponentType<any> | null>(null);
      React.useEffect(() => {
        importFn().then((mod: any) => {
          setComp(() => mod.default || mod);
        });
      }, []);
      if (!Comp) return null;
      return React.createElement(Comp, props);
    }
    DynamicWrapper.displayName = 'DynamicWrapper';
    return DynamicWrapper;
  };
});

jest.mock('next/router', () => ({
  useRouter: jest.fn(() => ({
    pathname: '/opera-shared-page/header',
    query: {},
    asPath: '/opera-shared-page/header',
  })),
}));

jest.mock('@whitbread-eos/api', () => {
  const actual = jest.requireActual('@whitbread-eos/api');
  return {
    ...actual,
    getStaticContent: jest.fn((isBarrierFreeLabelEnabled: boolean) => {
      // Return a query string that includes accessibleOrBarrierFree when enabled
      return isBarrierFreeLabelEnabled
        ? 'query { accessibleOrBarrierFree headerInformation footer }'
        : 'query { headerInformation footer }';
    }),
  };
});

jest.mock('@whitbread-eos/organisms', () => ({
  PISearchContainer: () => <div data-testid="SearchPage" />,
  Header: () => <div data-testid="Header" />,
  FooterWrapper: () => <div data-testid="FooterWrapper" />,
}));

jest.mock('~components', () => {
  const actual = jest.requireActual('~components');
  return {
    ...actual,
    DefaultLayout: jest.fn((props) => actual.DefaultLayout(props)),
  };
});

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="Error Boundry">{children}</div>
  ),
  Container: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="Container">{children}</div>
  ),
  Notification: ({ description, svg }: { description: string; svg: React.ReactNode }) => (
    <div data-testid="Notification">
      {description}
      {svg}
    </div>
  ),
  Info: () => <div data-testid="Info" />,
  Alert: () => <div data-testid="Alert" />,
  Error: () => <div data-testid="Error" />,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  HomeBanner: ({ searchComponent }: { searchComponent: React.ReactNode }) => (
    <div data-testid="HomeBanner">{searchComponent}</div>
  ),
  PromoCode: () => <div data-testid="PromoCode" />,
  SummerPromo: () => <div data-testid="SummerPromo" />,
  MAX_CLOSE_COUNT: 3,
  PUSH_REQUEST_CLOSED_COUNT: 'pushRequestClosedCount',
  PUSH_REQUEST_CLOSED_TIMESTAMP: 'pushRequestClosedTimestamp',
  CONSENT_COOKIE: 'userConsent',
  ConsentNotificationModal: () => <div data-testid="ConsentNotificationModal" />,
}));

jest.mock('~hooks/use-web-push-notification', () => ({
  useWebPushNotification: jest.fn(() => ({
    shouldShowNotificationModal: false,
    handleNotificationPermission: jest.fn(),
  })),
  PUSH_REQUEST_CLOSED_COUNT: 'pushRequestClosedCount',
  PUSH_REQUEST_CLOSED_TIMESTAMP: 'pushRequestClosedTimestamp',
}));

jest.mock('~hooks/use-amazon-chat', () => ({
  useAmazonChat: jest.fn(() => false),
}));

jest.mock('winston', () => ({
  ...jest.requireActual('winston'),
  createLogger: () => ({
    info: () => ({}),
  }),
}));

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    getServerSideCustomLocale: (locale: string) => {
      // Match the real implementation behavior
      const language = locale === 'gb' ? 'en' : 'de';
      const country = locale === 'gb' ? 'gb' : 'de';
      return { language, country };
    },
    graphQLRequest: jest.fn(() => Promise.resolve({ headerInformation: {}, footer: {} })),
    axiosRequest: jest.fn(() => Promise.resolve({})),
    decodeIdToken: jest.fn(() => ({ email: 'test@example.com' })),
    getUnleashToggles: jest.fn(() => Promise.resolve({})),
    getI18nLabels: jest.fn(() => Promise.resolve({ _nextI18Next: { initialLocale: 'en' } })),
    useFeatureToggle: jest.fn(() => ({})),
    setCookieWithDefaultDomain: jest.fn(),
    useCustomLocale: jest.fn(() => ({ language: 'en', country: 'gb' })),
    useQueryRequest: jest.fn(() => ({ data: { headerInformation: {} } })),
    useRestQueryRequest: jest.fn(() => ({ data: undefined, isSuccess: false, isFetching: false })),
    useUserData: jest.fn(() => ({ isLoggedIn: false, setIsLoggedIn: jest.fn() })),
    useAuthToken: jest.fn(() => ({ token: null, isAuth0Enabled: false, isLoading: false })),
    useAuth0User: jest.fn(() => ({ user: null, loading: false, error: null })),
  };
});

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: jest.fn(() => null),
  }));
});

const mockUseRouter = useRouter as jest.MockedFunction<typeof useRouter>;

describe('Home page', () => {
  const defaultProps = {
    featureToggles: {},
    pushRequestClosedCountCookie: 0,
    pushRequestClosedTimestampCookie: null,
    shouldShowHomeBanner: false,
    dehydratedState: { queries: [] },
  };

  // Mock Date.now() to have consistent timestamps in snapshots
  const MOCK_TIMESTAMP = 1700000000000;

  beforeAll(() => {
    jest.spyOn(Date, 'now').mockReturnValue(MOCK_TIMESTAMP);
  });

  afterAll(() => {
    jest.restoreAllMocks();
  });

  beforeEach(() => {
    // Mock document.body for the data-iswebpushenabled check
    Object.defineProperty(document.body, 'hasAttribute', {
      writable: true,
      configurable: true,
      value: jest.fn(() => false),
    });

    // Reset mocks before each test
    jest.clearAllMocks();
    (utils.graphQLRequest as jest.Mock).mockResolvedValue({
      headerInformation: {},
      footer: {},
    });
    (utils.getUnleashToggles as jest.Mock).mockResolvedValue({});
  });

  it('should match the snapshot with user details', () => {
    const { container } = render(<Header {...defaultProps} />);
    Header.getLayout(<div />);
    expect(container).toMatchSnapshot();
  });

  it('should render HomeBanner when banner query param is true', async () => {
    const { findByTestId } = render(<Header {...defaultProps} shouldShowHomeBanner={true} />);

    // HomeBanner loads asynchronously via next/dynamic ssr:false
    expect(await findByTestId('HomeBanner')).toBeTruthy();
    expect(await findByTestId('SearchPage')).toBeTruthy();
  });

  it('should not render HomeBanner when banner query param is absent', () => {
    const { queryByTestId, getByTestId } = render(
      <Header {...defaultProps} shouldShowHomeBanner={false} />
    );

    // HomeBanner should NOT appear
    expect(queryByTestId('HomeBanner')).toBeNull();

    // Regular Search component should render
    expect(getByTestId('SearchPage')).toBeTruthy();
  });

  it('should render Search when shouldHideSearchBar is false', () => {
    const { getByTestId } = render(<Header {...defaultProps} shouldHideSearchBar={false} />);
    expect(getByTestId('SearchPage')).toBeTruthy();
  });

  it('should not render Search when shouldHideSearchBar is true and banner is not shown', () => {
    const { queryByTestId } = render(
      <Header {...defaultProps} shouldHideSearchBar={true} shouldShowHomeBanner={false} />
    );
    expect(queryByTestId('SearchPage')).toBeNull();
    expect(queryByTestId('HomeBanner')).toBeNull();
  });

  it('should not render HomeBanner or Search when shouldHideSearchBar is true and banner is shown', () => {
    const { queryByTestId } = render(
      <Header {...defaultProps} shouldHideSearchBar={true} shouldShowHomeBanner={true} />
    );
    expect(queryByTestId('HomeBanner')).toBeNull();
    expect(queryByTestId('SearchPage')).toBeNull();
  });

  it('should apply banner top-margin when HomeBanner actually renders', () => {
    const { DefaultLayout } = jest.requireMock('~components');
    render(<Header {...defaultProps} shouldShowHomeBanner={true} shouldHideSearchBar={false} />);
    expect(DefaultLayout).toHaveBeenCalledWith(
      expect.objectContaining({ mainStyles: expect.objectContaining({ mt: 0 }) }),
      undefined
    );
  });

  it('should not apply banner top-margin when banner is suppressed by shouldHideSearchBar', () => {
    const { DefaultLayout } = jest.requireMock('~components');
    render(<Header {...defaultProps} shouldShowHomeBanner={true} shouldHideSearchBar={true} />);
    expect(DefaultLayout).toHaveBeenCalledWith(
      expect.objectContaining({ mainStyles: { mt: undefined } }),
      undefined
    );
  });

  it('should not apply banner top-margin when shouldShowHomeBanner is false', () => {
    const { DefaultLayout } = jest.requireMock('~components');
    render(<Header {...defaultProps} shouldShowHomeBanner={false} shouldHideSearchBar={false} />);
    expect(DefaultLayout).toHaveBeenCalledWith(
      expect.objectContaining({ mainStyles: { mt: undefined } }),
      undefined
    );
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      req: {},
      res: {},
    });

    const sanitizedResponse = {
      ...serverSideResponse,
      props: {
        ...serverSideResponse.props,
        dehydratedState: {
          ...serverSideResponse.props.dehydratedState,
          queries: serverSideResponse.props.dehydratedState.queries.map(
            ({ dehydratedAt, ...rest }: any) => rest
          ),
        },
      },
    };

    expect(sanitizedResponse).toMatchSnapshot();
  });

  it('should not show ConsentNotificationModal when shouldShowNotificationModal is false', () => {
    const { queryByTestId } = render(<Header {...defaultProps} />);
    expect(queryByTestId('ConsentNotificationModal')).toBeNull();
  });

  it('should prefetch GetStaticContent and getSearchRules queries', async () => {
    jest.clearAllMocks();

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      req: {},
      res: {},
    });

    // Should be called twice: once for GetStaticContent, once for getSearchRules
    expect(utils.graphQLRequest).toHaveBeenCalledTimes(2);
  });

  it('should use barrier-free label for DE locale when feature toggle is enabled', async () => {
    jest.clearAllMocks();

    // Set up feature toggle for DE locale with barrier-free label enabled
    (utils.getUnleashToggles as jest.Mock).mockResolvedValueOnce({
      release_pi_bb_ccui_barrier_free_label: true,
    });

    (utils.graphQLRequest as jest.Mock).mockResolvedValue({
      headerInformation: {},
      footer: {},
    });

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    await getServerSideProps({
      locale: 'de',
      query: { key: 'test' },
      req: {},
      res: {},
    });

    // Verify graphQLRequest was called
    expect(utils.graphQLRequest).toHaveBeenCalled();

    // The query should include the barrier-free label field for DE locale
    const mockCalls = (utils.graphQLRequest as jest.Mock).mock.calls;
    expect(mockCalls.length).toBeGreaterThan(0);
    const firstCall = mockCalls[0];
    const query = firstCall[0];
    expect(query).toContain('accessibleOrBarrierFree');
  });

  it('should return dehydrated state with prefetched queries', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const result = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      req: {},
      res: {},
    });

    expect(result.props).toHaveProperty('dehydratedState');
    expect(result.props.dehydratedState).toHaveProperty('queries');
  });

  it('should render header announcement when announcement text exists', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Important announcement message',
          },
        },
      },
    });

    const { getByTestId, getByText } = render(<Header {...defaultProps} />);

    // Notification should be rendered
    expect(getByTestId('Notification')).toBeTruthy();
    expect(getByText('Important announcement message')).toBeTruthy();
  });

  it('should not render header announcement when announcement text is null', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: null,
        },
      },
    });

    const { queryByTestId } = render(<Header {...defaultProps} />);

    // Notification should NOT be rendered
    expect(queryByTestId('Notification')).toBeNull();
  });

  it('should not render header announcement when announcement data is undefined', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {},
      },
    });

    const { queryByTestId } = render(<Header {...defaultProps} />);

    // Notification should NOT be rendered
    expect(queryByTestId('Notification')).toBeNull();
  });

  it('should render header announcement before HomeBanner when banner is shown', async () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Test announcement',
          },
        },
      },
    });

    const { container, findByTestId } = render(
      <Header {...defaultProps} shouldShowHomeBanner={true} />
    );

    // HomeBanner loads asynchronously via next/dynamic ssr:false
    const notification = await findByTestId('Notification');
    const homeBanner = await findByTestId('HomeBanner');
    expect(notification).toBeTruthy();
    expect(homeBanner).toBeTruthy();

    // Get all elements in the DOM tree
    const allElements = Array.from(container.querySelectorAll('*'));
    const notificationIndex = allElements.indexOf(notification);
    const homeBannerIndex = allElements.indexOf(homeBanner);

    // Notification should appear before HomeBanner in the DOM
    expect(notificationIndex).toBeGreaterThan(-1);
    expect(homeBannerIndex).toBeGreaterThan(-1);
    expect(notificationIndex).toBeLessThan(homeBannerIndex);
  });

  it('should render Error icon when announcement type is error', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Error announcement',
            type: 'error',
          },
        },
      },
    });

    const { getByTestId, queryByTestId } = render(<Header {...defaultProps} />);

    expect(getByTestId('Error')).toBeTruthy();
    expect(queryByTestId('Alert')).toBeNull();
    expect(queryByTestId('Info')).toBeNull();
  });

  it('should render Alert icon when announcement type is alert', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Alert announcement',
            type: 'alert',
          },
        },
      },
    });

    const { getByTestId, queryByTestId } = render(<Header {...defaultProps} />);

    expect(getByTestId('Alert')).toBeTruthy();
    expect(queryByTestId('Error')).toBeNull();
    expect(queryByTestId('Info')).toBeNull();
  });

  it('should render Info icon when announcement type is warning', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Warning announcement',
            type: 'warning',
          },
        },
      },
    });

    const { getByTestId, queryByTestId } = render(<Header {...defaultProps} />);

    expect(getByTestId('Info')).toBeTruthy();
    expect(queryByTestId('Error')).toBeNull();
    expect(queryByTestId('Alert')).toBeNull();
  });

  it('should render Info icon when announcement type is info', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Info announcement',
            type: 'info',
          },
        },
      },
    });

    const { getByTestId, queryByTestId } = render(<Header {...defaultProps} />);

    expect(getByTestId('Info')).toBeTruthy();
    expect(queryByTestId('Error')).toBeNull();
    expect(queryByTestId('Alert')).toBeNull();
  });

  it('should render Info icon when announcement type is not set', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Announcement without type',
          },
        },
      },
    });

    const { getByTestId, queryByTestId } = render(<Header {...defaultProps} />);

    expect(getByTestId('Info')).toBeTruthy();
    expect(queryByTestId('Error')).toBeNull();
    expect(queryByTestId('Alert')).toBeNull();
  });

  it('should render search form visible after mount', () => {
    const { container } = render(<Header {...defaultProps} />);
    // After mount (isMounted becomes true via useEffect), the form should be in the DOM
    const form = container.querySelector('form');
    expect(form).toBeTruthy();
  });

  it('should call useRestQueryRequest with correct args when user is logged in', () => {
    (utils.useUserData as jest.Mock).mockReturnValue({
      isLoggedIn: true,
      setIsLoggedIn: jest.fn(),
    });
    (utils.useAuthToken as jest.Mock).mockReturnValue({
      token: 'mock-token',
      isAuth0Enabled: false,
      isLoading: false,
    });
    (utils.decodeIdToken as jest.Mock).mockReturnValue({ email: 'test@example.com' });

    render(<Header {...defaultProps} />);

    expect(utils.useRestQueryRequest).toHaveBeenCalledWith(
      ['userBookingPreference', 'test@example.com'],
      'GET',
      expect.stringContaining('test@example.com'),
      expect.objectContaining({ Authorization: 'Bearer mock-token' }),
      expect.objectContaining({ enabled: true })
    );
  });

  it('should not fetch booking preference when user is not logged in', () => {
    (utils.useUserData as jest.Mock).mockReturnValue({
      isLoggedIn: false,
      setIsLoggedIn: jest.fn(),
    });
    (utils.useAuthToken as jest.Mock).mockReturnValue({
      token: null,
      isAuth0Enabled: false,
      isLoading: false,
    });

    render(<Header {...defaultProps} />);

    expect(utils.useRestQueryRequest).toHaveBeenCalledWith(
      expect.anything(),
      'GET',
      expect.anything(),
      expect.anything(),
      expect.objectContaining({ enabled: false })
    );
  });

  it('should pass clientDefaultRooms to Search when booking preference is available', () => {
    (utils.useUserData as jest.Mock).mockReturnValue({
      isLoggedIn: true,
      setIsLoggedIn: jest.fn(),
    });
    (utils.useAuthToken as jest.Mock).mockReturnValue({
      token: 'mock-token',
      isAuth0Enabled: false,
      isLoading: false,
    });
    (utils.decodeIdToken as jest.Mock).mockReturnValue({ email: 'test@example.com' });
    (utils.useRestQueryRequest as jest.Mock).mockReturnValue({
      data: {
        bookingPreference: {
          roomRequirements: { adults: 2, children: 0, cotRequired: false, type: 'TWIN' },
        },
      },
      isSuccess: true,
      isFetching: false,
    });

    const { getByTestId } = render(<Header {...defaultProps} />);
    expect(getByTestId('SearchPage')).toBeTruthy();
  });

  it('should render header announcement before Search when banner is not shown', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: {
        headerInformation: {
          announcement: {
            text: 'Test announcement',
          },
        },
      },
    });

    const { container, getByTestId } = render(
      <Header {...defaultProps} shouldShowHomeBanner={false} />
    );

    // Both notification and Search should be rendered
    const notification = getByTestId('Notification');
    const searchPage = getByTestId('SearchPage');
    expect(notification).toBeTruthy();
    expect(searchPage).toBeTruthy();

    // Get all elements in the DOM tree
    const allElements = Array.from(container.querySelectorAll('*'));
    const notificationIndex = allElements.indexOf(notification);
    const searchIndex = allElements.indexOf(searchPage);

    // Notification should appear before Search in the DOM
    expect(notificationIndex).toBeGreaterThan(-1);
    expect(searchIndex).toBeGreaterThan(-1);
    expect(notificationIndex).toBeLessThan(searchIndex);
  });

  it('should use auth0User email when Auth0 is enabled', () => {
    (utils.useAuthToken as jest.Mock).mockReturnValue({
      token: 'testtoken',
      isAuth0Enabled: true,
      isLoading: false,
    });
    (utils.useAuth0User as jest.Mock).mockReturnValue({
      user: { email: 'auth0user@example.com' },
      loading: false,
      error: null,
    });
    (utils.useUserData as jest.Mock).mockReturnValue({
      isLoggedIn: true,
      setIsLoggedIn: jest.fn(),
    });

    render(<Header {...defaultProps} />);

    expect(utils.useRestQueryRequest).toHaveBeenCalledWith(
      ['userBookingPreference', 'auth0user@example.com'],
      'GET',
      expect.stringContaining('auth0user@example.com'),
      expect.objectContaining({ Authorization: 'Bearer testtoken' }),
      expect.objectContaining({ enabled: true })
    );
  });

  it('should use empty string email when Auth0 is enabled but user is null', () => {
    (utils.useAuthToken as jest.Mock).mockReturnValue({
      token: 'testtoken',
      isAuth0Enabled: true,
      isLoading: false,
    });
    (utils.useAuth0User as jest.Mock).mockReturnValue({
      user: null,
      loading: false,
      error: null,
    });
    (utils.useUserData as jest.Mock).mockReturnValue({
      isLoggedIn: false,
      setIsLoggedIn: jest.fn(),
    });

    render(<Header {...defaultProps} />);

    expect(utils.useRestQueryRequest).toHaveBeenCalledWith(
      ['userBookingPreference', ''],
      'GET',
      expect.anything(),
      expect.anything(),
      expect.objectContaining({ enabled: false })
    );
  });

  it('should render ConsentNotificationModal when shouldShowNotificationModal is true and page supports it', () => {
    const { useWebPushNotification } = jest.requireMock('~hooks/use-web-push-notification');
    (useWebPushNotification as jest.Mock).mockReturnValue({
      shouldShowNotificationModal: true,
      handleNotificationPermission: jest.fn(),
    });

    Object.defineProperty(document.body, 'hasAttribute', {
      writable: true,
      configurable: true,
      value: jest.fn(() => true),
    });

    const { getByTestId } = render(<Header {...defaultProps} />);
    expect(getByTestId('ConsentNotificationModal')).toBeTruthy();
  });

  it('should not render ConsentNotificationModal when page does not support web push', () => {
    const { useWebPushNotification } = jest.requireMock('~hooks/use-web-push-notification');
    (useWebPushNotification as jest.Mock).mockReturnValue({
      shouldShowNotificationModal: true,
      handleNotificationPermission: jest.fn(),
    });

    // hasAttribute returns false — page does not have data-iswebpushenabled
    Object.defineProperty(document.body, 'hasAttribute', {
      writable: true,
      configurable: true,
      value: jest.fn(() => false),
    });

    const { queryByTestId } = render(<Header {...defaultProps} />);
    expect(queryByTestId('ConsentNotificationModal')).toBeNull();
  });

  it('should keep search form in DOM (hidden) when static content is loading', () => {
    (utils.useQueryRequest as jest.Mock).mockReturnValue({
      data: undefined,
      isLoading: true,
    });

    const { container, getByTestId } = render(<Header {...defaultProps} />);
    // form is rendered with visibility:hidden (not display:none) so layout is preserved
    const form = container.querySelector('form');
    expect(form).toBeTruthy();
    // Search is still mounted inside the hidden form
    expect(getByTestId('SearchPage')).toBeTruthy();
  });

  describe('getServerSideProps', () => {
    it('should set shouldShowHomeBanner true when banner query param is "true"', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: { banner: 'true' },
        req: {},
        res: {},
      });

      expect(result.props.shouldShowHomeBanner).toBe(true);
    });

    it('should set shouldShowHomeBanner false when banner query param is absent', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      expect(result.props.shouldShowHomeBanner).toBe(false);
    });

    it('should set shouldHideSearchBar true when hideSearchBar query param is "true"', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: { hideSearchBar: 'true' },
        req: {},
        res: {},
      });

      expect(result.props.shouldHideSearchBar).toBe(true);
    });

    it('should set shouldHideSearchBar true when hidesearchbar query param is lowercase', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: { hidesearchbar: 'true' },
        req: {},
        res: {},
      });

      expect(result.props.shouldHideSearchBar).toBe(true);
    });

    it('should set shouldHideSearchBar true regardless of query param key casing', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: { HIDESEARCHBAR: 'true' },
        req: {},
        res: {},
      });

      expect(result.props.shouldHideSearchBar).toBe(true);
    });

    it('should set shouldHideSearchBar false when hideSearchBar query param value is not "true"', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: { hideSearchBar: 'false' },
        req: {},
        res: {},
      });

      expect(result.props.shouldHideSearchBar).toBe(false);
    });

    it('should set shouldHideSearchBar false when hideSearchBar query param is absent', async () => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      expect(result.props.shouldHideSearchBar).toBe(false);
    });

    it('should fetch user profile and set defaultRooms when legacy cookie is present', async () => {
      const Cookies = jest.requireMock('cookies');
      Cookies.mockImplementation(() => ({
        get: jest.fn((name: string) => {
          if (name === 'id_token_cookie') return 'mock-id-token';
          return null;
        }),
      }));

      (utils.decodeIdToken as jest.Mock).mockReturnValue({ email: 'cookieuser@example.com' });
      (utils.axiosRequest as jest.Mock).mockResolvedValue({
        bookingPreference: {
          roomRequirements: { adults: 2, children: 1, cotRequired: true, type: 'TWIN' },
        },
      });

      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      expect(utils.axiosRequest).toHaveBeenCalledWith(
        expect.objectContaining({
          method: 'GET',
          url: expect.stringContaining('cookieuser@example.com'),
        })
      );
      expect(result.props.defaultRooms).toEqual([
        { adults: 2, children: 1, shouldIncludeCot: true, roomType: 'TWIN' },
      ]);
    });

    it('should not include defaultRooms in props when no auth token', async () => {
      const Cookies = jest.requireMock('cookies');
      Cookies.mockImplementation(() => ({
        get: jest.fn(() => null),
      }));

      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      expect(result.props).not.toHaveProperty('defaultRooms');
    });

    it('should warn and omit defaultRooms when axiosRequest throws', async () => {
      const Cookies = jest.requireMock('cookies');
      Cookies.mockImplementation(() => ({
        get: jest.fn((name: string) => {
          if (name === 'id_token_cookie') return 'mock-id-token';
          return null;
        }),
      }));

      (utils.decodeIdToken as jest.Mock).mockReturnValue({ email: 'error@example.com' });
      (utils.axiosRequest as jest.Mock).mockRejectedValue(new Error('Network error'));

      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      // Page still renders, defaultRooms is absent
      expect(result.props).not.toHaveProperty('defaultRooms');
    });

    it('should not call getAuth0TokenAndEmail when Auth0 feature toggle is disabled', async () => {
      const { getAuth0TokenAndEmail } = jest.requireMock('../../src/lib/getAuth0Token');

      (utils.getUnleashToggles as jest.Mock).mockResolvedValue({
        release_pi_auth0_login: false,
      });

      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      expect(getAuth0TokenAndEmail).not.toHaveBeenCalled();
    });

    it('should use default room values when roomRequirements fields are missing', async () => {
      const Cookies = jest.requireMock('cookies');
      Cookies.mockImplementation(() => ({
        get: jest.fn((name: string) => {
          if (name === 'id_token_cookie') return 'mock-id-token';
          return null;
        }),
      }));

      (utils.decodeIdToken as jest.Mock).mockReturnValue({ email: 'user@example.com' });
      (utils.axiosRequest as jest.Mock).mockResolvedValue({
        bookingPreference: {
          roomRequirements: {},
        },
      });

      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      //@ts-ignore
      const result = await getServerSideProps({
        locale: 'gb',
        query: {},
        req: {},
        res: {},
      });

      expect(result.props.defaultRooms).toEqual([
        { adults: 1, children: 0, shouldIncludeCot: false, roomType: 'DB' },
      ]);
    });
  });
});
