import '@testing-library/jest-dom';

import Dashboard, { getServerSideProps } from '~pages/business-booker/account/dashboard';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  BookingHistoryCancelBookingModal: () => <div data-testid="MockPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~page-helper/dashboard/page.bb', () => ({
  __esModule: true,
  default: ({ children }: any) => <div data-testid="Page">{children}</div>,
}));

const mockUseQueryRequest = {
  isLoading: true,
  isError: false,
  error: { message: '' },
  data: {
    bookingHistory: {
      bookings: [
        {
          hotelName: '',
          leadGuest: '',
          arrivalDate: '',
          noOfNights: '',
          hotelCode: '',
          bookedBy: '',
        },
      ],
    },
  } as any,
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockUseQueryRequest,
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  getUnleashToggles: () => ({
    release_ib_enabled: true,
  }),
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

const mockGetCookie = 'mockCookieString';

jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});

describe('Dashboard page', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should match the snapshot', () => {
    const { container } = render(
      Dashboard.getLayout(
        <Dashboard featureToggles={{ release_pi_ancillaries_extras_display: true }} />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with data booking undefined', () => {
    mockUseQueryRequest.data.bookingHistory.bookings = [
      {
        hotelName: undefined,
        leadGuest: undefined,
        arrivalDate: undefined,
        noOfNights: undefined,
        hotelCode: undefined,
        bookedBy: undefined,
      },
    ];
    const { container } = render(
      Dashboard.getLayout(
        <Dashboard featureToggles={{ release_pi_ancillaries_extras_display: true }} />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with data bookingHistory undefined', () => {
    mockUseQueryRequest.data.bookingHistory = undefined;
    const { container } = render(
      Dashboard.getLayout(
        <Dashboard featureToggles={{ release_pi_ancillaries_extras_display: true }} />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with data bookingHistory undefined', () => {
    mockUseQueryRequest.data = undefined;
    const { container } = render(
      Dashboard.getLayout(
        <Dashboard featureToggles={{ release_pi_ancillaries_extras_display: true }} />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
      req: { headers: {} } as any,
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render Dashboard with innBusiness prop', () => {
    const innBusinessMock = { some: 'value' };
    const { getByTestId } = render(
      Dashboard.getLayout(
        <Dashboard
          featureToggles={{ release_pi_ancillaries_extras_display: true }}
          innBusiness={innBusinessMock as any}
        />
      )
    );
    expect(getByTestId('Page')).toBeInTheDocument();
  });
});
