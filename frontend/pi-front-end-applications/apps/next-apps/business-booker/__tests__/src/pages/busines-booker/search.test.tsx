import '@testing-library/jest-dom';
import { FT_IB_REDIRECT_AFTER_LOGIN } from '@whitbread-eos/api';
import { getPromotionsInformation } from '@whitbread-eos/molecules';
import { getGQLClient } from '@whitbread-eos/utils';
import type { GetServerSidePropsContext } from 'next';

import { createSearchResultsBBDataLoader } from '~page-helper/search';
import SearchPage, {
  getServerSideProps,
  innBusinessLoginRedirect,
} from '~pages/business-booker/search';
import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getPromotionsInformation: jest.fn(),
}));

jest.mock('~utils/checkValidRedirect', () => jest.fn(() => true));

const mockServerSideCustomLocale = { language: 'gb', country: '' };

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('~page-helper/search', () => ({
  createSearchResultsBBDataLoader: jest.fn(),
  SearchPageBB: () => <div data-testid="SearchPage" />,
}));

let mockUserDetails: any = {
  business: {
    accessLevel: 'SUPER',
  },
};

const mockGetCookie = jest
  .fn()
  .mockReturnValue(
    'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFUUkJSVVF4TURaRE1USTFPVEk0TkRnME0wUTNSRFl3TlRoQ1FqUkVOVVpGTWtJeU9EUXdOdyJ9.eyJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzgwZWFkZTU2LTY3YTUtNGViOS1hYzA3LTJlNmZlNWMyNDczOSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMXzk3MGUzN2E2LWIwNmUtNDkxNi04NWVhLTEyNjQwYzdiMGIyMyIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxOTMxMzkwMzMzLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoiaWJfcGl1a190cmF2ZWxhY2NvdW50QHlvcG1haWwuY29tIiwiaHR0cHM6Ly9wcmVtaWVyaW5uLmNvbS9vcGVyYUNvbXBhbnlJZCI6Ijk2ODYwODciLCJuaWNrbmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudCIsInByb2ZpbGUiOnsiYWNjZXNzTGV2ZWwiOiJTVVBFUiIsImNvbXBhbnlJZCI6IjE5MzEzOTAzMzMiLCJlbXBsb3llZUlkIjoiMTkzMTM5MDM0MSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6ImRlcHJlY2F0ZWQifSwibmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudEB5b3BtYWlsLmNvbSIsInBpY3R1cmUiOiJodHRwczovL3MuZ3JhdmF0YXIuY29tL2F2YXRhci8yNzI0NGFmNGE5MDg4MjE5NDE3ZWZhNzVhODlhNDM3Mj9zPTQ4MCZyPXBnJmQ9aHR0cHMlM0ElMkYlMkZjZG4uYXV0aDAuY29tJTJGYXZhdGFycyUyRmliLnBuZyIsInVwZGF0ZWRfYXQiOiIyMDI1LTA5LTAxVDA2OjAyOjU1LjYxOFoiLCJpc3MiOiJodHRwczovL2F1dGgwLnByZW1pZXJpbm4uZGlnaXRhbC8iLCJhdWQiOiJmWUVxUHBLRzZkTmZoVG1GUlVaRWp1djlDVUQ2VFFpdiIsInN1YiI6ImF1dGgwfDY3ODkxMDIzYjFmMWNjNTM4ZWJiMzlhYyIsImlhdCI6MTc1NjcwNjU3NywiZXhwIjoxNzU2NzEwMTc3LCJzaWQiOiJ0cE40UFVCbl91bE55QXFiSmR6SGo3SGE0alpOVkNrNSIsImF0X2hhc2giOiJsRjJGS3NBMnlLMG1GN3RJaHRDdTVnIiwibm9uY2UiOiJKaWNpSFlka0xGb2ZRdDJWU3duWmJVS2xaNTZmSHJXRCJ9.SnA54TymuUyT3kCoXrSwo75AXFPTlGD_OvUjeTDDZe_uC-vFU50Yq5VYGc5Jl3xFlMvepM4RBL8I1BRTmEJ3zICGYM70qlk3wFBQcmxEiTt3zMF8rlqOec_GSNv1GwKnBt3CVzdty_fifrYMRMqdd91zEvX6CxW4FhhNY6MJzJNxUFg-RqOqAaaNeKwGBhsbHEw47T3soxD4ZD0DXEaasfNlWVXIdy7Up2zgYmKLfPJShWrlTh_TxORrM8njlLtsyhxHgiXx4mMOUZUl7wcsGQQZoEyt5ZV5wAigLClLj0rJEconlw-kZQRYB4gVJWutcMrCkUnXdT5rowq-nr8d9g'
  );
