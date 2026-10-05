import {
  useFeatureToggle,
  getUnleashToggles,
  getLoggedInUserInfo,
  GLOBALS,
} from '@whitbread-eos/utils';
import React from 'react';

import HotelDetailsPage, { getServerSideProps } from '~pages/business-booker/hotels/[...slug]';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale: { language: string; country: string | undefined } = {
  language: 'gb',
  country: '',
};

jest.mock('~page-helper/hotel-details/hdp', () => ({
  createHDPBbDataLoaderFn: () => ({}),
  HotelDetailsPageBB: () => <div data-testid="HotelDetailsPage" />,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

// Controllable cookie getter so we can simulate "no id token" scenarios
const mockCookieGet = jest.fn(() => 'mock-id-token');
jest.mock('cookies', () => {
  return function () {
    return { get: (...args: unknown[]) => mockCookieGet(...args) };
  };
});

jest.mock('~components/innBusiness/InnBusinessLayout', () => ({
  __esModule: true,
  default: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="inn-business-layout">{children}</div>
  ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    release_ib_enabled: true,
  })),
  getLoggedInUserInfo: jest.fn(() => ({ accessLevel: 'ADMIN' })),
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
}));

describe('Hotel Details page', () => {
  let consoleErrorSpy: jest.SpyInstance;

  beforeEach(() => {
    consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
  });
  afterEach(() => {
    consoleErrorSpy.mockRestore();
    jest.clearAllMocks();
    mockCookieGet.mockReturnValue('mock-id-token');
    (getLoggedInUserInfo as jest.Mock).mockReturnValue({ accessLevel: 'ADMIN' });
    mockServerSideCustomLocale.country = '';
    mockServerSideCustomLocale.language = 'gb';
  });

  it('should match the snapshot', () => {
    const { container } = render(
      <HotelDetailsPage isGuestUser={false} featureToggles={{ release_ib_enabled: true }} />
    );
    expect(useFeatureToggle).toBeCalledWith({ release_ib_enabled: true });
    expect(container).toMatchSnapshot();
  });

  // --- NEW: covers the `!isGuestUser` false branch, currently untested ---
  it('should render nothing when isGuestUser is true', () => {
    const { container } = render(
      <HotelDetailsPage isGuestUser={true} featureToggles={{ release_ib_enabled: true }} />
    );
    expect(container.firstChild).toBeNull();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';

    const serverSideResponse = await getServerSideProps({
      locale: 'de',
      req: { headers: {} } as any,
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should call useFeatureToggle with correct toggles', () => {
    render(<HotelDetailsPage isGuestUser={false} featureToggles={{ release_ib_enabled: false }} />);
    expect(useFeatureToggle).toBeCalledWith({ release_ib_enabled: false });
  });

  it('should handle missing locale in getServerSideProps', async () => {
    const serverSideResponse = await getServerSideProps({
      req: { headers: {} } as any,
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should handle getServerSideProps with custom headers', async () => {
    mockServerSideCustomLocale.language = 'fr';
    const serverSideResponse = await getServerSideProps({
      locale: 'fr',
      req: { headers: { host: 'custom.host.com', 'x-custom-header': 'value' } } as any,
    });
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should call getUnleashToggles only once per getServerSideProps call', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
    });
    expect(getUnleashToggles).toHaveBeenCalledTimes(1);
  });

  it('should fall back to GLOBALS.locale.GB when country is undefined', async () => {
    mockServerSideCustomLocale.country = undefined;

    await getServerSideProps({
      locale: 'gb',
      req: { headers: { host: 'business.premierinn.com' } } as any,
    });

    expect(getUnleashToggles).toBeCalledWith(
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.objectContaining({ country: GLOBALS.locale.GB })
    );
  });

  it('should use the provided country when it is defined', async () => {
    mockServerSideCustomLocale.country = 'de';

    await getServerSideProps({
      locale: 'de',
      req: { headers: { host: 'business.premierinn.com' } } as any,
    });

    expect(getUnleashToggles).toBeCalledWith(
      expect.anything(),
      expect.anything(),
      expect.anything(),
      expect.objectContaining({ country: 'de' })
    );
  });

  // --- NEW: covers both branches of getLayout ---
  describe('getLayout', () => {
    it('should render InnBusinessLayout when isInnBusinessAppPage is true', () => {
      const page = React.cloneElement(
        <HotelDetailsPage isGuestUser={false} featureToggles={{ release_ib_enabled: true }} />,
        {
          isInnBusinessAppPage: true,
          innBusiness: {},
          featureToggles: { release_ib_enabled: true },
        }
      );

      const { getByTestId } = render(HotelDetailsPage.getLayout(page));
      expect(getByTestId('inn-business-layout')).toBeTruthy();
    });

    it('should render DefaultLayout when isInnBusinessAppPage is false', () => {
      const page = React.cloneElement(
        <HotelDetailsPage isGuestUser={false} featureToggles={{ release_ib_enabled: true }} />,
        {
          isInnBusinessAppPage: false,
        }
      );

      const { container } = render(HotelDetailsPage.getLayout(page));
      expect(container).toBeTruthy();
    });
  });
});
