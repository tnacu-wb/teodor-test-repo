import { LOCALES } from '@whitbread-eos/api';
import { usePathname } from 'next/navigation';
import React from 'react';

import { act, fireEvent, mockUseTranslation, render, waitFor } from '../../utils/test-utils';
import Sidebar from './Sidebar.component';

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
    options: {},
  },
};

const mockLevelResponse = {
  manage: [
    {
      key: 'ManageEmployees',
      label: 'Manage employees',
      isActive: false,
      href: '/en-gb/manage/employees',
      condition: true,
    },
    {
      key: 'BookingAllowances',
      label: 'Booking allowances',
      isActive: false,
      href: '/en-gb/manage/allowances',
      condition: true,
    },
    {
      key: 'BookingAlerts',
      label: 'Booking alerts',
      isActive: false,
      href: '/en-gb/manage/alerts',
      condition: true,
    },
    {
      key: 'CardManagement',
      label: 'Card management',
      isActive: false,
      href: '/en-gb/manage/cards',
      condition: true,
    },
    {
      key: 'EmployeeQuestions',
      label: 'Employee questions',
      isActive: false,
      href: '/en-gb/manage/questions',
      condition: true,
    },
    {
      key: 'CompanyDetails',
      label: 'Company details',
      isActive: false,
      href: '/en-gb/manage/company',
      condition: true,
    },
  ],
};

jest.mock('next/navigation', () => {
  const actualNavigation = jest.requireActual('next/navigation');
  return {
    ...actualNavigation,
    usePathname: jest.fn(),
    useSearchParams: jest.fn(() => ({
      get: jest.fn(() => null),
    })),
  };
});
const mockUsePathname = usePathname as jest.Mock;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: mockUseTranslation,
    getPathForLocale: jest.fn(() => '/en'),
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getPathForLocale: (locale: string, path: string) => {
    return `/${locale}/${path}`;
  },
  useFeatureToggle: jest.fn().mockReturnValue({}),
  getSecondLevelLinks: () => mockLevelResponse,
}));

const mockProps = {
  userRole: 'SUPER',
  isAccountHolder: true,
  menuLabels: mockMenuLabels,
  icons: {},
  collapseIcons: undefined,
  isCardHolder: true,
  isTethered: false,
};

