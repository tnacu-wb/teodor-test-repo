import { QueryClient } from '@tanstack/react-query';
import { getI18nLabels } from '@whitbread-eos/utils';
import { getCookie } from 'cookies-next';

import HomePage, { getServerSideProps } from '~pages/index';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';
import { render } from '~utils/test-utils';
import { auth0 } from '../../../src/lib/auth0';

import { CCUI_ROLES } from '../../../src/types/general';

const context = {
  req: {},
  res: {},
  query: { language: 'en' },
  locale: 'gb',
  resolvedUrl: '/?language=en',
};

const session = {
  user: {
    wb_account_locale: 'en',
    [CCUI_ROLES]: ['role1', 'role2'],
  },
  accessToken: 'test-access-token',
  idToken: 'test-id-token',
};

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(() => session),
  },
}));

jest.mock('cookies-next', () => ({
  getCookie: jest.fn(),
}));

jest.mock('~utils/proxyOptions', () => ({
  getProxyOptions: jest.fn(),
  setProxyOptionsCookies: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: ({ children }: any) => <>{children}</>,
}));

jest.mock('@whitbread-eos/organisms', () => ({
  CCUISearchContainer: () => <div data-testid="SearchPage" />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getI18nLabels: jest.fn(),
  setAnalyticsUser: () => jest.fn(),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    testFeature: true,
  })),
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      query: { searchLocation: 'london' },
    };
  },
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

jest.mock('winston', () => ({
  ...jest.requireActual('winston'),
  createLogger: () => ({
    info: () => ({}),
  }),
}));

describe('Home page', () => {
  it('should match the snapshot', () => {
    const { container } = render(<HomePage featureToggles={{ testFeature: true }} />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot layout', () => {
    const { container } = render(
      HomePage.getLayout(<HomePage featureToggles={{ testFeature: true }} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    const { container } = render(<HomePage featureToggles={{ testFeature: true }} />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(<HomePage featureToggles={{ testFeature: true }} />);
    expect(container).toMatchSnapshot();
  });

  it('should fetch session and return props', async () => {
    await getServerSideProps(context as any);

    expect(auth0.getSession).toHaveBeenCalledWith(context.req);
    expect(getProxyOptions).toHaveBeenCalledWith({ req: context.req, res: context.res }, session);
    expect(setProxyOptionsCookies).toHaveBeenCalled();
    expect(getI18nLabels).toHaveBeenCalledWith({
      language: 'en',
      queryClient: expect.any(QueryClient),
    });
  });

  it('should redirect to the correct locale if necessary', async () => {
    (getCookie as jest.Mock).mockReturnValue('de');

    const result = await getServerSideProps({
      ...context,
      query: { language: 'en' },
      locale: 'gb',
    } as any);

    expect(result).toMatchSnapshot();
  });

  it('should not redirect if locale and selectedLocale match', async () => {
    (getCookie as jest.Mock).mockReturnValue('gb');

    const result = await getServerSideProps(context as any);
    expect(result).toMatchSnapshot();
  });

  it('should handle no session', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(null);

    const result = await getServerSideProps(context as any);
    expect(result).toMatchSnapshot();
  });
});
