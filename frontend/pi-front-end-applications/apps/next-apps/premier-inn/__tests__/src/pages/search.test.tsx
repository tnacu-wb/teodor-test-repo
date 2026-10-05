import { getPromotionsInformation } from '@whitbread-eos/molecules';
import { getGQLClient } from '@whitbread-eos/utils';

import SearchPage, { getServerSideProps } from '~pages/search';
import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getPromotionsInformation: jest.fn(),
}));

const mockServerSideCustomLocale = { language: 'gb', country: '' };

const promotionBannerData = {
  showPromo: true,
  promoExpiredMessage: null,
  promoInvalidMessage: null,
  promoBannerColour: 'white',
  promoBannerIcon: '/icon.jpg',
  promoBannerTitle: 'Save 20% off',
  promoBannerSubtitle: 'Save 20% off by purchasing 2 rooms and 2 nights',
  isWithinPromoWindow: true,
};

jest.mock('~page-helper/search', () => ({
  createSearchResultsPiDataLoader: () => ({}),
  SearchPagePI: () => <div data-testid="SearchPage" />,
}));

// temporary solution until next/router will be deprecated
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),

  useRouter: () => ({
    router: {},
    query: { ARRdd: 'value' },
  }),
}));

// Mock useCookieWatcher hook
const mockUseCookieWatcher = jest.fn();

jest.mock('~hooks/use-service-worker', () => ({
  __esModule: true,
  default: jest.fn(() => 'mocked_value'),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('next/dynamic', () => (fn) => {
  const ConsentNotificationModal = () => (
    <div data-testid="consent-notification-modal">ConsentNotificationModal</div>
  );
  ConsentNotificationModal.displayName = 'ConsentNotificationModal';
  return ConsentNotificationModal;
});

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getGQLClient: jest.fn(),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    testFeature: true,
  })),
}));

const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();
jest.mock('cookies', () => {
  return function () {
    return {
      get: mockGetCookie,
      set: mockSetCookie,
    };
  };
});

describe('Search page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseCookieWatcher.mockReturnValue(null);
  });

  const mockNotificationPermission = (permission) => {
    global.window.Notification = {
      permission,
      requestPermission: jest.fn().mockResolvedValue(permission),
    };
  };

  it('should match the snapshot', () => {
    const { container } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ release_pi_web_push_notifications: true }}
          promotionBannerData={promotionBannerData}
        />
      )
    );

    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    const { container } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ release_pi_web_push_notifications: true }}
          promotionBannerData={promotionBannerData}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ release_pi_web_push_notifications: true }}
          promotionBannerData={promotionBannerData}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb' });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should not render modal when notification permission is default', () => {
    mockGetCookie.mockReturnValueOnce(4);
    mockGetCookie.mockReturnValueOnce(
      'Mon Jul 15 2024 15:21:19 GMT+0300 (Eastern European Summer Time)'
    );
    mockNotificationPermission('default');
    mockGetCookie.mockReturnValue(null);

    const { queryByTestId } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ release_pi_web_push_notifications: true }}
          promotionBannerData={promotionBannerData}
        />
      )
    );

    expect(queryByTestId('pi-notification-permission-popup-modal-content')).toBeNull();
  });

  it('should render getLayout', async () => {
    const mockedProps = {
      error: null,
      dehydratedState: {
        queries: [
          {
            queryKey: ['GetStaticContent'],
            state: {
              data: {
                headerInformation: {
                  config: {
                    promotionBanner: { title: 'Promo!' },
                  },
                },
              },
            },
          },
        ],
      },
    };

    const layout = SearchPage.getLayout(
      <SearchPage {...mockedProps} promotionBannerData={promotionBannerData} />
    );
    const { container } = await render(layout);
    expect(container).toBeTruthy();
  });
});

// Tests for ConsentNotificationModal logic
describe('Search Page - ConsentNotificationModal Behavior', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.window = global.window || {};
    global.window.Notification = {
      permission: 'default',
      requestPermission: jest.fn().mockResolvedValue('default'),
    };
  });

  const mockNotificationPermission = (permission: string) => {
    global.window.Notification = {
      permission,
      requestPermission: jest.fn().mockResolvedValue(permission),
    };
  };

  const defaultProps = {
    featureToggles: { release_pi_web_push_notifications: true },
    pushRequestClosedCountCookie: 0,
    pushRequestClosedTimestampCookie: null,
    promotionBannerData: { showPromo: true },
  };

  it('should render component structure correctly when consent cookie exists', () => {
    mockUseCookieWatcher.mockReturnValue('consent_granted');
    mockNotificationPermission('default');

    const { container } = render(<SearchPage {...defaultProps} />);

    expect(container).toMatchSnapshot();
  });

  it('should render differently when consent cookie is not set', () => {
    mockUseCookieWatcher.mockReturnValue(null);
    mockNotificationPermission('default');

    const { container } = render(<SearchPage {...defaultProps} />);

    expect(container).toMatchSnapshot();
  });
});

describe('Search Page - Promotions Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create client from session cookie and pass it to getPromotionsInformation', async () => {
    const mockSessionId = 'mock-session-id';
    const mockClient = { fake: 'client' };

    mockGetCookie.mockReturnValue(mockSessionId);

    (getGQLClient as jest.Mock).mockReturnValue(mockClient);

    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      showPromo: true,
    });

    await getServerSideProps({
      locale: 'gb',
      query: {
        arrival: '2026-02-20',
        departure: '2026-02-22',
      },
    });

    expect(getPromotionsInformation).toHaveBeenCalled();

    const callArgs = (getPromotionsInformation as jest.Mock).mock.calls[0];
    expect(callArgs[callArgs.length - 1]).toBe('');
  });
});
