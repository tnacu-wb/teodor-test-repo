import { LOCALES, CompanyType } from '@whitbread-eos/api';
import React from 'react';

import { render, act, waitFor, fireEvent, mockUseTranslation } from '../../../utils/test-utils';
import FirstLevelNav from './FirstLevelNav.component';

const mockMenuLabels = {
  bookings: {
    label: 'Bookings',
    icon: '/content/dam/global/icons/common/suitcase.svg',
    iconActive: '/content/dam/global/icons/common/suitcase-solid.svg',
  },
  spending: {
    label: 'Spending',
    icon: '/content/dam/global/icons/common/chart-bars.svg',
    iconActive: '/content/dam/global/icons/common/chart-bars-solid.svg',
  },
  home: {
    label: 'Home',
    icon: '/content/dam/global/icons/common/home.svg',
    iconActive: '/content/dam/global/icons/common/home-solid.svg',
  },
  manage: {
    label: 'Manage',
    icon: '/content/dam/global/icons/common/chart-pie.svg',
    iconActive: '/content/dam/global/icons/common/chart-pie-solid.svg',
    options: {}, // Add a valid options object here as per the expected structure
  },
  contact: {
    label: 'Contact',
    icon: '/content/dam/global/icons/common/contact.svg',
    iconActive: '/content/dam/global/icons/common/contact-solid.svg',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getPathForLocale: (locale: string, path: string) => {
    return `/${locale}/${path}`;
  },
  useFeatureToggle: jest.fn().mockReturnValue({}),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocaleByPathname: jest.fn(() => LOCALES.EN),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: mockUseTranslation,
  };
});

jest.mock('next/navigation', () => {
  const actual = jest.requireActual('next/navigation');
  let mockPath = 'homepage';
  return {
    ...actual,
    usePathname: () => mockPath,
    __setMockPath: (path: string) => {
      mockPath = path;
    },
  };
});
const mockProps = {
  userRole: 'SUPER',
  isAccountHolder: true,
  isCollapsed: false,
  menuLabels: mockMenuLabels,
  isBusinessPayManager: true,
};

// eslint-disable-next-line @typescript-eslint/no-require-imports
const getLocaleByPathname = require('@whitbread-eos/utils/server').getLocaleByPathname;

