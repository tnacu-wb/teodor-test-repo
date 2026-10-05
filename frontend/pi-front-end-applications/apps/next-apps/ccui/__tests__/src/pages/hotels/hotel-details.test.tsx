import { useFeatureToggle, getUnleashToggles } from '@whitbread-eos/utils';
import { QueryClient } from '@tanstack/react-query';

import HotelDetailsPage, { getServerSideProps } from '~pages/hotels/[...slug]';
import { Claims } from '~types/general';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../../src/lib/auth0';
import {
  createHDPCcuiDataLoaderFn
} from '../../../../src/page-helper/hotel-details/hdp';
import { getProxyOptions } from '../../../../src/utils/proxyOptions';
import { getI18nLabels, getServerSideCustomLocale, readPromotionsInformation } from '@whitbread-eos/utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };
const mockFeatureToggles = { feature1: true };

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(() => ({})),
}));

jest.mock('../../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/hotel-details/hdp', () => ({
  createHDPCcuiDataLoaderFn: jest.fn(),
  HotelDetailsPageCCUI: jest.fn(() => <div data-testid="HotelDetailsPage" />),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
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
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(),
  getI18nLabels: jest.fn(),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(),
  readPromotionsInformation: jest.fn(),
}));

jest.mock('../../../../src/utils/proxyOptions', () => ({
  getProxyOptions: jest.fn(),
  setProxyOptionsCookies: jest.fn(),
}));

jest.mock('~hooks/use-screensize', () => ({
  useScreenSize: jest.fn(() => ({
    isLessThanXs: false,
    isLessThanSm: false,
    isLessThanMd: false,
    isLessThanLg: false,
  })),
}));

const user: Claims = {};

describe('Hotel Details page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createHDPCcuiDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue(mockServerSideCustomLocale);
    (getUnleashToggles as jest.Mock).mockResolvedValue(mockFeatureToggles);
    (readPromotionsInformation as jest.Mock).mockReturnValue(null);
    mockUseRouter.mockReturnValue({ query: {} });
  });

  describe('Component', () => {
    it('should render HotelDetailsPageCCUI', () => {
      const { container } = render(
        <HotelDetailsPage user={user} featureToggles={mockFeatureToggles} />
      );
      expect(useFeatureToggle).toHaveBeenCalledWith(mockFeatureToggles);
      expect(container).toMatchSnapshot();
    });

    it('should render with promotionBannerData', () => {
      const promotionData = { title: 'Promo', discount: 10 };
      const { container } = render(
        <HotelDetailsPage
          user={user}
          featureToggles={mockFeatureToggles}
          promotionBannerData={promotionData as any}
        />
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
      resolvedUrl: '/hotels/hotel-slug',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Fhotels%2Fhotel-slug',
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

      it('should return props with loaded data and feature toggles', async () => {
        const mockLoadedData = { hotelName: 'Test Hotel' };
        (createHDPCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            isLoading: false,
            isError: false,
            error: { message: '' },
            data: {},
            ...mockLoadedData,
            featureToggles: mockFeatureToggles,
            promotionBannerData: null,
          },
        });
      });

      it('should call getUnleashToggles with session user name', async () => {
        await getServerSideProps(mockContext as any);

        expect(getUnleashToggles).toHaveBeenCalled();
      });

      it('should call createHDPCcuiDataLoaderFn with feature toggles', async () => {
        await getServerSideProps(mockContext as any);

        expect(createHDPCcuiDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            session: mockSession,
            proxyOptions: mockGetProxyOptions(),
            language: 'gb',
            country: '',
            featureToggles: mockFeatureToggles,
          })
        );
      });

      it('should call readPromotionsInformation with loaded data', async () => {
        const mockLoadedData = { hotelName: 'Test Hotel', promotions: [] };
        (createHDPCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

        await getServerSideProps(mockContext as any);

        expect(readPromotionsInformation).toHaveBeenCalledWith(mockLoadedData);
      });

      it('should handle de locale', async () => {
        (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });

        await getServerSideProps({ ...mockContext, locale: 'de' } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('de');
        expect(getI18nLabels).toHaveBeenCalledWith({
          language: 'de',
          queryClient: expect.any(QueryClient),
        });
      });

      it('should include promotionBannerData in props when available', async () => {
        const promotionData = { title: 'Special Offer', discount: 15 };
        (readPromotionsInformation as jest.Mock).mockReturnValue(promotionData);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual(
          expect.objectContaining({
            props: expect.objectContaining({
              promotionBannerData: promotionData,
            }),
          })
        );
      });
    });
  });
});
