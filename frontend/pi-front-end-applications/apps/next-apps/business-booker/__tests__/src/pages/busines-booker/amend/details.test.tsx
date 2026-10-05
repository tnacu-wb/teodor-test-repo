import '@testing-library/jest-dom';

import AmendPage, { getServerSideProps } from '~pages/business-booker/amend/details';
import { render } from '~utils/test-utils';

jest.mock('~page-helper/amend/details', () => ({
  createAmendBbDataLoaderFn: jest.fn(async () => ({
    pcksQueryInput: {
      endDate: '',
      basketReferenceId: '',
      nightsNumber: 1,
      childrenNumber: 1,
      startDate: '',
      country: '',
      hotelId: '',
      language: '',
      bookingFlowId: '',
      adultsNumber: 1,
    },
    confirmationInput: {
      basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
      bookingReference: 'AWM8159458',
      country: 'gb',
      language: 'en',
    },
  })),
  Page: () => <div data-testid="DetailsPage" />,
}));

const mockUseUserDetails = {};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

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
  pcksQueryInput: {
    endDate: '',
    basketReferenceId: '',
    nightsNumber: 1,
    childrenNumber: 1,
    startDate: '',
    country: '',
    hotelId: '',
    language: '',
    bookingFlowId: '',
    adultsNumber: 1,
  },
  confirmationInput: {
    basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
    bookingReference: 'AWM8159458',
    country: 'gb',
    language: 'en',
  },
  isGuestUser: false,
  innBusiness: undefined,
  featureToggles: { release_pi_bb_ccui_maxrooms_amend: false },
};

describe('Amend Details page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      AmendPage.getLayout(
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        <AmendPage {...mockProps} />
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
      },
      req: {
        headers: { host: 'business.premierinn.com', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/business-booker/amend/details',
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render InnBusiness layout if feature flag is on', async () => {
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(AmendPage.getLayout(<AmendPage {...mockProps} />));

    findByTestId('InnBusinessLayout');
  });

  it('should redirect if user is guest', async () => {
    const mockContext = {
      locale: 'gb',
      query: {
        bookingReference: 'AWM8159458',
        basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
      },
      req: {
        headers: { host: 'business.premierinn.com', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/business-booker/amend/details',
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = (await getServerSideProps(mockContext)) as any;
    expect(serverSideResponse).toHaveProperty('redirect');
    expect((serverSideResponse as any).redirect).toHaveProperty('destination');
    if ('redirect' in serverSideResponse) {
      expect(serverSideResponse.redirect?.permanent).toBe(false);
    }
  });

  it('should render nothing if isGuestUser is true', () => {
    const { container } = render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <AmendPage {...mockProps} isGuestUser={true} />
    );
    expect(container.firstChild).toBeNull();
  });

  it('should push to router if user is STAYER', () => {
    const pushMock = jest.fn();
    mockUseRouter.mockReturnValue({ push: pushMock });
    // Simulate userData as STAYER
    (require('@whitbread-eos/utils').useUserDetails as any) = () => ({
      business: { accessLevel: 'STAYER' },
    });

    render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <AmendPage {...mockProps} />
    );
    expect(pushMock).toHaveBeenCalled();
  });

  it('should render AmendPageBb if not guest and not STAYER', () => {
    mockUseRouter.mockReturnValue({ push: jest.fn() });
    (require('@whitbread-eos/utils').useUserDetails as any) = () => ({
      business: { accessLevel: 'ADMIN' },
    });

    const { queryByTestId } = render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <AmendPage {...mockProps} />
    );
    expect(queryByTestId('DetailsPage')).toBeInTheDocument();
  });
});
