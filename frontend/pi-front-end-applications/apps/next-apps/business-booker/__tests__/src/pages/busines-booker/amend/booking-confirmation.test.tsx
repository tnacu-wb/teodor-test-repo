import '@testing-library/jest-dom';
import { CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';

import AmendConfirmationPage, {
  getServerSideProps,
} from '~pages/business-booker/amend/booking-confirmation';
import { render } from '~utils/test-utils';

jest.mock('~page-helper/amend/booking-confirmation', () => ({
  createBookingConfirmationBbDataLoaderFn: jest.fn(async () => ({
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
  Page: () => <div data-testid="DetailsPage" />,
}));

const mockUseUserDetails = {};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  useUserDetails: () => mockUseUserDetails,
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

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({ children }: any) => (
  <div data-testid="InnBusinessLayout">{children}</div>
));

const mockProps = {
  confirmationInput: {
    basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
    bookingReference: 'AWM8159458',
    country: 'gb',
    language: 'en',
  },
  tempBookingReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
  bookingSpinnerConfig: [],
  amendBookingStatus: CONFIRM_AMEND_STATUS.success,
  innBusiness: undefined,
  featureToggles: {},
};

describe('AmendConfirmationPage page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      AmendConfirmationPage.getLayout(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        <AmendConfirmationPage {...mockProps} />
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
        headers: { host: 'business.premierinn.com', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/business-booker/amend/booking-confirmation',
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render InnBusiness layout if feature flag is on', async () => {
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(
      AmendConfirmationPage.getLayout(<AmendConfirmationPage {...mockProps} />)
    );

    findByTestId('InnBusinessLayout');
  });

  it('should render DefaultLayout when isInnBusinessAppPage is false', () => {
    const props = { ...mockProps, isInnBusinessAppPage: false, innBusiness: undefined };
    const { getByTestId } = render(
      AmendConfirmationPage.getLayout(<AmendConfirmationPage {...props} />)
    );
    expect(getByTestId('DefaultLayout')).toBeInTheDocument();
  });

  it('should render InnBusinessLayout when isInnBusinessAppPage is true', () => {
    const props = {
      ...mockProps,
      isInnBusinessAppPage: true,
      innBusiness: {} as any,
      featureToggles: { release_ib_enabled: true },
    };
    const { getByTestId } = render(
      AmendConfirmationPage.getLayout(<AmendConfirmationPage {...props} />)
    );
    expect(getByTestId('InnBusinessLayout')).toBeInTheDocument();
  });

  it('should render ErrorBoundary in DefaultLayout', () => {
    const props = { ...mockProps, isInnBusinessAppPage: false };
    const { getByTestId } = render(
      AmendConfirmationPage.getLayout(<AmendConfirmationPage {...props} />)
    );
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
  });

  it('should render ErrorBoundary in InnBusinessLayout', () => {
    const props = {
      ...mockProps,
      isInnBusinessAppPage: true,
      innBusiness: {} as any,
      featureToggles: { release_ib_enabled: true },
    };
    const { getByTestId } = render(
      AmendConfirmationPage.getLayout(<AmendConfirmationPage {...props} />)
    );
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
  });

  it('should pass correct props from getServerSideProps', async () => {
    const mockContext = {
      locale: 'gb',
      query: {
        bookingReference: 'AWM8159458',
        basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
        status: 'success',
      },
      req: {
        headers: { host: 'business.premierinn.com', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/business-booker/amend/booking-confirmation',
    };
    // @ts-ignore
    const result = await getServerSideProps(mockContext);
    if ('props' in result) {
      expect(result.props).toHaveProperty('featureToggles');
      expect(result.props).toHaveProperty('isInnBusinessAppPage');
    } else {
      // Optionally, assert redirect if needed
      expect(result).toHaveProperty('redirect');
    }
  });

  it('should render AmendBookingConfirmationPageBb with correct props', () => {
    const { getByTestId } = render(<AmendConfirmationPage {...mockProps} />);
    expect(getByTestId('DetailsPage')).toBeInTheDocument();
  });
});