describe('FirstLevelNav Component', () => {
  let nextNavigationMock: any;
  beforeEach(() => {
    jest.clearAllMocks();
    jest.restoreAllMocks();
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    nextNavigationMock = require('next/navigation');
    nextNavigationMock.__setMockPath('homepage');
    // Reset default mocks
    getLocaleByPathname.mockReturnValue(LOCALES.EN);
  });

  it('should render FirstLevelNav component', () => {
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="homepage" />
    );
    expect(getByTestId('FirstLevelNav-container')).toBeInTheDocument();
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('Spending-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('Manage-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('Bookings-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('ContactUs-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render FirstLevelNav component not being travel manager', () => {
    const { getByTestId } = render(
      <FirstLevelNav
        {...mockProps}
        isTravelManager={false}
        isAccountHolder={false}
        activeKey="homepage"
      />
    );
    expect(getByTestId('FirstLevelNav-container')).toBeInTheDocument();
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('Spending-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('Bookings-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('ContactUs-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render FirstLevelNav component with click on Home link', async () => {
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="homepage" />
    );
    const homeLink = getByTestId('Home-Sidebar-Link');
    const manageLink = getByTestId('Manage-Sidebar-Link');
    const spendingLink = getByTestId('Spending-Sidebar-Link');
    const bookingsLink = getByTestId('Bookings-Sidebar-Link');
    const contactUsLink = getByTestId('ContactUs-Sidebar-Link');
    expect(homeLink).toBeInTheDocument();
    expect(manageLink).toBeInTheDocument();
    expect(contactUsLink).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(homeLink);
      fireEvent.click(manageLink);
      fireEvent.click(spendingLink);
      fireEvent.click(bookingsLink);
      fireEvent.click(contactUsLink);
    });

    await waitFor(() => {
      expect(getByTestId('FirstLevelNav-container')).toBeInTheDocument();
    });
  });

  it('should render FirstLevelNav component with menuLabels undefined', () => {
    mockMenuLabels.home = { label: '', icon: '', iconActive: '' };
    mockMenuLabels.bookings = { label: '', icon: '', iconActive: '' };
    mockMenuLabels.spending = { label: '', icon: '', iconActive: '' };
    mockMenuLabels.manage = { label: '', icon: '', iconActive: '', options: {} };
    mockMenuLabels.contact = { label: '', icon: '', iconActive: '' };
    const { getByTestId } = render(
      <FirstLevelNav {...mockProps} isTravelManager={true} activeKey="homepage" />
    );
    expect(getByTestId('FirstLevelNav-container')).toBeInTheDocument();
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
  });

  it('manage should redirect to cards management when isNOT Travel Manager isCardHolder is true and isAccountHolder is false', () => {
    const { queryByTestId } = render(
      <FirstLevelNav
        {...mockProps}
        menuLabels={{ ...mockMenuLabels }}
        isCardHolder={true}
        isAccountHolder={false}
        isTravelManager={false}
        activeKey="homepage"
      />
    );

    const link = queryByTestId('Manage-Sidebar-Link');

    expect(link).toBeInTheDocument();
    expect((link as HTMLAnchorElement)?.href.includes('manage/cards')).toBe(true);
  });

  it('should not render Bookings link for BP companytype', () => {
    const { queryByTestId } = render(
      <FirstLevelNav
        {...mockProps}
        companyType={CompanyType.BUSINESS_PAY}
        isTravelManager={true}
        activeKey="homepage"
      />
    );

    expect(queryByTestId('Bookings-Sidebar-Link')).not.toBeInTheDocument();
  });

  it('should set aria-current="page" on the Bookings link when pathname includes account/dashboard', async () => {
    nextNavigationMock.__setMockPath('/gb/en/business-booker/account/dashboard.html');
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="bookings" />
    );
    await waitFor(() => {
      const bookingsLink = getByTestId('Bookings-Sidebar-Link');
      expect(bookingsLink).toHaveAttribute('aria-current', 'page');
    });
  });

  it('should set FOCUS_NAV_KEY in sessionStorage on Enter keydown for Home', () => {
    sessionStorage.clear();
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="homepage" />
    );
    const homeLink = getByTestId('Home-Sidebar-Link');
    fireEvent.keyDown(homeLink, { key: 'Enter' });
    expect(sessionStorage.getItem('sidebarNavFocusKey')).toBe('homepage');
  });

  it('should set FOCUS_NAV_KEY in sessionStorage on Space keydown for Manage', () => {
    sessionStorage.clear();
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="manage" />
    );
    const manageLink = getByTestId('Manage-Sidebar-Link');
    fireEvent.keyDown(manageLink, { key: ' ' });
    expect(sessionStorage.getItem('sidebarNavFocusKey')).toBe('manage');
  });

  it('should restore focus to Home if FOCUS_NAV_KEY is set and not a reload', () => {
    sessionStorage.setItem('sidebarNavFocusKey', 'homepage');
    // Mock performance navigation to simulate not a reload
    Object.defineProperty(window, 'performance', {
      value: {
        getEntriesByType: () => [{ type: 'navigate' }],
        navigation: { type: 0 },
      },
      configurable: true,
    });
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="homepage" />
    );
    const homeLink = getByTestId('Home-Sidebar-Link');
    expect(document.activeElement === homeLink || homeLink === document.activeElement).toBe(true);
    expect(sessionStorage.getItem('sidebarNavFocusKey')).toBeNull();
  });

  it('should blur focused nav item and clear FOCUS_NAV_KEY on popstate - e.g. back button', () => {
    sessionStorage.setItem('sidebarNavFocusKey', 'homepage');
    const { getByTestId } = render(
      <FirstLevelNav isTravelManager={true} {...mockProps} activeKey="homepage" />
    );
    const homeLink = getByTestId('Home-Sidebar-Link');
    const blurSpy = jest.spyOn(homeLink, 'blur');
    homeLink.focus();
    window.dispatchEvent(new PopStateEvent('popstate'));
    expect(blurSpy).toHaveBeenCalled();
    expect(sessionStorage.getItem('sidebarNavFocusKey')).toBeNull();
    blurSpy.mockRestore();
  });

  it('should not render FirstLevelNav spending tab for BP companytype in DE if PIBA Euro is not active and not tethered', () => {
    getLocaleByPathname.mockReturnValue('de');
    const { getByTestId, queryByTestId } = render(
      <FirstLevelNav
        {...mockProps}
        isTravelManager={false}
        isAccountHolder={false}
        isTethered={false}
        companyType={CompanyType.BUSINESS_PAY}
        activeKey="spending"
        isPibaEuroEnabled={false}
      />
    );
    expect(getByTestId('FirstLevelNav-container')).toBeInTheDocument();
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
    expect(queryByTestId('Spending-Sidebar-Link')).not.toBeInTheDocument();
    expect(getByTestId('ContactUs-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render FirstLevelNav spending tab for BP companytype in DE if PIBA Euro is active and not tethered', () => {
    getLocaleByPathname.mockReturnValue('de');
    const { getByTestId } = render(
      <FirstLevelNav
        {...mockProps}
        isTravelManager={false}
        isAccountHolder={false}
        isTethered={false}
        companyType={CompanyType.BUSINESS_PAY}
        activeKey="spending"
        isPibaEuroEnabled={true}
      />
    );
    expect(getByTestId('FirstLevelNav-container')).toBeInTheDocument();
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('Spending-Sidebar-Link')).toBeInTheDocument();
    expect(getByTestId('ContactUs-Sidebar-Link')).toBeInTheDocument();
  });
});
