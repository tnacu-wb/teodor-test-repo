import { QueryClient } from '@tanstack/react-query';
import { getI18nLabels, getServerSideCustomLocale, getUnleashToggles } from '@whitbread-eos/utils';

import RepeatBookingPage, { getServerSideProps } from '~pages/repeat-booking';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';

import { auth0 } from '../../../src/lib/auth0';
import {
  RepeatBookingPage as RepeatBookingPageComponent,
  createRepeatPageCcuiDataLoaderFn,
} from '../../../src/page-helper/repeat-booking';
import { getProxyOptions, setProxyOptionsCookies } from '../../../src/utils/proxyOptions';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
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
  getI18nLabels: jest.fn(),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(),
  getUnleashToggles: jest.fn(),
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      query: { key: '' },
    };
  },
}));

jest.mock('../../../src/page-helper/repeat-booking', () => ({
  createRepeatPageCcuiDataLoaderFn: jest.fn(),
  RepeatBookingPage: jest.fn(() => <div data-testid="RepeatBookingPage" />),
}));

jest.mock('~components', () => ({
  DefaultLayout: ({
    children,
    showFooter,
  }: {
    children: React.ReactNode;
    showFooter?: boolean;
  }) => (
    <div data-testid="DefaultLayout" data-show-footer={showFooter}>
      {children}
    </div>
  ),
}));

jest.mock('../../../src/utils/proxyOptions', () => ({
  getProxyOptions: jest.fn(),
  setProxyOptionsCookies: jest.fn(),
}));

describe('RepeatBookingPage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createRepeatPageCcuiDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue(mockServerSideCustomLocale);
    (getUnleashToggles as jest.Mock).mockResolvedValue({ testFeature: true });
  });

  describe('Component', () => {
    it('should render RepeatBookingPage component', () => {
      const { container } = render(<RepeatBookingPage featureToggles={{ testFeature: true }} />);
      expect(container).toMatchSnapshot();
    });

    it('should render RepeatBookingPageComponent', () => {
      render(<RepeatBookingPage featureToggles={{ testFeature: true }} />);
      expect(RepeatBookingPageComponent).toHaveBeenCalled();
    });
  });

  describe('getLayout', () => {
    it('should wrap page with DefaultLayout and ErrorBoundary', () => {
      const { container } = render(
        RepeatBookingPage.getLayout(<RepeatBookingPage featureToggles={{ testFeature: true }} />)
      );
      expect(container).toMatchSnapshot();
    });
  });

  describe('getServerSideProps', () => {
    const mockContext = {
      locale: 'gb',
      query: { key: 'test' },
      req: {},
      res: {},
      resolvedUrl: '/repeat-booking',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Frepeat-booking',
            permanent: false,
          },
        });
      });

      it('should encode complex returnTo URL', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps({
          ...mockContext,
          resolvedUrl: '/repeat-booking?bookingRef=ABC123&hotelId=456',
        } as any);

        expect(result).toEqual({
          redirect: {
            destination:
              '/auth/login?returnTo=%2Frepeat-booking%3FbookingRef%3DABC123%26hotelId%3D456',
            permanent: false,
          },
        });
      });

      it('should not call data loader when session is null', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        await getServerSideProps(mockContext as any);

        expect(createRepeatPageCcuiDataLoaderFn).not.toHaveBeenCalled();
        expect(getI18nLabels).not.toHaveBeenCalled();
      });
    });

    describe('when session exists', () => {
      const mockSession = mockGetSession();

      beforeEach(() => {
        (auth0.getSession as jest.Mock).mockResolvedValue(mockSession);
      });

      it('should return props with loaded data', async () => {
        const mockLoadedData = { bookingData: 'test-data' };
        (createRepeatPageCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            featureToggles: { testFeature: true },
            ...mockLoadedData,
            isLoading: false,
            isError: false,
            error: { message: '' },
            data: {},
          },
        });
      });

      it('should call auth0.getSession with request object', async () => {
        await getServerSideProps(mockContext as any);

        expect(auth0.getSession).toHaveBeenCalledWith(mockContext.req);
      });

      it('should call getProxyOptions with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(getProxyOptions).toHaveBeenCalledWith(
          expect.objectContaining({
            query: { key: 'test' },
            req: {},
            res: {},
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
            req: {},
            res: {},
          })
        );
      });

      it('should call getServerSideCustomLocale with locale', async () => {
        await getServerSideProps(mockContext as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('gb');
      });

      it('should call createRepeatPageCcuiDataLoaderFn with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(createRepeatPageCcuiDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            session: mockSession,
            proxyOptions: mockGetProxyOptions(),
            language: 'gb',
            country: '',
            query: { key: 'test' },
            req: {},
            res: {},
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

      it('should handle ie locale', async () => {
        (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'en', country: 'ie' });

        await getServerSideProps({
          ...mockContext,
          locale: 'ie',
        } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('ie');
      });

      it('should merge loaded data and labels in props', async () => {
        const mockLoadedData = { bookings: ['booking1'], totalCount: 1, hotelId: '123' };
        const mockLabels = { labels: { title: 'Repeat Booking' } };
        (createRepeatPageCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);
        (getI18nLabels as jest.Mock).mockResolvedValue(mockLabels);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            featureToggles: { testFeature: true },
            ...mockLoadedData,
            ...mockLabels,
          },
        });
      });

      it('should not return redirect when session exists', async () => {
        const result = await getServerSideProps(mockContext as any);

        expect(result).not.toHaveProperty('redirect');
        expect(result).toHaveProperty('props');
      });

      it('should create new QueryClient for each request', async () => {
        await getServerSideProps(mockContext as any);
        await getServerSideProps(mockContext as any);

        expect(createRepeatPageCcuiDataLoaderFn).toHaveBeenCalledTimes(2);
        expect(getI18nLabels).toHaveBeenCalledTimes(2);
      });

      it('should pass all context props to data loader', async () => {
        const contextWithExtraProps = {
          ...mockContext,
          query: { bookingRef: 'ABC123', hotelId: '456' },
        };

        await getServerSideProps(contextWithExtraProps as any);

        expect(createRepeatPageCcuiDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            query: { bookingRef: 'ABC123', hotelId: '456' },
          })
        );
      });

      it('should handle empty query object', async () => {
        await getServerSideProps({
          ...mockContext,
          query: {},
        } as any);

        expect(createRepeatPageCcuiDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            query: {},
          })
        );
      });
    });
  });
});
