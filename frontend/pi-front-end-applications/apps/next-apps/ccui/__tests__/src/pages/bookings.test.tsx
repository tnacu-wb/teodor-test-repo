import { Claims } from '~types/general';

import BookingsPage, { getServerSideProps } from '~pages/bookings';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~components', () => ({
  BookingsLayout: ({ children }: any) => <div data-testid="BookingsLayout">{children}</div>,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('../../../src/page-helper/bookings', () => ({
  createBookingsCcuiDataLoaderFn: () => ({}),
  Page: () => <div data-testid="confirmation" />,
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
}));

const props = {
  user: {},
  featureToggles: { testFeature: true },
};

describe('Bookings page', () => {
  it('should match the snapshot layout', () => {
    const { container } = render(BookingsPage.getLayout(<BookingsPage {...props} />));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot', () => {
    const { container } = render(<BookingsPage {...props} />);
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      res: {},
      req: { headers: { host: '', connection: '' } },
    } as any);
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
      query: { key: 'test' },
      res: {},
      req: { headers: { host: '', connection: '' } },
    } as any);
    expect(serverSideResponse).toMatchSnapshot();
  });
});
