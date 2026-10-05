import { getPromotionsInformation } from '@whitbread-eos/molecules';
import { getGQLClient } from '@whitbread-eos/utils';

import SearchPage, { getServerSideProps } from '~pages/search';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../src/lib/auth0';

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getPromotionsInformation: jest.fn(),
}));

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/search', () => ({
  createSearchResultsCCUIDataLoader: () => ({}),
  SearchPageCCUI: () => <div data-testid="SearchPage" />,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getGQLClient: jest.fn(),
  getI18nLabels: jest.fn(() => mockGetI18NLabels()),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(() => mockGetProxyOptions()),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
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

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

describe('Search page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ testFeature: true }}
          promotionBannerData={{
            showPromo: null,
            isWithinPromoWindow: null,
            promotionCode: null,
            landingPage: null,
            promoBannerColour: null,
            promoBannerIcon: null,
            promoBannerTitle: null,
            promoBannerSubtitle: null,
            promoInvalidMessage: null,
            promoExpiredMessage: null,
            promoAmendMessage: null,
            promoBookingInfo: {
              ratePlanCode: null,
              promotionCode: null,
            },
          }}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    const { container } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ testFeature: true }}
          promotionBannerData={{
            showPromo: null,
            isWithinPromoWindow: null,
            promotionCode: null,
            landingPage: null,
            promoBannerColour: null,
            promoBannerIcon: null,
            promoBannerTitle: null,
            promoBannerSubtitle: null,
            promoInvalidMessage: null,
            promoExpiredMessage: null,
            promoAmendMessage: null,
            promoBookingInfo: {
              ratePlanCode: null,
              promotionCode: null,
            },
          }}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(
      SearchPage.getLayout(
        <SearchPage
          featureToggles={{ testFeature: true }}
          promotionBannerData={{
            showPromo: null,
            isWithinPromoWindow: null,
            promotionCode: null,
            landingPage: null,
            promoBannerColour: null,
            promoBannerIcon: null,
            promoBannerTitle: null,
            promoBannerSubtitle: null,
            promoInvalidMessage: null,
            promoExpiredMessage: null,
            promoAmendMessage: null,
            promoBookingInfo: {
              ratePlanCode: null,
              promotionCode: null,
            },
          }}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());
    
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      res: {},
      req: { headers: { host: '', connection: '' } },
    } as any);

    expect(serverSideResponse).toMatchSnapshot();
  });
});

describe('Search Page - Promotions Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create client from session cookie and pass it to getPromotionsInformation', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());
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
        arrival: '2026-02-01',
        departure: '2026-02-05',
      },
    } as any);

    expect(getPromotionsInformation).toHaveBeenCalled();

    const callArgs = (getPromotionsInformation as jest.Mock).mock.calls[0];
    expect(callArgs[callArgs.length - 1]).toBe('');
  });
});
