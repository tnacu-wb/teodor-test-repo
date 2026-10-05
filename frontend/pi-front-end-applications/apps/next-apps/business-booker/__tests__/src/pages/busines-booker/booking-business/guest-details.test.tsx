import '@testing-library/jest-dom';
import { Channel } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';

import GuestDetailsBBPage, {
  getServerSideProps,
} from '~pages/business-booker/booking-business/guest-details';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('cookies', () => {
  return function () {
    return {
      get: () => 'mocked_cookie_value',
    };
  };
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('~page-helper/guest-details', () => ({
  createGuestDetailsBBDataLoaderFn: () => ({}),
  Page: () => <div data-testid="MockPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    release_ib_enabled: true,
  })),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  isGuestDetailsPageAllowed: () => true,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
}));

jest.mock('~components', () => ({
  GuestDetailsLayout: ({ children }: any) => <div data-testid="GuestDetailsLayout">{children}</div>,
}));

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({ children }: any) => (
  <div data-testid="InnBusinessLayout">{children}</div>
));

window = Object.create(window);
const url = '';
Object.defineProperty(window, 'location', {
  value: {
    href: url,
  },
  writable: true,
});

const mockProps = {
  pcksQueryInput: {
    language: '',
    hotelId: '',
    country: '',
    adultsNumber: 1,
    basketReferenceId: '',
    bookingFlowId: '',
    childrenNumber: 1,
    nightsNumber: 1,
    endDate: '',
    startDate: '',
  },
  hiQueryInput: { language: 'en', country: 'gb', hotelId: '' },
  biQueryInput: {
    language: 'en',
    country: 'gb',
    basketReference: '',
    bookingChannelCriteria: {
      channel: Channel.Bb,
      subchannel: 'WEB',
      language: 'en',
    },
  },
  featureToggles: { release_ib_enabled: true },
  innBusiness: undefined,
};

describe('Ancillaries page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should match the snapshot', () => {
    const { container } = render(
      GuestDetailsBBPage.getLayout(<GuestDetailsBBPage {...mockProps} />)
    );
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
      req: { headers: {} } as any,
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should render InnBusiness layout if feature flag is on', async () => {
    mockProps.featureToggles = {} as any;
    mockProps.innBusiness = {} as any;

    const { findByTestId } = render(
      GuestDetailsBBPage.getLayout(<GuestDetailsBBPage {...mockProps} />)
    );

    findByTestId('InnBusinessLayout');
  });

  it('should redirect if user is not allowed on guest details page', () => {
    const mockSetOrigin = jest.fn();
    const mockIsGuestDetailsPageAllowed = jest.fn(() => false);
    const mockGetLoggedInUserInfo = jest.fn(() => ({ accessLevel: 'test' }));
    const mockUseCustomLocale = jest.fn(() => ({ language: 'en', country: 'gb' }));

    jest
      .spyOn(require('@whitbread-eos/utils'), 'isGuestDetailsPageAllowed')
      .mockImplementation(mockIsGuestDetailsPageAllowed);
    jest
      .spyOn(require('@whitbread-eos/utils'), 'getLoggedInUserInfo')
      .mockImplementation(mockGetLoggedInUserInfo);
    jest
      .spyOn(require('@whitbread-eos/utils'), 'useCustomLocale')
      .mockImplementation(mockUseCustomLocale);

    // Mock window.location
    window.location = { origin: 'http://localhost', href: '' } as any;

    render(<GuestDetailsBBPage {...mockProps} />);
    expect(window.location.href).toContain('/gb/en/business-booker');
  });

  it('should call useFeatureToggle with featureToggles', () => {
    render(<GuestDetailsBBPage {...mockProps} />);
    expect(useFeatureToggle).toHaveBeenCalledWith(mockProps.featureToggles);
  });

  it('should render GuestDetailsPageBB with correct props', () => {
    const { getByTestId } = render(<GuestDetailsBBPage {...mockProps} />);
    expect(getByTestId('MockPage')).toBeInTheDocument();
  });

  it('should use GuestDetailsLayout if isInnBusinessAppPage is false', () => {
    const page = <GuestDetailsBBPage {...mockProps} />;
    const { getByTestId } = render(GuestDetailsBBPage.getLayout(page));
    expect(getByTestId('GuestDetailsLayout')).toBeInTheDocument();
  });

  it('should use InnBusinessLayout if isInnBusinessAppPage is true', () => {
    const page = <GuestDetailsBBPage {...mockProps} isInnBusinessAppPage innBusiness={{} as any} />;
    const { getByTestId } = render(GuestDetailsBBPage.getLayout(page));
    expect(getByTestId('InnBusinessLayout')).toBeInTheDocument();
  });

  it('should redirect to error page if isInnBusinessAppPage is true and createGuestDetailsBBDataLoaderFn throws error', async () => {
    const mockError = new Error('Test error');

    jest
      .spyOn(require('~page-helper/guest-details'), 'createGuestDetailsBBDataLoaderFn')
      .mockImplementation(() => {
        throw mockError;
      });

    jest.spyOn(require('@whitbread-eos/utils'), 'isInnBusinessApp').mockReturnValue(true);

    const mockProps = {
      locale: 'de',
      req: { headers: { host: 'business.premierinn.com' } },
      res: {},
    };

    const result = await getServerSideProps(mockProps as any);

    expect(result).toEqual({
      redirect: {
        destination: '/de-de/error',
        permanent: false,
      },
    });

    jest.spyOn(require('@whitbread-eos/utils'), 'isInnBusinessApp').mockReturnValue(false);
    const nonPIBResult = await getServerSideProps(mockProps as any);
    expect(nonPIBResult).toEqual({
      notFound: true,
    });
  });

  it('getServerSideProps should return props with loadedData and labels', async () => {
    const loadedData = { foo: 'bar' };
    const labels = { data: { label: 'test' } };
    require('~page-helper/guest-details').createGuestDetailsBBDataLoaderFn = jest.fn(
      () => loadedData
    );
    require('@whitbread-eos/utils').getI18nLabels = jest.fn(() => labels);

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const result = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
      res: {} as any,
    });
    if ('props' in result) {
      expect(result.props).toMatchObject({
        ...loadedData,
        ...labels,
        featureToggles: expect.any(Object),
        isInnBusinessAppPage: expect.any(Boolean),
      });
    }
  });
});
