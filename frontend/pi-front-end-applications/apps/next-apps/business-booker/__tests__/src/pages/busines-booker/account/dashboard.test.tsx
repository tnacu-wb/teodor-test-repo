import '@testing-library/jest-dom';

import Dashboard, { getServerSideProps } from '~pages/business-booker/account/dashboard';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/dashboard/page.bb', () => ({
  __esModule: true,
  default: () => <div data-testid="MockPage" />,
}));

const mockGetCookie = 'mockCookieString';

jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});
jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
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

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => {
  const MockLayout = ({ children }: { children: React.ReactNode }) => (
    <div data-testid="InnBusinessLayout">{children}</div>
  );
  MockLayout.displayName = 'InnBusinessLayout';
  return MockLayout;
});

const mockProps = {
  featureToggles: { release_pi_ancillaries_extras_display: true },
  innBusiness: undefined,
};

describe('Dashboard page', () => {
  it('should match the snapshot', () => {
    const { container } = render(Dashboard.getLayout(<Dashboard {...mockProps} />));
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

  it('should render InnBusiness layout if feature flag is on', async () => {
    mockProps.featureToggles = {} as any;
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(Dashboard.getLayout(<Dashboard {...mockProps} />));

    findByTestId('InnBusinessLayout');
  });
});

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => {
  const MockLayout = ({ children }: { children: React.ReactNode }) => (
    <div data-testid="InnBusinessLayout">{children}</div>
  );
  MockLayout.displayName = 'InnBusinessLayout';
  return MockLayout;
});

describe('Dashboard page', () => {
  it('should match the snapshot', () => {
    const { container } = render(Dashboard.getLayout(<Dashboard {...mockProps} />));
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

  it('should render InnBusiness layout if feature flag is on', async () => {
    mockProps.featureToggles = {} as any;
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(Dashboard.getLayout(<Dashboard {...mockProps} />));

    findByTestId('InnBusinessLayout');
  });

  describe('getLayout', () => {
    it('renders InnBusinessLayout when isInnBusinessAppPage is true', () => {
      const props = {
        isInnBusinessAppPage: true,
        innBusiness: {
          labels: {
            content: {},
            layout: {},
          },
          icons: {},
          cards: {},
          userDetails: {},
          companyDetails: {},
          bookings: [],
          invoices: [],
          notifications: [],
          preferences: {},
          isAccountHolder: false,
          users: [],
          spending: {},
          isCardHolder: false,
          searchRules: [],
        },
        featureToggles: {},
      };

      const { getByTestId } = render(Dashboard.getLayout(<Dashboard {...(props as any)} />));
      expect(getByTestId('InnBusinessLayout')).toBeInTheDocument();
      expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
    });

    it('renders DefaultLayout when isInnBusinessAppPage is false', () => {
      const props = {
        isInnBusinessAppPage: false,
        featureToggles: {},
      };

      const { getByTestId } = render(Dashboard.getLayout(<Dashboard {...props} />));
      expect(getByTestId('DefaultLayout')).toBeInTheDocument();
      expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
    });

    it('renders DefaultLayout when isInnBusinessAppPage is undefined', () => {
      const props = {
        featureToggles: {},
      };

      const { getByTestId } = render(Dashboard.getLayout(<Dashboard {...props} />));
      expect(getByTestId('DefaultLayout')).toBeInTheDocument();
      expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
    });
  });
});
