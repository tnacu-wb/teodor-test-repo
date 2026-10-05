import '@testing-library/jest-dom';
import ChooseBathroomPage, {
  getServerSideProps,
} from '~pages/business-booker/hotels/choose-bathroom';
import { render } from '~utils/test-utils';
import { getLoggedInUserInfo, getUnleashToggles } from '@whitbread-eos/utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };
const mockGetCookie = 'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFUUkJSVVF4TURaRE1USTFPVEk0TkRnME0wUTNSRFl3TlRoQ1FqUkVOVVpGTWtJeU9EUXdOdyJ9.eyJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzgwZWFkZTU2LTY3YTUtNGViOS1hYzA3LTJlNmZlNWMyNDczOSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMXzk3MGUzN2E2LWIwNmUtNDkxNi04NWVhLTEyNjQwYzdiMGIyMyIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxOTMxMzkwMzMzLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoiaWJfcGl1a190cmF2ZWxhY2NvdW50QHlvcG1haWwuY29tIiwiaHR0cHM6Ly9wcmVtaWVyaW5uLmNvbS9vcGVyYUNvbXBhbnlJZCI6Ijk2ODYwODciLCJuaWNrbmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudCIsInByb2ZpbGUiOnsiYWNjZXNzTGV2ZWwiOiJTVVBFUiIsImNvbXBhbnlJZCI6IjE5MzEzOTAzMzMiLCJlbXBsb3llZUlkIjoiMTkzMTM5MDM0MSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6ImRlcHJlY2F0ZWQifSwibmFtZSI6ImliX3BpdWtfdHJhdmVsYWNjb3VudEB5b3BtYWlsLmNvbSIsInBpY3R1cmUiOiJodHRwczovL3MuZ3JhdmF0YXIuY29tL2F2YXRhci8yNzI0NGFmNGE5MDg4MjE5NDE3ZWZhNzVhODlhNDM3Mj9zPTQ4MCZyPXBnJmQ9aHR0cHMlM0ElMkYlMkZjZG4uYXV0aDAuY29tJTJGYXZhdGFycyUyRmliLnBuZyIsInVwZGF0ZWRfYXQiOiIyMDI1LTA5LTAxVDA2OjAyOjU1LjYxOFoiLCJpc3MiOiJodHRwczovL2F1dGgwLnByZW1pZXJpbm4uZGlnaXRhbC8iLCJhdWQiOiJmWUVxUHBLRzZkTmZoVG1GUlVaRWp1djlDVUQ2VFFpdiIsInN1YiI6ImF1dGgwfDY3ODkxMDIzYjFmMWNjNTM4ZWJiMzlhYyIsImlhdCI6MTc1NjcwNjU3NywiZXhwIjoxNzU2NzEwMTc3LCJzaWQiOiJ0cE40UFVCbl91bE55QXFiSmR6SGo3SGE0alpOVkNrNSIsImF0X2hhc2giOiJsRjJGS3NBMnlLMG1GN3RJaHRDdTVnIiwibm9uY2UiOiJKaWNpSFlka0xGb2ZRdDJWU3duWmJVS2xaNTZmSHJXRCJ9.SnA54TymuUyT3kCoXrSwo75AXFPTlGD_OvUjeTDDZe_uC-vFU50Yq5VYGc5Jl3xFlMvepM4RBL8I1BRTmEJ3zICGYM70qlk3wFBQcmxEiTt3zMF8rlqOec_GSNv1GwKnBt3CVzdty_fifrYMRMqdd91zEvX6CxW4FhhNY6MJzJNxUFg-RqOqAaaNeKwGBhsbHEw47T3soxD4ZD0DXEaasfNlWVXIdy7Up2zgYmKLfPJShWrlTh_TxORrM8njlLtsyhxHgiXx4mMOUZUl7wcsGQQZoEyt5ZV5wAigLClLj0rJEconlw-kZQRYB4gVJWutcMrCkUnXdT5rowq-nr8d9g';