const mockSetCookie = jest.fn();
jest.mock('cookies', () => {
  return function () {
    return {
      get: mockGetCookie,
      set: mockSetCookie,
    };
  };
});

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');

  return {
    ...actual,
    getGQLClient: jest.fn(),

    // locale
    getServerSideCustomLocale: () => mockServerSideCustomLocale,

    // user
    useUserData: () => ({
      isLoggedIn: true,
    }),
    useUserDetails: () => mockUserDetails,

    // i18n
    getI18nLabels: () =>
      Promise.resolve({
        isLoading: false,
        isError: false,
        error: { message: '' },
        data: {},
      }),

    // feature toggles
    useFeatureToggle: jest.fn(),
    getUnleashToggles: jest.fn(() => ({
      release_ib_enabled: true,
    })),

    // business logic
    isInnBusinessApp: jest.fn((host: string) => host?.includes('business')),

    getHotelBrandFromSearchResults: jest.fn().mockReturnValue('PID'),
  };
});

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout/InnBusinessLayout', () => ({
  __esModule: true,
  default: ({ children }: any) => <div data-testid="InnBusinessLayout">{children}</div>,
}));

describe('Search page', () => {
  afterEach(() => {
    mockUserDetails = {
      business: {
        accessLevel: 'SUPER',
      },
    };
  });

  it('should match the snapshot', () => {
    const { container } = render(
      SearchPage.getLayout(<SearchPage featureToggles={{ testFeature: true }} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details business undefined', () => {
    mockUserDetails.business = undefined;
    const { container } = render(
      SearchPage.getLayout(<SearchPage featureToggles={{ testFeature: true }} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    mockUserDetails = undefined;
    const { container } = render(
      SearchPage.getLayout(<SearchPage featureToggles={{ testFeature: true }} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
      res: {} as any,
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render nothing if accessLevel is falsy', () => {
    mockUserDetails.business.accessLevel = '';
    const { queryByTestId } = render(<SearchPage featureToggles={{}} />);
    expect(queryByTestId('SearchPage')).toBeNull();
  });

  it('should call useFeatureToggle with featureToggles', () => {
    const useFeatureToggle = require('@whitbread-eos/utils').useFeatureToggle;
    render(<SearchPage featureToggles={{ test: true }} />);
    expect(useFeatureToggle).toHaveBeenCalledWith({ test: true });
  });

  it('should render InnBusinessLayout when isInnBusinessAppPage is true', () => {
    const page = <SearchPage featureToggles={{}} isInnBusinessAppPage innBusiness={{} as any} />;
    const layout = SearchPage.getLayout(page);
    const { getByTestId } = render(layout);
    expect(getByTestId('InnBusinessLayout')).toBeInTheDocument();
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
  });

  it('should render DefaultLayout when isInnBusinessAppPage is false', () => {
    const page = <SearchPage featureToggles={{}} isInnBusinessAppPage={false} />;
    const layout = SearchPage.getLayout(page);
    const { getByTestId } = render(layout);
    expect(getByTestId('DefaultLayout')).toBeInTheDocument();
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
  });

  it('should execute getServerSideProps and not redirect if idTokenCookie exists', async () => {
    const result = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } },
      res: {},
    } as any);
    expect(result).toHaveProperty('props');
  });

  it('should call createSearchResultsBBDataLoader with correct params', async () => {
    await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
      res: {} as any,
    } as any);
    expect(createSearchResultsBBDataLoader).toHaveBeenCalled();
  });

  it('covers fallback when promoInformationQuery is missing', async () => {
    (createSearchResultsBBDataLoader as jest.Mock).mockResolvedValueOnce({
      hotels: [],
    });

    const result = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } },
      res: {},
    } as any);

    expect(result).toHaveProperty('props');
  });
  it('covers fallback when loadedData is undefined', async () => {
    (createSearchResultsBBDataLoader as jest.Mock).mockResolvedValueOnce(undefined);

    const result = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } },
      res: {},
    } as any);

    expect(result).toHaveProperty('props');
  });
});

describe('innBusinessLoginRedirect', () => {
  it('should redirect to EN login for non-DE locale', () => {
    const result = innBusinessLoginRedirect('gb');
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login',
        permanent: false,
      },
    });
  });

  it('should redirect to DE login for de locale', () => {
    const result = innBusinessLoginRedirect('de');
    expect(result).toEqual({
      redirect: {
        destination: '/de-de/account/login',
        permanent: false,
      },
    });
  });

  it('should be case insensitive for locale', () => {
    const result = innBusinessLoginRedirect('DE');
    expect(result).toEqual({
      redirect: {
        destination: '/de-de/account/login',
        permanent: false,
      },
    });
  });
});

