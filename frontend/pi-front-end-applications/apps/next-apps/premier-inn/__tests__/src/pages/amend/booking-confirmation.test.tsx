import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import AmendConfirmationPage, { getServerSideProps } from '~pages/amend/booking-confirmation';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/amend/booking-confirmation', () => ({
  Page: () => <div data-testid="BookingConfirmationPage" />,
  createBookingConfirmationPiDataLoaderFn: jest.fn(async () => ({
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
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
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
  getUnleashToggles: () =>
    Promise.resolve({
      release_ib_enabled: true,
    }),
  logger: () => ({
    info: jest.fn(),
    error: jest.fn(),
    warn: jest.fn(),
    debug: jest.fn(),
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

const featureToggles = {
  release_ib_enabled: true,
};

describe('Booking Confirmation page', () => {
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
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(serverSideResponse).toMatchSnapshot();
  });
});
