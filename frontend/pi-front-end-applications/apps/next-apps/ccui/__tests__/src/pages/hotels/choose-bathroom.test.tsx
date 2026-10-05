import { Claims } from '~types/general';

import ChooseBathroomPage, { getServerSideProps } from '~pages/hotels/choose-bathroom';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../../src/lib/auth0';
import {
  createChooseBathroomCcuiDataLoaderFn
} from '../../../../src/page-helper/hotel-details/choose-bathroom';
import { getProxyOptions } from '../../../../src/utils/proxyOptions';
import { getI18nLabels, getServerSideCustomLocale, getUnleashToggles } from '@whitbread-eos/utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };
const mockFeatureToggles = {
  release_pi_bb_ccui_premier_plus_accessible_room: true,
};

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(() => ({})),
}));

jest.mock('../../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/hotel-details/choose-bathroom', () => ({
  createChooseBathroomCcuiDataLoaderFn: jest.fn(),
  ChooseBathroomPageCCUI: jest.fn(() => <div data-testid="ChooseBathroomPage" />),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="ErrorBoundary">{children}</div>
  ),
}));

jest.mock('~components', () => ({
  SecondaryHDPLayout: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="SecondaryHDPLayout">{children}</div>
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: jest.fn(),
  getI18nLabels: jest.fn(),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(),
  getUnleashToggles: jest.fn(),
  useFeatureToggle: jest.fn(),
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
const featureToggles = mockFeatureToggles;

describe('Choose Your Bathroom page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createChooseBathroomCcuiDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue(mockServerSideCustomLocale);
    (getUnleashToggles as jest.Mock).mockResolvedValue(mockFeatureToggles);
  });

  describe('Component', () => {
    it('should render ChooseBathroomPageCCUI', () => {
      const { container } = render(<ChooseBathroomPage user={user} featureToggles={featureToggles} />);
      expect(container).toMatchSnapshot();
    });
  });

  describe('getLayout', () => {
    it('should wrap page with SecondaryHDPLayout and ErrorBoundary', () => {
      const { container } = render(
        ChooseBathroomPage.getLayout(
          <ChooseBathroomPage user={user} featureToggles={featureToggles} />
        )
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
      resolvedUrl: '/hotels/choose-bathroom',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Fhotels%2Fchoose-bathroom',
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
        const mockLoadedData = { hotelData: 'test' };
        (createChooseBathroomCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            isLoading: false,
            isError: false,
            error: { message: '' },
            data: {},
            ...mockLoadedData,
            featureToggles: mockFeatureToggles,
          },
        });
      });

      it('should call getUnleashToggles', async () => {
        await getServerSideProps(mockContext as any);

        expect(getUnleashToggles).toHaveBeenCalled();
      });

      it('should call createChooseBathroomCcuiDataLoaderFn with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(createChooseBathroomCcuiDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            session: mockSession,
            proxyOptions: mockGetProxyOptions(),
            language: 'gb',
            country: '',
          })
        );
      });

      it('should handle different locales', async () => {
        (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'de', country: 'de' });

        await getServerSideProps({ ...mockContext, locale: 'de' } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('de');
      });
    });
  });
});
