import getConfig from 'next/config';

import App, { getServerSideProps } from '~pages/_app';
import { Claims } from '~types/general';
import HomePage from '~pages/index';
import { render, waitFor } from '~utils/test-utils';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  setAnalyticsUser: () => jest.fn(),
}));

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  CCUISearchContainer: ({ children }: any) => <div data-testid="Search">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

const propsApps = {
  _nextI18Next: {
    initialI18nStore: {
      gb: {
        common: {},
      },
    },
    initialLocale: 'gb',
    userConfig: null,
  },
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  hasRegisteredSuccessfully: false,
};

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const user: Claims = {};

describe('App page', () => {
  it('should match the snapshot', async () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        PI_APPD_BRUM_API_KEY: 'XYZ',
      },
    }));
    mockUseRouter.mockReturnValue({
      locale: 'en',
      asPath: 'ancillaries?reservationId=LONEUS0936286',
      query: {
        searchLocation: 'as',
      },
      pathname: '',
    });

    const { container } = render(
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      <App pageProps={propsApps} Component={() => <HomePage user={user} />} />
    );
    await waitFor(() => {
      expect(container).toMatchSnapshot();
    });
  });

  it('should execute getServerSideProps with gb locale', async () => { 
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      req: {},
      res: { headers: { host: '', connection: '' } },
    } as any);
    expect(serverSideResponse).toMatchSnapshot();
  });
});
