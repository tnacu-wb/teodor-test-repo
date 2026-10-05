import { Channel } from '@whitbread-eos/api';

import AncillariesPage, { getServerSideProps } from '~pages/ancillaries';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/ancillaries', () => ({
  createAncillariesPiDataLoaderFn: () => ({}),
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
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

jest.mock('~components', () => ({
  AncillariesLayout: ({ children }: any) => <div data-testid="AncillariesLayout">{children}</div>,
}));

const mockGetCookie = 'id_token_cookie';

jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});

describe('Ancillaries page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      AncillariesPage.getLayout(
        <AncillariesPage
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
              channel: Channel.Pi,
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
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb' });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'de' });
    expect(serverSideResponse).toMatchSnapshot();
  });
});
