// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';

import ResetPasswordPage, { getServerSideProps } from '~pages/reset-password';
import { render } from '~utils/test-utils';

let mockUserDetails: any = {
  business: {
    accessLevel: 'SUPER',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  graphQLRequest: jest.fn().mockResolvedValue({}),
  getGQLClient: jest.fn(),
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('@whitbread-eos/organisms', () => ({
  AuthContentManagerBBVariant: () => <div data-testid="AuthContentManagerBBVariant" />,
  NewPassword: () => <div data-testid="NewPassword" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
      query: { token: '' },
    };
  },
}));

jest.mock('winston', () => ({
  ...jest.requireActual('winston'),
  createLogger: () => ({
    info: () => ({}),
  }),
}));

jest.mock('cookies', () => {
  return function () {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return 'mockToken';
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return undefined;
      },
    };
  };
});

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    dehydrate: () => ({ mutations: [], queries: [] }),
    prefetchQuery: () => Promise.resolve({}),
    fetchQuery: () => Promise.resolve({}),
    mount: jest.fn(),
    unmount: jest.fn(),
  };
});

describe('Reset Password Page page', () => {
  it('should match the snapshot', () => {
    const { container } = render(ResetPasswordPage.getLayout(<ResetPasswordPage />));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    mockUserDetails.business = undefined;
    const { container } = render(ResetPasswordPage.getLayout(<ResetPasswordPage />));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    mockUserDetails = undefined;
    const { container } = render(ResetPasswordPage.getLayout(<ResetPasswordPage />));
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb' });
    expect(serverSideResponse).toMatchSnapshot();
  }, 50000);
});
