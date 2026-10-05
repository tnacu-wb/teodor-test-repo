import { Claims } from '~types/general';

import ChooseRoomTypePage, { getServerSideProps } from '~pages/hotels/choose-roomtype';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../../src/lib/auth0';
import {
  createChooseRoomTypeCcuiDataLoaderFn
} from '../../../../src/page-helper/hotel-details/choose-roomtype';
import { getProxyOptions } from '../../../../src/utils/proxyOptions';
import { getI18nLabels, getServerSideCustomLocale, getUnleashToggles } from '@whitbread-eos/utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };
const mockFeatureToggles = {
  release_pi_bb_account_serv_2_serv: true,
  release_pi_bb_ccui_choose_room_type: true,
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

jest.mock('~page-helper/hotel-details/choose-roomtype', () => ({
  createChooseRoomTypeCcuiDataLoaderFn: jest.fn(),
  ChooseRoomTypePageCCUI: jest.fn(() => <div data-testid="ChooseRoomTypePage" />),
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

describe('Choose your Room type page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createChooseRoomTypeCcuiDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue(mockServerSideCustomLocale);
    (getUnleashToggles as jest.Mock).mockResolvedValue(mockFeatureToggles);
  });

  describe('Component', () => {
    it('should render ChooseRoomTypePageCCUI', () => {
      const { container } = render(
        <ChooseRoomTypePage user={user} featureToggles={featureToggles} />
      );
      expect(container).toMatchSnapshot();
    });
  });

  describe('getLayout', () => {
    it('should wrap page with SecondaryHDPLayout and ErrorBoundary', () => {
      const { container } = render(
        ChooseRoomTypePage.getLayout(
          <ChooseRoomTypePage user={user} featureToggles={featureToggles} />
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
      resolvedUrl: '/hotels/choose-roomtype',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Fhotels%2Fchoose-roomtype',
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
        const mockLoadedData = { roomTypes: ['single', 'double'] };
        (createChooseRoomTypeCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

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

      it('should call createChooseRoomTypeCcuiDataLoaderFn with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(createChooseRoomTypeCcuiDataLoaderFn).toHaveBeenCalledWith(
          expect.objectContaining({
            session: mockSession,
            proxyOptions: mockGetProxyOptions(),
            language: 'gb',
            country: '',
          })
        );
      });

      it('should handle different locales', async () => {
        (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'fr', country: 'fr' });

        await getServerSideProps({ ...mockContext, locale: 'fr' } as any);

        expect(getServerSideCustomLocale).toHaveBeenCalledWith('fr');
      });
    });
  });
});
