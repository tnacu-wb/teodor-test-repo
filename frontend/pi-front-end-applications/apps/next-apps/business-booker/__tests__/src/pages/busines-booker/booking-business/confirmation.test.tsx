import '@testing-library/jest-dom';
import { SITE } from '@whitbread-eos/api';

import ConfirmationPage, {
  getServerSideProps,
} from '~pages/business-booker/booking-business/confirmation';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('cookies', () => {
  return function () {
    return {
      get: () => 'token_cookie',
    };
  };
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('~components', () => ({
  ConfirmationLayout: ({ children }: any) => <div data-testid="ConfirmationLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({ children }: any) => (
  <div data-testid="InnBusinessLayout">{children}</div>
));

jest.mock('~page-helper/confirmation', () => ({
  __esModule: true,
  createConfirmationBbDataLoaderFn: () => ({}),
  default: () => <div data-testid="confirmation" />,
}));

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

const mockProps = {
  basketReference: '',
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
  hiQueryInput: { language: '', country: '', hotelId: '' },
  staticContentQueryInput: {
    language: '',
    country: '',
    site: 'leisure' as SITE,
    businessBooker: false,
  },
  promotionQueryInput: {
    language: '',
    country: '',
    hotelId: '',
    rateCode: '',
  },
  featureToggles: { release_ib_enabled: true },
  innBusiness: undefined,
};

describe('ConfirmationPage page', () => {
  it('should match the snapshot layout', () => {
    const { container } = render(ConfirmationPage.getLayout(<div></div>));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot', () => {
    const { container } = render(<ConfirmationPage {...mockProps} />);
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

    const { findByTestId } = render(
      ConfirmationPage.getLayout(<ConfirmationPage {...mockProps} />)
    );

    findByTestId('InnBusinessLayout');
  });

  it('should render ConfirmationLayout if feature flag is off', () => {
    const props = {
      ...mockProps,
      featureToggles: {},
      isInnBusinessAppPage: false,
      innBusiness: undefined,
    };
    const { getByTestId } = render(ConfirmationPage.getLayout(<ConfirmationPage {...props} />));
    expect(getByTestId('ConfirmationLayout')).toBeInTheDocument();
  });

  it('should render ErrorBoundary in both layouts', () => {
    const props = {
      ...mockProps,
      featureToggles: {},
      isInnBusinessAppPage: false,
      innBusiness: undefined,
    };
    const { getByTestId: getByTestId1 } = render(
      ConfirmationPage.getLayout(<ConfirmationPage {...props} />)
    );
    expect(getByTestId1('ErrorBoundary')).toBeInTheDocument();

    const innProps = {
      ...mockProps,
      featureToggles: {},
      isInnBusinessAppPage: true,
      innBusiness: {
        labels: {},
        icons: {},
        cards: [],
        userDetails: {},
        companyDetails: {},
        bookingDetails: {},
        bookingSummary: {},
        bookingExtras: {},
        bookingExtrasSummary: {},
      },
    };
    const { getAllByTestId } = render(
      ConfirmationPage.getLayout(<ConfirmationPage {...(innProps as any)} />)
    );
    expect(getAllByTestId('ErrorBoundary').length).toBeGreaterThan(0);
  });

  it('should pass correct props from getServerSideProps', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const result = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
      res: {} as any,
    });
    expect(result).toHaveProperty('props');
    if ('props' in result) {
      expect(result.props).toHaveProperty('featureToggles');
      expect(result.props).toHaveProperty('isInnBusinessAppPage');
    }
  });

  it('should render ConfirmationPageBb with correct props', () => {
    mockUseRouter.mockReturnValue({ pathname: '/test' });
    const { getByTestId } = render(<ConfirmationPage {...mockProps} />);
    expect(getByTestId('confirmation')).toBeInTheDocument();
  });
});
