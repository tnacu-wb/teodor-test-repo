import HomePage, { getServerSideProps } from '~pages/index';
import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/organisms', () => ({
  PISearchContainer: () => <div data-testid="SearchPage" />,
}));

jest.mock('winston', () => ({
  ...jest.requireActual('winston'),
  createLogger: () => ({
    info: () => ({}),
  }),
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      query: { key: '' },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  axiosRequest: jest.fn().mockImplementation(() => ({})),
  decodeIdToken: jest.fn(),
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('@tanstack/react-query'),
  () => ({
    mutations: undefined,
    queries: undefined,
  });

const mockGetCookie = 'id_token_cookie';
const mockServerSideCustomLocale = { language: 'gb', country: '' };
jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div />,
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

describe('Home page', () => {
  it('should match the snapshot', () => {
    const { container } = render(<HomePage idTokenCookie="token" />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    const { container } = render(<HomePage idTokenCookie="token" />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(<HomePage idTokenCookie="token" />);
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb', query: { key: 'test' } });
    expect(serverSideResponse).toMatchSnapshot();
  });
});
