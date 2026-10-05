import { useFeatureToggle, getUnleashToggles } from '@whitbread-eos/utils';

import ConfirmationPage, { getServerSideProps } from '~pages/confirmation';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };
const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();
const mockValidateBasketIdFromServer = jest.fn();

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: mockGetCookie,
    set: mockSetCookie,
  }));
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
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
    feature1: true,
  })),
  validateBasketIdFromServer: (basketRef: string, cookie: string) =>
    mockValidateBasketIdFromServer(basketRef, cookie),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
    language: {
      EN: 'en',
      DE: 'de',
    },
  },
  PAGE_UNAVAILABLE_URL_EN: 'page-unavailable.html',
  PAGE_UNAVAILABLE_URL_DE: 'seite-nicht-verfuegbar.html',
}));

jest.mock('~components', () => ({
  ConfirmationLayout: ({ children }: any) => <div data-testid="ConfirmationLayout">{children}</div>,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('~page-helper/confirmation', () => ({
  __esModule: true,
  createConfirmationPiDataLoaderFn: () => ({}),
  default: () => <div data-testid="confirmation" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

describe('ConfirmationPage page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockGetCookie.mockReturnValue('valid-basket-cookie');
    mockValidateBasketIdFromServer.mockReturnValue(true);
    mockServerSideCustomLocale.language = 'gb';
    mockServerSideCustomLocale.country = '';
    // Default feature toggle to enabled for basket validation
    (getUnleashToggles as jest.Mock).mockResolvedValue({
      feature1: true,
      release_basket_ids_cookie_validation: true,
    });
  });

  it('should match the snapshot layout', () => {
    const { container } = render(ConfirmationPage.getLayout(<div></div>));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot', () => {
    const { container } = render(
      <ConfirmationPage
        basketReference=""
        pcksQueryInput={{
          language: '',
          hotelId: '',
          country: '',
          adultsNumber: 1,
          basketReferenceId: '',
          bookingFlowId: '',
          childrenNumber: 1,
          nightsNumber: 1,
          endDate: '',
          startDate: '',
        }}
        hiQueryInput={{ language: '', country: '', hotelId: '' }}
        staticContentQueryInput={{
          language: '',
          country: '',
          site: 'leisure',
          businessBooker: false,
        }}
        promotionQueryInput={{
          language: '',
          country: '',
          hotelId: '',
          rateCode: '',
        }}
        featureToggles={{ feature1: true }}
      />
    );
    expect(useFeatureToggle).toBeCalledWith({ feature1: true });
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    const mockContext = {
      locale: 'gb',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'test-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(getUnleashToggles).toBeCalled();
    expect(mockValidateBasketIdFromServer).toHaveBeenCalledWith(
      'test-basket-id',
      'valid-basket-cookie'
    );
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    const mockContext = {
      locale: 'de',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'test-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(getUnleashToggles).toBeCalled();
    expect(mockValidateBasketIdFromServer).toHaveBeenCalledWith(
      'test-basket-id',
      'valid-basket-cookie'
    );
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should redirect to page-unavailable when basket ID validation fails', async () => {
    mockValidateBasketIdFromServer.mockReturnValue(false);
    mockServerSideCustomLocale.country = 'gb';
    mockServerSideCustomLocale.language = 'en';
    const mockContext = {
      locale: 'gb',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'invalid-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(mockValidateBasketIdFromServer).toHaveBeenCalledWith(
      'invalid-basket-id',
      'valid-basket-cookie'
    );
    expect(serverSideResponse).toEqual({
      redirect: {
        destination: '/gb/en/page-unavailable.html',
        permanent: false,
      },
    });
  });

  it('should redirect to page-unavailable when cookie is missing', async () => {
    mockGetCookie.mockReturnValue(undefined);
    mockValidateBasketIdFromServer.mockReturnValue(false);
    mockServerSideCustomLocale.country = 'gb';
    mockServerSideCustomLocale.language = 'en';
    const mockContext = {
      locale: 'gb',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'test-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(mockValidateBasketIdFromServer).toHaveBeenCalledWith('test-basket-id', undefined);
    expect(serverSideResponse).toEqual({
      redirect: {
        destination: '/gb/en/page-unavailable.html',
        permanent: false,
      },
    });
  });

  it('should allow access when feature toggle is disabled (validation skipped)', async () => {
    mockValidateBasketIdFromServer.mockReturnValue(false);
    mockServerSideCustomLocale.country = 'gb';
    mockServerSideCustomLocale.language = 'en';

    // Mock feature toggle to return false for basket validation
    (getUnleashToggles as jest.Mock).mockResolvedValue({
      feature1: true,
      release_basket_ids_cookie_validation: false,
    });

    const mockContext = {
      locale: 'gb',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'test-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);

    // Should NOT redirect because when toggle is off, validation is skipped
    expect(serverSideResponse).toHaveProperty('props');
    expect(serverSideResponse).not.toHaveProperty('redirect');
  });

  it('should redirect when feature toggle is enabled and validation fails', async () => {
    mockValidateBasketIdFromServer.mockReturnValue(false);
    mockServerSideCustomLocale.country = 'gb';
    mockServerSideCustomLocale.language = 'en';

    // Mock feature toggle to return true for basket validation
    (getUnleashToggles as jest.Mock).mockResolvedValue({
      feature1: true,
      release_basket_ids_cookie_validation: true,
    });

    const mockContext = {
      locale: 'gb',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'invalid-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);

    expect(serverSideResponse).toEqual({
      redirect: {
        destination: '/gb/en/page-unavailable.html',
        permanent: false,
      },
    });
  });

  it('should redirect to German page when language is de and validation fails', async () => {
    mockValidateBasketIdFromServer.mockReturnValue(false);
    mockServerSideCustomLocale.country = 'de';
    mockServerSideCustomLocale.language = 'de';

    // Mock feature toggle to return true for basket validation
    (getUnleashToggles as jest.Mock).mockResolvedValue({
      feature1: true,
      release_basket_ids_cookie_validation: true,
    });

    const mockContext = {
      locale: 'de',
      req: { headers: {} },
      res: { setHeader: jest.fn() },
      query: { reservationId: 'invalid-basket-id' },
    };

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);

    expect(serverSideResponse).toEqual({
      redirect: {
        destination: '/de/de/seite-nicht-verfuegbar.html',
        permanent: false,
      },
    });
  });
});
