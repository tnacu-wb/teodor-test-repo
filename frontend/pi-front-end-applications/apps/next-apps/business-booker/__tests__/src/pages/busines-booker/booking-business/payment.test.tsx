import '@testing-library/jest-dom';
import { useFeatureToggle } from '@whitbread-eos/utils';

import PaymentPage, { getServerSideProps } from '~pages/business-booker/booking-business/payment';
import { render } from '~utils/test-utils';

const isValidSecureBooking = jest.requireMock('@whitbread-eos/utils').isValidSecureBooking;

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('cookies', () => {
  return function () {
    return {
      get: () =>
        'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFUUkJSVVF4TURaRE1USTFPVEk0TkRnME0wUTNSRFl3TlRoQ1FqUkVOVVpGTWtJeU9EUXdOdyJ9.eyJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzgwZWFkZTU2LTY3YTUtNGViOS1hYzA3LTJlNmZlNWMyNDczOSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMXzk3MGUzN2E2LWIwNmUtNDkxNi04NWVhLTEyNjQwYzdiMGIyMyIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxOTMxMzkwMzMzLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoiaWJfcGl1a190cmF2ZWxhY2NvdW50QHlvcG1haWwuY29tIiwiaHR0cHM6Ly9wcmVtaWVyaW5uLmNvbS9vcGVyYUNvbXBhbnlJZCI6Ijk2ODYwODciLCJuaWNrbmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudCIsInByb2ZpbGUiOnsiYWNjZXNzTGV2ZWwiOiJTVVBFUiIsImNvbXBhbnlJZCI6IjE5MzEzOTAzMzMiLCJlbXBsb3llZUlkIjoiMTkzMTM5MDM0MSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6ImRlcHJlY2F0ZWQifSwibmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudEB5b3BtYWlsLmNvbSIsInBpY3R1cmUiOiJodHRwczovL3MuZ3JhdmF0YXIuY29tL2F2YXRhci8yNzI0NGFmNGE5MDg4MjE5NDE3ZWZhNzVhODlhNDM3Mj9zPTQ4MCZyPXBnJmQ9aHR0cHMlM0ElMkYlMkZjZG4uYXV0aDAuY29tJTJGYXZhdGFycyUyRmliLnBuZyIsInVwZGF0ZWRfYXQiOiIyMDI1LTA5LTAxVDA2OjAyOjU1LjYxOFoiLCJpc3MiOiJodHRwczovL2F1dGgwLnByZW1pZXJpbm4uZGlnaXRhbC8iLCJhdWQiOiJmWUVxUHBLRzZkTmZoVG1GUlVaRWp1djlDVUQ2VFFpdiIsInN1YiI6ImF1dGgwfDY3ODkxMDIzYjFmMWNjNTM4ZWJiMzlhYyIsImlhdCI6MTc1NjcwNjU3NywiZXhwIjoxNzU2NzEwMTc3LCJzaWQiOiJ0cE40UFVCbl91bE55QXFiSmR6SGo3SGE0alpOVkNrNSIsImF0X2hhc2giOiJsRjJGS3NBMnlLMG1GN3RJaHRDdTVnIiwibm9uY2UiOiJKaWNpSFlka0xGb2ZRdDJWU3duWmJVS2xaNTZmSHJXRCJ9.SnA54TymuUyT3kCoXrSwo75AXFPTlGD_OvUjeTDDZe_uC-vFU50Yq5VYGc5Jl3xFlMvepM4RBL8I1BRTmEJ3zICGYM70qlk3wFBQcmxEiTt3zMF8rlqOec_GSNv1GwKnBt3CVzdty_fifrYMRMqdd91zEvX6CxW4FhhNY6MJzJNxUFg-RqOqAaaNeKwGBhsbHEw47T3soxD4ZD0DXEaasfNlWVXIdy7Up2zgYmKLfPJShWrlTh_TxORrM8njlLtsyhxHgiXx4mMOUZUl7wcsGQQZoEyt5ZV5wAigLClLj0rJEconlw-kZQRYB4gVJWutcMrCkUnXdT5rowq-nr8d9g',
    };
  };
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('~page-helper/payment', () => ({
  createPaymentBbDataLoaderFn: () => ({}),
  PaymentPageBb: () => <div data-testid="MockPage" />,
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
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    release_ib_enabled: true,
  })),
}));

jest.mock('~components', () => ({
  PaymentLayout: ({ children }: any) => <div data-testid="PaymentLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({ children }: any) => (
  <div data-testid="InnBusinessLayout">{children}</div>
));

const mockProps = {
  pcksQueryInput: {
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
  },
  hiQueryInput: { language: 'en', country: 'gb', hotelId: '' },
  basketReference: '',
  featureToggles: { release_ib_enabled: true },
  innBusiness: undefined,
};

describe('Payment page', () => {
  it('should match the snapshot', () => {
    const { container } = render(PaymentPage.getLayout(<PaymentPage {...mockProps} />));
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'innbusiness' } } as any,
      query: { reservationId: '123' },
    });
    expect(isValidSecureBooking).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
      req: { headers: {} } as any,
      query: { reservationId: '123' },
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render InnBusiness layout if feature flag is on', async () => {
    mockProps.featureToggles = {} as any;
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(PaymentPage.getLayout(<PaymentPage {...mockProps} />));

    findByTestId('InnBusinessLayout');
  });

  it('should render PaymentLayout if feature flag is off', () => {
    mockProps.featureToggles = {} as any;
    mockProps.innBusiness = undefined;

    const { getByTestId } = render(PaymentPage.getLayout(<PaymentPage {...mockProps} />));
    expect(getByTestId('PaymentLayout')).toBeInTheDocument();
  });

  it('should pass userDetails and companyDetails to PaymentPageBb', () => {
    const userDetails = { name: 'John Doe' };
    const companyDetails = { company: 'Test Ltd' };
    mockProps.innBusiness = { userDetails, companyDetails } as any;

    const { getByTestId } = render(<PaymentPage {...mockProps} />);
    expect(getByTestId('MockPage')).toBeInTheDocument();
  });

  it('should return notFound if isValidSecureBooking returns error', async () => {
    isValidSecureBooking.mockReturnValueOnce({ error: true });

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'innbusiness' } } as any,
      query: { reservationId: '123' },
      res: {} as any,
    });
    expect(serverSideResponse).toEqual({ notFound: true });
  });

  it('should call useFeatureToggle with featureToggles', () => {
    render(<PaymentPage {...mockProps} />);
    expect(useFeatureToggle).toHaveBeenCalledWith(mockProps.featureToggles);
  });
});