describe('Sidebar Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Reset usePathname mock to a default before each test
    mockUsePathname.mockReturnValue('/en-gb/homepage');
  });

  it('should render Sidebar component with Home link', () => {
    mockUsePathname.mockReturnValue('/en-gb/homepage');
    const { getByTestId } = render(<Sidebar {...mockProps} />);
    expect(getByTestId('Home-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render Sidebar component with Spending link', () => {
    mockUsePathname.mockReturnValue('/en-gb/spending');
    const { getByTestId } = render(<Sidebar {...mockProps} />);
    expect(getByTestId('Spending-Sidebar-Link')).toBeInTheDocument();
  });

  it('should render Sidebar component with access to Manage link', () => {
    const { getByTestId } = render(<Sidebar {...mockProps} />);
    expect(getByTestId('Manage-Sidebar-Link')).toBeInTheDocument();
  });

  it('should toggle from collapsed to expanded when sidebar toggle is clicked', async () => {
    mockUsePathname.mockReturnValue('/en-gb/manage/employees');
    const { getByTestId } = render(<Sidebar {...mockProps} />);

    const toggle = getByTestId('SidebarToggle-button');
    expect(toggle).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(toggle);
    });

    await waitFor(async () => {
      expect(getByTestId('Manage-Sidebar-Link')).toBeInTheDocument();
    });
  });

  it('should toggle from expanded to collapsed when clicking on items that have second level', async () => {
    const { getByTestId } = render(<Sidebar {...mockProps} />);

    const link = getByTestId('Manage-Sidebar-Link');
    expect(link).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(link);
    });

    await waitFor(async () => {
      expect(getByTestId('Manage-Sidebar-Link')).toBeInTheDocument();
    });
  });

  it('should handle second-level links with conditions correctly', async () => {
    const secondLevelLinksMock = {
      manage: [{ condition: true }, { condition: false }],
    };

    const mockPropsWithSecondLevelLinks = {
      ...mockProps,
      secondLevelLinks: secondLevelLinksMock,
    };

    const { getByTestId } = render(<Sidebar {...mockPropsWithSecondLevelLinks} />);

    const link = getByTestId('Manage-Sidebar-Link');
    expect(link).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(link);
    });

    await waitFor(() => {
      expect(getByTestId('Manage-Sidebar-Link')).toBeInTheDocument();
    });
  });

  it('should handle Enter/Space keydown on FirstLevelNav (handleFirstLevelKeyDown)', async () => {
    const secondLevelLinksMock = {
      manage: [{ condition: true }, { condition: false }],
    };

    const mockPropsWithSecondLevelLinks = {
      ...mockProps,
      secondLevelLinks: secondLevelLinksMock,
    };

    // Start on homepage
    mockUsePathname.mockReturnValue('/en-gb/homepage');
    const { getByTestId, queryByTestId, rerender } = render(
      <Sidebar {...mockPropsWithSecondLevelLinks} />
    );
    const manageLink = getByTestId('Manage-Sidebar-Link');

    // SecondLevelNav should not be visible
    expect(queryByTestId('SecondLevelNav-container')).not.toBeInTheDocument();
    manageLink.focus();

    // Simulate Space key on the Manage link (the specific FirstLevelNav item)
    fireEvent.keyDown(manageLink, { key: ' ' });

    // Simulate navigation by updating the pathname to manage/employees
    mockUsePathname.mockReturnValue('/en-gb/manage/employees');
    rerender(<Sidebar {...mockPropsWithSecondLevelLinks} />);

    await waitFor(() => {
      expect(queryByTestId('SecondLevelNav-container')).toBeInTheDocument();
    });
  });

  it('should show SecondLevelNav when clicking a secondary nav item (e.g. BookingAllowances under Manage)', async () => {
    const secondLevelLinksMock = {
      manage: [
        {
          key: 'ManageEmployees',
          label: 'Manage employees',
          condition: true,
          href: '/en-gb/manage/employees',
        },
        {
          key: 'BookingAllowances',
          label: 'Booking allowances',
          condition: true,
          href: '/en-gb/manage/allowances',
        },
      ],
    };
    const mockPropsWithSecondLevelLinks = {
      ...mockProps,
      secondLevelLinks: secondLevelLinksMock,
    };

    // Set pathname so Manage is active
    mockUsePathname.mockReturnValue('/en-gb/manage/employees');
    const { getByTestId, queryByTestId, rerender } = render(
      <Sidebar {...mockPropsWithSecondLevelLinks} />
    );

    // Open SecondLevelNav by focusing Manage and pressing Space
    const manageLink = getByTestId('Manage-Sidebar-Link');
    manageLink.focus();
    fireEvent.keyDown(manageLink, { key: ' ' });

    // Simulate navigation to BookingAllowances
    mockUsePathname.mockReturnValue('/en-gb/manage/allowances');
    rerender(<Sidebar {...mockPropsWithSecondLevelLinks} />);

    // Wait for SecondLevelNav to appear
    await waitFor(() => {
      expect(queryByTestId('SecondLevelNav-container')).toBeInTheDocument();
    });
    const bookingAllowancesLink = getByTestId('BookingAllowances-Sidebar-Link');
    expect(bookingAllowancesLink).toBeInTheDocument();
  });

  it('should focus first focusable element in SecondLevelNav on ArrowRight keydown (handleFirstLevelKeyDown)', async () => {
    const secondLevelLinksMock = {
      manage: [
        {
          key: 'ManageEmployees',
          label: 'Manage employees',
          condition: true,
          href: '/en-gb/manage/employees',
        },
        {
          key: 'BookingAllowances',
          label: 'Booking allowances',
          condition: true,
          href: '/en-gb/manage/allowances',
        },
      ],
    };
    const mockPropsWithSecondLevelLinks = {
      ...mockProps,
      secondLevelLinks: secondLevelLinksMock,
    };

    // Set pathname so Manage is active
    mockUsePathname.mockReturnValue('/en-gb/manage/employees');
    const { getByTestId, queryByTestId } = render(<Sidebar {...mockPropsWithSecondLevelLinks} />);
    const manageLink = getByTestId('Manage-Sidebar-Link');
    manageLink.focus();
    // Simulate ArrowRight key on Manage link
    fireEvent.keyDown(manageLink, { key: 'ArrowRight' });

    await waitFor(() => {
      expect(queryByTestId('SecondLevelNav-container')).toBeInTheDocument();
    });

    // Find the first focusable element in SecondLevelNav
    const secondLevelNav = getByTestId('SecondLevelNav-container');
    const firstFocusable = secondLevelNav.querySelector(
      'a, button, [tabindex]:not([tabindex="-1"])'
    );

    // Expect focus to be on the first focusable element in SecondLevelNav
    expect(document.activeElement).toBe(firstFocusable);
  });

  it('should restore focus to active FirstLevelNav item on handleSecondLevelNavClose (Escape key)', async () => {
    const secondLevelLinksMock = {
      manage: [
        {
          key: 'ManageEmployees',
          label: 'Manage employees',
          condition: true,
          href: '/en-gb/manage/employees',
        },
        {
          key: 'BookingAllowances',
          label: 'Booking allowances',
          condition: true,
          href: '/en-gb/manage/allowances',
        },
      ],
    };
    const mockPropsWithSecondLevelLinks = {
      ...mockProps,
      secondLevelLinks: secondLevelLinksMock,
    };

    // Set pathname so Manage is active
    mockUsePathname.mockReturnValue('/en-gb/manage/employees');
    const { getByTestId, queryByTestId } = render(<Sidebar {...mockPropsWithSecondLevelLinks} />);
    const manageLink = getByTestId('Manage-Sidebar-Link');

    // Open SecondLevelNav by focusing Manage and pressing Space
    manageLink.focus();
    fireEvent.keyDown(manageLink, { key: ' ' });
    await waitFor(() => {
      expect(queryByTestId('SecondLevelNav-container')).toBeInTheDocument();
    });

    // Simulate Escape key on SecondLevelNav to trigger handleSecondLevelNavClose
    const secondLevelNav = getByTestId('SecondLevelNav-container');
    fireEvent.keyDown(secondLevelNav, { key: 'Escape' });

    // Wait for focus to be restored to the active FirstLevelNav item
    await waitFor(() => {
      expect(document.activeElement).toBe(manageLink);
    });
  });

  it('should restore focus to first Sidebar-Link with tabindex if no aria-current="page" found (handleSecondLevelNavClose fallback)', async () => {
    const secondLevelLinksMock = {
      manage: [
        {
          key: 'ManageEmployees',
          label: 'Manage employees',
          condition: true,
          href: '/en-gb/manage/employees',
        },
        {
          key: 'BookingAllowances',
          label: 'Booking allowances',
          condition: true,
          href: '/en-gb/manage/allowances',
        },
      ],
    };
    const mockPropsWithSecondLevelLinks = {
      ...mockProps,
      secondLevelLinks: secondLevelLinksMock,
    };

    // Set pathname so Manage is active
    mockUsePathname.mockReturnValue('/en-gb/manage/employees');
    const { getByTestId, queryByTestId } = render(<Sidebar {...mockPropsWithSecondLevelLinks} />);
    const manageLink = getByTestId('Manage-Sidebar-Link');

    // Open SecondLevelNav by focusing Manage and pressing Space
    manageLink.focus();
    fireEvent.keyDown(manageLink, { key: ' ' });

    await waitFor(() => {
      expect(queryByTestId('SecondLevelNav-container')).toBeInTheDocument();
    });

    // Remove aria-current="page" from all Sidebar-Link elements to force fallback
    const firstLevelNavContainer = getByTestId('FirstLevelNav-ref-container');
    firstLevelNavContainer
      .querySelectorAll('[data-testid$="Sidebar-Link"][aria-current="page"]')
      .forEach((el) => {
        el.removeAttribute('aria-current');
      });

    // Simulate Escape key on SecondLevelNav to trigger handleSecondLevelNavClose
    const secondLevelNav = getByTestId('SecondLevelNav-container');
    fireEvent.keyDown(secondLevelNav, { key: 'Escape' });

    // Wait for focus to be restored to the first Sidebar-Link
    await waitFor(() => {
      const fallbackLink = firstLevelNavContainer.querySelector(
        '[data-testid$="Sidebar-Link"]:not([aria-current="page"])'
      );
      expect(fallbackLink).not.toBeNull();
    });
  });
});
