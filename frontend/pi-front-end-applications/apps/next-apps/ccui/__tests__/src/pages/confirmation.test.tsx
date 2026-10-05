import ConfirmationPage, { getServerSideProps } from '~pages/confirmation';
import { Claims } from '~types/general';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

const createMockRes = () => ({
  getHeader: jest.fn(),
  setHeader: jest.fn(),

  setPreviewData: jest.fn(),
  clearPreviewData: jest.fn(),
  writeHead: jest.fn(),

  setCookie: jest.fn(),
  getCookie: jest.fn(),
});

const mockUser: Claims = {
  sub: 'user-123',
  email: 'test@example.com',
  name: 'Test User',
};

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(() => ({
      user: mockUser,
      accessToken: 'mock-token',
      idToken: 'mock-id-token',
    })),
  },
}));

jest.mock('~components', () => ({
  ConfirmationLayout: ({ children }: any) => <div data-testid="ConfirmationLayout">{children}</div>,
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('~page-helper/confirmation', () => ({
  __esModule: true,
  createConfirmationCcuiDataLoaderFn: () => ({
    isLoading: false,
    isError: false,
    error: { message: '' },
    data: {},
  }),
  default: () => <div data-testid="confirmation" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: jest.fn(() => ({
    labels: { test: 'Test Label' }, // Mock labels
  })),
  getSession: jest.fn(() => ({
    user: mockUser,
    accessToken: 'mock-token',
  })),
  getUnleashToggles: jest.fn(() => ({
    feature1: true,
    feature2: false,
  })),
  getProxyOptions: jest.fn(() => ({
    headers: {},
    cookies: {},
  })),
  GLOBALS: {
    locale: {
      GB: 'gb',
    },
  },
}));

describe('ConfirmationPage page', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseRouter.mockReturnValue({
      push: jest.fn(),
      replace: jest.fn(),
      prefetch: jest.fn(),
      back: jest.fn(),
      query: {},
      pathname: '/confirmation',
    });
  });

  it('should match the snapshot layout', () => {
    const { container } = render(
      ConfirmationPage.getLayout(
        <ConfirmationPage
          user={mockUser}
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
          featureToggles={{ feature1: true }}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    const mockRes = createMockRes();
    const mockReq = {
      headers: {
        host: 'localhost:3000',
        connection: 'keep-alive',
        cookie: '',
      },
    };

    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      res: mockRes,
      req: mockReq,
    } as any);

    expect(serverSideResponse).toEqual({
      props: {
        featureToggles: expect.any(Object),
        labels: expect.any(Object),
        isLoading: false,
        isError: false,
        error: { message: '' },
        data: {},
      },
    });
  });
});
