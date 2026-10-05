import { render } from '~utils/test-utils';

import { auth0 } from '../../../src/lib/auth0';
import GetTokenPage, { getServerSideProps } from '../../../src/pages/get-token';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
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

describe('GetTokenPage page', () => {
  const mockContext: any = {
    req: {},
    res: {},
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should match the snapshot layout', () => {
    const { container } = render(
      GetTokenPage.getLayout(<GetTokenPage idToken="" accessToken="" />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot', () => {
    const { container } = render(<GetTokenPage idToken="" accessToken="" />);
    expect(container).toMatchSnapshot();
  });

  it('returns tokens when session is valid and URL is allowed', async () => {
    process.env.AUTH0_BASE_URL = 'https://ccui.qa.example.com';
    (auth0.getSession as jest.Mock).mockReturnValue({
      tokenSet: {
        accessToken: 'testAccessToken',
        idToken: 'testIdToken',
      },
    });

    const result = await getServerSideProps(mockContext);

    expect(result).toEqual({
      props: {
        accessToken: 'testAccessToken',
        idToken: 'testIdToken',
      },
    });
  });

  it('redirects to home page when URL is not allowed', async () => {
    process.env.AUTH0_BASE_URL = 'https://notallowed.example.com';
    (auth0.getSession as jest.Mock).mockReturnValue({
      tokenSet: {
        accessToken: 'testAccessToken',
        idToken: 'testIdToken',
      },
    });

    const result = await getServerSideProps(mockContext);

    expect(result).toEqual({
      redirect: {
        permanent: false,
        destination: '/',
      },
      props: {
        accessToken: undefined,
        idToken: undefined,
      },
    });
  });

  it('handles no session case', async () => {
    process.env.AUTH0_BASE_URL = 'https://ccui.qa.example.com';
    (auth0.getSession as jest.Mock).mockReturnValue(null);

    const result = await getServerSideProps(mockContext);

    expect(result).toEqual({
      redirect: {
        destination: '/auth/login?returnTo=undefined',
        permanent: false,
      },
    });
  });
});
