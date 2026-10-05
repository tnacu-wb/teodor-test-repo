import '@testing-library/jest-dom';

import AddToWalletPage, { getServerSideProps } from '~pages/addtowallet';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/add-to-wallet', () => ({
  createAddToWalletPiDataLoaderFn: () => ({
    dehydratedState: { queries: [] },
    staticData: {},
  }),
  AddToWalletPagePi: () => <div data-testid="AddToWalletPagePi" />,
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),

  useRouter: () => ({
    router: {},
    query: { key: 'test' },
  }),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(() => ({
    prefetchQuery: jest.fn(),
    fetchQuery: jest.fn(),
    getQueryData: jest.fn(),
    setQueryData: jest.fn(),
  })),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  useFeatureToggle: jest.fn(() => ({})),
  getUnleashToggles: jest.fn(() => Promise.resolve({})),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  AddToWalletLayout: ({ children }: any) => <div data-testid="AddToWalletLayout">{children}</div>,
}));

const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();
jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie, set: () => mockSetCookie };
  };
});

describe('Add To Wallet page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should match the snapshot', () => {
    const { container } = render(
      AddToWalletPage.getLayout(<AddToWalletPage featureToggles={{}} />)
    );

    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details business undefined', () => {
    const { container } = render(
      AddToWalletPage.getLayout(<AddToWalletPage featureToggles={{}} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(
      AddToWalletPage.getLayout(<AddToWalletPage featureToggles={{}} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb' });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should test getLayout', () => {
    const mockedProps = {
      featureToggles: {},
      error: null,
      dehydratedState: {
        queries: [
          {
            queryKey: ['GetStaticContent'],
            state: {
              data: {
                headerInformation: {
                  config: {
                    promotionBanner: { title: 'Price Finder Promo!' },
                  },
                },
              },
            },
          },
        ],
      },
    };

    const layout = AddToWalletPage.getLayout(<AddToWalletPage {...mockedProps} />);
    render(layout);
  });

  it('should render AddToWalletPagePi component', () => {
    const { getByTestId } = render(<AddToWalletPage featureToggles={{}} />);

    expect(getByTestId('AddToWalletPagePi')).toBeInTheDocument();
  });

  it('should render within ErrorBoundary and AddToWalletLayout', () => {
    const { getByTestId } = render(
      AddToWalletPage.getLayout(<AddToWalletPage featureToggles={{}} />)
    );

    expect(getByTestId('AddToWalletPagePi')).toBeInTheDocument();
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
    expect(getByTestId('AddToWalletPagePi')).toBeInTheDocument();
  });
});
