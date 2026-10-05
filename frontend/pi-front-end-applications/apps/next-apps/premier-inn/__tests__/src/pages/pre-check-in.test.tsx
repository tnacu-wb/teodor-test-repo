import { FT_PI_PRE_CHECK_IN } from '@whitbread-eos/api';
import { getI18nLabels, getServerSideCustomLocale, getUnleashToggles } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';

import { render } from '~utils/test-utils';

import PreCheckInPage, { getServerSideProps } from '../../../src/pages/pre-check-in';

jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

jest.mock('@tanstack/react-query', () => ({
  useQueryClient: jest.fn().mockReturnValue({
    getQueryData: jest.fn(),
    setQueryData: jest.fn(),
  }),
  QueryClient: jest.fn(),
  QueryClientProvider: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  getI18nLabels: jest.fn(),
  getServerSideCustomLocale: jest.fn(),
  getUnleashToggles: jest.fn(),
  useFeatureToggle: jest.fn(),
  getNoOfDaysInYear: jest.fn().mockReturnValue(365),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

jest.mock('~page-helper/pre-check-in', () => ({
  Page: jest.fn(() => <div>PreCheckInPage Mock</div>),
}));

jest.mock('~components', () => ({
  DefaultLayout: jest.fn(({ children }) => <div>DefaultLayout Mock {children}</div>),
  SecondaryHDPLayout: jest.fn(({ children }) => <div>SecondaryHDPLayout Mock {children}</div>),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ErrorBoundary: jest.fn(({ children }) => <div>ErrorBoundary Mock {children}</div>),
}));

describe('PreCheckInPage', () => {
  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({
      query: {},
    });
  });

  it('renders PreCheckIn component with QueryClient', () => {
    const { container } = render(PreCheckInPage.getLayout(<PreCheckInPage featureToggles={{}} />));
    expect(container).toMatchSnapshot();
  });

  describe('getServerSideProps', () => {
    it('returns props correctly when preCheckIn is true', async () => {
      (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'en' });
      (getUnleashToggles as jest.Mock).mockResolvedValue({
        [FT_PI_PRE_CHECK_IN]: true,
      });
      (getI18nLabels as jest.Mock).mockResolvedValue({ label: 'test' });

      const context = { locale: 'gb' };
      const result = await getServerSideProps(context as any);

      expect(result).toEqual({
        props: {
          label: 'test',
          featureToggles: {
            [FT_PI_PRE_CHECK_IN]: true,
          },
        },
      });
    });

    it('returns notFound when preCheckIn is false', async () => {
      (getServerSideCustomLocale as jest.Mock).mockReturnValue({ language: 'en' });
      (getUnleashToggles as jest.Mock).mockResolvedValue({
        [FT_PI_PRE_CHECK_IN]: false,
      });

      const context = { locale: 'gb' };
      const result = await getServerSideProps(context as any);

      expect(result).toEqual({ notFound: true });
    });
  });
});
