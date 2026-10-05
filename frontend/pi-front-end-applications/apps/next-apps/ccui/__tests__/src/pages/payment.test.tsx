import { Claims } from '~types/general';

import PaymentPage, { getServerSideProps } from '~pages/payment';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../src/lib/auth0';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/payment', () => ({
  createDataLoaderCcui: () => ({}),
  PaymentPageCcui: () => <div data-testid="MockPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: jest.fn(() => mockGetI18NLabels()),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(() => mockGetProxyOptions()),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    feature1: true,
  })),
}));

jest.mock('~components', () => ({
  PaymentLayout: ({ children }: any) => <div data-testid="PaymentLayout">{children}</div>,
}));

const user: Claims = {};

describe('Payment page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      PaymentPage.getLayout(
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
          featureToggles={{ feature1: true }}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());
    
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test', reservationId: '123' },
      res: {},
      req: { headers: { host: '', connection: '' } },
    } as any);

    expect(serverSideResponse).toEqual({
      props: {
        isLoading: false,
        isError: false,
        error: { message: '' },
        featureToggles: { feature1: true },
        data: {},
      },
    });
  });
});