describe('innBusinessLoginRedirect', () => {
  const locale = 'gb';

  it('returns login redirect without redirectURL if feature toggle is off', () => {
    const result = innBusinessLoginRedirect(
      locale,
      { [FT_IB_REDIRECT_AFTER_LOGIN]: false },
      '/business-booker/search'
    );
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login',
        permanent: false,
      },
    });
  });

  it('returns login redirect with encoded redirectURL if feature toggle is on and path is valid', () => {
    const result = innBusinessLoginRedirect(
      locale,
      { [FT_IB_REDIRECT_AFTER_LOGIN]: true },
      '/business-booker/search'
    );
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login?redirectURL=%2Fgb%2Fen%2Fbusiness-booker%2Fsearch.html',
        permanent: false,
      },
    });
  });

  it('inserts .html before query string if missing', () => {
    const result = innBusinessLoginRedirect(
      locale,
      { [FT_IB_REDIRECT_AFTER_LOGIN]: true },
      '/business-booker/search?foo=bar'
    );
    expect(result).toEqual({
      redirect: {
        destination:
          '/en-gb/account/login?redirectURL=%2Fgb%2Fen%2Fbusiness-booker%2Fsearch.html%3Ffoo%3Dbar',
        permanent: false,
      },
    });
  });

  it('appends .html if missing and no query string', () => {
    const result = innBusinessLoginRedirect(
      locale,
      { [FT_IB_REDIRECT_AFTER_LOGIN]: true },
      '/business-booker/search'
    );
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login?redirectURL=%2Fgb%2Fen%2Fbusiness-booker%2Fsearch.html',
        permanent: false,
      },
    });
  });

  it('does not add .html if already present', () => {
    const result = innBusinessLoginRedirect(
      locale,
      { [FT_IB_REDIRECT_AFTER_LOGIN]: true },
      '/business-booker/search.html?foo=bar'
    );
    expect(result).toEqual({
      redirect: {
        destination:
          '/en-gb/account/login?redirectURL=%2Fgb%2Fen%2Fbusiness-booker%2Fsearch.html%3Ffoo%3Dbar',
        permanent: false,
      },
    });
  });

  it('uses /de/de for German locale', () => {
    const result = innBusinessLoginRedirect(
      'de',
      { [FT_IB_REDIRECT_AFTER_LOGIN]: true },
      '/business-booker/search'
    );
    expect(result).toEqual({
      redirect: {
        destination: '/de-de/account/login?redirectURL=%2Fde%2Fde%2Fbusiness-booker%2Fsearch.html',
        permanent: false,
      },
    });
  });

  it('returns login redirect without redirectURL if path is invalid', () => {
    const checkValidRedirect = require('~utils/checkValidRedirect');
    checkValidRedirect.mockImplementation(() => false);
    const result = innBusinessLoginRedirect(
      locale,
      { [FT_IB_REDIRECT_AFTER_LOGIN]: true },
      '/invalid/path'
    );
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login',
        permanent: false,
      },
    });
    checkValidRedirect.mockImplementation(() => true); // reset for other tests
  });

  it('returns login redirect without redirectURL if currentPath is empty', () => {
    const result = innBusinessLoginRedirect(locale, { [FT_IB_REDIRECT_AFTER_LOGIN]: true }, '');
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login',
        permanent: false,
      },
    });
  });
});

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
          featureToggles={{
            release_bb_promo_code_site_wide: true,
            release_bb_promo_code_landing_page: true,
          }}
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
});

describe('Search Page - Promotions Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetCookie.mockReturnValue('valid-session');
  });

  const createMockContext = (
    overrides: Partial<GetServerSidePropsContext> = {}
  ): GetServerSidePropsContext =>
    ({
      locale: 'gb',
      query: {},
      req: { headers: {} } as any,
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      } as any,
      resolvedUrl: '',
      params: {},
      locales: [],
      defaultLocale: 'gb',
      ...overrides,
    } as GetServerSidePropsContext);

  it('should create client from session cookie and pass it to getPromotionsInformation', async () => {
    const mockSessionId = 'mock-session-id';
    const mockClient = { fake: 'client' };

    mockGetCookie.mockReturnValue(mockSessionId);

    (getGQLClient as jest.Mock).mockReturnValue(mockClient);

    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      showPromo: true,
    });

    await getServerSideProps(
      createMockContext({
        query: {
          arrival: '2026-02-01',
          departure: '2026-02-04',
        },
      })
    );

    expect(getGQLClient).toHaveBeenCalledWith(mockSessionId);
    expect(getPromotionsInformation).toHaveBeenCalled();

    const callArgs = (getPromotionsInformation as jest.Mock).mock.calls[0];
    expect(callArgs[8]).toBe(mockClient);
  });
});
