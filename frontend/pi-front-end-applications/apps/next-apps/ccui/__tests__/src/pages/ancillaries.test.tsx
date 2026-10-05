import { Channel } from '@whitbread-eos/api';

import AncillariesPage, { getServerSideProps } from '~pages/ancillaries';
import { Claims } from '~types/general';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../src/lib/auth0';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: jest.fn(),
    set: jest.fn(),
  }));
});

jest.mock('~page-helper/ancillaries', () => ({
  createAncillariesCcuiDataLoaderFn: () => ({}),
  Page: () => <div data-testid="MockPage" />,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: jest.fn(() => mockGetI18NLabels()),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(() => mockGetProxyOptions()),
}));

jest.mock('~components', () => ({
  AncillariesLayout: ({ children }: any) => <div data-testid="AncillariesLayout">{children}</div>,
}));

const user: Claims = {};
describe('Ancillaries page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      AncillariesPage.getLayout(
        <AncillariesPage
          featureToggles={{
            key: true,
          }}
          user={user}
          pcksQueryInput={{
            language: '',
            hotelId: '',
            country: '',
            adultsNumber: 1,
            basketReferenceId: '',
            bookingFlowId: '',
            childrenNumber: 1,
            nightsNumber: 1,
            endDate: '',
            startDate: '',
          }}
          hiQueryInput={{ language: 'en', country: 'gb', hotelId: '' }}
          biQueryInput={{
            language: 'en',
            country: 'gb',
            basketReference: '',
            bookingChannelCriteria: {
              channel: Channel.Ccui,
              subchannel: 'WEB',
              language: 'en',
            },
          }}
        />
      )
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());
    
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      req: {},
      res: {},
    } as any);

    expect(serverSideResponse).toEqual({
      props: expect.objectContaining({
        isLoading: false,
        isError: false,
        error: { message: '' },
        data: {},
        featureToggles: expect.any(Object),
      }),
    });
  });
});
