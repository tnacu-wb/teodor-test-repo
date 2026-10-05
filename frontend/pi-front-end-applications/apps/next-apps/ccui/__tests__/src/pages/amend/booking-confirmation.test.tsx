import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import AmendConfirmationPage, { getServerSideProps } from '~pages/amend/booking-confirmation';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';

import { auth0 } from '../../../../src/lib/auth0';

jest.mock('../../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/amend/booking-confirmation', () => ({
  createBookingConfirmationCCUIDataLoaderFn: jest.fn(async () => ({
    confirmationInput: {
      basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
      bookingReference: 'AWM8159458',
      country: 'gb',
      language: 'en',
    },
    tempBookingReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
    bookingSpinnerConfig: [],
    amendBookingStatus: 'success',
  })),
  Page: () => <div data-testid="BookingConfirmationPage" />,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  getI18nLabels: jest.fn(() => mockGetI18NLabels()),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(() => mockGetProxyOptions()),
  getUnleashToggles: () =>
    Promise.resolve({
      release_ib_enabled: true,
    }),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

describe('Booking Confirmation page', () => {
  const featureToggles = {
    release_ib_enabled: true,
  };
  it('should match the snapshot', () => {
    const { container } = render(
      AmendConfirmationPage.getLayout(
        <AmendConfirmationPage
          confirmationInput={{
            basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
            bookingReference: 'AWM8159458',
            country: 'gb',
            language: 'en',
          }}
          tempBookingReference="AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c"
          bookingSpinnerConfig={[]}
          amendBookingStatus={CONFIRM_AMEND_STATUS.success}
          featureToggles={featureToggles}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());

    const mockContext = {
      locale: 'gb',
      query: {
        bookingReference: 'AWM8159458',
        basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
        status: 'success',
      },
      req: {
        headers: { host: 'localhost:3000', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/amend/booking-confirmation',
    };

    const serverSideResponse = await getServerSideProps(mockContext as any);

    expect(serverSideResponse).toEqual({
      props: {
        confirmationInput: {
          basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
          bookingReference: 'AWM8159458',
          country: 'gb',
          language: 'en',
        },
        tempBookingReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
        bookingSpinnerConfig: [],
        amendBookingStatus: 'success',
        isLoading: false,
        isError: false,
        error: { message: '' },
        data: {},
        featureToggles: featureToggles,
      },
    });
  });
});
