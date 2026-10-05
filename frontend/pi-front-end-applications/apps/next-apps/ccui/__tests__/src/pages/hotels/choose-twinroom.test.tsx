import { getI18nLabels, getServerSideCustomLocale } from '@whitbread-eos/utils';

import ChooseTwinroomPage, { getServerSideProps } from '~pages/hotels/choose-twinroom';
import { Claims } from '~types/general';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';

import { auth0 } from '../../../../src/lib/auth0';
import { createChooseTwinroomCcuiDataLoaderFn } from '../../../../src/page-helper/hotel-details/choose-twinroom';
import { getProxyOptions, setProxyOptionsCookies } from '../../../../src/utils/proxyOptions';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(() => ({})),
}));

jest.mock('../../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/hotel-details/choose-twinroom', () => ({
  createChooseTwinroomCcuiDataLoaderFn: jest.fn(),
  ChooseTwinroomPageCCUI: jest.fn(() => <div data-testid="ChooseTwinroomPage" />),
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
  getUnleashToggles: jest.fn(() => ({})),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(),
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

describe('Choose Your Twinroom page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (createChooseTwinroomCcuiDataLoaderFn as jest.Mock).mockResolvedValue({});
    (getI18nLabels as jest.Mock).mockResolvedValue(mockGetI18NLabels());
    (getProxyOptions as jest.Mock).mockReturnValue(mockGetProxyOptions());
    (getServerSideCustomLocale as jest.Mock).mockReturnValue(mockServerSideCustomLocale);
  });

  describe('Component', () => {
    it('should render ChooseTwinroomPageCCUI', () => {
      const { container } = render(<ChooseTwinroomPage user={user} />);
      expect(container).toMatchSnapshot();
    });
  });

  describe('getLayout', () => {
    it('should wrap page with SecondaryHDPLayout and ErrorBoundary', () => {
      const { container } = render(
        ChooseTwinroomPage.getLayout(<ChooseTwinroomPage user={user} />)
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
      resolvedUrl: '/hotels/choose-twinroom',
    };

    describe('when session is null', () => {
      it('should redirect to login with returnTo', async () => {
        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          redirect: {
            destination: '/auth/login?returnTo=%2Fhotels%2Fchoose-twinroom',
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
        const mockLoadedData = { twinRooms: ['room1', 'room2'] };
        (createChooseTwinroomCcuiDataLoaderFn as jest.Mock).mockResolvedValue(mockLoadedData);

        const result = await getServerSideProps(mockContext as any);

        expect(result).toEqual({
          props: {
            isLoading: false,
            isError: false,
            error: { message: '' },
            data: {},
            ...mockLoadedData,
          },
        });
      });

      it('should call createChooseTwinroomCcuiDataLoaderFn with correct parameters', async () => {
        await getServerSideProps(mockContext as any);

        expect(createChooseTwinroomCcuiDataLoaderFn).toHaveBeenCalledWith(
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

      it('should call setProxyOptionsCookies', async () => {
        await getServerSideProps(mockContext as any);

        expect(setProxyOptionsCookies).toHaveBeenCalledWith(
          mockGetProxyOptions(),
          expect.objectContaining({
            query: { key: 'test' },
          })
        );
      });
    });
  });
});
