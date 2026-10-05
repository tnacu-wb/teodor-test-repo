import * as chakra from '@chakra-ui/react';
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { FT_PI_BB_BOOKING_HISTORY_REDESIGN } from '@whitbread-eos/api';

import { render } from '~utils/test-utils';

import { MyDashboardPageBb } from './page.bb';

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: jest.fn(),
}));

const mockScreenSizes = {
  isLessThanLg: false,
  isLessThanMd: false,
  isLessThanMobile: false,
  isLessThanSm: false,
  isLessThanXl: true,
  isLessThanXs: false,
};

const mockCustomLocale = jest.fn();
const mockAuthCookie = jest.fn();
const mockUserData = { isLoggedIn: true };

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div></div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  useUserData: () => mockUserData,
  getAuthCookie: () => mockAuthCookie(),
  getLoggedInUserInfo: () => ({}),
  useFeatureToggle: jest.fn(),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  BookingsHistory: jest.fn(() => (
    <div data-testid="dummy-bookings-history">BB Bookings History</div>
  )),
  BBSearchContainer: jest.fn(() => (
    <div data-testid="dummy-search-container">BB Search Container</div>
  )),
  getTableRedesignDesktopConfig: jest.fn(),
  getDashboardRedesignTabletConfig: jest.fn(),
  getDashboardRedesignMobileConfig: jest.fn(),
  getTableConfig: jest.fn(),
}));

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  useRouter: () => mockUseRouter(),
}));

const mockRouter = {
  push: jest.fn(),
  query: {},
};

const queryClient = new ReactQuery.QueryClient();

describe('Page BB Dashboard', () => {
  beforeAll(() => {
    jest.clearAllMocks();
    mockUseRouter.mockReturnValue(mockRouter);
    mockAuthCookie.mockReturnValue('token');
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
    mockUserData.isLoggedIn = true;
    jest.mocked(chakra.useMediaQuery).mockReturnValue([false]);
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: false,
    });
  });

  it('should render SearchContainer & NOT render BookingsHistory if user is NOT LoggedIn ', () => {
    mockUserData.isLoggedIn = false;

    const { getByTestId, queryByTestId } = render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <MyDashboardPageBb screenSize={mockScreenSizes} />
      </ReactQuery.QueryClientProvider>
    );

    expect(getByTestId('dummy-search-container')).toBeInTheDocument();
    expect(queryByTestId('dummy-bookings-history')).not.toBeInTheDocument();
  });

  it('should render SearchContainer & BookingsHistory if user isLoggedIn', () => {
    const { getByTestId } = render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <MyDashboardPageBb screenSize={mockScreenSizes} />
      </ReactQuery.QueryClientProvider>
    );

    expect(getByTestId('dummy-search-container')).toBeInTheDocument();
    expect(getByTestId('dummy-bookings-history')).toBeInTheDocument();
  });
});

describe('MyDashboardPageBb tableConfig selection', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
    mockAuthCookie.mockReturnValue('token');
  });
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
    mockUserData.isLoggedIn = true;
  });

  it('uses getDashboardRedesignMobileConfig when feature flag is enabled and is mobile screen', () => {
    jest.mocked(chakra.useMediaQuery).mockReturnValue([true, false]);
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const tableConfig = require('@whitbread-eos/organisms');
    jest.spyOn(tableConfig, 'getDashboardRedesignMobileConfig');
    jest.spyOn(tableConfig, 'getDashboardRedesignTabletConfig');
    jest.spyOn(tableConfig, 'getTableRedesignDesktopConfig');
    jest.spyOn(tableConfig, 'getTableConfig');

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: true,
    });

    render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <MyDashboardPageBb screenSize={mockScreenSizes} />
      </ReactQuery.QueryClientProvider>
    );

    expect(tableConfig.getDashboardRedesignMobileConfig).toHaveBeenCalled();
    expect(tableConfig.getDashboardRedesignTabletConfig).not.toHaveBeenCalled();
    expect(tableConfig.getTableRedesignDesktopConfig).not.toHaveBeenCalled();
    expect(tableConfig.getTableConfig).not.toHaveBeenCalled();
  });

  it('uses getDashboardRedesignTabletConfig when feature flag is enabled and is tablet screen', () => {
    jest.mocked(chakra.useMediaQuery).mockReturnValue([false, true]);
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const tableConfig = require('@whitbread-eos/organisms');
    jest.spyOn(tableConfig, 'getDashboardRedesignMobileConfig');
    jest.spyOn(tableConfig, 'getDashboardRedesignTabletConfig');
    jest.spyOn(tableConfig, 'getTableRedesignDesktopConfig');
    jest.spyOn(tableConfig, 'getTableConfig');

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: true,
    });

    render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <MyDashboardPageBb screenSize={mockScreenSizes} />
      </ReactQuery.QueryClientProvider>
    );

    expect(tableConfig.getDashboardRedesignTabletConfig).toHaveBeenCalled();
    expect(tableConfig.getDashboardRedesignMobileConfig).not.toHaveBeenCalled();
    expect(tableConfig.getTableRedesignDesktopConfig).not.toHaveBeenCalled();
    expect(tableConfig.getTableConfig).not.toHaveBeenCalled();
  });

  it('uses getTableRedesignDesktopConfig when feature flag is enabled and is desktop screen', () => {
    jest.mocked(chakra.useMediaQuery).mockReturnValue([false, false]);
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const tableConfig = require('@whitbread-eos/organisms');
    jest.spyOn(tableConfig, 'getDashboardRedesignMobileConfig');
    jest.spyOn(tableConfig, 'getDashboardRedesignTabletConfig');
    jest.spyOn(tableConfig, 'getTableRedesignDesktopConfig');
    jest.spyOn(tableConfig, 'getTableConfig');

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: true,
    });

    render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <MyDashboardPageBb screenSize={mockScreenSizes} />
      </ReactQuery.QueryClientProvider>
    );

    expect(tableConfig.getTableRedesignDesktopConfig).toHaveBeenCalled();
    expect(tableConfig.getDashboardRedesignTabletConfig).not.toHaveBeenCalled();
    expect(tableConfig.getDashboardRedesignMobileConfig).not.toHaveBeenCalled();
    expect(tableConfig.getTableConfig).not.toHaveBeenCalled();
  });

  it('uses getTableConfig when feature flag is disabled', () => {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const tableConfig = require('@whitbread-eos/organisms');
    jest.spyOn(tableConfig, 'getDashboardRedesignMobileConfig');
    jest.spyOn(tableConfig, 'getDashboardRedesignTabletConfig');
    jest.spyOn(tableConfig, 'getTableRedesignDesktopConfig');
    jest.spyOn(tableConfig, 'getTableConfig');

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    jest.spyOn(require('@whitbread-eos/utils'), 'useFeatureToggle').mockReturnValue({
      [FT_PI_BB_BOOKING_HISTORY_REDESIGN]: false,
    });

    render(
      <ReactQuery.QueryClientProvider client={queryClient}>
        <MyDashboardPageBb screenSize={mockScreenSizes} />
      </ReactQuery.QueryClientProvider>
    );

    expect(tableConfig.getTableConfig).toHaveBeenCalledTimes(1);
    expect(tableConfig.getTableRedesignDesktopConfig).not.toHaveBeenCalled();
    expect(tableConfig.getDashboardRedesignTabletConfig).not.toHaveBeenCalled();
    expect(tableConfig.getDashboardRedesignMobileConfig).not.toHaveBeenCalled();
  });
});
