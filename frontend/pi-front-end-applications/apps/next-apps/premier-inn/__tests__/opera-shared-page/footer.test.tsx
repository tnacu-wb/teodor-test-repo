import Footer, { getServerSideProps } from '~pages/opera-shared-page/footer';
import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/organisms', () => ({
  PISearchContainer: () => <div data-testid="SearchPage" />,
}));

jest.mock('@auth0/nextjs-auth0/client', () => ({
  Auth0Provider: ({ children }) => <div data-testid="auth0-provider">{children}</div>,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: () => <div data-testid="Error Boundry" />,
}));

jest.mock('winston', () => ({
  ...jest.requireActual('winston'),
  createLogger: () => ({
    info: () => ({}),
  }),
}));

const mockServerSideCustomLocale = { language: 'gb', country: '' };

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
  it('should match the snapshot with user details', () => {
    const { container } = render(<Footer idTokenCookie="token" />);
    Footer.getLayout(<div />);
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb', query: { key: 'test' } });
    expect(serverSideResponse).toMatchSnapshot();
  });
});
