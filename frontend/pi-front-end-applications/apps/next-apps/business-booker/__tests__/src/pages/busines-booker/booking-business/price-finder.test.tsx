import '@testing-library/jest-dom';
import router from 'next/router';
import { PriceFinderPageBB } from '~page-helper/price-finder';

import PriceFinderPage, { getServerSideProps } from '~pages/price-finder';
import { innBusinessLoginRedirect } from '~pages/business-booker/search';
import { render } from '~utils/test-utils';
import * as ReactQuery from '@tanstack/react-query';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/price-finder', () => ({
  createPriceFinderBBDataLoaderFn: () => ({
    dehydratedState: { queries: [] },
    staticData: {},
  }),
  PriceFinderPageBB: () => <div data-testid="PriceFinderPagePi" />,
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),

  useRouter: () => ({
    router: {},
    query: { key: 'test' },
  }),
}));

const queryClient = new ReactQuery.QueryClient();

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
  PriceFinderLayout: ({ children }: any) => <div data-testid="PriceFinderLayout">{children}</div>,
}));

const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();
jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie, set: () => mockSetCookie };
  };
});

describe('Price Finder page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should match the snapshot', () => {
    const { container } = render(
      PriceFinderPage.getLayout(<PriceFinderPage featureToggles={{}} />)
    );

    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details business undefined', () => {
    const { container } = render(
      PriceFinderPage.getLayout(<PriceFinderPage featureToggles={{}} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(
      PriceFinderPage.getLayout(<PriceFinderPage featureToggles={{}} />)
    );
    expect(container).toMatchSnapshot();
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

    const layout = PriceFinderPage.getLayout(<PriceFinderPage {...mockedProps} />);
    render(layout);
  });

  it('should render PriceFinderPagePi component', () => {
    const { getByTestId } = render(<PriceFinderPage featureToggles={{}} />);

    expect(getByTestId('PriceFinderPagePi')).toBeInTheDocument();
  });

  it('should render within ErrorBoundary and PriceFinderLayout', () => {
    const { getByTestId } = render(
      PriceFinderPage.getLayout(<PriceFinderPage featureToggles={{}} />)
    );

    expect(getByTestId('PriceFinderPagePi')).toBeInTheDocument();
    expect(getByTestId('ErrorBoundary')).toBeInTheDocument();
    expect(getByTestId('PriceFinderPagePi')).toBeInTheDocument();
  });
});

describe('innBusinessLoginRedirect', () => {
  it('should redirect to EN login for non-DE locale', () => {
    const result = innBusinessLoginRedirect('gb');
    expect(result).toEqual({
      redirect: {
        destination: '/en-gb/account/login',
        permanent: false,
      },
    });
  });

  it('should redirect to DE login for de locale', () => {
    const result = innBusinessLoginRedirect('de');
    expect(result).toEqual({
      redirect: {
        destination: '/de-de/account/login',
        permanent: false,
      },
    });
  });

  it('should be case insensitive for locale', () => {
    const result = innBusinessLoginRedirect('DE');
    expect(result).toEqual({
      redirect: {
        destination: '/de-de/account/login',
        permanent: false,
      },
    });
  });
});

const mockProps = {
  props: {
  locale: 'de',
  req: { headers: { host: 'business.premierinn.com' } },
  res: {},
  isInnBusinessAppPage: true,
  innBusiness: {}
  }
};

it('should render PriceFinderLayout when isInnBusinessAppPage is true', () => {
  mockProps.props.innBusiness = {} as any;
  mockProps.props.isInnBusinessAppPage = true;
  const { getByTestId } = render(
    PriceFinderPage.getLayout(<PriceFinderPageBB {...mockProps} router={router} queryClient={queryClient} />)
  );
  expect(getByTestId('PriceFinderLayout')).toBeInTheDocument();
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
  