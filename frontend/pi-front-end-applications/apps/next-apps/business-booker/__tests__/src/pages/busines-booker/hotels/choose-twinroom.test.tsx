import ChooseTwinroomPage, {
  getServerSideProps,
} from '~pages/business-booker/hotels/choose-twinroom';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/hotel-details/choose-twinroom', () => ({
  createChooseTwinroomBbDataLoaderFn: () => ({}),
  ChooseTwinroomPageBB: () => <div data-testid="ChooseTwinroomPage" />,
}));
const mockGetCookie = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFUUkJSVVF4TURaRE1USTFPVEk0TkRnME0wUTNSRFl3TlRoQ1FqUkVOVVpGTWtJeU9EUXdOdyJ9.eyJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzgwZWFkZTU2LTY3YTUtNGViOS1hYzA3LTJlNmZlNWMyNDczOSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMXzk3MGUzN2E2LWIwNmUtNDkxNi04NWVhLTEyNjQwYzdiMGIyMyIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxOTMxMzkwMzMzLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoiaWJfcGl1a190cmF2ZWxhY2NvdW50QHlvcG1haWwuY29tIiwiaHR0cHM6Ly9wcmVtaWVyaW5uLmNvbS9vcGVyYUNvbXBhbnlJZCI6Ijk2ODYwODciLCJuaWNrbmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudCIsInByb2ZpbGUiOnsiYWNjZXNzTGV2ZWwiOiJTVVBFUiIsImNvbXBhbnlJZCI6IjE5MzEzOTAzMzMiLCJlbXBsb3llZUlkIjoiMTkzMTM5MDM0MSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6ImRlcHJlY2F0ZWQifSwibmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudEB5b3BtYWlsLmNvbSIsInBpY3R1cmUiOiJodHRwczovL3MuZ3JhdmF0YXIuY29tL2F2YXRhci8yNzI0NGFmNGE5MDg4MjE5NDE3ZWZhNzVhODlhNDM3Mj9zPTQ4MCZyPXBnJmQ9aHR0cHMlM0ElMkYlMkZjZG4uYXV0aDAuY29tJTJGYXZhdGFycyUyRmliLnBuZyIsInVwZGF0ZWRfYXQiOiIyMDI1LTA5LTAxVDA2OjAyOjU1LjYxOFoiLCJpc3MiOiJodHRwczovL2F1dGgwLnByZW1pZXJpbm4uZGlnaXRhbC8iLCJhdWQiOiJmWUVxUHBLRzZkTmZoVG1GUlVaRWp1djlDVUQ2VFFpdiIsInN1YiI6ImF1dGgwfDY3ODkxMDIzYjFmMWNjNTM4ZWJiMzlhYyIsImlhdCI6MTc1NjcwNjU3NywiZXhwIjoxNzU2NzEwMTc3LCJzaWQiOiJ0cE40UFVCbl91bE55QXFiSmR6SGo3SGE0alpOVkNrNSIsImF0X2hhc2giOiJsRjJGS3NBMnlLMG1GN3RJaHRDdTVnIiwibm9uY2UiOiJKaWNpSFlka0xGb2ZRdDJWU3duWmJVS2xaNTZmSHJXRCJ9.SnA54TymuUyT3kCoXrSwo75AXFPTlGD_OvUjeTDDZe_uC-vFU50Yq5VYGc5Jl3xFlMvepM4RBL8I1BRTmEJ3zICGYM70qlk3wFBQcmxEiTt3zMF8rlqOec_GSNv1GwKnBt3CVzdty_fifrYMRMqdd91zEvX6CxW4FhhNY6MJzJNxUFg-RqOqAaaNeKwGBhsbHEw47T3soxD4ZD0DXEaasfNlWVXIdy7Up2zgYmKLfPJShWrlTh_TxORrM8njlLtsyhxHgiXx4mMOUZUl7wcsGQQZoEyt5ZV5wAigLClLj0rJEconlw-kZQRYB4gVJWutcMrCkUnXdT5rowq-nr8d9g';

jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  SecondaryHDPLayout: ({ children }: any) => <div data-testid="SecondaryHDPLayout">{children}</div>,
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
  getUnleashToggles: () => ({
    release_ib_enabled: true,
  }),
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({ children }: any) => (
  <div data-testid="InnBusinessLayout">{children}</div>
));

const mockProps = {
  featureToggles: {},
  innBusiness: undefined,
};

describe('Choose Your Twin room page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      ChooseTwinroomPage.getLayout(<ChooseTwinroomPage {...mockProps} />)
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

  it('should render InnBusiness layout if innBusiness prop is defined', async () => {
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(
      ChooseTwinroomPage.getLayout(<ChooseTwinroomPage {...mockProps} />)
    );

    findByTestId('InnBusinessLayout');
  });
});
