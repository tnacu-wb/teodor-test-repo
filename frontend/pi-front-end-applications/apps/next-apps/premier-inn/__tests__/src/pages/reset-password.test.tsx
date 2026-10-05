import ResetPasswordPage, { getServerSideProps } from '~pages/reset-password';
import { render, waitFor } from '~utils/test-utils';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getI18nLabels: () => ({}),
  getServerSideCustomLocale: () => ({ language: 'gb', country: '' }),
  getUnleashToggles: jest.fn().mockResolvedValue({}),
  useFeatureToggle: jest.fn(),
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('@whitbread-eos/organisms', () => ({
  AuthContentManagerPIVariant: () => <div data-testid="AuthContentManagerBBVariant" />,
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

describe('Reset Password Page page', () => {
  it('should match the snapshot', () => {
    const { container } = render(ResetPasswordPage.getLayout(<ResetPasswordPage />));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details  business undefined', () => {
    const { container } = render(ResetPasswordPage.getLayout(<ResetPasswordPage />));
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with user details undefined', () => {
    const { container } = render(ResetPasswordPage.getLayout(<ResetPasswordPage />));
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    await waitFor(async () => {
      const serverSideResponse = await getServerSideProps({ locale: 'gb' });
      expect(serverSideResponse).toMatchSnapshot();
    });
  });
});
