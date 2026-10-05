import '@testing-library/jest-dom';
import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';

import Dashboard, { getServerSideProps } from '~pages/account/dashboard';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/dashboard', () => ({
  createDashboardPiDataLoaderFn: () => ({}),
}));
jest.mock('~page-helper/dashboard/page.pi', () => ({
  __esModule: true,
  default: () => <div data-testid="MockPage" />,
}));

const mockGetCookie = 'id_token_cookie';

jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});
jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

const mockGetAuth0TokenAndEmail = jest.fn().mockResolvedValue({ accessToken: null, email: null });
jest.mock('../../../../src/lib/getAuth0Token', () => ({
  getAuth0TokenAndEmail: (...args: any[]) => mockGetAuth0TokenAndEmail(...args),
}));

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
  getUnleashToggles: jest.fn().mockResolvedValue({}),
  getFindBookingToken: () => mockGetFindBookingToken(),
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: ({ page }: any) => <div data-testid="SEO" data-page={page} />,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  BookingInfoCardWrapper: ({ area, bookingReference, basketReference }: any) => (
    <div
      data-testid="BookingInfoCardWrapper"
      data-area={area}
      data-booking-reference={bookingReference}
      data-basket-reference={basketReference}
    />
  ),
}));

const mockUseRouter = jest.fn(() => ({
  query: {},
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockGetFindBookingToken = jest.fn(() => ({
  token: null,
  basketReference: null,
  operaConfNumber: '',
}));

describe('Dashboard page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should match the snapshot', () => {
    const { container } = render(Dashboard.getLayout(<Dashboard />));
    mockUseRouter.mockReturnValue({ query: {} });
    mockGetFindBookingToken.mockReturnValue({
      token: null,
      basketReference: null,
      operaConfNumber: '',
    });
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

  it('should fetch Auth0 token when FT_PI_AUTH0_LOGIN is enabled', async () => {
    const { getUnleashToggles } = require('@whitbread-eos/utils');
    (getUnleashToggles as jest.Mock).mockResolvedValueOnce({
      [FT_PI_AUTH0_LOGIN]: true,
    });
    mockGetAuth0TokenAndEmail.mockResolvedValueOnce({
      accessToken: 'mock-token',
      email: 'test@example.com',
    });

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb', req: {} });

    expect(mockGetAuth0TokenAndEmail).toHaveBeenCalled();
    expect(serverSideResponse).toEqual(expect.objectContaining({ props: expect.any(Object) }));
  });

  it('should skip Auth0 token fetch when FT_PI_AUTH0_LOGIN is disabled', async () => {
    const { getUnleashToggles } = require('@whitbread-eos/utils');
    (getUnleashToggles as jest.Mock).mockResolvedValueOnce({
      [FT_PI_AUTH0_LOGIN]: false,
    });

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    await getServerSideProps({ locale: 'gb', req: {} });

    expect(mockGetAuth0TokenAndEmail).not.toHaveBeenCalled();
  });

  describe('Single booking view', () => {
    it('should render normal dashboard when no bookingReference query param', () => {
      mockUseRouter.mockReturnValue({ query: {} });

      const { getByTestId, queryByTestId } = render(<Dashboard />);

      expect(getByTestId('MockPage')).toBeInTheDocument();
      expect(queryByTestId('BookingInfoCardWrapper')).not.toBeInTheDocument();
    });

    it('should render empty div when bookingReference present but no token', () => {
      mockUseRouter.mockReturnValue({ query: { bookingReference: 'PI123456' } });
      mockGetFindBookingToken.mockReturnValue({
        token: null,
        basketReference: 'basket-ref-123', // basketReference present but no token
        operaConfNumber: '',
      });

      const { container, queryByTestId } = render(<Dashboard />);

      expect(container.querySelector('div')).toBeInTheDocument();
      expect(queryByTestId('BookingInfoCardWrapper')).not.toBeInTheDocument();
      expect(queryByTestId('MockPage')).not.toBeInTheDocument();
    });

    it('should render empty div when bookingReference present but no basketReference', () => {
      mockUseRouter.mockReturnValue({ query: { bookingReference: 'PI123456' } });
      mockGetFindBookingToken.mockReturnValue({
        token: 'mock-token',
        basketReference: null,
        operaConfNumber: '',
      });

      const { queryByTestId } = render(<Dashboard />);

      expect(queryByTestId('BookingInfoCardWrapper')).not.toBeInTheDocument();
      expect(queryByTestId('MockPage')).toBeInTheDocument();
    });

    it('should render BookingInfoCardWrapper when bookingReference, token, and basketReference present', () => {
      mockUseRouter.mockReturnValue({ query: { bookingReference: 'PI123456' } });
      mockGetFindBookingToken.mockReturnValue({
        token: 'mock-token',
        basketReference: 'basket-ref-123',
        operaConfNumber: 'OPERA123',
      });

      const { getByTestId, queryByTestId } = render(<Dashboard />);

      const wrapper = getByTestId('BookingInfoCardWrapper');
      expect(wrapper).toBeInTheDocument();
      expect(wrapper).toHaveAttribute('data-area', 'pi');
      expect(wrapper).toHaveAttribute('data-booking-reference', 'PI123456');
      expect(wrapper).toHaveAttribute('data-basket-reference', 'basket-ref-123');
      expect(queryByTestId('MockPage')).not.toBeInTheDocument();
    });

    it('should render SEO component in single booking view', () => {
      mockUseRouter.mockReturnValue({ query: { bookingReference: 'PI123456' } });
      mockGetFindBookingToken.mockReturnValue({
        token: 'mock-token',
        basketReference: 'basket-ref-123',
        operaConfNumber: '',
      });

      const { getByTestId } = render(<Dashboard />);

      const seo = getByTestId('SEO');
      expect(seo).toBeInTheDocument();
      expect(seo).toHaveAttribute('data-page', 'dashboard');
    });
  });
});
