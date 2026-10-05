import * as ReactQuery from '@tanstack/react-query';

import HomePage, { getServerSideProps } from '~pages/index';
import { render } from '~utils/test-utils';

jest.mock('@whitbread-eos/organisms', () => ({
  BBSearchContainer: () => <div data-testid="SearchPage" />,
}));

let mockUserDetails: any = {
  business: {
    accessLevel: 'SUPER',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useUserData: () => ({
    isLoggedIn: true,
  }),
  useUserDetails: () => mockUserDetails,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div />,
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
jest.mock('@tanstack/react-query'),
  () => ({
    mutations: undefined,
    queries: undefined,
  });

const mockGetCookie = 'id_token_cookie';

jest.mock('cookies', () => {
  return function () {
    return { get: () => mockGetCookie };
  };
});

describe('Home page', () => {
  it('should match the snapshot', () => {
    const { container } = render(<HomePage />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    mockUserDetails.business = undefined;
    const { container } = render(<HomePage />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    mockUserDetails = undefined;
    const { container } = render(<HomePage />);
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb', query: { key: 'test' } });
    expect(serverSideResponse).toMatchSnapshot();
  });
});
