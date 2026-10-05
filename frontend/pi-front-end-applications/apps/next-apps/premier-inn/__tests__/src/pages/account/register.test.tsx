import { useFeatureToggle, getUnleashToggles } from '@whitbread-eos/utils';

import RegisterPage, { getServerSideProps } from '~pages/account/register';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/register', () => ({
  createRegisterPiDataLoaderFn: () => ({}),
  Page: () => <div data-testid="MockPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  data: {},
  error: '',
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  graphQLRequest: () => jest.fn(),
  useQueryRequest: () => mockUseQueryRequest,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    feature1: true,
  })),
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

describe('Register Page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      RegisterPage.getLayout(
        <RegisterPage featureToggles={{ kill_switch_pi_recaptcha_register_page: false }} />
      )
    );
    expect(useFeatureToggle).toBeCalled();
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'GetStaticContent' },
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });
});
