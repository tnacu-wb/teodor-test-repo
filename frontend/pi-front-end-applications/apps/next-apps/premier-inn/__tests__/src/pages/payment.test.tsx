import { useFeatureToggle, getUnleashToggles, isValidSecureBooking } from '@whitbread-eos/utils';

import PaymentPage, { getServerSideProps } from '~pages/payment';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/payment', () => ({
  createPaymentPiDataLoaderFn: () => ({}),
  PaymentPagePi: () => <div data-testid="MockPage" />,
}));

jest.mock('next/router', () => ({
  useRouter: () => ({ query: {}, replace: jest.fn() }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  isSecureBookingPage: jest.fn().mockReturnValue(true),
  isValidSecureBooking: jest.fn().mockReturnValue({ error: true }),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    feature1: true,
  })),
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

jest.mock('~components', () => ({
  PaymentLayout: ({ children }: any) => <div data-testid="PaymentLayout">{children}</div>,
}));

describe('Payment page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      PaymentPage.getLayout(
        <PaymentPage
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
          featureToggles={{ feature1: true }}
        />
      )
    );
    expect(useFeatureToggle).toBeCalledWith({ feature1: true });
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { reservationId: '123' },
    });
    expect(getUnleashToggles).toBeCalled();
    expect(isValidSecureBooking).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
      query: { reservationId: '123' },
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });
});
