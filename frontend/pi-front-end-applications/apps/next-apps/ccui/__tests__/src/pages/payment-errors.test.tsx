import PaymentPage, { getServerSideProps } from '~pages/payment-errors';
import { Claims } from '~types/general';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';

import { auth0 } from '../../../src/lib/auth0';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/payment', () => ({
  createDataLoaderCcuiErrors: () => ({}),
  PaymentsErrorHandling: () => <div data-testid="MockPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: jest.fn(() => mockGetI18NLabels()),
  getUnleashToggles: jest.fn(() => ({})),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(() => mockGetProxyOptions()),
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="PaymentLayout">{children}</div>,
}));

const user: Claims = {};

describe('Payment page error', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      <PaymentPage
        user={user}
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
        hiQueryInput={{ language: 'en', country: 'gb', hotelId: '' }}
        basketReference=""
        accessToken=""
      />
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

    expect(serverSideResponse).toEqual({
      props: { isLoading: false, isError: false, error: { message: '' }, data: {} },
    });
  });
});
