import { useFeatureToggle, getUnleashToggles } from '@whitbread-eos/utils';

import HotelDetailsPage, { getServerSideProps } from '~pages/hotels/[...slug]';
import { render } from '~utils/test-utils';

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

jest.mock('~page-helper/hotel-details/hdp', () => ({
  createHDPPiDataLoaderFn: () => ({}),
  HotelDetailsPagePI: () => <div data-testid="HotelDetailsPage" />,
}));

jest.mock('~page-helper/hotel-details/dlp', () => ({
  createDLPPiDataLoaderFn: () => ({}),
  DestinationLandingPagePI: () => <div data-testid="DestinationLandingPage" />,
}));

// temporary solution until next/router will be deprecated

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {},
    query: { ARRdd: 'value' },
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));
jest.mock('~hooks/use-service-worker', () => ({
  __esModule: true,
  default: jest.fn(() => 'mocked_value'),
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
  SecondaryHDPLayout: ({ children }: any) => <div data-testid="SecondaryHDPLayout">{children}</div>,
}));

// Mock useCookieWatcher hook
const mockUseCookieWatcher = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  useFeatureToggle: jest.fn(() => ({
    release_pi_web_push_notifications: true,
  })),
  getUnleashToggles: jest.fn(),
  useCookieWatcher: mockUseCookieWatcher,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
}));

const mockPushRequestClosedCount = '0';
const mockSetCookie = jest.fn();
jest.mock('cookies', () => {
  return function () {
    return { get: () => mockPushRequestClosedCount, set: () => mockSetCookie };
  };
});

describe('Hotel Details page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      <HotelDetailsPage
        featureToggles={{ feature1: true }}
        dehydratedState={{ queries: [] }}
        error={null}
        promotionBannerData={promotionBannerData}
      />
    );
    expect(useFeatureToggle).toBeCalledWith({ feature1: true });
    expect(container).toMatchSnapshot();
  });
  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb' });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'de' });
    expect(serverSideResponse).toMatchSnapshot();
  });
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {},
    query: { slug: ['england', 'greater-london', 'london'] },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  useFeatureToggle: jest.fn(() => ({
    release_pi_web_push_notifications: true,
  })),
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
}));

describe('Destination Landing page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      <HotelDetailsPage
        featureToggles={{
          release_pi_web_push_notifications: true,
        }}
        dehydratedState={{ queries: [] }}
        error={null}
        promotionBannerData={promotionBannerData}
      />
    );
    expect(useFeatureToggle).toBeCalledWith({
      release_pi_web_push_notifications: true,
    });
    expect(container).toMatchSnapshot();
  });
  it('should match the snapshot with error', () => {
    const err = { query: 'test', errors: [{ message: 'error' }] };
    try {
      render(
        <HotelDetailsPage
          featureToggles={{
            release_pi_web_push_notifications: true,
            release_pi_new_dlp: true,
          }}
          dehydratedState={{ queries: [] }}
          error={JSON.stringify(err)}
          promotionBannerData={promotionBannerData}
        />
      );
    } catch (e) {
      expect(useFeatureToggle).toBeCalledWith({
        release_pi_web_push_notifications: true,
      });
      expect(JSON.stringify(e)).toBe(JSON.stringify(err));
    }
  });
  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { slug: ['england', 'greater-london', 'london'] },
      res: { setHeader: jest.fn() },
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'de' });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render getLayout', async () => {
    const mockedProps = {
      error: null,
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailability'],
            state: {
              data: {
                hotelAvailability: {
                  roomRates: [{ id: 1, name: 'Standard Room' }],
                },
              },
            },
          },
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

    const layout = HotelDetailsPage.getLayout(
      <HotelDetailsPage {...mockedProps} promotionBannerData={promotionBannerData} />
    );
    const { container } = await render(layout);
    expect(container).toBeTruthy();
  });
});

// Tests for ConsentNotificationModal logic
describe('Hotel Details Page - ConsentNotificationModal Behavior', () => {
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
    dehydratedState: { queries: [] },
    dlpQueryKey: [],
    hotelsInformationQueryKey: [],
    searchInformationQueryKey: [],
    error: 'null',
    promotionBannerData: { showPromo: true },
  };

  it('should render component structure correctly when consent cookie exists', () => {
    mockUseCookieWatcher.mockReturnValue('consent_granted');
    mockNotificationPermission('default');

    const { container } = render(<HotelDetailsPage {...defaultProps} />);

    expect(container).toMatchSnapshot();
  });

  it('should render differently when consent cookie is not set', () => {
    mockUseCookieWatcher.mockReturnValue(null);
    mockNotificationPermission('default');

    const { container } = render(<HotelDetailsPage {...defaultProps} />);

    expect(container).toMatchSnapshot();
  });
});