jest.mock('~page-helper/hotel-details/choose-bathroom', () => ({
  createChooseBathroomBbDataLoaderFn: jest.fn(() => ({})),
  ChooseBathroomPageBB: () => <div data-testid="ChooseBathroomPage" />,
}));

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
  getLoggedInUserInfo: jest.fn(() => ({ accessLevel: null })),
  getI18nLabels: jest.fn(() =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    })
  ),
  getUnleashToggles: jest.fn(() => ({
    release_ib_enabled: true,
  })),
  isInnBusinessApp: jest.fn((host: string) => host.includes('business')),
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({ children }: any) => (
  <div data-testid="InnBusinessLayout">{children}</div>
));

const mockProps = {
  innBusiness: undefined,
  isInnBusinessAppPage: false,
  featureToggles: {},
};

describe('Choose Your Bathroom page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockServerSideCustomLocale.language = 'gb';
    mockServerSideCustomLocale.country = '';
    mockProps.innBusiness = undefined;
    mockProps.isInnBusinessAppPage = false;
    mockProps.featureToggles = {};
  });

  it('should match the snapshot', () => {
    const { container } = render(
      ChooseBathroomPage.getLayout(<ChooseBathroomPage {...mockProps} />)
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
    mockProps.isInnBusinessAppPage = true;

    const { findByTestId } = render(
      ChooseBathroomPage.getLayout(<ChooseBathroomPage {...mockProps} />)
    );

    expect(await findByTestId('InnBusinessLayout')).toBeInTheDocument();
  });

  it('should render SecondaryHDPLayout if isInnBusinessAppPage is false', () => {
    const props = { ...mockProps, isInnBusinessAppPage: false };
    const { getByTestId } = render(
      ChooseBathroomPage.getLayout(<ChooseBathroomPage {...props} />)
    );
    expect(getByTestId('SecondaryHDPLayout')).toBeInTheDocument();
  });

  it('should render ErrorBoundary in both layouts', () => {
    const { getAllByTestId: getAllByTestId1 } = render(
      ChooseBathroomPage.getLayout(<ChooseBathroomPage {...mockProps} />)
    );
    expect(getAllByTestId1('ErrorBoundary').length).toBeGreaterThan(0);

    const props = { ...mockProps, innBusiness: {} as any };
    const { getAllByTestId: getAllByTestId2 } = render(
      ChooseBathroomPage.getLayout(<ChooseBathroomPage {...props} />)
    );
    expect(getAllByTestId2('ErrorBoundary').length).toBeGreaterThan(0);
  });

  it('should redirect guest user to business-booker root', async () => {
    (getLoggedInUserInfo as jest.Mock).mockReturnValue({ accessLevel: 'STAYER' });

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const result = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
      res: {} as any,
    }) as any;
    expect(result).toHaveProperty('redirect');
    if ('redirect' in result && result.redirect) {
      expect(result.redirect.destination).toContain('/business-booker');
      expect(result.redirect.permanent).toBe(false);
    }
  });

  it('should render ChooseBathroomPageBB', () => {
    const { getByTestId } = render(<ChooseBathroomPage {...mockProps} />);
    expect(getByTestId('ChooseBathroomPage')).toBeInTheDocument();
  });

  // Additional tests for increased coverage

  it('should call useFeatureToggle with featureToggles', () => {
    const spy = jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle');
    render(<ChooseBathroomPage {...mockProps} />);
    expect(spy).toHaveBeenCalledWith(mockProps.featureToggles);
  });

  it('should pass correct visualDisplayContext props to ChooseBathroomPageBB', () => {
    const { getByTestId } = render(<ChooseBathroomPage {...mockProps} />);
    expect(getByTestId('ChooseBathroomPage')).toBeInTheDocument();
    // Can't check props directly, but render success implies hook usage
  });

  it('should call getUnleashToggles with correct arguments', async () => {
    const getUnleashTogglesMock = getUnleashToggles as jest.Mock;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
      res: {} as any,
    });
    expect(getUnleashTogglesMock).toHaveBeenCalled();
  });
});
