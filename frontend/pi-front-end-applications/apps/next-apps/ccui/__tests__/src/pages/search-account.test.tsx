import { QueryClient } from '@tanstack/react-query';
import { getI18nLabels, getServerSideCustomLocale, getUnleashToggles } from '@whitbread-eos/utils';

import SearchAccountPage, { getServerSideProps } from '~pages/search-account';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';

import { auth0 } from '../../../src/lib/auth0';
import { createSearchAccountCCUIDataLoaderFn } from '../../../src/page-helper/search-account';
import { getProxyOptions, setProxyOptionsCookies } from '../../../src/utils/proxyOptions';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(() => ({})),
}));

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('../../../src/page-helper/search-account', () => ({
  createSearchAccountCCUIDataLoaderFn: jest.fn(),
  Page: jest.fn(() => <div data-testid="MockPage" />),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="ErrorBoundary">{children}</div>
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: jest.fn(),
  isFeatureFlagEnabled: jest.fn(() => true),
  useFeatureSwitch: jest.fn(() => true),
  getI18nLabels: jest.fn(),
  getUnleashToggles: jest.fn(),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(),
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="DefaultLayout">{children}</div>
  ),
}));

jest.mock('../../../src/utils/proxyOptions', () => ({
  getProxyOptions: jest.fn(),
  setProxyOptionsCookies: jest.fn(),
}));

describe('SearchAccountPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createSearchAccountCCUIDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getUnleashToggles as jest.Mock).mockResolvedValue({});
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue(mockServerSideCustomLocale);
  });

  describe('Component', () => {
    it('should render SearchAccountPage component', () => {
      const { container } = render(<SearchAccountPage accessToken="test-token-123" />);
      expect(container).toMatchSnapshot();
    });

    it('should render with different accessToken', () => {
      const { container } = render(<SearchAccountPage accessToken="different-token-456" />);
      expect(container).toMatchSnapshot();
    });
  });

  describe('getLayout', () => {
    it('should wrap page with DefaultLayout and ErrorBoundary', () => {
      const { container } = render(
        SearchAccountPage.getLayout(<SearchAccountPage accessToken="123" />)
      );
      expect(container).toMatchSnapshot();
    });
  });

  describe('getServerSideProps', () => {
    const mockContext = {
      locale: 'gb',
      query: { key: 'test' },
      res: {},
      req: { headers: { host: '', connection: '' } },
      resolvedUrl: '/search-account',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Fsearch-account',
            permanent: false,
          },
        });
      });

      it('should encode complex returnTo URL', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps({
          ...mockContext,
          resolvedUrl: '/search-account?query=test&id=123',
        } as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Fsearch-account%3Fquery%3Dtest%26id%3D123',
            permanent: false,
          },
        });
      });
    });

    describe('when session exists', () => {
      const mockSession = mockGetSession();

      beforeEach(() => {
        (auth0.getSession as jest.Mock).mockResolvedValue(mockSession);
      });

      it('should return props with loaded data', async () => {
        const mockLoadedData = { data: 'test-data' };
        (createSearchAccountCCUIDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            ...mockLoadedData,
            isLoading: false,
            isError: false,
            error: { message: '' },
            data: {},
          },
          notFound: false,
        });
      });

      it('should call getProxyOptions with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(getProxyOptions).toHaveBeenCalledWith(
          expect.objectContaining({
            query: { key: 'test' },
            res: {},
            req: { headers: { host: '', connection: '' } },
          }),
          mockSession
        );
      });

      it('should call setProxyOptionsCookies with proxy options and props', async () => {
        const mockProxy = mockGetProxyOptions();

        await getServerSideProps(mockContext as any);

        expect(setProxyOptionsCookies).toHaveBeenCalledWith(
          mockProxy,
          expect.objectContaining({
            query: { key: 'test' },
            res: {},
            req: { headers: { host: '', connection: '' } },
          })
        );
      });

      it('should call getServerSideCustomLocale with locale', async () => {
        await getServerSideProps(mockContext as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('gb');
      });

      it('should call createSearchAccountCCUIDataLoaderFn with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(createSearchAccountCCUIDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            session: mockSession,
            proxyOptions: mockGetProxyOptions(),
            language: 'gb',
            country: '',
            query: { key: 'test' },
            res: {},
            req: { headers: { host: '', connection: '' } },
          })
        );
      });

      it('should call getI18nLabels with language and queryClient', async () => {
        await getServerSideProps(mockContext as any);

        expect(getI18nLabels).toHaveBeenCalledWith({
          language: 'gb',
          queryClient: expect.any(QueryClient),
        });
      });

      it('should use default locale gb when locale is undefined', async () => {
        await getServerSideProps({
          ...mockContext,
          locale: undefined,
        } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('gb');
      });

      it('should handle de locale', async () => {
        (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });

        await getServerSideProps({
          ...mockContext,
          locale: 'de',
        } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('de');
        expect(getI18nLabels).toHaveBeenCalledWith({
          language: 'de',
          queryClient: expect.any(QueryClient),
        });
      });

      it('should handle fr locale', async () => {
        (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'fr', country: 'fr' });

        await getServerSideProps({
          ...mockContext,
          locale: 'fr',
        } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('fr');
      });

      it('should merge loaded data and labels in props', async () => {
        const mockLoadedData = { bookings: ['booking1'], totalCount: 1 };
        const mockLabels = { labels: { title: 'Search Account' } };
        (createSearchAccountCCUIDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);
        (getI18nLabels as jest.Mock).mockResolvedValue(mockLabels);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            ...mockLoadedData,
            ...mockLabels,
          },
          notFound: false,
        });
      });

      it('should handle session without redirect', async () => {
        const result = await getServerSideProps(mockContext as any);

        expect(result).not.toHaveProperty('redirect');
        expect(result).toHaveProperty('props');
      });

      it('should create new QueryClient for each request', async () => {
        await getServerSideProps(mockContext as any);
        await getServerSideProps(mockContext as any);

        expect(createSearchAccountCCUIDataLoaderFn).toHaveBeenCalledTimes(2);
        expect(getI18nLabels).toHaveBeenCalledTimes(2);
      });
    });
  });
});
